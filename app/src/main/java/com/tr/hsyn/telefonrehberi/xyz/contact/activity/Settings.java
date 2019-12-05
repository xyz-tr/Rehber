package com.tr.hsyn.telefonrehberi.xyz.contact.activity;

import android.content.Context;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Toolbarx;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_AccountBox;

import io.paperdb.Paper;
import lombok.val;
import timber.log.Timber;


public class Settings extends AppCompatActivity{
   
   private              Switch rememberSelectedAccount;
   public static final  String KEY_REMEMBER_SELECTED_ACCOUNT = "rememberSelectedAccount";
   private static final String BOOK                          = "selected_account_book";
   private static final String KEY_SELETED_ACCOUNT           = "selected_account";
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_settings);
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      Toolbarx.setToolbar(this, toolbar);
      
      rememberSelectedAccount = findViewById(R.id.rememberSelectedAccount);
      rememberSelectedAccount.setOnCheckedChangeListener(this::onCheckedChangedSelectedAccountRemember);
      
      getSettings();
   }
   
   private void getSettings(){
      
      boolean c = Paper.book(BOOK).read(KEY_REMEMBER_SELECTED_ACCOUNT, false);
      
      Timber.d("key : %s", c);
      
      rememberSelectedAccount.setChecked(c);
   }
   
   private void onCheckedChangedSelectedAccountRemember(CompoundButton buttonView, boolean isChecked){
      
      Paper.book(BOOK).write(Settings.KEY_REMEMBER_SELECTED_ACCOUNT, isChecked);
      rememberAccount(isChecked);
   }
   
   public static void rememberAccount(boolean remember){
      
      val account = Kickback_AccountBox.getInstance().getSelectedAccount();
      
      if(remember){
         
         if(account != null){
            
            Paper.book(BOOK).write(KEY_SELETED_ACCOUNT, account);
            Timber.d("Hesap hatırlanacak : %s", account);
         }
         else{
            
            Timber.d("Hatırlanması istenen hesap null");
         }
      }
      else{
         
         Paper.book(BOOK).delete(KEY_SELETED_ACCOUNT);
      }
   }
   
   public static boolean isRememberSelectedAccount(Context context){
      
      return Paper.book(BOOK).read(Settings.KEY_REMEMBER_SELECTED_ACCOUNT, false);
   }
   
   public static Account getSelectedAccount(){
      
      return Paper.book(BOOK).read(KEY_SELETED_ACCOUNT, null);
   }
}
