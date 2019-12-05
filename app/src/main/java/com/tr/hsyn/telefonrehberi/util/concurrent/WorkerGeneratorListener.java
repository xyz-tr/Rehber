package com.tr.hsyn.telefonrehberi.util.concurrent;

@FunctionalInterface
public interface WorkerGeneratorListener<R>{
   
   void onGenerate(R result, int progress, int total);
}
