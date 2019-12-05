package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;


import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.database.CallLogDB;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * <h1>CallLoadFragment</h1>
 * Arama kayıtlarını almakla görevli fragment
 */
public abstract class CallLogLoader extends CallLogTitle{
   
   /**
    * Kayıtlar yüklendi mi?
    */
   @Getter private                        boolean callsLoaded;
   @Getter(AccessLevel.PROTECTED) private boolean isAnyLoad;
   
   /**
    * İzinler için gerekli code
    */
   private static final int RC_CALLS = 4;
   
   /**
    * Arama kayıtlarını almak için başlangıç noktası.
    */
   @Override
   protected void loadCalls(){
      
      if(getContext() == null) return;
      
      callsLoaded = false;
      
      if(Phone.isPermissionsGrant(getContext(), getCallsPermissions())){
         
         onPermissionsGranted();
         return;
      }
      
      requestCallPermissions();
   }
   
   /**
    * Gereken izinleri iste.
    */
   @Override
   protected void requestCallPermissions(){
      
      requestPermissions(getCallsPermissions(), RC_CALLS);
   }
   
   @AfterPermissionGranted(RC_CALLS)
   private void onPermissionsGranted(){
      
      isAnyLoad = true;
      onCallsLoading();
      
      Worker.onBackground(
            this::getCalls,
            "CallLoadFragment:Arama kayıtlarını alma")
            .whenCompleteAsync(this::onCallsLoaded, Worker.getMainThreadExecutor());
      
      
      Logger.i("Granted Call Permissions%n" +
               "========================%n" +
               "%s", TextUtils.join("\n", getCallsPermissions()));
   }
   
   /**
    * Arama kayıtlarını getir.
    *
    * @return Kayıtlar
    */
   private List<ICall> getCalls(){
      
      List<ICall> phoneCalls = new ArrayList<>();
      
      if(getContext() != null){
         
         ICallLog callLog = new CallLogDB(getContext());
         
         phoneCalls = callLog.getAll();
         //List<Call> phoneCalls = CallStory.getSystemCallLogCalls(getContext().getContentResolver());
         
         callsLoaded = true;
      }
      
      
      Logger.w("Arama kaydı sayısı : %d", phoneCalls.size());
      return phoneCalls;
   }
   
   /**
    * Kayıtlar alınmaya başladığında
    */
   abstract void onCallsLoading();
   
   /**
    * Kayıtlar alındığında.
    *
    * @param calls Kayıtlar
    */
   protected abstract void onCallsLoaded(List<ICall> calls, Throwable throwable);
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
   }
   
   
   
    /*protected final void loadCalls(boolean isProgress) {
      
      this.isProgress = isProgress;
      
      loadCalls();
   }*/
   
  /* private void loadCallsWithProgress() {
   
      if(getActivity() == null) return;
      
      callLogDB = new CallLogDB(getContext());
      int callCount = callLogDB.getCallCount();
      calls = new ArrayList<>();
      
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
   
      View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_call_load, null, false);
      
      progressText = view.findViewById(R.id.progressText);
      TextView totalCount = view.findViewById(R.id.totalText);
      totalCount.setText(String.valueOf(callCount));
      progressBar = view.findViewById(R.id.progress);
      progressBar.setMax(callCount);
      progressBar.setProgress(0);
      
      callLoadDialog = builder.create();
      callLoadDialog.setView(view);
   
      if (getCurrentPage() == MainActivity.PAGE_CALL_LOG) {
   
         callLoadDialog.show();
      }
      
      callLoadDialog.setCancelable(false);
      
      
      callLogDB.getAllCalls(this);
   }*/
   /*@Override
   public void onCallGet(PhoneLogCall call, int progress) {
      
      if(total == 0) {
         
         onCallsLoaded(CallStory.createStory(calls));
         return;
      }
      
      calls.add(call);
      progressBar.setProgress(progress);
      progressText.setText(String.valueOf(progress));
   
      
      if (!callLoadDialog.isShowing()) {
   
         if (getCurrentPage() == MainActivity.PAGE_CALL_LOG) {
            
            callLoadDialog.show();
         }
      }
   
      if (total == progress) {
      
         onCallsLoaded(CallStory.createStory(calls));
         callLoadDialog.cancel();
         callLogDB.close();
      }
      
   }*/
}
