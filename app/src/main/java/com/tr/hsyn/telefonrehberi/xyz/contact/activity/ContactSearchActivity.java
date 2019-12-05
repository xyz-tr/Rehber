package com.tr.hsyn.telefonrehberi.xyz.contact.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Logger;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventDeleteContactSearch;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.listener.SearchTextChangeListener;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.swipe.ContactSwipeCallBack;
import com.tr.hsyn.telefonrehberi.util.swipe.SwipeListener;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.searchview.MaterialSearchView;
import com.tr.hsyn.telefonrehberi.util.ui.searchview.OnSearchViewListener;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.ISimpleContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail.ContactDetailsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactSearchAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_AccountBox;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.ContactSelectListener;
import com.tr.hsyn.telefonrehberi.xyz.main.MainConsts;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import lombok.val;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


//todo En çok arananlar gösterilsin

public class ContactSearchActivity extends AppCompatActivity implements OnSearchViewListener, ContactSelectListener, SwipeListener{
   
   private FastScrollRecyclerView   recyclerView;
   private MaterialSearchView       searchView;
   private List<ISimpleContact>     contacts;
   private String                   searchText;
   private String                   numberToCall;
   private SearchTextChangeListener searchTextChangeListener;
   private View                     emptyView;
   private int scrollPosition;
   private static final Logger log     = Logger.jLog();
   private final static int    RC_CALL = 8;
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sarch);
      
      emptyView    = findViewById(R.id.emptyView);
      recyclerView = findViewById(R.id.recyclerViewSearch);
      searchView   = findViewById(R.id.sv);
      searchView.setOnSearchViewListener(this);
      
      int color = u.getPrimaryColor(this);
      
      recyclerView.setThumbColor(color);
      recyclerView.setPopupBgColor(color);
      
      View myView = findViewById(R.id.containerView);
      myView.post(() -> searchView.showSearch(true));
      
      startWork();
      
      ContactSwipeCallBack contactSwipeCallBack = new ContactSwipeCallBack(0, ItemTouchHelper.RIGHT, this, this);
      contactSwipeCallBack.setBgColor(u.getPrimaryColor(this));
      
      ItemTouchHelper itemTouchHelper = new ItemTouchHelper(contactSwipeCallBack);
      
      itemTouchHelper.attachToRecyclerView(recyclerView);
      
      ColorController.setIndaterminateProgressColor(this, findViewById(R.id.progress));
      ColorController.changeStatusBarColor(this);
   }
   
   private void startWork(){
      
      Worker.onBackground(
            this::getContacts,
            this::setContacts
      );
   }
   
   private List<ISimpleContact> getContacts(){
      
      List<ISimpleContact> contacts = Contacts.getContactsForSearch(getApplicationContext(), Kickback_AccountBox.getInstance().getSelectedAccount());
      
      if(contacts == null){
         
         log.w("Arama için kişiler alınamadı");
         return null;
      }
      
      return contacts /*Stream.of(contacts).distinct().toList()*/;
   }
   
   private void setContacts(List<ISimpleContact> contacts){
      
      this.contacts = contacts;
      
      setAdaper(this.contacts);
      
      findViewById(R.id.progress).setVisibility(View.GONE);
      
      if(searchText != null){
         
         filter(searchText);
      }
      
      setScrollPosition();
   }
   
   private void setScrollPosition(){
      
      if(scrollPosition != 0){
         
         recyclerView.scrollToPosition(scrollPosition);
      }
   }
   
   private void filter(String text){
      
      if(searchTextChangeListener != null) searchTextChangeListener.onSearchTextChange(text);
   }
   
   private void setAdaper(List<ISimpleContact> contacts){
      
      if(contacts != null && contacts.size() != 0){
         
         ContactSearchAdapter adapter = new ContactSearchAdapter(contacts).setContactSelectListener(this);
         searchTextChangeListener = adapter;
         recyclerView.setAdapter(adapter);
         
         int color = u.getPrimaryColor(this);
         adapter.setMarkColor(u.lighter(color, 0.3F));
      }
      
      checkEmpty();
      //getAdapter().notifyDataSetChanged();
   }
   
   private void checkEmpty(){
      
      if(contacts == null || contacts.size() == 0){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      else{
         
         emptyView.setVisibility(View.GONE);
      }
   }
   
   @Override
   protected void onStart(){
      
      super.onStart();
      
      
      u.keepGoing(() -> EventBus.getDefault().register(this));
   }
   
   @Override
   protected void onDestroy(){
      
      super.onDestroy();
      
      EventBus.getDefault().unregister(this);
   }
   
   @Override
   public void onSearchViewShown(){
      
   }
   
   @Override
   public void onSearchViewClosed(){
      
      onBackPressed();
   }
   
   @Override
   public boolean onQueryTextSubmit(String query){
      
      return true;
   }
   
   @Override
   public void onQueryTextChange(String newText){
      
      if(newText == null){ return; }
      
      searchText = newText;
      
      Timber.d(searchText);
      
      filter(newText);
      
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      Bungee.fade(this);
      finishAndRemoveTask();
      
   }
   
   @Override
   public void onContactSelect(String contactId){
      
      Worker.onMain(() -> ContactDetailsActivity.startActivity(this, contactId), MainConsts.DELAY_ACTIVITY_START);
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      EasyPermissions.onRequestPermissionsResult( requestCode,permissions,  grantResults, this);
   }
   
   @Override
   public void onSwipe(int index){
      
      getAdapter().notifyItemChanged(index);
      
      ISimpleContact contact = getAdapterContacts().get(index);
   
      if(Phone.isPermissionsGrant(this, Manifest.permission.CALL_PHONE)){
   
         call(contact.getNumber());
      }
      else{
         
         numberToCall = contact.getNumber();
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RC_CALL);
         }
      }
   }
   
   private List<ISimpleContact> getAdapterContacts(){
      
      return getAdapter().getFilteredList();
   }
   
   @AfterPermissionGranted(RC_CALL)
   private void call(){
      
      if(numberToCall != null) Phone.makeCall(this, numberToCall);
      numberToCall = null;
   }
   
   private void call(String number){
   
      Phone.makeCall(this, number);
   }
   
   @Subscribe
   public void onContactDelete(EventDeleteContactSearch event){
      
      int index, index2;
      
      //bu kesin alınacak
      index2 = getContactIndex(event.getContact(), contacts);
      
      if(index2 != -1){
         
         contacts.remove(index2);
      }
      
      val filteredList = getAdapter().getFilteredList();
      
      if(filteredList != null){
         
         //bu kesin değil
         index = getContactIndex(event.getContact(), filteredList);
         filteredList.remove(index);
         getAdapter().notifyItemRemoved(index);
      }
      else{
         getAdapter().notifyItemRemoved(index2);
      }
      
      Toast.makeText(this, getString(R.string.contact_deleted, event.getContact().getName()), Toast.LENGTH_SHORT).show();
   }
   
   private ContactSearchAdapter getAdapter(){
      
      return ((ContactSearchAdapter) recyclerView.getAdapter());
   }
   
   private int getContactIndex(ISimpleContact contact, List<ISimpleContact> contacts){
      
      for(int i = 0; i < contacts.size(); i++){
         if(contacts.get(i).equals(contact)){ return i; }
      }
      
      return -1;
   }
   
   @Subscribe
   public void onContactUpdated(EventRefreshContacts event){
      
      //noinspection ConstantConditions
      scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
      
      if(event.getCode() != null && event.getCode().equals("contact_update")){
         
         startWork();
      }
   }
}
