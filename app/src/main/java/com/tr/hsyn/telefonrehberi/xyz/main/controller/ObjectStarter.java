package com.tr.hsyn.telefonrehberi.xyz.main.controller;


import android.content.Context;
import android.content.Intent;

import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.stored.ContactsActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.activity.BackgroundWorksActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.activity.SignActivity;
import com.tr.hsyn.telefonrehberi.xyz.ptt.activity.relation.RelationsActivity;


public class ObjectStarter{
   
   public static void startSignActivity(Context context){
   
      context.startActivity(new Intent(context, SignActivity.class));
      Bungee.inAndOut(context);
   }
   
   public static void startBackgroundWorksActivity(Context context){
   
      context.startActivity(new Intent(context, BackgroundWorksActivity.class));
      Bungee.zoomFast(context);
   }
   
   public static void startRelationsActivity(Context context){
   
      context.startActivity(new Intent(context, RelationsActivity.class));
      Bungee.inAndOut(context);
   }
   
   public static void startStoredContactsActivity(Context context){
   
      context.startActivity(new Intent(context, ContactsActivity.class));
      Bungee.inAndOut(context);
   }
   
}
