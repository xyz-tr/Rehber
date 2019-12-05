package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;


public class EventUpdatedContact{
   
   private final IPhoneContact oldContact;
   private final IPhoneContact  updatedContact;
   
   public EventUpdatedContact(IPhoneContact oldContact, IPhoneContact updatedContact){
      
      this.oldContact     = oldContact;
      this.updatedContact = updatedContact;
   }

   public IPhoneContact getOldContact(){
      
      return oldContact;
   }
   
   public IPhoneContact getUpdatedContact(){
      
      return updatedContact;
   }
}
