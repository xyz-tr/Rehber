package com.tr.hsyn.telefonrehberi.util.sms;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;

import com.tr.hsyn.telefonrehberi.util.Logger;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import java.util.ArrayList;
import java.util.List;


public class Messages{
   
   public static final Logger   log      = Logger.jLog();
   private static      String[] SMS_COLS = {
         
         Telephony.Sms.ADDRESS,
         Telephony.Sms.TYPE,
         Telephony.Sms.DATE,
         Telephony.Sms.BODY
      
   };
   
   
   public static List<Message> getMessages(final Context contex, final String number){
   
      if(contex == null){ return null; }
      
      List<Message> messages = new ArrayList<>();
      
      final Cursor cursor = contex.getContentResolver().query(
            Telephony.Sms.CONTENT_URI,
            SMS_COLS,
            null,
            null,
            null
      );
   
      if(cursor == null){ return null; }
      
      while(cursor.moveToNext()){
         
         String _number = Contacts.normalizeNumber(cursor.getString(cursor.getColumnIndex(SMS_COLS[0])));
   
         if(Contacts.matchNumbers(_number, number)){
   
            final Message message = new Message(
                  number,
                  cursor.getString(cursor.getColumnIndex(SMS_COLS[3])),
                  cursor.getLong(cursor.getColumnIndex(SMS_COLS[2])),
                  cursor.getInt(cursor.getColumnIndex(SMS_COLS[1]))
            );
   
            messages.add(message);
         }
      }
      
      cursor.close();
      return messages;
   }
   
   
}
