package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.rejected;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Count;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>RejectedCountCommentStore</h1>
 * 
 * 
 * @author hsyn 2019-11-28 16:24:05
 */
public interface RejectedCount extends Count{
   
   @Override
   default CharSequence thisIsOnlyOneCallComment(boolean isLog){
      
      if(isLog){
         
         return new Spanner("Reddettiğin bu arama kaydı, bu kişiye ait tek arama kaydı. ");
      }
      
      return new Spanner("Bu kayıt bu kişiyi reddettiğin tek arama kaydı. ");
   }
   
   @Override
   default String thisIsOnlyOneCallDialogTitle(){
      
      return "Bir Kez Reddedilenler";
   }
   
   @Override
   default CharSequence getTotalCallComment(View.OnClickListener listener, int size){
      
      return new Spanner("Bu kayıtla birlikte bu kişiye ait ")
            .append(Stringx.format("%d reddedilen arama", size), getLinkStyles(listener))
            .append(" bulunuyor. ");
   }
   
   @Override
   default CharSequence getNoOneTimeCallComment(String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sadece bir kez reddedilen başka bir kişi yok. ");
   }
   
   @Override
   default CharSequence getOneTimeCallComment(View.OnClickListener listener, int count, String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sadece bir kez reddettiğin ")
            .append(Stringx.format("%d kişi daha", count), getLinkStyles(listener))
            .append(" var. ");
   }
   
}
