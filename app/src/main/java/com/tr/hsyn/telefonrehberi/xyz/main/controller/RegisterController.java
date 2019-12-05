package com.tr.hsyn.telefonrehberi.xyz.main.controller;

import android.app.Activity;
import android.content.Intent;

import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.main.activity.SignActivity;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.NLService;

import io.paperdb.Paper;
import lombok.val;
import timber.log.Timber;



public class RegisterController{
   
   public RegisterController(Activity activity){
      
      this.activity = activity;
   }
   
   private final Activity activity;
   
   public boolean isSign(){
      
      String from = SignActivity.getFrom();
      
      return from != null && Phone.isValidEmailAddress(from) && Phone.isServiceRunning(activity, NLService.class);
   }
   
   public boolean isRegistered(){
      
      val sign           = Paper.book("main").read("sign", false);
      val serviceRunning = Phone.isServiceRunning(activity, NLService.class);
      val from           = SignActivity.getFrom();
      Runnable stopService = () -> {
         
         val isStopped = activity.stopService(
               new Intent(activity, NLService.class));
         
         
         if(isStopped){
   
            Timber.d("Servise durduruldu");
         }
         else{
   
            Timber.w("Servise durdurulamadı");
         }
      };
      
      if(from == null){
         
         if(sign){
   
            Timber.w("Daha önce bir giriş kaydedilmiş ancak şuan giriş yapan kişi bilinmiyor");
            
            Paper.book("main").write("sign", false);
            
            if(serviceRunning){
   
               Timber.w("Giriş yapan kişi bilinmiyor " +
                       "ancak bildirim servisi aktif. " +
                       "Servis durduralacak");
               
               
               stopService.run();
            }
         }
         else{
            
            if(serviceRunning){
   
               Timber.w("Daha önce yapılan giriş silinmiş " +
                       "ancak bildirim servisi çalışıyor. " +
                       "Servis durdurulacak");
               
               
               stopService.run();
            }
            else{
   
               Timber.d("Daha önce giriş yapılmadı");
            }
         }
         
         return false;
      }
      
      if(!Phone.isValidEmailAddress(from)){
   
         Timber.w("Email adresi geçerli değil : %s", from);
         
         if(sign){
            
            Paper.book("main").read("sign", false);
            
            if(serviceRunning){
   
               Timber.w("Daha önce yapılan giriş değiştirilmiş " +
                       "ve bildirim servisi kapatılmamış. " +
                       "Servis kapatılacak");
               
               stopService.run();
            }
            else{
   
               Timber.d("Giriş değeri değiştirildi");
            }
         }
         else{
   
            Timber.d("Daha önce yapılmış bir giriş görünmüyor");
            
            if(serviceRunning){
   
               Timber.w("Ancak bildirim servisi açık Servis kapatılacak");
               
               stopService.run();
            }
            else{
   
               Timber.d("Bildirim servisi de devre dışı");
            }
         }
         
         return false;
      }
      
      if(!serviceRunning){
         
         String key   = "sign_notification_count";
         int    count = Paper.book().read(key, 0);
         
         if(sign){
   
            Timber.w("Daha önce giriş yapılmış ancak bildirim servisi devre dışı bırakılmış");
            
            if(count < 3){
               
               Paper.book().write(key, ++count);
   
               activity.runOnUiThread(() -> {
                  
                  com.tr.hsyn.telefonrehberi.util.ui.snack.Message.builder()
                                                                  .message("Daha önce giriş yapılmış ancak bildirim servisi devre dışı bırakılmış")
                                                                  .duration(5000L)
                                                                  .delay(1001L)
                                                                  .type(Show.WARN)
                                                                  .build()
                                                                  .showOn(activity);
                  
                  
                  com.tr.hsyn.telefonrehberi.util.ui.snack.Message.builder()
                                                                  .message("Bildirim Servisini aç")
                                                                  .actionMessage("AÇ")
                                                                  .duration(5000L)
                                                                  .delay(1500L)
                                                                  .type(Show.WARN)
                                                                  .clickListener(v -> Phone.openNotificationAccessSetting(activity))
                                                                  .build()
                                                                  .showOn(activity);
                  
               });
            }
            else{
   
               Timber.d("Bildirim servisi konusunda kullanıcı 3 kez uyarıldı. Bundan sonra uyarı olmayacak");
            }
         }
         else{
   
            Timber.w("Bildirim servisi devre dışı");
            
            if(count < 3){
               
               Paper.book().write(key, ++count);
   
               activity.runOnUiThread(() -> {
                  
                  com.tr.hsyn.telefonrehberi.util.ui.snack.Message.builder()
                                                                  .message("Bildirim servisi devre dışı")
                                                                  .delay(1010)
                                                                  .duration(3010)
                                                                  .build()
                                                                  .showOn(activity);
               });
            }
            else{
   
               Timber.d("Bildirim servisi konusunda kullanıcı 3 kez uyarıldı. Bundan sonra uyarı olmayacak");
            }
         }
      }
      
      return serviceRunning;
   }
}
