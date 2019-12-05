package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Relation;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.val;

//@off
/**
 * <h1>RelationCommentator</h1>
 * 
 * <p>
 *    İlişkisel yorumlar için varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:13:20
 */
//@on
public interface RelationCommentator extends Relation, ICommentator{
   
   
   /**
    * İlişkisel karşılaştırmada taban süre
    */
   long CALL_RELATION_LIMIT = TimeUnit.MINUTES.toMillis(3);
   
   @Override
   default CharSequence commentateRelation(){
      
      final long callTime     = getCall().getDate();
      final long intervalTime = callTime - CALL_RELATION_LIMIT;
      val        commentStore = getRelationCommentStore();
      
      List<ICall> relatedPhoneCalls = getCallStory().getCalls(xc -> xc.getDate() > intervalTime && xc.getDate() < callTime);
      
      if(relatedPhoneCalls.isEmpty()){
         
         return null;
      }
      
      View.OnClickListener listener = v -> {
         
         if(!isDialogOpen()) showList(
               commentStore.getRelatedCallsTitle(relatedPhoneCalls.size()),
               relatedPhoneCalls);
      };
      
      return new Spanner().append(commentStore.getRelatedCallsComment(
            relatedPhoneCalls.size(),
            listener,
            CALL_RELATION_LIMIT / TimeUnit.MINUTES.toMillis(1)));
   }
   
   default com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Relation getRelationCommentStore(){
      
      return (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Relation) getCommentStore();
   }
   
   
}
