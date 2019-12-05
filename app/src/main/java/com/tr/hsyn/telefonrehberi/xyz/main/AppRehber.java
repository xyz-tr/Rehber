package com.tr.hsyn.telefonrehberi.xyz.main;

import android.app.Application;

import androidx.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.tr.hsyn.telefonrehberi.BuildConfig;
import com.tr.hsyn.telefonrehberi.util.log.LogDebugTree;
import com.tr.hsyn.telefonrehberi.util.log.LogReleaseTree;

import io.paperdb.Paper;
import timber.log.Timber;


public class AppRehber extends Application{
  
   
   @Override
   public void onCreate(){
      
      super.onCreate();
      setLogger();
      Paper.init(this);
     
   }
   
   private void setLogger(){
      
      FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                                                          .showThreadInfo(true)  // (Optional) Whether to show thread info or not. Default true
                                                          .methodCount(1)         // (Optional) How many method line to show. Default 2
                                                          .methodOffset(0)        // (Optional) Hides internal method calls up to offset. Default 5
                                                          // .logStrategy(customLog) (Optional) Changes the log strategy to print out. Default LogCat
                                                          .tag("Rehber")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
                                                          .build();
      
      
      Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
         
         @Override
         public boolean isLoggable(int priority, @Nullable String tag){
            
            return BuildConfig.DEBUG;
         }
      });
      
      if(BuildConfig.DEBUG){
         
         Timber.plant(new LogDebugTree());
      }
      else{
         
         Timber.plant(new LogReleaseTree());
      }
   }
   
}
