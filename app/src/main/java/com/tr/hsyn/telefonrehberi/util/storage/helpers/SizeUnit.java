package com.tr.hsyn.telefonrehberi.util.storage.helpers;

import java.text.DecimalFormat;


public enum SizeUnit{
    
   B(1),
   KB(SizeUnit.BYTES),
   MB(SizeUnit.BYTES * SizeUnit.BYTES),
   GB(SizeUnit.BYTES * SizeUnit.BYTES * SizeUnit.BYTES);
   //TB(SizeUnit.BYTES * SizeUnit.BYTES * SizeUnit.BYTES * SizeUnit.BYTES);
   
   private static final int  BYTES = 1024;
   private              long inBytes;
   
   private SizeUnit(long bytes){
      
      this.inBytes = bytes;
   }
   
   public static String readableSizeUnit(long bytes){
      
      DecimalFormat df = new DecimalFormat("0.00");
      if(bytes < KB.inBytes()){
         return df.format(bytes / (float) B.inBytes()) + " B";
      }
      else if(bytes < MB.inBytes()){
         return df.format(bytes / (float) KB.inBytes()) + " KB";
      }
      else if(bytes < GB.inBytes()){
         return df.format(bytes / (float) MB.inBytes()) + " MB";
      }
      else{
         return df.format(bytes / (float) GB.inBytes()) + " GB";
      }
   }
   
   public long inBytes(){
      
      return inBytes;
   }
}
