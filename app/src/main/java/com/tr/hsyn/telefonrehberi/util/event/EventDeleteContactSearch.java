package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.contact.SimpleContact;


public class EventDeleteContactSearch{
   
   public SimpleContact getContact(){
      
      return contact;
   }
   
   private SimpleContact contact;
   
   public EventDeleteContactSearch(SimpleContact contact){
      
      this.contact = contact;
   }
}
