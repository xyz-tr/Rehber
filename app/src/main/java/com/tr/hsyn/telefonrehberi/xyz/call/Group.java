package com.tr.hsyn.telefonrehberi.xyz.call;


import java.util.List;


/**
 * <h1>Group</h1>
 *
 * <p>Telefon numarasına karşılık telefon numarasına ait tüm kayıtlar.</p>
 *
 * @author hsyn
 * @date 18-04-2019 16:13:05
 */
public interface Group<T>{
   
   void addCall(ICall call);
   
   /**
    * Returns Number.
    *
    * @return Number
    */
   T getGroupItem();
   
   /**
    * Numaraya ait tüm kayıtlar
    *
    * @return Kayıtlar
    */
   List<ICall> getCalls();
   
   /**
    * Kaydın konuşma sürelerinin, ya da zil sesi çalma sürelerinin toplamı.
    *
    * @return Toplam süre
    */
   long getTotalDuration();
   
   /**
    * Toplam süreyi setValue eder.
    *
    * @param totalDuration Toplam süre
    */
   void setTotalDuration(long totalDuration);
   
   /**
    * Numaranın sıra numarası.
    *
    * @return Sıra
    */
   int getRank();
   
   /**
    * Numaranın sıra numarasını setValue eder.
    *
    * @param rank Sıra
    */
   void setRank(int rank);
}
