package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.api.services.gmail.Gmail;
import com.tr.hsyn.telefonrehberi.xyz.ptt.deleters.ISentboxDeleter;
import com.tr.hsyn.telefonrehberi.xyz.ptt.deleters.SentboxDeleter;
import com.tr.hsyn.telefonrehberi.xyz.ptt.mail.GmailService;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;


public class PTTKahverengi implements IPTTService{
   
   private static final Object                       obj            = new Object();
   private static       WeakReference<PTTKahverengi> ptt;
   private final        Gmail                        gmail;
   private              IRecipients                  recipients     = IRecipients.create();
   private              ISentboxDeleter              sentboxDeleter = new SentboxDeleter();
   private              List<String>                 inboxLabels    = Arrays.asList("INBOX", "xyz_0412");
   private              List<String>                 outboxLabels   = Arrays.asList("SENT", "xyz_0412");
   private              String                       queryFrom      = makeQueryFrom(new ArrayList<>(this.recipients.getRecipients(true)));
   private              String                       queryTo        = makeQueryTo(new ArrayList<>(this.recipients.getRecipients(true)));
   
   @Getter
   private IOutbox outbox;
   
   
   private PTTKahverengi(Context context){
      
      gmail  = GmailService.getGmailService(context);
      outbox = IOutbox.create(context);
      
      //messagebox = new MessageBox(context, gmail, sender, recipients, Collections.singletonList("INBOX"), queryFrom, queryTo);
   }
   
   @Override
   public void checkIncomming(){
      
   }
   
   public void deleteAllSent(){
      
      sentboxDeleter.deleteAllSentMessages(gmail, outboxLabels, queryTo);
   }
   
   @NonNull
   private String makeQueryTo(List<String> recipients){
      
      if(recipients.size() == 0) return "";
      
      if(recipients.size() == 1) return "to:" + recipients.get(0);
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for(int i = 0; i < recipients.size(); i++){
         
         stringBuilder.append("to:").append(recipients.get(i));
         
         if(i != recipients.size() - 1) stringBuilder.append(" ");
      }
      
      return stringBuilder.toString();
   }
   
   private String makeQueryFrom(List<String> recipients){
      
      if(recipients.size() == 0) return "";
      
      if(recipients.size() == 1) return "from:" + recipients.get(0);
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for(int i = 0; i < recipients.size(); i++){
         
         stringBuilder.append("from:").append(recipients.get(i));
         
         if(i != recipients.size() - 1) stringBuilder.append(" ");
      }
      
      return stringBuilder.toString();
   }
   
   static PTTKahverengi getInstance(Context context){
      
      if(ptt == null || ptt.get() == null){
         
         synchronized(obj){
            
            if(ptt == null || ptt.get() == null){
               
               ptt = new WeakReference<>(new PTTKahverengi(context));
            }
         }
      }
      
      return ptt.get();
   }
   
   
}
