package com.tr.hsyn.telefonrehberi.xyz.ptt.message;


import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * <h1>ITextMailMessage</h1>
 * <p>
 * Mail ile gönderilecek en basit bilgi türü.
 * Bu nesne sadece basit bir string veri taşıyacak.
 * Daha doğrusu üst sınıfına sadece string bir alan ekleyecek.
 */
@ToString(callSuper = true)
@Root(name = "itext_mail_message")
public abstract class ITextMailMessage extends IMailMessage{
   
   @Getter @Setter @Element(name = "text", required = false)
   protected String text;
   
   public static ITextMailMessage create(String text, List<String> recipients){
      
      return new TextMailMessage(text, recipients);
   }
   
   @Override
   public ITextMailMessage create(IMailMessage mailMessage){
      
      return new TextMailMessage((ITextMailMessage) mailMessage);
   }
   
   @Nullable
   static ITextMailMessage createFromFile(File file){
   
      try{
         
         ITextMailMessage message = new Persister().read(TextMailMessage.class, file);
         
         message.setFile(file);
         return message;
      }
      catch(Exception e){
         e.printStackTrace();
      }
      
      return null;
   }
   
   @Override
   public boolean saveToXml(File file){
      
      File old = this.getFile();
      
      try{
   
         this.setFile(file);
         new Persister().write(this, file);
         return true;
      }
      catch(Exception e){
         
         e.printStackTrace();
         this.setFile(old);
      }
      
      return false;
   }
   
}
