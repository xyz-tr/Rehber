package com.tr.hsyn.telefonrehberi.util.ui.snack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.InterpolatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


/*
 * Copyright (C) 2014 Kenny Campagna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class SnackBarItem{
   
   private                 View.OnClickListener mActionClickListener;
   private                 View                 mSnackBarView;
   // The animation setValue used for animation. Set as an object for compatibility
   private                 AnimatorSet          mAnimator;
   private                 String               mMessageString;
   private                 String               mActionMessage;
   private                 Spanner              mSpannerMessage;
   // The color of the background
   private                 int                  mSnackBarColor         = Color.TRANSPARENT;
   // The color of the message
   private                 int                  mMessageColor          = Color.TRANSPARENT;
   // The default color the action item will be
   private                 int                  mActionColor           = Color.parseColor("#478eff");
   /* Flag for when the animation is canceled, should the item be disposed of. Will be setValue to false when
    the action button is selected so it removes immediately.*/
   private                 boolean              mShouldDisposeOnCancel = true;
   private                 boolean              mIsDisposed            = false;
   private                 boolean              mAutoDismiss           = true;
   private                 boolean              mIsGestureAccepted     = false;
   private                 Activity             mActivity;
   private                 SnackBarListener     mSnackBarListener;
   private                 long                 mAnimationDuration     = -1;
   private                 Interpolator         mInterpolator;
   private                 Object               mObject;
   private                 float                mPreviousY;
   private                 Typeface             mMessageTypeface       = null;
   private                 Typeface             mActionTypeface        = null;
   @StyleRes private       int                  mMessageTextAppearance = -1;
   @StyleRes private       int                  mActionTextAppearance  = -1;
   private                 float                mSnackBarOffset        = 0;
   private                 boolean              mActionButtonPressed   = false;
   private                 float                mToAnimation           = 0;
   private                 float                mFromAnimation         = 0;
   private                 int                  drawable               = -1;
   @Setter @Getter private int                  textLineSize;
   private static          int                  widthMeasureSpec;
   private static          int                  heightMeasureSpec;
   private static          int                  heightOfEachLine;
   private static          int                  paddingFirstLine;
   
   private static final int[] ATTR = new int[]{
         R.attr.snack_bar_background_color,
         R.attr.snack_bar_duration,
         R.attr.snack_bar_interpolator,
         R.attr.snack_bar_text_action_color,
         R.attr.snack_bar_text_color,
         R.attr.snack_bar_message_typeface,
         R.attr.snack_bar_action_typeface,
         R.attr.snack_bar_message_text_appearance,
         R.attr.snack_bar_action_text_appearance,
         R.attr.snack_bar_offset
   };
   
   private SnackBarItem(Activity activty){
      
      mActivity = activty;
   }
   
   /**
    * Cancels the Snack Bar from being displayed
    */
   public void cancel(){
      
      if(mAnimator != null){ mAnimator.cancel(); }
      dispose();
   }
   
   /**
    * Cleans up the Snack Bar when finished
    */
   private void dispose(){
      
      mIsDisposed = true;
      
      if(mSnackBarView != null){
         FrameLayout parent = (FrameLayout) mSnackBarView.getParent();
         if(parent != null){ parent.removeView(mSnackBarView); }
      }
      
      mAnimator            = null;
      mSnackBarView        = null;
      mActionClickListener = null;
      SnackBar.dispose(mActivity, this);
   }
   
   private static void calculateHeightOfEachLine(Context context){
      
      WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
      assert wm != null;
      Display display = wm.getDefaultDisplay();
      Point   size    = new Point();
      display.getSize(size);
      int deviceWidth = size.x;
      widthMeasureSpec  = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
      heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
      //1 line = 76; 2 lines = 76 + 66; 3 lines = 76 + 66 + 66
      //=> height of first line = 76 pixel; height of second line = third line =... n line = 66 pixel
      int heightOfFirstLine  = getHeightOfTextView(context, "A");
      int heightOfSecondLine = getHeightOfTextView(context, "A\nA") - heightOfFirstLine;
      paddingFirstLine = heightOfFirstLine - heightOfSecondLine;
      heightOfEachLine = heightOfSecondLine;
   }
   
   private static int getHeightOfTextView(Context context, String text){
      
      //if(widthMeasureSpec == 0 || heightMeasureSpec == 0) calculateHeightOfEachLine(context);
      
      // Getting height of text view before rendering to layout
      TextView textView = new TextView(context);
      textView.setPadding(18, 0, 18, 0);
      //textView.setTypeface(typeface);
      textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sb__text_size));
      textView.setText(text, TextView.BufferType.SPANNABLE);
      textView.measure(widthMeasureSpec, heightMeasureSpec);
      return textView.getMeasuredHeight();
   }
   
   private static int getLineCountOfTextViewBeforeRendering(Context context, String text){
      
      if(widthMeasureSpec == 0 || heightMeasureSpec == 0) calculateHeightOfEachLine(context);
      return (getHeightOfTextView(context, text) - paddingFirstLine) / heightOfEachLine;
   }
   
   /**
    * Sets up the touch listener to allow the SnackBar to be swiped to dismissed
    * Code from https://github.com/MrEngineer13/SnackBar
    */
   @SuppressLint("ClickableViewAccessibility")
   private void setupGestureDetector(){
      
      mSnackBarView.setOnTouchListener((view, event) -> {
         if(mIsDisposed || mIsGestureAccepted){ return false; }
         
         float y = event.getY();
         
         if(event.getAction() == MotionEvent.ACTION_MOVE){
            int[] location = new int[2];
            view.getLocationInWindow(location);
            
            if(y > mPreviousY && y - mPreviousY >= 50){
               mIsGestureAccepted     = true;
               mShouldDisposeOnCancel = false;
               mAnimator.cancel();
               createHideAnimation();
            }
         }
         
         mPreviousY = y;
         return true;
      });
   }
   
   //@SuppressWarnings("ResourceType")
   
   /**
    * Sets up the action button if available
    *
    * @param action act
    */
   private void setupActionButton(Button action){
      
      action.setVisibility(View.VISIBLE);
      //action.setText(mActionMessage.toUpperCase());
      action.setText(Html.fromHtml("<u>" + mActionMessage.toUpperCase() + "</u>"));
      action.setTextColor(mActionColor);
      
      if(mActionTextAppearance != -1){
         action.setTextAppearance(mActivity, mActionTextAppearance);
      }
      
      if(mActionTypeface != null){ action.setTypeface(mActionTypeface); }
      
      action.setOnClickListener(view -> {
         mActionButtonPressed   = true;
         mShouldDisposeOnCancel = false;
         mAnimator.cancel();
         if(mActionClickListener != null){ mActionClickListener.onClick(view); }
         createHideAnimation();
      });
   }
   
   /**
    * Gets the attributes to be used for the SnackBar from the context style
    *
    * @param context context
    */
   private void getAttributes(Context context){
      
      TypedArray a   = context.obtainStyledAttributes(ATTR);
      Resources  res = context.getResources();
      
      if(mSnackBarColor == Color.TRANSPARENT){
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            mSnackBarColor = a.getColor(0, res.getColor(R.color.snack_bar_bg_default, null));
         }
         else{
            mSnackBarColor = a.getColor(0, res.getColor(R.color.snack_bar_bg_default));
         }
      }
      
      if(mAnimationDuration == -1){ mAnimationDuration = a.getInt(1, 3000); }
      
      if(mInterpolator == null){
         int id = a.getResourceId(2, android.R.interpolator.accelerate_decelerate);
         mInterpolator = AnimationUtils.loadInterpolator(mActivity, id);
      }
      
      if(mActionColor == Color.TRANSPARENT){
         
         if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            mActionColor = a.getColor(3, res.getColor(R.color.snack_bar_action_default));
         }
      }
      
      if(mMessageColor == Color.TRANSPARENT){
         mMessageColor = a.getColor(4, Color.WHITE);
      }
      
      if(mMessageTypeface == null){
         String fontFile = a.getNonResourceString(5);
         if(!TextUtils.isEmpty(fontFile)){
            mMessageTypeface = Typeface.createFromAsset(mActivity.getAssets(), fontFile);
         }
      }
      
      if(mActionTypeface == null){
         String fontFile = a.getNonResourceString(6);
         if(!TextUtils.isEmpty(fontFile)){
            mActionTypeface = Typeface.createFromAsset(mActivity.getAssets(), fontFile);
         }
      }
      
      if(mMessageTextAppearance == -1){
         mMessageTextAppearance = a.getResourceId(7, -1);
      }
      if(mActionTextAppearance == -1){
         mActionTextAppearance = a.getResourceId(8, -1);
      }
      if(mSnackBarOffset == 0){ mSnackBarOffset = a.getDimension(9, 0); }
      a.recycle();
   }
   
   /**
    * Sets up and starts the show animation
    *
    * @param message The TextView of the Message
    * @param action  The Button of the action. May be null if no action is supplied
    */
   private void createShowAnimation(@NonNull TextView message, @Nullable Button action){
      
      mAnimator = new AnimatorSet();
      mAnimator.setInterpolator(mInterpolator);
      List<Animator> appearAnimations = new ArrayList<>();
      appearAnimations.add(getAppearAnimation(message, action));
      
      // Only add this animation if the SnackBar should auto dismiss itself
      if(mAutoDismiss){
         appearAnimations.add(ObjectAnimator.ofFloat(mSnackBarView, "alpha", 1.0f, 1.0f).setDuration(mAnimationDuration));
         appearAnimations.add(ObjectAnimator.ofFloat(mSnackBarView, "translationY", mToAnimation, mFromAnimation).setDuration(mActivity.getResources().getInteger(R.integer.snackbar_disappear_animation_length)));
      }
      
      mAnimator.playSequentially(appearAnimations);
      
      mAnimator.addListener(new AnimatorListenerAdapter(){
         
         @Override
         public void onAnimationCancel(Animator animation){
            
            if(mShouldDisposeOnCancel){ dispose(); }
            
         }
         
         @Override
         public void onAnimationEnd(Animator animation){
            
            if(mShouldDisposeOnCancel && mAutoDismiss){
               if(mSnackBarListener != null){
                  mSnackBarListener.onSnackBarFinished(mObject, mActionButtonPressed);
               }
               dispose();
            }
            
         }
         
         @Override
         public void onAnimationStart(Animator animation){
            
            if(mSnackBarListener != null){
               mSnackBarListener.onSnackBarStarted(mObject);
            }
         }
      });
      
      mAnimator.start();
   }
   
   /**
    * Sets up and starts the hide animation
    */
   private void createHideAnimation(){
      
      ObjectAnimator anim = ObjectAnimator.ofFloat(mSnackBarView, "translationY", mToAnimation, mFromAnimation).setDuration(mActivity.getResources().getInteger(R.integer.snackbar_disappear_animation_length));
      
      
      anim.addListener(new AnimatorListenerAdapter(){
         
         @Override
         public void onAnimationCancel(Animator animation){
            
            dispose();
         }
         
         @Override
         public void onAnimationEnd(Animator animation){
            
            if(mSnackBarListener != null){
               mSnackBarListener.onSnackBarFinished(mObject, mActionButtonPressed);
            }
            
            dispose();
         }
      });
      
      anim.start();
   }
   
   /**
    * Returns the animator for the appear animation
    *
    * @param message The TextView of the Message
    * @param action  The Button of the action. May be null if no action is supplied
    * @return t
    */
   private Animator getAppearAnimation(@NonNull TextView message, @Nullable Button action){
      
      
      Resources res = mActivity.getResources();
      int       h   = (int) res.getDimension(R.dimen.snack_bar_height);
      
      if(mSpannerMessage != null){
         
         textLineSize = getLineCountOfTextViewBeforeRendering(mActivity, mSpannerMessage.toString());
      }
      else{
         
         textLineSize = getLineCountOfTextViewBeforeRendering(mActivity, mMessageString);
      }
      
      if(textLineSize > 1){
         
         h += (textLineSize * 16);
      }
      
      mObject = h;
      
      mFromAnimation = h + 35;
      mToAnimation   = res.getDimension(R.dimen.snack_bar_animation_position) - mSnackBarOffset;
      int delay = res.getInteger(R.integer.snackbar_ui_delay);
      
      if(hasTranslucentNavigationBar()){
         int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
         if(resourceId > 0){
            mToAnimation -= res.getDimensionPixelSize(resourceId);
         }
      }
      
      AnimatorSet    set        = new AnimatorSet();
      List<Animator> animations = new ArrayList<>();
      set.setDuration(res.getInteger(R.integer.snackbar_appear_animation_length));
      
      animations.add(ObjectAnimator.ofFloat(mSnackBarView, "translationY", mFromAnimation, mToAnimation));
      
      ObjectAnimator messageAnim = ObjectAnimator.ofFloat(message, "alpha", 0.0f, 1.0f);
      messageAnim.setStartDelay(delay);
      animations.add(messageAnim);
      
      if(action != null){
         ObjectAnimator actionAnim = ObjectAnimator.ofFloat(action, "alpha", 0.0f, 1.0f);
         actionAnim.setStartDelay(delay);
         animations.add(actionAnim);
      }
      
      set.playTogether(animations);
      return set;
   }
   
   /**
    * Returns if the current style supports a transparent navigation bar
    *
    * @return t
    */
   private boolean hasTranslucentNavigationBar(){
      
      boolean isTranslucent;
      // Only Kit-Kit+ devices can have a translucent style
      Resources  res            = mActivity.getResources();
      int        transparencyId = res.getIdentifier("config_enableTranslucentDecor", "bool", "android");
      int[]      attrs          = new int[]{android.R.attr.windowTranslucentNavigation};
      TypedArray a              = mActivity.getTheme().obtainStyledAttributes(attrs);
      isTranslucent = a.getBoolean(0, false) && transparencyId > 0 && res.getBoolean(transparencyId);
      a.recycle();
      
      return isTranslucent;
   }
   
   /**
    * Factory for building custom SnackBarItems
    */
   @SuppressWarnings({"unused", "WeakerAccess"})
   public static class Builder{
      
      private SnackBarItem mSnackBarItem;
      
      private Resources mResources;
      
      /**
       * Factory for creating SnackBarItems
       */
      public Builder(Activity activity){
         
         mSnackBarItem = new SnackBarItem(activity);
         mResources    = activity.getResources();
      }
      
      /**
       * Shows the SnackBar
       */
      public void show(){
         
         SnackBar.show(mSnackBarItem.mActivity, build());
      }
      
      /**
       * Creates the SnackBarItem
       *
       * @return t
       */
      public SnackBarItem build(){
         
         return mSnackBarItem;
      }
      
      /**
       * Sets the gif for the SnackBarItem
       *
       * @param drawable d
       * @return t
       */
      public Builder setDrawable(int drawable){
         
         mSnackBarItem.drawable = drawable;
         return this;
      }
      
      public Builder setMessage(String message){
         
         mSnackBarItem.mMessageString = message;
         return this;
      }
      
      public Builder setMessage(Spanner message){
         
         mSnackBarItem.mSpannerMessage = message;
         return this;
      }
      
      /**
       * Sets the message for the SnackBarItem
       *
       * @param message m
       * @return t
       */
      public Builder setMessageResource(@StringRes int message){
         
         mSnackBarItem.mMessageString = mResources.getString(message);
         return this;
      }
      
      /**
       * Sets the Action Message of the SnackbarItem
       *
       * @param actionMessage a
       * @return t
       */
      public Builder setActionMessage(String actionMessage){
         // guard against any null values being passed
         if(TextUtils.isEmpty(actionMessage)){ return this; }
         
         mSnackBarItem.mActionMessage = actionMessage.toUpperCase();
         return this;
      }
      
      /**
       * Sets the Action Message of the SnackbarItem
       *
       * @param actionMessage a
       * @return t
       */
      public Builder setActionMessageResource(@StringRes int actionMessage){
         
         mSnackBarItem.mActionMessage = mResources.getString(actionMessage);
         return this;
      }
      
      /**
       * Sets the onNotificationItemClicked listener for the action message
       *
       * @param onClickListener c
       * @return t
       */
      public Builder setActionClickListener(View.OnClickListener onClickListener){
         
         mSnackBarItem.mActionClickListener = onClickListener;
         return this;
      }
      
      /**
       * Sets the default color of the action message
       *
       * @param color c
       * @return t
       */
      public Builder setActionMessageColor(int color){
         
         mSnackBarItem.mActionColor = color;
         return this;
      }
      
      /**
       * Sets the default color of the action message
       *
       * @param color c
       * @return t
       */
      public Builder setActionMessageColorResource(@ColorRes int color){
         
         mSnackBarItem.mActionColor = mResources.getColor(color);
         return this;
      }
      
      /**
       * Sets the background color of the SnackBar
       *
       * @param color c
       * @return t
       */
      public Builder setSnackBarBackgroundColor(int color){
         
         mSnackBarItem.mSnackBarColor = color;
         return this;
      }
      
      /**
       * Sets the background color of the SnackBar
       *
       * @param color c
       * @return t
       */
      public Builder setSnackBarBackgroundColorResource(@ColorRes int color){
         
         mSnackBarItem.mSnackBarColor = mResources.getColor(color);
         return this;
      }
      
      /**
       * Sets the color of the message of the SnackBar
       *
       * @param color c
       * @return t
       */
      public Builder setSnackBarMessageColor(int color){
         
         mSnackBarItem.mMessageColor = color;
         return this;
      }
      
      /**
       * Sets the color of the message of the SnackBar
       *
       * @param color c
       * @return t
       */
      public Builder setSnackBarMessageColorResource(@ColorRes int color){
         
         mSnackBarItem.mMessageColor = mResources.getColor(color);
         return this;
      }
      
      /**
       * Sets the duration of the SnackBar in milliseconds
       *
       * @param duration d
       * @return t
       */
      public Builder setDuration(long duration){
         
         mSnackBarItem.mAnimationDuration = duration;
         return this;
      }
      
      /**
       * Sets the duration of the SnackBar in milliseconds
       *
       * @param duration d
       * @return t
       */
      public Builder setDurationResource(@IntegerRes int duration){
         
         mSnackBarItem.mAnimationDuration = mResources.getInteger(duration);
         return this;
      }
      
      /**
       * Set the Interpolator of the SnackBar animation
       *
       * @param interpolator i
       * @return t
       */
      public Builder setInterpolator(Interpolator interpolator){
         
         mSnackBarItem.mInterpolator = interpolator;
         return this;
      }
      
      /**
       * Set the Interpolator of the SnackBar animation
       *
       * @param interpolator i
       * @return t
       */
      public Builder setInterpolatorResource(@InterpolatorRes int interpolator){
         
         mSnackBarItem.mInterpolator = AnimationUtils.loadInterpolator(mSnackBarItem.mActivity, interpolator);
         return this;
      }
      
      /**
       * Set the SnackBars object that will be returned in the SnackBarListener call backs
       *
       * @param object o
       * @return t
       */
      public Builder setObject(Object object){
         
         mSnackBarItem.mObject = object;
         return this;
      }
      
      /**
       * Sets the SnackBarListener
       *
       * @param listener l
       * @return t
       */
      public Builder setSnackBarListener(SnackBarListener listener){
         
         mSnackBarItem.mSnackBarListener = listener;
         return this;
      }
      
      /**
       * Sets the typeface for the SnackBar message
       *
       * @param typeFace t
       * @return t
       */
      public Builder setMessageTypeface(Typeface typeFace){
         
         mSnackBarItem.mMessageTypeface = typeFace;
         return this;
      }
      
      /**
       * Sets the typeface for the SnackBar action
       *
       * @param typeFace t
       * @return t
       */
      public Builder setActionTypeface(Typeface typeFace){
         
         mSnackBarItem.mActionTypeface = typeFace;
         return this;
      }
      
      /**
       * Sets whether a SnackBar should auto dismiss itself, defaulted to true.
       * If setValue to false,* the duration value is ignored for the SnackBar
       *
       * @param autoDismiss a
       * @return t
       */
      public Builder setAutoDismiss(boolean autoDismiss){
         
         mSnackBarItem.mAutoDismiss = autoDismiss;
         return this;
      }
      
      /**
       * Sets the text appearance style for the SnackBar message
       *
       * @param textAppearance t
       * @return t
       */
      public Builder setMessageTextAppearance(@StyleRes int textAppearance){
         
         mSnackBarItem.mMessageTextAppearance = textAppearance;
         return this;
      }
      
      /**
       * Sets the text appearance style for the SnackBar action
       *
       * @param textAppearance t
       * @return t
       */
      public Builder setActionTextAppearance(@StyleRes int textAppearance){
         
         mSnackBarItem.mActionTextAppearance = textAppearance;
         return this;
      }
      
      /**
       * Sets the offset for the SnackBar
       *
       * @param offset o
       * @return t
       */
      public Builder setSnackBarOffset(float offset){
         
         mSnackBarItem.mSnackBarOffset = offset;
         return this;
      }
      
      /**
       * Sets the offset resource for the SnackBar
       *
       * @param offset o
       * @return t
       */
      public Builder setSnackBarOffsetResource(@DimenRes int offset){
         
         mSnackBarItem.mSnackBarOffset = mResources.getDimension(offset);
         return this;
      }
   }
   
   /**
    * Shows the Snack Bar. This method is strictly for the SnackBarManager to call.
    */
   void show(){
      
      FrameLayout parent = mActivity.findViewById(android.R.id.content);
      mSnackBarView = mActivity.getLayoutInflater().inflate(R.layout.snack_bar, parent, false);
      getAttributes(mActivity);
      
      
      if(drawable != -1){
         
         try{
            GifDrawable gif = new GifDrawable(mActivity.getResources(), drawable);
            //gif.setSpeed(0.8f);
            
            GifImageView gifImageView = mSnackBarView.findViewById(R.id.gifImageView);
            gifImageView.setImageDrawable(gif);
            gifImageView.setVisibility(View.VISIBLE);
         }
         catch(IOException e){
            e.printStackTrace();
         }
      }
      
      
      // Setting up the background
      Drawable bg = mSnackBarView.getBackground();
      
      // Tablet SnackBars have a shape drawable as a background
      if(bg instanceof GradientDrawable){
         
         ((GradientDrawable) bg).setColor(mSnackBarColor);
      }
      else{
         
         mSnackBarView.setBackgroundColor(mSnackBarColor);
      }
      
      setupGestureDetector();
      TextView messageTV = mSnackBarView.findViewById(R.id.message);
      Button   actionBtn = null;
      
      if(mMessageTextAppearance != -1) messageTV.setTextAppearance(mActivity, mMessageTextAppearance);
      if(mMessageTypeface != null){ messageTV.setTypeface(mMessageTypeface); }
      
      messageTV.setTextColor(mMessageColor);
      
      messageTV.setText(mSpannerMessage != null ? mSpannerMessage : mMessageString);
      
      if(!TextUtils.isEmpty(mActionMessage)){
         // Only setValue up the action button when an action message has been supplied
         setupActionButton(actionBtn = mSnackBarView.findViewById(R.id.action));
      }
      parent.addView(mSnackBarView);
      createShowAnimation(messageTV, actionBtn);
   }
}