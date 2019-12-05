package com.tr.hsyn.telefonrehberi.util.ui.snack;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IntDef;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class Show{
   
   public static final int INFO  = 0;
   public static final int WARN  = 1;
   public static final int ERROR = 2;
   
   
   public static void globalMessage(Activity activity, String message){
      
      Message.builder().message(message).build().showOn(activity);
   }
   
   public static void globalMessage(Activity activity, Spanner message){
      
      Message.builder().message(message).build().showOn(activity);
   }
   
   public static void globalMessage(Activity activity, String message, long duration){
      
      Message.builder().message(message).duration(duration).build().showOn(activity);
   }
   
   public static void globalMessage(final Activity activity, Spanner message, long duration, long delay, SnackBarListener snackBarListener, View.OnClickListener clickListener, String actionMessage, boolean cancel, @Type int messageType){
      
      SnackBar.sbi(
            activity, 
            message, 
            duration, 
            cancel, 
            delay, 
            snackBarListener, 
            clickListener,
            actionMessage,
            messageType);
   }
   
   public static void globalMessage(final Activity activity, String message, long duration, long delay, SnackBarListener snackBarListener, View.OnClickListener clickListener, String actionMessage, boolean cancel, @Type int messageType){
      
      SnackBar.sbi(
            activity, 
            message, 
            duration, 
            cancel, 
            delay, 
            snackBarListener, 
            clickListener,
            actionMessage,
            messageType);
   }
   
   @IntDef({INFO, WARN, ERROR})
   @Retention(RetentionPolicy.SOURCE)
   public @interface Type{}
   
}
