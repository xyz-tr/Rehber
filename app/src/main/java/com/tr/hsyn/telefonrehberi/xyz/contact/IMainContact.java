package com.tr.hsyn.telefonrehberi.xyz.contact;

import android.os.Parcelable;


public interface IMainContact extends Parcelable{
   
   static IMainContact create(String name, String contactId){
      
      return new MainContact(name, contactId);
   }
   
   static IMainContact create(IMainContact contact){
   
      return new MainContact(contact.getName(), contact.getContactId());
   }
   
   String getContactId();
   
   String getName();
   
   void setName(String name);
   
   boolean equals(String id);
}
