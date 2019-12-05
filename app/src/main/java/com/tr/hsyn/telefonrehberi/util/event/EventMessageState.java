package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.MessageState;

import lombok.Getter;


public class EventMessageState{
   
   
   @Getter
   private final MessageState state;
   
   @Getter
   private final IMailMessage message;
   
   public EventMessageState(MessageState state, IMailMessage message){
      
      this.state   = state;
      this.message = message;
   }
}
