package com.tr.hsyn.telefonrehberi.util;

import java.util.Date;
import java.util.Random;


public class MRandom extends Random{
   
   
   public MRandom(){
      
      super.setSeed(new Date().getTime());
   }
   
   public MRandom(long seed){
      
      super.setSeed(seed);
   }
   
   public int nextInt(int start, int end){
      
      return nextInt(end - start) + start;
   }
   
   public long nextLong(long end){
      
      return Math.abs(nextLong()) % end;
   }
   
   public long nextLong(long start, long end){
      
      return nextLong(end - start) + start;
   }
   
   
}
