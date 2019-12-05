package com.tr.hsyn.telefonrehberi.xyz.contact.activity.group;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Dialog;
import com.tr.hsyn.telefonrehberi.util.PerfectSort;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.Group;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.CheckableContactsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail.ContactDetailsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.LongClickListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.paperdb.Paper;
import timber.log.Timber;


@SuppressWarnings("ConstantConditions")
public class LabeledContactsActivity extends AppCompatActivity implements ItemSelectListener, LongClickListener{
   
   public static final                    String            EXTRA_LABEL              = "label";
   public static final                    String            EXTRA_LIST              = "list";
   private final                          int               REQUEST_CODE_NEW_MEMBERS = 8;
   private                                Group             label;
   private                                List<IMainContact> contacts                 = new ArrayList<>();
   private                                boolean           isEditable;
   private  ProgressBar       progressBar;
   private RecyclerView      recyclerView;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_labeled_contacts);
      
      if(!getArguments()){
         
         onBackPressed();
         return;
      }
      
      setupToolbar();
      progressBar = findViewById(R.id.progressBar);
      recyclerView = findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      
      Worker.onBackground(() -> {
         
         List<String> ids = Contacts.getLabeledContactIds(this, label);
         contacts   = Contacts.getLabeledContacts(this, ids);
         isEditable = Contacts.isEditableLabel(this, label.getId());
         
         
         recyclerView.post(() -> {
            
            if(contacts != null){
               
               recyclerView.setAdapter(new ContactAdapter(contacts, this).setLongClickListener(this));
               
               assert getSupportActionBar() != null;
               getSupportActionBar().setTitle(GroupActivity.getTranslatedGroupName(this, label.getTitle()));
               getSupportActionBar().setSubtitle(String.valueOf(contacts.size()));
               
            }
            
            progressBar.setVisibility(View.GONE);
         });
      }, "LabeledContactsActivity:Bir gruba ait kişileri alma - " + label.getTitle());
   }
   
   private void setupToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      toolbar.setBackgroundColor(u.getPrimaryColor(this));
      
      getSupportActionBar().setTitle(getString(R.string.groups));
      
      new ReColor(this).setStatusBarColor(u.colorToString(ColorController.getLastColor()),
                                          u.colorToString(u.darken(u.getPrimaryColor(this), .9)),
                                          1000);
      
      
      toolbar.setNavigationOnClickListener((v) -> onBackPressed());
   }
   
   private boolean getArguments(){
      
      Intent i = getIntent();
      
      if(i == null || !i.hasExtra(EXTRA_LABEL)){
         
         Timber.w("Gerekli bilgiler sağlanmadı");
         return false;
         
      }
      
      label = i.getParcelableExtra(EXTRA_LABEL);
      
      Timber.d(label.toString());
      return true;
   }
   
   @Override
   public void onBackPressed(){
      
      setResult(RESULT_OK, u.createIntent("group", label));
      
      finishAfterTransition();
      Bungee.slideDown(this);
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      if(requestCode == REQUEST_CODE_NEW_MEMBERS && resultCode == Activity.RESULT_OK && data != null){
         
         //List<IMainContact> list = data.getParcelableArrayListExtra(EXTRA_LIST);
         List<IMainContact> list = Paper.book().read("CheckableContacts");
         Paper.book().delete("CheckableContacts");
         
         if(list != null){
            
            checkedItemsResult(list);
         }
      }
   }
   
   private void checkedItemsResult(List<IMainContact> contacts){
      
      if(contacts.size() == 0){ 
         
         Timber.d("liste boş geldi");
         return; 
      }
      
      this.contacts.addAll(contacts);
   
      Collections.sort(this.contacts, (x, y) -> PerfectSort.compare(x.getName(), y.getName()));
      recyclerView.setAdapter(new ContactAdapter(this.contacts, this).setLongClickListener(this));
      recyclerView.getAdapter().notifyDataSetChanged();
      
      getSupportActionBar().setSubtitle(String.valueOf(this.contacts.size()));
   
      Worker.onBackground(() -> {
         
         for(IMainContact contact : contacts){
   
            if(Contacts.addLabel(this, contact.getContactId(), label.getId())){
               
               Timber.d("label added : %s -> %s", label.getTitle(), contact.getContactId());
               
               if(label.getRingTone() != null){
      
                  Contacts.setRingTone(this, contact.getContactId(), label.getRingTone());
               }
            }
            else{
   
               Timber.d("label not added : %s", label.getTitle());
            }
         }
      }, "LabeledContactsActivity:Gruba kişi ekleme");
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.labeled_contacts_activity, menu);
      return super.onCreateOptionsMenu(menu);
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      
      if(item.getItemId() == R.id.menu_add_user){
         
         addUser();
         return true;
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   private void addUser(){
      
      if(!isEditable){
         
         Dialog.alert(this, getString(R.string.group_alert));
         return;
      }
      
      startActivityForResult(
            u.createIntent(this, CheckableContactsActivity.class)
             .putExtra(CheckableContactsActivity.EXTRA_LABEL, label),
            REQUEST_CODE_NEW_MEMBERS
      );
      
      Bungee.slideUp(this);
   }
   
   @Override
   public void onLongClick(View view, int index){
      
      if(!isEditable){
         
         Dialog.alert(this, getString(R.string.group_alert));
         return;
      }
      
      IMainContact contact = contacts.get(index);
      
      String name = contact.getName();
      
      if(name == null || name.trim().isEmpty()){
         
         name = "?";
      }
      
      if(name.length() > 1){
         
         name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
      }
      
      Spanner spanner = new Spanner().append(u.format("%s", name), Spans.bold())
                                     .append(u.format(" %s grubundan çıkarılsın mı?", GroupActivity.getTranslatedGroupName(this, label.getTitle())));
      
      Dialog.confirm(
            this,
            spanner,
            (d, w) -> removeFromGroup(contact, index),
            (d, w) -> {}
      );
   }
   
   private void removeFromGroup(IMainContact contact, int index){
      
      String contactId = contact.getContactId();
      boolean result = Contacts.removeLabel(this, contactId, label.getId());
      
      if(result){
         
         contacts.remove(index);
         recyclerView.getAdapter().notifyItemRemoved(index);
         //label.setMembersCount(label.getMembersCount() - 1);
         
         if(!Contacts.isDefaultRingTone(this, contactId)){
            
            if(!Contacts.setDefaultRingTone(this, contactId)){
               
               Timber.w("Zil sesi varsayılan olarak ayarlanamadı");
            }
            else{
               
               Timber.w("Zil sesi varsayılan olarak ayarlandı");
            }
         }
         
         if(contacts.size() == 0){
            
            onBackPressed();
         }
         else{
            
            getSupportActionBar().setSubtitle(String.valueOf(contacts.size()));
         }
         
         Show.globalMessage(this, getString(R.string.msg_removed_from_group, contact.getName()));
      }
   }
   
   @Override
   public void onItemSelected(int position){
      
      IMainContact contact = contacts.get(position);
      
      ContactDetailsActivity.startActivity(this, contact.getContactId());
   }
   
}
