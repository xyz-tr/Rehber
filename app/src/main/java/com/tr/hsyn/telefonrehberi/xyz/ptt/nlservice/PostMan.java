package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.util.phone.Files;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.ITextMailMessage;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import timber.log.Timber;


public final class PostMan implements IPostMan{
   
   private static final Object                 OBJECT = new Object();
   private static       WeakReference<PostMan> postMan;
   private final        Context                context;
   private final        String                 sender;
   
   private PostMan(Context context){
      
      this.context = context;
      sender       = Paper.book().read("from", null);
   }
   
   @Override
   public void postText(String text, String recipient){
      
      if(recipient == null || recipient.trim().isEmpty()) {
         
         Timber.w("Alıcı yok");
         return;
      }
      
      List<String> l = new ArrayList<>();
      l.add(recipient);
      
      postText(text, l);
   }
   
   @Override
   public void postText(String text, List<String> recipients){
      
      if(recipients.size() == 0){
         
         Timber.w("Alıcı yok");
         return;
      }
      
      ITextMailMessage message  = ITextMailMessage.create(text, recipients);
      String           fileName = System.nanoTime() + "_" + message.getSubject() + ".xml";
      File             file     = new File(Files.getOutbox(context), fileName);
      
      if(sender == null){
         
         Timber.w("Gönderici tanımlı değil");
         return;
      }
      
      message.setSender(sender);
      message.setFile(file);
      
      if(message.saveToXml(file)){
         
         Timber.d("Mesaj dosyası oluşturuldu : %s", fileName);
      }
      else{
   
         Timber.d("Mesaj dosyası oluşturulamadı : %s", message.getSubject());
      }
      
   }
   
   @NonNull
   static PostMan getInstance(@NonNull final Context context){
      
      if(postMan == null || postMan.get() == null){
         
         synchronized(OBJECT){
            
            if(postMan == null || postMan.get() == null){
               
               postMan = new WeakReference<>(new PostMan(context));
            }
         }
      }
      
      return postMan.get();
   }
   
   
}
