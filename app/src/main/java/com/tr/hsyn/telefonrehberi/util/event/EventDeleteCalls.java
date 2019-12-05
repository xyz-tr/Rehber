package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.call.Call;

import java.util.List;


public class EventDeleteCalls{
   
   public List<Call> getPhoneCalls(){
      
      return phoneCalls;
   }
   
   private List<Call> phoneCalls;
   
   public EventDeleteCalls(List<Call> phoneCalls){
   
      this.phoneCalls = phoneCalls;
   }
}
