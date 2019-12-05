package com.tr.hsyn.telefonrehberi.util.db;


import com.orhanobut.logger.Logger;


public interface DB{
   
   
   default String getCreateTableQuery(){
   
      StringBuilder create = new StringBuilder(String.format("CREATE TABLE %s (", getTableName()));
   
      DBItem[] dbItems = getDBItems();
   
      for(int i = 0; i < dbItems.length; i++){
      
         DBItem dbItem = dbItems[i];
         
         create.append(String.format("%s", dbItem.value));
      
         if(i != dbItems.length - 1) create.append(",");
      }
   
      create.append(")");
   
      Logger.d(create.toString());
      return create.toString();
   }
   
   String getTableName();
   
   String getDatabaseName();
   
   String[] getColumns();
   
   DBItem[] getDBItems();
   
   String getPrimaryKey();
   
   default int getVersion(){
      
      return 1;
   }
   
}
