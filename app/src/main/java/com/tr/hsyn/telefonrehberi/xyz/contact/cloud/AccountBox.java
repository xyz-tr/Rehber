package com.tr.hsyn.telefonrehberi.xyz.contact.cloud;

import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;


@KickbackBox
public class AccountBox{
   
   @KickbackElement(name = "selectedAccount")
   Account selectedAccount;
   
   @KickbackElement(name = "properAccount")
   Account properAccount;
   
}
