package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.incomming;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>IncommingResponseCommentStore</h1>
 * 
 * @author hsyn 2019-11-28 15:33:36
 */
public interface IncommingResponse extends Response{
   
   @Override
   default CharSequence getResponseDuration(long ringingDuration){
      
      return new Spanner(Stringx.format("Bu süre aynı zamanda aramayı cevaplama süren. "));
   }
   
   @Override
   default CharSequence getResponseAverageComment(double average, long ringingDuration){
      
      boolean      isQuick = ringingDuration < average;
      final double sec     = 1000.;
      
      return new Spanner("Gelen aramalara cevap verme sürelerinin ortalaması ")
            .append(Stringx.format("%.1f saniye. ", average / sec), Spans.bold())
            .append(Stringx.format("Bu ortalamaya göre bu aramayı %s cevapladığın söylenebilir. ", isQuick ? QUICK : LAZY));
   }
   
   @Override
   default CharSequence getResponseAverageRankComment(View.OnClickListener listener, RankInfo rankInfo, boolean isQuick){
      
      int rank      = rankInfo.getRank();
      int rankCount = rankInfo.getRankCount();
      
      String most = Stringx.format("%s", isQuick ? "çabuk" : "geç");
      
      return new Spanner("Ve bu arama ")
            .append(Stringx.format("en %s cevap verdiğin", most), getLinkStyles(listener))
            .append(Stringx.format(" aramalar listesinde %s %s sırada yer alıyor. ", rankCount == 1 ? "tek başına" : Stringx.format("%d arama ile birlikte", rankCount - 1), Stringx.format("%d.", rank)));
   }
   
   @Override
   default CharSequence getMostResponseRankInTheDirectionComment(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu arama, gelen aramalar içinde ")
            .append(Stringx.format("en %s", isMost ? QUICK : LAZY), getLinkStyles(listener))
            .append(Stringx.format(" cevapladığın arama %s. ", isMost ? STAR : ""));
   }
   
   @Override
   default CharSequence getMostResponseRankInPersonComment(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu arama, bu kişinin ")
            .append(Stringx.format("en %s", isMost ? QUICK : LAZY), getLinkStyles(listener))
            .append(Stringx.format(" cevapladığın araması%s. ", isMost ? STAR : ""));
   }
   
   @Override
   default String getResponseRankTitle(boolean isMost){
      
      return isMost ? "En Hızlı Cevaplananlar" : "En Geç Cevaplananlar";
   }
}
