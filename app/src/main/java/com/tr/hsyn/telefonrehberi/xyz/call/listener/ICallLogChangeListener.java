package com.tr.hsyn.telefonrehberi.xyz.call.listener;

/**
 * <h1>ICallLogChangeListener</h1>
 * 
 * <p>
 *    Arama kayıtlarına ekleme ve çıkarma olaylarının dinleyicisi.
 * 
 * 
 * @author hsyn 2019-12-03 14:48:20
 */
public interface ICallLogChangeListener{
   
   void startListen();
   
   void stopListen();
}
