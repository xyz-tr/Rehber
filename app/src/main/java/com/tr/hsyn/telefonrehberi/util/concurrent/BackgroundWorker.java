package com.tr.hsyn.telefonrehberi.util.concurrent;

@FunctionalInterface
public interface BackgroundWorker<R>{
   
   R workOnBackground();
}
