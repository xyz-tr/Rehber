package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1></h1>
 * 
 * 
 * @author hsyn 2019-11-28 15:59:15
 */
public interface OutgoingRinging extends Ringing{
   
   
   @Override
   default CharSequence getMostRingingCommentInDirection(View.OnClickListener listener, boolean isMost, int count){
   
      String most = isMost ? "en uzun" : "en kısa";
   
      return new Spanner("Kayıtlara göre bu çağrı, yapılan ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(" giden çağrı. ");
   }
   
   @Override
   default CharSequence getMostRingingCommentForPersonInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      return null;
   }
   
   @Override
   default CharSequence getMostRingingComment(View.OnClickListener listener, boolean isMost, int count){
      
      return null;
   }
   
   @Override
   default CharSequence getRingingAverage(long ringingDuration, double average){
      
      return null;
   }
   
}
