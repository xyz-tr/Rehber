package com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.databinding.ActivityMergedContactsBinding;
import com.tr.hsyn.telefonrehberi.util.Toolbarx;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.ContactNumber;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.MergeContact;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.val;
import timber.log.Timber;


@SuppressLint("Registered")
public class MergedContactsActivity extends AppCompatActivity{
   
   private final    Count                                 count   = new Count(0);
   private          ActivityMergedContactsBinding         binding;
   private          Map<String, View>                     viewMap = new HashMap<>();
   private          int                                   color;
   private          LayoutInflater                        layoutInflater;
   private volatile Map<String, LinkedList<MergeContact>> contacts;
   private          int                                   markUnmarkClickCount;
   private          boolean                               loaded = true;
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      //setContentView(R.layout.activity_merged_contacts);
      
      binding = DataBindingUtil.setContentView(this, R.layout.activity_merged_contacts);
      
      setViews();
      binding.setCount(count);
      
      start();
   }
   
   private void start(){
      
      if(loaded){
   
         binding.progress.setVisibility(View.VISIBLE);
         loaded = false;
         Worker.onBackground(this::getMergeContacts, this::takeContacts);
      }
   }
   
   private void takeContacts(Map<String, LinkedList<MergeContact>> contacts){
      
      this.contacts = contacts;
      count.setCount(contacts.size());
      
      color          = u.getPrimaryColor(this);
      layoutInflater = getLayoutInflater();
      
      for(val item : contacts.entrySet()){
         
         createView(item);
      }
      
      binding.progress.setVisibility(View.GONE);
      checkEmpty();
      
      loaded = true;
   }
   
   @SuppressLint("SetTextI18n")
   private void createView(Map.Entry<String, LinkedList<MergeContact>> map){
      
      val       key                    = map.getKey();
      val       numberAndId            = key.split("-");
      val       number                 = numberAndId[0];
      val       id                     = numberAndId[1];
      View      itemsContainer         = layoutInflater.inflate(R.layout.contacts_merged_items, binding.contactsContainer, false);
      TextView  title                  = itemsContainer.findViewById(R.id.title);
      View      header                 = itemsContainer.findViewById(R.id.header);
      CheckBox  checkBox               = header.findViewById(R.id.check);
      ViewGroup mergeContactsContainer = itemsContainer.findViewById(R.id.mergeContactsContainer);
      
      header.setBackgroundColor(u.lighter(color, 0.9F));
      title.setText(CallStory.formatNumberForDisplay(number) + "-" + id);
      checkBox.setButtonTintList(ColorStateList.valueOf(u.lighter(color, 0.2F)));
      header.findViewById(R.id.mergeIcon).setOnClickListener((v) -> separate(Collections.singleton(key)));
      
      viewMap.put(key, itemsContainer);
      
      for(val item : map.getValue()){
         
         View itemView = layoutInflater.inflate(R.layout.contact_merge_item, mergeContactsContainer, false);
         
         ImageView accountIcon = itemView.findViewById(R.id.icon);
         TextView  name        = itemView.findViewById(R.id.name);
         TextView  accountName = itemView.findViewById(R.id.account);
         
         name.setText(item.name);
         accountName.setText(item.numbers.get(0).getAccountName());
         accountIcon.setImageDrawable(Phone.getIconForAccount(this, item.numbers.get(0).getAccount()));
         
         mergeContactsContainer.addView(itemView);
      }
      
      binding.contactsContainer.addView(itemsContainer);
   }
   
   private void separateContacts(String key){
      
      val item = contacts.get(key);
      
      if(item != null){
         
         if(separateContacts(this, item.pop().rawId, item.pop().rawId)){
            
            contacts.remove(key);
            
            Worker.onMain(() -> {
               
               removeView(key);
               count.setCount(contacts.size());
               checkEmpty();
            });
            
            Timber.d("Ayrıldı : %s", key);
            
         }
         else{
            
            Timber.d("Ayırma başarısız : %s", key);
         }
      }
   }
   
   private void checkEmpty(){
      
      if(contacts == null){
         
         binding.empty.setVisibility(View.VISIBLE);
         return;
      }
      
      if(contacts.size() == 0){
         
         binding.empty.setVisibility(View.VISIBLE);
         count.setCount(0);
      }
      else{
         
         binding.empty.setVisibility(View.GONE);
      }
   }
   
   public static boolean separateContacts(Context context, String rawContactId1, String rawContactId2){
      
      ContentValues cv = new ContentValues();
      cv.put(ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_SEPARATE);
      cv.put(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, rawContactId1);
      cv.put(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2, rawContactId2);
      int rows = context.getContentResolver().update(ContactsContract.AggregationExceptions.CONTENT_URI, cv, null, null);
      
      return rows != 0;
   }
   
   private void removeView(String key){
      
      View view = viewMap.remove(key);
      
      TransitionManager.beginDelayedTransition(binding.contactsContainer);
      binding.contactsContainer.removeView(view);
      
   }
   
   private Map<String, LinkedList<MergeContact>> getMergeContacts(){
      
      val mergedContacts = getMergedContacts(this);
      Timber.d("Bağlı kişi sayısı : %d", mergedContacts.size());
      
      Map<String, LinkedList<MergeContact>> contacts = new HashMap<>();
      
      if(mergedContacts.size() == 0) return contacts;
      
      
      val accountTypeMessenger = getString(R.string.account_type_messenger);
      
      for(val item : mergedContacts){
         
         val contact_1 = getContact(item.rawId_1);
         val contact_2 = getContact(item.rawId_2);
         
         if(contact_1 != null && contact_2 != null){
            
            if(contact_1.numbers.get(0).getAccountType().equals(accountTypeMessenger) ||
               contact_2.numbers.get(0).getAccountType().equals(accountTypeMessenger)){
               
               continue;
            }
            
            val list = new LinkedList<MergeContact>();
            list.addLast(contact_1);
            list.addLast(contact_2);
            
            contacts.put(Contacts.createKeyFromNumber(contact_1.numbers.get(0).getNumber()) + "-" + contact_1.id, list);
         }
      }
      
      return contacts;
   }
   
   private static LinkedList<MergedContact> getMergedContacts(@NonNull final Context context){
      
      final Cursor cursor = context.getContentResolver().query(
            ContactsContract.AggregationExceptions.CONTENT_URI,
            null,
            ContactsContract.AggregationExceptions.TYPE + " = ?",
            new String[]{String.valueOf(ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER)},
            null);
      
      if(cursor == null) return new LinkedList<>();
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return new LinkedList<>();
      }
      
      final LinkedList<MergedContact> mergedContacts = new LinkedList<>();
      
      while(cursor.moveToNext()){
         
         final String r1   = cursor.getString(cursor.getColumnIndex(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1));
         final String r2   = cursor.getString(cursor.getColumnIndex(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2));
         final int    type = cursor.getInt(cursor.getColumnIndex(ContactsContract.AggregationExceptions.TYPE));
         
         mergedContacts.push(new MergedContact(r1, r2, type));
      }
      
      
      cursor.close();
      return mergedContacts;
   }
   
   private MergeContact getContact(String rawId){
      
      val rawContact = Contacts.getRawContact(this, rawId);
      
      if(rawContact == null) return null;
      
      val number = getNumber(rawId);
      
      return new MergeContact(rawContact.getContactId(), rawId, rawContact.getName(), Collections.singletonList(new ContactNumber(number, -1, rawContact.getAccount(), null)));
   }
   
   private String getNumber(String rawId){
      
      String[] cols = {
            
            ContactsContract.CommonDataKinds.Phone.NUMBER
      };
      
      val cursor = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            cols,
            ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " = ?",
            new String[]{rawId},
            null
      );
      
      if(cursor == null) return null;
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return null;
      }
      
      String number = null;
      
      if(cursor.moveToFirst()) number = cursor.getString(cursor.getColumnIndex(cols[0]));
      
      cursor.close();
      return number;
   }
   
   private void setViews(){
      
      setToolbar();
   
      ColorController.setIndaterminateProgressColor(this, binding.progress);
   }
   
   /**
    * Toolbar'ı ayarla
    */
   private void setToolbar(){
      
      //Toolbar toolbar = findViewById(R.id.toolbar);
      Toolbarx.setToolbar(this, binding.toolbar);
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.activity_merged_contacts_menu, menu);
      
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
         
         case R.id.contacts_merge_unmark_all:
            
            markAll(false);
            return true;
         
         case R.id.contacts_merge_mark_all:
            
            markAll(true);
            return true;
         
         case R.id.merge_contacts_refresh:
            
            refresh();
            return true;
         
         case R.id.contacts_merge_separate:
         case R.id.contacts_merge_merge_marked:
            
            separate(getSelectedItems());
            return true;
      }
      
      
      return false;
   }
   
   private void refresh(){
      
      binding.progress.setVisibility(View.VISIBLE);
      binding.contactsContainer.removeAllViews();
      
      if(viewMap != null) viewMap.clear();
      
      if(contacts != null) contacts.clear();
      
      checkEmpty();
      
      start();
   }
   
   private void separate(Collection<String> items){
      
      //val items = getSelectedItems();
      
      if(viewMap == null || viewMap.size() == 0 || items.size() == 0){
         
         u.toast(this, "Seçili kişi yok");
         return;
      }
      
      binding.progress.setVisibility(View.VISIBLE);
      
      Worker.onBackground(() -> {
         
         for(val item : items){
            
            separateContacts(item);
         }
         
         Worker.onMain(() -> {
   
            binding.progress.setVisibility(View.GONE);
   
            checkEmpty();
            
            if(count.getCount() == 0) start();
   
            EventBus.getDefault().post(new EventRefreshContacts());
         });
      }, "ContactsMergeActivity:Kişileri ayırma");
   }
   
   /**
    * Tüm kişileri işaretle
    */
   private void markAll(boolean mark){
      
      if(viewMap == null){
         
         return;
      }
      
      val keys = viewMap.keySet();
      
      for(val key : keys){
         
         val view = viewMap.get(key);
         
         assert view != null;
         CheckBox checkBox = view.findViewById(R.id.check);
         
         if(checkBox.isChecked() != mark){
            
            checkBox.setChecked(mark);
         }
      }
   }
   
   private LinkedList<String> getSelectedItems(){
      
      val items = new LinkedList<String>();
      
      if(viewMap == null || viewMap.size() == 0){
         
         return items;
      }
      
      val keys = viewMap.keySet();
      
      for(val key : keys){
         
         val view = viewMap.get(key);
         
         assert view != null;
         CheckBox checkBox = view.findViewById(R.id.check);
         
         if(checkBox.isChecked()) items.addLast(key);
      }
      
      return items;
   }
   
}
