package com.tr.hsyn.telefonrehberi.xyz.main.controller;


import android.app.Activity;
import android.graphics.Point;
import android.view.Display;

import io.paperdb.Paper;
import timber.log.Timber;


public class ScreenSizeController{
   
   private       boolean  isCheck;
   private final Activity activity;
   
   /**
    * Ekran ölçülerinin kaydedildiği book
    */
   public static final String BOOK = "sacreen_size";
   
   /**
    * Ekran ölçüsü kayıt anahtarı
    */
   public static final String KEY_SCREEN_WIDTH = "width";
   
   /**
    * Ekran ölçüsü kayıt anahtarı
    */
   public static final String KEY_SCREEN_HEIGHT = "height";
   
   public ScreenSizeController(Activity activity){
      
      this.activity = activity;
   }
   
   /**
    * Ekran ölçülerini alır ve kaydeder.
    */
   public final void checkScreenSize(){
      
      if(isCheck) return;
      
      Display display = activity.getWindowManager().getDefaultDisplay();
      
      Point size = new Point();
      display.getSize(size);
      int w = size.x;
      int h = size.y;
      
      Timber.d("%d x %d", w, h);
      
      Paper.book(BOOK).write(KEY_SCREEN_WIDTH, w).write(KEY_SCREEN_HEIGHT, h);
      
      /*Save save = new Save(activity, BOOK);
      save.saveInt(KEY_SCREEN_WIDTH, w).saveInt(KEY_SCREEN_HEIGHT, h);*/
      
      isCheck = true;
   }
   
   /**
    * @return Ölçülmüş ve kaydedilmiş ekran yüksekliğini döndürür.
    */
   public static int getHeight(){
      
      return Paper.book(BOOK).read(KEY_SCREEN_HEIGHT, 0);
   }
   
   /**
    * @return Ölçülmüş ve kaydedilmiş ekran genişliğini döndürür.
    */
   public static int getWidth(){
      
      return Paper.book(BOOK).read(KEY_SCREEN_WIDTH, 0);
   }
   
}
