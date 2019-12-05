package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.SnackBarListener;


public abstract class ContactDetailsAction extends ContactDetailsSocial implements SnackBarListener{
   
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
   
      Worker.onMain(this::showActionButton, 300);
   }
   
   @Override
   public void onSnackBarStarted(Object object){
      
      moveActionButton();
   }
   
   protected void moveActionButton(){
      
      ViewCompat.animate(actionButton).rotation(360F).translationY(-120F).setDuration(600).start();
   }
   
   @Override
   public void onSnackBarFinished(Object object, boolean actionPressed){
      
      backActionButton();
   }
   
   protected void backActionButton(){
      
      ViewCompat.animate(actionButton).rotation(0F).translationY(0F).setDuration(600).start();
   }
   
   /**
    * Sağ alt köşede ekşın butonu göster.
    * Bu metod çağrılmazsa buton ekrana girmeyecek.
    */
   @SuppressLint("RestrictedApi")
   protected final void showActionButton(){
      
      actionButton.setSupportBackgroundTintList(ColorStateList.valueOf(u.getPrimaryColor(this)));
      actionButton.setTranslationX(300);
      actionButton.setRotation(360);
      actionButton.setAlpha(0F);
      actionButton.setVisibility(View.VISIBLE);
      actionButton.animate().alpha(1).rotation(0).translationX(0).setDuration(900).start();
      actionButton.setOnClickListener((v) -> onEditContact());
   }
   
}
