package com.tr.hsyn.telefonrehberi.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DBBase{
   
   private SQLiteOpenHelper dataBase;
   
   public DBBase(@Nullable Context context, DB dbInterface){
      
      dataBase = new DataBase(context, dbInterface);
   }
   
   public SQLiteOpenHelper getDataBase(){
      
      return dataBase;
   }
   
   private static class DataBase extends SQLiteOpenHelper{
      
      DB dbInterface;
      
      DataBase(@Nullable Context context, DB dbInterface){
         
         super(context, dbInterface.getDatabaseName(), null, dbInterface.getVersion());
         this.dbInterface = dbInterface;
      }
      
      @Override
      public void onCreate(SQLiteDatabase sqLiteDatabase){
         
         sqLiteDatabase.execSQL(dbInterface.getCreateTableQuery());
      }
      
      @Override
      public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
         
      }
   }
   
   
}
