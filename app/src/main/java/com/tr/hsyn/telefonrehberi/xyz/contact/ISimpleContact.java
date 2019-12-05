package com.tr.hsyn.telefonrehberi.xyz.contact;

public interface ISimpleContact extends IMainContact{
   
   static ISimpleContact create(String rawId, String name, String number, String contactId){
      
      return new SimpleContact(rawId, name, number, contactId);
   }
   
   String getNumber();
}
