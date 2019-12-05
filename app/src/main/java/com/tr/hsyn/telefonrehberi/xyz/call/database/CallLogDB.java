package com.tr.hsyn.telefonrehberi.xyz.call.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class CallLogDB extends CallLogDatabase implements ICallLog{
   
   
   public CallLogDB(Context context){
      
      super(context);
   }
   
   @Override
   public void close(){
      
      super.close();
   }
   
   @Override
   public void add(Iterable<? extends ICall> calls){
      
      SQLiteDatabase writableDatabase = getWritableDatabase();
      
      try{
         
         writableDatabase.beginTransaction();
         
         for(ICall phoneCall : calls){
            
            ContentValues values = setValues(phoneCall);
            
            if(writableDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1){
               
               Timber.d("Veri tabanına yeni bir arama kaydı eklendi : %s", phoneCall.getName() != null ? phoneCall.getName() : phoneCall.getNumber());
            }
            else{
               
               Timber.w("Veri tabanına yeni bir arama kaydı eklenmek istendi ancak başarısız oldu : %s", phoneCall.getName() != null ? phoneCall.getName() : phoneCall.getNumber());
            }
         }
         
         writableDatabase.setTransactionSuccessful();
      }
      finally{
         
         writableDatabase.endTransaction();
      }
   }
   
   private static ContentValues setValues(@NonNull ICall phoneCall){
      
      ContentValues values = new ContentValues();
      
      values.put(ID, phoneCall.getId());
      values.put(NAME, phoneCall.getName());
      values.put(NUMBER, phoneCall.getNumber());
      values.put(DATE, phoneCall.getDate());
      values.put(TYPE, phoneCall.getType());
      values.put(DURATION, phoneCall.getDuration());
      values.put(DELETED_DATE, phoneCall.getDeletedDate());
      values.put(RINGING_DURATION, phoneCall.getRingingDuration());
      values.put(SPEAKING_DURATION, phoneCall.getSpeakingDuration());
      values.put(SHARABLE, phoneCall.isSharable());
      values.put(NOTE, phoneCall.getNote());
      
      return values;
   }
   
   @Override
   public boolean add(ICall phoneCall){
      
      boolean result = false;
      
      if(contains(phoneCall.getDate())){
         
         Logger.d("Bu kayıt zaten var : %s", phoneCall);
      }
      else{
         
         ContentValues  values           = setValues(phoneCall);
         SQLiteDatabase writableDatabase = getWritableDatabase();
         
         try{
            
            if(writableDatabase.insertWithOnConflict(TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE) != -1){
               
               Timber.d("Veri tabanına yeni bir arama kaydı eklendi : %s", phoneCall.getName() != null ? phoneCall.getName() : phoneCall.getNumber());
               result = true;
            }
            else{
               
               Timber.w("Veri tabanına yeni bir arama kaydı eklenmek istendi ancak başarısız oldu : %s", phoneCall.getName() != null ? phoneCall.getName() : phoneCall.getNumber());
            }
         }
         catch(Exception e){
            
            e.printStackTrace();
            Timber.w("Veri tabanına ekleme yapmak istendi ancak veritabanı açılamadı : %s", e.toString());
         }
      }
      
      return result;
   }
   
   @Override
   public void update(Iterable<? extends ICall> calls){
      
      SQLiteDatabase db = getWritableDatabase();
      
      db.beginTransaction();
      
      for(ICall phoneCall : calls){
         
         ContentValues values = setValues(phoneCall);
         db.update(TABLE_NAME, values, DATE + "=?", new String[]{String.valueOf(phoneCall.getDate())});
      }
      
      db.setTransactionSuccessful();
      db.endTransaction();
   }
   
   @Override
   public boolean update(ICall phoneCall){
      
      ContentValues  values = setValues(phoneCall);
      SQLiteDatabase db     = getWritableDatabase();
      
      int i = db.update(TABLE_NAME, values, DATE + "=?", new String[]{String.valueOf(phoneCall.getDate())});
      
      return i > 0;
   }
   
   @Override
   public ICall get(long date){
      
      SQLiteDatabase r = getReadableDatabase();
      
      Cursor cursor = r.query(
            TABLE_NAME,
            CALL_LOG_COLUMNS,
            u.format("%s =?", CALL_LOG_COLUMNS[2]),
            new String[]{String.valueOf(date)},
            null,
            null,
            null);
      
      ICall call = null;
      
      if(cursor != null){
         
         if(cursor.moveToFirst()){
            
            String id       = cursor.getString(cursor.getColumnIndex(ID));
            String name     = cursor.getString(cursor.getColumnIndex(NAME));
            String number   = cursor.getString(cursor.getColumnIndex(NUMBER));
            String note     = cursor.getString(cursor.getColumnIndex(NOTE));
            int    type     = cursor.getInt(cursor.getColumnIndex(TYPE));
            int    duration = cursor.getInt(cursor.getColumnIndex(DURATION));
            //long    date      = cursor.getLong(cursor.getColumnIndex(DATE));
            long    deleted   = cursor.getLong(cursor.getColumnIndex(DELETED_DATE));
            long    rDuration = cursor.getLong(cursor.getColumnIndex(RINGING_DURATION));
            long    sDuration = cursor.getLong(cursor.getColumnIndex(SPEAKING_DURATION));
            boolean sharable  = cursor.getInt(cursor.getColumnIndex(SHARABLE)) == 1;
            
            
            call = new Call(name, id, number, date, type, duration, deleted, rDuration, sDuration, sharable, note);
         }
         
         cursor.close();
      }
      
      return call;
   }
   
   private boolean contains(long date){
      
      SQLiteDatabase db = getReadableDatabase();
      
      Cursor cursor = db.query(
            TABLE_NAME,
            new String[]{DATE},
            DATE + "=?",
            new String[]{String.valueOf(date)},
            null,
            null,
            null
      );
      
      boolean result = false;
      
      if(cursor != null){
         
         result = cursor.getCount() > 0;
         
         cursor.close();
      }
      
      return result;
   }
   
   public boolean removeDbFile(){
      
      File file = new File(getDataDir());
      
      String[] items = file.list();
      
      boolean res = false;
      
      if(items != null){
         
         if(items.length != 0){
            
            for(String db : items){
               
               if(db.contains(TABLE_NAME)){
                  
                  if(deleteFile(new File(file, db))){
                     
                     res = true;
                  }
               }
            }
         }
      }
      
      return res;
   }
   
   @SuppressLint("SdCardPath")
   @NonNull
   private String getDataDir(){
      
      String fileName = null;
      
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
         
         fileName = new File(getContext().getDataDir(), "/databases").getAbsolutePath();
      }
      else{
         
         String destPath = getContext().getFilesDir().getPath();
         destPath = destPath.substring(0, destPath.lastIndexOf('/')) + "/databases";
         
         File file = new File(destPath);
         
         if(file.exists()){
            
            fileName = file.getAbsolutePath();
         }
         
         else{
            
            try{
               
               if(file.createNewFile()){
                  
                  fileName = file.getAbsolutePath();
               }
            }
            catch(IOException e){
               e.printStackTrace();
            }
         }
      }
      
      if(fileName == null) fileName = u.format("/data/data/%s/databases/", getContext().getPackageName());
      
      return fileName;
   }
   
   private boolean deleteFile(@NonNull File file){
      
      if(file.delete()){
         
         Timber.d("dosya silindi : %s", file.getAbsoluteFile());
         return true;
      }
      
      Timber.w("Dosya silinemedi : %s", file.getAbsoluteFile());
      return false;
   }
   
   public float dbFileSize(){
      
      File file = new File(getDataDir());
      
      String[] items = file.list();
      
      float size = 0.0f;
      
      if(items != null){
         
         if(items.length != 0){
            
            for(String db : items){
               
               if(TABLE_NAME.equals(db)){
                  
                  File dFile = new File(getDataDir(), db);
                  
                  size = dFile.length();
                  break;
               }
            }
         }
      }
      
      final float bytes = 1024.0F;
      return size / bytes;
   }
   
   public boolean delete(long date){
      
      return getWritableDatabase().delete(TABLE_NAME, DATE + "=?", new String[]{String.valueOf(date)}) > 0;
   }
   
   public List<ICall> getAll(boolean inlusiveDeletedCalls){
      
      if(inlusiveDeletedCalls) return getAll();
      
      List<ICall> phoneCalls = new ArrayList<>();
      
      try(SQLiteDatabase readableDatabase = getWritableDatabase()){
         
         Cursor cursor = readableDatabase.query(
               TABLE_NAME,
               CALL_LOG_COLUMNS,
               null,
               null,
               null,
               null,
               CALL_LOG_COLUMNS[2] + " desc");
         
         
         if(cursor == null){ return phoneCalls; }
         
         if(cursor.getCount() == 0){
            
            Timber.d("Veri tabanında hiç kayıt yok");
            
            cursor.close();
            return phoneCalls;
         }
         
         int idCol        = cursor.getColumnIndex(ID);
         int nameCol      = cursor.getColumnIndex(NAME);
         int numberCol    = cursor.getColumnIndex(NUMBER);
         int dateCol      = cursor.getColumnIndex(DATE);
         int typeCol      = cursor.getColumnIndex(TYPE);
         int durationCol  = cursor.getColumnIndex(DURATION);
         int deletedCol   = cursor.getColumnIndex(DELETED_DATE);
         int rDurationCol = cursor.getColumnIndex(RINGING_DURATION);
         int sDurationCol = cursor.getColumnIndex(SPEAKING_DURATION);
         int sharableCol  = cursor.getColumnIndex(SHARABLE);
         int noteCol      = cursor.getColumnIndex(NOTE);
         
         
         while(cursor.moveToNext()){
            
            String  id        = cursor.getString(idCol);
            String  name      = cursor.getString(nameCol);
            String  number    = cursor.getString(numberCol);
            long    date      = cursor.getLong(dateCol);
            int     type      = cursor.getInt(typeCol);
            int     duration  = cursor.getInt(durationCol);
            long    deleted   = cursor.getLong(deletedCol);
            long    rDuration = cursor.getLong(rDurationCol);
            long    sDuration = cursor.getLong(sDurationCol);
            boolean sharable  = cursor.getInt(sharableCol) == 1;
            String  note      = cursor.getString(noteCol);
            
            
            ICall phoneCall = new Call(name, id, number, date, type, duration, deleted, rDuration, sDuration, sharable, note);
            
            phoneCalls.add(phoneCall);
         }
         
         cursor.close();
         return phoneCalls;
      }
      catch(Exception e){
         
         Timber.e("Veri tabanı açılamıyor : %s", e.toString());
      }
      
      return phoneCalls;
   }
   
   /**
    * Tüm arama kayıtlarını döndür.
    *
    * @return Kayıtlar
    */
   @Override
   @NonNull
   public List<ICall> getAll(){
      
      List<ICall> phoneCalls = new ArrayList<>();
      
      try(SQLiteDatabase readableDatabase = getWritableDatabase()){
         
         Cursor cursor = readableDatabase.query(
               TABLE_NAME,
               CALL_LOG_COLUMNS,
               CALL_LOG_COLUMNS[5] + " = 0",
               null,
               null,
               null,
               CALL_LOG_COLUMNS[2] + " desc");
         
         
         if(cursor != null){
   
            int idCol        = cursor.getColumnIndex(ID);
            int nameCol      = cursor.getColumnIndex(NAME);
            int numberCol    = cursor.getColumnIndex(NUMBER);
            int dateCol      = cursor.getColumnIndex(DATE);
            int typeCol      = cursor.getColumnIndex(TYPE);
            int durationCol  = cursor.getColumnIndex(DURATION);
            int deletedCol   = cursor.getColumnIndex(DELETED_DATE);
            int rDurationCol = cursor.getColumnIndex(RINGING_DURATION);
            int sDurationCol = cursor.getColumnIndex(SPEAKING_DURATION);
            int sharableCol  = cursor.getColumnIndex(SHARABLE);
            int noteCol      = cursor.getColumnIndex(NOTE);
   
   
            while(cursor.moveToNext()){
      
               String  id        = cursor.getString(idCol);
               String  name      = cursor.getString(nameCol);
               String  number    = cursor.getString(numberCol);
               long    date      = cursor.getLong(dateCol);
               int     type      = cursor.getInt(typeCol);
               int     duration  = cursor.getInt(durationCol);
               long    deleted   = cursor.getLong(deletedCol);
               long    rDuration = cursor.getLong(rDurationCol);
               long    sDuration = cursor.getLong(sDurationCol);
               boolean sharable  = cursor.getInt(sharableCol) == 1;
               String  note      = cursor.getString(noteCol);
      
               ICall phoneCall = new Call(name, id, number, date, type, duration, deleted, rDuration, sDuration, sharable, note);
              
               phoneCalls.add(phoneCall);
            }
   
            cursor.close();
         }
      }
      catch(Exception e){
         
         Timber.e("Veri tabanı açılamıyor : %s", e.toString());
      }
      
      return phoneCalls;
   }
   
   
}
