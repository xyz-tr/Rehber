package com.tr.hsyn.telefonrehberi.util.event;

public class EventRefreshContacts{
   
   private String code;
   
   public EventRefreshContacts(String code){
      
      this.code = code;
   }
   
   public EventRefreshContacts(){
      
   }
   
   public String getCode(){
      
      return code;
   }
   
   public void setCode(String code){
      
      this.code = code;
   }
}
