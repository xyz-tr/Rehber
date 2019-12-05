package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.missed;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Count;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1></h1>
 * 
 * 
 * @author hsyn 2019-11-28 16:15:04
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public interface MissedCount extends Count{
   
   @Override
   default CharSequence thisIsOnlyOneCallComment(boolean isLog){
      
      if(isLog){
         
         return new Spanner("Bu cevapsız çağrı, bu kişiye ait tek arama kaydı. ");
      }
      
      return new Spanner("Bu çağrı bu kişinin tek cevapsız çağrısı. ");
   }
   
   @Override
   default String thisIsOnlyOneCallDialogTitle(){
      
      return "Bir Kez Çağrı Atanlar";
   }
   
   @Override
   default CharSequence getTotalCallComment(View.OnClickListener listener, int size){
      
      return new Spanner("Bu kayıtla birlikte bu kişiye ait ")
            .append(Stringx.format("%d cevapsız arama", size), Style.getLinkStyles(listener))
            .append(" kaydı bulunuyor. ");
   }
   
   @Override
   default CharSequence getNoOneTimeCallComment(String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sana sadece bir kez çağrı atan başka bir kişi yok. ");
   }
   
   @Override
   default CharSequence getOneTimeCallComment(View.OnClickListener listener, int count, String name){
      
      name = !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi";
      
      return new Spanner()
            .append(Stringx.format("%s", name), Spans.bold())
            .append(" haricinde sana sadece bir kez çağrı atan ")
            .append(Stringx.format("%d kişi daha", count), getLinkStyles(listener))
            .append(" var. ");
   }
   
}
