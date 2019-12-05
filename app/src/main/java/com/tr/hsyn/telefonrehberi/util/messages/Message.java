package com.tr.hsyn.telefonrehberi.util.messages;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import lombok.Getter;


public final class Message{
   
   public static final   int    INFO      = 0;
   public static final   int    WARN      = 1;
   public static final   int    ERROR     = 2;
   public static final   int    IMPORTANT = 3;
   @Getter private final String message;
   @Getter private final String subject;
   @Type private final   int    type;
   
   public Message(String message){
      
      this("-", message, INFO);
   }
   
   public Message(String subject, String message, @Type int type){
      
      this.message = message;
      this.type    = type;
      this.subject = subject;
   }
   
   public Message(String subject, String message){
      
      this(subject, message, INFO);
   }
   
   public Message(String message, @Type int type){
      
      this("-", message, type);
   }
   
   @Type
   public int getType(){
      
      return type;
   }
   
   
   @IntDef({INFO, WARN, ERROR, IMPORTANT})
   @Retention(RetentionPolicy.SOURCE)
   public @interface Type{}
   
   
}
