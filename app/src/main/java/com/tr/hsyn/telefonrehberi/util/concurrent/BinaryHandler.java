package com.tr.hsyn.telefonrehberi.util.concurrent;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Supplier;
import com.orhanobut.logger.Logger;


public class BinaryHandler<R> extends Thread{
   
   private Supplier<R> backgroundWorker;
   private Consumer<R> workerProccessor;
   private Consumer<R>    workerHandler;
   private Handler             handler;
   //private Looper               looper;
   
   public BinaryHandler(@NonNull Supplier<R> backgroundWorker,
                        @Nullable Consumer<R> workerProccessor,
                        @NonNull Consumer<R> workerHandler){
      
      super("BinaryHandler");
      
      this.backgroundWorker = backgroundWorker;
      this.workerHandler    = workerHandler;
      this.workerProccessor = workerProccessor;
   }
   
   @Override
   public void run(){
   
      Logger.d("running");
      Looper.prepare();
      
      handler = new MyHandler(Looper.myLooper(), backgroundWorker, workerProccessor, workerHandler);
      
      Looper.loop();
   }
   
   public void quit(){
      
      if(handler != null){
         
         handler.getLooper().quit();
      }
   }
   
   
   @SuppressLint("HandlerLeak")
   private class MyHandler extends Handler{
      
      private       Supplier<R> backgroundWorker;
      private       Consumer<R> workerProccessor;
      private       Consumer<R> workerHandler;
      private final Handler     mainHandler = new Handler(Looper.getMainLooper());
      
      
      MyHandler(Looper looper, @NonNull Supplier<R> backgroundWorker,
                @Nullable Consumer<R> workerProccessor,
                @NonNull Consumer<R> workerHandler){
         
         super(looper);
         this.backgroundWorker = backgroundWorker;
         this.workerHandler    = workerHandler;
         this.workerProccessor = workerProccessor;
         
      }
      
      
      @Override
      public void handleMessage(@androidx.annotation.NonNull Message msg){
         
         super.handleMessage(msg);
         
         R result = backgroundWorker.get();
   
         if(workerProccessor != null){ workerProccessor.accept(result); }
         
         mainHandler.post(() -> workerHandler.accept(result));
         
      }
   }
   
   public void requestWork(){
      
      if(backgroundWorker == null){
   
         Logger.d("backgroundWorker tanımlanmamış");
         return;
      }
      
      if(workerHandler == null){
   
         Logger.d("workerHandler tanımlanmamış");
         return;
      }
      
      if(handler != null){
         
         if(!isAlive()){
   
            Logger.d("Mesaj döngüsü durdurulmuş");
            return;
         }
         
         handler.sendEmptyMessage(0);
      }
      else{
   
         Logger.w("handler null");
      }
      
      
   }
   
}
