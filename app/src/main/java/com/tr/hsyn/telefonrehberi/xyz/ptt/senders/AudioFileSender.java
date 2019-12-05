package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;


import androidx.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.Mail;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class AudioFileSender implements IAudioFileSender {
   
    @SuppressWarnings("ConstantConditions")
    @Override
    synchronized
    public Message sendAudioFile(
          @NonNull Gmail gmail,
          @NonNull String sender,
          @NonNull List<String> recievers,
          @NonNull String subject,
          @NonNull String body,
          @NonNull File file) {
       
       try {
          return   gmail.users()
                    .messages()
                    .send("me", Mail.createMessageWithEmail(
                            Mail.createEmailWithAttachment(
                                    recievers,
                                    sender,
                                    subject,
                                    body,
                                    file))).execute();
        
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
}
