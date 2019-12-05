package com.tr.hsyn.telefonrehberi.xyz.contact;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class RawContact extends MainContact implements IRawContact{
   
   @Getter private final String rawId;
   @Getter private final Account account;
   
   public RawContact(String rawId, String contactId, String name, Account account){
      
      super(name, contactId);
      this.account = account;
      this.rawId = rawId;
   }
   
}
