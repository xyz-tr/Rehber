package com.tr.hsyn.telefonrehberi.xyz.call.story;

import android.annotation.SuppressLint;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.xyz.call.Group;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.FilterType;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class PhoneCallStory implements CallStory{
   
   private List<ICall> calls;
   
   
   public PhoneCallStory(List<ICall> calls){
      
      this.calls = calls;
   }
   
   @Override
   public boolean remove(ICall call){
      
      int index = calls.indexOf(call);
      
      if(index != -1) return calls.remove(index) != null;
      
      return false;
   }
   
   @SuppressLint("SwitchIntDef")
   @Override
   public List<Group<String>> getMostCalls(@FilterType int type){
      
      List<Group<String>> mostCalls = new ArrayList<>();
      
      switch(type){
         
         case Filter.MOST_OUTGOING_CALLS:
            
            mostCalls = CallStory.setRankByCallSize(createGroupList(getOutgoingCalls()), true);
            break;
         
         case Filter.MOST_INCOMMING_CALLS:
            
            mostCalls = CallStory.setRankByCallSize(createGroupList(getIncommingCalls()), true);
            break;
         
         case Filter.MOST_MISSED_CALLS:
            
            mostCalls = CallStory.setRankByCallSize(createGroupList(getMissedCalls()), true);
            break;
         
         case Filter.MOST_REJECTED_CALLS:
            
            mostCalls = CallStory.setRankByCallSize(createGroupList(getRejectedCalls()), true);
            break;
         
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL:
            
            mostCalls = CallStory.setRankByDuration(createGroupList(getIncommingCalls(CallPredicate.PREDICATE_SPEAKING)), true);
            break;
         
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL:
            
            mostCalls = CallStory.setRankByDuration(createGroupList(getOutgoingCalls(CallPredicate.PREDICATE_SPEAKING)), true);
            break;
         
         case Filter.LONGEST_MISSED_CALLS:
         case Filter.QUICKEST_MISSED_CALLS:
            
            mostCalls = createGroupList(getMissedCalls(CallPredicate.PREDICATE_RINGING));
            mostCalls = CallStory.setRankByRinging(mostCalls, type == Filter.LONGEST_MISSED_CALLS);
            break;
         
         case Filter.QUICKEST_ANSWERED_CALLS:
         case Filter.LONGEST_ANSWERED_CALLS:
            
            mostCalls = createGroupList(getIncommingCalls(CallPredicate.PREDICATE_RESPONSE));
            mostCalls = CallStory.setRankByRinging(mostCalls, type == Filter.LONGEST_ANSWERED_CALLS);
            break;
         
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
            
            mostCalls = createGroupList(getOutgoingCalls(CallPredicate.PREDICATE_RESPONSE));
            mostCalls = CallStory.setRankByRinging(mostCalls, type == Filter.LONGEST_MAKE_ANSWERED_CALLS);
            break;
         
         
         case Filter.QUICKEST_REJECTED_CALLS:
         case Filter.LONGEST_REJECTED_CALLS:
            
            mostCalls = createGroupList(getRejectedCalls(CallPredicate.PREDICATE_RESPONSE));
            mostCalls = CallStory.setRankByRinging(mostCalls, type == Filter.LONGEST_REJECTED_CALLS);
            
            
            break;
         
         
         default: Timber.w("Most calls type uyumu yok : %d", type);
      }
      
      return mostCalls;
   }
   
   @Override
   public List<ICall> getFilteredCalls(int type){
      
      //@off
      switch(type){
         
         case Filter.INCOMMING : return getIncommingCalls();
         case Filter.OUTGOING  : return getOutgoingCalls();
         case Filter.MISSED    : return getMissedCalls();
         case Filter.REJECTED  : return getRejectedCalls();
         case Filter.DELETED   : return getDeletedCalls();
         case Filter.BLOCKED   : return getBlockedCalls();
         
         //En çok konuşanlar
         case Filter.MOST_SPEAKING:
         //En çok konuştukların
         case Filter.MOST_TALKING:
            
            return Stream.of(type == Filter.MOST_SPEAKING ? getIncommingCalls() : getOutgoingCalls())
                         .filter(CallPredicate.PREDICATE_SPEAKING)
                         .sorted(ComparatorCompat.comparingInt(ICall::getDuration).reversed())
                         .toList();
            
      }
      
      return calls;
   }
   
   @Override
   public List<ICall> getCalls(){
      
      return calls;
   }
   
   @Override
   public void setCalls(List<ICall> calls){
      
      this.calls = calls;
   }
   

}
