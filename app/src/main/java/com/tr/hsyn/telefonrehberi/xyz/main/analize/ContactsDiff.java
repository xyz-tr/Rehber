package com.tr.hsyn.telefonrehberi.xyz.main.analize;

import android.content.ContentResolver;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;

import com.tr.hsyn.telefonrehberi.util.Listx;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.SimpleContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.ContactStory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.val;
import timber.log.Timber;


public class ContactsDiff{
   
   private       boolean          _callInBackgroundThread;
   private       int              _threadPriority;
   private       Callback         callback;
   private final ContentResolver  contentResolver;
   private final ContactStory     contactStory;
   private final ContactsDiffInfo info = new ContactsDiffInfo();
   
   private final Runnable work = new Runnable(){
      
      @Override
      public void run(){
         
         val systemKeys = getNumbers();
         val localKeys  = contactStory.getNumbers();
         
         if(ifKeysNull(systemKeys, localKeys)) return;
         
         assert systemKeys != null && localKeys != null;
         
         val newKeys     = Listx.difference(systemKeys, localKeys);
         val removedKeys = Listx.difference(localKeys, systemKeys);
         
         if(ifNoDiff(newKeys, removedKeys)) return;
         
         List<SimpleContact> newContacts     = getNewContacts(newKeys);
         List<SimpleContact> removedContacts = getRemovedContacts(removedKeys);
         
         info.setNewContacts(newContacts);
         info.setRemovedContacts(removedContacts);
         callBack();
      }
   };
   
   public ContactsDiff(final ContentResolver contentResolver, ContactStory contactStory, Callback callback, boolean callInBackgroundThread, int threadPriority){
      
      this.contentResolver    = contentResolver;
      this.contactStory       = contactStory;
      _callInBackgroundThread = callInBackgroundThread;
      _threadPriority         = threadPriority;
      this.callback           = callback;
   }
   
   public void start(){
      
      String reason = "ContactsDiff:Rehber kayıtlarındaki değişiklikleri kontrol etme";
      Worker.onBackground(work, reason, _threadPriority < Thread.NORM_PRIORITY);
   }
   
   public static void request(final ContentResolver contentResolver, ContactStory contactStory, Callback callback, boolean callInBackgroundThread, int threadPriority){
      
      new ContactsDiff(contentResolver, contactStory, callback, callInBackgroundThread, threadPriority);
   }
   
   private List<SimpleContact> getRemovedContacts(Collection<String> removedKeys){
      
      List<SimpleContact> contacts = new ArrayList<>(removedKeys.size());
      
      for(val key : removedKeys){
         
         val item = contactStory.get(key);
         
         if(item != null) contacts.add(new SimpleContact("-1", item.getName(), item.getNumber(), "-1"));
      }
      
      return contacts;
   }
   
   private List<SimpleContact> getNewContacts(Collection<String> keys){
      
      String[] cols = {
            
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
      };
      
      
      val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            null,
            null,
            null
      );
      
      if(cursor == null) return new ArrayList<>(0);
      
      Set<SimpleContact> contacts = new HashSet<>(keys.size());
      
      while(cursor.moveToNext()){
         
         String number = Contacts.createKeyFromNumber(cursor.getString(cursor.getColumnIndex(cols[1])));
         
         if(keys.contains(number)){
            
            String name    = cursor.getString(cursor.getColumnIndex(cols[0]));
            String raw     = cursor.getString(cursor.getColumnIndex(cols[2]));
            String contact = cursor.getString(cursor.getColumnIndex(cols[3]));
            
            contacts.add(new SimpleContact(raw, name, number, contact));
         }
      }
      
      cursor.close();
      return new ArrayList<>(contacts);
   }
   
   private boolean ifNoDiff(Collection<String> newKeys, Collection<String> removedKeys){
      
      if(newKeys.size() == 0 && removedKeys.size() == 0){
         
         String report = Stringx.format("System rehberi ve yerel rehber eşit");
         
         reportNoDiff(report);
         return true;
      }
      
      return false;
   }
   
   private void reportNoDiff(String report){
      
      info.setReport(report);
      callBack();
   }
   
   private void callBack(){
      
      if(callback != null){
         
         if(_callInBackgroundThread){
            
            callback.onContactsDiffResult(info);
         }
         else{
            
            new Handler(Looper.getMainLooper()).post(() -> callback.onContactsDiffResult(info));
         }
      }
   }
   
   private boolean ifKeysNull(List<String> keys1, List<String> keys2){
      
      if(keys1 == null || keys2 == null){
         
         Timber.w("Bilgiler alınamadı");
         
         info.setReport("Bilgiler alınamadı");
         
         callBack();
         
         return true;
      }
      
      return false;
   }
   
   private List<String> getNumbers(){
      
      String[] cols = {ContactsContract.CommonDataKinds.Phone.NUMBER};
      
      val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            null,
            null,
            null
      );
      
      if(cursor == null) return null;
      
      Set<String> numbers = new HashSet<>(cursor.getCount());
      
      while(cursor.moveToNext()){
         
         val number = cursor.getString(cursor.getColumnIndex(cols[0]));
         
         numbers.add(Contacts.createKeyFromNumber(number));
      }
      
      cursor.close();
      return new ArrayList<>(numbers);
   }
   
   public interface Callback{
      
      void onContactsDiffResult(ContactsDiffInfo contactsDiffInfo);
   }
}