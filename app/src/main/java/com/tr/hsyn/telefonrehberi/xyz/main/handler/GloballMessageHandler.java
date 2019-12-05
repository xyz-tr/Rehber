package com.tr.hsyn.telefonrehberi.xyz.main.handler;

import com.tr.hsyn.telefonrehberi.util.messages.Message;

import org.androidannotations.annotations.EBean;

import java9.util.function.Consumer;

@EBean
public class GloballMessageHandler implements Consumer<Message>{
   
   @Override
   public void accept(Message message){
      
      switch(message.getType()){
         
         case Message.IMPORTANT: handleImportantMessage(message);
            break;
         case Message.ERROR: handleErrorMessage(message);
            break;
         case Message.WARN: handleWarnMessage(message);
            break;
         case Message.INFO: handleInfoMessage(message);
            break;
      }
      
   }
   
   private void handleImportantMessage(Message message){
      
      
   }
   
   private void handleErrorMessage(Message message){
      
      
   }
   
   private void handleWarnMessage(Message message){
      
      
   }
   
   private void handleInfoMessage(Message message){
      
      
   }
   
}
