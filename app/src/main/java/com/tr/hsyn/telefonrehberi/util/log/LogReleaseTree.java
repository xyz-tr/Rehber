package com.tr.hsyn.telefonrehberi.util.log;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;


public class LogReleaseTree extends Timber.Tree{
   
   @Override
   protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t){
   
      if(priority == Log.VERBOSE || priority == Log.DEBUG){
         return;
      }
      
      Timber.log(priority, t, message);
   }
}
