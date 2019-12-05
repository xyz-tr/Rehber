package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener;

import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lombok.Getter;


@SuppressWarnings("WeakerAccess")
public class DialogListener{
   
   @Getter @NonNull private Listener dialogListener;
   @Getter @Nullable private DialogAction dialogAction;
   
   public DialogListener(@Nullable DialogAction action){
      
      dialogListener = new Listener(action);
      this.dialogAction = action;
   }
   
   public void setDialogAction(@Nullable DialogAction action){
      
      dialogListener.action = action;
   }
   
   private static class Listener implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener{
      
      @Nullable DialogAction action;
      
      Listener(@Nullable DialogAction action){
         
         this.action = action;
      }
      
      @Override
      public void onCancel(DialogInterface dialog){
         
         if(action != null) action.onDialogClose(dialog);
      }
      
      @Override
      public void onDismiss(DialogInterface dialog){
         
         if(action != null) action.onDialogClose(dialog);
      }
      
      @Override
      public void onShow(DialogInterface dialog){
         
         if(action != null) action.onDialogShow(dialog);
      }
   }
   
}
