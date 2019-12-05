package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class DeleteCall{
   
   @Getter private       String code;
   @Getter private final ICall  phoneCall;
  
   public DeleteCall setCode(String code){
      
      this.code = code;
      return this;
   }
}
