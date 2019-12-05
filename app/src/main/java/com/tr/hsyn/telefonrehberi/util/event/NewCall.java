package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * <h1>NewCall</h1>
 * 
 * <p>
 *    Yeni arama olmuş.
 * 
 * @author hsyn 2019-12-03 14:55:19
 */
public interface NewCall{
   
   ICall getCall();
   
   String getTag();
   
   NewCall setTag(String tag);
   
   static NewCall create(ICall call){
      
      return new EventNewCall(call);
   }
   
   @SuppressWarnings("PublicInnerClass")
   @RequiredArgsConstructor
   final class EventNewCall implements NewCall{
      
      @Getter private       String tag;
      @Getter private final ICall  call;
      
      @Override
      public NewCall setTag(String tag){
         
         this.tag = tag;
         return this;
      }
   }
}

