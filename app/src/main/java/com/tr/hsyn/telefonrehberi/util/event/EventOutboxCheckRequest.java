package com.tr.hsyn.telefonrehberi.util.event;

import lombok.Getter;


public class EventOutboxCheckRequest{
   
   @Getter
   private final String headrequestor;
   
   public EventOutboxCheckRequest(String headrequestor){
      
      this.headrequestor = headrequestor;
   }
}
