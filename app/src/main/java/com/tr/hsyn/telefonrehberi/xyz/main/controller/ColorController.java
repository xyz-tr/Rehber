package com.tr.hsyn.telefonrehberi.xyz.main.controller;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.widget.ProgressBar;

import com.simmorsal.recolor_project.ReColor;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Intx;
import com.tr.hsyn.telefonrehberi.util.event.ColorChanged;
import com.tr.hsyn.telefonrehberi.util.observable.Observe;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.main.Kickback_MainBox;
import com.tr.hsyn.telefonrehberi.xyz.main.color.SelectColorDialog;

import org.greenrobot.eventbus.EventBus;

import java.util.function.IntConsumer;

import lombok.Getter;


/**
 * <h1>ColorController</h1>
 * {@link com.tr.hsyn.telefonrehberi.xyz.main.MainActivity} için renkleri yönetecek
 *
 * @author hsyn
 * @date 2019-10-19 18:04:57
 */
public class ColorController{
   
   /**
    * Seçilen rengin index değeri
    */
   private                int selectedColorIndex;
   /**
    * Renk değiştirildiğinde oynatılan animasyonun süresi
    */
   private static         int COLOR_ANIMATION_DURATION = 1000;
   /**
    * Seçilen son renk. Renk değiştirildiğinde değeri değişecek
    */
   @Getter private static int lastColor                = Color.TRANSPARENT;
   /**
    * Seçilen renge uygun ripple
    */
   @Getter private static       int              wellRipple;
   private final                Context          context;
   /**
    * Seçilen rengin kayıt anahtarı
    */
   private final                String           KEY_SELECTED_COLOR_INDEX = "selectedColorIndex";
   /**
    * Renklerin kayıt edildiği ayar dosyası
    */
   public static final          String           PREF_COLORS              = "colors";
   /**
    * Kaydedilen primary color değerinin kayıt anahtarı
    */
   public static final          String           KEY_PRIMARY_COLOR        = "primaryColor";
   /**
    * Primary color
    */
   @Getter private static final Observe<Integer> primaryColor             = new Observe<>();

   static{
      
      primaryColor.setObserver(ColorController::onColorChanged);
   }
   
   public ColorController(Context context){
      
      this.context = context;
      initColor();
   }
   
   public void openChangeColorDialog(Activity activity){
      
      new SelectColorDialog(activity)
            .setItemSelectListener(this::saveColor)
            .setSelectedIndex(getSelectedColorIndex());
   }
   
   private void saveColor(int colorIndex){
      
      //@off
      int color = R.color.colorPrimary;
      lastColor = getMyColor(selectedColorIndex);
      
      switch(colorIndex){
         
         case SelectColorDialog.DEFAULT     : break;
         case SelectColorDialog.PURPLE      : color = R.color.color_primary_purple;      break;
         case SelectColorDialog.BLUE        : color = R.color.color_primary_blue;        break;
         case SelectColorDialog.ORANGE      : color = R.color.color_primary_orange;      break;
         case SelectColorDialog.GREEN       : color = R.color.color_primary_green;       break;
         case SelectColorDialog.BROWN       : color = R.color.color_primary_brown;       break;
         case SelectColorDialog.GREY        : color = R.color.color_primary_grey;        break;
         case SelectColorDialog.TEAL        : color = R.color.color_primary_teal;        break;
         case SelectColorDialog.PINK        : color = R.color.color_primary_pink;        break;
         case SelectColorDialog.LIME        : color = R.color.color_primary_lime;        break;
         case SelectColorDialog.AMBER       : color = R.color.color_primary_amber;       break;
         case SelectColorDialog.LIGHT_GREEN : color = R.color.color_primary_light_green; break;
         case SelectColorDialog.CHOCOLATE   : color = R.color.color_primary_çikolata;    break;
         case SelectColorDialog.SKY         : color = R.color.color_primary_sky;         break;
         case SelectColorDialog.NIGHT       : color = R.color.color_primary_night;       break;
         case SelectColorDialog.KESTANE     : color = R.color.color_primary_kestane;     break;
         case SelectColorDialog.ORKIDE      : color = R.color.color_primary_orkide;      break;
         case SelectColorDialog.YONCA       : color = R.color.color_primary_yonca;       break;
         case SelectColorDialog.ALEV        : color = R.color.color_primary_alev;        break;
         case SelectColorDialog.HAVUC       : color = R.color.color_primary_havuc;       break;
      }
      
      saveColorWithIndex(color, colorIndex);
      
      //@on
      changePrimaryColor(colorIndex);
   }
   
   private int getMyColor(int index){
      
      int color = u.getColor(context, R.color.colorPrimary);
      wellRipple = R.drawable.ripple;
      
      switch(index){
         
         case SelectColorDialog.DEFAULT: break;
         
         case SelectColorDialog.PURPLE:
            
            color = u.getColor(context, R.color.color_primary_purple);
            wellRipple = R.drawable.ripple_purple;
            break;
         
         case SelectColorDialog.BLUE:
            
            color = u.getColor(context, R.color.color_primary_blue);
            wellRipple = R.drawable.ripple_light_blue;
            break;
         
         case SelectColorDialog.ORANGE:
            
            color = u.getColor(context, R.color.color_primary_orange);
            wellRipple = R.drawable.ripple_orange;
            break;
         
         case SelectColorDialog.GREEN:
            
            color = u.getColor(context, R.color.color_primary_green);
            wellRipple = R.drawable.ripple_cimen;
            break;
         
         case SelectColorDialog.BROWN:
            
            color = u.getColor(context, R.color.color_primary_brown);
            wellRipple = R.drawable.ripple_toprak;
            break;
         
         case SelectColorDialog.GREY:
            
            color = u.getColor(context, R.color.color_primary_grey);
            wellRipple = R.drawable.ripple_gri;
            break;
         
         case SelectColorDialog.TEAL:
            
            color = u.getColor(context, R.color.color_primary_teal);
            wellRipple = R.drawable.ripple_teal;
            break;
         
         case SelectColorDialog.PINK:
            
            color = u.getColor(context, R.color.color_primary_pink);
            wellRipple = R.drawable.ripple_pink;
            break;
         
         case SelectColorDialog.LIME:
            
            color = u.getColor(context, R.color.color_primary_lime);
            wellRipple = R.drawable.ripple_lime;
            break;
         
         case SelectColorDialog.AMBER:
            
            color = u.getColor(context, R.color.color_primary_amber);
            wellRipple = R.drawable.ripple_amber;
            break;
         
         case SelectColorDialog.LIGHT_GREEN:
            
            color = u.getColor(context, R.color.color_primary_light_green);
            wellRipple = R.drawable.ripple_light_green;
            break;
         
         
         case SelectColorDialog.CHOCOLATE:
            
            color = u.getColor(context, R.color.color_primary_çikolata);
            wellRipple = R.drawable.ripple_cikolata;
            break;
         
         case SelectColorDialog.SKY:
            
            color = u.getColor(context, R.color.color_primary_sky);
            wellRipple = R.drawable.ripple_dark_blue;
            break;
         
         case SelectColorDialog.NIGHT:
            
            color = u.getColor(context, R.color.color_primary_night);
            wellRipple = R.drawable.ripple_night;
            break;
         
         
         case SelectColorDialog.KESTANE:
            
            color = u.getColor(context, R.color.color_primary_kestane);
            wellRipple = R.drawable.ripple_kestane;
            break;
         
         case SelectColorDialog.ORKIDE:
            
            color = u.getColor(context, R.color.color_primary_orkide);
            wellRipple = R.drawable.ripple_orkide;
            break;
         
         case SelectColorDialog.YONCA:
            
            color = u.getColor(context, R.color.color_primary_yonca);
            wellRipple = R.drawable.ripple_yonca;
            break;
         
         
         case SelectColorDialog.ALEV:
            
            color = u.getColor(context, R.color.color_primary_alev);
            wellRipple = R.drawable.ripple_alev;
            break;
         
         case SelectColorDialog.HAVUC:
            
            color = u.getColor(context, R.color.color_primary_havuc);
            wellRipple = R.drawable.ripple_havuc;
            break;
      }
   
      Kickback_MainBox.getInstance().setWellRipple(wellRipple);
      return color;
   }
   
   private void saveColorWithIndex(int color, int colorIndex){
      
      context.getSharedPreferences(PREF_COLORS, Context.MODE_PRIVATE)
             .edit()
             .putInt(KEY_PRIMARY_COLOR, u.getColor(context, color))
             .putInt(KEY_SELECTED_COLOR_INDEX, colorIndex)
             .apply();
   }
   
   /**
    * PrimaryColor'ın değiştiği yer.
    *
    * @param colorIndex Listeden seçilen rengin sırası.
    *                   Bu sıra öncekiyle aynı olamaz çünkü listede zaten seçili olan renk seçilemiyor.
    */
   private void changePrimaryColor(int colorIndex){
      
      primaryColor.setValue(getMyColor(selectedColorIndex = colorIndex));
   }
   
   /**
    * Uygulamanın seçilmiş rengi.
    * Her index bir renge karşılık geliyor.
    *
    * @return Seçilen rengin sırası.
    */
   private int getSelectedColorIndex(){
      
      return context.getSharedPreferences(PREF_COLORS, Context.MODE_PRIVATE).getInt(KEY_SELECTED_COLOR_INDEX, Intx.ZERO);
   }
   
   /**
    * Progressbar'ın rengini uygulamanın rengine uydur.
    *
    * @param context     Context
    * @param progressBar Progressbar
    */
   public static void setIndaterminateProgressColor(Context context, ProgressBar progressBar){
      
      progressBar.getIndeterminateDrawable().setColorFilter(u.getPrimaryColor(context), android.graphics.PorterDuff.Mode.SRC_IN);
   }
   
   public static void changeStatusBarColor(Activity activity, int color){
      
      activity.getWindow().setStatusBarColor(u.darken(color, .9F));
   }
   
   /**
    * StatusBar rengini değiştir.
    *
    * @param context Activity
    */
   public static void changeStatusBarColor(Context context){
      
      int COLOR_ANIMATION_DURATION = 150;
      
      int color = u.getPrimaryColor(context);
      
      new ReColor(context).setStatusBarColor(u.colorToString(ColorController.getLastColor()), u.colorToString(u.darken(color, .9F)), COLOR_ANIMATION_DURATION);
   }
   
   public static void setRecyclerColor(Context context, FastScrollRecyclerView recyclerView){
      
      int color = u.getPrimaryColor(context);
      
      recyclerView.setThumbColor(color);
      recyclerView.setPopupBgColor(color);
   }
   
   public static void runColorAnimation(IntConsumer intConsumers){
      
      int color = primaryColor.getValue();
      
      ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), ColorController.getLastColor(), color);
      colorAnimation.setDuration(COLOR_ANIMATION_DURATION);
      
      colorAnimation.addUpdateListener(animator -> intConsumers.accept((int) animator.getAnimatedValue()));
      
      colorAnimation.start();
   }
   
   /**
    * Renk değişti.
    *
    * @param newColor Yeni renk
    */
   private static void onColorChanged(int newColor){
      
      EventBus.getDefault().post(new ColorChanged(newColor));
   }
   
   private void initColor(){
      
      selectedColorIndex = getSelectedColorIndex();
      wellRipple         = getWellRipple(selectedColorIndex);
      primaryColor.initValue(u.getPrimaryColor(context));
   }
   
   /**
    * Seçilen renge göre ripple.
    *
    * @param index index
    * @return resource id
    */
   private int getWellRipple(int index){
      
      //@off
      int ripple = R.drawable.ripple;
      
      switch(index){

         case SelectColorDialog.DEFAULT     :                                        break;
         case SelectColorDialog.PURPLE      : ripple = R.drawable.ripple_purple;     break;
         case SelectColorDialog.BLUE        : ripple = R.drawable.ripple_light_blue; break;
         case SelectColorDialog.ORANGE      : ripple = R.drawable.ripple_orange;     break;
         case SelectColorDialog.GREEN       : ripple = R.drawable.ripple_cimen;      break;
         case SelectColorDialog.BROWN       : ripple = R.drawable.ripple_toprak;     break;
         case SelectColorDialog.GREY        : ripple = R.drawable.ripple_gri;        break;
         case SelectColorDialog.TEAL        : ripple = R.drawable.ripple_teal;       break;
         case SelectColorDialog.PINK        : ripple = R.drawable.ripple_pink;       break;
         case SelectColorDialog.LIME        : ripple = R.drawable.ripple_lime;       break;
         case SelectColorDialog.AMBER       : ripple = R.drawable.ripple_amber;      break;
         case SelectColorDialog.LIGHT_GREEN : ripple = R.drawable.ripple_light_green;break;
         case SelectColorDialog.CHOCOLATE   : ripple = R.drawable.ripple_cikolata;   break;
         case SelectColorDialog.SKY         : ripple = R.drawable.ripple_dark_blue;  break;
         case SelectColorDialog.NIGHT       : ripple = R.drawable.ripple_night;      break;
         case SelectColorDialog.KESTANE     : ripple = R.drawable.ripple_kestane;    break;
         case SelectColorDialog.ORKIDE      : ripple = R.drawable.ripple_orkide;     break;
         case SelectColorDialog.YONCA       : ripple = R.drawable.ripple_yonca;      break;
      }
      
      //@on
   
      Kickback_MainBox.getInstance().setWellRipple(ripple);
      return ripple;
   }
   
   
}
