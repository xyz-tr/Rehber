package com.tr.hsyn.telefonrehberi.util.text;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.NonNull;
import lombok.experimental.UtilityClass;


@UtilityClass
public final class Stringx{
   
   public static final String EMPTY = "";
   
   public static String emptyToNull(String str){
      
      return (str == null || str.trim().isEmpty()) ? null : str;
   }
   
   public static String toTitle(String text){
      
      if(isPhoneNumber(text)) return text;
      
      StringBuilder builder = new StringBuilder(text);
      
      builder.setCharAt(0, Character.toUpperCase(builder.charAt(0)));
      
      int index = 0;
      
      while((index = builder.indexOf(" ", index)) != -1){
         
         ++index;
         builder.setCharAt(index, Character.toUpperCase(builder.charAt(index)));
      }
      
      return builder.toString();
   }
   
   public static boolean isPhoneNumber(@NonNull final String str){
      
      if(str.isEmpty()) return false;
      
      if(!(str.contains("+") || str.contains("#") || str.contains("*"))){
         
         for(char character : str.toCharArray()){
            
            if(character == ' '){ continue; }
            if(!Character.isDigit(character)){ return false; }
         }
      }
      
      return true;
   }
   
   /**
    * Verilen string'i formatla. Aynen {@link String#format(String, Object...)} fonksiyonu gibi.
    * Tek farkı, dil olarak türkçe kullanması.
    *
    * @param text text
    * @param args args
    * @return text
    */
   public static String format(String text, Object... args){
      
      return String.format(new Locale("tr", "TR"), text, args);
   }
   
   /**
    * Verilen string'i formatla.
    * 
    * <pre>val value = Stringx.from("%d. name='$name', number='%s', type='$type', date='%s'%n")
    *                                     .arg(1, i)
    *                                     .key("name", name)
    *                                     .arg(2, noted.getNumber())
    *                                     .key("type", Type.getTypeStr(noted.getType()))
    *                                     .arg(3, Time.getDate(noted.getDate()))
    *                                     .format();</pre>
    *
    * @param str string
    * @return string
    */
   public static IFormatter from(String str){
      
      return IFormatter.create(str);
   }
   
   /**
    * Kelimenin ({@code word}) içinde başka bir kelime ({@code searchText}) geçiyor mu?
    * Yapılacak olan karşılaştırmanın özelliği string içindeki boşlukları da
    * hesaba katması.
    * Mesela {@code '543'}  ile {@code ' 5 4 3 '} karşılaştırması {@code true} sonucu verir.
    *
    * @param word       Aramanın yapılacağı string
    * @param searchText Aranacak string
    * @return {@code word} içinde {@code searchText} geçiyorsa {@code true}
    */
   public static boolean isMatch(String word, String searchText){
      
      if(word == null || searchText == null) return false;
      
      if(word.length() < searchText.length()) return false;
      
      return indexOfMatches(word, searchText).length != 0;
   }
   
   /**
    * Bir kelimenin içinde başka bir kelimenin geçtiği yerlerin başlangıç ve bitiş index'lerini verecek.
    * Eşleşme yapılırken boşluklar da hesaplanacak.
    * Mesela '543493' içinde ' 5 4 3 ' aranıyorsa eşleşecek ve [0,3] dizisi dönecek.
    * Mesela '5 4 3 493' içinde '543' aranıyorsa eşleşecek ve [0,6] dizisi dönecek.
    * Mesela '5 4 3493' içinde '543 9 3' aranıyorsa eşleşecek ve [0,5,6,8] dizisi dönecek.
    *
    * @param word       Aramanın yapılacağı kelime
    * @param searchText Aranacak kelime
    * @return Eşleşme olmazsa boş dizi
    */
   @androidx.annotation.NonNull
   public static Integer[] indexOfMatches(final String word, String searchText){
      
      searchText = trimLeft(searchText);
      
      //? Gelen string'lerden herhangi biri null ya da boş ise boş dizi dönecek
      if(word == null || searchText == null ||
         word.isEmpty() || searchText.isEmpty()) return new Integer[0];
      
      
      int  sIndex = 0, wIndex = 0;//? sIndex searchText için, wIndex ise word için index görevi yapacak
      char searchChar, wordChar;//? searchChar, searchText'ten ilerleyecek, wordChar ise word'dan ilerleyecek
      
      // sEnd ve wEnd string'lerin sonunu gösterecek
      final int           sEnd       = searchText.length();
      final int           wEnd       = word.length();
      final List<Integer> indexes    = new ArrayList<>();
      int                 matchCount = 0;
      
      while(true){
         
         //? Eğer iki string'in de sonuna gelmişsek iş bitmiş demektir.
         if(sIndex == sEnd && wIndex == wEnd){
            
            //? Ama bitirmeden önce eşleşme başlamışsa bitiş index'ini ekle
            if(matchCount == sEnd){
               
               indexes.add(wIndex);
            }
            else{
               
               if(matchCount == 0) break;
               if(indexes.isEmpty()) break;
               
               indexes.remove(indexes.size() - 1);
            }
            
            break; // İş biter
         }
         
         // searchText'in sonuna gelmişsek başlamış eşleşme varsa bitecek
         // yoksa ve hala word'un sonuna gelmemişsek searchText'in başına dönüp
         // varsa diğer eşleşmeleri arayacak
         if(sIndex == sEnd){
            
            if(matchCount == sEnd){
               
               indexes.add(wIndex);
            }
            else{
               
               if(matchCount > 0){
                  
                  if(!indexes.isEmpty())
                     indexes.remove(indexes.size() - 1);
               }
               
            }
            
            matchCount = sIndex = 0;
            wIndex++;
            continue;
         }
         
         // eğer word'un sonuna gelmişsek iş bitmiştir
         if(wIndex == wEnd){
            
            if(matchCount == sEnd){
               
               indexes.add(wIndex);
            }
            else{
               
               if(matchCount == 0) break;
               if(indexes.isEmpty()) break;
               
               indexes.remove(indexes.size() - 1);
            }
            
            break;
         }
         
         searchChar = searchText.charAt(sIndex);
         wordChar   = word.charAt(wIndex);
         
         if(searchChar == wordChar){
            
            if(sIndex == 0){
               
               indexes.add(wIndex);
            }
            
            // eşleşme olduğu sürece ilerleyerek devam et
            sIndex++;
            wIndex++;
            matchCount++;
            continue;
         }
         
         //Eşleşme yok
         
         if(Character.isSpaceChar(wordChar)){
            
            wIndex++;
         }
         else if(Character.isSpaceChar(searchChar)){
            
            sIndex++;
         }
         else{
            
            if(matchCount != 0){
               
               if(!indexes.isEmpty())
                  indexes.remove(indexes.size() - 1);
            }
            
            sIndex = matchCount = 0;
            wIndex++;
         }
      }
      
      return indexes.toArray(new Integer[0]);
   }
   
   /**
    * String'in başındaki boşlukları siler.
    *
    * @param word String
    * @return Boşlukları silinmiş string
    */
   public static String trimLeft(String word){
      
      if(word == null || word.isEmpty()) return word;
      
      if(!Character.isSpaceChar(word.charAt(0))) return word;
      
      StringBuilder stringBuilder = new StringBuilder(word);
      
      for(int i = 0; i < stringBuilder.length(); ){
         
         if(Character.isWhitespace(stringBuilder.charAt(i))){
            
            stringBuilder.deleteCharAt(i);
         }
         else{
            
            i++;
         }
      }
      
      return stringBuilder.toString();
   }
   
   public static String overWrite(String word){
      
      if(word == null) return null;
      if(word.isEmpty() || word.length() < 3) return word;
      
      StringBuilder stringBuilder = new StringBuilder(word);
      
      if(word.length() == 3){
         
         stringBuilder.setCharAt(1, '*');
         
         return stringBuilder.toString();
      }
      
      if(word.length() == 4){
         
         stringBuilder.setCharAt(1, '*');
         stringBuilder.setCharAt(2, '*');
         
         return stringBuilder.toString();
      }
      
      for(int i = 2; i < stringBuilder.length() - 2; i++){
         
         if(!Character.isSpaceChar(stringBuilder.charAt(i))){
            
            stringBuilder.setCharAt(i, '*');
         }
      }
      
      return stringBuilder.toString();
   }
   
   public static String overWrite(String word, char c){
      
      if(word == null) return null;
      if(word.isEmpty() || word.length() < 3) return word;
      
      StringBuilder stringBuilder = new StringBuilder(word);
      
      if(word.length() == 3){
         
         stringBuilder.setCharAt(1, c);
         
         return stringBuilder.toString();
      }
      
      if(word.length() == 4){
         
         stringBuilder.setCharAt(1, c);
         stringBuilder.setCharAt(2, c);
         
         return stringBuilder.toString();
      }
      
      for(int i = 2; i < stringBuilder.length() - 2; i++){
         
         if(!Character.isSpaceChar(stringBuilder.charAt(i))){
            
            stringBuilder.setCharAt(i, c);
         }
      }
      
      return stringBuilder.toString();
   }
   
   
}
