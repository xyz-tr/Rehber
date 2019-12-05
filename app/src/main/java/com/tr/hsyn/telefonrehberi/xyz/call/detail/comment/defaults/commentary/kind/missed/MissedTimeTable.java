package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.missed;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.TimeTable;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style;


/**
 * <h1>MissedTimeTableCommentStore</h1>
 * 
 * 
 * @author hsyn 2019-11-28 16:16:56
 */
public interface MissedTimeTable extends TimeTable{
   
   @Override
   default CharSequence getFirstCallDateComment(long lastDate, View.OnClickListener listener, String callType, boolean isSame){
      
      Spanner com = new Spanner();
      
      if(isSame){
         
         com.append("İlk kez", Style.getLinkStyles(listener))
            .append(Stringx.format(" %s çağrı attı. ", Time.getDateHistory(lastDate)));
      }
      else{
         
         com.append("İlk kayıt", Style.getLinkStyles(listener))
            .append(Stringx.format(" ise %s gerçekleşen bir %s. ", Time.getDateHistory(lastDate), callType));
      }
      
      return com;
   }
   
   @Override
   default CharSequence getLastCallDateComment(long lastDate, View.OnClickListener listener, String callType, boolean isSame){
      
      Spanner com = new Spanner();
      
      if(isSame){
         
         com.append("En son", Style.getLinkStyles(listener))
            .append(Stringx.format(" %s çağrı attı. ", Time.getDateHistory(lastDate)));
      }
      else{
         
         com.append("Son kayıt", Style.getLinkStyles(listener))
            .append(Stringx.format(" ise %s gerçekleşen bir %s. ", Time.getDateHistory(lastDate), callType));
      }
      
      return com;
   }
   
}
