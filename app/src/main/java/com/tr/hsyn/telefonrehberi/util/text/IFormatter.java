package com.tr.hsyn.telefonrehberi.util.text;

/**
 * <h1>IFormatter</h1>
 * String formatlamak için basit bir sözleşme.
 * 
 * @author hsyn 2019-11-27 12:22:04
 */
public interface IFormatter{
   
   /**
    * Verilen anahtar ve argümanları kullanarak string'i formatla.
    * 
    * @return formatlanmış string
    */
   CharSequence format();
   
   /**
    * Verilen string için gerekli olan argümanları
    * bu metodu kullanarak ver.
    * 
    * @param order argümanın verilen string içindeki sırası. Bu sıra sıfırdan <u>değil</u> 1'den başlar.
    * @param obj argüman
    * @return format nesnesi
    */
   IFormatter arg(int order, Object obj);
   
   /**
    * Verilen string için gerekli olan anahtarları 
    * bu metodu kullanarak ver.
    * 
    * @param key anahtar
    * @param obj argüman
    * @return string
    */
   IFormatter key(String key, Object obj);
   
   /**
    * Yeni bir format nesnesi oluştur.
    * 
    * @param str formatlanacak string
    * @return format nesnesi
    */
   static IFormatter create(String str){
      
      return new StrFormatter(str);
   }
}
