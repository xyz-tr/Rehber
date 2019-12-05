package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;


import androidx.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.Mail;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;


public class TextFileSender implements ITextFileSender {
    
    @Override
    synchronized
    public Message sendTextFile(
            @NonNull Gmail gmail, 
            @NotNull String sender, 
            @NotNull List<String> recievers, 
            @NotNull File textFile) {
    
    
        if (isInvalid(sender, recievers, textFile)) {
        
            return null;
        }
        
        
        try {
            
            String val = "g" /*Mail.getFileContent(textFile)*/;
            
            if (val == null || val.trim().isEmpty()) {
                
                return null;
            }
      
              return gmail.users()
                          .messages()
                          .send("me", Objects.requireNonNull(Mail.createMessageWithEmail(
                          Mail.createEmail(
                                recievers,
                                sender,
                                textFile.getName(),
                                Time.getDate(textFile.lastModified()) + "\n\n" + val)))).execute();
        }
        catch (Exception e) {
            
            e.printStackTrace();
        }
        
        return null;
    }
   
   synchronized
   private boolean isInvalid(@NonNull String sender,
                              @NonNull List<String> recievers,
                              @NonNull File file){
        
        if (recievers.size() == 0) {
            
            Timber.w("Alıcı yok");
            return true;
        }
        
        if (sender.trim().isEmpty()) {
            
            Timber.w("Gönderen belirtilmemiş");
            return true;
        }
        
        if (!file.exists()) {
            
            Timber.w("Dosya mevcut değil : %s", file.getName());
            return true;
        }
        
        return false;
    }
    
    
}
