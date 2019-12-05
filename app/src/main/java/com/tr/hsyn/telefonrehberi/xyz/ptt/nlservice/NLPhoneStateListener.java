package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.kitchen.ICallListener;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.kitchen.IStateHandler;

import timber.log.Timber;


public class NLPhoneStateListener extends PhoneStateListener implements IStateHandler{
   
   private static int           lastSate        = TelephonyManager.CALL_STATE_IDLE;
   private        Context       context;
   private static long          offHookTime;
   private static long          callStartTime;
   private static boolean       isIncomingCall;
   private static String        number;
   private static long          time;
   private        ICallListener callListener;
   
   
   
   public NLPhoneStateListener(Context context) {
      
      this.context = context;
      //callListener = new CallListener(context);
   }
   
   
   @Override
   public void onCallStateChanged(int state, String phoneNumber) {
      super.onCallStateChanged(state, phoneNumber);
   
      time = System.currentTimeMillis();
   
      if (state == lastSate) {
   
         //Timber.d("Same state : %d", state);
         return;
      }
   
      number = phoneNumber;
   
      switch (state) {
         
         case TelephonyManager.CALL_STATE_OFFHOOK: onOffHook();break;
         case TelephonyManager.CALL_STATE_RINGING: onRinging();break;
         case TelephonyManager.CALL_STATE_IDLE: onIdle();break;
         
      }
      
      
      lastSate = state;
   }
   
   @Override
   public void onOffHook() {
      
      if (!setOffHookTime(time)) return;
      
      //Timber.d("Telefon açıldı");
      
      if (lastSate == TelephonyManager.CALL_STATE_IDLE) {
         
         //outgoing call
         isIncomingCall = false;
        // Timber.d("Giden arama : %s", number);
         callListener.onOutgoingCallStart(time, number);
      }
      else{
      
         //incomming
         isIncomingCall = true;
         //Timber.d("Gelen arama cevaplandı : %s", number);
         callListener.onIncommingCallAnswer(callStartTime, time, number);
      }
   }
   
   @Override
   public void onRinging() {
      
      isIncomingCall = true;
      callStartTime = time;
      
      Timber.d("Telefon çalıyor : %s", number);
      
   }
   
   @Override
   public void onIdle() {
   
      Timber.d("Telefon kapandı");
      
      if (lastSate == TelephonyManager.CALL_STATE_RINGING) {
      
         //missed call or rejected
         Timber.d("Cevapsız Çağrı : %s", number);
         callListener.onMissedCall(callStartTime, time, number);
      }
      else if (isIncomingCall) {
         
         //end in
         Timber.d("Gelen arama sona erdi : %s", number);
         callListener.onIncommingCallEnd(callStartTime, offHookTime, time, number);
      }
      else{
         
         //end out
         Timber.d("Giden arama sona erdi : %s", number);
         callListener.onOutgoingCallEnd(offHookTime, time, number);
      }
   }
   
   private static boolean setOffHookTime(long offHookTime) {
      
      if (offHookTime == 0L) {
   
         NLPhoneStateListener.offHookTime = offHookTime;
         return true;
      }
      
      long dif = offHookTime - NLPhoneStateListener.offHookTime;
      
      Timber.w("İki offHook arasındaki zaman farkı : %dms  \u2605", dif);
      
      if (dif < 1000) {
         
         Timber.w("Bu fark beklenenden çok az. Görev bırakıldı");
         return false;
      }
   
      NLPhoneStateListener.offHookTime = offHookTime;
      return true;
   }
}
