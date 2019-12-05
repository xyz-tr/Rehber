package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.RelativeLayoutx;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.CallFilterAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogCloseListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnFilterItemClickListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ScreenSizeController;

import io.paperdb.Paper;
import timber.log.Timber;


/**
 * <h1>CallFilterDialog</h1>
 * 
 * <p>
 *    Arama kayıtlarını filtrelemek için dialog.
 *    
 * 
 * @author hsyn 2019-12-03 14:18:00
 */
public class CallFilterDialog{
   
   private       AlertDialog               alertDialog;
   private       OnFilterItemClickListener listener;
   private final int                       index;
   
   @SuppressLint("InflateParams")
   public CallFilterDialog(Activity activity, int index){
      
      if(index > 3) index--;
      
      this.index = index;
      String[]     filters      = activity.getResources().getStringArray(R.array.call_filter_items);
      View         view         = activity.getLayoutInflater().inflate(R.layout.call_filter_dialog, null, false);
      RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
      
      recyclerView.setLayoutManager(new LinearLayoutManager(activity));
      recyclerView.setAdapter(new CallFilterAdapter(filters, index).setClickListener(this::onFilterItemClicked));
      
      alertDialog = new AlertDialog.Builder(activity).create();
      alertDialog.setCancelable(true);
      alertDialog.setView(view);
      
      //noinspection ConstantConditions
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      alertDialog.show();
      
      
      setSizeAndColor(activity, view);
   }
   
   private static void setSizeAndColor(Activity activity, View view){
      
      View header = view.findViewById(R.id.header);
      header.setBackgroundColor(u.getPrimaryColor(activity));
      
      int w, h;
      
      w = Paper.book(ScreenSizeController.BOOK).read(ScreenSizeController.KEY_SCREEN_WIDTH, 0);
      h = Paper.book(ScreenSizeController.BOOK).read(ScreenSizeController.KEY_SCREEN_HEIGHT, 0);
      
      if(w == 0 || h == 0){
         
         Timber.w("w == 0 || h == 0");
         return;
      }
      
      Timber.d("%d x %d", w, h);
      
      RelativeLayoutx root = (RelativeLayoutx) view;
      final int       gap  = 150;
      
      root.setMaxHeight(h / 2 + gap);
   }
   
   private void onFilterItemClicked(View v, int index){
      
      if(this.index == index) return;
      
      final long delay = 400L;
      
      Worker.onMain(() -> alertDialog.dismiss(), delay);
      
      if(index > 3) index++;
      
      if(listener != null) listener.onFilterItemClicked(v, index);
      
   }
   
   public final void setCloseListener(DialogCloseListener listener){
      
      alertDialog.setOnDismissListener(listener);
      alertDialog.setOnCancelListener(listener);
   }
   
   public final void setFilterListener(OnFilterItemClickListener listener){
      
      this.listener = listener;
   }
   
}
