package com.tr.hsyn.telefonrehberi.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.tr.hsyn.telefonrehberi.R;

import java.util.List;


/**
 * Dialog işlemleri
 * 
 * @author hsyn
 */
@SuppressWarnings("ConstantConditions")
public class Dialog{
   
   public static View inflate(Activity activity, int id){
      
      return activity.getLayoutInflater().inflate(id, null, false);
   }
   
   public static void confirm(
         Activity activity,
         CharSequence message, 
         DialogInterface.OnClickListener okeyListener,
         DialogInterface.OnClickListener cancelListener){
   
   
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      
      builder.setMessage(message);
      builder.setPositiveButton("Tamam", okeyListener);
      builder.setNegativeButton("İptal", cancelListener);
      
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
   
      alertDialog.show();
   }
   
   public static void confirm(
         Activity activity,
         CharSequence message,
         DialogInterface.OnClickListener okeyListener){
      
      
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      
      builder.setMessage(message);
      builder.setPositiveButton("Tamam", okeyListener);
      builder.setNegativeButton("İptal", (di, w) -> di.dismiss());
      
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      alertDialog.show();
   }
   
   public static void confirm(
         Activity activity,
         CharSequence message,
         String buttonText,
         DialogInterface.OnClickListener okeyListener){
      
      
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      
      builder.setMessage(message);
      builder.setPositiveButton(buttonText, okeyListener);
      builder.setNegativeButton("İptal", (di, w) -> di.dismiss());
      
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      alertDialog.show();
   }
   
   public static void confirm(
         Activity activity,
         CharSequence message,
         DialogInterface.OnClickListener okeyListener,
         DialogInterface.OnClickListener cancelListener,
         String positiveButtonText){
      
      
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      
      builder.setMessage(message);
      builder.setPositiveButton(positiveButtonText, okeyListener);
      builder.setNegativeButton("İptal", cancelListener);
      
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      alertDialog.show();
   }

   
   public static void getString(Activity activity, CharSequence title, StringListener stringListener,
                                DialogInterface.OnClickListener cancelListener){
   
   
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      builder.setTitle(title);
   
      EditText editText = new EditText(activity);
      builder.setView(editText);
   
      builder.setPositiveButton("Tamam", (d, w) -> stringListener.onString(editText.getText().toString()));
      builder.setNegativeButton("İptal", cancelListener);
   
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
   
      alertDialog.show();
   }
   
   public static void getString(Activity activity, CharSequence title, CharSequence edittextValue, StringListener stringListener,
                                DialogInterface.OnClickListener cancelListener){
      
      
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      builder.setTitle(title);
      
      EditText editText = new EditText(activity);
      builder.setView(editText);
      editText.setText(edittextValue);
      
      builder.setPositiveButton("Tamam", (d, w) -> stringListener.onString(editText.getText().toString()));
      builder.setNegativeButton("İptal", cancelListener);
      
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      alertDialog.show();
   }
   
   
   public static void alert(Activity activity, CharSequence message){
      
      
      AlertDialog.Builder builder = new AlertDialog.Builder(activity);
      builder.setCancelable(true);
      
      builder.setMessage(message);
      
      builder.setPositiveButton("Tamam", (d, w) -> d.dismiss());
      
      AlertDialog alertDialog = builder.create();
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      alertDialog.show();
   }
   
   
   public static AlertDialog.Builder alertBuilder(Context context){
   
      return new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DefaultDialogTheme));
   }
   
   
   public static boolean hasOpenedDialogs(AppCompatActivity activity){
   
      List<Fragment> fragments = activity.getSupportFragmentManager().getFragments();
      
      if(fragments != null){
         
         for(Fragment fragment : fragments){
            
            if(fragment instanceof DialogFragment){
               
               return true;
            }
         }
      }
      
      return false;
   }
   
   
}
