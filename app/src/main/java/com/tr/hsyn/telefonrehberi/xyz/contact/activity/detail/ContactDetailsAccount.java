package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_AccountBox;

import java.util.ArrayList;
import java.util.Collection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.var;


public abstract class ContactDetailsAccount extends ContactDetailsImage{
   
   @Getter(AccessLevel.PROTECTED) private Account properAccount;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      properAccount = Contacts.getProperAccount(this, contact.getGoogleAccounts());
      setupAccounts();
   }
   
   /**
    * Kişinin bağlı olduğu tüm hesapları göster.
    */
   private void setupAccounts(){
      
      //setOnlyGoogleAccounts();
      Account selectedAccount = Kickback_AccountBox.getInstance().getSelectedAccount();
      var     accounts        = contact.getAccounts();
      
      if(selectedAccount == null){
         
         if(accounts == null){
            
            return;
         }
         
         accounts = Stream.of(accounts).distinctBy(cx -> cx.name).toList();
      }
      
      
      final TextView                  hr     = new TextView(this);//Çizgi
      final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      hr.setBackgroundColor(u.lighter(u.getPrimaryColor(this), 0.5F));
      hr.setAlpha(.8f);
      params.height = 1;
      
      params.setMargins(
            u.dpToPx(this, 74),
            0,
            0,
            u.dpToPx(this, 9)
      );
      
      
      hr.setLayoutParams(params);
      
      final ViewGroup accountsLayout = findViewById(R.id.accounts);
      accountsLayout.addView(hr);
      
      Collection<Account> temp = new ArrayList<>();
      
      if(selectedAccount != null) addAccountView(accountsLayout, selectedAccount);
      else{
         
         if(accounts == null) return;
         
         for(Account account : accounts){
            
            if(temp.contains(account)) continue;
            
            temp.add(account);
            
            addAccountView(accountsLayout, account);
         }
      }
   }
   
   private void addAccountView(ViewGroup accountsLayout, Account account){
      
      View      accountView;
      ImageView accountImage;
      TextView  accountName;
      
      accountView = getLayoutInflater().inflate(R.layout.account_item_contact_detail, accountsLayout, false);
      
      accountImage = accountView.findViewById(R.id.icon);
      accountName  = accountView.findViewById(R.id.accountName);
      accountView.setTag(account);
      
      
      Drawable icon = Phone.getIconForAccount(this, account);
      
      if(icon == null){
         
         icon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.sim);
         assert icon != null;
         u.setTintDrawable(icon, u.lighter(u.getPrimaryColor(this), 0.2F));
      }
      
      accountImage.setImageDrawable(icon);
      accountName.setText(account.name);
      
      accountView.setBackgroundResource(wellRipple);
      accountsLayout.addView(accountView);
      accountView.setOnClickListener(this::onAccountClick);
   }
   
   /**
    * Kişiye ait hesaplardan birine tıklandığında.
    *
    * @param view Tıklanan nesne. Bu nesne tıklanan hesabın bilgisini taşıyor.<br><br>
    *             {@code Account clickedAccount = view.getTag();//tıklanan hesap}
    */
   protected abstract void onAccountClick(View view);
}
