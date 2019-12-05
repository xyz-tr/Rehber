package com.tr.hsyn.telefonrehberi.util.spanner;

import android.text.Layout;
import android.text.style.AlignmentSpan;


public class AlignmentSpanBuilder implements SpanBuilder{
   
   private Layout.Alignment alignment;
   
   public AlignmentSpanBuilder(Layout.Alignment alignment){
      
      this.alignment = alignment;
   }
   
   @Override
   public Object build(){
      
      return new AlignmentSpan.Standard(alignment);
   }
   
}
