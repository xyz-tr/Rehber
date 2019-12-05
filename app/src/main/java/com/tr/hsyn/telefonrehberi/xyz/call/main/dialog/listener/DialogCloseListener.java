package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener;

import android.content.DialogInterface;

import androidx.annotation.Nullable;

import lombok.Setter;


/**
 * <h1>DialogCloseListener</h1>
 * <p>
 * Dialog penceresi kapandığında yapılacak action için yardımcı sınıf.
 */
@Deprecated
public class DialogCloseListener implements DialogInterface.OnCancelListener, DialogInterface.OnDismissListener, DialogInterface.OnShowListener{
   
   @Setter @Nullable private DialogAction action;
   
   public DialogCloseListener(){}
   
   public DialogCloseListener(@Nullable DialogAction dialogAction){
      
      action = dialogAction;
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
   
      if(action != null){
         action.onDialogShow(dialog);
      }
   }
}
