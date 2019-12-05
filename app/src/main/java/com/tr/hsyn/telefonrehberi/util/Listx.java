package com.tr.hsyn.telefonrehberi.util;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class Listx{
   
   public static <T> int indexOf(List<T> list, T item){
      
      int index = -1;
      
      for(int i = 0; i < list.size(); i++){
         if(list.get(i).equals(item)){
            
            index = i;
            break;
         }
      }
      
      return index;
   }
   
   /**
    * İki kümenin farkını bul.
    * A - B (A fark B)
    *
    * @param A   A kümesi
    * @param B   B kümesi
    * @param <T> Eleman türü
    * @return A'da olup B'de olmayan elemanların kümesi (A fark B)
    */
   @NonNull
   public static <T> Collection<T> difference(@NonNull java.util.Collection<? extends T> A, @NonNull java.util.Collection<? extends T> B){
      
      // A - B (A fark B)
      Collection<T> dif = new ArrayList<>();
      
      for(T t : A){ if(!B.contains(t)){ dif.add(t); } }
      
      return dif;
   }
   
   
   public static <T> void removeNulls(@NonNull Collection<T> list){
      
      list.removeIf(Objects::isNull);
   }
   
}
