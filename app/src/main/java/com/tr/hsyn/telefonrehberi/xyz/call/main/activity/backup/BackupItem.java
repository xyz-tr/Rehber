package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup;


import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BackupItem{
   
   private File    file;
   private boolean error;
   
   public String getName(){
      
      return convertBackupName(file.getName());
   }
   
   public static String convertBackupName(String name){
      
      try{
         
         return String.format(Locale.getDefault(), "%te/%<td/%<tY %<tH:%<tM:%<tS", new Date(Long.parseLong(name)));
      }
      catch(Exception e){
   
         Logger.w("Verdiğin değere bak : %s", name);
      }
      
      return name;
   }
}
