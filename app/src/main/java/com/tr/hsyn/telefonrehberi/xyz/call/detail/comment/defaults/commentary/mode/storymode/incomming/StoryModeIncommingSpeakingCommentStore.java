package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.storymode.incomming;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.incomming.IncommingSpeaking;


public interface StoryModeIncommingSpeakingCommentStore extends IncommingSpeaking{
   
   @Override
   default CharSequence commentSpeakingDuration(long speakingDuration){
      
      final long sec = 1000L;
      
      return new Spanner()
            .append(Stringx.format("%d saniye", (int) (speakingDuration / sec)), Spans.bold())
            .append(" konuşmuşsun. ");
   }
   
   
}
