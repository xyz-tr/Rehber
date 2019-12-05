package com.tr.hsyn.telefonrehberi.xyz.call.main.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.BinaryHandler;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.RandomCallsActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.CallLogFiltered;
import com.tr.hsyn.telefonrehberi.xyz.call.main.random.CallGenerator;
import com.tr.hsyn.telefonrehberi.xyz.call.main.random.GeneratorService;
import com.tr.hsyn.telefonrehberi.xyz.call.main.random.ICallGenerator;
import com.tr.hsyn.telefonrehberi.xyz.call.main.random.listener.GeneratorOperationListener;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.ISimpleContact;

import java.util.ArrayList;
import java.util.List;

import lombok.val;
import timber.log.Timber;


public class RandomCallsService extends Service implements GeneratorService{
   
   //@Inject             CallLogDatabaseOperator          callLog;
   private             ICallGenerator                   callGenerator;
   //private static      int                               progress;
   private             BinaryHandler<Call>              binaryHandler;
   private             Worker<Call>                     workerThread;
   private             NotificationManager              notificationManager;
   private             String                           CHANNEL_ID              = "8112044796";
   private             List<ISimpleContact>             contactSearchModels;
   private             Intent                           stopIntent;
   private             Intent                           openActivityIntent;
   private static      boolean                          running;
   private static      boolean                          binaryMode;
   private static      GeneratorOperationListener<Call> activityGenerationListener;
   private static      RandomCallsService               SERVICE;
   private static      Arguments                        arguments;
   private static      int                              generatedCallCounter;
   private final       int                              NOTIFICATION_ID         = 47968521;
   public static final int                              START                   = 0;
   public static final int                              STOP                    = 1;
   public static final String                           COMMAND                 = "command";
   public static final String                           EXTRA_TYPES             = "types";
   public static final String                           EXTRA_COUNT             = "count";
   public static final String                           EXTRA_SAVE_SYSTEM       = "save_system";
   public static final String                           EXTRA_START_DATE        = "startdate";
   public static final String                           EXTRA_END_DATE          = "enddate";
   public static final String                           EXTRA_RINGTONE_DURATION = "ringtone";
   public static final String                           EXTRA_SHARABLE          = "share";
   
   @Override
   public void onCreate(){
      
      super.onCreate();
      
      //inject((AppRehber) getApplication());
      SERVICE = this;
      
      stopIntent = new Intent(this, RandomCallsService.class);
      stopIntent.putExtra(COMMAND, STOP);
      
      openActivityIntent = new Intent(this, RandomCallsActivity.class);
      //openActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   }
   
   @Override
   public int onStartCommand(@Nullable Intent intent, int flags, int startId){
      
      arguments = new Arguments(intent);
      
      if(arguments.isOkey){
         
         running = true;
         startLonlyMode();
      }
      
      return Service.START_NOT_STICKY;
   }
   
   @Override
   public void onDestroy(){
   
      RandomCallsService.activityGenerationListener = null;
      super.onDestroy();
      Timber.d("Servis tamamen sonlandı");
      
   }
   
   @Nullable
   @Override
   public IBinder onBind(Intent intent){
      
      return null;
   }
   
   /**
    * Üretilen her arama kaydı buraya düşecek.
    * Bu metot otomatik olarak çağrılıyor. Bu bir <b>callback</b> metodu.
    *
    * @param result   {@code PhoneLogCall}. Üretilen arama kaydı.
    * @param progress Üretilen bu arama kaydının o ana kadar üretilen kaçıncı kayıt olduğunu bildirir.
    * @param total    Üretilmesi amaçlanan toplam arama kaydı sayısı.
    */
   @Override
   public void onGenerate(Call result, int progress, int total){
      
      if(binaryMode){
         
         onWorkResult(result);
         return;
      }
      
      NotificationCompat.Builder builder = getBuilder();
      
      builder.setContentText(u.format("%d / %d", progress, arguments.count));
      builder.setProgress(total, progress, false);
      
      Timber.d("%d / %d", progress, arguments.count);
      
      notificationManager.notify(NOTIFICATION_ID, builder.build());
   }
   
   /**
    * Activity servisten ayrıldığında üretim bildirim alanında devam edecek.
    * Üretimin gidişatı bildirim ile takip edilecek.
    * Bildirime dokunulduğuna activity yeniden açılır.
    * Ayrıca <b>durdur</b> butonuna basarak direk bildirim üzerinden üretim tamamen durdurulabilecek.
    *
    * @return NotificationCompat.Builder
    */
   private NotificationCompat.Builder getBuilder(){
      
      return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(new NotificationCompat.BigTextStyle())
            .setContentTitle("Rastgele Arama Kaydı Üretiliyor")
            .setSmallIcon(R.mipmap.call)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(arguments.count, generatedCallCounter, false)
            .setContentIntent(PendingIntent.getActivity(this, 2, openActivityIntent, 0))
            .setAutoCancel(true)
            .setColor(Color.YELLOW)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .addAction(R.drawable.delete_icon, "Durdur", PendingIntent.getService(this, 1, stopIntent, 0));
   }
   
   /**
    * Eğer activity servisi bağlı ise üretilen her kayıt buraya düşecek.
    * Bu kodlar ana thread üzerinde çalışacak.
    *
    * @param result {@code PhoneLogCall}. Üretilmiş arama kaydı.
    */
   private void onWorkResult(Call result){
      
      if(binaryMode){
         
         if(isFinish()){
            
            Timber.d("generatedCallCounter : %d, total : %d", generatedCallCounter, arguments.count);
   
            if(activityGenerationListener != null){
   
               activityGenerationListener.onGenerationComplete();
            }
            
            return;
         }
   
         if(activityGenerationListener != null){
   
            activityGenerationListener.onGenerate(result, generatedCallCounter, arguments.count);
         }
      }
   }
   
   /**
    * Üretimin bitip bitmediğini bildirecek.
    *
    * @return Eğer üretim tamamlanmış ise {@code true}, aksi halde {@code false} döndürür.
    */
   private boolean isFinish(){
      
      return generatedCallCounter >= arguments.count;
   }
   
   /**
    * Activity servise bağlı olmadan üretimi tamamladığında burası otomatik olarak çağrılır.
    */
   @Override
   public void onGenerationComplete(){
      
      //burası ikili modun dışında çalışacak
      stopService();
      CallLogFiltered.setNeedRefresh(true);
   }
   
   /**
    * Servisi tamamen sonlandırır.
    * Activity bağlı olsa da olmasa da servis {@code onDestroy} metoduna yönlendirir.
    */
   public void stopService(){
      
      if(!binaryMode){
         
         stopGeneration();
      }
      else{
         
         if(!isFinish()){
            
            activityGenerationListener.onBreak();
         }
      }
      
      quitBinaryHandler();
      //progress = 0;
      running = false;
      
      stopForeground(true);
      stopSelf();
   }
   
   /**
    * Activity servisten ayrıldığında activity için çalışan arka plan işlemini sonlandırır.
    */
   private void quitBinaryHandler(){
      
      if(binaryHandler != null){
         
         binaryHandler.quit();
      }
   }
   
   /**
    * Activity bağlı değilken yapılan üretimi sonlandırır.
    */
   private void stopGeneration(){
      
      if(workerThread != null){
         
         if(workerThread.isAlive()){
            
            workerThread.stop();
         }
      }
   }
   
   /**
    * Activity servise bağlı olsa da olmasa da üretim durdurulduğunda bu metot çağrılacak.
    * Ancak activity bağlı ise durdurulduğuna dair bilgi verecek.
    */
   @Override
   public void onBreak(){
      
      if(binaryMode){
         
         activityGenerationListener.onBreak();
      }
      
      CallLogFiltered.setNeedRefresh(true);
   }
   
   /**
    * Activity servisen ayrıldığında arka plan görevini sonlanlandıracak ve üretim tamamlanmamışsa işi servise bırakacak
    */
   public void stopBinaryMode(){
      
      quitBinaryHandler();
      binaryMode                 = false;
      
      
      if(!isFinish()){
         
         startLonlyMode();
      }
      else{
         
         stopService();
      }
   }
   
   /**
    * Activity ayrılıp tekrar bağlandığında yada servis sonlandırıldığında çağrılacak
    */
   public void stopLonlyMode(){
      
      stopGeneration();
      
      if(!isFinish()){
         
         if(binaryMode){
            
            startBinaryMode();
         }
         else{
            
            stopService();
         }
      }
   }
   
   /**
    * Servisin activity ile birlikte çalışmasını başlatacak.
    */
   private void startBinaryMode(){
      
      if(activityGenerationListener == null){
         
         Timber.d("ServiceGenerationListener tanımlı değil");
         return;
      }
      
      Timber.d("BinaryMode starting");
      
      //Servis activity ile çalışırken arka plan görevini BinaryHandler sınıfı üstleniyor
      binaryHandler = new BinaryHandler<>(this::backgroundWorker, this::proccessOnBackground, this::onWorkResult);
      binaryHandler.start();
      
      //Activity kapanıp geri açılmışsa servis tekrar arka plana dönecek
      stopForeground(true);
      
      Timber.d("Servis bağlanıyor");
      //herşey hazır, bağlanabiliriz
      activityGenerationListener.onConnect();
   }
   
   /**
    * Rastgele bir arama kaydı üretip döndürecek
    *
    * @return {@code PhoneLogCall}. Üretilen rastgele arama kaydı.
    */
   private Call backgroundWorker(){
      
      Call phoneCall = callGenerator.getCall();
      
      Timber.d(String.valueOf(phoneCall.getDate()));
      
      return phoneCall;
   }
   
   /**
    * Üretilen arama kaydı kaydı ile arka planda yapılacak işlemleri tanımlar.
    * Bu metot otomatik olarak çağrılır ve arka planda çalışır.
    *
    * @param result {@code PhoneLogCall}. Üretilen kayıt.
    */
   
   private void proccessOnBackground(Call result){
      
      generatedCallCounter++;
      
      saveToDatabase(result);
      
      if(arguments.isSystemSave){
         
         saveToSystemDatabase(result);
      }
   }
   
   /**
    * Arama kaydını sistem arama kayıtlarına kaydeder.
    *
    * @param phoneCall {@code PhoneLogCall}. Kaydedilecek arama kaydı.
    */
   private void saveToSystemDatabase(Call phoneCall){
      
      if(!CallStory.addCallIntoSystem(getContentResolver(), phoneCall)){
         
         Timber.w("Arama kaydı sisteme eklenemedi : %d", phoneCall.getDate());
      }
   }
   
   /**
    * Arama kaydını uygulamanın arama kayıtları veri tabanına kaydeder.
    *
    * @param phoneCall {@code PhoneLogCall}. Kaydedilecek arama kaydı.
    */
   private void saveToDatabase(Call phoneCall){
      
      //callLog.add(phoneCall);
   }
   
   /**
    * Activity bu metot aracılığı ile yeni bir arama kaydı talep eder.
    * Talep arka plan işlemine bildirir.
    * Bu durumda {@code binaryMode} değeri {@code true} ve {@code binaryHandler} ise {@code null} olmamalı.
    */
   public void requestValue(){
      
      if(binaryMode && binaryHandler != null){
         
         binaryHandler.requestWork();
      }
      else{
         
         Timber.d("BinaryHandler tanımlı değil");
      }
   }
   
   /**
    * Activity servisten ayrılmışsa ve üretim tamamlanmamışsa servis kendi başına görevi tamamlayacak
    */
   private void startLonlyMode(){
      
      if(contactSearchModels != null){
         
         onContactsReady(contactSearchModels);
         return;
      }
      
      Worker.onBackground(
            this::getContacts,
            this::onContactsReady
      );
   }
   
   /**
    * @param contactSearchModels Üretimin yapılacağı liste.
    */
   private void onContactsReady(List<ISimpleContact> contactSearchModels){
      
      this.contactSearchModels = contactSearchModels;
      callGenerator            = new CallGenerator(contactSearchModels, arguments.types, arguments.startDate, arguments.endDate, arguments.isRingtone, arguments.isShare);
      
      if(binaryMode){
         
         startBinaryMode();
         return;
      }
   
      activityGenerationListener = null;
      startFore();
      generateCalls();
   }
   
   /**
    * Activity'den bağımsız olarak arama kaydı üretimini başlatacak.
    * Yani activity servisten ayrıldığında bu metot çalışacak.
    */
   private void generateCalls(){
      
      workerThread = new Worker<Call>()
            .generate(arguments.count, generatedCallCounter, 200L)
            .thisIsMyWork(this::backgroundWorker)
            .handleOnForeground(this::onWorkResult)
            .listenGeneration(this)
            .proccessOnBackground(this::proccessOnBackground)
            .onGenerationComplete(this)
            .onBreak(this)
            .start();
   }
   
   /**
    * Activity servisten ayrıldığında servisi ön planda çalışmak üzere ayarlar.
    * Üretimin bildirim alanında devam etmesi için.
    */
   private void startFore(){
      
      if(notificationManager == null){
         
         notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         
         if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            
            String              channelName = "RandomCalls Channel";
            NotificationChannel chan        = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            
            chan.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationManager.createNotificationChannel(chan);
         }
      }
      
      startForeground(NOTIFICATION_ID, getBuilder().build());
   }
   
   /**
    * @return Servis çalışıyorsa {@code true}
    */
   public static boolean isRunning(){
      
      return running;
   }
   
   /**
    * @return Üretilmesi amaçlanan toplam arama kaydı sayısı
    */
   public static int getCount(){
      
      return arguments.count;
   }
   
   /**
    * @return O ana kadar üretilmiş arama kaydı sayısı
    */
   public static int getProgress(){
      
      return generatedCallCounter;
   }
   
   /**
    * @return RandomCallsService
    */
   public static RandomCallsService getService(){
      
      return SERVICE;
   }
   
   /**
    * @return Üretimin yapılacağı kişiler listesi
    */
   private List<ISimpleContact> getContacts(){
      
      List<IMainContact> contacts = Contacts.getMainContacts(getApplicationContext());
      
      List<String> idList = Stream.of(contacts).map(IMainContact::getContactId).toList();
      
      val list = Contacts.getContactsForSearch(this, idList);
      
      if(list == null) return new ArrayList<>(0);
      
      return list;
   }
   
   /**
    * @param activityGenerationListener Üretimi takip edecek olan organizasyon
    */
   public static void setActivityGenerationListener(GeneratorOperationListener<Call> activityGenerationListener){
      
      RandomCallsService.activityGenerationListener = activityGenerationListener;
   }
   
   /**
    * @param binaryMode Eğer {@code true} ise servis acivity ile birlikte çalışacak demektir
    */
   public static void setBinaryMode(boolean binaryMode){
      
      RandomCallsService.binaryMode = binaryMode;
   }
   
   /**
    * Servisin ihtiyaç duyduğu argümanları taşıyacak
    */
   private class Arguments{
      
      List<Integer> types;
      boolean       isSystemSave;
      boolean       isOkey = true;
      int           count;
      long          startDate;
      long          endDate;
      boolean       isRingtone;
      boolean       isShare;
      
      
      Arguments(Intent intent){
         
         if(intent == null){
            
            Timber.d("intent = null. Gerekli bilgiler alınamıyor.");
            isOkey = false;
            return;
         }
         
         if(!intent.hasExtra(COMMAND)){
            
            Timber.d("There is no command");
            isOkey = false;
            
            if(running){
               
               return;
            }
            
            stopService();
            return;
         }
         
         
         int command = intent.getIntExtra(COMMAND, 0);
         
         if(command == START){
            
            if(running){
               
               Timber.d("Service already running");
               isOkey = false;
               return;
            }
            
            if(!intent.hasExtra(EXTRA_TYPES)){
               
               Timber.d("Types gerekli ama yok");
               isOkey = false;
               stopService();
               return;
            }
            
            types = intent.getIntegerArrayListExtra(EXTRA_TYPES);
            
            if(types == null){
               
               Timber.d("Gönderilen 'types' null");
               isOkey = false;
               stopService();
               return;
            }
            
            if(!intent.hasExtra(EXTRA_COUNT)){
               
               Timber.d("count belirtilmemiş");
               isOkey = false;
               stopService();
               return;
            }
            
            count = intent.getIntExtra(EXTRA_COUNT, 0);
            
            startDate            = intent.getLongExtra(EXTRA_START_DATE, 0L);
            endDate              = intent.getLongExtra(EXTRA_END_DATE, 0L);
            isRingtone           = intent.getBooleanExtra(EXTRA_RINGTONE_DURATION, false);
            isSystemSave         = intent.getBooleanExtra(EXTRA_SAVE_SYSTEM, false);
            isShare              = intent.getBooleanExtra(EXTRA_SHARABLE, false);
            generatedCallCounter = 0;
         }
         else if(command == STOP){
            isOkey = false;
            
            CallLogFiltered.setNeedRefresh(true);
            stopService();
         }
         else{
            isOkey = false;
            Timber.d("Wrong command : %d", command);
            stopService();
         }
      }
      
   }
}
