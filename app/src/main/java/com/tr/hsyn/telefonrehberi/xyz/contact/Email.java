package com.tr.hsyn.telefonrehberi.xyz.contact;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.util.text.Stringx;


public class Email implements Parcelable{
   
   public static final Creator<Email> CREATOR = new Creator<Email>(){
      
      @Override
      public Email createFromParcel(Parcel in){
         
         return new Email(in);
      }
      
      @Override
      public Email[] newArray(int size){
         
         return new Email[size];
      }
   };
   private String email;
   private int    type;
   
   public Email(String email, int type){
      
      this.email = email;
      this.type  = type;
   }
   
   protected Email(Parcel in){
      
      email = in.readString();
      type  = in.readInt();
   }
   
   @Override
   public int describeContents(){
      
      return 0;
   }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
      dest.writeString(email);
      dest.writeInt(type);
   }
   
   @Override
   public int hashCode(){
      
      return email.hashCode();
   }
   
   @Override
   public boolean equals(@Nullable Object obj){
      
      return obj instanceof Email && email.equals(((Email) obj).email);
   }
   
   @NonNull
   @Override
   public String toString(){
      
      return Stringx.format("Email(%s, %d)", email, type);
   }
   
   public static String getTypeString(int type){
      
      switch(type){
         
         case ContactsContract.CommonDataKinds.Email.TYPE_HOME: return "Ev";
         case ContactsContract.CommonDataKinds.Email.TYPE_WORK: return "İş";
         case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE: return "Cep";
         
         default: return "Diğer";
      }
   }
   
   public String getEmail(){
      
      return email;
   }
   
   public void setEmail(String email){
      
      this.email = email;
   }
   
   public int getType(){
      
      return type;
   }
   
   public void setType(int type){
      
      this.type = type;
   }
   
}
