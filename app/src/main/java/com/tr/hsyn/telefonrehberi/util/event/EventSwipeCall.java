package com.tr.hsyn.telefonrehberi.util.event;

public class EventSwipeCall{
   
   public int getIndex(){
      
      return index;
   }
   
   private int index;
   
   public EventSwipeCall(int index){
      
      this.index = index;
   }
}
