package com.tr.hsyn.telefonrehberi.xyz.main.controller;

import timber.log.Timber;


public interface HaveTitle{
   
   default void setTitle(TitleController titleController, String title){
   
      if(titleController != null) {
         
         titleController.setTitle(title);
      }
      else{
   
         Timber.w("TitleController null");
      }
   }
   
   default void setSubTitle(TitleController titleController, String subTitle){
   
      if(titleController != null) titleController.setSubTitle(subTitle);
      else{
   
         Timber.w("TitleController null");
      }
   }
   
   default void setTitle(TitleController titleController, String title, String subTitle){
   
      if(titleController != null) titleController.setTitle(title, subTitle);
      else{
   
         Timber.w("TitleController null");
      }
   }
}
