package com.tr.hsyn.telefonrehberi.xyz.ptt.checkers;

import android.content.Context;

import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.util.audio.AudioService;
import com.tr.hsyn.telefonrehberi.util.phone.Files;

import java.io.File;

import timber.log.Timber;


public class AudioFilesChecker implements IAudioFilesChecker {
   
   private final Context                     context;
   private       File[]                      files;
   private       ICheckFilesCompleteListener completeListener;
   private       long                        lastCheck;
   
   AudioFilesChecker(Context context, ICheckFilesCompleteListener completeListener) {
      
      this.context = context;
      this.completeListener = completeListener;
   }
   
   @Override
   public void checkAudioFiles() {
      checkAudioFiles(null);
   }
   
   @Override
   synchronized
   public void checkAudioFiles(String id) {
      
      if (!FilesCheckers.timeIsUP(lastCheck, 10000L)) {
   
         if (id != null) {
   
            //PostMan.getInstance(context).setCommandExecuting(false, id, "checkAudioFiles(String) : kontrol sınırı");
         }
         
         return;
      }
      
      lastCheck = System.currentTimeMillis();
      
      if (noAudioFile()) {
         
         Timber.d("Dosya yok");
         onComplete(id);
         return;
      }
      
      if (files == null) {
         
         Timber.d("files null");
         onComplete(id);
         return;
      }
      
      File recordingFile = AudioService.getOnRecordingFile();
      
      Timber.d("%d Dosya var.", files.length);
      
      int i = 1;
      
      for (File file : files) {
         
         if (recordingFile != null && recordingFile.getName().equals(file.getName())) {
            
            Timber.d("%d. (kayıtta) %s", i++, file.getName());
         }
         else {
            
            Timber.d("%d. %s", i++, file.getName());
            //PostMan.getInstance(context).postAudio(file);
         }
      }
      
      onComplete(id);
   }
   
   private boolean noAudioFile() {
      
      files = Files.getAudioFolderFile(context).listFiles();
      
      return files != null && files.length == 0;
   }
   
   private void onComplete(@Nullable String id) {
      
      if (completeListener != null) completeListener.onCheckComplete(id);
   }
}
