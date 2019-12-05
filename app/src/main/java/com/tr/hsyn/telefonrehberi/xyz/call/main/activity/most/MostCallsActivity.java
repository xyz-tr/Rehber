package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Group;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.MostCallsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.Kickback_CallsConst;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class MostCallsActivity extends AppCompatActivity implements ItemSelectListener{
   
   public static final String                 EXTRA_CALL_TYPE = "type";
   private             List<Model>            callModels;
   private             FastScrollRecyclerView recyclerView;
   private             ProgressBar            progressBar;
   private             int                    type;
   private volatile    String                 title;
   private             CallStory              callStory;
   private             boolean                needRefresh;
   private             View                   emptyView;
   
   @Override
   protected final void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.most_calls_activity);
      
      callStory = Kickback_CallsConst.getInstance().getCallStory();
      emptyView = findViewById(R.id.emptyView);
      
      getCallType();
      setToolbar();
      setRecyclerView();
      
      progressBar = findViewById(R.id.progressBar);
      
      startWork();
      
      EventBus.getDefault().register(this);
   }
   
   private void getCallType(){
      
      try{
         
         type = getIntent().getIntExtra(EXTRA_CALL_TYPE, -1);
         
         if(type == -1){
   
            Timber.w("Call  Type alınamadı");
            onBackPressed();
         }
      }
      catch(Exception e){
   
         Timber.e("Arama türü bilgisi alınamadı");
      }
      
   }
   
   @Override
   public final void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideUp(this);
   }
   
   private void setToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      int color = u.getPrimaryColor(this);
      
      toolbar.setBackgroundColor(color);
      getWindow().setStatusBarColor(u.darken(color, .9F));
   }
   
   private void setRecyclerView(){
      
      recyclerView = findViewById(R.id.recyclerView);
      VirtualLayoutManager layoutManager = new VirtualLayoutManager(this);
      recyclerView.allowThumbInactiveColor(false);
      recyclerView.setLayoutManager(layoutManager);
      
      int v = u.getPrimaryColor(this);
      
      recyclerView.setPopupBgColor(v);
      recyclerView.setThumbColor(v);
   }
   
   private void startWork(){
      
      progressBar.setVisibility(View.VISIBLE);
      Worker.onBackground(this::generateCallModels, "MostCallsActivity:Arama kayıtları için model oluşturma")
            .whenCompleteAsync(this::setAdapter, Worker.getMainThreadExecutor());
   }
   
   private List<Model> generateCallModels(){
      
      boolean             isDuration = isDurationType(type);
      List<Group<String>> mostCalls  = callStory.getMostCalls(type);
   
      Timber.d("Toplam %d arama kaydı üzerinden araştırma yapılıyor", callStory.getCalls().size());
      
      title = getCallsTitle(type);
      settTitle(getActivityTitle(type));
      
      List<Model> modelList = new ArrayList<>();
      
      for(int i = 0; i < mostCalls.size(); i++){
         
         Group<String> pair = mostCalls.get(i);
         
         String name = pair.getCalls().get(0).getName();
         
         if(name == null){ name = pair.getGroupItem(); }
         
         Model model = new Model(name, isDuration ? pair.getTotalDuration() : pair.getCalls().size(), pair);
         
         modelList.add(model);
      }
      
      return modelList;
   }
   
   private boolean isDurationType(int type){
      
      switch(type){
         
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
         case Filter.LONGEST_ANSWERED_CALLS:
         case Filter.QUICKEST_ANSWERED_CALLS:
         case Filter.LONGEST_MISSED_CALLS:
         case Filter.QUICKEST_MISSED_CALLS:
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL:
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL:
            
            return true;
         
         default: return false;
      }
   }
   
   private String getCallsTitle(int type){
      
      switch(type){
         
         case Filter.MOST_REJECTED_CALLS:
            
            return getString(R.string.rejected);
         
         case Filter.QUICKEST_MISSED_CALLS:
         case Filter.LONGEST_MISSED_CALLS:
         case Filter.MOST_MISSED_CALLS:
            
            return getString(R.string.missed);
         
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL:
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
         case Filter.MOST_OUTGOING_CALLS:
            
            return getString(R.string.outgoing);
         
         
         case Filter.QUICKEST_ANSWERED_CALLS:
         case Filter.LONGEST_ANSWERED_CALLS:
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
         case Filter.MOST_INCOMMING_CALLS:
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL:
            
            return getString(R.string.incomming);
         
         default: return "Arama";
      }
      
   }
   
   private void settTitle(String text){
      
      runOnUiThread(() -> {
         
         assert getSupportActionBar() != null;
         getSupportActionBar().setTitle(text);
      });
      
   }
   
   /*private boolean isNotDurationType(int type){
      
      switch(type){
         
         case CallStory.Type.MOST_REJECTED_CALLS:
         case CallStory.Type.MOST_MISSED_CALLS:
         case CallStory.Type.MOST_OUTGOING_CALLS:
         case CallStory.Type.MOST_INCOMMING_CALLS:
            
            return true;
         
         default: return false;
      }
   }*/
   
   private String getActivityTitle(int type){
      //@off
      switch(type){
         
         case Filter.MOST_INCOMMING_CALLS                 : return getString(R.string.most_incomming_calls);
         case Filter.MOST_OUTGOING_CALLS                  : return getString(R.string.most_outgoing_calls);
         case Filter.MOST_MISSED_CALLS                    : return getString(R.string.most_missed_calls);
         case Filter.MOST_REJECTED_CALLS                  : return getString(R.string.most_rejected_calls);
         case Filter.QUICKEST_MISSED_CALLS                : return getString(R.string.most_small_missed_calls);
         case Filter.LONGEST_MISSED_CALLS                 : return getString(R.string.most_long_missed_calls);
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL   : return getString(R.string.most_duration_outgoing_calls);
         case Filter.LONGEST_MAKE_ANSWERED_CALLS          : return getString(R.string.most_late_make_answer);
         case Filter.QUICKEST_ANSWERED_CALLS              : return getString(R.string.most_quick_answer);
         case Filter.LONGEST_ANSWERED_CALLS               : return getString(R.string.most_late_answer);
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS         : return getString(R.string.most_quick_make_answer);
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL  : return getString(R.string.most_duration_incomming_calls);
         case Filter.QUICKEST_REJECTED_CALLS              : return getString(R.string.quickest_rejected_call);
         case Filter.LONGEST_REJECTED_CALLS               : return getString(R.string.longest_rejected_call);
         
         default: return "Arama";
      }
      //@on
   }
   
   private void setAdapter(List<Model> models, Throwable throwable){
      
      callModels = models;
      recyclerView.setAdapter(new MostCallsAdapter(models, type).setClickListener(this));
      progressBar.setVisibility(View.GONE);
      
      if(models.size() == 0){
         
         //noinspection ConstantConditions
         getSupportActionBar().setSubtitle("Henüz Yeterli Bilgi Yok");
      }
      else{
         
         //noinspection ConstantConditions
         getSupportActionBar().setSubtitle(recyclerView.getAdapter().getItemCount() + " Kişi");
      }
      
      checkEmpty();
   }
   
   private void checkEmpty(){
      
      if(callModels == null || callModels.size() == 0){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      else{
         
         emptyView.setVisibility(View.GONE);
      }
   }
   
   @Override
   protected void onDestroy(){
      
      super.onDestroy();
      EventBus.getDefault().unregister(this);
   }
   
   @Override
   protected void onResume(){
      
      super.onResume();
      
      checkNeedRefresh();
   }
   
   private void checkNeedRefresh(){
      
      if(needRefresh){
         
         needRefresh = false;
         startWork();
      }
   }
   
   @Override
   public final void onItemSelected(int position){
      
      List<ICall> mcalls = callModels.get(position).getPair().getCalls();
      
      ShowMostCallsActivity.setPhoneCalls(mcalls);
      
      Intent intent = new Intent(this, ShowMostCallsActivity.class);
      
      String tempTitle;
      
      ICall phoneCall = mcalls.get(0);
      
      tempTitle = phoneCall.getName() != null ? phoneCall.getName() : phoneCall.getNumber();
      
      intent.putExtra("title", tempTitle);
      intent.putExtra("type", this.title);
      
      startActivity(intent);
      Bungee.slideLeft(this);
   }
   
   @Override
   public final boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Subscribe
   public void onNeedRefresh(EventRefreshContacts event){
      
      needRefresh = true;
   }
   
}
