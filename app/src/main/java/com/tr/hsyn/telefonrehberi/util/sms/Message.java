package com.tr.hsyn.telefonrehberi.util.sms;



public class Message{
   
   private final String number;
   private final String body;
   private final long   date;
   private final int    type;
   
   public Message(String number, String body, long date, int type){
      
      this.number = number;
      this.body   = body;
      this.date   = date;
      this.type   = type;
   }
   
   public long getDate(){
      
      return date;
   }
   
   public String getNumber(){
      
      return number;
   }
   
   public String getBody(){
      
      return body;
   }
   
   public int getType(){
      
      return type;
   }
   
   
   @Override
   public String toString(){
      
      return number;
   }
}
