package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Span;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;


/**
 * @author hsyn
 */
public enum Style{
   ;
   
   /**
    * Yorumlardaki zaman bildiriminin yapılacağı yazı stili.
    *
    * @return Yazı stili
    */
   public static Span[] getDurationStyles(){
      
      return new Span[]{Spans.bold()};
   }
   
   /**
    * Yorumlarda verilen linkler için yazı stili
    *
    * @param listener Linke tıklandığında çalışacak olan dinleyici
    * @return Yazı stili
    */
   public static Span[] getLinkStyles(View.OnClickListener listener){
      
      if(listener == null) return new Span[]{Spans.bold()};
      
      return new Span[]{Spans.underline(), Spans.click(listener)};
   }
   
   public static Span[] getLinkStyles(View.OnClickListener listener, boolean isBold){
      
      if(listener == null) return new Span[]{Spans.bold()};
   
      if(isBold){
   
         return new Span[]{Spans.underline(), Spans.bold(), Spans.click(listener)};
      }
      else{
   
         return new Span[]{Spans.underline(), Spans.click(listener)};
      }
   }
   
}
