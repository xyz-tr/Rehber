package com.tr.hsyn.telefonrehberi.util.audio;

import android.content.Context;

import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.phone.FreeSpaceManager;


public class AudioServiceController{
   
   private Context context;
   private long    duration;
   private long    delay;
   private String  commandId;
   private boolean high;
   
   
   public AudioServiceController(Context context, long duration, long delay, String commandId, boolean high) {
      
      this.context = context;
      this.duration = duration;
      this.delay = delay;
      this.commandId = commandId;
      this.high = high;
      
      Worker.onBackground(this::doit, this::onWorkResult);
   }
   
   private void startAudioService(){
      
      AudioService.startAudio(context, duration, delay, commandId, high);
   }
   
   private Boolean doit() {
   
      return new FreeSpaceManager(context).isOkey();
   }
   
   private void onWorkResult(Boolean result) {
      
      if (result) {
         
         startAudioService();
      }
   }
}
