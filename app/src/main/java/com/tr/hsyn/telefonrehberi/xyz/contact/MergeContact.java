package com.tr.hsyn.telefonrehberi.xyz.contact;

import androidx.annotation.NonNull;

import java.util.List;


public class MergeContact{
   
   public final String              id;
   public final String              rawId;
   public final String              name;
   public final List<ContactNumber> numbers;
   
   public MergeContact(String id, String rawId, String name, List<ContactNumber> numbers){
      
      this.id      = id;
      this.rawId   = rawId;
      this.name    = name;
      this.numbers = numbers;
   }
   
   
   @NonNull
   @Override
   public String toString(){
      
      return "MergeContact{" +
             "id='" + id + '\'' +
             ", rawId='" + rawId + '\'' +
             ", name='" + name + '\'' +
             ", numbers=" + numbers +
             '}';
   }
   
   public static boolean equals(List<ContactNumber> c1, List<ContactNumber> c2){
   
      for(int i = 0; i < c1.size(); i++){
      
         ContactNumber n1 = c1.get(i);
   
         for(int j = 0; j < c2.size(); j++){
   
            ContactNumber n2 = c2.get(j);
   
            if(!Contacts.matchNumbers(n1.getNumber(), n2.getNumber())){
               
               return false;
            }
            else{
   
               if(!n1.getAccountName().equals(n2.getAccountName())){
                  
                  return false;
               }
            }
         }
      }
      
      return true;
   }
}
