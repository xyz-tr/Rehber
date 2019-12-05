package com.tr.hsyn.telefonrehberi.xyz.ptt.checkers;

import android.content.Context;

import com.tr.hsyn.telefonrehberi.util.phone.Files;

import java.io.File;
import java.util.concurrent.ConcurrentLinkedDeque;

import io.paperdb.Paper;
import lombok.val;
import timber.log.Timber;


public class MessageChecker{
   
   private final long                          MESSAGE_DATE_LIMIT;
   private final Context                       context;
   private       ConcurrentLinkedDeque<String> oldbox;
   
   public MessageChecker(Context context){
      
      this.context = context;
      setOldbox();
      
      MESSAGE_DATE_LIMIT = Paper.book("message_checker").read("MESSAGE_DATE_LIMIT", 180000L);
   
      Timber.d("Mesaj zaman limiti [%d ms] olarak ayarlandı.", MESSAGE_DATE_LIMIT);
   }
   
   private void setOldbox(){
      
      val list = Files.getSentbox(context).listFiles();
      oldbox = new ConcurrentLinkedDeque<>();
      
      if(list == null) return;
      
      for(File file : list){
         
         oldbox.add(file.getAbsolutePath());
      }
   }
   

   public void checkMessage(){
      
     /* if(message.getFile() == null) return true;
   
      return existFile(message.getFile());*/
      
   }
   
   private boolean existFile(File file){
   
      for(String fileName : oldbox){
         if(file.getAbsolutePath().equals(fileName)) return true;
      }
      
      return false;
   }
   
   /*private boolean checkAudioFileMessage(IMessage message){
      
      File file = message.getFile();
      
      if(file == null){
         
         u.log.w("Dosya null");
         return true;
      }
      
      if(existFile(file)){
         
         u.log.d("Bu dosya daha önce işlendi . %s", file.getName());
         Files.deleteFile(file);
         return true;
      }
      
      if(file.length() < 5000L){
         
         u.log.d("Dosya boyutu çok küçük : %s [%d kb]", file.getName(), (file.length() / 1024L));
         Files.deleteFile(file);
         return true;
      }
      
      setAudioInfo(message);
      
      return false;
   }
   
   @SuppressLint("UsableSpace")
   private void setAudioInfo(IMessage message){
      
      File file = message.getFile();
      
      long duration = Files.getDuration(file.getAbsolutePath());
      
      String body = Time.getDate(file.lastModified()) + "\n";
      
      body += String.format(new Locale("tr"), "%-21s : %s\n%-21s : %.2f MB\n%-21s : %s\n%-21s : %.2f MB\n%-21s : %.2f MB\n%-21s : %.2f MB\n",
      
      
                            "Dosya ismi", file.getName(),
                            "Boyut", (float) file.length() / (1024 * 1024),
                            "Süre", duration != -60L ? Files.formatMilliSeconds(duration) : "Bilgi alınamadı",
                            "Toplam alan", (float) file.getTotalSpace() / (1024 * 1024),
                            "Boş alan", (float) file.getFreeSpace() / (1024 * 1024),
                            "Kullanılabilir alan", (float) file.getUsableSpace() / (1024 * 1024)
      
      );
      
      if(duration == -60L){
         
         message.setSubject(file.getName());
      }
      else{
         
         message.setSubject("AR");
      }
      
      message.setBody(body);
   }*/
   
}
