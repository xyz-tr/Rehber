package com.tr.hsyn.telefonrehberi.xyz.call;

import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import java.util.List;


/**
 * <h1>CallGroup</h1>
 *
 * <p>
 *    Arama kayıtlarını telefon numarasına göre gruplamak için.
 *    Numaraya karşılık, bu numaraya ait tüm kayıtların tutulacağı nesne.
 *    Ancak numara yerine başka herhangi bir nesne de verilebilir.
 *    Ancak bunun karşısında daima arama kaydı listesi olacak.
 *
 * @author hsyn
 * @date 18-04-2019 16:18:37
 */
public class CallGroup<T> implements Group<T>{
   
   private T           item;
   private List<ICall> calls;
   private long        totalDuration;
   private int         rank;
   
   public CallGroup(){}
   
   public CallGroup(T item, List<ICall> phoneCalls){
   
      this.item  = item;
      this.calls = phoneCalls;
   }
   
   @Override
   public T getGroupItem(){
      
      return item;
   }
   
   @Override
   public void addCall(ICall call){
      calls.add(call);
   }
   
   @Override
   public List<ICall> getCalls(){
      
      return calls;
   }
   
   @Override
   public long getTotalDuration(){
      
      return totalDuration;
   }
   
   @Override
   public void setTotalDuration(long totalDuration){
      
      this.totalDuration = totalDuration;
   }
   
   @Override
   public int getRank(){
      
      return rank;
   }
   
   @Override
   public void setRank(int rank){
      
      this.rank = rank;
   }
   
   @Override
   public boolean equals(Object obj){
   
      try{
   
         return obj instanceof Group && Contacts.matchNumbers((String) item, (String) ((Group) obj).getGroupItem());
      }
      catch(Exception e){
   
         return item.equals(((Group) obj).getGroupItem());
      }
      
   }
   
   @Override
   public int hashCode(){
      
      return item.hashCode();
   }
   
}
