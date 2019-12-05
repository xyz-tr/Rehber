package com.tr.hsyn.telefonrehberi.util.ui.snack;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import lombok.val;


/**
 * Copyright (C) 2014 Kenny Campagna
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Kenny Campagna
 */
public class SnackBar{
   
   /**
    * Shows a Snack Bar
    *
    * @param activity The activity to show the Snack Bar in
    * @param message  The Sting Resource of the message to show
    */
   public static void show(Activity activity, @StringRes int message){
      
      new SnackBarItem.Builder(activity)
            .setMessageResource(message)
            .show();
   }
   
   /**
    * Shows a Snack Bar
    *
    * @param activity        The activity to show the Snack Bar in
    * @param message         The Sting Resource of the message to show
    * @param actionMessage   The String Resource of the action message to show
    * @param onClickListener The onclick listener for the action button
    */
   public static void show(Activity activity, @StringRes int message, @StringRes int actionMessage, View.OnClickListener onClickListener){
      
      new SnackBarItem.Builder(activity)
            .setMessageResource(message)
            .setActionMessageResource(actionMessage)
            .setActionClickListener(onClickListener)
            .show();
   }
   
   /**
    * Shows a Snack Bar
    *
    * @param activity The activity to show the Snack Bar in
    * @param message  The  message to show
    */
   public static void show(Activity activity, Spanner message){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .show();
   }
   
   public static void show(Activity activity, String message){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .show();
   }
   
   /**
    * Shows a Snack Bar
    *
    * @param activity        The activity to show the Snack Bar in
    * @param message         The message to show
    * @param actionMessage   The  action message to show
    * @param onClickListener The onclick listener for the action button
    */
   public static void show(Activity activity, Spanner message, String actionMessage, View.OnClickListener onClickListener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .setActionMessage(actionMessage)
            .setActionClickListener(onClickListener)
            .show();
   }
   
   /**
    * Shows a SnackBar
    *
    * @param activity The activity to show the Snack Bar in
    * @param message  The Sting Resource of the message to show
    * @param listener The SnackBarListener to onWorkResult callbacks
    */
   public static void show(Activity activity, @StringRes int message, SnackBarListener listener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(activity.getString(message))
            .setSnackBarListener(listener)
            .show();
   }
   
   /**
    * Shows a SnackBar
    *
    * @param activity      The activity to show the Snack Bar in
    * @param message       The Sting Resource of the message to show
    * @param actionMessage The String Resource of the action message to show
    * @param listener      The SnackBarListener to onWorkResult callbacks
    */
   public static void show(Activity activity, @StringRes int message, @StringRes int actionMessage, SnackBarListener listener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(activity.getString(message))
            .setActionMessage(activity.getString(actionMessage))
            .setSnackBarListener(listener)
            .show();
   }
   
   /**
    * Shows a SnackBar
    *
    * @param activity      The activity to show the Snack Bar in
    * @param message       The message to show
    * @param actionMessage The action message to show
    * @param listener      The SnackBarListener to onWorkResult callbacks
    */
   public static void show(Activity activity, Spanner message, String actionMessage, SnackBarListener listener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .setActionMessage(actionMessage)
            .setSnackBarListener(listener)
            .show();
   }
   
   public static void show(Activity activity, String message, String actionMessage, SnackBarListener listener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .setActionMessage(actionMessage)
            .setSnackBarListener(listener)
            .show();
   }
   
   /**
    * Shows a SnackBar
    *
    * @param activity The activity to show the Snack Bar in
    * @param message  The message to show
    * @param listener The SnackBarListener to onWorkResult callbacks
    */
   public static void show(Activity activity, Spanner message, SnackBarListener listener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .setSnackBarListener(listener)
            .show();
   }
   
   public static void show(Activity activity, String message, SnackBarListener listener){
      
      new SnackBarItem.Builder(activity)
            .setMessage(message)
            .setSnackBarListener(listener)
            .show();
   }
   
   /**
    * Shows a Snack Bar
    *
    * @param activity     The activity to show the Snack Bar in
    * @param snackBarItem The SnackBarItem to Show
    */
   public static void show(Activity activity, SnackBarItem snackBarItem){
      
      if(snackBarItem == null){
         
         throw new NullPointerException("SnackBarItem can not be null");
      }
      
      SnackBarManager.getInstance().addSnackBar(activity, snackBarItem);
   }
   
   static void sbi(final Activity activity, @NonNull final Spanner msg, long durationMilis, boolean cancel, long delay, SnackBarListener snackBarListener, View.OnClickListener clickListener, String actionMessage, @Show.Type int messageType){
      
      if(cancel) SnackBar.cancelSnackBars(activity);
      
      int icon = R.drawable.loader_i;
      
      switch(messageType){
         
         case Show.INFO: break;
         case Show.WARN: icon = R.drawable.loader_w;break;
         case Show.ERROR: icon = R.drawable.loader_e;
         
      }
      
      
      val snack = new SnackBarItem.Builder(activity)
            .setDrawable(icon)
            .setSnackBarMessageColorResource(R.color.snack_bar_message_blue)
            .setMessage(msg)
            .setSnackBarListener(snackBarListener)
            .setDuration(durationMilis)
            .setActionClickListener(clickListener)
            .setActionMessage(actionMessage);
      
      
      new Handler(Looper.getMainLooper()).postDelayed(snack::show, delay);
   }
   
   static void sbi(final Activity activity, @NonNull final String msg, long durationMilis, boolean cancel, long delay, SnackBarListener snackBarListener, View.OnClickListener clickListener, String actionMessage, @Show.Type int messageType){
      
      if(cancel) SnackBar.cancelSnackBars(activity);
      
      int icon = R.drawable.loader_i;
      
      switch(messageType){
         
         case Show.INFO: break;
         case Show.WARN: icon = R.drawable.loader_w;
            break;
         case Show.ERROR: icon = R.drawable.loader_e;
         
      }
      
      
      val snack = new SnackBarItem.Builder(activity)
            .setDrawable(icon)
            .setSnackBarMessageColorResource(R.color.snack_bar_message_blue)
            .setMessage(msg)
            .setSnackBarListener(snackBarListener)
            .setDuration(durationMilis)
            .setActionClickListener(clickListener)
            .setActionMessage(actionMessage);
      
      
      new Handler(Looper.getMainLooper()).postDelayed(snack::show, delay);
   }
   
   /**
    * Cancels all SnackBars for the given activity
    *
    * @param activity a
    */
   private static void cancelSnackBars(Activity activity){
      
      SnackBarManager.getInstance().cancelSnackBars(activity);
   }
   
   static void dispose(Activity activity, SnackBarItem snackBarItem){
      
      SnackBarManager.getInstance().disposeSnackBar(activity, snackBarItem);
   }
   
   private static class SnackBarManager{
      
      private static SnackBarManager                                                  sManager;
      private final  ConcurrentHashMap<Activity, ConcurrentLinkedQueue<SnackBarItem>> mQueue             = new ConcurrentHashMap<>();
      private        boolean                                                          mIsShowingSnackBar = false;
      
      private boolean mIsCanceling = false;
      
      public static SnackBarManager getInstance(){
         
         if(sManager == null){ sManager = new SnackBarManager(); }
         return sManager;
      }
      
      /**
       * Cancels all SnackBar messages for an activity
       *
       * @param activity a
       */
      void cancelSnackBars(Activity activity){
         
         ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);
         
         if(list != null){
            mIsCanceling = true;
            
            for(SnackBarItem items : list){
               items.cancel();
            }
            
            mIsCanceling = false;
            list.clear();
            mQueue.remove(activity);
         }
      }
      
      /**
       * Adds a SnackBar to The queue to be displayed
       *
       * @param activity a
       * @param item     i
       */
      void addSnackBar(Activity activity, SnackBarItem item){
         
         ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);
         
         if(list == null){
            
            list = new ConcurrentLinkedQueue<>();
            mQueue.put(activity, list);
         }
         
         list.add(item);
         
         if(!mIsShowingSnackBar){
            
            showSnackBars(activity);
         }
      }
      
      /**
       * Shows the nextSnackBar for the current activity
       *
       * @param activity a
       */
      void showSnackBars(Activity activity){
         
         ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);
         
         if(list != null && !list.isEmpty()){
            mIsShowingSnackBar = true;
            
            //noinspection ConstantConditions
            list.peek().show();
         }
      }
      
      /**
       * Cleans up the {@link SnackBarItem} and the {@link Activity} it is tied to
       *
       * @param activity     The {@link Activity} tied to the {@link SnackBarItem}
       * @param snackBarItem The {@link SnackBarItem} to clean up
       */
      void disposeSnackBar(Activity activity, SnackBarItem snackBarItem){
         
         ConcurrentLinkedQueue<SnackBarItem> list = mQueue.get(activity);
         
         if(list != null){
            list.remove(snackBarItem);
            
            if(list.peek() == null){
               mQueue.remove(activity);
               mIsShowingSnackBar = false;
            }
            else if(!mIsCanceling){
               mIsShowingSnackBar = true;
               //noinspection ConstantConditions
               list.peek().show();
            }
         }
      }
   }
}