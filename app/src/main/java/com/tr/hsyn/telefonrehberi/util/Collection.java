package com.tr.hsyn.telefonrehberi.util;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Consumer;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;

import java.util.List;


public class Collection{
   
   public static <T> List<T> filter(java.util.Collection<T> collection, Predicate<T> predicate){
   
      return Stream.of(collection).filter(predicate).toList();
   }
   
   public static <T, R> List<R> map(java.util.Collection<T> collection, Function<T, R> function){
      
      return Stream.of(collection).map(function).toList();
   }
   
   public static <T> void forEach(java.util.Collection<T> collection, Consumer<T> consumer){
      
      Stream.of(collection).forEach(consumer);
   }
   
}
