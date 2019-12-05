package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.view.View;

import com.annimon.stream.ComparatorCompat;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.TimeTable;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import lombok.val;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.COMPARE_SIZE;

//@off
/**
 * <h1>TimeTableCommentator</h1>
 * 
 * <p>
 *    Zamansal yorumlar için varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:08:21
 */
//@on
public interface TimeTableCommentator extends TimeTable, ICommentator{
   
   @Override
   default CharSequence commentateTimeTable(){
      
      List<ICall> myCalls = getHistory().getCalls();
      
      Spanner comment = new Spanner();
      
      if(myCalls.size() > COMPARE_SIZE){
         
         Collections.sort(myCalls, ComparatorCompat.comparingLong(ICall::getDate).reversed());
         
         ICall lastCall  = myCalls.get(0);
         ICall firstCall = myCalls.get(myCalls.size() - 1);
         
         if(isSameDay(firstCall, lastCall)){
            
            comment.append(getTimeTableCommentStore().sayAllCallsInSameDay());
         }
         else{
            
            comment.append(checkFirstCall(firstCall, lastCall));
            comment.append(checkLastCall(firstCall, lastCall));
         }
      }
      
      return comment;
      
      //todo zamansal olarak ne kadar sıklıkta arıyor?
   }
   
   default com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.TimeTable getTimeTableCommentStore(){
      
      return (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.TimeTable) getCommentStore();
   }
   
   default CharSequence checkFirstCall(ICall firstCall, ICall lastCall){
      
      Spanner comment = new Spanner();
      
      if(getCall().equals(firstCall)){
         
         View.OnClickListener listener = v -> {
            
            if(!isDialogOpen()) showCall(lastCall);
         };
         
         boolean isSame   = getCall().getType() == lastCall.getType();
         String  callType = ICall.getTypeStr(getActivityReference().get(), lastCall.getType());
         
         val commentStore = getTimeTableCommentStore();
         
         comment.append(commentStore.sayThisIsFirstCall())
                .append(commentStore.getLastCallDateComment(lastCall.getDate(), listener, callType, isSame))
                .append(commentStore.getFirstAndLastCallDateIntervalComment(lastCall.getDate(), firstCall.getDate()));
         
      }
      
      return comment;
   }
   
   default CharSequence checkLastCall(ICall firstCall, ICall lastCall){
      
      Spanner comment = new Spanner();
      
      if(getCall().equals(lastCall)){
         
         View.OnClickListener listener = v -> {
            
            if(!isDialogOpen()) showCall(firstCall);
         };
         
         boolean isSame   = getCall().getType() == firstCall.getType();
         String  callType = ICall.getTypeStr(getActivityReference().get(), firstCall.getType());
         
         val commentStore = getTimeTableCommentStore();
         
         comment.append(commentStore.sayThisIsLastCall())
                .append(commentStore.getFirstCallDateComment(firstCall.getDate(), listener, callType, isSame))
                .append(commentStore.getFirstAndLastCallDateIntervalComment(lastCall.getDate(), firstCall.getDate()));
         
      }
      
      return comment;
   }
   
   static boolean isSameDay(ICall firstCall, ICall lastCall){
      
      Calendar firstDate = Calendar.getInstance();
      Calendar lastDate  = Calendar.getInstance();
      
      lastDate.setTimeInMillis(lastCall.getDate());
      firstDate.setTimeInMillis(firstCall.getDate());
      
      int lastYear    = lastDate.get(Calendar.YEAR);
      int lastMounth  = lastDate.get(Calendar.MONTH);
      int lastDay     = lastDate.get(Calendar.DAY_OF_MONTH);
      int firstYear   = firstDate.get(Calendar.YEAR);
      int firstMounth = firstDate.get(Calendar.MONTH);
      int firstDay    = firstDate.get(Calendar.DAY_OF_MONTH);
      
      return lastYear == firstYear &&
             lastMounth == firstMounth &&
             lastDay == firstDay;
   }
   
   
}
