package com.tr.hsyn.telefonrehberi.util.db;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


public interface SimpleDBOperator{
   
   default boolean add(SQLiteDatabase db, String tableName, ContentValues values){
   
      return db.insert(tableName, null, values) > 0;
   }
   
   default boolean update(SQLiteDatabase db, String tableName, String selection, String[] args, ContentValues values){
   
      return db.update(tableName, values, selection, args) > 0;
   }
   
   default boolean delete(SQLiteDatabase db, String tableName, String selection, String[] selectionArgs){
      
      return db.delete(tableName, selection, selectionArgs) > 0;
   }
   
}
