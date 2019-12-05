package com.tr.hsyn.telefonrehberi.util;

import android.util.Log;

import com.tr.hsyn.telefonrehberi.util.text.Stringx;

import java.util.Hashtable;


/**
 * The class for print LogCall
 *
 * @author kesenhoo
 */
public class Logger{
   
   private final static boolean logFlag = true;
   
   
   private final static String                    TAG          = "[Rehber]";
   private final static int                       logLevel     = Log.VERBOSE;
   private static       Hashtable<String, Logger> sLoggerTable = new Hashtable<>();
   private              String                    mClassName;
   private static       Logger                    jlog;
   private static       Logger                    klog;
   private static final String                    HSYN         = "";
   private static final String                    KESEN        = "@kesen@ ";
   
   private Logger(String name){
      
      mClassName = name;
   }
   
   /**
    * @param className class name
    * @return Logger
    */
   @SuppressWarnings("unused")
   private static Logger getLogger(String className){
      
      Logger classLogger = sLoggerTable.get(className);
      if(classLogger == null){
         classLogger = new Logger(className);
         sLoggerTable.put(className, classLogger);
      }
      return classLogger;
   }
   
   /**
    * Purpose:Mark user one
    *
    * @return logger
    */
   public static Logger kLog(){
      
      if(klog == null){
         klog = new Logger(KESEN);
      }
      return klog;
   }
   
   /**
    * Purpose:Mark user two
    *
    * @return logger
    */
   public static Logger jLog(){
      
      if(jlog == null){
         jlog = new Logger(HSYN);
      }
      return jlog;
   }
   
   /**
    * Get The Current Function Name
    *
    * @return
    */
   private String getFunctionName(){
      
      StackTraceElement[] sts = Thread.currentThread().getStackTrace();
      
      if(sts == null){
         return null;
      }
      
      for(StackTraceElement st : sts){
         
         if(st.isNativeMethod()){
            continue;
         }
         
         if(st.getClassName().equals(Thread.class.getName())){
            continue;
         }
         
         if(st.getClassName().equals(this.getClass().getName())){
            continue;
         }
         
         return mClassName + "[ " + Thread.currentThread().getName() + ": " + st.getFileName() + ":" + st.getLineNumber() + " " + st.getMethodName() + " ]";
      }
      return null;
   }
   
   /**
    * The LogCall Level:i
    *
    * @param str
    */
   public void i(Object str, Object... args){
      
      if(logFlag){
         
         if(logLevel <= Log.INFO){
            
            String name = getFunctionName();
            
            try{
               
               if(name != null){
                  Log.i(TAG, name + " - " + String.format(str.toString(), args));
               }
               else{
                  Log.i(TAG, str.toString());
               }
               
            }
            catch(Exception e){
               
               this.e(e.toString());
               Log.i(TAG, str.toString());
            }
         }
      }
      
   }
   
   /**
    * The LogCall Level:d
    *
    * @param str
    */
   public void d(Object str, Object... args){
      
      if(logFlag){
         if(logLevel <= Log.DEBUG){
            String name = getFunctionName();
            
            try{
               
               if(name != null){
                  Log.d(TAG, name + " - " + String.format(str.toString(), args));
               }
               else{
                  Log.d(TAG, str.toString());
               }
               
            }
            catch(Exception e){
               
               this.e(e.toString());
               Log.d(TAG, str.toString());
            }
         }
      }
   }
   
   private String ifTrue, ifFalse;
   private boolean condition;
   
   public void ifTrue(boolean condition, Object str, Object... args){
   
      if(condition){
   
         if(logFlag){
            
            if(logLevel <= Log.DEBUG){
               
               String name = getFunctionName();
               str = Stringx.format(str.toString(), args);
         
               try{
            
                  if(name != null){
                     Log.d(TAG, name + " - " + str);
                  }
                  else{
                     Log.d(TAG, "" + str);
                  }
            
               }
               catch(Exception e){
            
                  this.e(e.toString());
                  Log.d(TAG, str.toString());
               }
            }
         }
      }
      
   }
   
   public Logger setTrue(Object str, Object... args){
      
      ifTrue = Stringx.format(str.toString(), args);
      return this;
   }
   
   public Logger setFalse(Object str, Object... args){
      
      ifFalse = Stringx.format(str.toString(), args);
      return this;
   }
   
   public Logger withCondition(boolean condition){
      
      this.condition = condition;
      return this;
   }
   
   public void toLog(){
   
      if(logFlag){
         
         if(logLevel <= Log.DEBUG){
            
            String name = getFunctionName();
            Object str  = getObject(condition, ifTrue, ifFalse);
         
            try{
            
               if(name != null){
                  
                  Log.d(TAG, name + " - " + str);
               }
               else{
                  
                  Log.d(TAG, "" + str);
               }
            
            }
            catch(Exception e){
            
               this.e(e.toString());
               Log.d(TAG, str.toString());
            }
         }
      }
   }
   
   
   public void _if(boolean condition, Object ifTrue, Object ifFalse){
      
      if(logFlag){
         if(logLevel <= Log.DEBUG){
            String name = getFunctionName();
            Object str = getObject(condition, ifTrue, ifFalse);
            
            try{
               
               if(name != null){
                  Log.d(TAG, name + " - " + str);
               }
               else{
                  Log.d(TAG,  "" + str);
               }
               
            }
            catch(Exception e){
               
               this.e(e.toString());
               Log.d(TAG, str.toString());
            }
         }
      }
   }
   
   public void _if(Object object, Object ifTrue, Object ifFalse){
      
      _if(object != null, ifTrue, ifFalse);
   }
   
   private Object getObject(boolean condition, Object ifTrue, Object ifFalse){
      
      return condition ? ifTrue : ifFalse;
   }
   
   /**
    * The LogCall Level:V
    *
    * @param str
    */
   public void v(Object str, Object... args){
      
      if(logFlag){
         if(logLevel <= Log.VERBOSE){
            String name = getFunctionName();
            
            try{
               
               if(name != null){
                  Log.v(TAG, name + " - " + String.format(str.toString(), args));
               }
               else{
                  Log.v(TAG, str.toString());
               }
            }
            catch(Exception e){
               
               this.e(e.toString());
            }
            
         }
      }
   }
   
   /**
    * The LogCall Level:w
    *
    * @param str
    */
   public void w(Object str, Object... args){
      
      if(logFlag){
         
         if(logLevel <= Log.WARN){
            
            String name = getFunctionName();
            
            try{
               
               if(name != null){
                  Log.w(TAG, name + " - " + String.format(str.toString(), args));
               }
               else{
                  Log.w(TAG, str.toString());
               }
               
            }
            catch(Exception e){
               
               this.e(e.toString());
               Log.w(TAG, str.toString());
            }
         }
      }
   }
   
   /**
    * The LogCall Level:e
    *
    * @param str str
    */
   public void e(Object str, Object... args){
      
      if(logFlag){
         if(logLevel <= Log.ERROR){
            
            String name = getFunctionName();
            
            try{
               
               if(name != null){
                  Log.e(TAG, name + " - " + String.format(str.toString(), args));
               }
               else{
                  Log.e(TAG, str.toString());
               }
               
            }
            catch(Exception e){
               
               this.e(e.toString());
               Log.e(TAG, str.toString());
            }
            
         }
      }
   }
   
   /**
    * The LogCall Level:e
    *
    * @param ex
    */
   public void e(Exception ex){
      
      if(logFlag){
         if(logLevel <= Log.ERROR){
            Log.e(TAG, "error", ex);
         }
      }
   }
   
   /**
    * The LogCall Level:e
    *
    * @param log
    * @param tr
    */
   public void e(String log, Throwable tr){
      
      if(logFlag){
         String line = getFunctionName();
         Log.e(TAG, "{Thread:" + Thread.currentThread().getName() + "}" + "[" + mClassName + line + ":] " + log + "\n", tr);
      }
   }
}