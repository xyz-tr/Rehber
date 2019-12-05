package com.tr.hsyn.telefonrehberi.util.concurrent;

import androidx.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class PriorityThreadFactory implements ThreadFactory{
   
   private              int           priority;
   private              boolean       daemon;
   private final        String        namePrefix;
   private static final AtomicInteger poolNumber   = new AtomicInteger(1);
   private final        AtomicInteger threadNumber = new AtomicInteger(1);
   
   public PriorityThreadFactory(int priority){
      
      this(priority, true);
   }
   
   public PriorityThreadFactory(int priority, boolean daemon){
      
      this.priority = priority;
      this.daemon   = daemon;
      namePrefix    = "T:" + priority + "-p:" + poolNumber.getAndIncrement() + "-t:";
   }
   
   @Override
   public Thread newThread(@NonNull Runnable r){
      
      Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
      t.setDaemon(daemon);
      t.setPriority(priority);
      return t;
   }
}
