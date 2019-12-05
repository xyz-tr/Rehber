package com.tr.hsyn.telefonrehberi.xyz.main.analize;

import com.tr.hsyn.telefonrehberi.xyz.contact.SimpleContact;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContactsDiffInfo{
   
   private List<SimpleContact> newContacts;
   private List<SimpleContact> removedContacts;
   private String              report;
   
}
