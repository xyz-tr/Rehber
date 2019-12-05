package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.incomming;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1></h1>
 * 
 * @author hsyn 2019-11-28 15:37:23
 */
public interface IncommingRinging extends Ringing{
   
   @Override
   default CharSequence getMostRingingCommentInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      String most = isMost ? "en uzun" : "en kısa";
      
      return new Spanner("Bu çağrı, tüm gelen aramalar içindeki ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" çağrı%s. ", count == 1 ? "" : "lardan biri"));
   }
   
   @Override
   default CharSequence getMostRingingCommentForPersonInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      String most = isMost ? "en uzun" : "en kısa";
      
      return new Spanner("Bu çağrı, bu kişiden gelen aramalar içindeki ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" çağrı%s. ", count == 1 ? "" : "lardan biri"));
   }
   
   @Override
   default CharSequence getMostRingingComment(View.OnClickListener listener, boolean isMost, int count){
      
      String most = isMost ? "en uzun" : "en kısa";
      
      return new Spanner("Bu çalma süresi, tüm kayıtlar içindeki ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" çalma süre%s. ", count == 1 ? "si" : "lerinden biri"));
   }
   
   @Override
   default CharSequence getRingingAverage(long ringingDuration, double average){
      
      boolean isLong = ((double) ringingDuration / 1000) > average;
      //final int oneMinute = 60;// :)
      
      return new Spanner("Gelen aramalarda telefonun çalma sürelerinin ortalaması ")
            .append(Stringx.format("%.1f saniye", average), Spans.bold())
            .append(Stringx.format(". Bu ortalamaya göre bu gelen aramada telefonun %s çaldığı söylenebilir. ", isLong ? "uzun" : "kısa"));
      
   }
}
