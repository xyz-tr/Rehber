package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import java.util.List;


/**
 * <h1>Sentbox</h1>
 * 
 * Bu sınıf giden kutusu görevini yerine getirecek
 */
public abstract class ISentbox implements MessageExecutive{
   
   /**
    * Alıcılar
    */
   IRecipients recipients = IRecipients.create();
   
   /**
    * Giden mesajlar
    */
   List<IMailMessage> messages;
   
}
