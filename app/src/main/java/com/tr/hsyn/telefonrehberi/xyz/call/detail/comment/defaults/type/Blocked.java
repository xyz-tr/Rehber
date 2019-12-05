package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type;

import android.app.Activity;

import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base.BlockedCallCommentator;

import java.lang.ref.WeakReference;


/**
 * <h1>DefaultBlockedCommentStore</h1>
 *
 * <p>Engellenen aramaları yorumlayacak varsayılan yorumcu.</p>
 *
 * @author hsyn
 * @see Commentator
 */
public class Blocked extends BlockedCallCommentator{
   
   public Blocked(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      super(activityWeakReference, commentStore);
      
   }
   
   
}
