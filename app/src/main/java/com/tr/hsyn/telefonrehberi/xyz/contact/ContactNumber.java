package com.tr.hsyn.telefonrehberi.xyz.contact;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.util.text.Stringx;


public class ContactNumber implements Parcelable{
   
   private String  number;
   private int     type;
   private Account account;
   private String  lookupKey;
   
   public ContactNumber(String number, int type, Account account, String lookupKey){
      
      this.number    = number;
      this.type      = type;
      this.account   = account;
      this.lookupKey = lookupKey;
   }
   
   protected ContactNumber(Parcel in){
      
      number    = in.readString();
      type      = in.readInt();
      account   = in.readParcelable(Account.class.getClassLoader());
      lookupKey = in.readString();
   }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
      dest.writeString(number);
      dest.writeInt(type);
      dest.writeParcelable(account, flags);
      dest.writeString(lookupKey);
   }
   
   @Override
   public int describeContents(){
      
      return 0;
   }
   
   public static final Creator<ContactNumber> CREATOR = new Creator<ContactNumber>(){
      
      @Override
      public ContactNumber createFromParcel(Parcel in){
         
         return new ContactNumber(in);
      }
      
      @Override
      public ContactNumber[] newArray(int size){
         
         return new ContactNumber[size];
      }
   };
   
   @NonNull
   @Override
   public String toString(){
      
      return Stringx.format("ContactNumber(number=%s, accountName=%s)", number, account);
   }
   
   public Account getAccount(){
      
      return account;
   }
   
   public String getAccountType(){
      
      return account.type;
   }
   
   public String getLookupKey(){
      
      return lookupKey;
   }
   
   public String getNumber(){
      
      return number;
   }
   
   public int getType(){
      
      return type;
   }
   
   public String getAccountName(){
      
      return account.name;
   }
   
}
