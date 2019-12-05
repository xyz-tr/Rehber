package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;


import android.content.Context;


public interface IPTTService{
   
   void checkIncomming();
   
   IOutbox getOutbox();
   
   
   static IPTTService getService(Context context){
      
      return PTTKahverengi.getInstance(context);
   }
}
