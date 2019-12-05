package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;


/**
 * <h1>Response</h1>
 * 
 * <p>
 *    Bu arayüz aramaları tepki verme süresi üzerine yorumlar.
 *    Bu tepki aramanın türüne göre değişir.
 *    Mesela gelen aramalar için aramaya cevap verme süresidir.
 *    Reddedilen aramalar için aramayı reddetme süresidir.
 * 
 * @author hsyn 2019-11-30 15:52:47
 */
public interface Response extends IComment{
   
    CharSequence getResponseDuration(long ringingDuration);
   
    CharSequence getResponseAverageComment(double average, long ringingDuration);
   
    CharSequence getResponseAverageRankComment(View.OnClickListener listener, RankInfo rankInfo, boolean isQuick);
   
   CharSequence getMostResponseRankInTheDirectionComment(View.OnClickListener listener, boolean isMost);
   
    CharSequence getMostResponseRankInPersonComment(View.OnClickListener listener, boolean isMost);
   
    String getResponseRankTitle(boolean isMost);
   
   
}
