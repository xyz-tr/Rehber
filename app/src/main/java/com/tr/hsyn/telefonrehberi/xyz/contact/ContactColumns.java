package com.tr.hsyn.telefonrehberi.xyz.contact;

import android.provider.ContactsContract;


public interface ContactColumns{
   
   String[] MAIN_CONTACT_COLUMNS = {
      
         ContactsContract.Contacts._ID,
         ContactsContract.Contacts.DISPLAY_NAME_PRIMARY,
         ContactsContract.Contacts.SORT_KEY_PRIMARY
   };
}
