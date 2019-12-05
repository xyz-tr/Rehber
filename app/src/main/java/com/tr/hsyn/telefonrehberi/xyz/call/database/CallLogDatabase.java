package com.tr.hsyn.telefonrehberi.xyz.call.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import lombok.Getter;


public class CallLogDatabase extends SQLiteOpenHelper{
   
   protected static final String NUMBER            = "number";
   protected static final String TYPE              = "type";
   static final           String ID                = "id";
   static final           String TABLE_NAME        = "call_log";
   static final           String NAME              = "name";
   static final           String DATE              = "date";
   static final           String DURATION          = "duration";
   static final           String DELETED_DATE      = "deleted_date";
   static final           String RINGING_DURATION  = "ringing_duration";
   static final           String SPEAKING_DURATION = "speaking_duration";
   static final           String SHARABLE          = "sharable";
   static final           String NOTE              = "note";
   
   static final String[] CALL_LOG_COLUMNS = {
         NAME,
         NUMBER,
         DATE,
         TYPE,
         DURATION,
         DELETED_DATE,
         RINGING_DURATION,
         SPEAKING_DURATION,
         SHARABLE,
         NOTE,
         ID
   };
   
   private static final String SQL_CREATE_CALL_LOG =
         String.format(
               "CREATE TABLE %s (%s TEXT, %s TEXT, %s INTEGER PRIMARY KEY, %s INTEGER, %s INTEGER, %s INTEGER DEFAULT 0, %s INTEGER DEFAULT 0, %s INTEGER DEFAULT 0, %s INTEGER DEFAULT 0, %s TEXT DEFAULT NULL, %s TEXT)",
               TABLE_NAME,
               NAME,
               NUMBER,
               DATE,
               TYPE,
               DURATION,
               DELETED_DATE,
               RINGING_DURATION,
               SPEAKING_DURATION,
               SHARABLE,
               NOTE,
               ID
         );

   
   @Getter private final Context context;
   
   CallLogDatabase(Context context){
      
      super(context, TABLE_NAME, null, 1);
      this.context = context;
   }
   
   @Override
   public void onCreate(SQLiteDatabase db){
      
      db.execSQL(SQL_CREATE_CALL_LOG);
   }
   
   @Override
   public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ }
   
   
}
