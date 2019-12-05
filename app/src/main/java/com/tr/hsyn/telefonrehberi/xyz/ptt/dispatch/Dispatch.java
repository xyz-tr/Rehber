package com.tr.hsyn.telefonrehberi.xyz.ptt.dispatch;


import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.xyz.ptt.command.ICommand;
import com.tr.hsyn.telefonrehberi.xyz.ptt.command.ICommandExecutor;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class Dispatch implements IDispatch {
   
   
   private List<ICommandExecutor> recievers = new ArrayList<>();
   
   @Override
   public void registerReciever(@NonNull ICommandExecutor reciever) {
      
      recievers.add(reciever);
   }
   
   @Override
   public void unregisterReciever(@NonNull ICommandExecutor reciever) {
      
      recievers.remove(reciever);
   }
   
   @Override
   public void publishMessages(List<ICommand> messages)  {
      
      if (recievers.size() == 0) {
   
         Timber.d("Kayıtlı alıcı yok. Konular işlenmiyor \u2708");
         return;
      }
      
      Timber.d("%d Alıcı var", recievers.size());
      
      for (ICommand message : messages) {
   
         for (ICommandExecutor reciever : recievers){
            
            reciever.executeCommand(message);
         }
      }
   }
   
   
}
