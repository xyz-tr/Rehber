package com.tr.hsyn.telefonrehberi.xyz.call;

import android.content.Context;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.call.kitchen.NameEditable;

import java.util.Comparator;


public interface ICall extends Parcelable, Comparable<ICall>, Comparator<ICall>, NameEditable{
   
   @Override
   default int compare(ICall o1, ICall o2){
      
      return Long.compare(o2.getDate(), o1.getDate());
   }
   
   long getDate();
   
   @Override
   default int compareTo(ICall o){
      
      return Long.compare(o.getDate(), getDate());
   }
   
   void setDeleted();
   
   boolean equals(@NonNull ICall obj);
   
   static ICall create(String id, String name, String number, long date, int type, int duration){
      
      return new Call(id, name, number, date, type, duration);
   }
   
   /**
    * Arama türünü string olarak verir.
    *
    * @param type Arama türü
    * @return Türün string karşılığı
    */
   static String getTypeStr(Context resources, @CallType int type){
      
      String typeStr = null;
      
      //@off
      switch(type){
         
         case Type.INCOMMING        :
         case Type.INCOMMING_WIFI   : typeStr = resources.getString(R.string.incomming_call_details);break;
         case Type.OUTGOING         : 
         case Type.OUTGOING_WIFI    : typeStr = resources.getString(R.string.outgoing_call_details);break;
         case Type.MISSED           : typeStr = resources.getString(R.string.missed_call_details);break;
         case Type.REJECTED         : typeStr = resources.getString(R.string.rejected_call_details);break;
         case Type.BLOCKED          : typeStr = resources.getString(R.string.blocked_call_details);break;
         case Type.UNREACHED        : typeStr = "ulaşılmayan arama";break;
         case Type.GETREJECTED      : typeStr = "reddedilmiş arama"; break;
         case Type.UNRECIEVED       : typeStr = "ulaşmayan arama";  break;
         case Type.UNKNOWN          : typeStr = "Bilinmiyor"; break;
     }
      
      //@on
      return typeStr;
   }
   
   String getId();
   
   @Nullable
   String getName();
   
   String getNumber();
   
   int getType();
   
   int getDuration();
   
   long getRingingDuration();
   
   void setRingingDuration(long duration);
   
   long getSpeakingDuration();
   
   boolean isSharable();
   
   @Nullable
   String getNote();
   
   void setNote(@Nullable String note);
   
   default boolean isDeleted(){
      
      return getDeletedDate() != 0L;
   }
   
   long getDeletedDate();
   
   void setDeletedDate(long date);
}
