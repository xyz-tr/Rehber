package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;


import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.ContactSearchActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.Settings;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.account.AccountsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.GroupActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge.ContactsMergeActivity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;


/**
 * <h1>ContactsFragmentBase</h1>
 * <p>
 * Rehber için ön hazırlıkları içerir.
 * Genel olarak görsel elemanları ayarlar.
 */
public abstract class ContactsBase extends ContactsMenu{
   
   @Getter(AccessLevel.PROTECTED) @Setter(AccessLevel.PROTECTED) private int lastlistPosition = 0;
   
   @Override
   protected void onMenuMergeContacts(){
      
      startActivity(new Intent(getActivity(), ContactsMergeActivity.class));
   }
   
   @Override
   protected void onMenuSettings(){
      
      startActivity(new Intent(getContext(), Settings.class));
   }
   
   @Override
   protected void onMenuGroups(){
      
      startActivity(new Intent(getContext(), GroupActivity.class));
      Bungee.slideDown(getContext());
   }
   
   @Override
   protected void onMenuAccounts(){
      
      Intent i = new Intent(getContext(), AccountsActivity.class);
      startActivityForResult(i, REQUEST_CODE_LOCAL_ACCOUNT_SELECTION);
      Bungee.slideRight(getContext());
   }
   
   @Override
   protected void onContactsRefresh(){
      
      saveLastListPosition();
      loadContacts();
   }
   
   protected abstract void loadContacts();
   
   @Override
   protected void onMenuSearch(){
      
      Intent i = new Intent(getContext(), ContactSearchActivity.class);
      
      startActivity(i);
      Bungee.fade(getContext());
      
   }
   
   @Override
   public void onPause(){
      
      super.onPause();
      
      saveLastListPosition();
   }
   
   private void saveLastListPosition(){
      
      if(recyclerView != null){
         
         val manager = (LinearLayoutManager) recyclerView.getLayoutManager();
         
         if(manager != null){
            
            lastlistPosition = manager.findFirstVisibleItemPosition();
         }
      }
   }
   
}
