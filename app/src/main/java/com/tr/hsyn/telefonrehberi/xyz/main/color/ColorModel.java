package com.tr.hsyn.telefonrehberi.xyz.main.color;

public class ColorModel{
   
   private int    color;
   private String name;
   
   public ColorModel(int color, String name){
      
      this.color = color;
      this.name  = name;
   }
   
   public int getColor(){
      
      return color;
   }
   
   public void setColor(int color){
      
      this.color = color;
   }
   
   public String getName(){
      
      return name;
   }
   
   public void setName(String name){
      
      this.name = name;
   }
   
}
