package com.tr.hsyn.telefonrehberi.xyz.call;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@NoArgsConstructor
@ToString
@Getter
@AllArgsConstructor
public class Call implements Serializable, ICall{
   
   public static final Parcelable.Creator<Call> CREATOR = new Parcelable.Creator<Call>(){
      
      @Override
      public Call createFromParcel(Parcel source){return new Call(source);}
      
      @Override
      public Call[] newArray(int size){return new Call[size];}
   };
   @Setter private     String                   name;
   private             String                   id;
   private             String                   number;
   private             long                     date;
   private             int                      type;
   private             int                      duration;
   @Setter private     long                     deletedDate;
   @Setter private     long                     ringingDuration;
   @Setter private     long                     speakingDuration;
   @Setter private     boolean                  sharable;
   @Setter private     String                   note;
 
   
   public Call(String id, String name, String number, long date, int type, int duration){
      
      this.id       = id;
      this.name     = name;
      this.number   = number;
      this.date     = date;
      this.type     = type;
      this.duration = duration;
   }
   
   protected Call(Parcel in){
      
      id               = in.readString();
      name             = in.readString();
      number           = in.readString();
      date             = in.readLong();
      type             = in.readInt();
      duration         = in.readInt();
      deletedDate      = in.readLong();
      ringingDuration  = in.readLong();
      speakingDuration = in.readLong();
      sharable         = in.readByte() != 0;
      note             = in.readString();
   }
   
   @Override
   public int compare(ICall o1, ICall o2){
      
      return Long.compare(o2.getDate(), o1.getDate());
   }
   
   @Override
   public int compareTo(ICall o){
      
      return Long.compare(o.getDate(), date);
   }
   
   @Override
   public void setDeleted(){
      
      deletedDate = System.currentTimeMillis();
   }
   
   @Override
   public int describeContents(){ return 0; }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
      dest.writeString(id);
      dest.writeString(name);
      dest.writeString(number);
      dest.writeLong(date);
      dest.writeInt(type);
      dest.writeInt(duration);
      dest.writeLong(deletedDate);
      dest.writeLong(ringingDuration);
      dest.writeLong(speakingDuration);
      dest.writeByte(sharable ? (byte) 1 : (byte) 0);
      dest.writeString(note);
   }
   
   @Override
   public boolean equals(@Nullable Object obj){
      
      return obj instanceof Call && date == ((Call) obj).date;
   }
   
   @Override
   public boolean equals(@NonNull ICall obj){
      
      return date == obj.getDate();
   }
   
}
