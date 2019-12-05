package com.tr.hsyn.telefonrehberi.util.messages;


import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.IShow;

import org.androidannotations.annotations.EBean;

import java.util.ArrayList;
import java.util.List;

import java9.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;


/**
 * <h1>MessageBox</h1>
 * Mesaj kutusu
 */
@EBean(scope = EBean.Scope.Singleton)
public class MessageBox{
   
   @Getter @Setter private IShow             showman;
   @Getter @Setter private Consumer<Message> action;
   @Getter private final   List<Message>     messageList = new ArrayList<>();
   
   public void add(String message){
      
      add(new Message(message, Message.INFO));
   }
   
   public void add(Message message){
      
      messageList.add(message);
      
      if(action != null) action.accept(message);
   }
   
   public void add(String subject, String message){
      
      add(new Message(subject, message, Message.INFO));
   }
   
   public void add(String message, @Message.Type int type){
      
      add(new Message(message, type));
   }
   
   public void add(String subject, String message, @Message.Type int type){
      
      add(new Message(subject, message, type));
   }
   
   public void show(IShow showman){
      
      showman.show(get());
   }
   
   @Nullable
   public Message get(){
      
      if(size() != 0){
         
         Message message = messageList.remove(0);
         
         Logger.d("Mesaj kutusunda kalan mesaj sayısı : %d", messageList.size());
         
         return message;
      }
      
      return null;
   }
   
   public int size(){
      
      return messageList.size();
   }
   
   public void show(){
      
      if(showman != null){ showman.show(get()); }
   }
   
   
}
