package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup.BackupItem;

import java.util.List;


public class BackupFileDeleted{
   
   private BackupItem  backupItem;
   private int         index;
   private List<ICall> phoneCalls;
   
   public BackupFileDeleted(BackupItem backupItem, int index, List<ICall> phoneCalls){
      
      this.backupItem = backupItem;
      this.index      = index;
      this.phoneCalls = phoneCalls;
   }
   
   public BackupItem getBackupItem(){
      
      return backupItem;
   }
   
   public int getIndex(){
      
      return index;
   }
   
   public List<ICall> getPhoneCalls(){
      
      return phoneCalls;
   }
}
