package com.tr.hsyn.telefonrehberi.xyz.contact.activity.account;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Toolbarx;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.AccountAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_AccountBox;
import com.tr.hsyn.telefonrehberi.xyz.contact.fragment.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.val;
import timber.log.Timber;


public class AccountsActivity extends AppCompatActivity implements ItemSelectListener{
   
   private RecyclerView       recyclerView;
   private ProgressBar        progressBar;
   private List<AccountModel> accounts;
   private Account            selectedAccount;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.activity_phone_accounts);
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      Toolbarx.setToolbar(this, toolbar);
      
      recyclerView = findViewById(R.id.recyclerView);
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setBackgroundColor(u.lighter(u.getPrimaryColor(this), 0.7F));
      
      progressBar = findViewById(R.id.progress);
      //TextView contactCount = findViewById(R.id.contactCount);
      
      selectedAccount = Kickback_AccountBox.getInstance().getSelectedAccount();
      
      Worker.onBackground(
            this::generateModels,
            this::setAccounts
      );
   }
   
   private List<AccountModel> generateModels(){
      
      List<AccountModel> accountModelList = new ArrayList<>();
      
      Account[] accounts = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.getAccounts(this);
      
      Timber.d(Arrays.toString(accounts));
      
      for(Account account : accounts){
         
         AccountModel model;
         
         if(account.name.equals("Messenger")){
            
            val list = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.getMessengerContactsIds(this);
            
            if(list == null){
               
               return null;
            }
            
            model = new AccountModel(
                  account.name,
                  account.type,
                  Stream.of(list).distinct().toList().size());
         }
         else{
            
            model = new AccountModel(
                  account.name,
                  account.type,
                  getContactCount(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI, account));
         }
         
         if(isNotContainsModel(accountModelList, model)){
            accountModelList.add(model);
         }
      }
      
      accountModelList.add(new AccountModel(getString(R.string.all_contacts), "all", getContactCount()));
      
      return accountModelList;
   }
   
   private boolean isNotContainsModel(List<AccountModel> accounts, AccountModel model){
      
      for(AccountModel accountModel : accounts){
         if(accountModel.name.equals(model.name) && accountModel.type.equals(model.type)){
            return false;
         }
      }
      
      return true;
      
   }
   
   private void setAccounts(List<AccountModel> accounts){
      
      this.accounts = accounts;
   
      if(selectedAccount == null){
   
         recyclerView.setAdapter(new AccountAdapter(accounts, this, null));
      }
      else {
   
         recyclerView.setAdapter(new AccountAdapter(accounts, this, new android.accounts.Account(selectedAccount.name, selectedAccount.type)));
      }
      
      
      progressBar.setVisibility(View.GONE);
   }
   
   public static int getContactCount(Context context, Uri uri, Account account){
      
      Cursor cursor = context.getContentResolver().query(
            uri,
            null,
            ContactsContract.RawContacts.ACCOUNT_NAME + "=? and " + ContactsContract.RawContacts.ACCOUNT_TYPE + "=?",
            new String[]{account.name, account.type},
            null
      );
      
      if(cursor == null){
         
         Timber.w(context.getString(R.string.msg_cursor_error));
         return -1;
      }
      
      int count = cursor.getCount();
      cursor.close();
      
      return count;
   }
   
   private int getContactCount(){
      
      Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null
      );
      
      if(cursor == null){
         
         Timber.w(getString(R.string.msg_cursor_error));
         
         return -1;
      }
      
      int count = cursor.getCount();
      cursor.close();
      
      return count;
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      
      Bungee.slideLeft(this);
   }
   
   @Override
   public void onItemSelected(int position){
      
      Timber.d("Seçilen hesap : %s", accounts.get(position));
      
      Intent i = new Intent();
      i.putExtra(Contacts.EXTRA_SELECTED_ACCOUNT, accounts.get(position));
      
      setResult(RESULT_OK, i);
      
      Worker.onMain(this::onBackPressed, 200);
   }
   
   
}
