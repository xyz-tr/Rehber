package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.rejected;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>RejectedResponseCommentStore</h1>
 * 
 * 
 * @author hsyn 2019-11-28 16:21:52
 */
public interface RejectedResponse extends Response{
   
   
   
   @Override
   default CharSequence getResponseDuration(long ringingDuration){
      
      return new Spanner(Stringx.format("Aramanın reddedilme süresi %.2f saniye. ", (float) ringingDuration / 1000));
   }
   
   @Override
   default CharSequence getResponseAverageComment(double average, long ringingDuration){
      
      return new Spanner("Reddettiğin aramaların, reddetme sürelerinin ortalaması ")
            .append(Stringx.format("%.1f saniye. ", average / 1000))
            .append(Stringx.format("Bu ortalamaya göre bu aramayı %s reddettiğin söylenebilir. ", ringingDuration > average ? LAZY : QUICK));
      
   }
   
   @Override
   default CharSequence getResponseAverageRankComment(View.OnClickListener listener, RankInfo rankInfo, boolean isQuick){
      
      int rank      = rankInfo.getRank();
      int rankCount = rankInfo.getRankCount();
      
      String most = Stringx.format("%s", isQuick ? QUICK : LAZY);
      
      return new Spanner("Ve bu arama ")
            .append(Stringx.format("en %s reddedilen", most), getLinkStyles(listener))
            .append(Stringx.format(" aramalar listesinde %s %s sırada yer alıyor. ",
                                   rankCount == 1 ? "tek başına" : Stringx.format(
                                         "%d arama ile birlikte",
                                         rankCount - 1), Stringx.format("%d.", rank)));
   }
   
   @Override
   default CharSequence getMostResponseRankInTheDirectionComment(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu arama gelen aramalardaki ")
            .append(Stringx.format("en %s", isMost ? QUICK : LAZY), getLinkStyles(listener))
            .append(" reddettiğin arama. ");
   }
   
   @Override
   default CharSequence getMostResponseRankInPersonComment(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu arama, bu kişiye ait gelen aramalardaki ")
            .append(Stringx.format("en %s", isMost ? QUICK : LAZY), getLinkStyles(listener))
            .append(" reddettiğin arama. ");
   }
   
   @Override
   default String getResponseRankTitle(boolean isMost){
      
      return isMost ? "En Hızlı Reddedilenler" : "En Geç Reddedilenler";
   }
}
