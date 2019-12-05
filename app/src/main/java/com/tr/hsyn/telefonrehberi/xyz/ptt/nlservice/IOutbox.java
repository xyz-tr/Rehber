package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;


import android.content.Context;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import java.io.File;


/**
 * <h1>IOutbox</h1>
 * 
 * Bu sınıf gönderilecek mesajları yönetecek.
 * 
 */
public abstract class IOutbox{
   
   static IOutbox create(Context context){
      
      return new Outbox(context);
   }
   
   abstract public File[] getOutFiles();
   
   abstract public void stopWatchingOutbox();
   
   abstract public void startWatchingOutbox();
   
   abstract public boolean isStopSending();
   
   abstract public void setStopSending(boolean stopSending);
   
   abstract public boolean isWatchingOutbox();
   
   abstract public void add(@NonNull final File file);
   
   abstract public void setNewFileListener(NewFileListener listener);
   
   abstract public void sendMessage(@NonNull IMailMessage message);
}
