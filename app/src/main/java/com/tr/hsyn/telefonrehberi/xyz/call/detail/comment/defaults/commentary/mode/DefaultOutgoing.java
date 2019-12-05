package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing.IOutgoing;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * @author hsyn
 */
public class DefaultOutgoing implements IOutgoing{
   
   
   @Override
   public CharSequence getMostOutgoingCallDurationComment(View.OnClickListener listener, boolean isLongest){
      
      return null;
   }
   
   @Override
   public final CharSequence getCallDurationAverageComment(double average, long ringingDuration){
      
      final double oneSec = 1000.;
      return new Spanner(Stringx.format("Çağrı sürelerinin ortalamasına bakılırsa (%.2fsn) bu çağrının %s bir çağrı olduğu söylenebilir. ", average / oneSec, ringingDuration < average ? "kısa" : "uzun"));
   }
   
   @Override
   public final CharSequence getCallDurationRankComment(View.OnClickListener listener, RankInfo rankInfo, boolean isLongest){
      
      int    rank      = rankInfo.getRank();
      int    rankCount = rankInfo.getRankCount();
      String most      = isLongest ? "en uzun çağrı" : "en kısa çağrı";
      
      Spanner com = new Spanner("Bu arama ")
            .append(Stringx.format("%s", most), getLinkStyles(listener));
      
      if(rankCount == 1){
         
         com.append(Stringx.format(" yaptığın aramalar listesinde %d. sırada. ", rank));
      }
      else{
         
         com.append(Stringx.format(" yaptığın aramalar listesinde %d kişi ile birlikte %d. sırada. ", rankCount - 1, rank));
      }
      
      return com;
      
   }
   
   
   
  
}
