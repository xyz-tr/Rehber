package com.tr.hsyn.telefonrehberi.xyz.call.main.listener;


import com.tr.hsyn.telefonrehberi.xyz.call.ICall;


/**
 * <h1>CallUpdateListener</h1>
 * 
 * <p>
 *    Arama kaydı güncellemesini dinleyen şahıs.
 * 
 * @author hsyn 2019-12-03 14:30:59
 */
@FunctionalInterface
public interface CallUpdateListener{
   
   void onCallUpdated(int index, ICall call);
}
