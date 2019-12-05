package com.tr.hsyn.telefonrehberi.util.spanner;

public class Span{
   
   private SpanBuilder builder;
   
   Span(SpanBuilder builder){
      
      this.builder = builder;
   }
   
   Object buildSpan(){
      
      return builder.build();
   }
}
