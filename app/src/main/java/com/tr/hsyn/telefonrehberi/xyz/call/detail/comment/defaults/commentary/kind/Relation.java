package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>Relation</h1>
 * 
 * Bu arayüz ilişkisel yorumlar için sağlanması gereken yorumları bildirir.
 * 
 * 
 * @author hsyn 2019-11-27 23:16:02
 */
public interface Relation extends IComment{
   
   default CharSequence getRelatedCallsComment(int size, View.OnClickListener listener, long minuteInterval){
      
      return new Spanner(Stringx.format("Bu arama kaydı, aramadan önceki %d dakikalık süre içerisinde gerçekleşen ", minuteInterval))
            .append(Stringx.format("%d arama kaydı", size), getLinkStyles(listener))
            .append(" ile bağlantılı gibi görünüyor. ");
   }
   
   default String getRelatedCallsTitle(int size){
      
      return Stringx.format("Bağlantılı %d Arama Kaydı", size);
   }
}
