package com.tr.hsyn.telefonrehberi.xyz.call.history;

import com.annimon.stream.function.Predicate;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import java.util.List;


/**
 * <h1>Historicall</h1>
 *
 * <p>
 * Bir arama kaydı ve bu kaydın sahibine ait tüm arama kayıtlarını
 * tutan sınıfların arayüzü.
 * </p>
 *
 * @author hsyn
 * @date 16-04-2019 20:36:28
 */
public interface Historicall<T>{
   
   /**
    * Tutulan kaydı verir.
    *
    * @return {@linkplain T}
    */
   T getVal();
   
   /**
    * Tutulan arama kaydının sahibine ait tüm aramalar.
    *
    * @return Liste
    */
   List<ICall> getCalls();
   
   /**
    * Kişiye ait aramalardan belirli türe ait olanları verir.
    *
    * @param type Arama türü.
    * @return Liste
    */
   List<ICall> getCalls(int type);
   
   /**
    * Kişiye ait aramalardan sınamayı geçen kayıtları verir.<br><br>
    * <p>
    * {@code getCalls(call -> call.isRingingDuration != 0)}<br>
    * {@code getCalls(Call::isSharable)}<br>
    *
    * @param predicate Sınama nesnesi
    * @return Liste
    */
   List<ICall> getCalls(Predicate<? super ICall> predicate);
   
   /**
    * Kişiye ait aramalardan konuşma yapılmış olanlaı verir.
    *
    * @return Liste
    */
   List<ICall> getSpeakingCalls();
   
   List<ICall> getRingingCalls();
   
   List<ICall> getRingingCalls(int type);
   
   List<ICall> getSpeakingCalls(int type);
   
   List<ICall> getSpeakingCalls(int type, Predicate<ICall> predicate);
   
   /**
    * Kişiye ait aramalardan konuşma yapılmış olanları verir.
    * Ancak kaydın {@code getRingingDuration} metodu {@code 0} dönmeyenler.
    *
    * @param type Arama türü
    * @return Liste
    */
   List<ICall> getResponsedCalls(int type);
   
   /**
    * Kişinin kayıtlarına arama ekler.
    * Tabi eğer daha önce eklenmemişse.
    *
    * @param phoneCall Arama
    */
   void addMycall(ICall phoneCall);
   
   /**
    * Kişinin kayıtlarından arama siler.
    *
    * @param phoneCall Arama
    */
   void removeMycall(ICall phoneCall);
   
   /**
    * Kişinin kayıtlarından, verilen index'e ait aramayı siler.
    *
    * @param index İndex
    */
   void removeMycall(int index);
   
   
   List<ICall> getIncommingCalls();
   
   List<ICall> getOutgoingCalls();
   
   List<ICall> getMissedCalls();
   
   List<ICall> getRejectedCalls();
   
   List<ICall> getBlockedCalls();
   
   List<ICall> getUnreachedCalls();

   
   
}
