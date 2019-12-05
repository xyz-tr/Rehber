package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Count;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>OutgoingCountCommentStore</h1>
 * 
 * 
 * @author hsyn 2019-11-28 15:54:58
 */
public interface OutgoingCount extends Count{
   
   @Override
   default CharSequence getTotalCallComment(View.OnClickListener listener, int size){
      
      return new Spanner("Bu kayıtla birlikte bu kişiye ait ")
            .append(Stringx.format("%d giden arama", size), getLinkStyles(listener))
            .append(" kaydı bulunuyor. ");
   }
   
   @Override
   default CharSequence getNoOneTimeCallComment(String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sadece bir kez aradığın başka bir kişi yok. ");
   }
   
   @Override
   default CharSequence getOneTimeCallComment(View.OnClickListener listener, int count, String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sadece bir kez aradığın ")
            .append(Stringx.format("%d kişi daha", count), getLinkStyles(listener))
            .append(" var. ");
   }
   
   @Override
   default CharSequence thisIsOnlyOneCallComment(boolean isLog){
      
      if(isLog){
         
         return new Spanner("Bu giden arama kaydı, bu kişiye ait tek kayıt. ");
      }
      
      return new Spanner("Bu arama bu kişiyi ilk ve son araman. ");
   }
   
   @Override
   default String thisIsOnlyOneCallDialogTitle(){
      
      return "Bir Kez Arananlar";
   }
   
}
