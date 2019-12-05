package com.tr.hsyn.telefonrehberi.util.spanner;


import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import androidx.annotation.NonNull;


public class Spanner extends SpannableStringBuilder implements ISpanner{
   
   public Spanner(){
      
      super("");
   }
   
   public Spanner(CharSequence text){
      
      super(text);
   }
   
   
   @NonNull
   @Override
   public Spanner append(CharSequence text){
      
      if(text == null){
         
         return this;
      }
      
      super.append(text);
      
      return this;
   }
   
   @NonNull
   @Override
   public Spanner append(CharSequence text, int start, int end){
      
      super.append(text, start, end);
      
      return this;
   }
   
   @NonNull
   @Override
   public Spanner append(char text){
      
      super.append(text);
      return this;
   }
   
   @Override
   public Spanner insert(int where, CharSequence tb){
      
      if(tb == null){
         return this;
      }
      
      super.insert(where, tb);
      return this;
   }
   @Override
   public Spanner append(CharSequence text, Object what, int flags){
      
      if(text == null){
         return this;
      }
      
      super.append(text, what, flags);
      return this;
   }
   
   @Override
   public Spanner replace(int start, int end, CharSequence tb, int tbstart, int tbend){
      
      if(tb == null){
         return this;
      }
      
      super.replace(start, end, tb, tbstart, tbend);
      return this;
   }
   
   @Override
   public Spanner replace(int start, int end, CharSequence tb){
      
      if(tb == null){
         return this;
      }
      
      super.replace(start, end, tb);
      return this;
   }
   
   @Override
   public SpannableStringBuilder delete(int start, int end){
      
      super.delete(start, end);
      return this;
   }
   
   public Spanner append(CharSequence text, Span... args){
      
      if(text == null){
         
         return this;
      }
      
      int start = length();
      
      append(text);
      
      for(Span span : args){
         
         this.setSpan(span.buildSpan(), start, length(), 0);
      }
      
      return this;
   }
   
   public Spanner append(ImageSpan span){
      
      int start = length();
      
      
      append(" ");
      
      setSpan(span.buildSpan(), start, length(), 0);
      
      return this;
   }
   
   public Spanner span(CharSequence text, Span... spans){
      
      span(0, text, spans);
      return this;
   }
   
   public Spanner span(int start, CharSequence text, Span... spans){
      
      if(text.toString().trim().isEmpty()){
         
         setSpans(0, length(), spans);
         
         return this;
      }
      
      int last = start - 1;
      
      while(true){
         
         last = TextUtils.indexOf(this, text, last + 1);
         
         if(last == -1){
            
            break;
         }
         
         for(Span span : spans){
            
            setSpans(last, last + text.length(), span);
         }
      }
      
      return this;
   }
   
   public Spanner setSpans(int start, int end, Span... spans){
      
      for(Span span : spans){
         
         setSpan(span.buildSpan(), start, end, 0);
      }
      
      return this;
   }
   
   public Spanner replace(int start, int end, CharSequence text, Span... spans){
      
      if(text == null){
         text = "";
      }
      
      super.replace(start, end, text);
      
      setSpans(start, start + text.length(), spans);
      return this;
   }
   
   public Spanner replace(CharSequence search, CharSequence replace, Span... spans){
      
      int start;
      
      while(true){
         
         start = TextUtils.indexOf(this, search);
         
         if(start == -1){ break; }
         
         replace(start, start + search.length(), replace, spans);
      }
      
      return this;
   }
   
   public Spanner insert(int where, CharSequence text, Span... spans){
   
      super.insert(where, text);
   
      setSpans(where, where + text.length(), spans);
      
      return this;
   }
   
   
}
























