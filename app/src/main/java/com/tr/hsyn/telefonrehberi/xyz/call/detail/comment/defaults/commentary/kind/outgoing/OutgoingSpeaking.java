package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking;


/**
 * <h1>OutgoingSpeakingCommentStore</h1>
 *
 * @author hsyn 2019-11-28 15:47:56
 */
public interface OutgoingSpeaking extends Speaking{
   
   
   @Override
   default CharSequence commentMostSpeakingInDirection(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, giden aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInTheLog(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, bu kişiye ait aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInDirection(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, bu kişiye ait giden aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentMostSpeakingInTheLog(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu konuşma, tüm arama kayıtları içindeki ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingInDirection(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu konuşma, giden aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInTheLog(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu konuşma, bu kişiye ait aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingForPersonInDirection(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count){
      
      return new Spanner("Bu konuşma, bu kişiye ait giden aramalar içindeki ")
            .append(commentMostSpeaking(isMost, listener))
            .append(commentSameSpeakingDurationCount(listenerPart, count));
   }
   
   @Override
   default CharSequence commentMostSpeakingInTheLog(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu konuşma, tüm arama kayıtları içindeki ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   @Override
   default CharSequence commentNoSpeaking(){
      
      return new Spanner("Konuşma olmadı. ");
   }
   
   
}
