package com.tr.hsyn.telefonrehberi.util.save;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SuppressLint("ApplySharedPref")
public class Save{
   
   private final SharedPreferences mSharedPreferences;
   
   public Save(@NonNull final Context context, @NonNull final String prefName){
      
      mSharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
   }
   
   public Save saveInt(String key, int value){
      
      mSharedPreferences.edit().putInt(key, value).commit();
      return this;
   }
   
   public int getInt(String key, int defaultValue){
   
      return mSharedPreferences.getInt(key, defaultValue);
   }
   
   public void saveBoolean(String key, boolean value){
      
      mSharedPreferences.edit().putBoolean(key, value).commit();
   }
   
   public boolean getBoolean(String key, boolean defaultValue){
   
      return mSharedPreferences.getBoolean(key, defaultValue);
   }
   
   public void saveFloat(String key, float value){
      
      SharedPreferences.Editor editor = mSharedPreferences.edit();
      editor.putFloat(key, value);
      editor.apply();
   }
   
   public float getFloat(String key, float defaultValue){
      
      if(isKeyExists(key)){
         return mSharedPreferences.getFloat(key, defaultValue);
      }
      return defaultValue;
   }
   
   public void saveLong(String key, long value){
      
      SharedPreferences.Editor editor = mSharedPreferences.edit();
      editor.putLong(key, value);
      editor.commit();
   }
   
   public long getLong(String key, long defaultValue){
      
      if(isKeyExists(key)){
         return mSharedPreferences.getLong(key, defaultValue);
      }
      return defaultValue;
   }
   
   public void saveString(String key, String value){
      
      SharedPreferences.Editor editor = mSharedPreferences.edit();
      editor.putString(key, value);
      editor.commit();
   }
   
   public String getString(String key, String defaultValue){
      
      if(isKeyExists(key)){
         return mSharedPreferences.getString(key, defaultValue);
      }
      return defaultValue;
   }
   
   public <T> void saveObject(String key, T object){
      
      String                   objectString = new Gson().toJson(object);
      SharedPreferences.Editor editor       = mSharedPreferences.edit();
      editor.putString(key, objectString);
      editor.apply();
   }
   
   public <T> T getObject(String key, Class<T> classType){
      
      if(isKeyExists(key)){
         String objectString = mSharedPreferences.getString(key, null);
         if(objectString != null){
            return new Gson().fromJson(objectString, classType);
         }
      }
      return null;
   }
   
   public <T> void saveObjectsList(String key, List<T> objectList){
      
      String                   objectString = new Gson().toJson(objectList);
      SharedPreferences.Editor editor       = mSharedPreferences.edit();
      editor.putString(key, objectString);
      editor.commit();
   }
   
   public <T> List<T> getObjectsList(String key, Class<T> classType){
      
      if(isKeyExists(key)){
         
         String objectString = mSharedPreferences.getString(key, null);
         
         if(objectString != null){
            
            ArrayList<T> t = new Gson().fromJson(objectString, new TypeToken<List<T>>(){}.getType());
            
            List<T> finalList = new ArrayList<>();
            
            for(int i = 0; i < t.size(); i++){
               String s = String.valueOf(t.get(i));
               finalList.add(new Gson().fromJson(s, classType));
            }
            
            return finalList;
         }
      }
      
      return new ArrayList<>();
   }
   
   public void clearSession(){
      
      SharedPreferences.Editor editor = mSharedPreferences.edit();
      editor.clear();
      editor.apply();
   }
   
   public boolean deleteValue(String key){
      
      if(isKeyExists(key)){
         SharedPreferences.Editor editor = mSharedPreferences.edit();
         editor.remove(key);
         editor.commit();
         return true;
      }
      
      return false;
   }
   
   public boolean isKeyExists(String key){
      
      Map<String, ?> map = mSharedPreferences.getAll();
      //LogCall.e("Save", "No element founded in sharedPrefs with the key " + key);
      return map.containsKey(key);
   }
   
   
   
}
