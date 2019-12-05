package com.tr.hsyn.telefonrehberi.xyz.main.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.Toolbarx;
import com.tr.hsyn.telefonrehberi.util.concurrent.WorkStat;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventWorkFinish;
import com.tr.hsyn.telefonrehberi.util.event.EventWorkStart;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogAction;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import java9.util.concurrent.CompletableFuture;
import timber.log.Timber;


//todo Yazıyorum

/*  Yolum az mı çok mu acaba
    Çok günahım var hesapladım kabaca
    Tükeniyor biliyorum
    Dönüş yok gidiyorum
    Tek çıkış bir af
    Rabbimden diliyorum.
    
    Hoşçakal artık yokum
    Tamam herşey benim suçum
    Ama azcık da sen düşün
    Ne ektin ne biçeceksin
    


*/

@SuppressLint("SetTextI18n")
public class BackgroundWorksActivity extends AppCompatActivity implements ItemSelectListener{
   
   private       RecyclerView           recyclerView;
   private       View                   emptyView;
   private       BackgroundWorksAdapter adapter;
   private       Dialog                 dialog;
   private final List<WorkStat>         works = new ArrayList<>(Worker.getWorks());
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_background_works);
      
      Toolbarx.setToolbar(this, findViewById(R.id.toolbar));
      
      
      recyclerView = findViewById(R.id.recyclerView);
      emptyView    = findViewById(R.id.empty_view);
      
      //Collections.sort(works, ComparatorCompat.comparingLong(WorkStat::getWorkStartTime));
      recyclerView.setAdapter(adapter = new BackgroundWorksAdapter(works, this));
      
      EventBus.getDefault().register(this);
      
      dialog = new Dialog(new WeakReference<>(this), null);
      checkSize();
   }
   
   @Override
   protected void onDestroy(){
      
      EventBus.getDefault().unregister(this);
      
      dialog = null;
      
      super.onDestroy();
   }
   
   private void checkSize(){
      
      //noinspection ConstantConditions
      getSupportActionBar().setSubtitle(String.valueOf(works.size()));
      
      if(works.size() == 0){
         
         if(emptyView.getVisibility() == View.GONE){
            
            emptyView.setVisibility(View.VISIBLE);
         }
      }
      else{
         
         if(emptyView.getVisibility() == View.VISIBLE){
            
            emptyView.setVisibility(View.GONE);
         }
      }
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      
      Bungee.zoomFast(this);
   }
   
   @Override
   public void onItemSelected(int position){
      
      if(Dialog.isOpen) return;
      
      WorkStat workStat = works.get(position);
      
      Worker.onMain(() -> dialog.show(workStat, () -> onWorkFinish(new EventWorkFinish(workStat))), 200L);
   }
   
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onWorkFinish(EventWorkFinish event){
      
      WorkStat workStat = event.getWorkStat();
      
      if(!works.contains(workStat)){
         
         works.add(0, workStat);
         adapter.notifyItemInserted(0);
      }
      else{
         
         adapter.notifyItemChanged(works.indexOf(workStat));
      }
      
      checkSize();
      
      if(!Dialog.isOpen) return;
      
      if(!workStat.equals(dialog.workStat)) return;
      
      editDialog(workStat);
   }
   
   private void editDialog(WorkStat workStat){
      
      dialog.endTimeText.setText(Time.getTimeWithNano(workStat.getWorkEndTime()));
      dialog.terminateTask.setEnabled(false);
      dialog.durationText.setText(Stringx.format("%dms", workStat.getWorkEndTime() - workStat.getWorkStartTime()));
      
      if(workStat.getError() != null){
         
         if(workStat.getCompletableFuture() != null){
            
            if(workStat.getCompletableFuture().isCancelled()){
               
               dialog.stateText.setText("Görev sonlandırıldı");
            }
         }
         else{
            
            dialog.stateText.setText("Görev hata ile sonlandı");
         }
         
         dialog.errorText.setText(workStat.getError());
      }
      else{
         
         dialog.stateText.setText("Görev tamamlandı");
         dialog.errorText.setText("Hata yok");
      }
   }
   
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onWorkStart(EventWorkStart event){
      
      WorkStat workStat = event.getWorkStat();
      
      if(!works.contains(workStat)){
         
         works.add(0, workStat);
         adapter.notifyItemInserted(0);
      }
      else{
         
         adapter.notifyItemChanged(works.indexOf(workStat));
      }
      
      checkSize();
   }
   
   private static final class Dialog extends DialogListener implements DialogAction{
      
      Button                  terminateTask;
      WeakReference<Activity> activityWeakReference;
      WorkStat                workStat;
      TextView                stateText;
      TextView                endTimeText;
      TextView                errorText;
      TextView                durationText;
      TextView                reasonText;
      TextView                starterText;
      TextView                startTimeText;
      Button                  close;
      TextView                priorityText;
      View                    view;
      AlertDialog             dialog;
      static boolean isOpen;
      
      
      Dialog(WeakReference<Activity> activityWeakReference, DialogAction dialogAction){
         
         super(dialogAction);
         this.activityWeakReference = activityWeakReference;
         setDialogAction(this);
      }
      
      @Override
      public void onDialogClose(DialogInterface dialogInterface){
         
         isOpen = false;
         reset();
      }
      
      @Override
      public void onDialogShow(DialogInterface dialogInterface){
         
         
      }
      
      void reset(){
         
         view   = null;
         dialog = null;
      }
      
      @SuppressWarnings("ConstantConditions")
      void show(WorkStat workStat, Runnable runnable){
         
         if(isOpen()) return;
         
         this.workStat = workStat;
         
         AlertDialog.Builder builder = getDialogBuilder();
         view = inflateView();
         setViews();
         
         terminateTask.setOnClickListener(v -> terminateTask(workStat.getCompletableFuture(), runnable));
         
         setReason();
         setTime();
         
         priorityText.setText(workStat.isMinPriority() ? "Düşük" : "Normal");
         errorText.setText(workStat.getError() == null ? "Yok" : workStat.getError());
         
         builder.setView(view);
         builder.setCancelable(true);
         
         dialog = builder.create();
         close.setOnClickListener(v -> dialog.dismiss());
         
         dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
         dialog.getWindow().setBackgroundDrawableResource(R.drawable.circle);
         
         dialog.show();
         
         dialog.setOnCancelListener(getDialogListener());
         dialog.setOnDismissListener(getDialogListener());
      }
      
      void setReason(){
         
         String reason = workStat.getReason();
         int    index  = reason.indexOf(':');
         
         if(index != -1){
            
            starterText.setText(reason.substring(0, index));
            reasonText.setText(reason.substring(index + 1));
         }
      }
      
      AlertDialog.Builder getDialogBuilder(){
         
         return new AlertDialog.Builder(activityWeakReference.get());
      }
      
      @SuppressLint("InflateParams")
      View inflateView(){
         
         return activityWeakReference.get().getLayoutInflater()
                                     .inflate(R.layout.background_works_activity_dialog, null, false);
         
      }
      
      void setTime(){
         
         long endTime   = workStat.getWorkEndTime();
         long startTime = workStat.getWorkStartTime();
         
         startTimeText.setText(Time.getTimeWithNano(startTime));
         
         if(endTime != 0){
            
            endTimeText.setText(Time.getTimeWithNano(endTime));
            
            durationText.setText(Stringx.format("%dms", endTime - startTime));
            
            if(workStat.getCompletableFuture() != null){
               
               if(workStat.getCompletableFuture().isCancelled()){
                  
                  stateText.setText("Görev sonlandırıldı");
               }
            }
            else{
               
               stateText.setText("Görev tamamlandı");
            }
            
            if(terminateTask.isEnabled()){
               
               terminateTask.setEnabled(false);
            }
         }
         else{
            
            
            endTimeText.setText("-");
            stateText.setText("Devam ediyor");
            durationText.setText("-");
            
            if(!workStat.getCompletableFuture().isDone()){
               
               terminateTask.setEnabled(true);
            }
         }
      }
      
      boolean isOpen(){
         
         if(isOpen) return true;
         
         isOpen = true;
         
         return false;
      }
      
      void setViews(){
         
         reasonText    = view.findViewById(R.id.reason);
         starterText   = view.findViewById(R.id.starter);
         startTimeText = view.findViewById(R.id.start_time);
         close         = view.findViewById(R.id.close);
         priorityText  = view.findViewById(R.id.priority);
         
         endTimeText   = view.findViewById(R.id.end_time);
         errorText     = view.findViewById(R.id.error);
         stateText     = view.findViewById(R.id.state);
         durationText  = view.findViewById(R.id.duration);
         terminateTask = view.findViewById(R.id.terminate_task);
         
      }
      
      void terminateTask(CompletableFuture completableFuture, Runnable runnable){
         
         if(completableFuture == null){
            
            Show.globalMessage(activityWeakReference.get(), "Görev listeden silinmiş görünüyor");
            terminateTask.setEnabled(false);
            return;
         }
         
         Timber.d("Görev iptal ediliyor : %s", workStat.getReason());
         completableFuture.cancel(true);
         
         if(completableFuture.isCancelled()){
            
            workStat.setDone(true);
            workStat.setWorkEndTime(System.currentTimeMillis());
            workStat.setError("Görev iptal edildi");
            Timber.d("Görev iptal edildi : %s", workStat.getReason());
            
            runnable.run();
         }
         else{
            
            Timber.d("Görev iptal edilemedi : %s", workStat.getReason());
         }
      }
      
      
   }
   
}
