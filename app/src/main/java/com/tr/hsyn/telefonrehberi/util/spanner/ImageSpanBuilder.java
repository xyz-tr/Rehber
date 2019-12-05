package com.tr.hsyn.telefonrehberi.util.spanner;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.style.DrawableMarginSpan;
import android.text.style.IconMarginSpan;


public class ImageSpanBuilder implements SpanBuilder{
   
   private Drawable drawable;
   private Bitmap   bitmap;
   private Integer  pad;
   
   public ImageSpanBuilder(Drawable drawable, Bitmap bitmap, Integer pad){
      
      this.drawable = drawable;
      this.bitmap   = bitmap;
      this.pad      = pad;
   }
   
   
   @Override
   public Object build(){
   
      if(drawable != null && pad != null){
         return new DrawableMarginSpan(drawable, pad);
      }
      else if(drawable != null){
         return new DrawableMarginSpan(drawable);
      }
      else if(bitmap != null && pad != null){
         return new IconMarginSpan(bitmap, pad);
      }
      else if(bitmap != null){
         return new IconMarginSpan(bitmap);
      }
      else{
         throw new RuntimeException("drawable or bitmap must be not null");
      }
   }
   
}
