package com.tr.hsyn.telefonrehberi.xyz.ptt.mail;

import android.content.Context;

import io.paperdb.Paper;


/**
 * Created by hsyn on 7.06.2017.
 *
 * <p>Gmail is a Acount</p>
 */

public class Account {
    
    private         String  account;
    protected final Context context;
    
    Account(Context context) {
        
        this.context = context;
        this.account = Paper.book().read("from", null);
        
    }
    
    String getAccount() {return account;}
}
