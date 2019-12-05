package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type;

import android.app.Activity;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.base.MissedCallCommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;

import java.lang.ref.WeakReference;
import java.util.List;

import lombok.val;
import timber.log.Timber;


/**
 * <h1>DefaultMissedCallCommentator</h1>
 *
 * <p>
 * Cevapsız aramaları yorumlayacak varsayılan yorumcu.
 * </p>
 *
 * @author hsyn
 * @date 16-04-2019 22:26:16
 * @see Commentator
 */
public class Missed extends MissedCallCommentator{
   
   public Missed(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      super(activityWeakReference, commentStore);
   }
   
   @Override
   public CharSequence commentateRinging(){
      
      Spanner comment = new Spanner().append(super.commentateRinging());
      
      if(getRingingDuration() != 0L){
         
         comment.append(whyDidntAnswer());
      }
      
      return comment;
   }
   
   private CharSequence whyDidntAnswer(){
      
      //Cevap verilen aramaların, ne kadar sürede cevaplandığını bul
      List<ICall> ringingIncommingCalls = getCallStory().getRingingCalls(Type.INCOMMING);
      
      Spanner com = new Spanner();
      
      if(ringingIncommingCalls.size() < COMPARE_SIZE){
         
         Timber.d("Gelen aramaların çalma süreleri için yeterli kayıt yok. Kayıt sayısı : %d", ringingIncommingCalls.size());
         return com;
      }
      
      double average = CallStory.getRingingAverage(ringingIncommingCalls);
      
      final double oneSec = 1000.;
      com.append(Stringx.format("Gelen aramalara cevap verme hızın ise ortalama %.2f saniye. ", average / oneSec));
      
      boolean     bx      = getRingingDuration() > average;
      List<ICall> inCalls = getHistory().getCalls(cx -> cx.getType() == Type.INCOMMING || cx.getType() == Type.MISSED);
      
      val inMiss = Stream.of(inCalls)
                         .collect(
                               Collectors.partitioningBy(cx -> cx.getType() == Type.INCOMMING,
                                                         Collectors.counting()));
      
      Timber.d(inMiss.toString());
      
      
      if(bx){
         
         com.append("Bu ortalamaya göre bu aramaya bilerek cevap vermediğin düşünülebilir. ");
      }
      else{
         
         com.append("Bu ortalamaya göre bu aramanın cevapsız olması normal görünüyor. ");
      }
      
      return com;
   }
   
   
}
