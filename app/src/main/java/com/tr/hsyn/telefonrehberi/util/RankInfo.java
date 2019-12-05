package com.tr.hsyn.telefonrehberi.util;

/**
 * <h1>RankInfo</h1>
 * Arama kayıtları karşılaştırldığında sonuç bilgileri bu nesnede taşınır.
 * 
 * @author hsyn 2019-11-21 20:58:58
 */
public interface RankInfo{
   
   /**
    * Aramaya verilmiş sıralama numarası.
    * Sıralama 1'den başlar.
    * 
    * @return sıralama numarası
    */
   int getRank();
   
   /**
    * Aynı sıralama numarasına sahip kişi sayısı.
    * Eğer 1 dönerse, bu sıralamada kişi tek başına demektir.
    * Mesela 'tek başına birinci'
    * 
    * @return kişi sayısı
    */
   int getRankCount();
   
   /**
    * En yüksek sıralama numarasını döndür.
    * En yüksek demek en sonda demektir.
    * Mesela bu methodun döndürdüğü değer ile {@linkplain #getRank()} methodunun değeri 
    * eşit ise kişi son sırada demektir.
    * 
    * @return En yüksek sıralama numarası
    */
   int getMostRank();
   
}
