package com.tr.hsyn.telefonrehberi.util.event;

public class EventSwipeContact{
   
   public int getIndex(){
      
      return index;
   }
   
   private int index;
   
   public EventSwipeContact(int index){
      
      this.index = index;
   }
}
