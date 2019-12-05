package com.tr.hsyn.telefonrehberi.util.concurrent;

@FunctionalInterface
public interface WorkerProccessor<R>{
   
   void proccessOnBackground(R result);
}
