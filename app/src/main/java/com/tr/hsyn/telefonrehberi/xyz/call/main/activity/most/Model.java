package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most;

import com.tr.hsyn.telefonrehberi.xyz.call.Group;


/**
 * @author hsyn
 */
public class Model{
   
   private final String        name;
   private final long          count;
   private final Group<String> pair;
   

   public Model(String name, long count, Group<String> pair){
      
      this.name  = name;
      this.count = count;
      this.pair  = pair;
   }

   public final String getName(){
      
      return name;
   }
   
   public final long getCount(){
      
      return count;
   }
   
   public final Group<String> getPair(){
      
      return pair;
   }
}
