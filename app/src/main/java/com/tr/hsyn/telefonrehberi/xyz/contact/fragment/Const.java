package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

public interface Const{
   
   /**
    * Uygulama içinde hesap seçimi yapıldığında seçilen hesabı almak için request code
    */
   String   EXTRA_SELECTED_ACCOUNT    = "EXTRA_SELECTED_ACCOUNT";
   /**
    * Ayarlar dosyası
    */
   String   PREF_MAIN                 = "pref_main";
   /**
    * Kayıtlı seçilen hesap ismi için anahtar
    */
   String   KEY_SELECTED_ACCOUNT_NAME = "selected_account_name";
   
   
   /**
    * Uygulama içinde hesap seçimi için kullanılacak request code
    */
   int    REQUEST_CODE_LOCAL_ACCOUNT_SELECTION  = 17;
   /**
    * Sistem üzerinden hesap seçimi için kullanılacak request code
    */
   int    REQUEST_CODE_SYSTEM_ACCOUNT_SELECTION = 16;
   
   /**
    * Kayıtlı seçilen hesap türü için anahtar
    */
   String KEY_SELECTED_ACCOUNT_TYPE             = "selected_account_type";
}
