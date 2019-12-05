package com.tr.hsyn.telefonrehberi.util;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;


public class Toolbarx{
   
   /**
    * Toolbar'ı activity'ye set et.
    * Status bar ve tollbar rengini primary color değerine göre ayarla.
    * 
    * @param activity Activity
    * @param toolbar Toolbar
    */
   public static void setToolbar(AppCompatActivity activity, Toolbar toolbar){
      
      activity.setSupportActionBar(toolbar);
   
      int primaryColor = u.getPrimaryColor(activity);
      toolbar.setBackgroundColor(primaryColor);
   
      new ReColor(activity).setStatusBarColor(u.colorToString(ColorController.getLastColor()), u.colorToString(u.darken(primaryColor, .9F)), 400);
   }
}
