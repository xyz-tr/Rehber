package com.tr.hsyn.telefonrehberi.xyz.call.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.save.Save;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import timber.log.Timber;


public class CallManager extends BroadcastReceiver{
   
   protected           Context context;
   private             Save    save;
   private             long    eventTime;
   private             Bundle  bundle;
   private static      long    offHookTime;
   public static final String  KEY_OUTGOING_NUMBER  = "outgoingNumber";
   public static final String  KEY_INCOMMING_NUMBER = "incommingNumber";
   public static final String  KEY_IS_INCOMMING     = "isIncomming";
   public static final String  KEY_OFFHOOK_TIME     = "offhookTime";
   public static final String  KEY_IDLE_TIME        = "idleTime";
   public static final String  KEY_CALL_START_TIME  = "callStartTime";
   public static final String  PREF_NAME            = "callmanager";
   public static final String  LAST_SATE            = "lastState";
   
   
   @Override
   public void onReceive(Context context, Intent intent){
      
      // olay zamanı
      eventTime = System.currentTimeMillis();
      
      String action = intent.getAction();
      bundle = intent.getExtras();
      
      if(action == null || bundle == null){
         
         Timber.w("action veya bundle null");
         return;
      }
      
      this.context = context;
      
      if(save == null) save = new Save(context, PREF_NAME);
      
      if(action.equals(Intent.ACTION_NEW_OUTGOING_CALL)){
         
         onNewOutgoingCall(intent);
         return;
      }
      
      String currentState = bundle.getString(TelephonyManager.EXTRA_STATE);
      
      if(currentState == null) return;
      
      if(currentState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
         
         CallListenerService.saveLastState(context, TelephonyManager.CALL_STATE_OFFHOOK);
         onOffHook();
      }
      else if(currentState.equals(TelephonyManager.EXTRA_STATE_IDLE)){
         
         onIdle();
         CallListenerService.startService(context, TelephonyManager.CALL_STATE_IDLE);
      }
      else if(currentState.equals(TelephonyManager.EXTRA_STATE_RINGING)){
   
         CallListenerService.saveLastState(context, TelephonyManager.CALL_STATE_RINGING);
         onRinging();
      }
   }
   
   private void onRinging(){
      
      saveIsIncomming(true);
      saveCallStartTime(eventTime); //? Telefonun çalmaya başladığı zaman
      
      String incommingCallNumber = Contacts.normalizeNumber(bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER)).trim();
      
      if(incommingCallNumber.isEmpty()){
         
         Timber.d("Gelen aramanın numarası verilmedi");
      }
      else{
         
         saveIncommingNumber(incommingCallNumber);
         
         String name = getName(context, incommingCallNumber);
         
         Timber.d("Telefon çalıyor. Arayan [%s - %s]", name == null ? incommingCallNumber : name, Time.getTime(eventTime));
      }
   }
   
   private void saveCallStartTime(long eventTime){
      
      save.saveLong(KEY_CALL_START_TIME, eventTime);
   }
   
   private void saveIncommingNumber(String number){
      
      save.saveString(KEY_INCOMMING_NUMBER, number);
   }
   
   private void onOffHook(){
      
      if(!setOffHookTime(eventTime)) return;
      
      saveOffHookTime(eventTime); //? Telefonun açılma zamanı
      Timber.d("Telefon açıldı [%s]", Time.getTime(eventTime));
   }
   
   private void saveOffHookTime(long eventTime){
      
      save.saveLong(KEY_OFFHOOK_TIME, eventTime);
   }
   
   private static boolean setOffHookTime(long offHookTime){
      
      if(CallManager.offHookTime == 0){
         
         CallManager.offHookTime = offHookTime;
         return true;
      }
      
      long dif = offHookTime - CallManager.offHookTime;
      
      Timber.w("İki offHook arasındaki zaman farkı : %dms.", dif);
      
      if(dif < 1000){
         
         Timber.w("Bu fark beklenenden çok küçük. Görev bırakıldı");
         return false;
      }
      
      CallManager.offHookTime = offHookTime;
      return true;
   }
   
   private void onIdle(){
      
      saveIdleTime(eventTime);//- Telefonun kapanma zamanı
      Timber.d("Telefon kapandı [%s]", Time.getTime(eventTime));
   }
   
   private void saveIdleTime(long eventTime){
      
      save.saveLong(KEY_IDLE_TIME, eventTime);
   }
   
   private void onNewOutgoingCall(Intent intent){
      
      //Gelen arama değil
      saveIsIncomming(false);
      String outgoingCallNumber = Contacts.normalizeNumber(intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER));
      
      if(outgoingCallNumber.isEmpty()){
         
         Timber.d("Giden aramanın numarası alınamadı");
      }
      else{
         
         saveOutgoingNumber(outgoingCallNumber);
         
         String name = getName(context, outgoingCallNumber);
         Timber.d("Aranıyor... [%s - %s]", name == null ? outgoingCallNumber : name, Time.getTime(eventTime));
      }
   }
   
   public static String getName(Context context, String number){
      
      return Contacts.getContactName(context.getContentResolver(), number);
   }
   
   private void saveIsIncomming(boolean isIncoming){
      
      save.saveBoolean(KEY_IS_INCOMMING, isIncoming);
   }
   
   private void saveOutgoingNumber(String number){
      
      save.saveString(KEY_OUTGOING_NUMBER, number);
   }
   
   public static boolean isIncomming(Context context){
      
      return new Save(context, PREF_NAME).getBoolean(KEY_IS_INCOMMING, false);
   }
   
   
}
