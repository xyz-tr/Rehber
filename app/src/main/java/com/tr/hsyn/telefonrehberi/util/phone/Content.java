package com.tr.hsyn.telefonrehberi.util.phone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;



@ToString
@EqualsAndHashCode
@Root(name = "content")
public class Content implements IContent{
   
   @Getter @Nullable @Element(name = "title", required = false)
   private final String title;
   
   @Getter @NonNull @Element(name = "text") 
   private final String body;
   
   Content(@Nullable String title, @NonNull String body){
      
      this.title = title;
      this.body  = body;
   }
   
   
}
