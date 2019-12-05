package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;


import androidx.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.Mail;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import lombok.val;
import timber.log.Timber;


public class FileSender implements IFileSender{
   
   
   @Override
   synchronized
   public Message sendMessage(@NonNull Gmail gmail, IMailMessage message){
      
      return sendFile(gmail, message.getSender(), message.getRecipients(), message.getSubject(), message.getBody(), message.getFile());
   }
   
   @Override
   synchronized
   public Message sendFile(
         @NonNull Gmail gmail,
         @NonNull String sender,
         @NonNull List<String> recievers,
         @NonNull File file){
      
      if(isInvalid(sender, recievers, file)){
         
         return null;
      }
      
      try{
         return gmail
               .users()
               .messages()
               .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                     Mail.createEmailWithAttachment(
                           recievers,
                           sender,
                           file.getName(),
                           Time.getDate(file.lastModified()),
                           file)))).execute();
         
      }
      catch(IOException e){
         e.printStackTrace();
      }
      
      
      return null;
   }
   
   @Override
   synchronized
   public Message sendFile(
         @NonNull Gmail gmail,
         @NotNull String sender,
         @NotNull List<String> recievers,
         @NotNull String subject,
         @NotNull String body,
         @NotNull File file){
      
      if(isInvalid(sender, recievers, file)){
         
         throw new RuntimeException("Mesaj geçersiz");
      }
      
      try{
         
         val mail = Mail.createMessageWithEmail(
               Mail.createEmailWithAttachment(
                     recievers,
                     sender,
                     subject,
                     body,
                     file));
         
         if(mail == null){
            
            throw new RuntimeException("Mesaj oluşturulamadı");
         }
         
         return gmail.users()
                     .messages()
                     .send(sender, mail).execute();
         
      }
      catch(IOException e){
         e.printStackTrace();
         Timber.w(e);
         
         throw new RuntimeException(e);
      }
   }
   
   private boolean isInvalid(@NonNull String sender,
                             @NonNull List<String> recievers,
                             @NonNull File file){
      
      if(recievers.size() == 0){
         
         Timber.w("Alıcı yok");
         return true;
      }
      
      if(sender.trim().isEmpty()){
         
         Timber.w("Gönderen belirtilmemiş");
         return true;
      }
      
      if(!file.exists()){
         
         Timber.w("Dosya mevcut değil : %s", file.getName());
         return true;
      }
      
      return false;
   }
   
}
