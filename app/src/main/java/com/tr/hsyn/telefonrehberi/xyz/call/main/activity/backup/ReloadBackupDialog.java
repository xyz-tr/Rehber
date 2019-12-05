package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.provider.CallLog;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.ReloadCall;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.CallLogFiltered;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import timber.log.Timber;


@SuppressWarnings("ConstantConditions")
class ReloadBackupDialog{
   
   private ProgressBar progressBar;
   private TextView    size, title, result;
   private TextView                progressText;
   private Button                  close;
   private File                    file;
   private int                     zatenOlan;
   private AlertDialog             dialog;
   private WeakReference<Activity> activityWeakReference;
   private ViewGroup               view;
   
   @SuppressLint("InflateParams")
   ReloadBackupDialog(Activity activity, File file){
      
      this.file             = file;
      activityWeakReference = new WeakReference<>(activity);
      
      view = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.reload_backup, null, false);
      
      dialog = new AlertDialog.Builder(activityWeakReference.get()).create();
      dialog.setCancelable(false);
      dialog.setView(view);
      
      progressBar  = view.findViewById(R.id.progressLoading);
      size         = view.findViewById(R.id.size);
      progressText = view.findViewById(R.id.progressText);
      title        = view.findViewById(R.id.title);
      close        = view.findViewById(R.id.close);
      result       = view.findViewById(R.id.result);
      
      close.setOnClickListener((v) -> dialog.dismiss());
      
      progressBar.getProgressDrawable().setColorFilter(
            u.getPrimaryColor(activity), android.graphics.PorterDuff.Mode.SRC_IN);
      
      dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      dialog.show();
      
      Worker.onBackground(this::onReload, "BackupCallLogActivity:Arama kaydı yedeğini geri yükleme");
      
   }
   
   @SuppressLint("SetTextI18n")
   private void onReload(){
      
      final List<ICall> phoneCalls  = BackupCallLogActivity.readCalls(file.getAbsolutePath());
      List<ICall>       systemCalls = CallStory.getSystemCallLogCalls(activityWeakReference.get().getContentResolver());
      
      if(phoneCalls == null){
         
         Worker.onMain(() -> Toast.makeText(activityWeakReference.get(), activityWeakReference.get().getString(R.string.dosya_okunamadi), Toast.LENGTH_SHORT).show());
         return;
      }
      
      Worker.onMain(() -> {
         
         title.setText(BackupItem.convertBackupName(file.getName()));
         size.setText("/" + phoneCalls.size());
         progressBar.setMax(phoneCalls.size());
         progressBar.setProgress(1);
         progressText.setText(String.valueOf(1));
         
      });
      
      
      for(int i = 0; i < phoneCalls.size(); i++){
         
         ICall phoneCall = phoneCalls.get(i);
         
         int index = systemCalls.indexOf(phoneCall);
         
         if(index != -1){
            
            zatenOlan++;
         }
         else{
            
            addCallToCallLog(phoneCall);
         }
         
         int finalI = i + 1;
         
         Worker.onMain(() -> {
            
            progressBar.setProgress(finalI);
            progressText.setText(String.valueOf(finalI));
         });
         
      }
      
      Worker.onMain(() -> {
         
         close.setEnabled(true);
         
         if(zatenOlan == 0){
            
            result.setText(u.format("Tüm kayıtlar geri yüklendi"));
            
            CallLogFiltered.setNeedRefresh(true);
         }
         else if(zatenOlan == phoneCalls.size()){
            
            result.setText(u.format("Kayıtların hepsi zaten vardı"));
         }
         else{
            
            result.setText(u.format("%d kayıt zaten vardı.%n%d kayıt geri yüklendi", zatenOlan, phoneCalls.size() - zatenOlan));
            
            CallLogFiltered.setNeedRefresh(true);
         }
         
         TransitionManager.beginDelayedTransition(view.findViewById(R.id.root));
         result.setVisibility(View.VISIBLE);
      });
   }
   
   @SuppressLint("MissingPermission")
   private void addCallToCallLog(@NonNull ICall phoneCall){
      
      ContentValues values = new ContentValues();
      values.put(CallLog.Calls.CACHED_NAME, phoneCall.getName());
      values.put(CallLog.Calls.NUMBER, phoneCall.getNumber());
      values.put(CallLog.Calls.DATE, phoneCall.getDate());
      values.put(CallLog.Calls.DURATION, phoneCall.getDuration());
      values.put(CallLog.Calls.TYPE, phoneCall.getType());
      values.put(CallLog.Calls.IS_READ, 1);
      values.put(CallLog.Calls.PHONE_ACCOUNT_ID, "xyz");
      
      if(activityWeakReference.get().getContentResolver().insert(CallLog.Calls.CONTENT_URI, values) != null){
         
         EventBus.getDefault().post(new ReloadCall(phoneCall));
      }
      else{
         
         Timber.d("Kayıt geri yüklenemedi : %d (log)", phoneCall.getDate());
      }
      
   }
   
}
