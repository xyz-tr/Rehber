package com.tr.hsyn.telefonrehberi.xyz.contact;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.account.AccountsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge.ContactsMergeActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge.MergedContact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.val;
import lombok.var;
import timber.log.Timber;


/**
 * <h1>Contacts</h1>
 * <p>
 * Tüm rehber işlemlerini görecek.
 *
 * @author hsyn 2019-07-15 12:04:27
 */
public class Contacts implements ContactColumns{
   
   private Contacts(){}
   
   /**
    * Google hesapları arasından en uygun olanını seçer.
    * Eğer google hesabı yoksa {@code null}.
    * Seçim, en çok kaydı olan hesap sorunsalı ile çalışır.
    *
    * @param context Context
    * @return Hesap
    */
   public static Account getProperAccount(Context context, List<Account> accounts){
      
      if(accounts == null) return null;
      
      return ContactsMergeActivity.selectProperAccount(context, accounts.toArray(new Account[0]));
   }
   
   public static Account getGlobalProperAccount(Context context){
      
      if(context == null) return null;
      
      val accounts = getAccounts(context, new Account(null, "com.google"));
      
      if(accounts.length == 0) return null;
      
      if(accounts.length == 1) return accounts[0];
   
      Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

      val map = Arrays.stream(accounts).collect(Collectors.toMap(account -> account, account -> AccountsActivity.getContactCount(context, uri, account)));
      
      Map.Entry<Account, Integer> max = null;
      
      for(val item : map.entrySet()){
         
         if(max == null) {
            
            max = item;
            continue;
         }
         
         if(max.getValue() < item.getValue()) max = item;
      }
      
      if(max == null) return null;
      
      Logger.d("Winner account : %s [size=%d]", max.getKey(), max.getValue());
      
      return max.getKey();
   }
   
   public static Throwable openNewContactActivity(Activity activity, int requestCode){
      
      try{
         
         Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
         intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
         intent.putExtra("finishActivityOnSaveCompleted", true);
         
         activity.startActivityForResult(intent, requestCode);
      }
      catch(Exception e){
         
         return e;
      }
      
      return null;
   }
   
   public static boolean updateContact(Context context, String name, String newPhoneNumber){
      
      ArrayList<ContentProviderOperation> ops = new ArrayList<>();
      
      String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                     ContactsContract.Data.MIMETYPE + " = ? AND " +
                     String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";
      
      String[] params = new String[]{name,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)};
      
      ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                      .withSelection(where, params)
                                      .withValue(ContactsContract.CommonDataKinds.Phone.DATA, newPhoneNumber)
                                      .build());
      try{
         context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
         return true;
         
      }
      catch(Exception e){
         e.printStackTrace();
         return false;
      }
   }
   
   public static boolean addContact(Context context, String name, String number){
      
      ArrayList<ContentProviderOperation> ops                   = new ArrayList<>();
      int                                 rawContactInsertIndex = ops.size();
      //ContentProviderResult[]             results               = null;
      
      ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                      .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                                      .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                                      .build());
      
      
      ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                      .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                      .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                      .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                      .build());
      
      
      ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                      .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                      .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                      .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                                      .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                      .build());
      
      
      try{
         context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
         return true;
      }
      catch(RemoteException | OperationApplicationException e){
         e.printStackTrace();
      }
      return false;
   }
   
   public static boolean addContact(@NonNull Context context, String name, String number, @Nullable Account account){
      
      ArrayList<ContentProviderOperation> ops                   = new ArrayList<>();
      int                                 rawContactInsertIndex = ops.size();
      //ContentProviderResult[]             results               = null;
      
      ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                                      .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, account != null ? account.type : null)
                                      .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account != null ? account.name : null)
                                      .build());
      
      
      ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                      .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                      .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                                      .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                                      .build());
      
      
      ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                                      .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                                      .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                                      .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number)
                                      .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                      .build());
      
      
      try{
         context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
         return true;
      }
      catch(RemoteException | OperationApplicationException e){
         e.printStackTrace();
      }
      return false;
   }
   
   public static LinkedList<SimpleContact> getNumbers(Context context){
      
      if(context == null) return new LinkedList<>();
      
      final String[] column = {
            
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
         
      };
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            column,
            null,
            null,
            null);
      
      if(cursor == null) return new LinkedList<>();
      
      LinkedList<SimpleContact> list = new LinkedList<>();
      
      while(cursor.moveToNext()){
         
         String raw       = cursor.getString(cursor.getColumnIndex(column[0]));
         String name      = cursor.getString(cursor.getColumnIndex(column[1]));
         String number    = cursor.getString(cursor.getColumnIndex(column[2]));
         String contactId = cursor.getString(cursor.getColumnIndex(column[3]));
         
         list.push(new SimpleContact(raw, name, number, contactId));
      }
      
      cursor.close();
      return list;
   }
   
   /**
    * Verilen numaranın rehberde kayıtlı ismini döndür.
    *
    * @param contentResolver contentResolver
    * @param number  Telefon numarası
    * @return İsim. Kişi bulunamazsa {@code null}
    */
   @Nullable
   public static String getContactName(@NonNull ContentResolver contentResolver, @NonNull String number){
      
      if(number.isEmpty()){
         
         Timber.d("number empty");
         return null;
      }
      
      String[] cols = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY, ContactsContract.CommonDataKinds.Phone.NUMBER};
      
      final Cursor cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            null,
            null,
            null
      
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      String    name      = null;
      final int numberCol = cursor.getColumnIndex(cols[1]);
      
      while(cursor.moveToNext()){
         
         String _number = cursor.getString(numberCol);
         
         if(Contacts.matchNumbers(_number, number)){
            
            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY));
            break;
         }
      }
      
      cursor.close();
      return name;
   }
   
   /**
    * İki telefon numarasının eşit olup olmadığını bulacak.
    * Değişik numara formatları olduğu için,
    * iki numaranın (gereksiz karakterler (+, *, #, 0-9 harici) çıkarıldıktan sonra) karakter sayıları
    * eşit değilse, büyük olanın küçük olan içinde geçip geçmediğine bakılacak.
    * Karakter sayısı büyük olan numara, diğer numaranın tüm karakterlerini aynı sırada içeriyorsa {@code true} döndürecek.
    *
    * @param n1 Numara1
    * @param n2 Numara2
    * @return İki numara eşitse {@code true}, değilse {@code false}
    */
   public static boolean matchNumbers(String n1, String n2){
      
      n1 = normalizeNumber(n1);
      n2 = normalizeNumber(n2);
      
      if(n1.length() == n2.length()){
         
         return n1.equals(n2);
      }
      
      boolean match;
      
      if(n1.length() > n2.length()){
         
         match = Stringx.isMatch(n1, n2);
      }
      else{
         
         match = Stringx.isMatch(n2, n1);
      }
      
      if(match){
         
         if(Math.abs(n1.length() - n2.length()) > 3) return false;
      }
      
      return match;
   }
   
   /**
    * Verilen numaranın içindeki gereksiz karakterleri (+, *, #, 0-9 harici) silecek.
    *
    * @param number Telefon numarası
    * @return Eğer {@code number null} ise boş string, değilse gereksiz karakterlerin silindiği numara.
    */
   @NonNull
   public static String normalizeNumber(String number){
      
      if(number == null){
         return Stringx.EMPTY;
      }
      
      //noinspection RegExpRedundantEscape
      return number.replaceAll("[^\\+\\*#0-9]", "");
   }
   
   /**
    * Numarayı karşılaştırmalar için uygun hale getirir.
    * 
    * @param number numara
    * @return numara
    */
   public static String formatNumber(String number){
   
      number = normalizeNumber(number);
   
      //905434937530
      if(number.startsWith("9") && number.length() == 12){
         
         return number.substring(2);//5434937530
      }
      
      //05434937530
      if(number.startsWith("0") && number.length() == 11){
   
         return number.substring(1);//5434937530
      }
      
      return number;
   }
   
   /**
    * Sistemde kayıtlı tüm hesapları al.
    *
    * @param context context
    * @return hesaplar
    */
   @NonNull
   public static Account[] getAccounts(@NonNull final Context context){
      
      return convertAccounts(AccountManager.get(context).getAccounts());
   }
   
   public static Account[] getAccounts(@NonNull final Context context, Account account){
      
      return convertAccounts(AccountManager.get(context).getAccountsByType(account.type));
   }
   
   public static Account[] convertAccounts(@NonNull final android.accounts.Account[] accounts){
      
      Account[] _account = new Account[accounts.length];
      
      for(int i = 0; i < accounts.length; i++){
         _account[i] = new Account(accounts[i].name, accounts[i].type);
      }
      
      return _account;
      
   }
   
   @NonNull
   public static Account convertAccount(@NonNull final android.accounts.Account account){
      
      return new Account(account.name, account.type);
      
   }
   
   public static android.accounts.Account convertAccount(@NonNull final Account account){
      
      return new android.accounts.Account(account.name, account.type);
   }
   
   /**
    * Belirli bir türe ait hesapları al.
    *
    * @param context     context
    * @param accountType İstenen hesap türü
    * @return Hesaplar. Yoksa boş dizi
    */
   @NonNull
   public static Account[] getPhoneAccounts(@NonNull final Context context, @NonNull final String accountType){
      
      return convertAccounts(AccountManager.get(context).getAccountsByType(accountType));
   }
   
   public static boolean deleteContact(Context context, String lookupKey){
      
      val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
      
      return context.getContentResolver().delete(uri, null, null) > 0;
   }
   
   @Nullable
   public static String getLookupKey(Context context, String contactId){
      
      val cursor = context.getContentResolver().query(
            
            ContactsContract.Contacts.CONTENT_URI,
            new String[]{ContactsContract.Contacts.LOOKUP_KEY},
            ContactsContract.Contacts._ID + " = ?",
            new String[]{contactId},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      String key = null;
      
      if(cursor.moveToFirst()){
         
         key = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
      }
      
      cursor.close();
      return key;
   }
   
   /**
    * Sistem kayıtlarından kişiyi sil.
    *
    * @param context   context
    * @param contactId contact id
    * @return Silme başarılı olursa {@code true}.
    */
   public static boolean delete(final Context context, final String contactId){
      
      if(context == null){
         return false;
      }
      
      final ArrayList<ContentProviderOperation> ops = new ArrayList<>();
      
      final ContentResolver cr = context.getContentResolver();
      
      ops.add(ContentProviderOperation
                    .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(ContactsContract.RawContacts.CONTACT_ID + " = ?", new String[]{contactId})
                    .build());
      
      try{
         
         ContentProviderResult[] results = cr.applyBatch(ContactsContract.AUTHORITY, ops);
         ops.clear();
         return results[0].count != 0;
      }
      catch(RemoteException | OperationApplicationException e){
         e.printStackTrace();
      }
      
      return false;
   }
   
   /**
    * Verilen uri ile kişiyi al.
    *
    * @param context    Context
    * @param contactUri Uri
    * @return Kişi
    */
   @Nullable
   public static IMainContact getContact(final Context context, @NonNull final Uri contactUri){
      
      if(context == null){
         return null;
      }
      
      String[] cols = {
            
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts._ID
      };
      
      if(contactUri.toString().contains("raw_contacts")){
         
         String id = getContactId(context.getContentResolver(), contactUri);
         
         if(id != null){
            
            return getContact(context, id);
         }
         
         return null;
      }
      
      final Cursor cursor = context.getContentResolver().query(
            
            contactUri,
            cols,
            null,
            null,
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            
            cursor.close();
         }
         
         return null;
      }
      
      cursor.moveToFirst();
      
      IMainContact contact = IMainContact.create(
            cursor.getString(cursor.getColumnIndex(cols[0])),
            cursor.getString(cursor.getColumnIndex(cols[1])));
      
      cursor.close();
      return contact;
   }
   
   /**
    * Verilen raw contact uri ile contact id değerini bul.
    *
    * @param contentResolver       cr
    * @param rawContactUri Raw Contact Uri
    * @return Contact id
    */
   private static String getContactId(@NonNull final ContentResolver contentResolver, @NonNull final Uri rawContactUri){
      
      final Cursor cursor = contentResolver.query(
            
            rawContactUri,
            new String[]{ContactsContract.RawContacts.CONTACT_ID},
            null,
            null,
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            
            cursor.close();
         }
         
         return null;
      }
      
      String id = null;
      
      while(cursor.moveToNext()){
   
         id = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.CONTACT_ID));
   
         Timber.d("Bulunan id : %s", id);
      }
      
      
      cursor.close();
      return id;
   }
   
   /**
    * Verilen contact id ile kişiyi bul.
    *
    * @param context   Context
    * @param contactId Contact id
    * @return Kişi
    */
   @Nullable
   public static IMainContact getContact(@NonNull final Context context, @NonNull final String contactId){
      
      String[] cols = {
            
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY
      };
      
      
      final Cursor cursor = context.getContentResolver().query(
            
            ContactsContract.Contacts.CONTENT_URI,
            cols,
            cols[0] + "=?",
            new String[]{contactId},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      cursor.moveToFirst();
      
      IMainContact contact = IMainContact.create(
            cursor.getString(cursor.getColumnIndex(cols[1])),
            cursor.getString(cursor.getColumnIndex(cols[0]))
      );
      
      cursor.close();
      return contact;
   }
   
   public static String getContactIdFromLookup(@NonNull final Context context, @NonNull final String lookupKey){
      
      Uri contactUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
      
      final Cursor cursor = context.getContentResolver().query(
            
            contactUri,
            new String[]{ContactsContract.Contacts._ID},
            null,
            null,
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            
            cursor.close();
         }
         
         return null;
      }
      
      String id = null;
      
      if(cursor.moveToFirst()){
         
         id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
      }
      
      cursor.close();
      return id;
   }
   
   /**
    * Kişinin email adreslerini döndür.
    *
    * @param context   Context
    * @param contactId Contact id
    * @return Email listesi. Yoksa {@code null}
    */
   public static Email[] getEmails(Context context, String contactId){
      
      String[] EMAIL_COLUMNS = {
            
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.ADDRESS,
            ContactsContract.CommonDataKinds.Email.TYPE};
      
      final Uri      uri       = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
      final String   selection = Stringx.format("%s = ?", EMAIL_COLUMNS[0]);
      final String[] args      = {contactId};
      
      
      final Cursor cursor = context.getContentResolver().query(
            uri,
            EMAIL_COLUMNS,
            selection,
            args,
            null,
            null
      );
      
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null) cursor.close();
         
         return null;
      }
      
      Email[] emails = new Email[cursor.getCount()];
      
      final int emailCol = cursor.getColumnIndex(EMAIL_COLUMNS[1]);
      final int typeCol  = cursor.getColumnIndex(EMAIL_COLUMNS[2]);
      
      int i = 0;
      
      while(cursor.moveToNext()){
         
         final String email = cursor.getString(emailCol);
         final int    type  = cursor.getInt(typeCol);
         
         emails[i++] = new Email(email, type);
      }
      
      cursor.close();
      return emails;
   }
   
   public static Uri addEmail(Context context, String rawId, String email){
      
      //raw contact id zorunlu
      
      if(context == null || rawId == null || rawId.trim().isEmpty() || email == null || email.trim().isEmpty())
         return null;
      
      ContentValues values = new ContentValues();
      
      values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
      values.put(ContactsContract.CommonDataKinds.Email.RAW_CONTACT_ID, rawId);
      values.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);
      values.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
      
      return context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
   }
   
   /**
    * Verilen id ile kişinin kayıtlı bir numarası olup olmadığa bak.
    *
    * @param context   Context
    * @param contactId id
    * @return Verilen id'ye ait kişinin telefon numarası varsa {@code true}
    */
   public static boolean hasNumber(@NonNull final Context context, @NonNull final String contactId){
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?",
            new String[]{contactId},
            null
      );
      
      if(cursor == null){
         return false;
      }
      
      if(cursor.getCount() == 0){
         
         Timber.w("Numarası yok : %s", contactId);
         cursor.close();
         return false;
      }
      
      cursor.close();
      return true;
   }
   
   /**
    * Kişilerin telefon numaralarının içindeki gereksiz karakterleri sil ve güncelle.
    *
    * @param context Context
    */
   public static void updateNumbers(Context context){
      
      if(context == null){
         return;
      }
      
      String[] cols = {
            
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
         
      };
      
      String[] DATA_COLUMNS = {
            
            ContactsContract.Data.DATA1,
            ContactsContract.Data.CONTACT_ID
         
      };
      
      ContentResolver contentResolver = context.getContentResolver();
      Uri             uri             = ContactsContract.Data.CONTENT_URI;
      
      Cursor cursor = contentResolver.query(
            
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            null,
            null,
            null
      );
      
      if(cursor == null){
         return;
      }
      
      while(cursor.moveToNext()){
         
         String id     = cursor.getString(cursor.getColumnIndex(cols[0]));
         String number = cursor.getString(cursor.getColumnIndex(cols[1]));
         
         String updatedNumber = normalizeNumber(number);
         
         ContentValues values = new ContentValues();
         values.put(cols[1], updatedNumber);
         
         String   where = DATA_COLUMNS[1] + "='" + ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE + "' and " + DATA_COLUMNS[1] + "=?";
         String[] args  = {id};
         
         int row = contentResolver.update(uri, values, where, args);
         
         if(row != 0){
            
            Timber.d("number updated : %s - %s", number, updatedNumber);
         }
         else{
            
            Timber.d("no setUpdated : %s", number);
         }
         
      }
      
      cursor.close();
      
   }
   
   public static Number getNumber(Context context, String rawId){
      
      if(context == null){
         return null;
      }
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            new String[]{ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID, ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.NUMBER},
            u.format("%s = ?", ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID),
            new String[]{rawId},
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      if(cursor.getCount() == 0){
         
         Timber.d("Numarası yok : %s", rawId);
         return null;
      }
      
      Number number = null;
      
      if(cursor.moveToNext()){
         
         String  _number   = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
         boolean isPrimary = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY)) == 1;
         int     type      = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
         
         number = new Number(_number, isPrimary, type);
         
      }
      
      cursor.close();
      return number;
   }
   
   public static Cursor getDataDetails(Context context, String contactId){
      
      return context.getContentResolver().query(
            ContactsContract.Data.CONTENT_URI,
            null,
            u.format("%s = ?", ContactsContract.Data.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      
   }
   
   public static Cursor getRawDetails(Context context, String contactId){
      
      return context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            null,
            u.format("%s = ?", ContactsContract.RawContacts.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      
   }
   
   public static void testUri(Cursor cursor, Context context){
      
      String[] columNames = cursor.getColumnNames();
      
      StringBuilder s = new StringBuilder();
      
      try{
         
         while(cursor.moveToNext()){
            
            for(String column : columNames){
               
               String value = cursor.getString(cursor.getColumnIndex(column));
               
               s.append(u.format("%40s : %s%n", column, value));
               
            }
            s.append("================================================================\n");
         }
         
         cursor.close();
      }
      catch(Exception e){
         
         Timber.w(e.getMessage());
      }
      
      
      File file = new File(context.getFilesDir(), "data.txt");
      
      try{
         file.createNewFile();
         
         FileOutputStream out = new FileOutputStream(file, true);
         
         out.write("****************************************************\n".getBytes());
         out.write(s.toString().getBytes());
         
         out.close();
      }
      catch(IOException e){
         e.printStackTrace();
      }
      
   }
   
   public static String getNameRawContactIdFromProfile(Context context){
      
      if(context == null){
         return null;
      }
      
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Profile.CONTENT_URI,
            new String[]{ContactsContract.Profile.NAME_RAW_CONTACT_ID},
            null,
            null,
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         return null;
      }
      
      cursor.moveToFirst();
      
      String rawId = cursor.getString(cursor.getColumnIndex(ContactsContract.Profile.NAME_RAW_CONTACT_ID));
      
      cursor.close();
      return rawId;
   }
   
   public static boolean existRaw(Context context, String contactId){
      
      if(context == null || contactId == null || contactId.trim().isEmpty()){
         return false;
      }
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            new String[]{ContactsContract.RawContacts.ACCOUNT_NAME, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.RawContacts.CONTACT_ID},
            u.format("%s = ?", ContactsContract.RawContacts.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            
            cursor.close();
         }
         
         return false;
      }
      
      
      cursor.close();
      return true;
   }
   
   public static boolean existData(Context context, String contactId){
      
      if(context == null || contactId == null || contactId.trim().isEmpty()){
         return false;
      }
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            new String[]{ContactsContract.RawContacts.ACCOUNT_NAME, ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.RawContacts.CONTACT_ID},
            u.format("%s = ?", ContactsContract.RawContacts.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            
            cursor.close();
         }
         
         return false;
      }
      
      
      cursor.close();
      return true;
   }
   
   /**
    * Rehberde facebook hesabı olan kişilerin id'lerini döndür.
    *
    * @param context Context
    * @return id listesi. Yoksa {@code null}
    */
   public static List<String> getMessengerContactsIds(@Nullable final Context context){
      
      if(context == null){
         return null;
      }
      
      String accountName = "Messenger";
      
      String[] cols = {
            
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.ACCOUNT_NAME
      };
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            cols[1] + "=?",
            new String[]{accountName},
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      List<String> ids = new ArrayList<>();
      
      if(cursor.getCount() == 0){
         return ids;
      }
      
      int idCol = cursor.getColumnIndex(cols[0]);
      
      while(cursor.moveToNext()){
         ids.add(cursor.getString(idCol));
      }
      
      cursor.close();
      return ids;
   }
   
   /**
    * Facebook hesapları olan kişileri al.
    *
    * @param context Context
    * @return Kişiler
    */
   @NonNull
   public static List<IMainContact> getMessengerContacts(@Nullable final Context context){
      
      List<IMainContact> contacts = new ArrayList<>();
      
      if(context == null){
         return contacts;
      }
      
      List<String> ids = getMessengerContactIdList(context);
      
      String[] cols = {
            
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.SORT_KEY_PRIMARY,
            ContactsContract.Contacts._ID
      };
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            cols,
            u.format("%s IN (%s)", cols[2], joinToString(ids)),
            null,
            cols[1]
      );
      
      if(cursor == null){
         return contacts;
      }
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return contacts;
      }
      
      
      int contactIdCol = cursor.getColumnIndex(cols[2]);
      int nameCol      = cursor.getColumnIndex(cols[0]);
      
      while(cursor.moveToNext()){
         
         contacts.add(IMainContact.create(
               cursor.getString(contactIdCol),
               cursor.getString(nameCol)));
      }
      
      cursor.close();
      return contacts;
   }
   
   public static List<String> getMessengerContactIdList(Context context){
      
      List<String> ids = new ArrayList<>();
      
      if(context == null){return ids;}
      
      String mimetype = "vnd.android.cursor.item/com.facebook.messenger.chat";
      Uri    uri      = ContactsContract.Data.CONTENT_URI;
      
      String[] cols = {
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.MIMETYPE
      };
      
      String selection = u.format("%s='%s'", cols[1], mimetype);
      
      Cursor cursor = context.getContentResolver().query(
            uri,
            cols,
            selection,
            null,
            null);
      if(cursor == null || cursor.getCount() == 0){return ids;}
      
      
      while(cursor.moveToNext()){
         
         ids.add(cursor.getString(cursor.getColumnIndex(cols[0])));
      }
      
      cursor.close();
      return ids;
   }
   
   @Nullable
   public static <T> String joinToString(List<T> collection){
      
      if(collection == null) return null;
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for(int i = 0; i < collection.size(); i++){
         
         T t = collection.get(i);
         
         stringBuilder.append(t.toString());
         
         if(i != collection.size() - 1){
            
            stringBuilder.append(",");
         }
         
      }
      
      return stringBuilder.toString();
   }
   
   /**
    * İstenen hesaba ait kişileri döndür.
    * Birden fazla numarası olanlar ayrı bir kişi olarak ele alınacak.
    *
    * @param context Context
    * @param account İstenen hesap
    * @return Kişiler
    */
   public static List<ISimpleContact> getContactsForSearch(@NonNull final Context context, @Nullable final Account account){
      
      if(account == null || account.name.trim().isEmpty()){
         
         return getContactsForSearch(context);
      }
      
      String[] cols = {
            
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY,
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
            ContactsContract.CommonDataKinds.Phone.NUMBER
      };
      
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            cols[4] + "=? and " + cols[5] + "=?",
            new String[]{account.name, account.type},
            cols[3]
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      final List<ISimpleContact> contacts = new ArrayList<>(cursor.getCount());
      
      final int idCol        = cursor.getColumnIndex(cols[0]);
      final int contactIdCol = cursor.getColumnIndex(cols[1]);
      final int nameCol      = cursor.getColumnIndex(cols[2]);
      final int numberCol    = cursor.getColumnIndex(cols[6]);
      
      while(cursor.moveToNext()){
         
         String id        = cursor.getString(idCol);
         String name      = cursor.getString(nameCol);
         String contactId = cursor.getString(contactIdCol);
         String number    = cursor.getString(numberCol);
         
         ISimpleContact contact = ISimpleContact.create(id, name, number, contactId);
         
         if(!contacts.contains(contact)) contacts.add(contact);
      }
      
      cursor.close();
      return contacts;
   }
   
   /**
    * Rehber'de arama yapıldığında kullanılacak.
    * Kişinin birden fazla numarası olsa bile ayrı görünecek.
    *
    * @param context context
    * @return Kişiler
    */
   private static List<ISimpleContact> getContactsForSearch(@NonNull final Context context){
      
      String[] rawCols = {
            
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY
      };
      
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            rawCols,
            null,
            null,
            rawCols[4]
      );
      
      if(cursor == null){
         return new ArrayList<>(0);
      }
      
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return new ArrayList<>(0);
      }
      
      final List<ISimpleContact> contacts = new ArrayList<>(cursor.getCount());
      
      final int idCol     = cursor.getColumnIndex(rawCols[0]);
      final int rawIdCol  = cursor.getColumnIndex(rawCols[1]);
      final int nameCol   = cursor.getColumnIndex(rawCols[2]);
      final int numberCol = cursor.getColumnIndex(rawCols[3]);
      
      while(cursor.moveToNext()){
         
         final String number = cursor.getString(numberCol);
         final String id     = cursor.getString(idCol);
         final String name   = cursor.getString(nameCol);
         final String rawId  = cursor.getString(rawIdCol);
         
         ISimpleContact contact = ISimpleContact.create(rawId, name, number, id);
         
         if(!contacts.contains(contact)) contacts.add(contact);
         
      }
      
      cursor.close();
      return contacts;
   }
   
   /**
    * İstenen id'lere ait kişileri döndür.
    *
    * @param context       Context
    * @param contactIdList İstenen id'ler
    * @return Kişiler
    */
   @Nullable
   public static List<ISimpleContact> getContactsForSearch(@Nullable final Context context, List<String> contactIdList){
      
      if(context == null){
         return null;
      }
      
      String[] cols = {
            
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
         
      };
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            u.format("%s IN (%s)", cols[4], joinToString(contactIdList)),
            null,
            cols[1]
      );
      
      if(cursor == null){
         return null;
      }
      
      List<ISimpleContact> contacts = new ArrayList<>();
      
      int idCol        = cursor.getColumnIndex(cols[0]);
      int nameCol      = cursor.getColumnIndex(cols[2]);
      int numberCol    = cursor.getColumnIndex(cols[3]);
      int contactIdCol = cursor.getColumnIndex(cols[4]);
      
      
      while(cursor.moveToNext()){
         
         contacts.add(
               new SimpleContact(
                     cursor.getString(idCol),
                     cursor.getString(nameCol),
                     cursor.getString(numberCol),
                     cursor.getString(contactIdCol)
               )
         );
         
      }
      
      cursor.close();
      return contacts;
   }
   
   /**
    * Sadece ve sadece telefon numarasını değiştirmek-güncellemek için.
    * Numaranın türü veya başka bir bilgisi değiştirilmez, sadece "<u>numara</u>".
    *
    * @param context   appcontext
    * @param number    Değişecek olan numara.
    * @param newNumber Kaydedilecek yeni numara.
    * @return Eğer güncelleme yapılırsa true
    */
   public static boolean updateContactNumber(final Context context, String number, @Nullable String newNumber){
      
      if(context == null || number == null || number.trim().isEmpty()){
         return false;
      }
      
      if(newNumber != null && newNumber.trim().isEmpty()){
         newNumber = null;
      }
      
      if(newNumber == null){
         
         //todo eğer numara null ise numarayı ve diğer bilgileri başka bir metotla sil
         //şimdilik false
         return false;
      }
      
      String[] PHONE_COLUMNS = {
            
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
         
      };
      
      String[] DATA_COLUMNS = {
            
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID
         
      };
      
      ContentValues values = new ContentValues();
      values.put(PHONE_COLUMNS[1], newNumber);
      
      String where = u.format(
            "%s = '%s' AND %s = ?",
            DATA_COLUMNS[0],
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            DATA_COLUMNS[1]);
      
      String[] args = {number};
      
      int rows = context.getContentResolver().update(ContactsContract.Data.CONTENT_URI, values, where, args);
      
      if(rows != 0){
         
         Timber.d("updated %d rows", rows);
         return true;
      }
      
      Timber.d("no updated");
      
      return false;
   }
   
   public static boolean updateNameAndNumber(final Context context, String number, String newName, String newNumber){
      
      if(context == null || number == null || number.trim().isEmpty()){
         return false;
      }
      
      if(newNumber != null && newNumber.trim().isEmpty()){
         newNumber = null;
      }
      
      if(newNumber == null){
         return false;
      }
      
      String[] PHONE_COLUMNS = {
            
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
         
      };
      
      String[] DATA_COLUMNS = {
            
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID
         
      };
      
      
      String contactId = getId(context, number);
      
      if(contactId == null){
         return false;
      }
      
      //selection for name
      String where = String.format(
            "%s = '%s' AND %s = ?",
            DATA_COLUMNS[0], //mimetype
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
            DATA_COLUMNS[1]/*contactId*/);
      
      String[] args = {contactId};
      
      ArrayList<ContentProviderOperation> operations = new ArrayList<>();
      
      operations.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                    .withSelection(where, args)
                                    .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, newName)
                                    .build()
      );
      
      //change selection for number
      where = String.format(
            "%s = '%s' AND %s = ?",
            DATA_COLUMNS[0],//mimetype
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
            DATA_COLUMNS[1]/*number*/);
      
      //change args for number
      args[0] = number;
      
      operations.add(
            ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                                    .withSelection(where, args)
                                    .withValue(DATA_COLUMNS[1]/*number*/, newNumber)
                                    .build()
      );
      
      try{
         
         ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
         
         for(ContentProviderResult result : results){
            
            Log.d("Update Result", result.toString());
         }
         
         return true;
      }
      catch(Exception e){
         e.printStackTrace();
      }
      
      return false;
   }
   
   /**
    * Verilen telefon numarasına sahip kişinin contact id değerini al.
    *
    * @param context Context
    * @param number  Telefon numarası
    * @return Contact id. Yoksa {@code null}
    */
   @Nullable
   private static String getId(final Context context, String number){
      
      if(context == null){
         return null;
      }
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.NUMBER},
            null,
            null,
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         return null;
      }
      
      final int numberCol = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
      final int idCol     = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
      String    id        = null;
      
      while(cursor.moveToNext()){
         
         final String _number = cursor.getString(numberCol);
         
         if(matchNumbers(number, _number)) id = cursor.getString(idCol);
      }
      
      cursor.close();
      return id;
   }
   
   public static void lookUri(Context context, Uri uri){
      
      if(context == null){
         return;
      }
      
      Cursor cursor = context.getContentResolver().query(
            
            uri,
            null,
            null,
            null,
            null
      
      );
      
      if(cursor == null){
         return;
      }
      
      String[] columNames = cursor.getColumnNames();
      
      StringBuilder s = new StringBuilder();
      
      try{
         
         while(cursor.moveToNext()){
            
            for(String column : columNames){
               
               String value = cursor.getString(cursor.getColumnIndex(column));
               
               s.append(u.format("%40s : %s%n", column, value));
               
            }
            s.append("================================================================\n");
         }
         
         cursor.close();
      }
      catch(Exception e){
         
         Timber.w(e.getMessage());
      }
      
      
      File file = new File(context.getFilesDir(), u.format("%s.txt", String.valueOf(uri).replaceAll("/", "_")));
      
      try{
         
         if(file.createNewFile()){
            
            FileOutputStream out = new FileOutputStream(file);
            
            out.write(u.format("%s%n------------------------------------------%n", uri).getBytes());
            
            out.write(s.toString().getBytes());
            
            out.close();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }
   
   public static void lookUri(Context context, String contactId, Uri uri){
      
      if(context == null){
         return;
      }
      
      Cursor cursor = context.getContentResolver().query(
            
            uri,
            null,
            "contact_id =?",
            new String[]{contactId},
            null
      
      );
      
      if(cursor == null){
         return;
      }
      
      String[] columNames = cursor.getColumnNames();
      
      StringBuilder s = new StringBuilder();
      
      try{
         
         while(cursor.moveToNext()){
            
            for(String column : columNames){
               
               String value = cursor.getString(cursor.getColumnIndex(column));
               
               s.append(u.format("%40s : %s%n", column, value));
               
            }
            s.append("================================================================\n");
         }
         
         cursor.close();
      }
      catch(Exception e){
         
         Timber.w(e.getMessage());
      }
      
      
      File file = new File(context.getFilesDir(), u.format("%s_%s.txt", String.valueOf(uri).replaceAll("/", "_"), contactId));
      
      try{
         
         if(file.createNewFile()){
            
            FileOutputStream out = new FileOutputStream(file);
            
            out.write(u.format("%s - %s%n------------------------------------------%n", uri, contactId).getBytes());
            out.write(s.toString().getBytes());
            
            out.close();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }
   
   public static void lookUri(Context context, Uri uri, String account){
      
      if(context == null){
         return;
      }
      
      Cursor cursor = context.getContentResolver().query(
            
            uri,
            null,
            ContactsContract.RawContacts.ACCOUNT_NAME + "=?",
            new String[]{account},
            null
      
      );
      
      if(cursor == null){
         return;
      }
      
      String[] columNames = cursor.getColumnNames();
      
      StringBuilder s = new StringBuilder();
      
      while(cursor.moveToNext()){
         
         for(String column : columNames){
            
            String value = cursor.getString(cursor.getColumnIndex(column));
            
            s.append(u.format("%40s : %s%n", column, value));
            
         }
         s.append("================================================================\n");
      }
      
      cursor.close();
      
      
      File file = new File(context.getFilesDir(), u.format("%s_%s.txt", String.valueOf(uri).replaceAll("/", "_"), account));
      
      try{
         
         if(file.createNewFile()){
            
            FileOutputStream out = new FileOutputStream(file);
            out.write(u.format("%s - %s%n------------------------------------------%n", uri, account).getBytes());
            out.write(s.toString().getBytes());
            
            out.close();
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }
   
   /**
    * Telefon numarasının türünü string olarak döndür.
    * Sadece cep, ev ve iş telefonları dikkate alınacak.
    * Bunların dışındakiler 'diğer' katagorisine giriyor.
    *
    * @param context Context
    * @param type    Dönüştürülecek tür
    * @return Türün string karşılığı
    */
   public static String getNumberTypeString(Context context, int type){
      
      String typeStr = context.getString(R.string.type_other);
      
      switch(type){
         
         case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
            typeStr = context.getString(R.string.type_mobile);
            break;
         
         case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
            typeStr = context.getString(R.string.type_work);
            break;
         
         case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
            typeStr = context.getString(R.string.type_home);
            break;
      }
      
      return typeStr;
   }
   
   public static List<IMainContact> getMainContacts(final Context context, Account account){
      
      if(context == null) return new ArrayList<>(0);
      
      if(account == null) return getMainContacts(context);
      
      Timber.d("Alınacak olan hesap : %s", account);
      
      String[] cols = {
            
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.RawContacts.SORT_KEY_PRIMARY,
            ContactsContract.RawContacts.CONTACT_ID
      };
      
      val selection = Stringx.format("%s = '%s' and %s = '%s'",
                                     ContactsContract.RawContacts.ACCOUNT_NAME, account.name,
                                     ContactsContract.RawContacts.ACCOUNT_TYPE, account.type);
      
      val cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            selection,
            null,
            cols[1]
      );
      
      if(cursor == null) return new ArrayList<>(0);
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return new ArrayList<>(0);
      }
      
      List<IMainContact> contacts = new ArrayList<>(cursor.getCount());
      
      val nameCol      = cursor.getColumnIndex(cols[0]);
      val contactIdCol = cursor.getColumnIndex(cols[2]);
      
      while(cursor.moveToNext()){
         
         contacts.add(IMainContact.create(
               cursor.getString(nameCol),
               cursor.getString(contactIdCol)));
      }
      
      cursor.close();
      return contacts;
   }
   
   @NonNull
   public static List<IMainContact> getMainContacts(@NonNull final Context context){
      
      String[] cols = {
            
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.SORT_KEY_PRIMARY,
            ContactsContract.Contacts._ID
      };
      
      
      val cursor = context.getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            cols,
            null,
            null,
            cols[1]
      );
      
      if(cursor == null) return new ArrayList<>(0);
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return new ArrayList<>(0);
      }
      
      final List<IMainContact> contacts = new ArrayList<>(cursor.getCount());
      
      val nameCol    = cursor.getColumnIndex(cols[0]);
      val contactCol = cursor.getColumnIndex(cols[2]);
      
      while(cursor.moveToNext()){
         
         contacts.add(IMainContact.create(
               cursor.getString(nameCol),
               cursor.getString(contactCol)));
      }
      
      cursor.close();
      return contacts;
   }
   
   public static IPhoneContact getPhoneContact(final ContentResolver contentResolver, final String contactId){
      
      val rawContacts = getRawContacts(contentResolver, contactId);
      val numbers     = getNumbers(contentResolver, contactId);
      val phone       = new PhoneContact(contactId, getName(contentResolver, contactId), rawContacts, numbers);
      
      return phone;
   }
   
   public static String getName(ContentResolver contentResolver, String contactId){
      
      Cursor cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            new String[]{ContactsContract.Contacts.DISPLAY_NAME_PRIMARY},
            ContactsContract.Contacts._ID + "=?",
            new String[]{contactId},
            null
      );
      
      if(cursor == null) return null;
      
      String name = null;
      
      if(cursor.moveToFirst()){
         
         name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
      }
      
      cursor.close();
      return name;
   }
   
   /**
    * Verilen id'ye ait numara bilgilerini al.
    *
    * @param contentResolver cr
    * @param contactId id
    * @return Numara bilgileri. Eğer yoksa {@code null}
    */
   public static List<INumber> getNumbers(final ContentResolver contentResolver, String contactId){
      
      if(contentResolver == null) return null;
      
      var cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            new String[]{ContactsContract.CommonDataKinds.Phone.IS_PRIMARY, ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.NUMBER},
            u.format("%s = ?", ContactsContract.CommonDataKinds.Phone.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      if(cursor == null) return null;
      
      
      if(cursor.getCount() == 0){
         
         Timber.d("Numarası yok : %s", contactId);
         cursor.close();
         return null;
      }
      
      List<INumber> numbers = new LinkedList<>();
      
      while(cursor.moveToNext()){
         
         String  number    = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
         boolean isPrimary = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.IS_PRIMARY)) != 0;
         int     type      = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
         
         Number model = new Number(number, isPrimary, type);
         
         if(!numbers.contains(model)){
            
            numbers.add(model);
         }
      }
      
      cursor.close();
      return numbers;
   }
   
   @Nullable
   public static @Size(min = 1)
   List<IRawContact> getRawContacts(ContentResolver contentResolver, String contactId){
      
      String[] cols = {
            
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
            ContactsContract.RawContacts._ID,
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY
      };
      
      
      val cursor = contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            ContactsContract.RawContacts.CONTACT_ID + " = ?",
            new String[]{contactId},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      List<IRawContact> rawContacts = new LinkedList<>();
      
      while(cursor.moveToNext()){
         
         String accountName = cursor.getString(cursor.getColumnIndex(cols[0]));
         String accountType = cursor.getString(cursor.getColumnIndex(cols[1]));
         String rawId       = cursor.getString(cursor.getColumnIndex(cols[2]));
         String name        = cursor.getString(cursor.getColumnIndex(cols[3]));
         
         RawContact rawContact = new RawContact(rawId, contactId, name, new Account(accountName, accountType));
         
         rawContacts.add(rawContact);
      }
      
      cursor.close();
      return rawContacts;
   }
   
   public static LinkedList<MergedContact> getMergedContacts(Context context, String rawId){
      
      val      selection = Stringx.format("%s = %d and (%s = ? or %s = ?)", ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER, ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, ContactsContract.AggregationExceptions.RAW_CONTACT_ID2);
      String[] args      = {rawId, rawId};
      
      
      val cursor = context.getContentResolver().query(
            ContactsContract.AggregationExceptions.CONTENT_URI,
            null,
            selection,
            args,
            null
      );
      
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      LinkedList<MergedContact> mergedContacts = new LinkedList<>();
      
      while(cursor.moveToNext()){
         
         mergedContacts.push(new MergedContact(
               cursor.getString(cursor.getColumnIndex(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1)),
               cursor.getString(cursor.getColumnIndex(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2)),
               ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER
         ));
      }
      
      cursor.close();
      return mergedContacts;
   }
   
   public static RawContact getRawContact(Context context, String rawId){
      
      String[] cols = {
            
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.ACCOUNT_TYPE,
            ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.RawContacts.CONTACT_ID
      };
      
      
      val cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            ContactsContract.RawContacts._ID + " = ?",
            new String[]{rawId},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      RawContact rawContact = null;
      
      if(cursor.moveToNext()){
         
         String accountName = cursor.getString(cursor.getColumnIndex(cols[0]));
         String accountType = cursor.getString(cursor.getColumnIndex(cols[1]));
         String name        = cursor.getString(cursor.getColumnIndex(cols[2]));
         String contactId   = cursor.getString(cursor.getColumnIndex(cols[3]));
         
         rawContact = new RawContact(rawId, contactId, name, new Account(accountName, accountType));
         
      }
      
      cursor.close();
      return rawContact;
   }
   
   public static String createKeyFromNumber(@NonNull String number){
      
      number = Contacts.normalizeNumber(number);
      
      switch(number.length()){
         
         case 11: return number.substring(1);
         case 13: return number.substring(3);
         case 12: return number.substring(2);
         case 14: return number.substring(4);
         case 15: return number.substring(5);
         
         default: return number;
      }
   }
   
   public static Uri getImageUri(Context context, String contactId){
      
      String[] cols = {
            ContactsContract.Contacts.PHOTO_URI,
            ContactsContract.Contacts.PHOTO_THUMBNAIL_URI
         
      };
      
      val cursor = context.getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            cols,
            ContactsContract.Contacts._ID + " =?",
            new String[]{contactId},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      Uri uri = null;
      
      if(cursor.moveToFirst()){
         
         String _uri = cursor.getString(cursor.getColumnIndex(cols[0]));
         
         if(_uri != null) uri = Uri.parse(_uri);
         else{
            
            _uri = cursor.getString(cursor.getColumnIndex(cols[1]));
            if(_uri != null) uri = Uri.parse(_uri);
         }
      }
      
      cursor.close();
      return uri;
   }
   
   /**
    * Verilen id'ye ait kişiyi istenen gruptan çıkar.
    *
    * @param context   Context
    * @param contactId id
    * @param labelId   Grup id
    * @return İşlem başarılı ise {@code true}
    */
   public static boolean removeLabel(Context context, String contactId, String labelId){
      
      Uri    uri      = ContactsContract.Data.CONTENT_URI;
      String mimetype = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
      
      String[] cols = {
            
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,
            ContactsContract.Data.CONTACT_ID
      };
      
      
      String selection = String.format(
            
            "%s = '%s' AND %s =? AND %s =?",
            cols[0],
            mimetype,
            cols[1],
            cols[2]
      );
      
      String[] args = {labelId, contactId};
      
      
      int rows = context.getContentResolver().delete(uri, selection, args);
      
      return rows != 0;
   }
   
   /**
    * Verilen hesaba ait, verilen isimde yeni bir grup oluştur.
    *
    * @param context    Context
    * @param groupTitle Grup ismi
    * @param account    Hesap
    * @return İşlem başarılı ise {@code true}
    */
   public static boolean createNewGroup(Context context, String groupTitle, @Nullable Account account){
      
      ContentValues values = new ContentValues();
      values.put(ContactsContract.Groups.TITLE, groupTitle);
      values.put(ContactsContract.Groups.GROUP_VISIBLE, "1");
      values.put(ContactsContract.Groups.ACCOUNT_NAME, account != null ? account.name : null);
      values.put(ContactsContract.Groups.ACCOUNT_TYPE, account != null ? account.type : null);
      
      Uri newUri = context.getContentResolver().insert(ContactsContract.Groups.CONTENT_URI, values);
      
      if(newUri != null){
         
         Log.d("createNewGroup", "Yeni grup eklendi : " + groupTitle);
         return true;
      }
      else{
         
         Log.d("createNewGroup", "Yeni grup eklenemedi");
         return false;
      }
   }
   
   /**
    * Verilen id'ye ait grubun ismini değiştir.
    *
    * @param context Context
    * @param groupId Grup id
    * @param newName Yeni isim
    * @return İşlem başarılı ise {@code true}
    */
   public static boolean updateGroupName(Context context, String groupId, String newName){
      
      ContentValues values = new ContentValues();
      
      values.put(ContactsContract.Groups.TITLE, newName);
      
      String   selection = String.format("%s =?", ContactsContract.Groups._ID);
      String[] args      = {groupId};
      
      ContentResolver contentResolver = context.getContentResolver();
      
      int rows = contentResolver.update(ContactsContract.Groups.CONTENT_URI, values, selection, args);
      
      if(rows != 0){
         
         Log.d("updateGroup", "Grup güncellendi");
         return true;
      }
      else{
         
         Log.d("updateGroup", "Grup güncellemesi başarısız");
         return false;
      }
   }
   
   public static boolean setGroupVisibility(Context context, String groupId, String isVisable){
      
      ContentValues values = new ContentValues();
      
      values.put(ContactsContract.Groups.GROUP_VISIBLE, isVisable);
      
      String   selection = String.format("%s =?", ContactsContract.Groups._ID);
      String[] args      = {groupId};
      
      ContentResolver contentResolver = context.getContentResolver();
      
      int rows = contentResolver.update(ContactsContract.Groups.CONTENT_URI, values, selection, args);
      
      if(rows != 0){
         
         if(isVisable.equals("0")){
            
            Log.d("setGroupVisibility", "Grup gizlendi");
         }
         else{
            
            Log.d("setGroupVisibility", "Grup görünür oldu");
         }
         
         return true;
      }
      else{
         
         Log.d("setGroupVisibility", "İşlem başarısız");
         return false;
      }
   }
   
   public static boolean deleteGroup(Context context, String groupId){
      
      String   selection = String.format("%s =?", ContactsContract.Groups._ID);
      String[] args      = {groupId};
      
      ContentResolver contentResolver = context.getContentResolver();
      
      int rows = contentResolver.delete(ContactsContract.Groups.CONTENT_URI, selection, args);
      
      if(rows != 0){
         
         Log.d("deleteGroup", "Grup silindi");
         return true;
      }
      else{
         
         Log.d("deleteGroup", "Grup silinemedi");
         return false;
      }
   }
   
   public static boolean makeGroupInVisibile(Context context, String groupId){
      
      Uri uri = ContactsContract.Groups.CONTENT_URI;
      
      ContentValues values = new ContentValues();
      values.put(ContactsContract.Groups.GROUP_VISIBLE, "0");
      
      String   selection = String.format("%s =?", ContactsContract.Groups._ID);
      String[] args      = {groupId};
      
      
      int rows = context.getContentResolver().update(uri, values, selection, args);
      
      if(rows != 0){
         
         Log.d("makeGroupInVisibile", "Grup gizlendi");
         return true;
      }
      else{
         
         Log.d("makeGroupInVisibile", "Grup gizleme başarısız");
         return false;
      }
      
   }
   
   public static Account getGroupAccount(Context context, String id){
      
      if(context == null || id == null || id.trim().isEmpty()){ return null; }
      
      String[] cols = {
            ContactsContract.Groups._ID,
            ContactsContract.Groups.ACCOUNT_TYPE,
            ContactsContract.Groups.ACCOUNT_NAME
      };
      
      String[] args      = {id};
      String   selection = u.format("%s=%s", cols[0], id);
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Groups.CONTENT_URI,
            cols,
            selection,
            args,
            null);
      if(cursor == null){return null; }
      
      
      if(cursor.moveToNext()){
         
         String type = cursor.getString(cursor.getColumnIndex(cols[1]));
         String name = cursor.getString(cursor.getColumnIndex(cols[2]));
         
         cursor.close();
         
         return new Account(name, type);
      }
      
      cursor.close();
      return null;
   }
   
   public static String getDefaultRingTone(Context context){
      
      Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
      
      if(uri != null){
         
         Ringtone ring = RingtoneManager.getRingtone(context, uri);
         
         if(ring != null){
            
            return ring.getTitle(context);
         }
      }
      
      return null;
   }
   
   public static boolean isDefaultRingTone(Context context, String contactId){
      
      return getRingTone(context, contactId) == null;
   }
   
   public static String getRingTone(Context context, String contactId){
      
      if(context == null){
         
         return null;
      }
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.CUSTOM_RINGTONE},
            ContactsContract.Contacts._ID + "=?",
            new String[]{contactId},
            null);
      
      if(cursor == null || cursor.getCount() == 0){
         
         return null;
      }
      
      cursor.moveToFirst();
      
      String ring = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
      
      cursor.close();
      
      return ring;
   }
   
   public static String getContactId(Context context, String rawId){
      
      String[] cols = {
            
            ContactsContract.RawContacts._ID,
            ContactsContract.RawContacts.CONTACT_ID
      };
      
      val      selection = Stringx.format("%s=?", cols[0]);
      String[] args      = {rawId};
      
      val cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            selection,
            args,
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      String contactId = null;
      
      if(cursor.moveToFirst()){
         
         contactId = cursor.getString(cursor.getColumnIndex(cols[1]));
      }
      
      cursor.close();
      return contactId;
   }
   
   public static boolean setDefaultRingTone(Context context, String contactId){
      
      return setRingTone(context, contactId, null);
   }
   
   public static boolean setRingTone(Context context, String contactId, String newRingToneUri){
      
      String ringUri = getRingTone(context, contactId);
      
      String currentRingTone = ringUri != null ? getRingtoneName(context, ringUri) : null;
      String newRingTone     = getRingtoneName(context, newRingToneUri);
      
      if(currentRingTone != null){
         
         Timber.d("Kişinin kayıtlı zil sesi : %s", currentRingTone);
         
      }
      else{
         
         Timber.d("Varsayılan zil sesi kayıtlı");
      }
      
      if(currentRingTone != null && currentRingTone.equals(newRingTone)){
         
         Timber.d("Zaten atanacak olan zil sesi kayıtlı");
         return true;
      }
      else{
         
         Timber.d("Atanacak olan zil sesi : %s", newRingTone);
      }
      
      
      ContentValues contentValues = new ContentValues();
      
      if(newRingToneUri == null){
         
         contentValues.putNull(ContactsContract.Contacts.CUSTOM_RINGTONE);
      }
      else{
         
         contentValues.put(ContactsContract.Contacts.CUSTOM_RINGTONE, newRingToneUri);
      }
      
      String   selection = u.format("%s=?", ContactsContract.Contacts._ID);
      String[] args      = new String[]{contactId};
      
      
      int rows = context.getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, contentValues, selection, args);
      
      
      return rows > 0;
   }
   
   @Nullable
   public static String getRingtoneName(Context context, String toneUri){
      
      if(context == null || toneUri == null) return null;
      
      Ringtone uri = RingtoneManager.getRingtone(context, Uri.parse(toneUri));
      
      if(uri != null){
         
         return uri.getTitle(context);
      }
      
      return null;
   }
   
   public static boolean setRingToneOfGroup(Context context, String groupId, Uri ringToneUri){
      
      String   selection = String.format("%s =?", ContactsContract.Groups._ID);
      String[] args      = {groupId};
      
      ContentValues values = new ContentValues();
      
      values.put(ContactsContract.Groups.NOTES, ringToneUri.toString());
      
      int rows = context.getContentResolver().update(ContactsContract.Groups.CONTENT_URI, values, selection, args);
      
      return rows > 0;
   }
   
   public static Group getGroup(Context context, Account account, String name){
      
      String[] cols = {
            ContactsContract.Groups._ID,
            ContactsContract.Groups.ACCOUNT_NAME,
            ContactsContract.Groups.TITLE,
            ContactsContract.Groups.GROUP_IS_READ_ONLY,
            ContactsContract.Groups.GROUP_VISIBLE,
            ContactsContract.Groups.DELETED,
            ContactsContract.Groups.NOTES
      };
      
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Groups.CONTENT_URI,
            cols,
            u.format("%s=? and %s=? and %s=?", cols[1], cols[5], cols[2]),
            new String[]{account.name, "0", name},
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      if(cursor.moveToNext()){
         
         String  id         = cursor.getString(cursor.getColumnIndex(cols[0]));
         boolean isReadOnly = cursor.getString(cursor.getColumnIndex(cols[3])).equals("1");
         boolean isVisable  = cursor.getString(cursor.getColumnIndex(cols[4])).equals("1");
         String  ringtone   = cursor.getString(cursor.getColumnIndex(cols[6]));
         
         cursor.close();
         return new Group(id, name, account, isReadOnly, isVisable, -1, ringtone);
      }
      
      cursor.close();
      return null;
   }
   
   public static List<String> getGroupTitles(Context context, Account account){
      
      if(context == null){
         return null;
      }
      
      List<String> titles = new ArrayList<>();
      
      String[] cols = {
            ContactsContract.Groups.ACCOUNT_NAME,
            ContactsContract.Groups.TITLE
      };
      
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Groups.CONTENT_URI,
            cols,
            u.format("%s=?", cols[0]),
            new String[]{account.name},
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      while(cursor.moveToNext()){
         
         titles.add(cursor.getString(cursor.getColumnIndex(cols[1])));
      }
      
      cursor.close();
      return titles;
   }
   
   public static List<Group> getGroupsInfo(Context context, Account account){
      
      
      String[] cols = {
            ContactsContract.Groups._ID,
            ContactsContract.Groups.ACCOUNT_NAME,
            ContactsContract.Groups.TITLE,
            ContactsContract.Groups.GROUP_IS_READ_ONLY,
            ContactsContract.Groups.GROUP_VISIBLE,
            ContactsContract.Groups.DELETED,
            ContactsContract.Groups.NOTES
      };
      
      
      Cursor cursor = getCursor(
            context.getContentResolver(),
            ContactsContract.Groups.CONTENT_URI,
            cols,
            u.format("%s=? and %s=?", cols[1], cols[5]),
            new String[]{account.name, "0"});
      
      if(cursor == null){
         return null;
      }
      
      List<Group> groups = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         String  id         = cursor.getString(cursor.getColumnIndex(cols[0]));
         String  title      = cursor.getString(cursor.getColumnIndex(cols[2]));
         boolean isReadOnly = cursor.getString(cursor.getColumnIndex(cols[3])).equals("1");
         boolean isVisable  = cursor.getString(cursor.getColumnIndex(cols[4])).equals("1");
         String  ringtone   = cursor.getString(cursor.getColumnIndex(cols[6]));
         
         
         groups.add(new Group(id, title, account, isReadOnly, isVisable, -1, ringtone));
      }
      
      cursor.close();
      return groups;
   }
   
   public static Cursor getCursor(ContentResolver contentResolver, Uri uri, String[] cols, String selection, String[] args){
      
      return contentResolver.query(uri, cols, selection, args, null);
   }
   
   public static List<String> getGroupIdList(Context context, String contactId, String accountName){
      
      if(context == null){return null;}
      
      String[] DATA_COLUMNS = {
            
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.CONTACT_ID
         
      };
      
      
      String selection = u.format("%s =? AND %s = '%s' AND %s =?",
                                  DATA_COLUMNS[2],
                                  DATA_COLUMNS[0],
                                  ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
                                  ContactsContract.RawContacts.ACCOUNT_NAME);
      
      String[] args = {contactId, accountName};
      
      Cursor cursor = getCursor(context.getContentResolver(),
                                ContactsContract.Data.CONTENT_URI,
                                DATA_COLUMNS,
                                selection,
                                args
      );
      
      if(cursor == null){
         return null;
      }
      
      List<String> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         list.add(cursor.getString(cursor.getColumnIndex(DATA_COLUMNS[1])));
      }
      
      cursor.close();
      return list;
   }
   
   /**
    * Sadece görünür olan grupların listesini döndürür.
    *
    * @param context Context
    * @param idList  Grupların id listesi
    * @param account Bağlı olan hesap
    * @return Grup listesi
    */
   public static List<Group> getGroupList(Context context, List<String> idList, Account account){
      
      if(context == null){return null;}
      
      String[] cols = {
            ContactsContract.Groups._ID,
            ContactsContract.Groups.TITLE,
            ContactsContract.Groups.GROUP_IS_READ_ONLY,
            ContactsContract.Groups.ACCOUNT_NAME,
            ContactsContract.Groups.DELETED,
            ContactsContract.Groups.GROUP_VISIBLE,
            ContactsContract.Groups.NOTES
      };
      
      String selection = u.format("%s in (%s) AND %s ='1' AND %s ='0' AND %s =?",
                                  cols[0],
                                  joinToString(idList),
                                  cols[5],
                                  cols[4],
                                  cols[3]
      );
      
      Cursor cursor = getCursor(context.getContentResolver(),
                                ContactsContract.Groups.CONTENT_URI,
                                cols,
                                selection,
                                new String[]{account.name});
      
      if(cursor == null){
         return null;
      }
      
      List<Group> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         String  id         = cursor.getString(cursor.getColumnIndex(cols[0]));
         String  title      = cursor.getString(cursor.getColumnIndex(cols[1]));
         boolean isReadOnly = cursor.getString(cursor.getColumnIndex(cols[2])).equals("1");
         boolean isVisable  = cursor.getString(cursor.getColumnIndex(cols[5])).equals("1");
         String  ringtone   = cursor.getString(cursor.getColumnIndex(cols[6]));
         
         
         list.add(new Group(
               id,
               title,
               account,
               isReadOnly,
               isVisable,
               -1,
               ringtone));
         
      }
      
      
      cursor.close();
      return list;
   }
   
   public static <T> String joinToString(List<T> collection, String delimiter){
      
      StringBuilder stringBuilder = new StringBuilder();
      
      for(int i = 0; i < collection.size(); i++){
         
         T t = collection.get(i);
         
         stringBuilder.append(t.toString());
         
         if(i != collection.size() - 1){
            
            stringBuilder.append(delimiter);
         }
      }
      
      return stringBuilder.toString();
   }
   
   public static List<String> getGroupMembers(Context context, String groupId){
      
      if(context == null){return null;}
      
      String[] cols = {
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA1
      };
      
      
      Uri      uri       = ContactsContract.Data.CONTENT_URI;
      String   mimetype  = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
      String   selection = u.format("%s='%s' and %s=?", cols[0], mimetype, cols[2]);
      String[] args      = {groupId};
      
      Cursor cursor = context.getContentResolver().query(
            uri,
            cols,
            selection,
            args,
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      
      List<String> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         list.add(cursor.getString(cursor.getColumnIndex(cols[1])));
      }
      
      cursor.close();
      return list;
   }
   
   public static List<String> getNumbers(Context context, List<String> idList){
      
      if(context == null){return null;}
      
      Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
      
      String[] cols = {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
      };
      
      String selection = u.format("%s in (%s)", cols[0], joinToString(idList));
      
      Cursor cursor = context.getContentResolver().query(
            uri,
            cols,
            selection,
            null,
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      List<String> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         list.add(cursor.getString(cursor.getColumnIndex(cols[1])));
      }
      
      cursor.close();
      return list;
   }
   
   public static List<String> getLabeledContactIds(Context context, Group group){
      
      if(context == null){return null;}
      
      String[] cols = {
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA1,
            ContactsContract.RawContacts.ACCOUNT_NAME
      };
      
      String   mimetype  = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
      String   selection = u.format("%s='%s' and %s=? and %s=?", cols[0], mimetype, cols[2], cols[3]);
      String[] args      = {group.getId(), group.getAccount().name};
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Data.CONTENT_URI,
            cols,
            selection,
            args,
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      List<String> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         list.add(cursor.getString(cursor.getColumnIndex(cols[1])));
      }
      
      cursor.close();
      
      return list;
   }
   
   public static List<String> getNotLabeledContactIds(Context context, Group group, List<String> idList){
      
      if(context == null){return null;}
      
      
      String[] cols = {
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DATA1,
            ContactsContract.RawContacts.ACCOUNT_NAME
      };
      
      String   mimetype  = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
      String   selection = u.format("%s='%s' and %s != ? and %s=? and %s NOT IN (%s)", cols[0], mimetype, cols[2], cols[3], cols[1], joinToString(idList));
      String[] args      = {group.getId(), group.getAccount().name};
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Data.CONTENT_URI,
            cols,
            selection,
            args,
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      List<String> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         list.add(cursor.getString(cursor.getColumnIndex(cols[1])));
      }
      
      cursor.close();
      return list;
   }
   
   public static List<IMainContact> getLabeledContacts(Context context, List<String> idList){
      
      if(context == null){return null;}
      
      String selection = u.format("%s in (%s)", Contacts.MAIN_CONTACT_COLUMNS[0], joinToString(idList));
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            MAIN_CONTACT_COLUMNS,
            selection,
            null,
            MAIN_CONTACT_COLUMNS[2]
      );
      
      if(cursor == null){
         return null;
      }
      
      List<IMainContact> list = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         String id   = cursor.getString(cursor.getColumnIndex(MAIN_CONTACT_COLUMNS[0]));
         String name = cursor.getString(cursor.getColumnIndex(MAIN_CONTACT_COLUMNS[1]));
         
         list.add(IMainContact.create(name, id));
      }
      
      cursor.close();
      return list;
   }
   
   public static boolean addLabel(Context context, String contactId, String groupId){
      
      // RAW_CONTACT_ID mecbur
      
      ContentValues values = new ContentValues();
      values.put(ContactsContract.Data.RAW_CONTACT_ID, getRawId(context, contactId));
      values.put(ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID, groupId);
      values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE);
      
      
      return context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values) != null;
   }
   
   public static String getRawId(Context context, String contactId){
      
      if(context == null){return null;}
      
      String[] cols = {
            ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts._ID
      };
      
      String   selection = u.format("%s=?", cols[0]);
      String[] args      = {contactId};
      
      Cursor cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            selection,
            args,
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      if(cursor.moveToFirst()){
         
         String id = cursor.getString(cursor.getColumnIndex(cols[1]));
         
         cursor.close();
         return id;
      }
      
      cursor.close();
      return null;
   }
   
   public static boolean isEditableLabel(Context context, String groupId){
      
      if(context == null){return false;}
      
      String[] cols = {
            ContactsContract.Groups._ID,
            ContactsContract.Groups.AUTO_ADD
      };
      
      String   selection = u.format("%s=?", cols[0]);
      String[] args      = {groupId};
      
      Cursor cursor = getCursor(
            context.getContentResolver(),
            ContactsContract.Groups.CONTENT_URI,
            cols,
            selection,
            args
      );
      
      if(cursor == null){
         return false;
      }
      
      if(cursor.moveToFirst()){
         
         //Auoto_add '1' olursa editable false olur
         boolean isEditable = cursor.getString(cursor.getColumnIndex(cols[1])).equals("0");
         
         cursor.close();
         return isEditable;
      }
      
      cursor.close();
      return false;
   }
   
   public static int getGroupCount(Context context, Group group){
      
      if(context == null){return -1;}
      
      String[] cols = {
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.DATA1,//grup id
            ContactsContract.RawContacts.ACCOUNT_NAME
      };
      
      
      String   mimetype  = ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE;
      String   selection = u.format("%s = '%s' AND %s = ? AND %s = ?", cols[0], mimetype, cols[1], cols[2]);
      String[] args      = {group.getId(), group.getAccount().name};
      
      
      Cursor cursor = getCursor(
            context.getContentResolver(),
            ContactsContract.Data.CONTENT_URI, cols, selection, args
      );
      
      if(cursor == null){
         return -1;
      }
      
      group.setMembersCount(cursor.getCount());
      cursor.close();
      return group.getMembersCount();
   }
   
   private static Cursor getContactDetails(Context context, String contactId){
      
      return context.getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            u.format("%s = ?", ContactsContract.Contacts._ID),
            new String[]{contactId},
            null
      );
   }
   
   private static LinkedList<String> getMergedRaws(LinkedList<MergedContact> contacts, String rawId){
      
      LinkedList<String> raws = new LinkedList<>();
      
      do{
         
         val item = contacts.pop();
         
         if(item.rawId_1.equals(rawId)) raws.push(item.rawId_2);
         else raws.push(item.rawId_1);
      }
      while(!contacts.isEmpty());
      
      return raws;
   }
   
   /**
    * Verilen rawId'ye ait numaraları döndür.
    *
    * @param context Context
    * @param rawId   Contact id
    * @return Numara dizisi. Yoksa boş dizi
    */
   @NonNull
   private static String[] getContactNumbers(@NonNull final Context context, @NonNull final String rawId){
      
      String[] phoneColumns = {
            
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.NUMBER
      };
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            phoneColumns,
            phoneColumns[0] + "=?",
            new String[]{rawId},
            null
      );
      
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            cursor.close();
         }
         
         return new String[]{};
      }
      
      final int numberCol = cursor.getColumnIndex(phoneColumns[1]);
      
      String[] numbers = new String[cursor.getCount()];
      
      int i = 0;
      
      while(cursor.moveToNext()){
         
         numbers[i++] = cursor.getString(numberCol);
      }
      
      cursor.close();
      return numbers;
   }
   
   /**
    * Verilen id değerine sahip kişinin bağlı olduğu tüm hesapları döndür.
    *
    * @param context   Context
    * @param contactId id
    * @return Varsa hesaplar, yoksa {@code null}
    */
   @Nullable
   private static List<Account> getAccounts(@NonNull final Context context, @NonNull final String contactId){
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.RawContacts.CONTENT_URI,
            new String[]{ContactsContract.RawContacts.CONTACT_ID, ContactsContract.RawContacts.ACCOUNT_NAME, ContactsContract.RawContacts.ACCOUNT_TYPE},
            u.format("%s = ?", ContactsContract.RawContacts.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      if(cursor == null || cursor.getCount() == 0){
         
         if(cursor != null){
            
            cursor.close();
         }
         
         return null;
      }
      
      final List<Account> accounts = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         final String account     = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
         final String accountType = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));
         
         if(accountType == null || accountType.trim().isEmpty()) continue;
         
         accounts.add(new Account(account, accountType));
      }
      
      cursor.close();
      return accounts;
   }
   
   private static Account getMessengerAccount(Context context, String contactId){
      
      String[] cols = {
            
            //ContactsContract.RawContacts.CONTACT_ID,
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.ACCOUNT_TYPE
      };
      
      val cursor = context.getContentResolver().query(
            
            ContactsContract.RawContacts.CONTENT_URI,
            cols,
            ContactsContract.RawContacts.CONTACT_ID + " = ? and " + ContactsContract.RawContacts.ACCOUNT_TYPE + " = ?",
            new String[]{contactId, context.getString(R.string.account_type_messenger)},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      Account account = null;
      
      if(cursor.moveToNext()){
         
         String name = cursor.getString(cursor.getColumnIndex(cols[0]));
         String type = cursor.getString(cursor.getColumnIndex(cols[1]));
         
         account = new Account(name, type);
      }
      
      cursor.close();
      return account;
   }
   
}
