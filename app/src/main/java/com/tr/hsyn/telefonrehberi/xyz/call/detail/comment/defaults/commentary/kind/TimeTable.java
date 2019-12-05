package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;


/**
 * <h1>TimeTableCommentStore</h1>
 * Zamansal yorumlar için gereken metot bildirimleri.
 * 
 * @author hsyn
 */
public interface TimeTable extends IComment{

   CharSequence getLastCallDateComment(long lastDate, View.OnClickListener listener, String callType, boolean isSame);
   
   CharSequence getFirstCallDateComment(long firstDate, View.OnClickListener listener, String callType, boolean isSame);
   
   default CharSequence getFirstAndLastCallDateIntervalComment(long lastDate, long firstDate){
      
      return new Spanner("İlk ve son kayıt arasında geçen zaman tam olarak ")
            .append(Stringx.format("%s. ", Time.getDuration(lastDate - firstDate)), Spans.bold());
   }
   
   default CharSequence sayAllCallsInSameDay(){
      
      return new Spanner("Bu kişiye ait tüm kayıtlar aynı güne ait. ");
   }
   
   default CharSequence sayThisIsFirstCall(){
      
      return new Spanner("Bu kayıt, bu kişiye ait ilk arama kaydı. ");
   }
   
   default CharSequence sayThisIsLastCall(){
      
      return new Spanner("\n\nBu kayıt, bu kişiye ait en son arama kaydı. ");
   }
   
}
