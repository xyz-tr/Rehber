package com.tr.hsyn.telefonrehberi.util.log;

import com.tr.hsyn.telefonrehberi.util.text.Stringx;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;


public class LogDebugTree extends Timber.DebugTree{
   
   @Override
   protected @Nullable String createStackElementTag(@NotNull StackTraceElement element){
      
      String className  = element.getClassName();
      String fileName   = element.getFileName();
      String methodName = element.getMethodName();
      int    lineNumber = element.getLineNumber();
      
      
      className = className.substring(className.lastIndexOf('.') + 1);
      //fileName  = fileName.substring(fileName.indexOf('.'));
      
      return Stringx.format("Rehber: %s.%s(%s:%d)",className, methodName,  fileName, lineNumber);
      //return Stringx.format("Rehber:%s:%s:%d", className, methodName, lineNumber);
      //return super.createStackElementTag(element);
   }
}
