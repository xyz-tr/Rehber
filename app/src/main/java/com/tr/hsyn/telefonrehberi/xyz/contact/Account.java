package com.tr.hsyn.telefonrehberi.xyz.contact;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.R;

import java.util.Objects;



public class Account implements Parcelable{
   
   public String name;
   public String type;
   
   public Account(){
      
   }
   
   public Account(String name, String type){
      
      this.name = name;
      this.type = type;
   }
   
   protected Account(Parcel in){
      
      name = in.readString();
      type = in.readString();
   }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
      dest.writeString(name);
      dest.writeString(type);
   }
   
   @Override
   public int describeContents(){
      
      return 0;
   }
   
   public static final Creator<Account> CREATOR = new Creator<Account>(){
      
      @Override
      public Account createFromParcel(Parcel in){
         
         return new Account(in);
      }
      
      @Override
      public Account[] newArray(int size){
         
         return new Account[size];
      }
   };
   
   @NonNull
   @Override
   public String toString(){
      
      return "Account{" +
             "name='" + name + '\'' +
             ", type='" + type + '\'' +
             '}';
   }
   
   @Override
   public boolean equals(Object o){
      
      return o instanceof Account && Objects.equals(name, ((Account) o).name) && Objects.equals(type, ((Account) o).type);
   }
   
   @Override
   public int hashCode(){
      
      return Objects.hash(name, type);
   }
   
   public static Account createMessengerAccount(Context context){
   
      return new Account(context.getString(R.string.account_name_messenger), context.getString(R.string.account_type_messenger));
   }
   
   public static Account createSim1Account(Context context){
      
      return new Account(context.getString(R.string.account_name_sim_1), context.getString(R.string.account_type_sim));
   }
   
   public static Account createSim2Account(Context context){
      
      return new Account(context.getString(R.string.account_name_sim_2), context.getString(R.string.account_type_sim));
   }
}
