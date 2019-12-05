package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import lombok.Getter;


public enum MessageState{
   
   SENDING("Gönderiliyor"), SENT("Gönderildi"), FAILED("Gönderilemedi");
   
   @Getter
   String stateString;
   
   MessageState(String state){
      
      stateString = state;
   }
   
   
}
