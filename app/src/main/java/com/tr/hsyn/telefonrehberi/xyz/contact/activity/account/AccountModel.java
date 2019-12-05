package com.tr.hsyn.telefonrehberi.xyz.contact.activity.account;

import android.accounts.Account;


public final class AccountModel extends Account{
   
   public final int    count;
   
   public AccountModel(String name, String type, int count){
      
      super(name, type);
      this.count = count;
   }
   
}
