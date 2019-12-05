package com.tr.hsyn.telefonrehberi.util.phone;

import androidx.annotation.Nullable;


public interface IContent{
   
   static IContent create(String text){
      
      return new Content(null, text);
   }
   
   static IContent create(String title, String body){
      
      return new Content(title, body);
   }
   
   @Nullable
   String getTitle();
   
   String getBody();
}
