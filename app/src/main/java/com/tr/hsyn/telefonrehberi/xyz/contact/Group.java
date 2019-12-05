package com.tr.hsyn.telefonrehberi.xyz.contact;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.Setter;


public class Group implements Parcelable{
   
   @Getter private         String  id;
   @Getter @Setter private String  title;
   @Getter private         Account account;
   @Getter private         boolean isReadOnly;
   @Getter @Setter private boolean isVisable;
   @Getter @Setter private int     membersCount;
   @Getter @Setter private         String  ringTone;
   
   public Group(String id, String title, Account account, boolean isReadOnly, boolean isVisable, int membersCount, String ringTone){
      
      this.id           = id;
      this.title        = title;
      this.account      = account;
      this.isReadOnly   = isReadOnly;
      this.isVisable    = isVisable;
      this.membersCount = membersCount;
      this.ringTone     = ringTone;
   }
   
   @Override
   public int hashCode(){
      
      return id.hashCode();
   }
   
   @Override
   public boolean equals(Object obj){
      
      return obj instanceof Group && id.equals(((Group) obj).id);
   }
   
   @NonNull
   @Override
   public String toString(){
      
      return "Group{" +
             "id='" + id + '\'' +
             ", title='" + title + '\'' +
             ", account=" + account +
             ", isReadOnly=" + isReadOnly +
             ", isVisable=" + isVisable +
             ", membersCount=" + membersCount +
             ", ringTone='" + ringTone + '\'' +
             '}';
   }
   
   @Override
   public int describeContents(){ return 0; }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
      dest.writeString(this.id);
      dest.writeString(this.title);
      dest.writeParcelable(this.account, flags);
      dest.writeByte(this.isReadOnly ? (byte) 1 : (byte) 0);
      dest.writeByte(this.isVisable ? (byte) 1 : (byte) 0);
      dest.writeInt(this.membersCount);
      dest.writeString(this.ringTone);
   }
   
   protected Group(Parcel in){
      
      this.id           = in.readString();
      this.title        = in.readString();
      this.account      = in.readParcelable(Account.class.getClassLoader());
      this.isReadOnly   = in.readByte() != 0;
      this.isVisable    = in.readByte() != 0;
      this.membersCount = in.readInt();
      this.ringTone     = in.readString();
   }
   
   public static final Creator<Group> CREATOR = new Creator<Group>(){
      
      @Override
      public Group createFromParcel(Parcel source){return new Group(source);}
      
      @Override
      public Group[] newArray(int size){return new Group[size];}
   };
   
}
