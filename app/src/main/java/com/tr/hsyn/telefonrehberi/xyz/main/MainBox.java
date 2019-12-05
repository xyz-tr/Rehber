package com.tr.hsyn.telefonrehberi.xyz.main;


import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;

import java.io.File;


@KickbackBox
public class MainBox{
   
   @KickbackElement(name = "wellRipple")
   int wellRipple;
   
   /**
    * Arkaplan işlemleri için gerçekleşen son hata kaydının yazıldığı dosya
    */
   @KickbackElement(name = "logFile")
   File logFile;
}
