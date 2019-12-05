package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.Mail;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;


public class TextSender implements ITextSender {
   
   @Override
   synchronized
   public Message sendText(Gmail gmail, String sender, List<String> recievers, String subject, String body) {
      
      if (subject == null || subject.trim().isEmpty()) {
   
         Timber.w("Geçersiz boş mesaj");
         return null;
      }
      
      try {
         
         return gmail
               .users()
               .messages()
               .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                     Mail.createEmail(
                           recievers,
                           sender,
                           subject,
                           body)))).execute();
         
         
      }
      catch (Exception e) {
         
         e.printStackTrace();
      }
      
      return null;
   }
   
   
   
}

