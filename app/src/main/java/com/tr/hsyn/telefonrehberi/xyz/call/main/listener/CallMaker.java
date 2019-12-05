package com.tr.hsyn.telefonrehberi.xyz.call.main.listener;


/**
 * <h1>CallMaker</h1>
 * 
 * <p>
 *    Arama yapabilecek olan şahıs.
 * 
 * @author hsyn 2019-12-03 14:29:09
 */

@FunctionalInterface
public interface CallMaker{
   
   void call(String number);
}
