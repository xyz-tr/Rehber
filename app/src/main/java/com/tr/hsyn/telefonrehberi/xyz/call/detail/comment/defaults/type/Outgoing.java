package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type;

import android.app.Activity;

import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base.OutgoingCallCommentator;

import java.lang.ref.WeakReference;


/**
 * <h1>DefaultOutgoingCallCommentator</h1>
 *
 * <p>Giden aramaları yorumlayacak varsayılan yorumcu.</p>
 *
 * @author hsyn
 * @date 16-04-2019 22:25:01
 * @see Commentator
 */
public class Outgoing extends OutgoingCallCommentator{
   
   public Outgoing(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      super(activityWeakReference, commentStore);
   }
 
   
   
   
   
   
  /* @Override
   protected void commentResponse(){
   
      super.commentResponse();
      
      if(call.isSharable() && speakingDuration == 0L){
         
         commentCallDuration();
      }
   }
   
   private void commentCallDuration(){
      
      List<ICall> calls = Stream.of(callStory.getOutgoingCalls())
                                .filter(cx -> cx.isSharable() && cx.getSpeakingDuration() == 0L)
                                .toList();
      
      
      if(calls.size() < COMPARE_SIZE) return;
      
      Collections.sort(calls, ComparatorCompat.comparingLong(ICall::getRingingDuration));
      
      boolean              first                = call.equals(calls.get(0));
      boolean              last                 = call.equals(calls.get(calls.size() - 1));
      OutgoingCommentStore outgoingCommentStore = (OutgoingCommentStore) commentStore;
      final String         title                = outgoingCommentStore.getMostCallDurationTitle(last);
      
      com.append(outgoingCommentStore.getCallDuration(ringingDuration));
      
      if(last){ Collections.reverse(calls);}
      
      View.OnClickListener listener = v ->
            showList(title, calls, call);
      
      if(first || last){
         
         com.append(outgoingCommentStore.getMostOutgoingCallDurationComment(listener, last));
      }
      else{
         
         double average   = callStory.getAverage(calls, ICall::getRingingDuration);
         int    rank      = getRank(calls);
         int    rankCount = getRankCount(calls, ICall::getRingingDuration);
         
         com.append(outgoingCommentStore.getCallDurationAverageComment(average, ringingDuration));
         com.append(outgoingCommentStore.getCallDurationRankComment(listener, new Rank(rank, rankCount), ringingDuration > average));
      }
      
   }
   
   @Override
   public Spanner commentate(){
   
      commentSpeaking();
      commentResponse();
      commentCount();
      commentFrequency();
      commentRelation();
      commentTimeTable();
      commentTheNote();
      
      return comment;
   }*/
   
}
