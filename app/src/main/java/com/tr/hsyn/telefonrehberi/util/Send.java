package com.tr.hsyn.telefonrehberi.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;

import com.tr.hsyn.telefonrehberi.BuildConfig;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.messages.Message;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.GlobalMessageController;

import java.util.Random;

import lombok.val;
import timber.log.Timber;


public class Send{
   
   public static void globalMessage(String message){
      
      GlobalMessageController.addMessage(new Message(message));
   }
   
   public static void log(String subject, String message, Object... args){
      
      if(BuildConfig.DEBUG){
   
         Timber.d("%s:%s", subject, Stringx.format(message, args));
      }
   }
   
   public static void log(String message, Object... args){
      
      if(BuildConfig.DEBUG){
   
         Timber.d(Stringx.format(message, args));
      }
   }
   
   public static void notification(Context context, String title, String text, Object... args){
      
      String CHANNEL_ID   = "12.04.1981";
      String CHANNEL_NAME = "xyz.tr.hsyn.log.channel";
      
      NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      
      if(mNotificationManager == null){ return; }
      
      
      NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(context, CHANNEL_ID)
                  .setContentText(Stringx.format(text, args))
                  .setStyle(new NotificationCompat.BigTextStyle().bigText(Stringx.format(text, args)))
                  .setTicker(CHANNEL_NAME)
                  .setAutoCancel(true)
                  .setOngoing(false)
                  .setSmallIcon(R.mipmap.call)
                  .setLargeIcon(ResourceUtil.getBitmap(context, R.mipmap.call))
                  .setContentTitle(title)
                  .setColor(Color.YELLOW)
                  .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                  .setLights(Color.YELLOW, 300, 5000)
                  .setVibrate(new long[]{ 5L});
        
        /*
        mBuilder.setDefaults(
                Notification.DEFAULT_SOUND
                             );
        */
      
      
      if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
         
         NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
         notificationChannel.enableLights(true);
         notificationChannel.enableVibration(true);
         mBuilder.setChannelId(CHANNEL_ID);
         mNotificationManager.createNotificationChannel(notificationChannel);
      }
      
      val n = mBuilder.build();
      
      mNotificationManager.notify(new Random().nextInt(), n);
   }
   
   public static void broadcast(Context context, String action){
      
      context.sendBroadcast(new Intent(action));
   }
   
   public static String getFunctionName(){
      
      StackTraceElement[] sts = Thread.currentThread().getStackTrace();
      
      for(StackTraceElement st : sts){
         
         if(st.isNativeMethod()){
            continue;
         }
         
         if(st.getClassName().equals(Thread.class.getName())){
            continue;
         }
         
         return st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName();
      }
      
      return null;
   }
   
   
}
