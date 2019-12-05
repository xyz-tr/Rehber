package com.tr.hsyn.telefonrehberi.xyz.main.controller;

import android.app.Activity;

import com.tr.hsyn.telefonrehberi.util.messages.Message;
import com.tr.hsyn.telefonrehberi.util.messages.MessageBox;
import com.tr.hsyn.telefonrehberi.util.ui.snack.SnackBarListener;

import lombok.Setter;


public class GlobalMessageController{
   
   private         Activity         activity;
   @Setter private SnackBarListener snackBarListener;
   private static  MessageBox       messageBox;
   
   public GlobalMessageController(Activity activity){
      
      this.activity = activity;
      messageBox = new MessageBox();
   }
   
   public static void addMessage(Message message){
      
      messageBox.add(message);
   }
   
   public void checkMessages(){
      
      Message message;
      
      while((message = messageBox.get()) != null){
         
         com.tr.hsyn.telefonrehberi.util.ui.snack.Message.builder()
                                                         .message(message.getMessage())
                                                         .snackBarListener(snackBarListener)
                                                         .delay(1223)
                                                         .build()
                                                         .showOn(activity);
         
      }
      
   }
   
}
