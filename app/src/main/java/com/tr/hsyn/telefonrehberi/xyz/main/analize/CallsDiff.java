package com.tr.hsyn.telefonrehberi.xyz.main.analize;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.util.Listx;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;

import java.util.Collection;
import java.util.List;

import lombok.val;


public class CallsDiff{
   
   private final ContentResolver contentResolver;
   private final CallsDiffInfo   info = new CallsDiffInfo();
   private final boolean         _callInBackgroundThread;
   private final int             _threadPriority;
   private final Callback        callback;
   private final ICallLog        callLog;
   
   private final Runnable work = new MyRunnable();
   
   public CallsDiff(ContentResolver contentResolver, Callback callback, ICallLog callLog, boolean callInBackground, int priority){
      
      this.contentResolver    = contentResolver;
      this.callback           = callback;
      this.callLog            = callLog;
      _callInBackgroundThread = callInBackground;
      _threadPriority         = priority;
   }
   
   public void start(){
      
      String reason = "CallsDiff:Arama kayıtlarındaki değişiklikleri kontrol etme";
      Worker.onBackground(work, reason, _threadPriority < Thread.NORM_PRIORITY);
   }
   
   private class MyRunnable implements Runnable{
      
      @Override
      public void run(){
         
         List<ICall> database = getCalls(false);
         List<ICall> system   = getCalls(true);
         
         if(!isNull(database, system)){
   
            val newCalls     = Listx.difference(system, database);
            val removedCalls = Listx.difference(database, system);
   
            info.getReport().delete(0, info.getReport().length());
            checkNewCalls(newCalls);
            checkRemovedCalls(removedCalls);
   
            info.setNewCalls(newCalls);
            info.setRemovedCalls(removedCalls);
            callBack();
         }
      }
      
      private List<ICall> getCalls(boolean isSystem){
   
         List<ICall> calls;
         
         if(isSystem) calls =  CallStory.getSystemCallLogCalls(contentResolver);
         else calls = callLog.getAll();
         
         return calls;
      }
      
      private void checkNewCalls(Collection<ICall> calls){
         
         val report = info.getReport();
         
         if(calls.isEmpty()){
            
            report.append("Yeni bir arama kaydı yok.\n");
         }
         else{
   
            report.append(Stringx.format("%d yeni arama var.\n", calls.size()));
   
            callLog.add(calls);
   
            report.append("Aramalar veri tabanına kaydedildi.\n");
         }
      }
      
      private void checkRemovedCalls(Collection<ICall> calls){
         
         val report = info.getReport();
         
         if(calls.isEmpty()){
            
            report.append("Silinen bir arama kaydı yok.\n");
            return;
         }
         
         boolean alreadyDeleted = Stream.of(calls).allMatch(ICall::isDeleted);
         
         if(alreadyDeleted){
            
            report.append("Silinen bir arama kaydı yok.\n");
            return;
         }
         
         //zaten silinmiş olanları çıkar
         calls = Stream.of(calls).filter(call -> !call.isDeleted()).toList();
         
         long date = System.currentTimeMillis();
         Stream.of(calls).forEach(call -> call.setDeletedDate(date));
         
         callLog.update(calls);
         
         report.append("Aramalar silindi olarak güncellendi.\n");
      }
      
      private void callBack(){
         
         if(callback != null){
            
            if(_callInBackgroundThread){
               
               callback.onCallsDiffResult(info);
            }
            else{
               
               new Handler(Looper.getMainLooper()).post(() -> callback.onCallsDiffResult(info));
            }
         }
      }
      
      private boolean isNull(List<ICall> callLogs, List<ICall> calls){
         
         val res = callLogs == null || calls == null;
         
         if(res){
            
            info.getReport().append("Arama kayıtları alınamadı.\n");
         }
         
         return res;
      }
   }
   
}
