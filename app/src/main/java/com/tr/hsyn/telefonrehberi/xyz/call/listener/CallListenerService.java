package com.tr.hsyn.telefonrehberi.xyz.call.listener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.provider.CallLog;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.NewCall;
import com.tr.hsyn.telefonrehberi.util.save.Save;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.database.CallLogDB;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;
import com.tr.hsyn.telefonrehberi.xyz.call.main.service.NotedCall;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.KEY_CALL_START_TIME;
import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.KEY_IDLE_TIME;
import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.KEY_INCOMMING_NUMBER;
import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.KEY_OFFHOOK_TIME;
import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.KEY_OUTGOING_NUMBER;
import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.LAST_SATE;
import static com.tr.hsyn.telefonrehberi.xyz.call.listener.CallManager.PREF_NAME;


public class CallListenerService extends Service{
   
   private              Save     save;
   private              ICallLog callOperator;
   public static final  String   EXTRA_CALL_STATE = "call_state";
   private static final long     BIN_MS           = 1000L;
   
   
   public CallListenerService(){
      
   }
   
   
   @Override
   public void onCreate(){
      
      super.onCreate();
      save         = new Save(this, PREF_NAME);
      callOperator = new CallLogDB(this);
   }
   
   @Override
   public int onStartCommand(Intent intent, int flags, int startId){
      
      if(intent == null){
         
         stopService();
      }
      else{
         
         int state = intent.getIntExtra(EXTRA_CALL_STATE, -99);
         
         if(state == -99) stopService();
         else{
            
            startForground();
            onStateChanged(state);
         }
      }
      
      return Service.START_NOT_STICKY;
   }
   
   @Override
   public IBinder onBind(Intent intent){
     
      throw new UnsupportedOperationException("Not yet implemented");
   }
   
   public static void startService(Context context, int state){
      
      Intent i = new Intent(context, CallListenerService.class);
      i.putExtra(EXTRA_CALL_STATE, state);
      
      context.startService(i);
   }
   
   private void taskCompleted(){
      
      stopService();
   }
   
   private void handleIncommingCallEnded(String incommingCallNumber, long callStartTime, long offHookTime, long idleTime){
      
      String name = CallManager.getName(this, incommingCallNumber);
      
      final String title = String.format(Locale.getDefault(), "Gelen arama sona erdi : [%s] [%s] [%s]", incommingCallNumber, name != null ? name : "Rehberde kayıtlı değil", u.formatDate(idleTime));
      
      Timber.d(title);
      
      Call phoneCall = getCallRecord(incommingCallNumber, callStartTime, CallLog.Calls.INCOMING_TYPE);
      
      if(phoneCall != null){
         
         int duration = phoneCall.getDuration();
         
         long speakTime   = 1000L * duration;
         long totalTime   = idleTime - callStartTime;
         long ringingTime = totalTime - speakTime;
         
         if(duration > 0){
            
            String value = "";
            value += Stringx.format("%nBaşlangıç     : %s (native %s)(fark %dms)%n", u.formatDate(callStartTime), u.formatDate(phoneCall.getDate()), phoneCall.getDate() - callStartTime);
            value += Stringx.format("Bitiş          : %s%n", u.formatDate(idleTime));
            value += Stringx.format("Çalma süresi   : %.2fsn (%dms)%n", (float) ringingTime / 1000.0F, ringingTime);
            value += Stringx.format("Konuşma süresi : %.2fsn (native %.2fsn)%n", (float) (idleTime - offHookTime) / 1000.0F, (float) speakTime / 1000.0F);
            value += Stringx.format("Toplam süre    : %dms %.2fsn", totalTime, (float) totalTime / 1000.0F);
            
            Logger.d(value);
         }
         else{
            
            Timber.d("Konuşma olmadı");
         }
         
         phoneCall.setRingingDuration(ringingTime);
         phoneCall.setSpeakingDuration(idleTime - offHookTime);
         phoneCall.setSharable(true);
         
         if(phoneCall.getName() == null && name != null){
            
            phoneCall.setName(name);
            updateCachedName(phoneCall);
         }
         
         if(isCallSaved(phoneCall)){
            
            EventBus.getDefault().post(NewCall.create(phoneCall).setTag("CallManager"));
         }
      }
      else{
         
         Timber.w("Arama kaydı alınamadı - gelen arama");
         Timber.d("Kayıtlara tekrar bakılıyor");
         
         getRecord(incommingCallNumber, callStartTime, idleTime, offHookTime);
         
      }
      
      taskCompleted();
   }
   
   private void handleOutgoingCallEnded(String outgoingCallNumber, long offHookTime, long idleTime){
      
      String name  = CallManager.getName(this, outgoingCallNumber);
      String title = String.format(Locale.getDefault(), "Giden arama sona erdi : [%s] [%s] [%s]", outgoingCallNumber, name != null ? name : "Rehberde kayıtlı değil", u.formatDate(idleTime));
      Logger.d(title);
      
      Call phoneCall = getCallRecord(outgoingCallNumber, offHookTime, CallLog.Calls.OUTGOING_TYPE);
      
      if(phoneCall != null){
         
         int duration = phoneCall.getDuration();
         
         long ringingTime = idleTime - offHookTime;
         long speakTime   = 1000L * duration;
         
         if(duration == 0){
            
            Timber.d("Konuşma olmadı");
            Timber.d("Telefonun açık kaldığı süre : %.2fsn%n", (float) ringingTime / BIN_MS);
         }
         else{
            
            long totalTime = idleTime - offHookTime;
            ringingTime = totalTime - speakTime;
            
            String val = String.format(Locale.getDefault(), "Telefon açılana kadar geçen süre : %.2f sn%n", (float) ringingTime / BIN_MS);
            val += String.format(Locale.getDefault(), "Konuşma süresi                   : %.2f sn%n", (float) speakTime / BIN_MS);
            val += String.format(Locale.getDefault(), "Toplam süre                      : %.2f sn", (float) totalTime / BIN_MS);
            
            phoneCall.setSpeakingDuration(speakTime);
            Logger.d(val);
         }
         
         phoneCall.setRingingDuration(ringingTime);
         phoneCall.setSharable(true);
         
         if(phoneCall.getName() == null && name != null){
            
            phoneCall.setName(name);
            updateCachedName(phoneCall);
         }
         
         Timber.d(phoneCall.toString());
         
         if(isCallSaved(phoneCall)){
            
            EventBus.getDefault().post(NewCall.create(phoneCall).setTag("CallManager"));
         }
      }
      else{
         
         Timber.d("Arama kaydı alınamadı - giden arama");
      }
      
      taskCompleted();
   }
   
   private void handleMissedCall(String incommingCallNumber, long callStartTime, long idleTime){
      
      String name = CallManager.getName(this, incommingCallNumber);
      
      if(CallStory.isRejected(getContentResolver(), incommingCallNumber, callStartTime)){
         
         Call phoneCall = getCallRecord(incommingCallNumber, callStartTime, 5);
         
         if(phoneCall != null){
            
            long  time            = idleTime - phoneCall.getDate();
            float ringingDuration = time / 1000.0F;
            
            phoneCall.setRingingDuration(time);
            phoneCall.setSharable(true);
            
            String val = String.format(Locale.getDefault(), "Reddedilene kadar telefonun çaldığı süre : %.2fsn (%dms)", ringingDuration, time);
            
            Timber.d("Gelen arama reddedildi : %s", incommingCallNumber);
            Timber.d(val);
            
            if(phoneCall.getName() == null && name != null){
               
               phoneCall.setName(name);
               updateCachedName(phoneCall);
            }
            
            if(isCallSaved(phoneCall)){
               
               EventBus.getDefault().post(NewCall.create(phoneCall).setTag("CallManager"));
            }
         }
         else{
            
            Timber.w("Reddedilen arama kaydı alınamadı");
         }
      }
      else{
         
         Call phoneCall = getCall(this, incommingCallNumber, callStartTime, CallLog.Calls.MISSED_TYPE);
         
         if(phoneCall != null){
            
            long ringingDuration = idleTime - phoneCall.getDate();
            phoneCall.setRingingDuration(ringingDuration);
            phoneCall.setSharable(true);
            
            Timber.d("Cevapsız çağrı : %s", incommingCallNumber);
            Timber.d("Telefonun çaldığı süre : %dms (%.2fsn)", ringingDuration, (float) ringingDuration / 1000.0F);
            
            if(phoneCall.getName() == null && name != null){
               
               phoneCall.setName(name);
               updateCachedName(phoneCall);
            }
            
            if(isCallSaved(phoneCall)){
               
               EventBus.getDefault().post(NewCall.create(phoneCall).setTag("CallManager"));
            }
         }
         else{
            
            Timber.w("kayıtlardan numara alınamadı");
         }
      }
      
      taskCompleted();
   }
   
   private Call getCallRecord(String number, long callStartTime, int type){
      
      return getCall(this, number, callStartTime, type);
   }
   
   private void getRecord(String incommingCallNumber, long callStartTime, long idleTime, long offHookTime){
      
      Worker.onBackground(() -> {
         
         Call phoneCall2 = getCallRecord(incommingCallNumber, callStartTime, CallLog.Calls.INCOMING_TYPE);
         
         if(phoneCall2 != null){
            
            Timber.d("Kayıt bulundu : %s", phoneCall2);
            
            int  duration    = phoneCall2.getDuration();
            long speakTime   = 1000L * duration;
            long totalTime   = idleTime - callStartTime;
            long ringingTime = totalTime - speakTime;
            
            if(duration > 0){
               
               phoneCall2.setSpeakingDuration(idleTime - offHookTime);
            }
            else{
               
               Timber.d("Konuşma olmadı");
            }
            
            phoneCall2.setRingingDuration(ringingTime);
            phoneCall2.setSharable(true);
            
            String name = phoneCall2.getName();
            
            if(name == null){
               
               name = CallManager.getName(this, incommingCallNumber);
            }
            
            if(name != null){
               
               phoneCall2.setName(name);
               updateCachedName(phoneCall2);
            }
            
            if(isCallSaved(phoneCall2)){
               
               EventBus.getDefault().post(NewCall.create(phoneCall2).setTag("CallManager"));
            }
         }
         else{
            
            Timber.w("Arama kaydı alınamadı. Kayıt oluştuktan hemen sonra kullanıcı tarafından silinmiş olabilir");
         }
      }, 5000L);
      
   }
   
   private void updateCachedName(Call phoneCall){
      
      if(phoneCall.getName() != null){
         
         CallStory.addToCachedName(this, phoneCall.getDate(), phoneCall.getName());
      }
   }
   
   private boolean isCallSaved(Call phoneCall){
      
      return callOperator.add(phoneCall);
   }
   
   private static Call getCall(@NonNull Context context, @NonNull String number, long date, int type){
      
      List<Call> phoneCallList = CallStory.getLastCalls(context.getContentResolver());
      
      if(phoneCallList.isEmpty()){
         
         Timber.w("Son aramalar alınamadı");
         return null;
      }
      
      List<Call> matches = new ArrayList<>(5);
      
      for(Call phoneCall : Stream.of(phoneCallList).filter(cx -> cx.getType() == type).toList()){
         
         if(Contacts.matchNumbers(number, phoneCall.getNumber()) && (Math.abs(date - phoneCall.getDate()) < 2000)){
            
            matches.add(phoneCall);
         }
      }
      
      if(matches.isEmpty()){
         
         Timber.w("Eşleşen bir numara bulunamadı");
         
         noteTheCall(number, date, type);
      }
      else{
         
         if(matches.size() == 1) return matches.get(0);
         
         return Collections.max(matches, ComparatorCompat.comparingLong(Call::getDate));
      }
      
      return null;
   }
   
   private static void noteTheCall(String number, long date, int type){
   
      number = Contacts.createKeyFromNumber(number);
      
      Paper.book("noted_call").write(String.valueOf(date), new NotedCall(number, date, type));
   }
   
   private String getState(int state){
      
      String stateStr = "-";
      
      switch(state){
         
         case TelephonyManager.CALL_STATE_IDLE: stateStr = TelephonyManager.EXTRA_STATE_IDLE;
            break;
         case TelephonyManager.CALL_STATE_OFFHOOK: stateStr = TelephonyManager.EXTRA_STATE_OFFHOOK;
            break;
         case TelephonyManager.CALL_STATE_RINGING: stateStr = TelephonyManager.EXTRA_STATE_RINGING;
            break;
      }
      
      return stateStr;
   }
   
   private void onStateChanged(int state){
   
      int lastState = getLastState();
      
      Logger.w("Previous State : %s%n" +
               "Present State  : %s", getState(lastState), getState(state));
      
      if(state == TelephonyManager.CALL_STATE_IDLE){
         
         if(lastState == TelephonyManager.CALL_STATE_OFFHOOK){
            
            if(CallManager.isIncomming(this)){//? Gelen arama ise telefon açılmış
               
               Worker.onBackground(() -> handleIncommingCallEnded(getIncommingNumber(), getCallStartTime(), getOffHookTime(), getIdleTime()),
                                   "CallManager:Gelen aramayı işleme",
                                   2000L);
            }
            else{
               Worker.onBackground(() -> handleOutgoingCallEnded(getOutgoingNumber(), getOffHookTime(), getIdleTime()),
                                   "CallManager:Giden aramayı işleme",
                                   2000);
            }
         }
         else if(lastState == TelephonyManager.CALL_STATE_RINGING){
            
            //rejected or missed ?
            
            Worker.onBackground(
                  () -> handleMissedCall(getIncommingNumber(), getCallStartTime(), getIdleTime()),
                  "CallManager:Cevapsız aramayı işleme",
                  2000L);
            
         }
      }
      else{
         
         Worker.onMain(this::taskCompleted, 500L);
      }
      
      saveLastState(this,state);
   }
   
   public static void saveLastState(Context context, int state){
      
      new Save(context, PREF_NAME).saveInt(LAST_SATE, state);
   }
   
   private void stopService(){
      
      stopForground();
      stopSelf();
   }
   
   private void stopForground(){
      
      stopForeground(true);
   }
   
   private void startForground(){
      
      String CHANNEL_ID   = "xyz.964745625";
      String CHANNEL_NAME = "CallListener Channel";
      
      NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                  .setSmallIcon(R.mipmap.ic_launcher_round)
                  .setPriority(NotificationCompat.PRIORITY_MIN)
                  .setVisibility(NotificationCompat.VISIBILITY_SECRET);
      
      if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
         
         NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         NotificationChannel chan    = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_MIN);
         
         if(manager != null){
            
            chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            manager.createNotificationChannel(chan);
            
            startForeground(18, builder.build());
         }
      }
      else{
         
         startForeground(0, builder.build());
      }
   }
   
   private long getOffHookTime(){
      
      return save.getLong(KEY_OFFHOOK_TIME, 1L);
   }
   
   private long getIdleTime(){
      
      return save.getLong(KEY_IDLE_TIME, 1L);
   }
   
   private String getOutgoingNumber(){
      
      return save.getString(KEY_OUTGOING_NUMBER, "");
   }
   
   private String getIncommingNumber(){
      
      return save.getString(KEY_INCOMMING_NUMBER, "");
   }
   
   private long getCallStartTime(){
      
      return save.getLong(KEY_CALL_START_TIME, 1L);
   }
   
   public int getLastState(){
      
      return save.getInt(LAST_SATE, TelephonyManager.CALL_STATE_IDLE);
   }
}
