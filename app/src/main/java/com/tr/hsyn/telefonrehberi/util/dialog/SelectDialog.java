package com.tr.hsyn.telefonrehberi.util.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.adapter.SelectDialogAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.SelectDialogListener;

import java.lang.ref.WeakReference;

//@off
/**
 * <h1>SelectDialog</h1>
 * 
 * <p>
 *    Listeden bir eleman seçmek için dialog.
 *    Liste elemanları basit birer text.
 *    Bu elemanlar {@link #setItems( String[])} metodu ile verilir.
 *    Dialog başlığı ve icon da eklenebilir.
 *    Seçilen eleman ise {@link #setListener( SelectDialogListener)} metodu ile
 *    verilen dinleyici aracılığı ile alınır.
 *    
 * <p>
 *    Hızlı bir şekilde listeden eleman seçtirmek için kullanılır.
 * 
 * @author hsyn 2019-12-03 13:16:05
 */
//@on
@SuppressWarnings({"ConstantConditions", "WeakerAccess"})
public class SelectDialog implements SelectDialogListener, ISelectDialog{
   
   private CharSequence            title;
   private CharSequence[]          items;
   private WeakReference<Activity> activityWeakReference;
   private SelectDialogListener    listener;
   private boolean                 cancelable     = true;
   private int                     animationStyle = R.style.DialogAnimationBounce;
   private AlertDialog             dialog;
   private Drawable                titleIcon;
   
   
   protected SelectDialog(Activity activity){
      
      activityWeakReference = new WeakReference<>(activity);
   }
   
   @Override
   @SuppressLint("InflateParams")
   public void build(){
      
      dialog = new AlertDialog.Builder(activityWeakReference.get()).create();
      View         view         = activityWeakReference.get().getLayoutInflater().inflate(R.layout.select_dialog_layout, null, false);
      RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
      TextView     title        = view.findViewById(R.id.title);
      ImageView    icon         = view.findViewById(R.id.titleIcon);
      
      if(titleIcon == null){
         
         icon.setVisibility(View.GONE);
      }
      else{
         
         icon.setImageDrawable(titleIcon);
      }
      
      if(this.title == null){
         
         title.setVisibility(View.GONE);
         view.findViewById(R.id.divider).setVisibility(View.GONE);
         
      }
      else{
         
         title.setText(this.title);
      }
      
      dialog.setView(view);
      
      recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
      recyclerView.setAdapter(new SelectDialogAdapter(items, this));
      dialog.setCancelable(cancelable);
      dialog.getWindow().getAttributes().windowAnimations = animationStyle;
      
      dialog.show();
   }
   
   @Override
   public ISelectDialog setTitle(CharSequence title){
      
      this.title = title;
      return this;
   }
   
   @Override
   public ISelectDialog setItems(CharSequence[] items){
      
      this.items = items;
      return this;
   }
   
   @Override
   public ISelectDialog setListener(SelectDialogListener listener){
      
      this.listener = listener;
      return this;
   }
   
   @Override
   public ISelectDialog setCancelable(boolean cancelable){
      
      this.cancelable = cancelable;
      return this;
   }
   
   @Override
   public void setAnimationStyle(int animationStyle){
      
      this.animationStyle = animationStyle;
   }
   
   @Override
   public ISelectDialog setTitleIcon(Drawable titleIcon){
      
      this.titleIcon = titleIcon;
      return this;
   }
   
   @Override
   public void onSelectDialogItem(int index){
      
      final long delay = 300L;
      Worker.onMain(() -> {
         
         dialog.dismiss();
         
         if(listener != null) listener.onSelectDialogItem(index);
         
         dialog = null;
         
      }, delay);
      
   }
   
   public static class Builder extends SelectDialog{
      
      public Builder(Activity activity){
         
         super(activity);
      }
   }
   
}
