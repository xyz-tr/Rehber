package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ContactDeleted{
   
   @Getter private final IPhoneContact contact;
}
