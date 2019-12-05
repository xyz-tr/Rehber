package com.tr.hsyn.telefonrehberi.xyz.main.page;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import timber.log.Timber;


@SuppressWarnings("WeakerAccess")
public final class ShowTimer{
   
   @Getter @Setter private ShowMe[] showMes;
   
   public ShowTimer(){}
   
   public ShowTimer(ShowMe[] showMes){
      
      this.showMes = showMes;
   }
   
   public void showTime(int index){
   
      if(showMes == null){
   
         Timber.w("Göstericiler tanımlı değil");
         return;
      }
      
      for(int i = 0; i < showMes.length; i++) {
         
         val me = showMes[i];
         
         if(me != null) me.showTime(i == index);
      }
   }
}
