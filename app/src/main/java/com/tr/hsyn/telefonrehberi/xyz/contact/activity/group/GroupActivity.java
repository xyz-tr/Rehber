package com.tr.hsyn.telefonrehberi.xyz.contact.activity.group;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Dialog;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.Group;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail.ContactDetailsGroups;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactGroupsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.GroupOptionsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.SelectAccountAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;


@SuppressWarnings("ConstantConditions")
public class GroupActivity extends AppCompatActivity implements GroupItemLongClickListener, CompoundButton.OnCheckedChangeListener{
   
   
   private Account              selectedAccout;
   private ContactGroupsAdapter adapter;
   private Account[]            googleAccounts;
   //private Account              localAccount = new Account("Local", "Phone");
   private Group                selectedGroup;
   private RecyclerView         recyclerView;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_groups);
      
      recyclerView = findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setBackgroundColor(u.lighter(u.getPrimaryColor(this), .9F));
      setupToolbar();
      googleAccounts = Contacts.getPhoneAccounts(this, getString(R.string.account_type_google));
   
   
      if(googleAccounts.length != 0){
   
         adapter = new ContactGroupsAdapter(this, googleAccounts);
         adapter.setLongClickListener(this);
         setAdapter();
         
         adapter.setLongClickListener((view, group, index) -> {
      
            onLongClickGroupItem(view, group, index);
      
            return true;
         });
      }
      else{
         
         findViewById(R.id.emptyView).setVisibility(View.VISIBLE);
      }
   }
   
   private void setAdapter(){
   
      recyclerView.setAdapter(adapter);
   }
   
   @Override
   protected void onResume(){
      
      super.onResume();
      
      
   }
   
   private void getGroupName(){
      
      Dialog.getString(
            this,
            getString(R.string.new_group_name),
            (s) -> createNewGroup(s, selectedAccout),
            (d, w) -> {});
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.groups_activity_menu, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
   
      if(item.getItemId() == R.id.groups_add_menu){
         
         onClickAddMenu();
         return true;
      }
      
      
      return super.onOptionsItemSelected(item);
   }
   
   private void onClickAddMenu(){
   
      if(googleAccounts.length == 0){
         
         Dialog.alert(this, "Kayıtlı bir hesap yok");
         return;
      }
      
      if(googleAccounts.length == 1){
         
         selectedAccout = googleAccounts[0];
         getGroupName();
      }
      else{
         
         selectAccountForGroup();
      }
   }
   
   private void selectAccountForGroup(){
      
      AlertDialog dialog         = new AlertDialog.Builder(this).create();
      View        accountsLayout = Dialog.inflate(this, R.layout.group_select_account);
      dialog.setView(accountsLayout);
   
      dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      RecyclerView recyclerView = accountsLayout.findViewById(R.id.recyclerViewAccounts);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      List<String>         accounts       = Stream.of(googleAccounts).map(x -> x.name).toList();
      SelectAccountAdapter accountAdapter = new SelectAccountAdapter(accounts);
      recyclerView.setAdapter(accountAdapter);
      accountAdapter.setClickListener(( i) -> {
   
         selectedAccout = googleAccounts[i];
   
         Worker.onMain(() -> {
      
            dialog.dismiss();
            getGroupName();
      
         }, 300);
      });
      
      
      
      dialog.show();
      
   }
   
   private void createNewGroup(String name, Account account){
      
      if(name.trim().isEmpty()){
         
         return;
      }
      
      if(ContactDetailsGroups.groupNameExist(this, name, account)){
         
         Show.globalMessage(this, "Bu isimde bir grup zaten var");
         
         return;
      }
      
      if(Contacts.createNewGroup(this, name, account)){
         
         int index = getAccountIndex(selectedAccout);
         
         ViewGroup groupsLayout = adapter.getGroupLayouts()[index];
         
         if(groupsLayout == null || groupsLayout.getChildCount() == 0){ return; }
         
         ViewGroup groupItem = (ViewGroup) getLayoutInflater().inflate(R.layout.group_item, groupsLayout, false);
         
         ImageView groupIcon = groupItem.findViewById(R.id.icon);
         TextView  groupName = groupItem.findViewById(R.id.groupName);
         groupIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.groups_else));
         
         groupName.setText(name);
         
         groupItem.setAlpha(0F);
         groupItem.setTranslationY(-100f);
         groupsLayout.addView(groupItem);
         
         
         ViewCompat.animate(groupItem)
                   .alpha(1f)
                   .translationY(0f)
                   .setDuration(1000)
                   .start();
         
         Switch groupSwitch = groupItem.findViewById(R.id.groupSwitch);
         groupSwitch.setChecked(true);
         groupItem.setBackgroundResource(MainActivity.getWellRipple());
         
         Group group = Contacts.getGroup(this, account, name);
         
         
         if(group == null){ return; }
         
         groupSwitch.setOnCheckedChangeListener(this);
         groupSwitch.setTag(group);
         groupItem.setTag(group);
         
         groupItem.setOnClickListener(v -> {
            
            Intent intent = new Intent(getApplicationContext(), LabeledContactsActivity.class);
            intent.putExtra(LabeledContactsActivity.EXTRA_LABEL, group);
            
            startActivity(intent);
            Bungee.slideUp(getApplicationContext());
         });
         
         groupItem.setOnLongClickListener(v -> onLongClickGroupItem(v, group, index));
         
         TextView groupCount = groupItem.findViewById(R.id.groupCount);
         
         if(group.getMembersCount() >= 0){
            
            groupCount.setText(String.valueOf(group.getMembersCount()));
            return;
         }
         
         int count = Contacts.getGroupCount(this, group);
         
         
         if(count >= 0){
            
            groupCount.setText(String.valueOf(group.getMembersCount()));
            groupCount.setVisibility(View.VISIBLE);
         }
         
         //setAdapter();
         return;
      }
      
      Show.globalMessage(this, getString(R.string.group_not_added, name));
   }
   
   private int getAccountIndex(Account account){
      
      for(int i = 0; i < googleAccounts.length; i++){
         
         if(account == googleAccounts[i]){
            
            return i;
         }
      }
      
      return -1;
   }
   
   private void setupToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      toolbar.setBackgroundColor(u.getPrimaryColor(this));
      
      getSupportActionBar().setTitle(getString(R.string.groups));
      
      new ReColor(this).setStatusBarColor(u.colorToString(ColorController.getLastColor()),
                                          u.colorToString(u.darken(u.getPrimaryColor(this), .9)),
                                          1000);
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
      super.onActivityResult(requestCode, resultCode, data);
   
   
      if(requestCode == 1 && resultCode == Activity.RESULT_OK && data != null){
   
         Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
   
         if(uri != null){
   
            setRingtoneToGroup(selectedGroup, uri);
         }
         
      }
      
      if(requestCode == 11 && resultCode == Activity.RESULT_OK && data != null){
         
         Group group = data.getParcelableExtra("group");
         Contacts.getGroupCount(this, group);
         
         adapter.updateGroupCount(group);
      }
   }
   
   private void setRingtoneToGroup(Group group, Uri ringToneUri){
      
      group.setRingTone(String.valueOf(ringToneUri));
      
      List<String> members = Contacts.getGroupMembers(this, group.getId());
   
      if(!members.isEmpty()){
   
         ContentValues values = new ContentValues();
   
         values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, ringToneUri.toString());
         String where = u.format("%s in (%s)", ContactsContract.Contacts._ID, Contacts.joinToString(members));
         
         getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, values, where, null);
      }
   
      Ringtone ringtone = RingtoneManager.getRingtone(this, ringToneUri);
   
      if(ringtone != null){
   
         if(!Contacts.setRingToneOfGroup(this, group.getId(), ringToneUri)){
   
            Timber.w("Grup için Zil sesi kaydedilemedi");
            return;
         }
   
         Show.globalMessage(this, getString(R.string.ringtone_set, ringtone.getTitle(this)));
   
         Show.globalMessage(this, "Zil sesi ayarlandı");
         
         
         
      }
   }
 
   @Override
   public boolean onLongClickGroupItem(View view, Group group, int index){
      
      String ringtoneName = group.getRingTone() == null ?
                            Contacts.getDefaultRingTone(this) :
                            Contacts.getRingtoneName(this, group.getRingTone());
      String showHide = group.isVisable() ? getString(R.string.hide) : getString(R.string.show);
      
      List<String> optionsString = new ArrayList<>();
      
      optionsString.add(showHide);
      optionsString.add(getString(R.string.delete));
      optionsString.add(getString(R.string.send_message));
      optionsString.add(getString(R.string.assign_ringtone, ringtoneName));
      optionsString.add(getString(R.string.rename));
      
      
      AlertDialog dialog = new AlertDialog.Builder(this).create();
      dialog.setCancelable(true);
      dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      View options = Dialog.inflate(this, R.layout.group_options);
      dialog.setView(options);
      
      GroupOptionsAdapter adapter = group.isReadOnly() ?
                                    new GroupOptionsAdapter(optionsString, Arrays.asList(1, 4)) :
                                    new GroupOptionsAdapter(optionsString);
      
      
      adapter.setClickListener(( i) -> {
         
         switch(i){
            
            case 0:
               
               Switch sw = view.findViewById(R.id.groupSwitch);
               sw.setChecked(!group.isVisable());
               break;
            
            
            case 1:
               
               if(group.isReadOnly()) return;
               deleteGroup(group, view, index);
               break;
            
            case 2:
               
               TextView sizeTextView = view.findViewById(R.id.groupCount);
               
               if(sizeTextView != null){
                  
                  u.keepGoing(() -> {
                     
                     int size = Integer.parseInt(sizeTextView.getText().toString());
                     
                     if(size == 0){
                        
                        dialog.dismiss();
                        Show.globalMessage(this, getString(R.string.no_member));
                        return;
                     }
                     
                     sendMessageToGroup(this, group);
                     
                  });
               }
               
               break;
            
            case 3:
               
               selectedGroup = group;
               selectRingTone();
               break;
            
            case 4:
   
               if(group.isReadOnly()) return;
               renameGroup(group, view);
               
               break;
         }
         
         Worker.onMain(dialog::dismiss, 300);
      });
      
      
      RecyclerView recyclerView = options.findViewById(R.id.recyclerViewGroup);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);
      
      Drawable  drawable  = ((ImageView) view.findViewById(R.id.icon)).getDrawable();
      ImageView imageView = options.findViewById((R.id.groupIcon));
      imageView.setImageDrawable(drawable);
      
      TextView textView = options.findViewById(R.id.groupTitle);
      textView.setText(getTranslatedGroupName(this, group.getTitle()));
      dialog.show();
      
      return true;
   }
   
   @NonNull
   public static String getTranslatedGroupName(Context context, @NonNull String groupName){
      
      switch(groupName){
         
         case "My Contacts": return context.getString(R.string.contact_group_my_contacts);
         case "Friends": return context.getString(R.string.contact_group_friends);
         case "Family": return context.getString(R.string.contact_group_family);
         case "Coworkers": return context.getString(R.string.contact_group_coworkers);
         case "Starred in Android": return context.getString(R.string.contact_group_started);
         default:return groupName;
      }
   }
   
   private void renameGroup(Group group, View view){
      
      Dialog.getString(
            this,
            "Yeni isim",
            group.getTitle(),
            (s) -> {
               
               if(!s.trim().isEmpty() && !s.equals(group.getTitle())){
                  
                  Account account = Contacts.getGroupAccount(getApplicationContext(), group.getId());
   
                  if(account == null){ return; }
                  
                  if(ContactDetailsGroups.groupNameExist(this, s, account)){
                     
                     Show.globalMessage(this, "Aynı isimde bir grup zaten var");
                     return;
                  }
                  
                  if(Contacts.updateGroupName(this, group.getId(), s)){
                     
                     group.setTitle(s);
                     
                     Show.globalMessage(this, "Grup güncellendi");
                     
                  }
                  
                  ((TextView) view.findViewById(R.id.groupName)).setText(s);
                  
               }
               
            },
            (d, w) -> {});
   }
   
   private void selectRingTone(){
      
      startActivityForResult(
            u.createIntent(RingtoneManager.ACTION_RINGTONE_PICKER)
             .putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_ringtone))
             .putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE),
            1);
   }
   
   public static void sendMessageToGroup(Context context, Group group){
      
      List<String> members = Contacts.getGroupMembers(context, group.getId());
      
      if(members.isEmpty()){
         
         u.toast(context, context.getString(R.string.no_member));
         return;
      }
      
      List<String> numbers = Contacts.getNumbers(context, members);
      
      
      if(numbers.isEmpty()){ return; }
      
      Intent i = u.createIntent(Intent.ACTION_SENDTO);
      
      i.setData(Uri.parse(u.format("smsto:%s", Contacts.joinToString(numbers, ";"))));
      
      if(i.resolveActivity(context.getPackageManager()) != null){
         
         context.startActivity(i);
         
         u.toast(context, "Mesajlar ayarlanıyor");
      }
      else{
         
         u.toast(context, "Bu işlemi gerçekleştirecek bir uygulaman yok");
      }
      
      
   }
   
   private void deleteGroup(Group group, View view, int index){
      
      Dialog.confirm(
            this,
            getString(R.string.is_delete, getTranslatedGroupName(GroupActivity.this, group.getTitle())),
            (di, w) -> {
               
               if(Contacts.deleteGroup(getApplicationContext(), group.getId())){
                  
                  ViewGroup viewGroup = adapter.getGroupLayouts()[index];
                  
                  
                  ViewCompat
                        .animate(view)
                        .translationZ(-400F)
                        .translationX(900F)
                        .scaleY(0.5F)
                        .setDuration(900)
                        .alpha(0F)
                        .withEndAction(() -> viewGroup.removeView(view))
                        .start();
                  
                  
                  Show.globalMessage(
                        GroupActivity.this,
                        getString(R.string.deleted, group.getTitle()));
                  
               }
               else{
                  
                  Show.globalMessage(
                        GroupActivity.this,
                        getString(R.string.not_deleted, group.getTitle()));
               }
               
               
            },
            (d, w) -> {},
            getString(R.string.delete)
      );
   }
   
   @Override
   public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
      
      Group group = (Group) buttonView.getTag();
      
      if(group == null) return;
   
      if(isChecked){
   
         if(!group.isVisable()){
   
            if(Contacts.setGroupVisibility(buttonView.getContext(), group.getId(), "1")){
      
               group.setVisable(true);
               Show.globalMessage(this, getString(R.string.msg_group_visable, getTranslatedGroupName(this, group.getTitle())));
            }
         }
      }
      else{
   
         if(group.isVisable()){
   
            if(Contacts.setGroupVisibility(buttonView.getContext(), group.getId(), "0")){
   
               group.setVisable(false);
   
               Show.globalMessage(this, getString(R.string.msg_group_visable, getTranslatedGroupName(this, group.getTitle())));
            }
         }
      }
   }
   
   @Override
   public void onBackPressed(){
      
      finishAfterTransition();
      Bungee.slideUp(this);
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
   
      onBackPressed();
      return true;
   }
   
}
