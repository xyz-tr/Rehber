package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.rejected;

import android.view.View;

import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing;


/**
 * <h1></h1>
 * 
 * 
 * @author hsyn 2019-11-28 16:25:21
 */
public interface RejectedRinging extends Ringing{
   
   @Override
   default CharSequence getMostRingingCommentInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      return null;
   }
   
   @Override
   default CharSequence getMostRingingCommentForPersonInDirection(View.OnClickListener listener, boolean isMost, int count){
      
      return null;
   }
   
   @Override
   default CharSequence getMostRingingComment(View.OnClickListener listener, boolean isMost, int count){
      
      return null;
   }
   
   @Override
   default CharSequence getRingingAverage(long ringingDuration, double average){
      
      return null;
   }
}
