package com.tr.hsyn.telefonrehberi.util.event;

public class EventRememberSelectedAccount{
   
   private final boolean isRemember;
   
   public EventRememberSelectedAccount(boolean isRemember){
      
      this.isRemember = isRemember;
   }
   
   public boolean isRemember(){
      
      return isRemember;
   }
}
