package com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;


public class Count extends BaseObservable{
   
   private int count;
   
   public Count(int count){
      
      this.count = count;
   }
   
   @Bindable
   public int getCount(){
      
      return count;
   }
   
   public void setCount(int count){
      
      this.count = count;
      notifyPropertyChanged(com.tr.hsyn.telefonrehberi.BR.count);
   }
}
