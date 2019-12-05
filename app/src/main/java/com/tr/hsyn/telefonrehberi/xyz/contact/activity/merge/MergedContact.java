package com.tr.hsyn.telefonrehberi.xyz.contact.activity.merge;

public class MergedContact{
   
   public final String rawId_1;
   public final String rawId_2;
   public final int    type;
   
   public MergedContact(String rawId_1, String rawId_2, int type){
      
      this.rawId_1 = rawId_1;
      this.rawId_2 = rawId_2;
      this.type    = type;
   }
}
