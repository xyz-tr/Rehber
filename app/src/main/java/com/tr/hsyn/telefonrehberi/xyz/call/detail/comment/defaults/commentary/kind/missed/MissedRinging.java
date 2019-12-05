package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.missed;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1></h1>
 * 
 * 
 * @author hsyn 2019-11-28 16:13:26
 */
@SuppressWarnings("ClassNamePrefixedWithPackageName")
public interface MissedRinging extends Ringing{
   
   @Override
   default CharSequence getMostRingingCommentInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      String most = isMost ? "en uzun" : "en kısa";
      
      return new Spanner("Bu çağrı, tüm cevapsız çağrılar içindeki ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" çağrı%s. ", count == 1 ? "" : "lardan biri"));
   }
   
   @Override
   default CharSequence getMostRingingCommentForPersonInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      String most = isMost ? "en uzun" : "en kısa";
      
      return new Spanner("Bu çağrı, bu kişiye ait cevapsız çağrılar içindeki ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" çağrı%s. ", count == 1 ? "" : "lardan biri"));
   }
   
   @Override
   default CharSequence getMostRingingComment(View.OnClickListener listener, boolean isMost, int count){
      
      String most = isMost ? "en uzun" : "en kısa";
      
      return new Spanner("Bu çağrı, tüm kayıtlar içindeki ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" çağrı%s. ", count == 1 ? "" : "lardan biri"));
   }
   
   @Override
   default CharSequence getRingingAverage(long ringingDuration, double average){
      
      boolean isLong = ((double) ringingDuration / 1000) > average;
      //final int oneMinute = 60;// :)
      
      return new Spanner("Cevapsız çağrılarda telefonun çalma sürelerinin ortalaması ")
            .append(Stringx.format("%.1f saniye", average), Spans.bold())
            .append(Stringx.format(". Bu ortalamaya göre bu çağrının %s bir çağrı olduğu söylenebilir. ", isLong ? "uzun" : "kısa"));
      
   }
   
}
