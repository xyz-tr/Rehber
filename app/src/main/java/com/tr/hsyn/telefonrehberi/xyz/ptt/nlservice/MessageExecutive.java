package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import java.util.List;


public interface MessageExecutive{
   
   /**
    * Mesajları al getir.
    *
    * @return gelen mesajlar
    */
   List<IMailMessage> getMessages();
   
   /**
    * Mesajı sil.
    *
    * @param message silinecek mesaj
    * @return silme işlemi başarılı ise {@code true}
    */
   boolean deleteMessage(IMailMessage message);
   
   /**
    * Tüm mesajları sil.
    */
   void deleteMessages();
   
   
}
