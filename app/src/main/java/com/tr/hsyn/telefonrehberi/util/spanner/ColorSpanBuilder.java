package com.tr.hsyn.telefonrehberi.util.spanner;


import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;


public class ColorSpanBuilder implements SpanBuilder{
   
   final static int FOREGROUND = 0;
   final static int BACKGROUND = 1;
   
   private int type;
   private int color;
   
   public ColorSpanBuilder(int type, int color){
      
      this.type  = type;
      this.color = color;
   }
   
   @Override
   public Object build(){
   
      if(type == 0){
         
         return new ForegroundColorSpan(color);
      }
      
      return new BackgroundColorSpan(color);
   }
   
}
