package com.tr.hsyn.telefonrehberi.xyz.ptt.activity.relation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * <h1>Relation</h1>
 * <p>
 * Kullanıcı rehberindeki her kişiyle bağlantı kurabilir.
 * Bunun için kişinin <b>GMail</b> adresinin kayıtlı olması yeterli.
 * GMail adresi kayıtlı kişiler kullanıcı açısından bir bağlantıdır.
 * Ancak her bağlantı aktif olmayabilir.
 * Bu sınıf bir bağlantıyı ve bu bağlantının özelliklerini tarif eder.
 * 
 * @author hsyn 
 * @date 2019-10-12 21:34:18
 * 
 */
@ToString
@Getter
@RequiredArgsConstructor
public final class Relation{
   
   /**
    * Bağlantının rehberdeki contactId değeri.
    * Bu değer kişinin detayları için bir anahtardır.
    */
   private final String contactId;
   
   /**
    * Bağlantının rehberde kayıtlı ismi
    */
   private final String name;
   
   /**
    * Bağlantı adresi
    */
   private final String address;
   
   /**
    * Bağlantının aktif olup olmadığı
    */
   @Setter private boolean isActive;
   
   /**
    * Bağlantının aktif olma zamanı
    */
   @Setter private long relationTime;
   
   
   
}
