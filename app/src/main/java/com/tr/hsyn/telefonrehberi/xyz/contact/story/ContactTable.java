package com.tr.hsyn.telefonrehberi.xyz.contact.story;

public class ContactTable{
   
   
   static final String TABLE_NAME     = "contacts_table";
   static final String NAME           = "contact_name";
   static final String NUMBER         = "number";
   static final String SAVED_DATE     = "saved_date";
   static final String DELETED_DATE   = "deleted_date";
   static final String UPDATED_DATE   = "updated_date";
   static final String LAST_LOOK_DATE = "last_look_date";
   static final String LOOK_COUNT     = "look_count";
   static final String DESCRIPTION    = "description";
   
   static final String CREATE_CONTACTS_TABLE = String.format(
         
         "CREATE TABLE %s (%s text PRIMARY KEY ON CONFLICT replace, %s text DEFAULT null, %s integer DEFAULT 0, %s integer DEFAULT 0, %s integer DEFAULT 0, %s integer DEFAULT 0, %s integer DEFAULT 0, %s TEXT DEFAULT null)",
         TABLE_NAME, NUMBER, NAME, SAVED_DATE, DELETED_DATE, UPDATED_DATE, LAST_LOOK_DATE, LOOK_COUNT, DESCRIPTION
   
   );
   
}
