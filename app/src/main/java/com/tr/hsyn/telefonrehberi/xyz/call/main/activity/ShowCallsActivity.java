package com.tr.hsyn.telefonrehberi.xyz.call.main.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.mikepenz.itemanimators.SlideRightAlphaAnimator;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.DeleteCall;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.activity.CallDetails;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.ShowCallsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.Kickback_CallsConst;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnDeleteCallListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import java9.util.function.Supplier;
import lombok.val;
import timber.log.Timber;


public class ShowCallsActivity extends AppCompatActivity implements OnDeleteCallListener, ItemSelectListener{
   
   private             List<ICall>            phoneCalls;
   //private             ShowCallsAdapter       adapter;
   private             String                 title;
   private             String                 subTitle        = "";
   private             FastScrollRecyclerView recyclerView;
   private             ProgressBar            progressBar;
   private             View                   noCallsView;
   public static final String                 EXTRA_TYPE      = "call_type";
   public static final String                 EXTRA_NUMBERS   = "numbers";
   public static final String                 EXTRA_TITLE     = "title";
   public static final String                 EXTRA_SUB_TITLE = "sub_title";
   
   @Override
   protected final void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_show_calls);
      progressBar = findViewById(R.id.progress);
      noCallsView = findViewById(R.id.noCallsView);
      ColorController.setIndaterminateProgressColor(this, progressBar);
      
      
      setupToolbar();
      setViews();
      
      if(!checkArguments()){
         
         u.toast(this, "Gerekli bilgiler verilmedi");
         onBackPressed();
         return;
      }
      
      EventBus.getDefault().register(this);
      
   }
   
   private void setupToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      int         color  = u.getPrimaryColor(this);
      final float factor = .9f;
      
      toolbar.setBackgroundColor(color);
      getWindow().setStatusBarColor(u.darken(color, factor));
      
   }
   
   private void setViews(){
      
      recyclerView = findViewById(R.id.recyclerView);
      ColorController.setRecyclerColor(this, recyclerView);
      
      
      if(phoneCalls != null){
         
         recyclerView.setAdapter(new ShowCallsAdapter(phoneCalls, this, this));
      }
   }
   
   private boolean checkArguments(){
      
      Intent intent = getIntent();
      
      if(intent == null || !intent.hasExtra(EXTRA_NUMBERS)) return false;
      
      List<String> numbers = intent.getStringArrayListExtra(EXTRA_NUMBERS);
      
      findViewById(R.id.progress).setVisibility(View.VISIBLE);
      
      Supplier<List<ICall>> worker = () -> getCalls(numbers);
      
      if(intent.hasExtra(EXTRA_TYPE)){
         
         int type = intent.getIntExtra(EXTRA_TYPE, -1);
         
         worker = () -> getCalls(numbers, type);
         
         subTitle = intent.getStringExtra(EXTRA_SUB_TITLE);
      }
      
      Worker.onBackground(
            worker,
            "ShowCallsActivity : Belirli telefon numaralarına ait arama kayıtlarını alma")
            .whenCompleteAsync(this::setPhoneCalls, Worker.getMainThreadExecutor());
      
      title = intent.hasExtra(EXTRA_TITLE) ? intent.getStringExtra(EXTRA_TITLE) : getString(R.string.call_log);
      setTitle(title);
      return true;
   }
   
   private static List<ICall> getCalls(Iterable<String> numbers){
      
      return Kickback_CallsConst.getInstance().getCallStory().getCalls(numbers);
   }
   
   private static List<ICall> getCalls(Collection<String> numbers, int type){
      
      return Kickback_CallsConst.getInstance().getCallStory().getCalls(numbers, type);
   }
   
   private void setPhoneCalls(List<ICall> phoneCalls, Throwable throwable){
      
      progressBar.setVisibility(View.GONE);
      
      checkEmpty();
      
      if(phoneCalls == null){
         
         Toast.makeText(this, getString(R.string.gerekli_bilgiler_sağlanmadı), Toast.LENGTH_SHORT).show();
         return;
      }
      
      this.phoneCalls = phoneCalls;
      val        animator = new SlideRightAlphaAnimator();
      final long duration = 500L;
      
      animator.setRemoveDuration(duration);
      animator.withInterpolator(new AccelerateInterpolator());
      recyclerView.setItemAnimator(animator);
      recyclerView.setAdapter(new ShowCallsAdapter(phoneCalls, this, this));
      
      
      checkEmpty();
      
      setTitle(title);
      setSubTitle(phoneCalls.size(), subTitle);
   }
   
   private void setTitle(String title){
      
      Objects.requireNonNull(getSupportActionBar()).setTitle(title);
   }
   
   private void checkEmpty(){
      
      if(phoneCalls == null || phoneCalls.isEmpty()){
         
         noCallsView.setVisibility(View.VISIBLE);
      }
      else{
         
         noCallsView.setVisibility(View.GONE);
      }
   }
   
   private void setSubTitle(int size, String text){
      
      Objects.requireNonNull(getSupportActionBar()).setSubtitle(getString(R.string.call_details_calls, size, text));
   }
   
   @Override
   public final void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideLeft(this);
      
   }
   
   @Override
   protected void onDestroy(){
      
      super.onDestroy();
      EventBus.getDefault().unregister(this);
   }
   
   @Override
   public final boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.show_calls_activity, menu);
      return true;
   }
   
   @Override
   public final boolean onOptionsItemSelected(MenuItem item){
      
      if(item.getItemId() == R.id.menu_delete_all){
         
         deleteAllCalls();
         return true;
      }
      
      
      return false;
   }
   
   @Override
   public final boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   private void deleteAllCalls(){
      
      final int size = phoneCalls.size();
      
      for(int i = size - 1; i >= 0; i--){ onDeleteCall(i); }
   }
   
   @SuppressLint("UseValueOf")
   @Override
   public final void onDeleteCall(int position){
      
      ICall call = phoneCalls.remove(position);
      
      //adapter içinden kaldırılıyor, burayı açma
      //adapter.notifyItemRemoved(position);
      
      setSubTitle(phoneCalls.size(), subTitle);
      checkEmpty();
      call.setDeleted();
      EventBus.getDefault().post(new DeleteCall(call).setCode("showcalls"));
   }
   
   @Override
   public final void onItemSelected(int position){
      
      Kickback_CallDetailBox.getInstance().setCalls(new ArrayList<>(phoneCalls));
      
      final Intent intent = CallDetails.createIntent(this, phoneCalls.get(position));
      
      final long delay = 250;
      
      Worker.onMain(() -> {
         
         startActivity(intent);
         Bungee.slideLeft(this);
      }, delay);
   }
   
   @Subscribe
   public void onCallDeleted(DeleteCall event){
      
      if(event.getCode() != null && event.getCode().equals("showcalls")) return;
      
      int index = phoneCalls.indexOf(event.getPhoneCall());
      
      if(index == -1){
         
         Timber.w("Kayıt listede bulunamadı");
         return;
      }
      
      onDeleteCall(index);
   }
   
}
