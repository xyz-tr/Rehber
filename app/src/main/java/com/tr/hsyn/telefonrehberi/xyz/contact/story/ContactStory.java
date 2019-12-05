package com.tr.hsyn.telefonrehberi.xyz.contact.story;

import android.content.Context;

import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.util.Listx;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * <h1>ContactStory</h1>
 * <p>
 * Uygulamanın bizzat tutacağı rehber kayıtları üzerinde yönetim.
 */

public interface ContactStory{
   
   void close();
   
   /**
    * Tüm kayıtları döndür.
    *
    * @return Tüm kayıtlar
    */
   List<Contact> get();
   
   /**
    * Kaydı al.
    *
    * @param number Alınacak kaydın anahtarı (telefon numarası)
    * @return Kayıt varsa kayıt, yoksa {@code null}
    */
   @Nullable
   Contact get(String number);
   
   /**
    * 
    * @param contact eklenecek kişi
    * @return başarılı ise {@code true}
    */
   boolean add(Contact contact);
   
   /**
    * Ekle.
    * 
    * @param name isim
    * @param number numara
    * @return başarılı ise {@code true}
    */
   boolean add(String name, String number);
   
   /**
    * 
    * @param key telefon numarası
    * @return verilen numara kayıtlı ise {@code true}
    */
   boolean exist(String key);
   
   /**
    * Kaydı sil.
    *
    * @param contact Silinecek kayıt
    * @return Silindi ise {@code true}
    */
   boolean setDeleted(Contact contact);
   
   /**
    * Kaydı güncellenmiş olarak kaydet.
    *
    * @param oldContact     Orjinal kayıt
    * @param updatedContact Orjinal kaydın güncellenmiş örneği
    * @return Güncelleme yapıldı ise {@code true}
    */
   boolean update(IPhoneContact oldContact, IPhoneContact updatedContact);
   
   /**
    * 
    * @param contact güncellenecek
    * @return başarılı ise {@code true}
    */
   boolean update(Contact contact);
   
   /**
    * 
    * @param keyNumber silinecek
    * @return başarılı ise {@code true}
    */
   boolean delete(String keyNumber);
   
   /**
    * Bakılma sayısını 1 arttır.
    *
    * @param key anahtar
    * @return işlem başarılı ise {@code true}
    */
   boolean incLookCount(String key);
   
   /**
    * Bakılma sayısını ver.
    *
    * @param key anahtar telefon numarası
    * @return Bakılma sayısı
    */
   int getLookCount(String key);
   
   /**
    * Story oluştur.
    *
    * @return ContactStory
    */
   static ContactStory create(Context context){
      
      return new ContactDatabase(context);
   }
   
   /**
    * İki listedeki numaraların eşit olup olmadığını kontrol et.
    *
    * @param numbers  Liste_1
    * @param numbers2 Liste_2
    * @return Numaralar eşit ise @{code true}
    */
   static boolean equalsNumbers(Collection<String> numbers, Collection<String> numbers2){
      
      if(numbers == null && numbers2 == null) return true;
      if(numbers == null || numbers2 == null) return false;
      
      Listx.removeNulls(numbers);
      Listx.removeNulls(numbers2);
      
      if(numbers.isEmpty() && numbers2.isEmpty()) return true;
      if(numbers.isEmpty() || numbers2.isEmpty()) return false;
      
      if(numbers.size() != numbers2.size()) return false;
      
      if(numbers.size() == 1){
         
         return Arrays.equals(numbers.toArray(), numbers2.toArray());
      }
      
      //Eleman sayıları eşit
      
      for(String number : numbers){
         
         boolean yes = false;
         
         for(String number2 : numbers2){
            
            if(number.equals(number2)){
               
               yes = true;
               break;
            }
         }
         
         if(!yes) return false;
      }
      
      return true;
   }
   
   /**
    * 
    * @return tüm anahtarlar
    */
   List<String> getNumbers();
   
}
