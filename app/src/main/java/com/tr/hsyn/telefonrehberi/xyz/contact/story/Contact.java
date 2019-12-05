package com.tr.hsyn.telefonrehberi.xyz.contact.story;


import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.util.Time;

import lombok.Getter;
import lombok.Setter;


public class Contact{
   
   @Getter @Setter private String name;
   @Getter @Setter private String number;
   @Getter @Setter private long   savedDate;
   @Getter @Setter private long   deletedDate;
   @Getter @Setter private long   updatedDate;
   @Getter @Setter private int    lookCount;
   @Getter @Setter private long   lastLookDate;
   @Getter @Setter private String description;
   
   public Contact(String name, String number, long savedDate, long deletedDate, long updatedDate, int lookCount, long lastLookDate, String description){
      
      this.name         = name;
      this.number       = number;
      this.savedDate    = savedDate;
      this.deletedDate  = deletedDate;
      this.updatedDate  = updatedDate;
      this.lookCount    = lookCount;
      this.lastLookDate = lastLookDate;
      this.description  = description;
   }
   
   public Contact(Contact other){
      
      this.name         = other.name;
      this.number       = other.number;
      this.savedDate    = other.savedDate;
      this.deletedDate  = other.deletedDate;
      this.updatedDate  = other.updatedDate;
      this.lookCount    = other.lookCount;
      this.lastLookDate = other.lastLookDate;
      this.description  = other.description;
   }
   
   public Contact(String name, String number){
      
      this.name   = name;
      this.number = number;
   }
   
   public Contact(String number){
      
      this(null, number);
   }
   
   @NonNull
   @Override
   public String toString(){
      
      return "Contact{" +
             "name=" + name +
             ", number=" + number +
             ", saveDate=" + getDate(savedDate) +
             ", deletedDate=" + getDate(deletedDate) +
             ", updatedDate=" + getDate(updatedDate) +
             ", lastLookDate=" + getDate(lastLookDate) +
             ", lookCount=" + lookCount +
             '}';
   }
   
   private String getDate(long date){
      
      return date <= 0 ? "-" : Time.getDateTime(date);
   }
   
   public void incLookCount(){
      
      lookCount++;
   }
   
   
}
