package com.tr.hsyn.telefonrehberi.util.concurrent;


public class Bool{
   
   public static <T> void ifFalse(T object, Runnable action){
      
      ifTrue(object == null, action);
   }
   
   public static void ifTrue(boolean condition, Runnable action){
      
      ifTrue(condition, action, false);
   }
   
   public static void ifTrue(boolean condition, Runnable action, boolean callInBackgroundThread){
   
      if(action == null) return;
      
      if(!condition) return;
      
      if(callInBackgroundThread){
         
         Worker.onBackground(action);
      }
      else{
         
         Worker.onMain(action);
      }
   }
   
   public static void ifFalseTrue(boolean condition, Runnable trueAction, Runnable falseAction){
      
      ifFalseTrue(condition, trueAction, falseAction, false);
   }
   
   public static void ifFalseTrue(boolean condition, Runnable trueAction, Runnable falseAction, boolean callInBackgroundThread){
      
      if(callInBackgroundThread){
         
         if(condition){
   
            if(trueAction == null) return;
            Worker.onBackground(trueAction);
         }
         else{
            
            if(falseAction == null) return;
            Worker.onBackground(falseAction);
         }
      }
      else{
         
         if(condition){
            
            Worker.onMain(trueAction);
         }
         else{
            
            Worker.onMain(falseAction);
         }
      }
   }
   
   public static <T> void ifTrue(T object, Runnable trueAction){
      
      ifTrue(object != null, trueAction);
   }
   
   public static <T> void ifNot(T object, Runnable action){
      
      ifTrue(object == null, action);
   }
   
   public static <T> void ifNot(boolean condition, Runnable action){
      
      ifFalse(condition, action);
   }
   
   public static void ifFalse(boolean condition, Runnable action){
      
      ifFalse(condition, action, false);
   }
   
   public static void ifFalse(boolean condition, Runnable action, boolean callInBackgroundThread){
      
      if(condition) return;
      
      if(action == null) return;
      
      if(callInBackgroundThread){
         
         Worker.onBackground(action);
      }
      else{
         
         Worker.onMain(action);
      }
   }
   
}
