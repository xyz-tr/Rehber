package com.tr.hsyn.telefonrehberi.xyz.contact;


public interface IRawContact extends IMainContact{
   
   Account getAccount();
   
   String getRawId();
   
   static IRawContact create(String rawId, String contactId, String name, Account account){
      
      return new RawContact(rawId, contactId, name, account);
   }
}
