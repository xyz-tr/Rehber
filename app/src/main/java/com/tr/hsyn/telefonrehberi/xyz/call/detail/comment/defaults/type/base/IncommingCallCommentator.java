package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base;


import android.app.Activity;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.CountCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.FrequencyCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.RelationCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.ResponseCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.RingingCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.SpeakingCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.TimeTableCommentator;

import java.lang.ref.WeakReference;

import lombok.val;


/**
 * <h1>IncommingCallCommentator</h1>
 * <p><b>Gelen arama yorumcusu.</b></p>
 * Gelen aramaları yorumlamak isteyenler için temel sınıf.
 *
 * @author hsyn 2019-11-25 12:32:32
 */
public abstract class IncommingCallCommentator extends Commentator
      implements RingingCommentator, ResponseCommentator, SpeakingCommentator, CountCommentator, FrequencyCommentator, RelationCommentator, TimeTableCommentator{
   
   protected IncommingCallCommentator(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      super(activityWeakReference, commentStore);
   }
   
   @Override
   public CharSequence commentate(ICall subject){
      
      setCall(subject);
      
      val comment = new Spanner();
      
      comment.append(commentateRinging())
             .append(commentateResponse())
             .append(commentateSpeakingDuration())
             .append(commentateMostSpeaking())
             .append(commentateCount())
             .append(commentateFrequency())
             .append(commentateRelation())
             .append(commentateTimeTable())
             .append(commentateTheNote());
      
      return comment;
   }
   
   
}
























