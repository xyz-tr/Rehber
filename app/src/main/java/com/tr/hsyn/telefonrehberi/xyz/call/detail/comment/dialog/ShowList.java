package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Dialog;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.adapter.CallListDialogAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogAction;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;

import java.lang.ref.WeakReference;
import java.util.List;

import lombok.Builder;
import lombok.val;


public class ShowList{
   
   private boolean      isOpen;
   private CharSequence title;
   private List<ICall>  calls;
   private ICall         call;
   private int          animationResource;
   private DialogAction dialogAction;
   private boolean      isCancelable;
   
   @Builder
   public ShowList(CharSequence title, List<ICall> calls, ICall call, int animationResource, DialogAction dialogAction, boolean isCancelable){
      
      this.title             = title;
      this.calls             = calls;
      this.call              = call;
      this.animationResource = animationResource;
      this.dialogAction      = dialogAction;
      this.isCancelable      = isCancelable;
   }
   
   public AlertDialog showOn(WeakReference<Activity> activityWeakReference){
      
      if(isOpen) return null;
      
      isOpen = true;
      
      
      val _dialogListener = new DialogListener(new DialogAction(){
         
         @Override
         public void onDialogClose(DialogInterface dialogInterface){
            
            isOpen = false;
            
            if(dialogAction != null){
               
               dialogAction.onDialogClose(dialogInterface);
            }
         }
         
         @Override
         public void onDialogShow(DialogInterface dialogInterface){
            
            if(dialogAction != null)
               dialogAction.onDialogShow(dialogInterface);
         }
      });
      
      AlertDialog.Builder dialog = new AlertDialog.Builder(activityWeakReference.get());
      dialog.setCancelable(isCancelable);
      
      View rootDialogView = Dialog.inflate(activityWeakReference.get(), R.layout.dialog_call_list);
      
      dialog.setView(rootDialogView);
      
      TextView titleText = rootDialogView.findViewById(R.id.titleText);
      
      RecyclerView recyclerView = rootDialogView.findViewById(R.id.recyclerView);
      titleText.setText(title);
      
      recyclerView.setAdapter(new CallListDialogAdapter(calls, call));
      
      View header = rootDialogView.findViewById(R.id.header);
      header.setBackgroundColor(u.getPrimaryColor(activityWeakReference.get()));
      
      val dia = dialog.create();
      //noinspection ConstantConditions
      dia.getWindow().getAttributes().windowAnimations = animationResource;
      
      dia.setOnDismissListener(_dialogListener.getDialogListener());
      dia.setOnCancelListener(_dialogListener.getDialogListener());
      dia.setOnShowListener(_dialogListener.getDialogListener());
      
      dia.show();
      return dia;
   }
   
   public static class ShowListBuilder{
   
      private int     animationResource = R.style.DialogAnimationBounce;
      private boolean isCancelable      = true;
   }
   
}
