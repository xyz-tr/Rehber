package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import android.os.Build;
import android.os.FileObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;

import java.io.File;

import timber.log.Timber;


public abstract class IFileObserver extends FileObserver{
   
   //private final File    file;
   private       boolean isWatching;
   
   IFileObserver(String path, int mask){
      
      super(path, mask);
      
     // file = new File(path);
   }
   
   @RequiresApi(api = Build.VERSION_CODES.Q) IFileObserver(@NonNull File file, int mask){
      
      super(file, mask);
      //this.file = file;
   }
   
   @Override public void startWatching(){
      
      super.startWatching();
      isWatching = true;
   }
   
   @Override public void stopWatching(){
      
      super.stopWatching();
      isWatching = false;
   }
   
   @Override
   public void onEvent(int i, @Nullable String s){
      
      if(i != FileObserver.CREATE){
         
         Timber.d("İstenmeyen olay : %d", i);
         
         if(s != null){
            
            Timber.d("İstenmeyen olayın gerçekleştiği dosya : %s", s);
         }
         
         return;
      }
      
      Worker.onMain(() -> newFileCreated(s));
   }
   
   /**
    * Yeni bir dosya oluştuğunda bildir.
    */
   abstract void newFileCreated(String fileName);
   
   boolean isWatching(){
      
      return isWatching;
   }
}
