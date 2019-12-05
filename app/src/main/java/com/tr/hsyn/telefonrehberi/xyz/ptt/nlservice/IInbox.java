package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import java.util.List;


/**
 * <h1>IInbox</h1>
 * 
 * Bu sınıf gelen kutusu görevini yerine getirecek
 */
public abstract class IInbox implements MessageExecutive{
   
   /**
    * Göndericiler
    */
   IRecipients senders = IRecipients.create();
   
   /**
    * Gelen mesajlar
    */
   List<IMailMessage> messages;
   
   
}
