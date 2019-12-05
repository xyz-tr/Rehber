package com.tr.hsyn.telefonrehberi.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java9.util.function.Function;


public class PerfectSort{
   
   private static final String ABC = "abcçdefgğhıijklmnoöpqrsştuüvwxyz0123456789";
   
   private static final Map<Character, Integer> MAP = new HashMap<>();
   
   static{
      
      for(int i = 0; i < ABC.length(); i++){
         
         MAP.put(ABC.charAt(i), i);
      }
   }
   
   private static int compare(Character c1, Character c2){
      
      Integer val1 = MAP.get(c1);
   
      if(val1 == null){ return 1; }
      
      Integer val2 = MAP.get(c2);
   
      if(val2 == null){ return -1; }
      
      return val1 - val2;
   }
   
   private static boolean isBig(char c1, char c2){
      
      return compare(c1, c2) > 0;
   }
   
   public static int compare(String s1, String s2){
   
      if(s1 == null) return 1;
      if(s2 == null) return -1;
      
      if(s1.equals(s2)){ return 0; }
      
      s1 = s1.toLowerCase();
      s2 = s2.toLowerCase();
      
      int minLen = Math.min(s1.length(), s2.length());
      
      for(int i = 0; i < minLen; i++){
         
         char c1 = s1.charAt(i);
         char c2 = s2.charAt(i);
         
         if(c1 != c2){
   
            if(isBig(c1, c2)){ return 1; }
            
            return -1;
         }
      }
      
      return s1.length() - s2.length();
   }
   
   public static boolean isSmall(String s1, String s2){
      
      return compare(s1, s2) < 0;
      
   }
   
   public static void sort(List<String> list){
      
      Collections.sort(list, PerfectSort::compare);
   }
   
   public static <T> Comparator<T> comparator(Function<T, String> keyExtractor){
      
      return (o1, o2) -> {

         String s1 = keyExtractor.apply(o1);
         String s2 = keyExtractor.apply(o2);
         
         return compare(s1, s2);
      };
   }
   
   
}
