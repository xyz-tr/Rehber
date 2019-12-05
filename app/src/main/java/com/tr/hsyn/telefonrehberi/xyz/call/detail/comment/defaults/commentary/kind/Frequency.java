package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;

import com.tr.hsyn.telefonrehberi.util.NameExtention;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;

import lombok.val;


/**
 * <h1>Frequency</h1>
 * 
 * Görüşme sıklığı üzerine gereken yorumları bildirir.
 * 
 * @author hsyn 2019-11-27 23:18:57
 */
public interface Frequency extends IComment{
   
   /**
    * Verilen milisaniye cinsinden zaman, bu kişi ile olan toplam görüşme geçmişinin süresidir.
    * Yani bu kişi ile bu kadar zamanlık bir görüşme geçmişi var.
    * 
    * @param estimatedTime geçen zaman
    * @return yorum
    */
   default CharSequence getEstimatedCallHistoryComment(long estimatedTime){
   
      String time       = Time.getDuration(estimatedTime);
      int    firstspace = time.indexOf(' ');
      time = time.substring(0, time.indexOf(' ', firstspace + 1));
   
      val timeHistory = Stringx.from("\n\nBu kişi ile $time bir görüşme geçmişiniz var. ")
                               .key("time", time + NameExtention.getExt(time, NameExtention.SOFT))
                               .format();
   
      return new Spanner().append(timeHistory);
   }
}
