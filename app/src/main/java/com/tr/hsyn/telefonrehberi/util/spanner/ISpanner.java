package com.tr.hsyn.telefonrehberi.util.spanner;

public interface ISpanner{
   
   ISpanner append(CharSequence text);
   
   ISpanner append(CharSequence text, int start, int end);
   
   ISpanner append(char text);
   
   ISpanner append(CharSequence text, Span... args);
   
   ISpanner append(ImageSpan span);
}
