package com.tr.hsyn.telefonrehberi.util.swipe;


/**
 * <h1>SwipeListener</h1>
 * 
 * Listedeki elemanların kaydırma olayı dinleyicisi
 */
public interface SwipeListener{
   
   /**
    * Kaydırma gerçekleştiğinde.
    * 
    * @param index Kaydırılan elemanın index'i
    */
   void onSwipe(int index);
}
