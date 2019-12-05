package com.tr.hsyn.telefonrehberi.util.db;

import android.database.Cursor;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class Operator<R>{
   
   private               List<R>             list = new ArrayList<>();
   private               Predicate<R>        predicate;
   private               Function<Cursor, R> function;
   @Getter private final Cursor              cursor;
   
   public Operator transform(Function<Cursor, R> function){
      
      this.function = function;
      return this;
   }
   
   public List<R> filter(Predicate<R> predicate){
      
      this.predicate = predicate;
      return list;
   }
   
   @NonNull
   private List<R> execute(){
      
      if(function == null) return list;
      
      while(cursor.moveToNext()){
         
         R item = function.apply(cursor);
         
         if(predicate == null) list.add(item);
         else if(predicate.test(item)) list.add(item);
      }
      
      cursor.close();
      return list;
   }
   
}
