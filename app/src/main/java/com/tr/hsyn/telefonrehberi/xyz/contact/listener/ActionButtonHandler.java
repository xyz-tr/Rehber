package com.tr.hsyn.telefonrehberi.xyz.contact.listener;

import android.view.View;

import androidx.core.view.ViewCompat;

import com.tr.hsyn.telefonrehberi.util.ui.snack.SnackBarListener;


public class ActionButtonHandler implements SnackBarListener{
   
   private View actionButton;
   
   public void hide(){
   
      ViewCompat.animate(actionButton).rotation(360f).translationY(-120f).setDuration(600).start();
   }
   
   public void show(){
   
      ViewCompat.animate(actionButton).rotation(0f).translationY(0f).alpha(1f).setDuration(600).start();
   }
   
   @Override
   public void onSnackBarStarted(Object object){
      
      hide();
   }
   
   @Override
   public void onSnackBarFinished(Object object, boolean actionPressed){
      
      show();
   }
   
}
