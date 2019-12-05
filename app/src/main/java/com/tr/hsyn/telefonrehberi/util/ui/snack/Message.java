package com.tr.hsyn.telefonrehberi.util.ui.snack;

import android.app.Activity;
import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;

import lombok.Builder;


@Builder
public class Message{
   
   @Deprecated private      Spanner              messageSpan;
   @Deprecated private      int                  messageType;
   private                  String               message;
   @Builder.Default private long                 duration = 2250L;
   @Builder.Default private long                 delay    = 225L;
   private                  SnackBarListener     snackBarListener;
   private                  View.OnClickListener clickListener;
   private                  String               actionMessage;
   private                  boolean              cancel;
   
   public void showOn(Activity activity){
      
      if(activity == null)
         return;
      
      if(snackBarListener == null)
         if(activity instanceof SnackBarListener) snackBarListener = (SnackBarListener) activity;
      
      if(messageSpan != null){
         
         Show.globalMessage(
               activity,
               messageSpan,
               duration,
               delay,
               snackBarListener,
               clickListener,
               actionMessage,
               cancel,
               messageType
         );
      }
      else{
         
         Show.globalMessage(
               activity,
               message,
               duration,
               delay,
               snackBarListener,
               clickListener,
               actionMessage,
               cancel,
               messageType
         );
      }
      
      
   }
   
   @SuppressWarnings("FieldCanBeLocal")
   public static class MessageBuilder{
      
      private            Spanner messageSpan;
      private            String  message;
      @Show.Type private int     messageType;
      
      public MessageBuilder type(@Show.Type int type){
         
         messageType = type;
         return this;
      }
      
      public MessageBuilder message(Spanner message){
         
         this.messageSpan = message;
         return this;
      }
      
      public MessageBuilder message(String message){
         
         this.message = message;
         return this;
      }
      
      public MessageBuilder message(String message, Object... args){
   
         this.message = Stringx.format(message, args);
         return this;
      }
      
      
      
   }
   
}
