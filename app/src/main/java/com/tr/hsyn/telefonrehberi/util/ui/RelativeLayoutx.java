package com.tr.hsyn.telefonrehberi.util.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.tr.hsyn.telefonrehberi.R;


public class RelativeLayoutx extends RelativeLayout{
   
   
   
   
   private int maxHeight;
   
   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
      
      heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
   }
   
   private void init(Context context, AttributeSet attrs){
   
      if(attrs != null){
         
         TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.RelativeLayoutx);
         maxHeight = styledAttrs.getDimensionPixelSize(R.styleable.RelativeLayoutx_maxHeightx, 0);
         
         styledAttrs.recycle();
      }
   }
   
   public RelativeLayoutx(Context context){
      
      super(context);
   }
   
   public RelativeLayoutx(Context context, AttributeSet attrs){
      
      super(context, attrs);
   
      if(attrs != null){
         
         init(context, attrs);
      }
      
   }
   
   public RelativeLayoutx(Context context, AttributeSet attrs, int defStyleAttr){
      
      super(context, attrs, defStyleAttr);
   
      if(attrs != null){
      
         init(context, attrs);
      }
   }
   
   public RelativeLayoutx(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes){
      
      super(context, attrs, defStyleAttr, defStyleRes);
   
      if(attrs != null){
      
         init(context, attrs);
      }
   }
   
   
   public void setMaxHeight(int maxHeight){
      
      this.maxHeight = maxHeight;
      invalidate();
   }
}
