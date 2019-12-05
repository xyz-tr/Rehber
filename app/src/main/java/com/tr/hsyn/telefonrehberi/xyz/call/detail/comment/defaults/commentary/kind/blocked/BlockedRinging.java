package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.blocked;

import android.view.View;

import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing;


public interface BlockedRinging extends Ringing{
   
   
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
