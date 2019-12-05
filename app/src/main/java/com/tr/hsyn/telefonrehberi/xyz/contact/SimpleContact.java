package com.tr.hsyn.telefonrehberi.xyz.contact;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.Getter;
import lombok.ToString;


@ToString(callSuper = true)
public class SimpleContact extends MainContact implements ISimpleContact{
   
   @Getter
   private final String number;
   @Getter private final String rawId;
   
   public SimpleContact(@NonNull String rawId, @Nullable String name, String number, String contactId){
      
      super(name, contactId);
      this.number = number;
      this.rawId = rawId;
   }
   
   @Override
   public boolean equals(Object o){
      
      String key = Contacts.createKeyFromNumber(number);
      return o instanceof SimpleContact && key.equals(Contacts.createKeyFromNumber(((SimpleContact) o).number));
   }
   
   @Override
   public int hashCode(){
      
      return Contacts.createKeyFromNumber(number).hashCode();
   }
}
