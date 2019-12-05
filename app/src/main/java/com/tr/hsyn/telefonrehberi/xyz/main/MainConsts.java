package com.tr.hsyn.telefonrehberi.xyz.main;

public interface MainConsts{
   
   /**
    * Yeni bir kişi eklemek için kullanılacak request code
    */
   int REQUEST_CODE_ADD_CONTACT = 9;
   
   /**
    * Activity'ler başlatılırken kullanılan geçirtirme süresi
    */
   long DELAY_ACTIVITY_START = 80L;
   /**
    * ViewPager içindeki rehber ekranının index'i
    */
   int  PAGE_CONTACTS        = 0;
   /**
    * ViewPager içindeki arama kayıtları ekranının index'i
    */
   int  PAGE_CALL_LOG        = 1;
   
}
