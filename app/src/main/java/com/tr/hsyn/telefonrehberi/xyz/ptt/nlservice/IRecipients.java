package com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import lombok.val;
import lombok.var;


public interface IRecipients{
   
   String SENTBOX    = "sentbox";
   String RECIPIENTS = "recipients";
   
   default boolean add(String recipient){
   
      val book = Paper.book(SENTBOX);
      val allRecipients = getAllRecipients();
      
      if(allRecipients.contains(recipient)) return false;
   
      val recipients = book.read(RECIPIENTS, new HashMap<>());
      var trues = getRecipients(true);
      
      if(trues == null) trues = new ArrayList<>(1);
      
      trues.add(recipient);
      recipients.put(true, trues);
      
      book.write(RECIPIENTS, recipients);
      
      return true;
   }
   
   default boolean add(String recipient, boolean trueOrFalse){
   
      val book = Paper.book(SENTBOX);
      val allRecipients = getAllRecipients();
      
      if(allRecipients.contains(recipient)) return false;
   
      val recipients = book.read(RECIPIENTS, new HashMap<Boolean, List<String>>());
      var truesOrFalses = getRecipients(trueOrFalse);
      
      if(truesOrFalses == null) truesOrFalses = new ArrayList<>(1);
   
      truesOrFalses.add(recipient);
      recipients.put(trueOrFalse, truesOrFalses);
      
      book.write(RECIPIENTS, recipients);
      
      return true;
   }
   
   default boolean remove(String recipient){
   
      val book = Paper.book(SENTBOX);
   
      val recipients = book.read(RECIPIENTS, new HashMap<Boolean, List<String>>());
   
      var trues = recipients.get(true);
   
      if(trues != null && trues.contains(recipient)){
         
         trues.remove(recipient);
         
         recipients.put(true, trues);
         book.write(RECIPIENTS, recipients);
         return true;
      }
   
      var falses = recipients.get(false);
   
      if(falses != null && falses.contains(recipient)){
   
         falses.remove(recipient);
      
         recipients.put(true, falses);
         book.write(RECIPIENTS, recipients);
         return true;
      }
   
      return false;
   }
   
   default List<String> getRecipients(boolean trueOrFalse){
      
      return Paper.book(SENTBOX).read(RECIPIENTS, new HashMap<Boolean, List<String>>()).get(trueOrFalse);
   }
   
   default List<String> getAllRecipients(){
      
      Map<Boolean, List<String>> map = Paper.book(SENTBOX).read(RECIPIENTS, new HashMap<>());
      
      List<String> all = new ArrayList<>();
      
      val trues = map.get(true);
      val falses = map.get(false);
      
      if(trues != null && trues.size() != 0) all.addAll(trues);
      if(falses != null && falses.size() != 0) all.addAll(falses);
      
      return all;
   }
   
   default Map<Boolean, List<String>> getRecipients(){
      
      return Paper.book(SENTBOX).read(RECIPIENTS, new HashMap<>());
   }
   
   default boolean makeFalseOrTrue(String item, boolean falseOrTrue){
      
      if(!contains(item)) return false;
   
      if(remove(item)){
         
         return add(item, falseOrTrue);
      }
      
      return false;
   }
   
   default boolean contains(String item){
      
      return getAllRecipients().contains(item);
   }
   
   static IRecipients create(){
      
      return new Recipients();
   }
   
   
}
