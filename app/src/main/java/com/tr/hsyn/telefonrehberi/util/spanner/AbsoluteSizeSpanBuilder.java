package com.tr.hsyn.telefonrehberi.util.spanner;

import android.text.style.AbsoluteSizeSpan;


public class AbsoluteSizeSpanBuilder implements SpanBuilder{
   
   private int size;
   private boolean dip;
   
   public AbsoluteSizeSpanBuilder(int size, boolean dip){
      
      this.size = size;
      this.dip  = dip;
   }
   
   @Override
   public Object build(){
      
      return new AbsoluteSizeSpan(size, dip);
   }
   
}
