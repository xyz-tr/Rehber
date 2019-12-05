package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base;


import android.app.Activity;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.CountCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.FrequencyCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.RelationCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.TimeTableCommentator;

import java.lang.ref.WeakReference;

import lombok.val;


/**
 * <h1>BlockedCallCommentator</h1>
 * 
 * <p>
 *    Engellenen aramalar için yorumcu.
 * 
 * @author hsyn 2019-12-02 14:55:02
 */
public abstract class BlockedCallCommentator 
      extends Commentator
      implements CountCommentator, FrequencyCommentator, RelationCommentator, TimeTableCommentator{
   
   protected BlockedCallCommentator(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      super(activityWeakReference, commentStore);
   }
   
   @Override
   public CharSequence commentate(ICall subject){
      
      setCall(subject);
      
      val comment = new Spanner();
      
      comment
            .append(commentateCount())
            .append(commentateRelation())
            .append(commentateFrequency())
            .append(commentateTimeTable())
            .append(commentateTheNote());
      
      return comment;
   }
}
