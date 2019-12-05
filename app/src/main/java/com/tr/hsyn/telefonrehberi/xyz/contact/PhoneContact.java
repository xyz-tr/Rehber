package com.tr.hsyn.telefonrehberi.xyz.contact;


import com.annimon.stream.Stream;

import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;


@ToString(callSuper = true)
public class PhoneContact extends MainContact implements IPhoneContact{
   
   private final           List<INumber>     numbers;
   @Getter final private   List<IRawContact> rawContacts;
   @Getter @Setter private Email[]           emails;
   
   public PhoneContact(IMainContact mainContact, List<IRawContact> rawContacts, List<INumber> numbers){
      
      this(mainContact.getContactId(), mainContact.getName(), rawContacts, numbers);
   }
   
   public PhoneContact(String contactId, String name, List<IRawContact> rawContacts, List<INumber> numbers){
      
      super(name, contactId);
      this.numbers     = numbers;
      this.rawContacts = rawContacts;
   }
   
   public PhoneContact(IPhoneContact other){
      
      this(other.getContactId(), other.getName(), other.getRawContacts(), other.getNumbers());
   }
  
   @Override
   public INumber getNumber(String number){
      
      if(numbers == null) return null;
      
      val v = Stream.of(numbers).filter(cx -> Contacts.matchNumbers(cx.getNumber(), number)).findFirst();
      
      return v.orElse(null);
   }
   
   @Override
   public List<INumber> getNumbers(){
      
      if(this.numbers == null) return null;
      
      return Stream.of(numbers).distinct().toList();
   }
   
   @Override
   public String getNumber(){
      
      if(numbers != null){
         
         return numbers.get(0).getNumber();
      }
      
      return null;
   }
   
   @Override
   public List<Account> getAccounts(){
      
      if(rawContacts == null) return null;
      
      return Stream.of(rawContacts).map(IRawContact::getAccount).toList();
   }
   
   @Override
   public List<Account> getAccounts(String type){
      
      val accounts = getAccounts();
      
      if(accounts == null) return null;
      
      return Stream.of(accounts).filter(cx -> cx.type.equals(type)).toList();
   }
   
   @Override
   public Collection<String> getNumberList(){
      
      if(numbers == null) return null;
      
      return Stream.of(this.numbers).map(INumber::getNumber).toList();
   }
   
   @Override
   public List<Account> getGoogleAccounts(){
      
      val accounts = getAccounts();
      
      if(accounts == null || accounts.isEmpty()) return null;
      
      return Stream.of(accounts).filter(cx -> cx.type != null && cx.type.equals("com.google")).toList();
   }
}
