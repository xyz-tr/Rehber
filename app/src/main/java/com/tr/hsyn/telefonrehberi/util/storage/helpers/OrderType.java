package com.tr.hsyn.telefonrehberi.util.storage.helpers;

import com.annimon.stream.ComparatorCompat;

import java.io.File;


public enum OrderType{
   
   NAME,
   /**
    * Last modified is the first
    */
   DATE,
   /**
    * Smaller size will be in the first place
    */
   SIZE;
   
   public ComparatorCompat<File> getComparator(){
	
      switch(ordinal()){
      	
         case 0: // name
            return ComparatorCompat.comparing(File::getName);
         case 1: // date
            return ComparatorCompat.comparing(File::lastModified);
         case 2: // size
            return ComparatorCompat.comparing(File::length);
         default:
            break;
      }
      return null;
   }
}
