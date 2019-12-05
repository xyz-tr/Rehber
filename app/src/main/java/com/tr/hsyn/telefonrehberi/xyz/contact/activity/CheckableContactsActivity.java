package com.tr.hsyn.telefonrehberi.xyz.contact.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Dialog;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.Group;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.GroupActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.CheckableContactsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.CheckedChangeListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import timber.log.Timber;


public class CheckableContactsActivity extends AppCompatActivity implements CheckedChangeListener{
   
   public static final String EXTRA_LABEL = "label";
   
   private List<String>       idList       = new ArrayList<>();
   private List<IMainContact> contacts     = new ArrayList<>();
   private List< IMainContact>  checkedItems = new ArrayList<>();
   private boolean            saveOkey;
   private Group              group;
   private RecyclerView       recyclerView;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      setContentView(R.layout.activity_checkable_contacts);
      super.onCreate(savedInstanceState);
      
      setupToolbar();
      
      recyclerView = findViewById(R.id.recyclerView);
      
      if(!getArguments()){
         
         onBackPressed();
      }
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.checkable_contacts_activity, menu);
      return super.onCreateOptionsMenu(menu);
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      
      if(item.getItemId() == R.id.menu_accept){
         
         selectionFinish();
         return true;
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public void checkedChange(View view, boolean isChecked, int index){
      
      if(isChecked && !checkedItems.contains(contacts.get(index))){
         
         checkedItems.add(contacts.get(index));
         return;
      }
      
      if(!isChecked){
         
         checkedItems.remove(contacts.get(index));
      }
   }
   
   @Override
   public void onBackPressed(){
      
      if(checkedItems.size() != 0 && !saveOkey){
         
         String groupName = GroupActivity.getTranslatedGroupName(this, group.getTitle());
         
         Spanner spanner = new Spanner("Seçilen kişiler ")
               .append(u.format("%s", groupName), Spans.bold())
               .append(" grubuna eklensin mi?");
         
         Dialog.confirm(
               this,
               spanner,
               (dialog, which) -> selectionFinish(),
               (dialog, which) -> {
                  checkedItems.clear();
                  onBackPressed();
               }
         );
         
         return;
      }
      
      super.onBackPressed();
      Bungee.slideDown(this);
   }
   
   private void setupToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      toolbar.setBackgroundColor(u.getPrimaryColor(this));
      
      
      new ReColor(this).setStatusBarColor(u.colorToString(ColorController.getLastColor()),
                                          u.colorToString(u.darken(u.getPrimaryColor(this), .9)),
                                          1000);
   }
   
   private void selectionFinish(){
      
      Intent intent = new Intent();
     // intent.putParcelableArrayListExtra(LabeledContactsActivity.EXTRA_LIST, (ArrayList<? extends Parcelable>) checkedItems);
      setResult(Activity.RESULT_OK, intent);
      
      Paper.book().write("CheckableContacts", checkedItems);
      
      saveOkey = true;
      onBackPressed();
      
   }
   
   private boolean getArguments(){
      
      Intent intent = getIntent();
      
      if(intent == null){
         
         return false;
      }
      
      if(!intent.hasExtra(EXTRA_LABEL)){
   
         Timber.w("Gerekli bilgiler sağlanmadı");
         return false;
      }
      
      group = intent.getParcelableExtra(EXTRA_LABEL);
      
      contacts = Contacts.getLabeledContacts(this, Contacts.getLabeledContactIds(this, group));
      
      idList = Contacts.getNotLabeledContactIds(this, group, Stream.of(contacts).map(IMainContact::getContactId).toList());
      
      contacts = Contacts.getLabeledContacts(this, idList);
      
      if(contacts != null){
         
         recyclerView.setAdapter(new CheckableContactsAdapter(contacts).setCheckedChangeListener(CheckableContactsActivity.this));
         getSupportActionBar().setTitle(GroupActivity.getTranslatedGroupName(this, group.getTitle()));
      }
      
      return idList != null;
   }
   
}
