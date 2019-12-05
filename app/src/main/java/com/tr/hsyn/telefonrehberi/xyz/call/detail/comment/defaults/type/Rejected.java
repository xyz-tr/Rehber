package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type;

import android.app.Activity;

import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base.RejectedCallCommentator;

import java.lang.ref.WeakReference;


/**
 * <h1>DefaultRejectedCallCommentator</h1>
 *
 * <p>Reddedilen aramaları yorumlayacak varsayılan yorumcu.</p>
 *
 * @author hsyn
 * @see Commentator
 */
public class Rejected extends RejectedCallCommentator{
   
   public Rejected(WeakReference<Activity> activityWeakReference, final IComment commentStore){
      
      super(activityWeakReference, commentStore);
   }
   
   
}
