package com.tr.hsyn.telefonrehberi.util.ui.searchview.model;

/**
 * Created by claud on 02/11/2016.
 */

public abstract class BaseElement{
   
   private String mName;
   
   public BaseElement(String mName){
      
      this.mName = mName;
   }
   
   public String getName(){
      
      return mName;
   }
}
