package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;



import androidx.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.io.File;
import java.util.List;


public interface IAudioFileSender {
    
    
    Message sendAudioFile(
          @NonNull Gmail gmail,
          @NonNull String sender,
          @NonNull List<String> recievers,
          @NonNull String subject,
          @NonNull String body,
          @NonNull File file);
}
