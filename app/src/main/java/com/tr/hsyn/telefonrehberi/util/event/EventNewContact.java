package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class EventNewContact{
   
   @Getter private final IMainContact contact;
}
