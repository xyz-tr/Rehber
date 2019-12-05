package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.view.Menu;
import android.view.MenuItem;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;


public abstract class ContactDetailsMenu extends ContactDetailsHistory{
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.contact_details_menu, menu);
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      
      switch(item.getItemId()){
         
         case R.id.edit_contact_menu_item:
            
            onEditContact();
            return true;
         
         case R.id.delete_contact_menu_item:
            
            deleteContact();
            return true;
         
         
         default:
            return super.onOptionsItemSelected(item);
      }
   }
   
   /**
    * Kişiyi sistem rehber kayıtlarından siler.
    */
   private void deleteContact(){
      
      onContactDeleted(Contacts.delete(this, contact.getContactId()));
   }
   
   /**
    * Kişi sistem kayıtlarından silindiğinde.
    *
    * @param success Eğer silme işlemi başarılı ise {@code true}
    */
   protected abstract void onContactDeleted(boolean success); 
   
}
