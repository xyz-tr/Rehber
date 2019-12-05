package com.tr.hsyn.telefonrehberi.xyz.call.history;


import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Predicate;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;

import java.util.List;


/**
 * <h1>History</h1>
 * <p>Kişiye özel arama kayıtları.
 * Bir arama kaydına karşılık,
 * kaydın sahibine ait tüm aramaların olduğu bir liste tadında.
 * Ancak anahtar olarak arama kaydından başka bir şey de kullanılabilir.
 * </p>
 *
 * @author hsyn 2019-12-03 15:07:28
 */
public class History<T> implements Historicall<T>{
   
   private final T           val;
   private final List<ICall> mycalls;
   
   
   private History(final T val, final List<ICall> mycalls){
      
      this.val     = val;
      this.mycalls = mycalls;
   }
   
   @Override
   public final T getVal(){
      
      return val;
   }
   
   @Override
   public final List<ICall> getCalls(){
      
      return mycalls;
   }
   
   public static <T> Historicall<T> create(T val, List<ICall> mycalls){
      
      return new History<>(val, mycalls);
   }
   
   @Override
   public final void addMycall(ICall phoneCall){
      
      if(!mycalls.contains(phoneCall)){
         mycalls.add(phoneCall);
      }
   }
   
   @Override
   public final void removeMycall(ICall phoneCall){
      
      mycalls.remove(phoneCall);
   }
   
   @Override
   public final void removeMycall(int index){
      
      mycalls.remove(index);
   }
   
   @Override
   public List<ICall> getIncommingCalls(){
      
      return getCalls(Type.INCOMMING);
   }
   
   @Override
   public List<ICall> getOutgoingCalls(){
      
      return getCalls(Type.OUTGOING);
   }
   
   @Override
   public List<ICall> getMissedCalls(){
      
      return getCalls(Type.MISSED);
   }
   
   @Override
   public List<ICall> getRejectedCalls(){
      
      return getCalls(Type.REJECTED);
   }
   
   @Override
   public List<ICall> getBlockedCalls(){
      
      return getCalls(Type.BLOCKED);
   }
   
   @Override
   public List<ICall> getUnreachedCalls(){
      
      return getCalls(Type.UNREACHED);
   }
   
   @Override
   public final List<ICall> getCalls(int type){
      
      return Stream.of(mycalls)
                   .filter(xc -> xc.getType() == type)
                   .toList();
   }
   
   @Override
   public final List<ICall> getCalls(@NonNull Predicate<? super ICall> predicate){
      
      return Stream.of(mycalls)
                   .filter(predicate)
                   .toList();
   }
   
   @Override
   public final List<ICall> getSpeakingCalls(){
      
      return CallStory.getSpeakingCalls(mycalls);
   }
   
   @Override
   public List<ICall> getRingingCalls(){
      
      return CallStory.getRingingCalls(mycalls);
   }
   
   @Override
   public List<ICall> getRingingCalls(int type){
      
      return CallStory.getRingingCalls(mycalls, type);
   }
   
   @Override
   public List<ICall> getSpeakingCalls(int type){
      
      return CallStory.getSpeakingCalls(mycalls, type);
   }
   
   @Override
   public List<ICall> getSpeakingCalls(int type, Predicate<ICall> predicate){
      
      return Stream.of(mycalls)
                   .filter(cx -> cx.getType() == type && predicate.test(cx))
                   .toList();
   }
   
   @Override
   public final List<ICall> getResponsedCalls(int type){
      
      return Stream.of(mycalls)
                   .filter(xc -> xc.getRingingDuration() != 0 && xc.getType() == type && xc.getDuration() != 0)
                   .toList();
   }
   
}
