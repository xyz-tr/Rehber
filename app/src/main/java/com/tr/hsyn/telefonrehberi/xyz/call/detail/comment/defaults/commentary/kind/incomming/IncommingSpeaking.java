package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.incomming;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking;


public interface IncommingSpeaking extends Speaking{
   
   @Override
   default CharSequence commentMostSpeakingInDirection(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, gelen aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInTheLog(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, bu kişiye ait aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInDirection(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, bu kişiye ait gelen aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentMostSpeakingInTheLog(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu konuşma, tüm kayıtlar içinde ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingInDirection(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner(Stringx.format("Bu konuşma, gelen aramalar içindeki "))
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInTheLog(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu arama, bu kişiye ait aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInDirection(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu konuşma, bu kişiye ait gelen aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   
   
   
   
   
   
}
