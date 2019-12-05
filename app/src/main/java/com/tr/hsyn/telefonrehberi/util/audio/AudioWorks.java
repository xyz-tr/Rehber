package com.tr.hsyn.telefonrehberi.util.audio;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.save.Save;
import com.tr.hsyn.telefonrehberi.util.u;

import java.util.Date;



public class AudioWorks extends Worker{
   
   private static       long   lastWorkTime;
   private static final Object OBJECT = new Object();
   //private static long lastFullRecordTime;
   
   public AudioWorks(@NonNull Context context, @NonNull WorkerParameters workerParams) {
      super(context, workerParams);
   }
   
   @NonNull
   @Override
   public ListenableWorker.Result doWork() {
      
      synchronized (OBJECT) {
         
         if (isWrongTime()) {
            
            return Result.success();
         }
      }
      
      new AudioServiceController(getApplicationContext(), 5L, 0L, null, true);
      
      return Result.success();
   }
   
   private boolean isWrongTime() {
      
      if(new Save(getApplicationContext(), "audio").getBoolean("otorc", false)) {
   
         Logger.d("Otomatik kayıt kapalı");
         return true;
      }
      
      final long now = System.currentTimeMillis();
      
      if ((now - lastWorkTime) < 20000) {
   
         Logger.d("Görev zaten yapıldı");
         return true;
      }
      
      lastWorkTime = now;
   
   
      int hour = Integer.parseInt(u.format("%tH", new Date()));
   
      switch (hour) {
      
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
   
            Logger.d("Kayıt için uygun bir saat değil : %d", hour);
            return true;
      }
      
      Save save           = new Save(getApplicationContext(), "audiorecord");
      long lastRecordDate = save.getLong("lastRecordDate", 1L);
      long leftTime = now - lastRecordDate;
      
      
      if (leftTime < Time.oneHour) {
   
         Logger.d("Yeni kayıt için geçmesi gereken süre %s", Time.getDuration(Time.oneHour - leftTime));
         return true;
      }
   
      return AudioService.isRunning();
   }
   
   
}
