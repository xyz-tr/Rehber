package com.tr.hsyn.telefonrehberi.xyz.contact;

import java.util.Collection;
import java.util.List;


public interface IPhoneContact extends IMainContact{
   
   INumber getNumber(String number);
   
   static IPhoneContact create(String contactId, String name, List<IRawContact> rawContacts, List<INumber> numbers){
      
      return new PhoneContact(contactId, name, rawContacts, numbers);
   }
   
   static IPhoneContact create(IMainContact mainContact, List<IRawContact> rawContacts, List<INumber> numbers){
      
      return new PhoneContact(mainContact, rawContacts, numbers);
   }
   
   List<INumber> getNumbers();
   
   String getNumber();
   
   List<Account> getAccounts();
   
   List<Account> getAccounts(String type);
   
   Collection<String> getNumberList();
   
   List<IRawContact> getRawContacts();
   
   Email[] getEmails();
   
   void setEmails(Email[] emails);
   
   List<Account> getGoogleAccounts();
   
   
}
