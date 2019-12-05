package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.util.concurrent.WorkStat;

import java9.util.concurrent.CompletableFuture;
import lombok.Getter;


public class EventWorkStart{
   
   @Getter private final WorkStat          workStat;
   @Getter private final CompletableFuture completableFuture;
   
   public EventWorkStart(WorkStat workStat, CompletableFuture completableFuture){
      
      this.workStat          = workStat;
      this.completableFuture = completableFuture;
   }
   
}
