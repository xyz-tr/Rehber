package com.tr.hsyn.telefonrehberi.util.swipe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.ResourceUtil;

import lombok.Setter;


@SuppressWarnings("WeakerAccess")
public class SwipeCallBack extends ItemTouchHelper.SimpleCallback{
   
   protected         Paint         paint   = new Paint();
   @Setter protected int           bgColor = 0xA349A4;
   private           Bitmap        icon;
   private final     SwipeListener swipeListener;
   
   public SwipeCallBack(int dragDirs, int swipeDirs, @NonNull final Context context, @NonNull final SwipeListener swipeListener){
      
      super(dragDirs, swipeDirs);
      this.swipeListener = swipeListener;
      icon               = ResourceUtil.getBitmap(context, R.drawable.delete_white);
   }
   
   @Override
   public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1){
      
      return false;
   }
   
   @Override
   public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i){
      
      swipeListener.onSwipe(viewHolder.getAdapterPosition());
   }
   
   @Override
   public void onChildDraw(
         @NonNull Canvas c,
         @NonNull RecyclerView recyclerView,
         @NonNull RecyclerView.ViewHolder viewHolder,
         float dX, float dY,
         int actionState,
         boolean isCurrentlyActive){
      
      
      View itemView = viewHolder.itemView;
      
      if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
         
         float height = (float) itemView.getBottom() - (float) itemView.getTop();
         float width  = height / 3;
         
         
         if(dX < 0){
            
            paint.setColor(bgColor);
            RectF background = new RectF((float) itemView.getRight(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
            //RectF background = new RectF((float) itemView.getRight(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
            c.drawRect(background, paint);
            
            RectF icon_dest = new RectF(
                  (float) itemView.getRight() - width * 2,
                  (float) itemView.getTop() + width,
                  (float) itemView.getRight() - width,
                  (float) itemView.getBottom() - width);
            
            c.drawBitmap(icon, null, icon_dest, paint);
         }
         
         itemView.setAlpha(1.0F - (-dX / 900.0F));
      }
      
      super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
   }
   
   @Override
   public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder){
      
      return makeMovementFlags(0, ItemTouchHelper.LEFT);
   }
}
