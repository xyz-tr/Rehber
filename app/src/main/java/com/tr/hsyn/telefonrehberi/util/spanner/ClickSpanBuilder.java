package com.tr.hsyn.telefonrehberi.util.spanner;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;

import lombok.Setter;


public class ClickSpanBuilder implements SpanBuilder{
   
   private View.OnClickListener listener;
   
   ClickSpanBuilder(View.OnClickListener listener){
      
      this.listener = listener;
   }
   
   @Override
   public Object build(){
      
      return new EasyClickableSpan(listener);
   }
   
   
   private static class EasyClickableSpan extends ClickableSpan{
      
     @Setter private View.OnClickListener listener;
   
      @Override
      public void updateDrawState(@NonNull TextPaint ds){
   
         super.updateDrawState(ds);
         ds.setColor(Color.WHITE);
         //
      }
   
      EasyClickableSpan(View.OnClickListener listener){
         
         this.listener = listener;
      }
      
      @Override
      public void onClick(@NonNull View widget){
         
         if(listener != null) listener.onClick(widget);
      }
      
   }
   
}
