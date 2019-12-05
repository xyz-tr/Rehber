package com.tr.hsyn.telefonrehberi.xyz.contact.cloud;


import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;

import java.util.List;


@KickbackBox
public class ContactBox{
   
   @KickbackElement(name = "contacts")
   List<IMainContact> contacts;
}
