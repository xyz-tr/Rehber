package com.tr.hsyn.telefonrehberi.util.concurrent;

@FunctionalInterface
public interface WorkerHandler<R>{
   
   void onWorkResult(R result);
}
