package com.tr.hsyn.telefonrehberi.util.db;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class COperator{
   
   private final Uri uri;
   private String[] projection;
   private String selection;
   private String[] args;
   private String sortOrder;
   //private ContentResolver contentResolver;
   
   public static COperator on(Uri uri){
      
      return new COperator(uri);
   }
   
   public COperator projection(String[] projection){
      
      this.projection = projection;
      return this;
   }
   
   public COperator selection(String selection){
      
      this.selection = selection;
      return this;
   }
   
   public COperator selectionArgs(String[] args){
      
      this.args = args;
      return this;
   }
   
   public COperator sortOrder(String sortOrder){
      
      this.sortOrder = sortOrder;
      return this;
   }
   
   public Operator perform(ContentResolver contentResolver){
   
      Cursor cursor = contentResolver.query(uri, projection, selection, args, sortOrder);
      
      return new Operator(cursor);
   }
   
}
