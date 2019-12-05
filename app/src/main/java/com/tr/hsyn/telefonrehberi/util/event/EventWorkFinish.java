package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.util.concurrent.WorkStat;

import lombok.Getter;


public class EventWorkFinish{
   
   @Getter private final WorkStat workStat;
   
   public EventWorkFinish(WorkStat workStat){
      
      this.workStat = workStat;
   }
}
