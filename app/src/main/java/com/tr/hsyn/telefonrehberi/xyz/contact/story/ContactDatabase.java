package com.tr.hsyn.telefonrehberi.xyz.contact.story;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.db.SimpleDBOperator;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.val;
import lombok.var;
import timber.log.Timber;


public class ContactDatabase extends SQLiteOpenHelper implements SimpleDBOperator, ContactStory{
   
   private              Context context;
   private static final String  DB_NAME = "saved_contacts_database";
   
   public ContactDatabase(Context context){
      
      super(context, DB_NAME, null, 1);
      this.context = context;
   }
   
   @Override
   public List<Contact> get(){
      
      final long n = System.currentTimeMillis();
      val        c = getContacts();
      
      Timber.d("Load Time : %dms [size=%d]", System.currentTimeMillis() - n, c.size());
      return c;
   }
   
   public List<Contact> getContacts(){
      
      final Cursor cursor = getReadableDatabase().rawQuery("select * from contacts_table", null);
      
      if(cursor == null) return new ArrayList<>(0);
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return new ArrayList<>(0);
      }
      
      
      final List<Contact> contacts = new ArrayList<>(cursor.getCount());
      
      final int nameCol         = cursor.getColumnIndex(ContactTable.NAME);
      final int numberCol       = cursor.getColumnIndex(ContactTable.NUMBER);
      final int savedDateCol    = cursor.getColumnIndex(ContactTable.SAVED_DATE);
      final int deletedDateCol  = cursor.getColumnIndex(ContactTable.DELETED_DATE);
      final int updatedDateCol  = cursor.getColumnIndex(ContactTable.UPDATED_DATE);
      final int lastLookDateCol = cursor.getColumnIndex(ContactTable.LAST_LOOK_DATE);
      final int descriptionCol  = cursor.getColumnIndex(ContactTable.DESCRIPTION);
      final int lookCountCol    = cursor.getColumnIndex(ContactTable.LOOK_COUNT);
      
      
      while(cursor.moveToNext()){
         
         final String name         = cursor.getString(nameCol);
         final String number       = cursor.getString(numberCol);
         final long   savedDate    = cursor.getLong(savedDateCol);
         final long   deletedDate  = cursor.getLong(deletedDateCol);
         final long   updatedDate  = cursor.getLong(updatedDateCol);
         final long   lastLookDate = cursor.getLong(lastLookDateCol);
         final String description  = cursor.getString(descriptionCol);
         final int    lookCount    = cursor.getInt(lookCountCol);
         
         final Contact contact = new Contact(name, number, savedDate, deletedDate, updatedDate, lookCount, lastLookDate, description);
         
         contacts.add(contact);
      }
      
      
      cursor.close();
      return contacts;
   }
   
   @Override
   public void close(){
      
      super.close();
   }
   
   @Override
   public void onConfigure(SQLiteDatabase db){
      
      super.onConfigure(db);
      
      //db.setForeignKeyConstraintsEnabled(true);
   }
   
   @Override
   public void onCreate(SQLiteDatabase sqLiteDatabase){
      
      sqLiteDatabase.execSQL(ContactTable.CREATE_CONTACTS_TABLE);
   }
   
   @Override
   public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1){
      
   }
   
   private boolean replace(Contact contact){
      
      val db = getWritableDatabase();
      
      return add(db, ContactTable.TABLE_NAME, setContactValues(contact));
   }
   
   private ContentValues setContactValues(Contact contact){
      
      final ContentValues values = new ContentValues();
      
      values.put(ContactTable.NUMBER, contact.getNumber());
      values.put(ContactTable.NAME, contact.getName());
      values.put(ContactTable.DELETED_DATE, contact.getDeletedDate());
      values.put(ContactTable.SAVED_DATE, contact.getSavedDate());
      values.put(ContactTable.LAST_LOOK_DATE, contact.getLastLookDate());
      values.put(ContactTable.LOOK_COUNT, contact.getLookCount());
      values.put(ContactTable.DESCRIPTION, contact.getDescription());
      values.put(ContactTable.UPDATED_DATE, contact.getUpdatedDate());
      
      return values;
   }
   
   @Override
   @Nullable
   public Contact get(String number){
      
      String       key    = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(number);
      final Cursor cursor = getReadableDatabase().rawQuery("select * from contacts_table where number=?", new String[]{key});
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      Contact contact = null;
      
      if(cursor.moveToNext()){
         
         final String name         = cursor.getString(cursor.getColumnIndex(ContactTable.NAME));
         final String _number      = cursor.getString(cursor.getColumnIndex(ContactTable.NUMBER));
         final long   savedDate    = cursor.getLong(cursor.getColumnIndex(ContactTable.SAVED_DATE));
         final long   deletedDate  = cursor.getLong(cursor.getColumnIndex(ContactTable.DELETED_DATE));
         final long   updatedDate  = cursor.getLong(cursor.getColumnIndex(ContactTable.UPDATED_DATE));
         final long   lastLookDate = cursor.getLong(cursor.getColumnIndex(ContactTable.LAST_LOOK_DATE));
         final String description  = cursor.getString(cursor.getColumnIndex(ContactTable.DESCRIPTION));
         final int    lookCount    = cursor.getInt(cursor.getColumnIndex(ContactTable.LOOK_COUNT));
         
         contact = new Contact(name, _number, savedDate, deletedDate, updatedDate, lookCount, lastLookDate, description);
      }
      
      
      cursor.close();
      return contact;
   }
   
   @Override
   public List<String> getNumbers(){
      
      val cursor = getReadableDatabase().rawQuery("select number from contacts_table", null);
      
      List<String> keys = new ArrayList<>(cursor.getCount());
      
      while(cursor.moveToNext()) keys.add(cursor.getString(cursor.getColumnIndex(ContactTable.NUMBER)));
      
      cursor.close();
      return keys;
   }
   
   @Override
   public boolean add(Contact contact){
      
      if(contact.getNumber() == null || contact.getNumber().trim().isEmpty()){
         
         Timber.w("Kaydedilmek istenen kişinin numarası yok");
         
         return false;
      }
      
      if(contact.getName() == null){
         
         contact.setName(contact.getNumber());
      }
      
      String key      = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(contact.getNumber());
      String coverKey = Stringx.overWrite(key);
      
      if(exist(key)){
         
         val existContact = get(key);
         
         if(existContact == null){
            
            Timber.w("Bu kayıt [%s] veri tabanında bulunmasına rağmen erişim başarısız oldu", coverKey);
            return false;
         }
         
         //???????????????????????????????????????????????????????
         Timber.d("Kişi mevcut : %s", existContact);
         
         if(existContact.getDeletedDate() == 0L){
            
            Timber.d("Veri tabanına eklenmek istenen bu kişi [%s] zaten var ve silinmemiş görünüyor.", coverKey);
            
            if(!existContact.getName().equals(contact.getName())){
               
               Timber.d("Ancak isimler farklı. Yani aynı numara farklı isimlerle rehbere kaydedilmiş, yeni isim güncel olacak");
               
               addDescription(existContact, Stringx.format("%-25s Farklı bir isim ile [%s --> %s] yeniden kaydedildi%n", Time.getDateTime(System.currentTimeMillis()), contact.getName(), existContact.getName()));
               existContact.setName(contact.getName());
               return update(existContact);
            }
            
            return false;
         }
         
         //???????????????????????????????????????????????????????
         Timber.d("Veri tabanına eklenmek istenen bu kişi daha silinmiş bir kişi [%s]. Silinme tarihi : %s. Bu bilgi kaybolacak", coverKey, Time.getShortDate(existContact.getDeletedDate()));
         
         if(!existContact.getName().equals(contact.getName())){
            
            Timber.d("Ayrıca kayıtlı kişi ile kaydedilmek istenen kişinin isimleri farklı. Bu yüzden kayıtlı olanın ismi yenisi ile güncellenecek. Bu değişiklik description alanında görünecek");
            
            addDescription(existContact, Stringx.format("%-25s Farklı bir isim ile [%s --> %s] yeniden kaydedildi%n", Time.getDateTime(System.currentTimeMillis()), contact.getName(), existContact.getName()));
            existContact.setName(contact.getName());
         }
         
         existContact.setDeletedDate(0L);
         
         if(existContact.getSavedDate() == 0L){
            
            existContact.setSavedDate(System.currentTimeMillis());
         }
         else{
            
            Timber.d("Kişinin daha önceki kaydedilme tarihi korundu : %s", Time.getShortDate(existContact.getSavedDate()));
         }
         
         return update(existContact);
      }
      
      contact.setNumber(key);
      
      val db = getWritableDatabase();
      
      contact.setSavedDate(System.currentTimeMillis());
      return add(db, ContactTable.TABLE_NAME, setContactValues(contact));
   }
   
   @Override
   public boolean add(String name, String number){
      
      Contact contact = new Contact(name, number);
      
      return add(contact);
   }
   
   @Override
   public boolean setDeleted(Contact contactDetail){
      
      long    date    = System.currentTimeMillis();
      Contact contact = get(contactDetail.getNumber());
      
      if(contact != null){
         
         if(contact.getDeletedDate() != 0L){
            
            Timber.d("[%s] daha önce silinmiş görünüyor. Bu yüzden silinme tarihi korunacak. Ancak bu bilgi açıklama olarak kaydedilecek", contact.getName());
            addDescription(contact, Stringx.format("%-25s Tekrar silinmek istendi%n", Time.getDateTime(System.currentTimeMillis())));
            update(contact);
            return true;
         }
         
         contactDetail.setDeletedDate(date);
         ContentValues values = new ContentValues();
         values.put(ContactTable.DELETED_DATE, date);
         
         return update(getWritableDatabase(), ContactTable.TABLE_NAME, String.format("%s=?", ContactTable.NUMBER), new String[]{contact.getNumber()}, values);
      }
      else{
         
         contact = new Contact(contactDetail.getName());
         contact.setDeletedDate(date);
         return add(contact);
      }
   }
   
   @Override
   public boolean update(IPhoneContact oldContact, IPhoneContact updatedContact){
      
      Logger.d("old : %s%nnew : %s", oldContact, updatedContact);
      
      String desc    = checkChanges(oldContact, updatedContact);
      long   time    = System.currentTimeMillis();
      var    number  = updatedContact.getNumber();
      var    number2 = oldContact.getNumber();
      
      number  = Stringx.emptyToNull(number);
      number2 = Stringx.emptyToNull(number2);
      
      //Kişi güncellenirken numarası silinmiş
      if(number == null && number2 != null){
         
         //Bu durumda kişiyi silindi olarak işaretlememiz lazım
         
         Logger.d("Kişi güncellenirken numarası silinmiş : %s", number2);
         
         val contact = get(number2);
         
         if(contact != null){
            
            contact.setDeletedDate(time);
            
            boolean b = update(contact);
            
            if(b){
               
               Logger.d("Numarası silinen kişi silinmiş olarak güncellendi");
            }
            else{
               
               Logger.d("Numarası silinen kişi silinmiş olarak güncellenemedi");
            }
            
            return b;
         }
         else{
            
            Logger.w("Numarası silinen kişi veri tabanında bulunamadı : %s", number2);
         }
      }
      
      //Numarası yokmuş eklenmiş
      if(number != null && number2 == null){
         
         Logger.w("//Numarası yokmuş eklenmiş");
         
         //Eğer eklenen numara veri tabanında kayıtlı ise
         //Silinmemiş olarak güncelle
         val contact = get(number);
         
         if(contact != null){
            
            contact.setUpdatedDate(time);
            contact.setDeletedDate(0L);
            
            return update(contact);
         }
         else{
            
            Logger.w("Eklenen numara kayıtlı değil : %s", number);
            
            return add(new Contact(updatedContact.getName(), number));
         }
      }
      
      if(number == null) return false;
      
      //Eski numarayla alıyoruz
      val     key           = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(number2);
      Contact contactStored = get(key);
      
      if(contactStored != null){
         
         if(delete(key)){
            
            val key2    = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(number);
            val contact = new Contact(contactStored);
            contact.setNumber(key2);
            contact.setName(updatedContact.getName());
            contact.setUpdatedDate(time);
            addDescription(contact, desc);
            return add(contact);
         }
         else{
            
            Logger.w("Kişi yenilenmek için silinemedi : %s", key);
         }
         
         return false;
      }
      
      //Buraya gelmemesi lazım
      
      contactStored = new Contact(updatedContact.getName(), key);
      contactStored.setUpdatedDate(System.currentTimeMillis());
      contactStored.setSavedDate(System.currentTimeMillis());
      addDescription(contactStored, desc);
      
      return add(contactStored);
   }
   
   public boolean delete(String keyNumber){
      
      keyNumber = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(keyNumber);
      
      return delete(
            getWritableDatabase(),
            ContactTable.TABLE_NAME,
            Stringx.format("%s=?", ContactTable.NUMBER),
            new String[]{keyNumber});
   }
   
   @Override
   public boolean update(Contact contact){
      
      return update(getWritableDatabase(), ContactTable.TABLE_NAME, String.format("%s=?", ContactTable.NUMBER), new String[]{contact.getNumber()}, setContactValues(contact));
   }
   
   private boolean update(String key, ContentValues values){
      
      return update(getWritableDatabase(), ContactTable.TABLE_NAME, String.format("%s=?", ContactTable.NUMBER), new String[]{key}, values);
   }
   
   @Override
   public boolean incLookCount(String key){
      
      key = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(key);
      Contact contact = get(key);
      
      if(contact != null){
         
         contact.incLookCount();
         contact.setLastLookDate(System.currentTimeMillis());
         addInkDescription(contact);
         
         ContentValues values = new ContentValues();
         values.put(ContactTable.LOOK_COUNT, contact.getLookCount());
         values.put(ContactTable.LAST_LOOK_DATE, contact.getLastLookDate());
         values.put(ContactTable.DESCRIPTION, contact.getDescription());
         
         return update(key, values);
      }
      else{
         
         if(context == null) return false;
         
         val name = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.getContactName(context.getContentResolver(), key);
         contact = new Contact(name, key);
         contact.incLookCount();
         contact.setLastLookDate(System.currentTimeMillis());
         addInkDescription(contact);
         return add(contact);
      }
   }
   
   private void addInkDescription(Contact contact){
      
      String desc = contact.getDescription();
      String add  = Stringx.format("%-25s Bakıldı : %d\n", Time.getShortDateShortTime(contact.getLastLookDate()), contact.getLookCount());
      
      if(desc != null){
         
         contact.setDescription(desc + add);
      }
      else{
         
         contact.setDescription(add);
      }
   }
   
   @Override
   public int getLookCount(String key){
      
      Contact contact = get(key);
      
      if(contact != null) return contact.getLookCount();
      
      return -1;
   }
   
   @Override
   public boolean exist(String number){
      
      String       key    = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.createKeyFromNumber(number);
      final Cursor cursor = getReadableDatabase().rawQuery("select number from contacts_table where number=?", new String[]{key});
      
      if(cursor == null) return false;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return false;
      }
      
      cursor.close();
      return true;
   }
   
   /**
    * Yapılan değişikliklere bak ve rapor et.
    *
    * @param oldContact     Güncellemeden önceki nesne
    * @param updatedContact Güncellemeden sonraki nesne
    * @return Neyin değiştiğiyle ilgili kısa bir rapor. Şimdilik sadece isim ve numara kontrol ediliyor.
    */
   @NonNull
   private String checkChanges(IPhoneContact oldContact, IPhoneContact updatedContact){
      
      long                now           = System.currentTimeMillis();
      final String        time          = Time.getShortDateShortTime(now);
      final StringBuilder stringBuilder = new StringBuilder();
      
      if(!Objects.equals(oldContact.getName(), updatedContact.getName())){
         
         String change = Stringx.format("%-25s İsim değişti : %s --> %s\n", time, oldContact.getName(), updatedContact.getName());
         
         Timber.d(change);
         stringBuilder.append(change);
      }
      
      if(!ContactStory.equalsNumbers(oldContact.getNumberList(), updatedContact.getNumberList())){
         
         String change = Stringx.format("%-25s Numara değişti : %s --> %s\n", time, oldContact.getNumberList(), updatedContact.getNumberList());
         
         Timber.d(change);
         stringBuilder.append(change);
      }
      
      if(stringBuilder.length() == 0){
         
         return Stringx.format("%-25s Güncellendi\n", time);
      }
      
      return stringBuilder.toString();
   }
   
   /**
    * Kişinin bilgilerine bilgi ekle.
    *
    * @param stored      Kişi
    * @param description Eklenecek bilgi
    */
   private static void addDescription(Contact stored, String description){
      
      if(description == null) return;
      
      String desc = stored.getDescription();
      
      if(desc != null){
         
         stored.setDescription(desc + description);
      }
      else{
         
         stored.setDescription(description);
      }
      
      
   }
   
   
}
