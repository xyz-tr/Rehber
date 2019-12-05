package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.view.View;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Speaking;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_DIRECTION;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_PERSON;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_PERSON_IN_THE_DIRECTION;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.COMPARE_SIZE;

//@off
/**
 * <h1>SpeakingCommentator</h1>
 * 
 * <p>
 *    Konuşma süresine dair varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:21:17
 */
//@on
public interface SpeakingCommentator extends Speaking, ICommentator{
   
   @Override
   default CharSequence commentateSpeakingDuration(){
      
      CharSequence comment = null;
      
      if(getSpeakingDuration() == 0L){
         
         if(getCall().getType() == Type.OUTGOING){
            
            comment = ((com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking) getCommentStore()).commentNoSpeaking();
         }
      }
      else{
         
         com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking speakingCommentStore = (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking) getCommentStore();
         comment = speakingCommentStore.commentSpeakingDuration(getSpeakingDuration());
      }
      
      return comment;
   }
   
   @Override
   default CharSequence commentateMostSpeaking(){
      
      if(getSpeakingDuration() == 0L) return null;
      
      CharSequence spanner = null;
      List<ICall>  speakingCalls;
      
      @Commentator.RecordType int[] types = new int[]{
            
            ALL_CALLS,
            ALL_CALLS_OF_DIRECTION,
            ALL_CALLS_OF_PERSON,
            ALL_CALLS_OF_PERSON_IN_THE_DIRECTION
      };
      
      LOOP:
      for(int i = 0; i < 4; i++){
         
         @Commentator.RecordType int type = types[i];
         
         switch(type){
            
            case ALL_CALLS: //?Konuşma yapılmış tüm giden ve gelen aramalar
               
               speakingCalls = getCallStory().getSpeakingCalls();
               spanner = checkMostSpeaking(speakingCalls, ALL_CALLS);
            
            case ALL_CALLS_OF_DIRECTION://?Konuşma yapılmış giden veya gelen aramalar - türe göre
               
               if(spanner != null) break LOOP;
               
               speakingCalls = getCallStory().getSpeakingCalls(getCall().getType());
               spanner = checkMostSpeaking(speakingCalls, ALL_CALLS_OF_DIRECTION);
            
            case ALL_CALLS_OF_PERSON://?Konuşma yapılmış kişiye ait giden ve gelen aramalar
               
               if(spanner != null) break LOOP;
               
               speakingCalls = getHistory().getSpeakingCalls();
               spanner = checkMostSpeaking(speakingCalls, ALL_CALLS_OF_PERSON);
            
            case ALL_CALLS_OF_PERSON_IN_THE_DIRECTION://?Konuşma yapılmış kişiye ait giden veya gelen aramalar - türe göre
               
               if(spanner != null) break LOOP;
               
               speakingCalls = getHistory().getSpeakingCalls(getCall().getType());
               spanner = checkMostSpeaking(speakingCalls, ALL_CALLS_OF_PERSON_IN_THE_DIRECTION);
         }
      }
      
      Spanner averageComment = new Spanner().append(commentSpeakingAverage());
      
      if(spanner == null){
         
         averageComment.append(commentSpeakingDurationRank());
      }
      else{
         
         averageComment.append(spanner);
      }
      
      return averageComment;
   }
   
   default CharSequence checkMostSpeaking(List<ICall> speakingCalls, @Commentator.RecordType int type){
      
      if(speakingCalls.size() < COMPARE_SIZE) return null;
      
      Spanner spanner = null;
      
      Collections.sort(
            speakingCalls,
            ComparatorCompat.comparingInt(ICall::getDuration).reversed());
      
      View.OnClickListener listener = v -> {
         
         if(!isDialogOpen()){
            
            showList("Konuşma Süresine Göre", speakingCalls, getCall());
         }
      };
      
      
      List<ICall> samePhoneCalls;
      
      if(getCall().equals(speakingCalls.get(0))){//en uzun konuşulan
         
         spanner        = new Spanner();
         samePhoneCalls = getWithSameDurationCalls(getCall().getDuration(), speakingCalls);
         
         if(samePhoneCalls.size() == 1){//sadece kendisi
            
            spanner.append(commentMostSpeaking(type, listener, null, true, 0));
         }
         else{
            
            //Tarihe göre sırala
            Collections.sort(samePhoneCalls);
            
            View.OnClickListener listenerPart = v -> {
               
               if(!isDialogOpen()) showList("Aynı Konuşma Süresi", samePhoneCalls);
               
            };
            
            spanner.append(commentMostSpeaking(
                  type,
                  listener,
                  listenerPart,
                  true,
                  samePhoneCalls.size()));
         }
         
         return spanner;
      }
      
      if(getCall().equals(speakingCalls.get(speakingCalls.size() - 1))){
         
         spanner = new Spanner();
         Collections.reverse(speakingCalls);
         samePhoneCalls = getWithSameDurationCalls(getCall().getDuration(), speakingCalls);
         
         if(samePhoneCalls.size() == 1){
            
            spanner.append(commentMostSpeaking(type, listener, null, false, 0));
         }
         else{
            
            Collections.sort(samePhoneCalls);
            
            View.OnClickListener listenerPart = v -> {
               
               if(!isDialogOpen()) showList("Aynı Konuşma Süresi", samePhoneCalls);
            };
            
            spanner.append(commentMostSpeaking(
                  type,
                  listener,
                  listenerPart,
                  false,
                  samePhoneCalls.size()));
         }
      }
      
      return spanner;
   }
   
   static List<ICall> getWithSameDurationCalls(int speakDuration, Iterable<ICall> phoneCalls){
      
      return Stream.of(phoneCalls).filter(ccall -> ccall.getDuration() == speakDuration).toList();
   }
   
   default CharSequence commentMostSpeaking(@Commentator.RecordType int type, View.OnClickListener mostCallsTrigger, View.OnClickListener sameDurationCallsTrigger, boolean isMost, int count){
      
      com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking commentStore = getSpeakingCommentStore();
      CharSequence                                                                         com          = null;
      
      if(count == 0){
         
         //@off
         switch(type){
            
            case                            ALL_CALLS: com = commentStore.commentMostSpeakingInTheLog(mostCallsTrigger, isMost);            break;
            case ALL_CALLS_OF_PERSON_IN_THE_DIRECTION: com = commentStore.commentMostSpeakingInDirection(mostCallsTrigger, isMost);         break;
            case               ALL_CALLS_OF_DIRECTION: com = commentStore.commentMostSpeakingForPersonInTheLog(mostCallsTrigger, isMost);   break;
            case                  ALL_CALLS_OF_PERSON: com = commentStore.commentMostSpeakingForPersonInDirection(mostCallsTrigger, isMost);break;
         }
      }
      else{
         
         switch(type){
            
            case ALL_CALLS: com = commentStore.commentMostSpeakingInTheLog(
                                                                     mostCallsTrigger,
                                                                     sameDurationCallsTrigger,
                                                                     isMost,
                                                                     count); break;
                  
            case ALL_CALLS_OF_PERSON_IN_THE_DIRECTION: com = commentStore.commentMostSpeakingInDirection(
                                                                                                mostCallsTrigger,
                                                                                                sameDurationCallsTrigger,
                                                                                                isMost,
                                                                                                count); break;
                  
            case ALL_CALLS_OF_DIRECTION: com = commentStore.commentMostSpeakingForPersonInTheLog(
                                                                                          mostCallsTrigger,
                                                                                          sameDurationCallsTrigger,
                                                                                          isMost,
                                                                                          count); break;
                  
            case ALL_CALLS_OF_PERSON: com = commentStore.commentMostSpeakingForPersonInDirection(
                                                                                          mostCallsTrigger,
                                                                                          sameDurationCallsTrigger,
                                                                                          isMost,
                                                                                          count); break;
         }
      }
      
      return com;//@on
   }
   
   default com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking getSpeakingCommentStore(){
      
      return (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Speaking) getCommentStore();
   }
   
   default CharSequence commentSpeakingDurationRank(){
      
      final List<ICall> speakingPhoneCalls = getCallStory().getSpeakingCalls();
      final double      speakingAverage    = getCallStory().getSpeakingAverage(speakingPhoneCalls);
      final boolean     isMost             = getCall().getDuration() > speakingAverage;
      final String      title              = isMost ? "En Uzun Konuşmalar" : "En Kısa Konuşmalar";
      
      if(isMost){
         
         Collections.sort(
               speakingPhoneCalls,
               ComparatorCompat.comparingInt(ICall::getDuration).reversed());
      }
      else{
         
         Collections.sort(speakingPhoneCalls, ComparatorCompat.comparingInt(ICall::getDuration));
      }
      
      final int currentCallIndex = speakingPhoneCalls.indexOf(getCall());
      
      if(currentCallIndex == -1){
         
         Timber.w("Bunun olmaması gerek. Aranan arama kaydının index'i listede bulunamadı.");
         return null;
      }
      
      View.OnClickListener listener = v -> {
         
         if(!isDialogOpen()) showList(title, speakingPhoneCalls, getCall());
      };
      
      return new Spanner().append(getSpeakingCommentStore().commentSpeakingDurationRank(
            listener,
            currentCallIndex + 1,
            isMost));
   }
   
   /**
    * Yoruma konuşma süresi ortalamasını yazmak için.
    */
   default CharSequence commentSpeakingAverage(){
      
      double  average = getCallStory().getSpeakingAverage();
      Spanner comment = new Spanner();
      
      if(average >= 1.0){
         
         comment.append(getSpeakingCommentStore().commentSpeakingAverage(getSpeakingDuration(), average));
      }
      
      return comment;
   }
   
   
}
