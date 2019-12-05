package com.tr.hsyn.telefonrehberi.util.phone;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.u;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class FreeSpaceManager{
   
   private final float   FREE_SPACE_LIMIT = 70.0F;
   private final Context context;
   private       float   freeMBytes;
   private       boolean okey;
   
   public FreeSpaceManager(Context context) {
      
      this.context = context;
   
      freeMBytes = (float) getFreeBytes() / (1024 * 1024);
      
      checkRecordFiles();
   }
   
   
   private void checkRecordFiles() {
      
      List<File> files = new ArrayList<>();
      
      File[] callFiles  = Files.getCallAudioFolderFile(context).listFiles();
      File[] audioFiles = Files.getAudioFolderFile(context).listFiles();
      
      if (callFiles != null) {
         
         if (callFiles.length > 0) {
            
            Logger.d(u.format("Kaydedilmiş %d arama kaydı var%n", callFiles.length));
            files.addAll(Arrays.asList(callFiles));
         }
         else {
   
            Logger.d("Kaydedilmiş bir arama kaydı yok\n");
         }
      }
      else {
   
         Logger.d("Arama kayıtlarına ulaşılamadı\n");
      }
      
      if (audioFiles != null) {
         
         if (audioFiles.length > 0) {
   
            Logger.d(u.format("Kaydedilmiş %d ses kaydı var%n", audioFiles.length));
            files.addAll(Arrays.asList(audioFiles));
         }
         else {
   
            Logger.d("Kaydedilmiş bir ses kaydı yok\n");
         }
      }
      else {
   
         Logger.d("Ses kayıtlarına ulaşılamadı\n");
      }
      
      int size = files.size();
      
      if (size == 0) {
   
         Logger.d("Kaydedilmiş bir ses dosyası bulunamadı\n");
      }
      else{
   
         Logger.d(u.format("Toplam %d kayıt var%n", size));
      }
      
      int i = 1;
      
      for (File file : files) {
         
         long duration = Files.getDuration(file.getAbsolutePath());
   
         Logger.d(u.format("%d. %s [süre=%s, kbytes=%d, date=%s, sağlam=%s]%n", i++, file.getName(), Files.getMp3Duration(file), file.length() / 1024L, Time.getDate(file.lastModified()), duration != -60L));
      }
      
      Logger.d(u.format("Kullanılabilir alan        : %.2f MB%n", freeMBytes));
      Logger.d(u.format("Kullanılabilir alan limiti : %.2f MB%n", FREE_SPACE_LIMIT));
      
      if (freeMBytes < FREE_SPACE_LIMIT) {
   
         Logger.d("Kullanılabilir alan kayıt yapmak için yeterli değil");
         freeSpace();
   
         freeMBytes = (float) getFreeBytes() / (1024 * 1024);
   
         if (freeMBytes > FREE_SPACE_LIMIT) {
      
            okey = true;
            return;
         }
   
         Logger.d("Kayıt için yeterli alan yok");
      }
      else{
   
         Logger.d("Kullanılabilir alan kayıt yapmak için yeterli.");
      }
   
      okey = true;
   }
   
   private void checkRecords(){
      
      
   }
   
   public boolean isOkey() {
      return okey;
   }
   
   private void freeSpace(){
      
      File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
   
      if (downloadDir == null) {
      
         Logger.d("Download klasörüne ulaşılamadı");
         return;
      }
      
      if (!downloadDir.exists()) {
         
         Logger.d("Böyle bir klasör yok : %s", downloadDir.getName());
         return;
      }
      
      deleteFiles(downloadDir);
   }
   
   private void deleteFiles(File dir) {
   
      File[] files = dir.listFiles();
   
      if (files == null) {
      
         Logger.w("Klasördeki dosyalara ulaşılamadı : %s", dir.getName());
         return;
      }
   
      if (files.length == 0) {
      
         Logger.d("Klasörde dosya yok");
         return;
      }
   
      Logger.d("Klasörde %d dosya var", files.length);
   
      for (File file : files) {
   
         if (file.delete()) {
            
            Logger.d("Dosya silindi : %s", file.getName());
         }
         else{
   
            Logger.w("Dosya silinemedi : %s", file.getName());
         }
      }
   }
   
   private long getFreeBytes(){
      
      return new StatFs(context.getFilesDir().getAbsolutePath()).getAvailableBytes();
   }
   
   
}
