package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing;


import android.view.View;

import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Frequency;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Relation;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style;


/**
 * @author hsyn 2019-11-27 22:25:47
 */
public interface IOutgoing extends
      OutgoingSpeaking,
      OutgoingResponse,
      OutgoingCount,
      OutgoingTimeTable,
      OutgoingRinging,
      Frequency,
      Relation{
   
   CharSequence getMostOutgoingCallDurationComment(View.OnClickListener listener, boolean isLongest);
   
   CharSequence getCallDurationAverageComment(double average, long ringingDuration);
   
   CharSequence getCallDurationRankComment(View.OnClickListener listener, RankInfo rankInfo, boolean isLongest);
   
   default String getMostCallDurationTitle(boolean isLongest){
      
      return isLongest ? "En Uzun Çağrılar" : "En Kısa Çağrılar";
   }
   
   default CharSequence getCallDuration(long ringingDuration){
      
      return new Spanner("Çağrı süresi ")
            .append(Stringx.format("%.2f saniye. ", (float) ringingDuration / 1000), Style.getDurationStyles());
   
   
   }
  
}
