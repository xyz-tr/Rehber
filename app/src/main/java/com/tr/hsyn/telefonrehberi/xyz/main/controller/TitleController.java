package com.tr.hsyn.telefonrehberi.xyz.main.controller;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.util.ui.htext.base.HTextView;

import lombok.RequiredArgsConstructor;
import lombok.Setter;


@RequiredArgsConstructor
public class TitleController{
   
   @NonNull private final HTextView title;
   private final          HTextView subTitle;
   @Setter private        boolean   withAnimation = true;
   
   
   public void setTitle(@NonNull Object title, @NonNull Object subTitle){
      
      changeText(this.title, title.toString());
      changeText(this.subTitle, subTitle.toString());
   }
   
   private void changeText(HTextView textView, String title){
      
      if(withAnimation){
       
         textView.animateText(title);
      }
      else{
   
         textView.setText(title);
      }
   }
   
   public void setTitle(@NonNull Object title){
      
      changeText(this.title, title.toString());
   }
   
   public void setSubTitle(@NonNull Object subTitle){
      
      changeText(this.subTitle, subTitle.toString());
   }
   
}
