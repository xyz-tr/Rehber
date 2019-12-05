package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.view.View;

import com.annimon.stream.ComparatorCompat;
import com.google.common.base.Preconditions;
import com.tr.hsyn.telefonrehberi.util.Rank;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Response;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.var;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.ALL_CALLS_OF_PERSON;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.COMPARE_SIZE;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.getRankCount;

//@off
/**
 * <h1>ResponseCommentator</h1>
 * 
 * <p>
 *    Tepkisel yorumlar için varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:17:09
 */
//@on
public interface ResponseCommentator extends Response, ICommentator{
   
   /**
    * Cevap verme hızlarını karşılaştırarak yorum yapar.
    * Sadece en hızlı ya da en geç verilen cevaplar dikkate alınır.
    */
   @Override
   default CharSequence commentateResponse(){
      
      Spanner comment = new Spanner();
      
      //Aramanın izlenmiş olması şart
      if(getCall().isSharable()){
         
         if(getCall().getType() != Type.REJECTED){
            
            if(getSpeakingDuration() == 0L) return comment;
         }
         
         Preconditions.checkArgument(getCommentStore() instanceof com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response, "Bu karşılaştırma için gerekli olan yorum dükkanı : ResponseCommentStore");
         
         com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response responseCommentStore = (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response) getCommentStore();
         comment.append(responseCommentStore.getResponseDuration(getRingingDuration()));
         
         //Cevap verilmiş tüm aramalar
         List<ICall> callsWithResponse = getCallStory().getResponsedCalls(getCall().getType());
         
         if(callsWithResponse.size() > COMPARE_SIZE){
            
            Collections.sort(callsWithResponse, ComparatorCompat.comparingLong(ICall::getRingingDuration));
            
            var com = commentMostResponse(callsWithResponse, ALL_CALLS);
            
            if(com == null){
               
               //kişinin cevap verilmiş tüm aramaları
               callsWithResponse = getHistory().getResponsedCalls(getCall().getType());
               
               if(callsWithResponse.size() > COMPARE_SIZE){
                  
                  Collections.sort(callsWithResponse, ComparatorCompat.comparingLong(ICall::getRingingDuration));
                  
                  com = commentMostResponse(callsWithResponse, ALL_CALLS_OF_PERSON);
                  
                  if(com == null){
                     
                     comment.append(commentResponseAverage());
                  }
                  else{
                     
                     comment.append(com);
                  }
               }
               else{
                  
                  comment.append(commentResponseAverage());
               }
            }
            else{
               
               comment.append(com);
            }
         }
      }
      
      return comment;
   }
   
   default CharSequence commentMostResponse(List<ICall> callsWithResponse, @Commentator.RecordType int commentType){
      
      final View.OnClickListener                                                            listener;
      boolean                                                                              first                = getCall().equals(callsWithResponse.get(0));
      boolean                                                                              last                 = getCall().equals(callsWithResponse.get(callsWithResponse.size() - 1));
      AtomicBoolean                                                                        isMost               = new AtomicBoolean(true);
      com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response responseCommentStore = (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response) getCommentStore();
      
      Spanner comment = null;
      
      if(first || last){
         
         if(last){
            
            Collections.reverse(callsWithResponse);
            isMost.set(false);
         }
         
         listener = v -> {
            
            if(!isDialogOpen()) showList(
                  responseCommentStore.getResponseRankTitle(isMost.get()),
                  callsWithResponse,
                  getCall());
            
            
         };
         
         CharSequence com = commentType == ALL_CALLS ?
                            responseCommentStore.getMostResponseRankInTheDirectionComment(listener, isMost.get()) :
                            responseCommentStore.getMostResponseRankInPersonComment(listener, isMost.get());
         
         comment = new Spanner().append(com);
      }
      
      return comment;
   }
   
   default CharSequence commentResponseAverage(){
      
      List<ICall> callsWithResponse = getCallStory().getResponsedCalls(getCall().getType());
      
      if(callsWithResponse.size() < COMPARE_SIZE){ return null; }
      
      Spanner comment = new Spanner();
      
      Collections.sort(callsWithResponse, ComparatorCompat.comparingLong(ICall::getRingingDuration));
      
      double responseAverage = getCallStory().getResponseAverage(callsWithResponse);
      
      comment.append(((com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response) getCommentStore()).getResponseAverageComment(responseAverage, getRingingDuration()));
      
      comment.append(writeResponseAverageRankComment(callsWithResponse, getRingingDuration() < responseAverage));
      
      return comment;
   }
   
   default CharSequence writeResponseAverageRankComment(List<ICall> callsWithResponse, boolean isQuick){
      
      com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response responseCommentStore = (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Response) getCommentStore();
      
      if(!isQuick){
         
         Collections.reverse(callsWithResponse);
      }
      
      int     rank    = getRank(callsWithResponse, getCall());
      Spanner comment = new Spanner();
      
      if(rank != -1){
         
         int          rankCount = getRankCount(callsWithResponse, ICall::getRingingDuration, getCall());
         CharSequence title     = responseCommentStore.getResponseRankTitle(isQuick);
         
         View.OnClickListener listener = v -> {
            
            if(!isDialogOpen())
               showList(title, callsWithResponse, getCall());
            
         };
         
         comment.append(responseCommentStore.getResponseAverageRankComment(listener, new Rank(rank, rankCount), isQuick));
         
      }
      
      return comment;
   }
   
   /**
    * Aramanın listede kaçıncı sırada olduğunu döndürür.
    * Bu sıra 1'den başlıyor.
    *
    * @param calls liste
    * @param call  arama
    * @return rank
    */
   static int getRank(Iterable<ICall> calls, ICall call){
      
      int i = 0;
      
      for(ICall _call : calls){
         
         if(call.equals(_call)) return i + 1;
         
         i++;
      }
      
      return -1;
   }
   
   
}
