package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.Settings;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_AccountBox;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;


/**
 * <h1>ContactsFragmentAccount</h1>
 * Bu fragment hesap işlemlerini halledecek
 *
 * @author hsyn 2019-11-03 09:35:25
 */
public abstract class ContactsAccount extends LoadContacts{
   
   
   /**
    * Seçilen hesabı tutacak.
    * Hesap seçimi yapıldığında uygulama açık kaldığı sürece bu hesap bu değişkende tutulur.
    * Uygulama kapanıp yeniden açıldığında tekrar hesap seçimi yapılana kadar seçilmiş bir hesap yoktur.
    * Bu metod bu şekilde geçici olarak tuttuğu seçilmiş hesabı döner.
    * Hesap seçimi ya da değişimi yapılmamışsa {@code null}.
    */
   @Getter(AccessLevel.PROTECTED) private Account selectedAccount;
   
   @Override
   public void onAttach(@NonNull Context context){
      
      super.onAttach(context);
   
      selectedAccount = Settings.getSelectedAccount();
      Worker.onBackground(this::setGlobalProperAccount, "ContactsFragmentAccount:Uygun global bir hesap arama");
   }
   
   private void setGlobalProperAccount(){
   
      Account account = Contacts.getGlobalProperAccount(getContext());
      Kickback_AccountBox.getInstance().setProperAccount(account);
   }
   
   @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      if(data == null){
         
         Logger.d("data yok");
         return;
      }
      
      switch(requestCode){
         
         case REQUEST_CODE_SYSTEM_ACCOUNT_SELECTION://Android sistemine ait olan
            
            if(resultCode == Activity.RESULT_OK){
               
               onAccountSelected(new Account(
                     data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME),
                     data.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)));
            }
            
            break;
         
         //Bu da bizim yaptığımız
         case REQUEST_CODE_LOCAL_ACCOUNT_SELECTION:
            
            if(resultCode == Activity.RESULT_OK){
               
               if(data.hasExtra(EXTRA_SELECTED_ACCOUNT)){
                  
                  val temp = data.getParcelableExtra(EXTRA_SELECTED_ACCOUNT);
                  
                  if(temp == null){
                     
                     Logger.w("Seçilen hesap null");
                     return;
                  }
                  
                  Account _selectedAccount = Contacts.convertAccount((android.accounts.Account) temp);
                  
                  if(_selectedAccount.name.equals(getString(R.string.all_contacts))){
                     
                     if(getSelectedAccount() == null){ return; }
                     onAccountSelected(null);
                     return;
                  }
                  
                  
                  Logger.w("Account selected : %s", _selectedAccount);
                  onAccountSelected(_selectedAccount);
               }
            }
            
            break;
      }
   }
   
   private void onAccountSelected(final Account account){
      
      if(account == null){
         
         setSelectedAccount(null);
         return;
      }
      
      if(getContext() == null) return;
      
      final Account[] accounts = Contacts.getAccounts(getContext());
      
      if(account.equals(getSelectedAccount())){ return; }
      
      for(Account _account : accounts){
         
         if(_account.name.equals(account.name) && _account.type.equals(account.type)){
            
            setSelectedAccount(account);
            return;
         }
      }
   }
   
   private void setSelectedAccount(final Account selectedAccount){
      
      Kickback_AccountBox.getInstance().setSelectedAccount(this.selectedAccount = selectedAccount);
      
      if(getContext() == null){ return; }
      
      if(Settings.isRememberSelectedAccount(getContext())){
         
         Settings.rememberAccount(selectedAccount != null);
         Logger.d("Seçilen hesap güncellendi : %s", selectedAccount);
      }
      
      loadContacts();
   }
   
   
}
