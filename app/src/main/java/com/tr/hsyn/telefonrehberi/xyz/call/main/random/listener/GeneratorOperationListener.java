package com.tr.hsyn.telefonrehberi.xyz.call.main.random.listener;

import com.tr.hsyn.telefonrehberi.util.concurrent.WorkerBreakeListener;
import com.tr.hsyn.telefonrehberi.util.concurrent.WorkerGenerationOnCompleteListener;
import com.tr.hsyn.telefonrehberi.util.concurrent.WorkerGeneratorListener;


public interface GeneratorOperationListener<R> extends
      IConnectionListener,
      WorkerBreakeListener,
      WorkerGenerationOnCompleteListener,
      WorkerGeneratorListener<R>{
   
   
}
