/*
package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.implementers;

import android.content.Context;
import android.provider.CallLog;


import com.tr.hsyn.telefonrehberi.BuildConfig;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.PostMan;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.interfaces.ICallListener;

import java.util.List;
import java.util.Locale;


public class CallListener implements ICallListener{
   
   private Context context;
   
   public CallListener(Context context) {
      
      this.context = context;
   }
   
   
   @Override
   public void onOutgoingCallStart(long callStartTime, String number) {
   
      startRecord(number);
   }
   
   @Override
   public void onIncommingCallAnswer(long ringingStartTime, long answerTime, String number) {
      startRecord(number);
   }
   
   @Override
   public void onMissedCall(long ringingStartTime, long endTime, String number) {
      handleMissedCall(number, ringingStartTime, endTime);
   }
   
   @Override
   public void onOutgoingCallEnd(long callStartTime, long endTime, String number) {
   
      stopRecord();
   
      handleOutgoingCallEnded(
            number,
            callStartTime,
            endTime);
   }
   
   @Override
   public void onIncommingCallEnd(long ringingStartTime, long speakingStartTime, long endTime, String number) {
   
      stopRecord();
   
      handleIncommingCallEnded(
            number,
            ringingStartTime,
            speakingStartTime,
            endTime);
   }
   
   private void handleIncommingCallEnded(String incommingCallNumber, long callStartTime, long offHookTime, long idleTime) {
      
      String name = getName(context, incommingCallNumber);
      
      long speakTime = idleTime - offHookTime;
      if (speakTime == 0) speakTime = 1;
      long totalTime   = idleTime - callStartTime;
      long ringingTime = offHookTime - callStartTime;
      
      
      String value = String.format(new Locale("tr"),
            
            "Gelen arama sona erdi%n" +
            "=====================%n" +
            "Kişi           : %s%n" +
            "Numara         : %s%n" +
            "Başlama        : %s%n" +
            "Bitiş          : %s%n" +
            "Çalma süresi   : %.2fsn (%dms)%n" +
            "Konuşma süresi : %.2fsn%n" +
            "Toplam süre    : %.2fsn (%dms)",


                                   name,
                                   incommingCallNumber,
                                   Time.getDate(callStartTime),
                                   Time.getDate(idleTime),
            (float) ringingTime / 1000.0F, ringingTime,
            (float) speakTime / 1000.0F,
            (float) totalTime / 1000.0F, totalTime);
      
      
      if (BuildConfig.DEBUG) {
         u.log.d(value);
      }
      
      Worker.onBackground(() -> PostMan.getInstance(context).postText("Gelen Arama", value));
   }
   
   private void handleOutgoingCallEnded(String outgoingCallNumber, long callStartTime, long idleTime) {
      
      //String title = String.format(Locale.getDefault(), "Giden arama sona erdi : [%s] [%s] [%s]", outgoingCallNumber, name, u.formatDate(idleTime));
      
      String name = getName(context, outgoingCallNumber);
      
      final String[] val = {String.format(new Locale("tr"),
            "Giden Arama Sona Erdi%n" +
            "=====================%n" +
            "Kişi     : %s%n" +
            "Numara   : %s%n" +
            "Süre     : %.2f%n" +
            "Start    : %s%n" +
            "End      : %s",
                                          name,
                                          outgoingCallNumber,
            (float) (idleTime - callStartTime) / 1000,
                                          Time.getDate(callStartTime),
                                          Time.getDate(idleTime)
      
      )};
      
      
      Worker.onMain(() -> {
         
         Call call = getCallRecord(outgoingCallNumber, callStartTime, CallLog.Calls.OUTGOING_TYPE);
         
         if (call != null) {
            
            int duration = call.getDuration();
            
            if (duration == 0) {
               
               u.log.d("Konuşma olmadı");
               
               long ringingTime = idleTime - callStartTime;
               
               u.log.d("Telefonun açık kaldığı süre : %.2fsn%n", (float) ringingTime / 1000L);
               
               
               val[0] = String.format(new Locale("tr"),
                     "Giden Arama Sona Erdi%n" +
                     "=====================%n" +
                     "Kişi               : %s%n" +
                     "Numara             : %s%n" +
                     "Konuşma süresi     : Konuşma olmadı%n" +
                     "Start              : %s%n" +
                     "End                : %s%n" +
                     "Çalma süresi       : %.2fsn",
                                      name,
                                      outgoingCallNumber,
                                      Time.formatDate(callStartTime),
                                      Time.formatDate(idleTime),
                     (float) ringingTime / 1000L);
               
               Run.runThread(() -> PostMan.getInstance(context).postText("Giden Arama", val[0]));
               
               if (BuildConfig.DEBUG) {
                  u.log.d(val[0]);
               }
            }
            else {
               
               long speakTime   = 1000L * duration;
               long totalTime   = idleTime - callStartTime;
               long ringingTime = totalTime - speakTime;
               
               
               val[0] = String.format(new Locale("tr"),
                     "Giden Arama Sona Erdi%n" +
                     "=====================%n" +
                     "Kişi               : %s%n" +
                     "Numara             : %s%n" +
                     "Konuşma süresi     : %dsn%n" +
                     "Start              : %s%n" +
                     "End                : %s%n" +
                     "Çalma süresi       : %.2fsn",
                                      name,
                                      outgoingCallNumber,
                                      duration,
                                      Time.formatDate(callStartTime),
                                      Time.formatDate(idleTime),
                                      (float) ringingTime / 1000L);
               
               
               Run.runThread(() -> PostMan.getInstance(context).postText("Giden Arama", val[0]));
               
               if (BuildConfig.DEBUG) {
                  u.log.d(val[0]);
               }
               
               
            }
         }
         else {
            
            u.log.w("call = null");
            
            Run.runThread(() -> PostMan.getInstance(context).postText("Giden Arama", val[0]));
            
            if (BuildConfig.DEBUG) {
               u.log.d(val[0]);
            }
            
         }
      }, 5000);
   }
   
   private void handleMissedCall(String incommingCallNumber, long callStartTime, long idleTime) {
      
      Run.run(() -> {
         
         if (Calls.isRejected(context, incommingCallNumber, callStartTime)) {
            
            Call call = getCallRecord(incommingCallNumber, callStartTime, 5);
            
            if (call != null) {
               
               setName(context, call);
               
               long  time            = idleTime - call.getDate();
               float ringingDuration = (float) time / 1000.0F;
               
               String val = String.format(new Locale("tr"),
                     
                     "Gelen arama reddedildi%n" +
                     "======================%n" +
                     "Kişi            : %s%n" +
                     "Numara          : %s%n" +
                     "Tarih           : %s%n" +
                     "Reddetme süresi : %.2fsn (%dms)",

                                          call.getName(),
                                          incommingCallNumber,
                                          Time.getDate(callStartTime),
                                          ringingDuration, time
               
               );
               
               Run.runThread(() -> PostMan.getInstance(context).postText("Reddedilen Arama", val));
               
               if (BuildConfig.DEBUG) {
                  u.log.d(val);
               }
               
            }
         }
         else {
            
            Call call = getCall(context, incommingCallNumber, callStartTime, CallLog.Calls.MISSED_TYPE);
            
            if (call != null) {
               
               setName(context, call);
               
               long ringingDuration = idleTime - call.getDate();
               
               String val = String.format(new Locale("tr"),
                     
                     "Cevapsız Çağrı%n" +
                     "==============%n" +
                     "Kişi            : %s%n" +
                     "Numara          : %s%n" +
                     "Tarih           : %s%n" +
                     "Çalma süresi    : %.2fsn (%dms)",


                                          call.getName(),
                                          incommingCallNumber,
                                          Time.getDate(callStartTime),
                     (float) ringingDuration / 1000.0F,
                                          ringingDuration
               );
               
               Run.runThread(() -> PostMan.getInstance(context).postText("Cevapsız Arama", val));
               
               if (BuildConfig.DEBUG) {
                  u.log.d(val);
               }
            }
         }
      }, 5000);
   }
   
   private Call getCallRecord(String number, long callStartTime, int type) {
      
      return getCall(context, number, callStartTime, type);
   }
   
   private void setName(Context context, Call call) {
      
      if (context == null) return;
      
      if (call.getName() == null) {
         call.setName(getName(context, call.getNumber()));
      }
   }
   
   public static String getName(Context context, String number) {
      
      return Contacts.getContactNameWithNumber(context, number);
   }
   
   private Call getCall(Context context, String number, long date, int type) {
      
      if (number == null) {
         
         u.saveToFile(context, "Hata", "numara alınamadı");
         u.log.w("numara alınamadı");
         return null;
      }
      
      
      List<Call> callList = Calls.getCalls(context);
      
      if (callList == null) return null;
      
      for (Call call : callList) {
         
         if (call.getType() == type && (Contacts.normalizeNumber(number).equals(Contacts.normalizeNumber(call.getNumber())) && (Math.abs(date - call.getDate()) < 5000))) {
            
            return call;
         }
      }
      
      return null;
   }
   
   private void startRecord(String number) {
   
      stopRecord();
      
      AudioService.recordCall(context, Contacts.normalizeNumber(number));
   }
   
   private void stopRecord() {
      
      AudioService.setRunning(false);
   }
   
   
}
*/
