package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.TimeTable;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1></h1>
 * 
 * 
 * @author hsyn 2019-11-28 15:58:40
 */
public interface OutgoingTimeTable extends TimeTable{
   
   
   @Override
   default CharSequence getFirstCallDateComment(long lastDate, View.OnClickListener listener, String callType, boolean isSame){
      
      Spanner com = new Spanner();
      
      if(isSame){
         
         com.append("İlk kez", getLinkStyles(listener))
            .append(Stringx.format(" %s aradın. ", Time.getDateHistory(lastDate)));
      }
      else{
         
         com.append("İlk kayıt", getLinkStyles(listener))
            .append(Stringx.format(" ise %s gerçekleşen bir %s. ", Time.getDateHistory(lastDate), callType));
      }
      
      return com;
   }
   
   
   @Override
   default CharSequence getLastCallDateComment(long lastDate, View.OnClickListener listener, String callType, boolean isSame){
      
      Spanner com = new Spanner();
      
      if(isSame){
         
         com.append("En son", getLinkStyles(listener))
            .append(Stringx.format(" %s aradın. ", Time.getDateHistory(lastDate)));
      }
      else{
         
         com.append("Son kayıt", getLinkStyles(listener))
            .append(Stringx.format(" ise %s gerçekleşen bir %s. ", Time.getDateHistory(lastDate), callType));
      }
      
      return com;
   }
   
}
