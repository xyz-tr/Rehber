package com.tr.hsyn.telefonrehberi.util.observable;


import lombok.Setter;


public class Observe<T> extends Observablex<T>{
   
   @Setter private boolean changeOnSameValue;
   private         T       value;
   
   public Observe(){}
   
   public Observe(T value){
      
      this.value = value;
   }
   
   public void initValue(T value){
      
      this.value = value;
   }
   
   public T getValue(){
      
      return value;
   }
   
   public void setValue(T value){
   
      if(changeOnSameValue || !this.value.equals(value)){
      
         this.value = value;
         notifyChange(value);
      }
   
      
   }
   
   
   
   
}
