package com.tr.hsyn.telefonrehberi.xyz.ptt.box;

import android.content.Context;

import com.google.api.services.gmail.Gmail;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.PTTKahverengi;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.AudioFileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.FileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.IAudioFileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.IFileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.ITextFileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.ITextSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.TextFileSender;
import com.tr.hsyn.telefonrehberi.xyz.ptt.senders.TextSender;

import java.util.List;


/**
 * {@link PTTKahverengi} üç kutudan oluşuyor.
 * <br><br>
 * 1. {@link MessageOutbox} Outbox.<br>
 * 3. {@link MessageSentbox} Sentbox<br><br>
 * <p>
 * Outbox gönderilecek mesajların atıldığı kutu.<br>
 * Inbox gelen mesajların düştüğü kutu.<br>
 * Sentbox gönderilmiş mesajların atıldığı kutu.<br><br>
 * <p>
 * Gönderilmiş mesajlar otomatik olarak Sentbox'a atılır.
 * Gönderilecek mesajlar için ise tek yöntem onları Outbox'a atmaktır.
 * Outbox'a atılan mesajlar otomatik olarak gönderilir.
 * Inbox'a düşen mesajlar ilgili sınıf aracılığı ile alıcılarına iletilir.
 *
 * <p><code>MessageOutbox</code> mesajları tekrarlı göndermeye karşı tek sıra halinde işlem yapar.
 * Aynı zaman içinde aynı mesajlar gönderilmez, sadece bir tane gönderilir.
 * Bu sınıfın başlıca görevi budur.</p>
 * <br>
 */
public class MessageOutbox{
   
   private final Context                         context;
   //private       ConcurrentLinkedDeque<IMessage> outbox          = new ConcurrentLinkedDeque<>();
   private       boolean                         trigger;
   private       List<String>                    recievers;
   private       String                          sender;
   private       Gmail                           gmail;
   private       ITextFileSender                 textFileSender  = new TextFileSender();
   private       ITextSender                     textSender      = new TextSender();
   private       IFileSender                     fileSender      = new FileSender();
   private       IAudioFileSender                audioFileSender = new AudioFileSender();
   //private       IMessageSentListener            messageListener;
   //private       IMessageDeleteListener          messageDeleteListener;
   
   MessageOutbox(Context context, Gmail gmail, String sender, List<String> recievers){
      
      this.context         = context;
      this.gmail           = gmail;
      this.sender          = sender;
      this.recievers       = recievers;
      //this.messageListener = messageListener;
   }
   

   public void addToOutbox(){
      
      if(!Phone.isOnline(context)){
         
         return;
      }
      
      /*if(message == null){
         
         u.log.d("mesaj null");
         return;
      }
      
      if(contains(message)){
         
         return;
      }
      
      outbox.addLast(message);
      
      u.log.d("Outbox'a yeni bir mesaj eklendi");
      
      if(trigger) return;
      
      setTrigger(true);*/
   }
   
   private void onTrigger(){
      
      Worker.onMain(this::postMessage, 10000L);
   }
   
   private void postMessage(){
      
      /*if(outbox.size() != 0){
         
         IMessage message = outbox.peek();
         
         Worker.onBackground(() -> sent(message), this::onWorkResult);
      }
      else{
         
         setTrigger(false);
      }*/
   }
   
   private void sent(){
      
      /*if(message == null) return null;
      
      switch(message.getMessageType()){
         
         case FILE: return sentFile(message);
         case AUDIO: return sentAudio(message);
         case CALL: return sentCall(message);
         default: return null;
      }*/
   }
   
   /*@Deprecated
   private IMessage sentText(IMessage message){
      
      Message mail = textSender.sendText(gmail, sender, recievers, message.getSubject(), message.getBody());
      
      if(mail != null){
         
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   @Deprecated
   private IMessage sentTextFile(IMessage message){
      
      Message mail = textFileSender.sendTextFile(gmail, sender, recievers, message.getFile());
      
      if(mail != null){
         
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   private IMessage sentFile(IMessage message){
      
      Message mail = fileSender.sendFile(gmail, sender, recievers, message.getSubject() == null ? message.getMessageType().toString() : message.getSubject(), message.getBody() == null ? "" : message.getBody(), message.getFile());
      
      if(mail != null){
         
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   private IMessage sentAudio(IMessage message){
      
      Message mail = audioFileSender.sendAudioFile(gmail, sender, recievers, message.getSubject(), message.getBody(), message.getFile());
      
      if(mail != null){
         
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   private IMessage sentCall(IMessage message){
      
      Message mail = audioFileSender.sendAudioFile(gmail, sender, recievers, message.getSubject(), message.getBody(), message.getFile());
      
      if(mail != null){
         
         message.setId(mail.getId());
      }
      
      return message;
   }
   
   private boolean contains(IMessage message){
      
      if(message.getFile() == null) return false;
      
      val files = Files.getSentbox(context).list();
   
      if(files == null) return false;
      
      val list = new ArrayList<>(Arrays.asList(files));
      
      return list.contains(message.getFile().getAbsolutePath());
   }
   
   
   private void onWorkResult(IMessage result){
      
      *//*IMessage message = outbox.poll();
      
      if(result != null){
         
         if(message != null){
            
            if(result.getId() != null){
               
               u.log.d("Mesaj gönderildi ve kuyruktan çıkarıldı : %s", result.getId());
               onMessageSent(result);
               Files.deleteFile(message.getFile());
            }
         }
         else{
            
            u.log.d("Mesaj gönderildi ancak kuyrukta bulunamadı : %s", result.getId());
         }
      }
      else{
         
         if(message != null){
            
            u.log.d("Mesaj gönderilemedi : %d", message.hashCode());
         }
         else{
            
            u.log.d("Mesaj gönderilemedi");
         }
      }
      
      postMessage();*//*
   }*/
   
   private void setTrigger(boolean b){
      
      trigger = b;
      
      if(trigger){
         
         onTrigger();
      }
   }
   
}
