package com.tr.hsyn.telefonrehberi.util.observable;



public class Observablex<T>{
   
   private Observerx<T> observer;
   
   public void setObserver(Observerx<T> observer){
   
      this.observer = observer;
   }
   
   protected void notifyChange(T newValue){
      
      if(observer != null) observer.update(newValue);
   }
   
   
}
