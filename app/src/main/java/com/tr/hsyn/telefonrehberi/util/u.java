package com.tr.hsyn.telefonrehberi.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;


public class u{
   
   //public static final Logger         log            = Logger.jLog();
   public static final ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
   
   private static final int MINUTE = 60;
   private static final int HOUR   = MINUTE * 60;
   private static final int DAY    = HOUR * 24;
   
   public static String formatSeconds(int seconds){
      
      if(seconds >= DAY){
         
         int day   = seconds / DAY;
         int kalan = seconds % DAY;
         int hour  = kalan / HOUR;
         kalan = kalan % HOUR;
         int minute = kalan / MINUTE;
         kalan = kalan % MINUTE;
         int second = kalan % MINUTE;
         
         return String.format(Locale.getDefault(), "%02d:%02d:%02d:%02d", day, hour, minute, second);
      }
      else if(seconds >= HOUR){
         
         int hour   = seconds / HOUR;
         int kalan  = seconds % HOUR;
         int minute = kalan / MINUTE;
         kalan = kalan % MINUTE;
         int second = kalan % MINUTE;
         
         return String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minute, second);
      }
      else if(seconds >= MINUTE){
         
         int minute = seconds / MINUTE;
         int second = seconds % MINUTE;
         
         return String.format(Locale.getDefault(), "%02d:%02d", minute, second);
      }
      else{
         
         return String.format(Locale.getDefault(), "00:%02d", seconds);
      }
   }
   
   @NonNull
   public static String formatMilliseconds(long milliseconds){
      
      long oneHour = 60000 * 60;
      long oneDay  = oneHour * 24;
      
      long day    = milliseconds / oneDay;
      long hour   = (milliseconds % oneDay) / oneHour;
      long minute = ((milliseconds % oneDay) % oneHour) / 60000;
      long second = (((milliseconds % oneDay) % oneHour) % 60000) / 1000;
      
      
      if(day == 0){
         
         if(hour == 0){
            
            return String.format(new Locale("tr"), "%02d:%02d", minute, second);
         }
         else{
            
            return String.format(new Locale("tr"), "%02d:%02d:%02d", hour, minute, second);
         }
      }
      
      return String.format(new Locale("tr"), "%02d:%02d:%02d:%02d", day, hour, minute, second);
   }
   
   public static boolean copyToClipboard(Context context, String text){
   
      if(context == null){ return false; }
      
      ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
      ClipData         clip      = ClipData.newPlainText("xyz", text);
   
      if(clipboard == null){ return false; }
      
      clipboard.setPrimaryClip(clip);
      Toast.makeText(context, "Kopyalandı\n" + text, Toast.LENGTH_SHORT).show();
      return true;
   }
   
   @Deprecated
   public static String format(String text, Object... args){
      
      return String.format(Locale.getDefault(), text, args);
   }
   
   public static String colorToString(int color){
      
      return String.format("#%06X", 0xFFFFFF & color);
   }
   
   public static int lighter(int color, float factor){
      
      int red   = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
      int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
      int blue  = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
      return Color.argb(Color.alpha(color), red, green, blue);
   }
   
   private static int crimp(int c){
      
      return Math.min(Math.max(c, 0), 255);
   }
   
   public static int darken(int color, double factor){
      
      return (color & 0xFF000000) |
             (crimp((int) (((color >> 16) & 0xFF) * factor)) << 16) |
             (crimp((int) (((color >> 8) & 0xFF) * factor)) << 8) |
             (crimp((int) (((color) & 0xFF) * factor)));
   }
   
   public static int dpToPx(Context context, int dp){
      
      float density = context.getResources().getDisplayMetrics().density;
      return Math.round((float) dp * density);
   }
   
   /**
    * This method converts device specific pixels to density independent pixels.
    *
    * @param context Context to get resources and device specific display metrics
    * @param px      A value in px (pixels) unit. Which we need to convert into db
    * @return A float value to represent dp equivalent to px value
    */
   public static float pxToDp(Context context, float px){
   
      return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
   }
   
   public static void setTintDrawable(@NonNull Drawable drawable, @ColorInt int color){
      
      drawable.clearColorFilter();
      drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      drawable.invalidateSelf();
      Drawable wrapDrawable = DrawableCompat.wrap(drawable).mutate();
      DrawableCompat.setTint(wrapDrawable, color);
   }
   
   
   
   public static String formatShortDate(long milis){
      
      return String.format(Locale.getDefault(), "%te %<tB %<tY %<tA", new Date(milis));
   }
   
   public static String formatDate(long milis){
      
      return String.format(Locale.getDefault(), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date(milis));
   }
   
 
   
   public static void keepGoing(IKeepGoing action){
      
      try{
         
         action.go();
      }
      catch(Exception e){
         
         Timber.w(e);
      }
   }
   
   private static WeakReference<ReColor> reColor;
   
   public static void initReColor(Context context){
      
      setReColor(context);
   }
   
   private static void setReColor(Context context){
      
      reColor = new WeakReference<>(new ReColor(context));
   }
   
   private static WeakReference<ReColor> reColorReference(){
      
      return reColor;
   }
   
   public static void changeColor(View view, int color, int duration){
      
      ReColor reColor = reColorReference().get();
   
      if(reColor == null){ return; }
      
      reColor.setViewBackgroundColor(view, null, colorToString(color), duration);
   }
   
   public static int getPrimaryColor(Context context){
   
      if(context == null){
         return 0xF44336;//colorPrimary
      }
      
      return context
            .getSharedPreferences(
                  ColorController.PREF_COLORS,
                  Context.MODE_PRIVATE)
            .getInt(
                  ColorController.KEY_PRIMARY_COLOR, getColor(context, R.color.colorPrimary));
   }
   
   public static int getColor(Context context, int resourceId){
      
      return ContextCompat.getColor(context, resourceId);
   }
   
   public static void toast(Context context, String message){
      
      Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
   }
   
   public static Intent createIntent(){
      
      Intent i = new Intent();
      
      return i;
   }
   
   public static Intent createIntent(String action){
      
      Intent i = new Intent(action);
      
      return i;
   }
   
   public static <T extends Parcelable> Intent createIntent(String key, T value){
      
      Intent i = new Intent();
      i.putExtra(key, value);
      return i;
   }
   
   public static Intent createIntent(Context context, Class<?> clazz){
      
      Intent i = new Intent(context, clazz);
      
      return i;
   }
   
   
   
   
}
