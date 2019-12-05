package com.tr.hsyn.telefonrehberi.xyz.ptt.checkers;

import android.content.Context;

import androidx.annotation.Nullable;

import com.annimon.stream.Stream;

import java.io.File;
import java.util.List;

import timber.log.Timber;


public class TextFileChecker implements ITextFilesChecker {
   
   private final Context                     context;
   private       File[]                      files;
   private       ICheckFilesCompleteListener completeListener;
   private       long                        lastCheck;
   
   
   public TextFileChecker(Context context, ICheckFilesCompleteListener completeListener) {
      
      this.context = context;
      this.completeListener = completeListener;
   }
   
   @Override
   synchronized 
   public void checkTextFiles(@Nullable String id) {
      
      if (!FilesCheckers.timeIsUP(lastCheck, 10000L)) {
   
        // PostMan.getInstance(context).setCommandExecuting(false, id, "checkTextFiles(String) : kontrol sınırı");
         return;
      }
      
      lastCheck = System.currentTimeMillis();
      
      if (isSavedFileExist()) {
         
         List<File> fileList = Stream.of(files).filter(File::isFile).toList();
         
         Timber.d("%d Dosya var.", fileList.size());
         
         int i = 1;
         
         for (File file : fileList) {
            
            Timber.d("%d. %s", i++, file.getName());
            //PostMan.getInstance(context).postTextFile(file);
         }
      }
      else {
         
         Timber.d("Dosya yok");
      }
      
      onComplete(id);
   }
   
   @Override
   public void checkTextFiles() {
      checkTextFiles(null);
   }
   
   private boolean isSavedFileExist() {
      
      if (context == null) return false;
      
      files = context.getFilesDir().listFiles();
      
      if (files == null) {
         
         Timber.w("Dosyalar alınamadı");
         return false;
      }
      
      
      if (files.length == 0) return false;
      
      for (File file : files) {
         
         if (!file.isDirectory()) return true;
      }
      
      return false;
   }
   
   private void onComplete(String id) {
      
      if (completeListener != null) completeListener.onCheckComplete(id);
   }
}
