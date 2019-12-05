package com.tr.hsyn.telefonrehberi.xyz.contact;


import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;
import lombok.val;

@ToString
public class Number implements INumber{
   
   @Getter private String  number;
   @Getter private boolean isPrimary;
   @Getter private int     type;
   
   public Number(String number, boolean isPrimary, int type){
      
      this.number    = number;
      this.isPrimary = isPrimary;
      this.type      = type;
   }
   
   @Override
   public int hashCode(){
      
      return Contacts.createKeyFromNumber(number).hashCode();
   }
   
   @Override
   public boolean equals(@Nullable Object obj){
      
      val key = Contacts.createKeyFromNumber(number);
      
      return obj instanceof Number && key.equals(Contacts.createKeyFromNumber(((Number) obj).number));
   }
}
