package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

public class OutboxHeadrequestor{
   
   public static String MAIN = "Ana Uygulama";
   
   
   public static String getRequestorName(String className){
   
      switch(className){
         
         
         case "MainActivity":
            
            return MAIN;
            
         default:return  "Bilinmeyen Bir Yer";
      }
   }
   
}
