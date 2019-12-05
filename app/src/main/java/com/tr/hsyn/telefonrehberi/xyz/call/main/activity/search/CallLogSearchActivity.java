package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.event.CallNumberRequest;
import com.tr.hsyn.telefonrehberi.util.event.DeleteCall;
import com.tr.hsyn.telefonrehberi.util.event.EventDeleteSearchCall;
import com.tr.hsyn.telefonrehberi.util.listener.SearchTextChangeListener;
import com.tr.hsyn.telefonrehberi.util.swipe.SwipeCallBack;
import com.tr.hsyn.telefonrehberi.util.swipe.SwipeListener;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.searchview.MaterialSearchView;
import com.tr.hsyn.telefonrehberi.util.ui.searchview.OnSearchViewListener;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.activity.CallDetails;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.CallLogSearchAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.CallMaker;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import lombok.val;


public class CallLogSearchActivity extends AppCompatActivity implements CallMaker, ItemSelectListener, OnSearchViewListener, SwipeListener{
   
   /**
    * Arama kayıtlarını tutacak görsel liste
    */
   private FastScrollRecyclerView recyclerView;
   
   /**
    * Arama kutusu
    */
   private MaterialSearchView       searchView;
   private ProgressBar              progressBar;
   private CallLogSearchAdapter     adapter;
   private SearchTextChangeListener searchTextChangeListener;
   private String                   searchText;
  
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_call_log_search);
      progressBar = findViewById(R.id.progressBar);
      progressBar.setVisibility(View.VISIBLE);
      
      recyclerView = findViewById(R.id.recyclerView);
      searchView   = findViewById(R.id.sv);
      searchView.post(() -> searchView.showSearch(true));
      searchView.setOnSearchViewListener(this);
   
      ColorController.setRecyclerColor(this, recyclerView);
      
      SwipeCallBack swipeCallBack = new SwipeCallBack(0, ItemTouchHelper.LEFT, this, this);
      swipeCallBack.setBgColor(u.getPrimaryColor(this));
      
      ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeCallBack);
      
      itemTouchHelper.attachToRecyclerView(recyclerView);
      
      setPhoneCalls();
      
      EventBus.getDefault().register(this);
   }
   
   private void setPhoneCalls(){
      
      val calls = Kickback_CallDetailBox.getInstance().getCalls();
      adapter = new CallLogSearchAdapter(calls, this, this);
      recyclerView.setAdapter(adapter);
      searchTextChangeListener = adapter;
      
      progressBar.setVisibility(View.GONE);
      
      if(searchText != null){
         
         filter(searchText);
      }
      
   }
   
   @Override
   protected void onDestroy(){
   
      EventBus.getDefault().unregister(this);
      super.onDestroy();
   }
   
   private void filter(String nameOrNumber){
      
      if(searchTextChangeListener != null) searchTextChangeListener.onSearchTextChange(nameOrNumber);
   }
   
   @Override
   public void onItemSelected(int position){
      
      List<ICall> phoneCalls = getAdapterCalls();
      
      ICall phoneCall = phoneCalls.get(position);
   
      Kickback_CallDetailBox.getInstance().setCalls(new ArrayList<>(phoneCalls));
      
      startActivity(CallDetails.createIntent(this, phoneCall));
      Bungee.slideLeft(this);
   }
   
   private List<ICall> getAdapterCalls(){
      
      return adapter.getFilteredList();
   }
   
   @Override
   public void onSearchViewShown(){
      
   }
   
   @Override
   public void onSearchViewClosed(){
      
      onBackPressed();
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      Bungee.fade(this);
   }
   
   @Override
   public boolean onQueryTextSubmit(String query){
      
      return false;
   }
   
   @Override
   public void onQueryTextChange(String newText){
      
      if(newText == null){
         return;
      }
      
      searchText = newText;
      
      filter(newText);
   }
   
   @Override
   public void call(String number){
      
      EventBus.getDefault().post(new CallNumberRequest(number));
   }
   
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onCallDelete(DeleteCall event){
      
      ICall phoneCall = event.getPhoneCall();
      
      final int index = adapter.getFilteredList().indexOf(phoneCall);
   
      if(index != -1){
         
         onSwipe(index);
      }
   }
   
   @Override
   public void onSwipe(int index){
   
      ICall call = getAdapterCalls().get(index);
   
      Show.globalMessage(this, Stringx.format("Siliniyor : %s", call.getName() == null ? call.getNumber() : call.getName()), 1000L);
   
      adapter.getFilteredList().remove(index);
      adapter.notifyItemRemoved(index);
   
      EventBus.getDefault().post(new EventDeleteSearchCall(call));
   }
}
