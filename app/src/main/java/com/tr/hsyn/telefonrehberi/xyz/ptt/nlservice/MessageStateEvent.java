package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import com.tr.hsyn.telefonrehberi.util.event.EventMessageState;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import org.greenrobot.eventbus.EventBus;


/**
 * <h1>MessageState</h1>
 * <p>
 * İşleme alınan mesajın durumunu temsil edecek.
 *
 * @author hsyn 2019-09-11 15:30:20
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public final class MessageStateEvent{
   
   private final IMailMessage message;
   private       MessageState state;
   
   
   MessageStateEvent(MessageState state, IMailMessage message){
      
      this.message = message;
      setState(state);
   }
   
   /**
    * Durumu değiştir.
    *
    * @param state durum
    */
   void setState(MessageState state){
      
      this.state = state;
      
      EventBus.getDefault().post(new EventMessageState(state, message));
   }
   
}
