package com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.databinding.ActivityContactsMergeBinding;
import com.tr.hsyn.telefonrehberi.util.Toolbarx;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.ContactNumber;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.MergeContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.account.AccountsActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.val;
import lombok.var;
import timber.log.Timber;


@SuppressLint("Registered")
public class ContactsMergeActivity extends AppCompatActivity{
   
   private final        Count                                        count        = new Count(0);
   private              String                                       ACCOUNT_TYPE_GOOGLE;
   private              String                                       ACCOUNT_TYPE_SIM;
   private              ProgressBar                                  progress;
   private              int                                          markUnmarkClickCount;
   private              View                                         emptyView;
   private volatile     Map<String, LinkedList<MergeContact>>        mergeContacts;
   private              ViewGroup                                    contactsMainContainer;//herşeyi buna ekleyeceğiz
   private volatile     Map<String, View>                            contactsMergeItemsView;
   private              ActivityContactsMergeBinding                 binding;
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      //setContentView(R.layout.activity_contacts_merge);
      
      binding = DataBindingUtil.setContentView(this, R.layout.activity_contacts_merge);
      setViews();
      binding.setCount(count);
      
      ACCOUNT_TYPE_GOOGLE = getString(R.string.account_type_google);
      ACCOUNT_TYPE_SIM    = getString(R.string.account_type_sim);
      start();
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.activity_contacts_merge_menu, menu);
      
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item){
      
      int id = item.getItemId();
      
      switch(id){
         
         case R.id.contacts_merge_mark_unmark_all:
            
            markUnmarkClickCount++;
            markAll(markUnmarkClickCount % 2 == 0);
            
            return true;
         
         case R.id.contacts_merge_merge_marked:
         case R.id.contacts_merge_merge:
            
            mergeMarked();
            return true;
         
         case R.id.contacts_merge_unmark_all:
            
            markAll(false);
            return true;
         
         case R.id.contacts_merge_mark_all:
            
            markAll(true);
            return true;
         
         case R.id.merge_contacts_merged_contacts:
            
            startActivity(new Intent(this, MergedContactsActivity.class));
            return true;
         
         case R.id.merge_contacts_refresh:
            
            refresh();
            return true;
      }
      
      
      return super.onOptionsItemSelected(item);
   }
   
   public Map<String, LinkedList<MergeContact>> getMergeContacts(final Context context){
      
      val contacts = getSimpleMergeContacts(context);
      
      Timber.d("contacts : %d", contacts.size());
      
      if(contacts.size() < 2) return new HashMap<>();
      
      return getMergePairs(contacts);
   }
   
   public static Account selectProperAccount(Context context, final Account[] accounts){
      
      if(accounts == null) return null;
      
      val _accountSize = accounts.length;
      
      if(_accountSize == 0) return null;
      
      if(_accountSize == 1){
         
         return accounts[0];
      }
      
      val _googleAccountType = "com.google";
      val _simAccountType    = "com.android.sim";
      
      //Google hesaplarını seç
      val googleAccounts = Stream.of(accounts)
                                 .filter(cx -> cx.type.equals(_googleAccountType))
                                 .distinctBy(cy -> cy.name)
                                 .toArray(Account[]::new);
      
      if(googleAccounts.length == 1){
         
         return googleAccounts[0];
      }
      
      if(googleAccounts.length >= 2){
         
         
         //? Kimin en çok kaydı varsa o hesabı döndür
         
         val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
         
         val countMap = Stream.of(googleAccounts).collect(Collectors.toMap(cz -> cz, cx -> AccountsActivity.getContactCount(context, uri, cx)));
         
         val maxCount = Stream.of(countMap).max((cx, cy) -> Integer.compare(cx.getValue(), cy.getValue()));
         
         return maxCount.get().getKey();
      }
      
      //! Demekki google hesabı yok
      //? Aynı zamanda hesap sayısı 1'den fazla
      
      val account_0 = accounts[0];
      val account_1 = accounts[1];
      
      if(_accountSize == 2){
         
         //İki hesap da aynı mı
         if(account_0.equals(account_1)) return account_0;
         
         //İki hesap da farklı
         //Peki türler farklı mı
         if(!account_0.type.equals(account_1.type)){
            
            //Türler farklı
            //Varsa sim kart döndür
            
            //Ver bir sim
            if(account_0.type.equals(_simAccountType)){
               
               return account_0;
            }
            
            //Sim değil
            //Ne çıkarsa bahtına
            return account_1;
         }
         else{//Türler eşit
            
            return account_0;
         }
      }
      else{
         
         //? Hesap sayısı 2'den fazla
         //! Ayrıca google hesabı yok
         
         //Sim hesaplarını seç
         val simAccounts = Stream.of(accounts).filter(cx -> cx.type.equals(_simAccountType)).toArray(Account[]::new);
         
         //Ver bir sim
         if(simAccounts.length != 0){
            
            return simAccounts[0];
         }
         
         //Sim değil
         //Ne çıkarsa bahtına
         return account_0;
      }
   }
   
   private void refresh(){
      
      if(contactsMergeItemsView != null) {
   
         contactsMergeItemsView.clear();
      }
      
      if(mergeContacts != null) mergeContacts.clear();
      
      binding.contactsContainer.removeAllViews();
      
      checkEmpty();
      
      start();
   }
   
   private void start(){
   
      binding.progress.setVisibility(View.VISIBLE);
      Worker.onBackground(this::getContactsForMerge, "ContactsMergeActivity:Birleştirilecek kişileri alma")
            .whenCompleteAsync(this::takeMergeContacts, Worker.getMainThreadExecutor());
   }
   
   private void setViews(){
      
      setToolbar();
      progress              = binding.progress;
      contactsMainContainer = binding.contactsContainer;
      ColorController.setIndaterminateProgressColor(this, progress);
      emptyView = binding.getRoot().findViewById(R.id.empty);
   }
   
   /**
    * Toolbar'ı ayarla
    */
   private void setToolbar(){
      
      //Toolbar toolbar = findViewById(R.id.toolbar);
      Toolbarx.setToolbar(this, binding.toolbar);
   }
   
   private void removeIgnoredPairs(Map<String, LinkedList<MergeContact>> contacts){
      
      
   }
   
   @SuppressWarnings("ConstantConditions") 
   private Map<String, LinkedList<MergeContact>> getMergePairs(LinkedList<MergeContact> contacts){
      
      
      //Her bir kişiyi diğer tüm kişilerle karşılaştır
      //Numarası aynı olanları topla
      
      
      Map<String, LinkedList<MergeContact>> map = new HashMap<>();
      
      
      for(val item : contacts){
         
         Set<String> keys = new HashSet<>();
         
         if(item.numbers == null) continue;
         
         for(int i = 0; i < item.numbers.size(); i++){
            
            val number = item.numbers.get(i);
            
            if(number != null){
               
               keys.add(Contacts.createKeyFromNumber(number.getNumber()));
            }
         }
         
         for(val key : keys){
            
            if(!map.containsKey(key)){
               
               val link = new LinkedList<MergeContact>();
               link.push(item);
               map.put(key, link);
            }
            else{
               
               map.get(key).push(item);
            }
         }
      }
   
      
      //Eğer birleştirilecek kişiler arasında google hesabı yoksa listeden çıkar
      //Çünkü birleştirme olayı sadece google hesapları üzerine olabiliyor
      
      val mapCopy = new HashMap<String, LinkedList<MergeContact>>();
      
      loop:
      for(val entry : map.entrySet()){
         
         val item = entry.getValue();
         
         if(item.size() < 2) continue;
   
         for(val mergeItem : item){
   
            for(val number : mergeItem.numbers){
               
               if(number.getAccount().type.equals(ACCOUNT_TYPE_GOOGLE)){
                  
                  mapCopy.put(entry.getKey(), entry.getValue());
                  continue loop;
               }
            }
         }
      }
      
      //val list = Stream.of(map).flatMap(cx -> Stream.of(cx.getValue())).toList();
      
      return mapCopy;
      //return Stream.of(mapCopy).filter(cx -> cx.getValue().size() >= 2).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
   }
   
   @NonNull
   private LinkedList<MergeContact> getSimpleMergeContacts(Context context){
      
      LinkedList<MergeContact> contacts = new LinkedList<>();
      
      if(context == null) return contacts;
   
   
      String[] SIMPLE_CONTACT_COLUMNS = {
      
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.NAME_RAW_CONTACT_ID
            
      };
      
      
      final Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            SIMPLE_CONTACT_COLUMNS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER + " != 0",
            null,
            null
      );
      
      if(cursor == null) return contacts;
      
      final int idCol   = cursor.getColumnIndex(SIMPLE_CONTACT_COLUMNS[0]);
      final int nameCol = cursor.getColumnIndex(SIMPLE_CONTACT_COLUMNS[1]);
      final int rawCol  = cursor.getColumnIndex(SIMPLE_CONTACT_COLUMNS[2]);
      
      while(cursor.moveToNext()){
         
         final String id    = cursor.getString(idCol);
         final String rawId = cursor.getString(rawCol);
         final String name  = cursor.getString(nameCol);
         
         contacts.push(new MergeContact(id, rawId, name, getPNumbers(context, id)));
      }
      
      cursor.close();
      return contacts;
   }
   
   private List<ContactNumber> getPNumbers(@NonNull final Context context, @NonNull final String contactId){
   
      String[] NUMBER_COLUMNS = {
      
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.RawContacts.ACCOUNT_NAME,
            ContactsContract.RawContacts.ACCOUNT_TYPE
      };
      
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            NUMBER_COLUMNS,
            String.format("%s = ?", ContactsContract.CommonDataKinds.Phone.CONTACT_ID),
            new String[]{contactId},
            null
      );
      
      if(cursor == null){
         return null;
      }
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      List<ContactNumber> numbers = new ArrayList<>(cursor.getCount());
      
      int numberCol      = cursor.getColumnIndex(NUMBER_COLUMNS[0]);
      int accountCol     = cursor.getColumnIndex(NUMBER_COLUMNS[1]);
      int accountTypeCol = cursor.getColumnIndex(NUMBER_COLUMNS[2]);
      //int lookupCol      = cursor.getColumnIndex(Contacts.NUMBER_COLUMNS[3]);
      
      while(cursor.moveToNext()){
         
         ContactNumber contactNumber = new ContactNumber(
               cursor.getString(numberCol),
               -1,
               new Account(cursor.getString(accountCol), cursor.getString(accountTypeCol)),
               null/*cursor.getString(lookupCol)*/);
         
         numbers.add(contactNumber);
      }
      
      cursor.close();
      return numbers;
   }
   
   /**
    * Birleştirilmesi gereken kişileri al kullan.
    * Kişileri listeye set et.
    *
    * @param contacts Kişiler
    */
   private void takeMergeContacts(Map<String, LinkedList<MergeContact>> contacts, Throwable throwable){
   
      if(throwable != null){
         
         Timber.d("Görev hata ile sonlandı : %s", throwable.getMessage());
      }
      
      if(contacts == null){
         
         return;
      }
      
      mergeContacts = contacts;
      
      if(contacts.size() != 0){
         
         createItems();
         
         count.setCount(contacts.size());
      }
      
      checkEmpty();
      progress.setVisibility(View.GONE);
   }
   
   private void createItems(){
      
      contactsMergeItemsView = new HashMap<>();
      final LayoutInflater layoutInflater = getLayoutInflater();
      
      val keys = mergeContacts.keySet();
      //LinkedList<Account> accounts = new LinkedList<>();
      
      int color = u.getPrimaryColor(this);
      
      for(val key : keys){
         
         val items = mergeContacts.get(key);
         
         View mergeItemsView = layoutInflater.inflate(R.layout.contacts_merge_items, contactsMainContainer, false);
         
         TextView title = mergeItemsView.findViewById(R.id.title);
         title.setText(CallStory.formatNumberForDisplay(key));
         
         View header = mergeItemsView.findViewById(R.id.header);
         header.setBackgroundColor(u.lighter(color, 0.9F));
         
         CheckBox checkBox = header.findViewById(R.id.check);
         
         checkBox.setButtonTintList(ColorStateList.valueOf(u.lighter(color, 0.2F)));
         header.findViewById(R.id.mergeIcon).setOnClickListener((v) -> merge(new LinkedList<>(Collections.singletonList(key))));
         
         ViewGroup mergeContactsContainerView = mergeItemsView.findViewById(R.id.mergeContactsContainer);
         
         assert items != null;
         for(val item : items){
            
            val account = selectProperAccount(this, getAccounts(item.numbers));
            
            View itemView = layoutInflater.inflate(R.layout.contact_merge_item, mergeContactsContainerView, false);
            
            ImageView accountIcon = itemView.findViewById(R.id.icon);
            TextView  name        = itemView.findViewById(R.id.name);
            TextView  accountName = itemView.findViewById(R.id.account);
            
            name.setText(item.name);
            
            var icon = Phone.getIconForAccount(this, account);
   
            if(icon == null){
   
               icon = ContextCompat.getDrawable(this, R.drawable.question_icon);
               /*assert icon != null;
               u.setTintDrawable(icon, Color.WHITE);*/
            }
            
            accountIcon.setImageDrawable(icon);
            
            if(account != null){
               
               accountName.setText(account.name);
            }
            else{
               
               accountName.setText("-");
            }
            
            
            mergeContactsContainerView.addView(itemView);
         }
         
         contactsMainContainer.addView(mergeItemsView);
         contactsMergeItemsView.put(key, mergeItemsView);
      }
   }
   
   /**
    * Verilen kişileri birleştir.
    *
    * @param items birleştirileceklerin listesi. Bu listede kişileri gösteren anahtarlar var.
    */
   private void merge(LinkedList<String> items){
      
      if(items.size() == 0){
         
         u.toast(this, "Seçili bir kişi yok");
         return;
      }
   
      setProgress(true);
      
      Worker.onBackground(() -> {
         
         for(val item : items){
            
            //items, telefon numaraları.
            //Yani kişiler telefon numaralarına göre birbirinden ayırdediliyor ve gruplanıyor
            //mergeContacts ise bu numaralara karşılık birleştirilmesi muhtemel kişileri tutan bir map nesnesi
            val contacts = mergeContacts.get(item);
            //Bu aldığımız nesne, telefon numarası aynı olan kişiler
            //Bu kişilerin minimum sayısı 2
            //Bu sayı 1 olamaz çünkü o zaman birleştirme diye bir şey söz konusu olamaz.
            //Ayrıca bu nesne null olamaz çünkü zaten anahtarını kullanarak aldık
            assert contacts != null;
            
            boolean yes = mergeContacts(contacts);
            
            if(yes){
               
               Timber.d("Birleştirildi : %s", contacts);
               
               Worker.onMain(() -> {
                  
                  mergeContacts.remove(item);
                  removeView(item);
                  count.setCount(mergeContacts.size());
               });
            }
            else{
               
               Timber.d("Birleştirme başarısız : %s", contacts);
            }
         }
         
         Worker.onMain(() -> {
            
            setProgress(false);
            checkEmpty();
            EventBus.getDefault().post(new EventRefreshContacts());
            
            if(count.getCount() == 0) start();
         });
         
      }, "ContactsMergeActivity:Kişileri birleştirme");
   }
   
   private boolean mergeContacts(LinkedList<MergeContact> contacts){
      
      if(contacts.size() == 2)
         return mergeContacts(contacts.getFirst(), contacts.getLast());
      
      while(true){
         
         if(contacts.size() < 2) break;
         
         val bool = mergeProperContacts(contacts);
         
         if(!bool) break;
         
         contacts = getMergeContacts(getMergeContacts(contacts.get(0).numbers.get(0).getNumber()));
         
         mergeContacts(contacts);
      }
      
      return true;
   }
   
   private boolean mergeProperContacts(LinkedList<MergeContact> contacts){
      
      if(contacts.size() == 2)
         return mergeContacts(contacts.getFirst(), contacts.getLast());
      
      if(contacts.size() < 2) return false;
      
      //En uygun iki kişiyi seç
      
      val mapIdToContact = Stream.of(contacts).collect(Collectors.toMap(cx -> cx.id));
      val mapIdToAccount = Stream.of(contacts).collect(Collectors.toMap(cx -> cx.id, cy -> selectProperAccount(this, getAccounts(cy.numbers))));
      val googleAccounts = Stream.of(mapIdToAccount).filter(cx -> cx.getValue().type.equals(ACCOUNT_TYPE_GOOGLE)).map(cy -> mapIdToContact.get(cy.getKey())).toList();
      
      
      if(googleAccounts.size() == 0){
         
         return false;
      }
      
      if(googleAccounts.size() >= 2){
         
         return mergeContacts(googleAccounts.get(0), googleAccounts.get(1));
      }
      
      val nonGoogleAccounts = Stream.of(mapIdToAccount).filter(cx -> !cx.getValue().type.equals(ACCOUNT_TYPE_GOOGLE)).map(cy -> mapIdToContact.get(cy.getKey())).toList();
      
      if(nonGoogleAccounts.size() == 0) return false;
      
      val simAccounts = Stream.of(mapIdToAccount).filter(cx -> cx.getValue().type.equals(ACCOUNT_TYPE_SIM)).map(cy -> mapIdToContact.get(cy.getKey())).toList();
      
      if(simAccounts.size() == 0){
         
         return mergeContacts(googleAccounts.get(0), nonGoogleAccounts.get(0));
      }
      
      return mergeContacts(googleAccounts.get(0), simAccounts.get(0));
   }
   
   private boolean mergeContacts(MergeContact contact_0, MergeContact contact_1){
      
      val account_0 = selectProperAccount(this, getAccounts(contact_0.numbers));
      val account_1 = selectProperAccount(this, getAccounts(contact_1.numbers));
      
      if(account_0 == null) return false;
      
      if(account_0.equals(account_1)){
         
         if(account_0.type.equals(ACCOUNT_TYPE_GOOGLE) || account_0.type.equals(ACCOUNT_TYPE_SIM)){
            
            return mergeContacts(this, contact_0.rawId, contact_1.rawId);
         }
         
         Timber.w("Birleştirilmek istenen kişiler aynı hesaba ait ve ikisi de google veya sim değil");
         return false;
      }
      
      val account = selectProperAccount(this, new Account[]{account_0, account_1});
      
      if(account == null) return false;
      
      //Birleştirilecek kişilerin sayısının 2 olması, bu ikisini direk birleştirebiliriz anlamına geliyor.
      //Ancak yine de birleştirmenin google hesabı üzerine yapılması gerektiği kanaatindeyim (varsa tabiii)
      
      //Kanaatim doğru imiş. Google hesabı harici hesaplar birleşmiyor
      
      return account.equals(account_0) ?
             mergeContacts(this, contact_0.rawId, contact_1.rawId) :
             mergeContacts(this, contact_1.rawId, contact_0.rawId);
   }
   
   /**
    * Nesnedeki hesapları çıkar.
    *
    * @param numbers nesne
    * @return hesaplar
    */
   private Account[] getAccounts(List<ContactNumber> numbers){
      
      return Stream.of(numbers).map(ContactNumber::getAccount).toArray(Account[]::new);
   }
   
   private static boolean mergeContacts(Context context, String rawContactId1, String rawContactId2){
      
      ContentValues cv = new ContentValues();
      cv.put(ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER);
      cv.put(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, rawContactId1);
      cv.put(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2, rawContactId2);
      int rows = context.getContentResolver().update(ContactsContract.AggregationExceptions.CONTENT_URI, cv, null, null);
      
      return rows != 0;
   }
   
   private void checkEmpty(){
      
      if(mergeContacts == null) {
   
         emptyView.setVisibility(View.VISIBLE);
         return;
      }
      
      if(mergeContacts.size() == 0){
         
         emptyView.setVisibility(View.VISIBLE);
         count.setCount(0);
      }
      else{
         
         emptyView.setVisibility(View.GONE);
      }
   }
   
   private void removeView(String key){
      
      View view = contactsMergeItemsView.remove(key);
      
      TransitionManager.beginDelayedTransition(contactsMainContainer);
      contactsMainContainer.removeView(view);
   }
   
   /**
    * Tüm kişileri işaretle
    */
   private void markAll(boolean mark){
      
      if(contactsMergeItemsView == null){
         
         return;
      }
      
      if(contactsMergeItemsView.size() == 0){
         
         u.toast(this, "Seçili kişi yok");
         return;
      }
      
      val keys = contactsMergeItemsView.keySet();
      
      for(val key : keys){
         
         val view = contactsMergeItemsView.get(key);
         
         assert view != null;
         CheckBox checkBox = view.findViewById(R.id.check);
         
         if(checkBox.isChecked() != mark){
            
            checkBox.setChecked(mark);
         }
      }
   }
   
   /**
    * İşaretli olanları birleştir
    */
   private void mergeMarked(){
      
      merge(getSelectedItems());
   }
   
   private LinkedList<MergeContact> getMergeContacts(List<String> contactIdList){
      
      LinkedList<MergeContact> contacts = new LinkedList<>();
   
      String[] SIMPLE_CONTACT_COLUMNS = {
         
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
            ContactsContract.Contacts.NAME_RAW_CONTACT_ID
      
      };
      
      final Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            SIMPLE_CONTACT_COLUMNS,
            SIMPLE_CONTACT_COLUMNS[0] + " in (" + Contacts.joinToString(contactIdList) + ")",
            null,
            null
      );
      
      if(cursor == null) return contacts;
      
      final int idCol   = cursor.getColumnIndex(SIMPLE_CONTACT_COLUMNS[0]);
      final int nameCol = cursor.getColumnIndex(SIMPLE_CONTACT_COLUMNS[1]);
      final int rawCol  = cursor.getColumnIndex(SIMPLE_CONTACT_COLUMNS[2]);
      
      while(cursor.moveToNext()){
         
         final String id    = cursor.getString(idCol);
         final String rawId = cursor.getString(rawCol);
         final String name  = cursor.getString(nameCol);
         
         contacts.push(new MergeContact(id, rawId, name, getPNumbers(this, id)));
      }
      
      cursor.close();
      return contacts;
   }
   
   private List<String> getMergeContacts(String number){
      
      List<String> contacts = new ArrayList<>();
   
      String[] cols = {
            
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
      };
      
      final Cursor cursor = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            null,
            null,
            null);
      
      if(cursor == null) return contacts;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return contacts;
      }
      
      while(cursor.moveToNext()){
         
         String _number = cursor.getString(cursor.getColumnIndex(cols[0]));
         
         if(Contacts.matchNumbers(_number, number)){
            
            String id = cursor.getString(cursor.getColumnIndex(cols[1]));
            
            if(!contacts.contains(id)) contacts.add(id);
         }
      }
      
      cursor.close();
      return contacts;
   }
   
   /**
    * Birleştirilmesi gereken kişileri döndür.
    *
    * @return Kişiler
    */
   private Map<String, LinkedList<MergeContact>> getContactsForMerge(){
      
      val list = getMergeContacts(this);
      
      Timber.d("Birleştirilecek %d kişi var", list.size());
      
      removeIgnoredPairs(list);
      
      return list;
   }
   
   private LinkedList<String> getSelectedItems(){
      
      val items = new LinkedList<String>();
      
      if(contactsMergeItemsView == null || contactsMergeItemsView.size() == 0){
         
         return items;
      }
      
      val keys = contactsMergeItemsView.keySet();
      
      for(val key : keys){
         
         val view = contactsMergeItemsView.get(key);
         
         assert view != null;
         CheckBox checkBox = view.findViewById(R.id.check);
         
         if(checkBox.isChecked()) items.addLast(key);
      }
      
      return items;
   }
   
   /**
    * Progress'i göster ya da gizle.
    *
    * @param isVisible Göstermek için {@code true}
    */
   private void setProgress(boolean isVisible){
      
      Worker.onMain(() -> progress.setVisibility(isVisible ? View.VISIBLE : View.GONE));
   }
   
}
