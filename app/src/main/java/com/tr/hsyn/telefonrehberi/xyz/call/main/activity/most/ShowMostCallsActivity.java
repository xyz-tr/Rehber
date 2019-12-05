package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.event.DeleteCall;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.observable.Observe;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.activity.CallDetails;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.ShowMostCallAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.CallLogFiltered;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnDeleteCallListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class ShowMostCallsActivity extends AppCompatActivity implements OnDeleteCallListener{
   
   private        FastScrollRecyclerView recyclerView;
   private        View                   emptyView;
   private static List<ICall>            phoneCalls;
   private final  Observe<Integer>       callSize = new Observe<>();
   
   @Override
   protected final void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_show_most_calls);
      emptyView = findViewById(R.id.emptyView);
      
      if(phoneCalls == null){
         onBackPressed();
         finish();
      }
      
      setToolbar();
      
      recyclerView = findViewById(R.id.recyclerView);
      recyclerView.setAdapter(new ShowMostCallAdapter(phoneCalls, this, this::onItemClick));
      ColorController.setRecyclerColor(this, recyclerView);
      checkEmpty();
      EventBus.getDefault().register(this);
      
   }
   
   private void setToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      final float factor = .9f;
      int         color  = u.getPrimaryColor(this);
      toolbar.setBackgroundColor(color);
      getWindow().setStatusBarColor(u.darken(color, factor));
      
      //noinspection ConstantConditions
      getSupportActionBar().setTitle(getIntent().getStringExtra("title"));
      getSupportActionBar().setSubtitle(getString(R.string.call_log_calls, phoneCalls.size(), getIntent().getStringExtra("type")));
      
      callSize.initValue(0);
      callSize.setObserver(this::onCallSizeChange);
   }
   
   private void onCallSizeChange(int newSize){
      //noinspection ConstantConditions
      getSupportActionBar().setSubtitle(getString(R.string.call_log_calls, newSize, getIntent().getStringExtra("type")));
   }
   
   private void onItemClick(int index){
      
      Kickback_CallDetailBox.getInstance().setCalls(new ArrayList<>(phoneCalls));
      startActivity(CallDetails.createIntent(this, phoneCalls.get(index)));
      Bungee.slideLeft(this);
   }
   
   private void checkEmpty(){
      
      if(phoneCalls == null || phoneCalls.size() == 0){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      else{
         
         emptyView.setVisibility(View.GONE);
      }
   }
   
   @Override
   protected void onDestroy(){
      
      super.onDestroy();
      
      phoneCalls = null;
      EventBus.getDefault().unregister(this);
   }
   
   @Override
   public final void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideRight(this);
   }
   
   @Override
   public final boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.show_most_call_activity_menu, menu);
      
      return true;
   }
   
   @Override
   public final boolean onOptionsItemSelected(MenuItem item){
      
      if(item.getItemId() == R.id.menu_delete_all){
         
         deleteAll();
         return true;
      }
      
      return false;
   }
   
   @Override
   public final boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   private void deleteAll(){
      
      for(int i = 0; i < phoneCalls.size(); i++){
         
         onDeleteCall(i);
      }
   }
   
   /**
    * Listeden bir kaydı silmek için çöp kutusu simgesine basıldığında.
    *
    * @param position Silinen kaydın listedeki index'i
    */
   @Override
   public final void onDeleteCall(int position){
      
      //Silinen kayıt
      ICall call = phoneCalls.get(position);
      
      //Silme işlemi adapter üzerinden yapılıyor
      if(getAdapter().remove(call)){
         
         callSize.setValue(phoneCalls.size());
         
         call.setDeleted();
         EventBus.getDefault().post(new DeleteCall(call).setCode("mostcalls"));
      }
      else{
         
         Timber.w("Silinemedi : %s", call.getName());
      }
      
      checkEmpty();
      EventBus.getDefault().post(new EventRefreshContacts());
      CallLogFiltered.setNeedRefresh(true);
   }
   
   private ShowMostCallAdapter getAdapter(){
      
      return (ShowMostCallAdapter) recyclerView.getAdapter();
   }
   
   /**
    * Listeden bir arama seçilip detaylara gidildiğinde ve oradan kayıt silindiğinde
    *
    * @param event Silinen kaydı taşıyan nesne
    */
   @Subscribe
   public void onCallDeleted(DeleteCall event){
      
      if(event.getCode() != null && event.getCode().equals("mostcalls")) return;
      
      int index = phoneCalls.indexOf(event.getPhoneCall());
      
      if(index == -1){
         
         Timber.w("Arama kaydı listede bulunamadı");
         return;
      }
      
      onDeleteCall(index);
   }
   
   public static void setPhoneCalls(List<ICall> phoneCalls){
      
      ShowMostCallsActivity.phoneCalls = phoneCalls;
   }
}
