package com.tr.hsyn.telefonrehberi.xyz.call.main.random;

import com.tr.hsyn.telefonrehberi.util.concurrent.WorkerBreakeListener;
import com.tr.hsyn.telefonrehberi.util.concurrent.WorkerGenerationOnCompleteListener;
import com.tr.hsyn.telefonrehberi.util.concurrent.WorkerGeneratorListener;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;


public interface GeneratorService extends WorkerGeneratorListener<Call>, WorkerGenerationOnCompleteListener, WorkerBreakeListener{
}
