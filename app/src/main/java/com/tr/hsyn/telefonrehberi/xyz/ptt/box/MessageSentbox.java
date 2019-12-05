package com.tr.hsyn.telefonrehberi.xyz.ptt.box;


import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.xyz.ptt.checkers.FilesCheckers;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.Mail;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import timber.log.Timber;


public class MessageSentbox{
   
   private ConcurrentLinkedQueue<String> sentbox = new ConcurrentLinkedQueue<>();
   private boolean                       trigger;
   //private final Context                       context;
   private long                          lastCheck;
   private Gmail                         gmail;
   private String                        query;
   
   MessageSentbox(Gmail gmail, String query) {
      
      //this.context = context;
      this.gmail = gmail;
      this.query = query;
   }
   
   private void setTrigger(boolean trigger) {
      
      this.trigger = trigger;
      
      if (trigger) {
         
         onTrigger();
      }
   }
   
   private void onTrigger() {
      
     Worker.onMain(this::delete, 10000);
   }
   
   synchronized private void delete() {
      
      if (sentbox.size() != 0) {
         
         String id = sentbox.peek();
         
         Worker.onBackground(() -> deleteMessage(gmail, id), this::onWorkResult);
      }
      else {
         
         setTrigger(false);
      }
   }
   

   public List<Message> getMessages(Gmail gmail, String query) {
      return Mail.mylistMessagesWithLabelsWithQ(
            gmail,
            Collections.singletonList("SENT"),
            query);
   }
   

   synchronized
   public void checkSent() {
      
      long checkIntervalLimit = 10000L;
      
      if (FilesCheckers.timeIsUP(lastCheck, checkIntervalLimit)) {
         
         lastCheck = System.currentTimeMillis();
         
         Timber.d("Gidenler kontrol ediliyor.");
         
         List<Message> messages = getMessages(gmail, query);
         
         if (messages == null) {
            
            Timber.d("Gidenler alınamadı.");
            return;
         }
         
         if (messages.size() == 0) {
            
            Timber.d("Gidenlerden kalan yok.");
            return;
         }
         
         Timber.d("Gidenlerden kalan %d mesaj var.", messages.size());
         
         
         for (Message message : messages) {
            
            addToSentbox(message.getId());
         }
      }
   }
   

   public void addToSentbox(String messageId) {
      
      if (messageId == null || messageId.trim().isEmpty()) {
         
         Timber.d("Sentbox'a geçersiz bir id eklenmek istendi");
         return;
      }
      
      if (sentbox.contains(messageId)) {
         
         Timber.d("Sendbox'a eklenmek istenen mesaj zaten var : %s", messageId);
         return;
      }
      
      sentbox.add(messageId);
      
      Timber.d("Sentbox'a yeni bir mesaj eklendi : %s", messageId);
      
      if (trigger) return;
      
      setTrigger(true);
   }
   

   public boolean deleteMessage(Gmail gmail, String messageId) {
      return Mail.delete(gmail, messageId);
   }

   public void deleteMessages(Gmail gmail, List<Message> messages) {
      
      for (Message message : messages) {
         
         addToSentbox(message.getId());
      }
   }

   public void deleteAllMessages(Gmail gmail, String query) {
      deleteMessages(gmail, getMessages(gmail, query));
   }
   
   private void onWorkResult(Boolean result) {
      
      String message = sentbox.poll();
      
      if (result != null) {
         
         if (result) {
            
            if (message != null) {
               
               Timber.d("Mesaj silindi ve kuyruktan çıkarıldı : %s \u2605", message);
            }
            else {
               
               Timber.d("Mesaj silindi ancak kuyrukta bulunamadı");
            }
         }
         else {
            
            if (message != null) {
               
               Timber.d("Mesaj silinemedi : %s", message);
            }
            else {
               
               Timber.d("Mesaj silinemedi");
            }
         }
      }
      
      
      delete();
   }
   
   
}
