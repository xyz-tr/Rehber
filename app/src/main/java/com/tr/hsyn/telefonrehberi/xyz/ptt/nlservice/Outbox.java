package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventOutboxCheckRequest;
import com.tr.hsyn.telefonrehberi.util.phone.Files;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.ptt.file.IFileArchive;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.GmailService;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.FileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.IFileSender;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Setter;
import lombok.val;
import timber.log.Timber;


/**
 * <h1>Outbox</h1>
 * <p>
 * Giden kutusu.
 * Burası gönderilecek mesajların yönetildiği yer.
 * {@linkplain IPostMan} mesajları kaydettiğinde
 * burası görecek ve mesajları gönderecek.
 *
 * @author hsyn 2019-09-09 23:55:28
 */
@SuppressWarnings("unused")
final class Outbox extends IOutbox{
   
   private final Context context;
   
   /**
    * Google Gmail Api
    */
   private final Gmail gmail;
   
   /**
    * Dosyayı gönderecek olan gönderici
    */
   private final IFileSender fileSender = new FileSender();
   
   /**
    * Dosyaların ana kuyruğu
    */
   private final ConcurrentLinkedQueue<File> mainFileQueue = new ConcurrentLinkedQueue<>();
   
   /**
    * Üzerinde çalışılan dosyaların kuyruğu
    */
   private final ConcurrentLinkedQueue<String> workingFilesQueue = new ConcurrentLinkedQueue<>();
   
   /**
    * Dosyalar işleniyor mu?
    */
   private final AtomicBoolean working = new AtomicBoolean();
   
   private                  File            rootFile;
   @Setter private volatile NewFileListener newFileListener;
   
   /**
    * Gönderilen mesajları arşivle
    */
   private IFileArchive sentboxArchive;
   
   private boolean       stopSending;
   /**
    * Gönderilecek mesajların kaydedildiği klasörü dinleyecek.
    */
   @SuppressWarnings("FieldCanBeLocal")
   private IFileObserver fileObserver;
   
   /**
    * Kurucu.
    *
    * @param context app context
    */
   Outbox(Context context){
      
      this.context = context;
      gmail        = GmailService.getGmailService(context);
      EventBus.getDefault().register(this);
      sentboxArchive = IFileArchive.create(context, Files.getSentbox(context));
      setFileObserver();
      
   }
   
   /**
    * Klasör izleyiciyi set et ve izlemeyi başlat.
    */
   private void setFileObserver(){
      
      rootFile = Files.getOutbox(context);
      
      if(rootFile == null){
         
         return;
      }
      
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
         
         fileObserver = new FileObserver(rootFile, android.os.FileObserver.CREATE);
      }
      else{
         
         fileObserver = new FileObserver(rootFile.getAbsolutePath(), android.os.FileObserver.CREATE);
      }
      
      fileObserver.startWatching();
   }
   
   @Override
   protected void finalize() throws Throwable{
      
      try{
         
         EventBus.getDefault().unregister(this);
      }
      catch(Exception ignored){}
      
      super.finalize();
   }
   
   @Subscribe(threadMode = ThreadMode.ASYNC)
   void request(EventOutboxCheckRequest event){
      
      String requestor = Stringx.format("%s [%s] kontrol talebinde bulundu", event.getHeadrequestor(), OutboxHeadrequestor.getRequestorName(event.getHeadrequestor()));
      
      if(stopSending){
         
         Timber.d("%s, ancak kontrol kullanıcı tarafından durdurulmuş durumda.", requestor);
         return;
      }
      
      
      Timber.d("%s, ve talep kabul edildi", requestor);
      
      
      add(getOutFiles());
   }
   
   private void add(File[] files){
      
      for(File file : files){ add(file); }
   }
   
   @Override public void add(@NonNull final File file){
      
      if(mainFileQueue.contains(file)) return;
      
      mainFileQueue.add(file);
      
      if(!working.get()){
         
         working.set(true);
         work();
      }
   }
   
   private void work(){
      
      if(stopSending){
         
         working.set(false);
         return;
      }
      
      while(!mainFileQueue.isEmpty()){
         
         File file = mainFileQueue.poll();
         
         if(file == null) continue;
         
         if(workingFilesQueue.contains(file.getName())) continue;
         
         workingFilesQueue.add(file.getName());
         
         IMailMessage mailMessage = IMailMessage.createFromXml(file);
         
         if(mailMessage == null){
            
            Timber.w("MailMessage oluşturulamadı");
            continue;
         }
         
         sendMessage(mailMessage);
      }
      
      working.set(false);
   }
   
   @Override
   public void sendMessage(@NonNull IMailMessage message){
      
      message.setSentTime(System.currentTimeMillis());
      new MessageStateEvent(MessageState.SENDING, message);
      
      Worker.onBackground(
            () -> fileSender.sendMessage(gmail, message),
            "Outbox:Mesaj gönderme - " + message.getFile().getName())
            .whenComplete((r, t) -> sent(r, t, message));
   }
   
   public void sent(Message message, Throwable throwable, @NonNull IMailMessage mailMessage){
      
      if(throwable == null){
         
         if(message != null){
            
            mailMessage.setMailId(message.getId());
            onMessageDone(mailMessage);
         }
         else{
            
            //Eğer hata yoksa message nesnesi asla null gelmeyecek ama burası yine de kalacak
            onMessageFailed(mailMessage, null);
         }
      }
      else{
         
         onMessageFailed(mailMessage, throwable);
      }
   }
   
   /**
    * Mesaj gönderme işlemi başarılı olduysa.
    *
    * @param mailMessage mesaj
    */
   private void onMessageDone(IMailMessage mailMessage){
      
      new MessageStateEvent(MessageState.SENT, mailMessage);
      
      String fileName = mailMessage.getFile().getName();
      Timber.d("Mesaj gönderildi : %s", fileName);
      
      if(sentboxArchive.add(mailMessage)){
   
         Timber.d("Mesaj dosyası arşive kaydedildi : %s", fileName);
      }
      else{
   
         Timber.d("Mesaj dosyası arşive kaydedilemedi : %s", fileName);
      }
   }
   
   private void onMessageFailed(IMailMessage mailMessage, Throwable throwable){
      
      mailMessage.setSentTime(0L);
      new MessageStateEvent(MessageState.FAILED, mailMessage);
      
      Timber.w("Mail gönderilemedi");
      
      if(throwable != null){
         
         Timber.w(throwable.getMessage());
         
         if(throwable.getCause() != null){
            
            Timber.w(throwable.getCause().getMessage());
         }
      }
      
      int tryCount = mailMessage.getTryCount();
      
      if(tryCount > 5){
         
         Timber.w("Mesaj %d kez gönderilmek istendi ancak gönderme başarısız oldu. Mesaj dosyası yenilenerek tekrar denenecek : %s", tryCount, mailMessage.getId());
         
         File file = mailMessage.getFile();
         
         if(Files.deleteFile(file)){
            
            if(mailMessage.create(mailMessage).saveToXml(file)){
               
               Timber.d("Mesaj dosyası yeniden oluşturuldu : %s", file.getName());
            }
            else{
               
               Timber.w("Mesaj dosyası yeniden oluşturulamıyor : %s", file.getName());
            }
         }
         else{
            
            Timber.w("Mesaj dosyası yeniden oluşturulmak amacıyla silinmek istendi ancak silinemedi : %s", file.getName());
         }
      }
      else{
         
         mailMessage.setTryCount(++tryCount);
         mailMessage.saveToXml(mailMessage.getFile());
      }
   }
   
   /**
    * Outbox'daki dosyaları ver.
    *
    * @return mesaj dosyaları
    */
   @Override @NonNull public File[] getOutFiles(){
      
      val outbox = Files.getOutbox(context);
      
      if(outbox == null) return new File[0];
      
      val files = outbox.listFiles();
      
      if(files == null) return new File[0];
      
      return files;
   }
   
   @Override public void stopWatchingOutbox(){
      
      fileObserver.stopWatching();
   }
   
   @Override public void startWatchingOutbox(){
      
      fileObserver.startWatching();
   }
   
   @Override public boolean isStopSending(){
      
      return stopSending;
   }
   
   @Override public void setStopSending(boolean stopSending){
      
      this.stopSending = stopSending;
      
      if(stopSending) return;
      
      add(getOutFiles());
   }
   
   @Override public boolean isWatchingOutbox(){
      
      return fileObserver.isWatching();
   }
   
   private void onNewFile(String fileName){
      
      File file = new File(rootFile, fileName);
      
      if(newFileListener == null){
         
         setStopSending(false);
      }
      else{
         
         Worker.onMain(() -> newFileListener.onNewFile(file));
         
         if(!stopSending){
            
            add(getOutFiles());
         }
      }
   }
   
   
   /**
    * Gönderilecek olan mesajların kaydedileceği klasörü izleyecek.
    * İzleme sadece yeni oluşturulan dosyalar hakkında olacak.
    */
   private final class FileObserver extends IFileObserver{
      
      FileObserver(String path, int mask){
         
         super(path, mask);
      }
      
      @RequiresApi(api = Build.VERSION_CODES.Q) FileObserver(@NonNull File file, int mask){
         
         super(file, mask);
      }
      
      @Override
      void newFileCreated(String fileName){
         
         Worker.onBackground(() -> onNewFile(fileName), "FileObserver:Yeni oluşturulan dosyayı işleme gönderme");
      }
   }
   
}


