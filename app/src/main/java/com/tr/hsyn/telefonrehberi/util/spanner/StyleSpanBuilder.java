package com.tr.hsyn.telefonrehberi.util.spanner;

import android.text.style.StyleSpan;


public class StyleSpanBuilder implements SpanBuilder{
   
   private int style;
   
   public StyleSpanBuilder(int style){
      
      this.style = style;
   }
   
   @Override
   public Object build(){
      
      return new StyleSpan(style);
   }
   
}
