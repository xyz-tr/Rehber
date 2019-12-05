package com.tr.hsyn.telefonrehberi.xyz.contact.activity.stored;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.event.CloseRequest;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ScreenSizeController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import io.paperdb.Paper;
import lombok.val;


/**
 * <h1>ContactInfoActivityDialog</h1>
 * 
 * <p>
 *    Uygulamanın kaydettiği kişiler için bilgi ekranı.
 *    Bilgiler dialog olarak gösteriliyor.
 *    Görseli kendinde yok.
 *    Gösterilecek olan view {@link #show(Context, View, DialogListener)} metodu ile verilir.
 * 
 * 
 * @author hsyn 2019-12-03 13:43:56
 */
public class ContactInfoActivityDialog extends AppCompatActivity{
   
   
   private static DialogListener      dialogListener;
   private static WeakReference<View> view;
   
   @Subscribe
   public void onCloseRequest(CloseRequest evet){
      
      finishAndRemoveTask();
   }
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      if(view != null && view.get() != null) setContentView(view.get());
      else{
         
         Logger.w("View yoooooookk");
         finishAndRemoveTask();
      }
   
      setWidth();
      
      EventBus.getDefault().register(this);
   }
   
   private static void setWidth(){
   
      int width = Paper.book(ScreenSizeController.BOOK).read(ScreenSizeController.KEY_SCREEN_WIDTH, 0);
   
      if(width != 0){
      
         ViewGroup.LayoutParams layoutParams = view.get().getLayoutParams();
      
         final int gap = 44;
         layoutParams.width = width - gap;
         view.get().setLayoutParams(layoutParams);
      }
   }
   
   @Override
   protected void onStop(){
      
      super.onStop();
   
      EventBus.getDefault().unregister(this);
      
      if(dialogListener != null){
         
         if(dialogListener.getDialogAction() != null)
            dialogListener.getDialogAction().onDialogClose(null);
      }
   }
   
   @Override
   public void onDestroy(){
      
      view = null;
      super.onDestroy();
   }
   
   @Override
   public void onResume(){
      
      super.onResume();
      
      if(dialogListener != null){
         
         if(dialogListener.getDialogAction() != null)
            dialogListener.getDialogAction().onDialogShow(null);
      }
   }
   
   public static void show(Context context, View view, DialogListener dialogListener){
   
      ContactInfoActivityDialog.view           = new WeakReference<>(view);
      ContactInfoActivityDialog.dialogListener = dialogListener;
      
      val intent = new Intent(context, ContactInfoActivityDialog.class);
      
      context.startActivity(intent);
   }
}
