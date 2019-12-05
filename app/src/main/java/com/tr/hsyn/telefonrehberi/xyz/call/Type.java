package com.tr.hsyn.telefonrehberi.xyz.call;

import android.content.Context;
import android.provider.CallLog;

import com.tr.hsyn.telefonrehberi.R;


public interface Type{
   
   
   int INCOMMING      = CallLog.Calls.INCOMING_TYPE;
   int OUTGOING       = CallLog.Calls.OUTGOING_TYPE;
   int MISSED         = CallLog.Calls.MISSED_TYPE;
   int REJECTED       = 5;
   int BLOCKED        = 6;
   int INCOMMING_WIFI = 1000;
   int OUTGOING_WIFI  = 1001;
   
   /**
    * Arayıp da ulaşılamayan arama. Giden aramalar için.
    */
   int UNREACHED = 8000;
   
   /**
    * Arayıp da ulaşılamayan arama. Gelen aramalar için.
    */
   int UNRECIEVED = 8001;
   
   /**
    * Karşı tarafın reddettiği arama
    */
   int GETREJECTED = 8002;
   int UNKNOWN     = 8003;
   
   static String getTypeStr(Context context, @CallType int type){
      
      String typeStr;
      
      //@off
      switch(type){
         
         case INCOMMING      :
         case INCOMMING_WIFI : typeStr = context.getString(R.string.incomming_call);break;
         case OUTGOING       : 
         case OUTGOING_WIFI  : typeStr = context.getString(R.string.outgoing_call);break;
         case MISSED         : typeStr = context.getString(R.string.missed_call);break;
         case REJECTED       : typeStr = context.getString(R.string.rejected_call);break;
         case BLOCKED        : typeStr = context.getString(R.string.blocked_call);break;
         case UNREACHED      : typeStr = "Ulaşılamayan";break;
         case UNRECIEVED     : typeStr = "Ulaşmayan";break;
         case GETREJECTED    : typeStr = "Reddedildiğin";break;
         case UNKNOWN        :
         default             : typeStr = "Bilinmeyen";
      
      }
      
      //@on
      return typeStr;
   }
   
   static String getTypeStr(@CallType int type){
      
      String typeStr;
      
      //@off
      switch(type){
         
         case INCOMMING      :
         case INCOMMING_WIFI : typeStr = "Gelen Arama";break;
         case OUTGOING       : 
         case OUTGOING_WIFI  : typeStr = "Giden arama";break;
         case MISSED         : typeStr = "Cevapsız Arama";break;
         case REJECTED       : typeStr = "Reddedilen Arama";break;
         case BLOCKED        : typeStr = "Engellenen Arama";break;
         case UNREACHED      : typeStr = "Ulaşılamayan";break;
         case UNRECIEVED     : typeStr = "Ulaşmayan";break;
         case GETREJECTED    : typeStr = "Reddedildiğin";break;
         case UNKNOWN        :
         default             : typeStr = "Bilinmeyen";
      
      }
      
      //@on
      return typeStr;
   }
   
   static boolean isValidCallType(@CallType int callType){
      
      switch(callType){
         
         case INCOMMING:
         case OUTGOING:
         case MISSED:
         case REJECTED:
         case BLOCKED:
         case UNREACHED:
         case UNRECIEVED:
         case GETREJECTED:
            
            return true;
         
         case INCOMMING_WIFI:
         case OUTGOING_WIFI:
         case UNKNOWN:
         
         default: return false;
         
      }
   }
}
