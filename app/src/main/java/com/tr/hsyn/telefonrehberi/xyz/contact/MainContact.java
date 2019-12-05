package com.tr.hsyn.telefonrehberi.xyz.contact;


import android.os.Parcel;

import java.util.Objects;

import lombok.Getter;
import lombok.ToString;


@SuppressWarnings("WeakerAccess")
@ToString
public class MainContact implements IMainContact{
   
   @Getter private       String               name;
   @Getter private final String               contactId;
   public static final   Creator<MainContact> CREATOR = new Creator<MainContact>(){
      
      @Override
      public MainContact createFromParcel(Parcel in){
         
         return new MainContact(in);
      }
      
      @Override
      public MainContact[] newArray(int size){
         
         return new MainContact[size];
      }
   };
   
   protected MainContact(Parcel in){
      
      name      = in.readString();
      contactId = in.readString();
   }
   
   public MainContact(String name, String contactId){
      
      this.name      = name;
      this.contactId = contactId;
   }
   
   @Override
   public int describeContents(){
      
      return 0;
   }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
      dest.writeString(name);
      dest.writeString(contactId);
   }
   
   @Override
   public int hashCode(){
      
      return contactId.hashCode();
   }
   
   @Override
   public boolean equals(Object o){
      
      return o instanceof IMainContact && contactId.equals(((MainContact) o).contactId);
   }
   
   @Override
   public void setName(String name){
      
      this.name = name;
   }
   
   @Override
   public boolean equals(String id){
      
      return Objects.equals(contactId, id);
   }
}
