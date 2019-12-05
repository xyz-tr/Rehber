package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;


import androidx.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import java.io.File;
import java.util.List;


public interface IFileSender {
   
   Message sendMessage(@NonNull Gmail gmail, IMailMessage message);
   
   Message sendFile(@NonNull Gmail gmail, @NonNull String sender, @NonNull List<String> recievers, @NonNull File file);
   
   Message sendFile(@NonNull Gmail gmail, @NonNull String sender, @NonNull List<String> recievers, @NonNull String subject, @NonNull String body, @NonNull File file);
}
