package com.tr.hsyn.telefonrehberi.util;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import lombok.experimental.UtilityClass;


@UtilityClass
public class NameExtention{
   
   private static       String          str;
   public static final  int             FROM    = 0b0000_0100;
   public static final  int             TO      = 0b0000_1000;
   public static final  int             IN_TO   = 0b0001_0000;
   public static final  int             HERS    = 0b0010_0000;
   public static final  int             SOFT    = 0b0100_0000;
   private static final int             K_VOWEL = 0b0000_0000;
   private static final int             I_VOWEL = 0b0000_0001;
   private static final int             X_ABC   = 0b0000_0010;
   private static final List<Character> kWowels = Arrays.asList('a', 'ı', 'o', 'u');
   private static final List<Character> IWowels = Arrays.asList('e', 'i', 'ö', 'ü');
   
   /**
    * Kelimeye uygun son eklentiyi bulur.
    * Verilen kelimenin özel isim olduğu varsayılmakta.
    * Yani buradan dönen değerin kelimeye yukarıdan virgül ile ayrılarak yazılması gerek.
    * Tek istisna {@link #SOFT} türü için bu gerekli değil.
    * Yani eklenti türü {@link #SOFT} ise virgül ile ayırmadan kelime ile bitişik yazılmalı.
    * Mesela kelime 'hafta' ise dönen değer 'lık' olur ve 'haftalık' olarak yazılır.<br><br>
    * <p>
    * what = {@link #IN_TO} için örnekler<br>
    *
    *
    *    <ul>
    *       <li>Ağaç    --> Ağaç'ı</li>
    *       <li>Ahmet   --> Ahmet'i</li>
    *       <li>Hasta   --> Hasta'yı</li>
    *       <li>Özgür   --> Özgür'ü</li>
    *       <li>Harun   --> Harun'u</li>
    *       <li>Eskici  --> Eskici'yi</li>
    *       <li>Uykucu  --> Uykucu'yu</li>
    *       <li>Ütü     --> Ütü'yü</li>
    *    </ul>
    *
    * <p>what = {@link #TO} için örnekler<br>
    *
    * <ul>
    *    <li>Ağaç    --> Ağaç'a</li>
    *    <li>Ahmet   --> Ahmet'e</li>
    *    <li>Hasta   --> Hasta'ya</li>
    *    <li>Özgür   --> Özgür'e</li>
    *    <li>Harun   --> Harun'a</li>
    *    <li>Eskici  --> Eskici'ye</li>
    *    <li>Uykucu  --> Uykucu'ya</li>
    *    <li>Ütü     --> Ütü'ye</li>
    * </ul>
    * <p>
    * what = {@link #FROM} için örnekler<br>
    *
    * <ul>
    *    <li>Ağaç    --> Ağaç'tan</li>
    *    <li>Ahmet   --> Ahmet'ten</li>
    *    <li>Hasta   --> Hasta'dan</li>
    *    <li>Özgür   --> Özgür'den</li>
    *    <li>Harun   --> Harun'dan</li>
    *    <li>Eskici  --> Eskici'den</li>
    *    <li>Uykucu  --> Uykucu'dan</li>
    *    <li>Ütü     --> Ütü'den</li>
    * </ul>
    *
    * <p>what = {@link #HERS} için örnekler<br>
    *
    * <ul>
    *    <li>Ağaç    --> Ağaç'ın</li>
    *    <li>Ahmet   --> Ahmet'in</li>
    *    <li>Hasta   --> Hasta'nın</li>
    *    <li>Özgür   --> Özgür'ün</li>
    *    <li>Harun   --> Harun'un</li>
    *    <li>Eskici  --> Eskici'nin</li>
    *    <li>Uykucu  --> Uykucu'nun</li>
    *    <li>Ütü     --> Ütü'nün</li>
    * </ul>
    *
    * <p>what = {@link #SOFT} için örnekler<br>
    * <ul>
    *    <li>Ağaç    --> Ağaç'lık</li>
    *    <li>Ahmet   --> Ahmet'lik</li>
    *    <li>Hasta   --> Hasta'lık</li>
    *    <li>Özgür   --> Özgür'lük</li>
    *    <li>Harun   --> Harun'luk</li>
    *    <li>Eskici  --> Eskici'lik</li>
    *    <li>Uykucu  --> Uykucu'luk</li>
    *    <li>Ütü     --> Ütü'lük</li>
    * </ul>
    *
    * @param str  kelime
    * @param what eklentinin türü
    * @return eklenti
    */
   public static String getExt(String str, @ExtensionType int what){
      
      synchronized(IWowels){
         
         if(str == null || (str = str.trim()).isEmpty()){ return ""; }
         
         NameExtention.str = str;
         
         int[] where = getVowel(str);
         
         //@off
         switch(what){
            
            case   FROM: return getFrom(where);
            case     TO: return getTo(where);
            case  IN_TO: return getInto(where);
            case   HERS: return getHers(where);
            case   SOFT: return getSoft(where);
         }
         
         return "";//@on
      }
   }
   
   /**
    * Sesli harfin yerine göre eklentiyi döndürür.
    *
    * @param where sesli harfin yeri
    * @return uygun ek kelime
    */
   private static String getInto(int[] where){
      
      char lastChar = str.charAt(str.length() - 1);
      
      switch(where[0]){
         
         case I_VOWEL:
            
            if(where[1] == 1){
               
               if(lastChar == 'ü' || lastChar == 'ö'){
                  
                  return "yü";
               }
               else{
                  
                  return "yi";
               }
            }
            else if(where[1] == 2){
               
               char c = str.charAt(str.length() - 2);
               
               if(c == 'ü' || c == 'ö'){
                  
                  return "ü";
               }
               else{
                  
                  return "i";
               }
            }
            
            return "yi";
         
         case K_VOWEL:
            
            if(where[1] == 1){
               
               if(lastChar == 'u' || lastChar == 'o'){
                  
                  return "yu";
               }
               else{
                  
                  return "yı";
               }
            }
            else if(where[1] == 2){
               
               char c = str.charAt(str.length() - 2);
               
               if(c == 'o' || c == 'u'){
                  
                  return "u";
               }
               else{
                  
                  return "ı";
               }
            }
            
            return "yi";
         
         case X_ABC:
            
            return "yi";
      }
      
      
      return "";
   }
   
   private static String getTo(int[] where){
      
      switch(where[0]){
         
         case I_VOWEL:
            
            switch(where[1]){
               
               case 1: return "ye";
               
               default: return "e";
            }
         
         case K_VOWEL:
            
            if(where[1] == 1){
               
               return "ya";
            }
            
            return "a";
         
         case X_ABC:
            
            return "e";
      }
      
      
      return "";
   }
   
   private static String getFrom(int[] where){
      
      switch(where[0]){
         
         case I_VOWEL:
            
            if(where[1] == 1 || where[1] == 2){
               
               return "den";
            }
            
            return "ten";
         
         case K_VOWEL:
            
            if(where[1] == 1 || where[1] == 2){
               
               return "dan";
            }
            
            return "tan";
         
         case X_ABC:
            
            return "den";
      }
      
      
      return "";
   }
   
   private static String getHers(int[] where){
      
      switch(where[0]){
         
         case I_VOWEL:
            
            if(where[1] == 1){
               
               char c = str.charAt(str.length() - 1);
               
               if(c == 'ö' || c == 'ü'){
                  
                  return "nün";
               }
               else{
                  
                  return "nin";
               }
            }
            else if(where[1] == 2){
               
               char c = str.charAt(str.length() - 2);
               
               if(c == 'ö' || c == 'ü'){
                  
                  return "ün";
               }
               else{
                  
                  return "in";
               }
            }
            
            break;
         
         case K_VOWEL:
            
            if(where[1] == 1){
               
               char c = str.charAt(str.length() - 1);
               
               if(c == 'u' || c == 'o'){
                  
                  return "nun";
               }
               else{
                  
                  return "nın";
               }
            }
            else if(where[1] == 2){
               
               char c = str.charAt(str.length() - 2);
               
               if(c == 'o' || c == 'u'){
                  
                  return "un";
               }
               else{
                  
                  return "ın";
               }
            }
            
            break;
         
         case X_ABC:
            
            return "nin";
      }
      
      return "in";
   }
   
   private static String getSoft(int[] where){
      
      char lastChar = str.charAt(str.length() - 1);
      
      switch(where[0]){
         
         case I_VOWEL:
            
            if(lastChar == 'ö' || lastChar == 'ü') return "lük";
            
            return "lik";
         
         case K_VOWEL:
            
            if(lastChar == 'o' || lastChar == 'u') return "luk";
            
            return "lık";
      }
      
      
      return "";
   }
   
   /**
    * Amaç, sesli harfin sondan kaçıncı olduğunu bulmak.
    * Sesli harf yoksa, ismin baştan ilk sessiz harfi döner.
    *
    * @param name İsim
    * @return Harfin türü sıfırıncı, harfin sıra numarası (index'i değil, 1 son harf demek) birinci index'te olan bir dizi
    */
   private static int[] getVowel(CharSequence name){
      
      int index = name.length();
      
      for(int i = index - 1; i >= 0; i--){
         
         char c = name.charAt(i);
         
         if(kWowels.contains(c)){
            
            return new int[]{K_VOWEL, index - i};
         }
         else if(IWowels.contains(c)){
            
            return new int[]{I_VOWEL, index - i};
         }
      }
      
      return new int[]{X_ABC, index};
   }
   
   
   @Retention(RetentionPolicy.SOURCE)
   @IntDef({FROM, TO, IN_TO, HERS, SOFT})
   public @interface ExtensionType{}
}
   
   
