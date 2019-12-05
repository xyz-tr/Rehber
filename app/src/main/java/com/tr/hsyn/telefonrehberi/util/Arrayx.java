package com.tr.hsyn.telefonrehberi.util;


import com.tr.hsyn.telefonrehberi.util.text.Stringx;

import java.util.Arrays;


public class Arrayx{
   
   public static <T> boolean contains(T[] array, T element){
   
      for(T item : array) if(item.equals(element)) return true;
      return false;
   }
   
   public static <T> int indexOf(T[] array, T element){
   
      for(int i = 0; i < array.length; i++) if(array[i].equals(element)) return i;
      return -1;
   }
   
   public static <T> T[] shiftLeft(T[] array, int startIndex){
   
      if(startIndex < 0 || startIndex >= array.length){
         
         throw new ArrayIndexOutOfBoundsException(Stringx.format("startIndex : %d, array.length : %d", startIndex, array.length));
      }
   
      System.arraycopy(array, startIndex + 1, array, startIndex, array.length - startIndex);
      
      return Arrays.copyOf(array, array.length - 1);
   }
   
   
   
}
