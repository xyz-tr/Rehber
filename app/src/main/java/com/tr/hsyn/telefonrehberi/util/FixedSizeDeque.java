package com.tr.hsyn.telefonrehberi.util;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

import java9.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;


/**
 * <h1>FixedSizeDeque</h1>
 * Belirli sayıda eleman tutacak.
 * Elemanlar sürekli başa eklenecek ve sayı aşılırsa sondan silinecek.
 *
 * @param <E> eleman türü
 */
public class FixedSizeDeque<E> extends ConcurrentLinkedDeque<E>{
   
   @Getter private final int         fixedSize;
   @Setter private       Consumer<E> exceedFunction;
   
   public FixedSizeDeque(int size){
      
      this.fixedSize = size;
   }
   
   public FixedSizeDeque(int size, Consumer<E> exceedFunction){
      
      this.fixedSize      = size;
      this.exceedFunction = exceedFunction;
   }
   
   @Override
   public boolean add(E e){
      
      addFirst(e);
      
      checkSize();
      
      return true;
   }
   
   @Override 
   public void addFirst(E e){
      
      super.addFirst(e);
      checkSize();
   }
   
   @Override 
   public void addLast(E e){
      
      throw new RuntimeException("Sona ekleme yapılamaz");
   }
   
   @Override 
   public boolean addAll(Collection<? extends E> c){
      
      for(E e : c) addFirst(e);
      return true;
   }
   
   private void checkSize(){
      
      if(size() > fixedSize){
         
         E e = this.removeLast();
         
         if(exceedFunction != null) exceedFunction.accept(e);
      }
      
   }
}
