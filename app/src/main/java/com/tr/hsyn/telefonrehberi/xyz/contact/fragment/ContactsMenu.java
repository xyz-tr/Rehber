package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ObjectStarter;


public abstract class ContactsMenu extends ContactsTitle implements Const{
   
   /**
    * Arama butonu
    */
   private MenuItem searchMenuItem;
   
   @Override
   public void onResume(){
      
      super.onResume();
      
      if(searchMenuItem != null){ searchMenuItem.setEnabled(true); }
   }
   
   @Override
   public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
      
      //menu.clear();
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.contacts_fragment_menu, menu);
      
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      
      final int id = item.getItemId();
      
      switch(id){
         
         case R.id.menu_search:
            
            item.setEnabled(false);
            searchMenuItem = item;
            onMenuSearch();
            return true;
         
         
         case R.id.menu_accounts:
            
            onMenuAccounts();
            return true;
         
         
         case R.id.menu_refresh_contacts:
            
            onContactsRefresh();
            return true;
         
         
         case R.id.menu_groups:
            
            onMenuGroups();
            return true;
         
         
         case R.id.menu_settings:
            
            onMenuSettings();
            return true;
         
         
         case R.id.contacts_menu_merge:
            
            onMenuMergeContacts();
            return true;
   
         case R.id.menu_stored_contacts:
   
            //noinspection ConstantConditions
            ObjectStarter.startStoredContactsActivity(getContext());
            
            return true;
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   protected abstract void onMenuMergeContacts();
   
   /**
    * Rehber menüsünden 'Ayarlar' seçildiğinde
    */
   protected abstract void onMenuSettings();
   
   /**
    * Menüden 'Gruplar' seçildiğinde
    */
   protected abstract void onMenuGroups();
   
   /**
    * Menüden 'Hesaplar' seçildiğinde
    */
   protected abstract void onMenuAccounts();
   
   /**
    * Kişiler için yenileme isteği
    */
   protected abstract void onContactsRefresh();
   
   /**
    * Kişiler için arama isteği
    */
   protected abstract void onMenuSearch();
   
}
