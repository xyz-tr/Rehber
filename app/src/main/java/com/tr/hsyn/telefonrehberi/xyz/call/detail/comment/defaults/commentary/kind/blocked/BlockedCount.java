package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.blocked;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Count;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


public interface BlockedCount extends Count{
   
   
   @Override
   default CharSequence thisIsOnlyOneCallComment(boolean isLog){
      
      if(isLog){
         
         return new Spanner("Bu engellenen arama, bu kişiye ait tek arama kaydı. ");
      }
      
      return new Spanner("Bu engellenen arama bu kişinin engellenen tek araması. ");
   }
   
   @Override
   default String thisIsOnlyOneCallDialogTitle(){
      
      return "Bir Kez Engellenenler";
   }
   
   @Override
   default CharSequence getTotalCallComment(View.OnClickListener listener, int size){
      
      return null;
   }
   
   @Override
   default CharSequence getNoOneTimeCallComment(String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sadece bir kez engellenen başka bir kişi yok. ");
   }
   
   @Override
   default CharSequence getOneTimeCallComment(View.OnClickListener listener, int count, String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sadece bir kez engellenen ")
            .append(Stringx.format("%d kişi daha", count), getLinkStyles(listener))
            .append(" var. ");
   }
}
