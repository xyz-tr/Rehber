package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.view.View;

import com.annimon.stream.ComparatorCompat;
import com.google.common.base.Preconditions;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Ringing;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;

import java.util.Collections;
import java.util.List;

import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_DIRECTION;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_PERSON;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_PERSON_IN_THE_DIRECTION;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.COMPARE_SIZE;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.getRankCount;

//@off
/**
 * <h1>RingingCommentator</h1>
 * 
 * <p>
 *    Telefonun çalma süresine dair varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:18:52
 */
//@on
public interface RingingCommentator extends Ringing, ICommentator{
   
   @Override
   default CharSequence commentateRinging(){
      
      if(getRingingDuration() == 0L) return null;
      
      Preconditions.checkArgument(getCommentStore() instanceof com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing, "Bu karşılaştırmayı yorumlamak için gereken bakkal : RingingCommentStore");
      
      Spanner comment = new Spanner();
      
      //Yazılan ilk yorum değilse boşluk bırak
      if(comment.length() > 3) comment.append("\n\n");
      
      //Çalma süresini yaz
      comment.append(getRingingCommentStore().getRingingDuration(getRingingDuration()));
      
      //? Telefonun çalma süreleri kaydedilmiş tüm aramalar
      List<ICall> ringingCalls = getCallStory().getRingingCalls();
      
      if(ringingCalls.size() > COMPARE_SIZE){
         
         Collections.sort(ringingCalls, ComparatorCompat.comparingLong(ICall::getRingingDuration).reversed());
         
         int index     = getMostRingingCallIndex(ringingCalls);
         int rankCount = getRankCount(ringingCalls, ICall::getRingingDuration, getCall());
         
         if(index != -1){
            
            comment.append(commentRinging(
                  ringingCalls,
                  index == 0,
                  rankCount,
                  ALL_CALLS));
         }
         else{
            
            ringingCalls = getHistory().getRingingCalls();
            
            if(ringingCalls.size() > COMPARE_SIZE){
               
               index = getMostRingingCallIndex(ringingCalls);
               
               if(index != -1){
                  
                  comment.append(commentRinging(
                        ringingCalls,
                        index == 0,
                        rankCount,
                        ALL_CALLS_OF_PERSON));
               }
               else{
                  
                  double ringingAverage = getCallStory().getRingingAverage(getCall().getType());
                  comment.append(getRingingCommentStore().getRingingAverage(getRingingDuration(), ringingAverage));
                  comment.append(getRingingRankComment(ringingCalls));
               }
            }
         }
      }
      
      return comment;
   }
   
   default com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing getRingingCommentStore(){
      
      return (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing) getCommentStore();
   }
   
   default CharSequence commentRinging(List<ICall> ringingCalls, boolean most, int rankCount, int type){
      
      if(!most) Collections.reverse(ringingCalls);
      
      Spanner com   = new Spanner();
      String  title = getRingingCommentStore().getRingingTitle(most);
      View.OnClickListener listener = v -> {
         
         if(!isDialogOpen()) showList(title, ringingCalls);
      };
      
      return com.append(selectMostRingingComment(listener, most, type, rankCount));
   }
   
   default CharSequence selectMostRingingComment(final View.OnClickListener listener, final boolean isMost, @Commentator.RecordType final int type, final int count){
      
      com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing commentStore = getRingingCommentStore();
      
      CharSequence comment = null;
      
      switch(type){
         
         case ALL_CALLS_OF_PERSON_IN_THE_DIRECTION:
            
            comment = commentStore.getMostRingingCommentInDirection(listener, isMost, count);
            break;
         case ALL_CALLS_OF_PERSON:
            
            comment = commentStore.getMostRingingCommentForPersonInDirection(listener, isMost, count);
            break;
         
         case ALL_CALLS:
            
            comment = commentStore.getMostRingingComment(listener, isMost, count);
            break;
         
         case ALL_CALLS_OF_DIRECTION: break;
         
      }
      
      return comment;
   }
   
   /**
    * En uzun ya da en kısa çalan aramanın index'ini döndürür.
    *
    * @param ringingPhoneCalls aramalar
    * @return index. en uzun ise 0, değilse {@code ringingPhoneCalls.size() -1}
    */
   default int getMostRingingCallIndex(List<ICall> ringingPhoneCalls){
      
      int maxDuration = (int) ringingPhoneCalls.get(0).getRingingDuration();
      
      if(maxDuration == getCall().getRingingDuration()) return 0;
      
      int minDuration = (int) ringingPhoneCalls.get(ringingPhoneCalls.size() - 1).getRingingDuration();
      
      if(minDuration == getCall().getRingingDuration())
         return ringingPhoneCalls.size() - 1;
      
      return -1;
   }
   
   default CharSequence getRingingRankComment(final List<ICall> ringingPhoneCalls){
      
      double        ringingAverage = CallStory.getRingingAverage(ringingPhoneCalls);
      final boolean isLong         = getRingingDuration() > ringingAverage;
      final String  title          = isLong ? "En Uzun Çağrılar" : "En Kısa Çağrılar";
      
      if(!isLong){
         
         Collections.reverse(ringingPhoneCalls);
      }
      
      final View.OnClickListener listener = v -> {
         
         if(!isDialogOpen()) showList(title, ringingPhoneCalls, getCall());
      };
      
      final int currentCallIndex = ringingPhoneCalls.indexOf(getCall());
      
      if(currentCallIndex == -1){
         
         Timber.w("Arama kaydı listede bulunamadı.");
         return null;
      }
      
      return new Spanner().append(((com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Ringing) getCommentStore()).getRingingRankComment(listener, currentCallIndex + 1, isLong));
   }
   
   
}
