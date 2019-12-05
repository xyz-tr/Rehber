package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type;

import android.app.Activity;

import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base.IncommingCallCommentator;

import java.lang.ref.WeakReference;


/**
 * <h1>DefaultIncommingCallCommentator</h1>
 *
 * <p>Gelen aramaları yorumlayacak varsayılan yorumcu.</p>
 *
 * @author hsyn
 * @version 1.0.0
 * @date 15-04-2019
 * @see Commentator
 */
public class Incomming extends IncommingCallCommentator{
   
   public Incomming(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      super(activityWeakReference, commentStore);
   }
   
   
}