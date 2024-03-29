package com.tr.hsyn.telefonrehberi.util.ui.cookie;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;


/**
 * CookieBar is a lightweight library for showing a brief message at the top or bottom of the
 * screen. <p>
 * <pre>
 * new CookieBar
 *      .Builder(MainActivity.this)
 *      .setTitle("TITLE")
 *      .setMessage("MESSAGE")
 *      .setAction("ACTION", new OnActionClickListener() {})
 *      .show();
 * </pre>
 * <p> Created by Eric on 2017/3/2.
 */
public class CookieBar{
   
   private Cookie   cookieView;
   private Activity context;
   
   private CookieBar(){
      
   }
   
   private CookieBar(Activity context, Params params){
      
      this.context = context;
      cookieView   = new Cookie(context);
      cookieView.setParams(params);
   }
   
   public static Builder builder(Activity activity){
      
      return new Builder(activity);
   }
   
   public void show(){
      
      if(cookieView != null){
         
         final ViewGroup decorView = (ViewGroup) context.getWindow().getDecorView();
         final ViewGroup content   = decorView.findViewById(android.R.id.content);
         
         if(cookieView.getParent() == null){
            
            if(cookieView.getLayoutGravity() == Gravity.BOTTOM){
               
               content.addView(cookieView);
            }
            else{
               
               decorView.addView(cookieView);
            }
         }
      }
   }
   
   public static class Builder{
      
      public  Activity context;
      private Params   params = new Params();
      
      /**
       * Create a builder for an cookie.
       */
      public Builder(Activity activity){
         
         this.context = activity;
      }
      
      public Builder icon(@DrawableRes int iconResId){
         
         params.iconResId = iconResId;
         return this;
      }
      
      public Builder title(String title){
         
         params.title = title;
         return this;
      }
      
      public Builder title(Spanner title){
         
         params.spanedTitle = title;
         return this;
      }
      
      public Builder title(@StringRes int resId){
         
         params.title = context.getString(resId);
         return this;
      }
      
      public Builder message(String message){
         
         params.message = message;
         return this;
      }
      
      public Builder message(Spanner message){
         
         params.spanedMessage = message;
         return this;
      }
      
      public Builder message(@StringRes int resId){
         
         params.message = context.getString(resId);
         return this;
      }
      
      public Builder duration(long duration){
         
         params.duration = duration;
         return this;
      }
      
      public Builder delay(long delay){
         
         params.delay = delay;
         return this;
      }
      
      public Builder titleColor(@ColorRes int titleColor){
         
         params.titleColor = titleColor;
         return this;
      }
      
      public Builder messageColor(@ColorRes int messageColor){
         
         params.messageColor = messageColor;
         return this;
      }
      
      public Builder backgroundColor(@ColorRes int backgroundColor){
         
         params.backgroundColor = backgroundColor;
         return this;
      }
      
      public Builder actionColor(@ColorRes int actionColor){
         
         params.actionColor = actionColor;
         return this;
      }
      
      public Builder action(String action, OnActionClickListener onActionClickListener){
         
         params.action                = action;
         params.onActionClickListener = onActionClickListener;
         return this;
      }
      
      public Builder action(@StringRes int resId, OnActionClickListener onActionClickListener){
         
         params.action                = context.getString(resId);
         params.onActionClickListener = onActionClickListener;
         return this;
      }
      
      public Builder actionWithIcon(@DrawableRes int resId, OnActionClickListener onActionClickListener){
         
         params.actionIcon            = resId;
         params.onActionClickListener = onActionClickListener;
         return this;
      }
      
      public Builder gravity(int layoutGravity){
         
         params.layoutGravity = layoutGravity;
         return this;
      }
      
      public CookieBar show(){
         
         final CookieBar cookie = create();
         
         new Handler(Looper.getMainLooper()).postDelayed(cookie::show, params.delay);
         return cookie;
      }
      
      public CookieBar create(){
         
         CookieBar cookie = new CookieBar(context, params);
         return cookie;
      }
   }
   
   final static class Params{
      
      public long delay;
      
      public String title;
      
      public Spanner spanedTitle;
      
      public String message;
      
      public Spanner spanedMessage;
      
      public String action;
      
      public OnActionClickListener onActionClickListener;
      
      public int iconResId;
      
      public int backgroundColor;
      
      public int titleColor;
      
      public int messageColor;
      
      public int actionColor;
      
      public long duration = 2000;
      
      public int layoutGravity = Gravity.BOTTOM;
      
      public int actionIcon;
   }
   
}
