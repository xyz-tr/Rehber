package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;


import android.content.Context;

import java.util.List;


/**
 * <h1>IPostMan</h1>
 * <p>
 * Postacı.
 * Mesajları sadece postacı postalayabilir.
 *
 * @author hsyn 2019-09-12 17:26:21
 * @version 1.0.0
 * @since 2019-09-12 17:26:49
 */
public interface IPostMan{
   
   /**
    * Sadece string bir mesaj göndermek için.
    *
    * @param text      mesaj
    * @param recipient alıcı
    */
   void postText(String text, String recipient);
   
   /**
    * Sadece string bir mesaj göndermek için.
    *
    * @param text       mesaj
    * @param recipients alıcılar
    */
   void postText(String text, List<String> recipients);
   
   /**
    * Nesneyi oluştur.
    * 
    * @param context app context
    * @return postacı
    */
   static IPostMan callSigleton(Context context){
      
      return PostMan.getInstance(context);
   }
   
}
