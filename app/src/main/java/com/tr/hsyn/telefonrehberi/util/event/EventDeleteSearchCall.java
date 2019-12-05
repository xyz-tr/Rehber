package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import lombok.Getter;


public class EventDeleteSearchCall{
   
   @Getter private final ICall call;
   
   public EventDeleteSearchCall(ICall phoneCall){
      
      call = phoneCall;
   }
}
