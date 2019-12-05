package com.tr.hsyn.telefonrehberi.util.spanner;

import android.text.style.BulletSpan;


public class BulletSpanBuilder implements  SpanBuilder{
   
   private Integer gapWidth;
   private Integer color;
   
   public BulletSpanBuilder(Integer gapWidth, Integer color){
      
      this.gapWidth = gapWidth;
      this.color    = color;
   }
   
   @Override
   public Object build(){
   
      if(gapWidth != null && color != null){
         return new BulletSpan(gapWidth, color);
      }
      
      if(gapWidth != null){
         return new BulletSpan(gapWidth);
      }
   
      return new BulletSpan();
   }
   
}
