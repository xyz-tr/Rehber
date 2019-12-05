package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;


import android.view.View;

import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;

import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;




/*
 * Yüzlerinize baktığımda samimi olduğumuz yönündeydi izlenimlerim.
 * */


/**
 * <h1>Count</h1>
 * Arama sayısına göre yapılacak yorumlar.<br>
 * Buradaki methodlar yorumcular tarafından çağrılır.
 * Methodların çoğunda {@code View.OnClickListener} nesnesi de verilmekte.
 * Bu dinleyiciler altı çizilmesi gereken ve dialog penceresinin açılacağı anlamına gelen yerlerdir.
 * Hangi kelimelerin üzerine dokunulduğunda dialog açılsın istiyorsan o kelimelere bu dinleyicileri set et.
 *
 * <br><br>
 * Mesela arayüzün içinde default olarak tanımlanmış {@link #onlyHasOneRecordInTheLogComment(View.OnClickListener, int)} metodu var.<br><br>
 * <pre>default CharSequence onlyHasOneRecordInTheLogComment(
 *                               View.OnClickListener listener,
 *                               int count){
 *
 *       return new Spanner("Bu kişi haricinde, sadece 1 kaydı bulunan ")
 *             .append(Stringx.format("%d kişi daha", count), getLinkStyles(listener))
 *             .append(" var. ");
 * }</pre>
 * <p>
 * Bu methodun <br>
 * {@code Stringx.format("%d kişi daha", count), getLinkStyles(listener)}<br>
 * bu satırında dinleyici <u>"%d kişi daha"</u> kelimeleri üzerine set ediliyor.
 * Bu kelimelerin üzerine tıklandığında dialog penceresi açılır ve ilgili kişiler gösterilir.
 * Senin yapacağın tek şey dinleyiciyi anlamlı bir yere set etmek.
 *
 * @author hsyn 2019-10-31 12:07:37
 */
public interface Count extends IComment{
   
   /**
    * Toplam arama sayısı ile ilgili yorum.
    * Yorumcu burada tüm aramaları göstermek istiyor.
    * <b>Tüm aramalar</b>dan kasıt, yorumlanmakta olan aramanın türündeki tüm kayıtlar.
    * Mesela yorumlanan arama eğer cevapsız bir arama ise, bu kişinin tüm cevapsız aramaları kastediliyor (<u>yorumlanmakta olan bu kayıt dahil</u>).
    * Yorumu buna göre yap ve dinleyiciyi uygun bir yere set et.
    *
    * @param listener Aralamarın gösterilmesi için verilecek olan linke set edilecek dinleyici
    * @param size     Arama sayısı
    * @return Yorum
    */
   CharSequence getTotalCallComment(View.OnClickListener listener, int size);
   
   /**
    * Gerçekleşen arama kaydı bu kişinin tek arama kaydı ve bu kişiden başka,
    * tek bir kayda sahip olan başka bir kişi olmadığını bildir.
    * Buradaki kayıtlar aramanın türüne bağlıdır.
    * Mesela cevapsız bir arama yorumlanıyorsa,
    * bu kişinin sana sadece 1 cevapsız çağrısı var demektir.
    * Ve sana sadece 1 cevapsız çağrısı olan başka bir kişi yok demektir.
    *
    * @param name kaydın sahibi
    * @return Yorum
    */
   CharSequence getNoOneTimeCallComment(String name);
   
   /**
    * Gerçekleşen arama kaydından başka, tek bir kayda sahip kişileri bildir.
    * Bu yorumun {@link #onlyHasOneRecordInTheLogComment(View.OnClickListener, int)}
    * methodundan tek farkı aramanın türüne bağlı olması.
    * Yani bu yorum aramanın türüne göre yapılmalı.
    * Diyelimki aramanın türü gelen arama,
    * bu durumda bu kişi kullanıcıyı sadece bir kez aramış demektir.
    * Yani gelen arama kayıtlarında sadece 1 gelen araması var demektir.
    * Giden arama kayıtlarında 50 araması olabilir. Bu bizi ilgilendirmez.
    * Burada bildiğiiz şey şuki, bu kayıt bu kişinin arama türüne bağlı olarak gerçekleşmiş tek kaydı.
    * Ve bu kişi gibi başka kişiler de var. İşte yorumcu bu kişileri göstermek istiyor.
    * Yine gelen arama örneğinden gidersek,
    * bu yorumlanmakta olan arama kaydı bu kişiden gelen tek arama
    * ve aynı bu kişi gibi sadece 1 gelen araması olan başka kişiler de var.
    * Bu kişiler içinde yorumlanmakta olan kişi yok. {@code count} parametresine bu kişi dahil değil.
    * Yani yorumlanmakta olan aramanın sahibi haricindeki kişilerin sayısı = {@code count}.
    * Ve açılacak olan dialog'da da bu kişi haricindeki kişiler gösterilecek.
    * Bu yüzden yorumu buna göre yap ve dinleyiciyi uygun bir yere set et.
    *
    * @param listener Diğer kayıtları göstermek için tetikleyici
    * @param count    Tek bir kayda sahip olan kişi sayısı
    * @param name     yorumlanmakta olan aramanın sahibinin ismi
    * @return Yorum
    */
   CharSequence getOneTimeCallComment(View.OnClickListener listener, int count, String name);
   
   CharSequence thisIsOnlyOneCallComment(boolean isLog);
   
   CharSequence thisIsOnlyOneCallDialogTitle();
   
   /**
    * Arama sayısıyla ilgili yorumlarda gösterilecek dialog başlığı.
    * Başlık üç kelimeden oluşur(sadece Cevapsız aramalar için 4 kelime)
    * {@code isMost true} ise 'En Çok' {@code false} ise 'En Az' olur.
    * Üçüncü kelime ise aramanın türüne göre olur.<br><br>
    *
    * <p>
    * %s = isMost (Az | Çok)
    *    <ul>
    *       <li>{@code Type.INCOMMING --> "En %s Arayanlar"}</li>
    *       <li>{@code Type.OUTGOING --> "En %s Arananlar"}</li>
    *       <li>{@code Type.MISSED --> "En %s Cevapsız Kalanlar"}</li>
    *       <li>{@code Type.REJECTED --> "En %s Reddedilenler"}</li>
    *       <li>{@code Type.BLOCKED --> "En %s Engellenenler"}</li>
    *       <li>{@code Type.UNREACHED --> "En %s Ulaşamayanlar"}</li>
    *    </ul>
    * <p>
    * Mesela {@code isMost true} ise ve arama türü {@code Type.REJECTED} ise --> 'En Çok Reddedilenler' başlık olur.<br>
    *
    * @param type   Aramanın türü
    * @param isMost En Çok?
    * @return Yorum.
    */
   default CharSequence getRankTitle(int type, boolean isMost){
      
      String interval = Stringx.format("%s", isMost ? "Çok" : "Az");
      
      //@off
      switch(type){
         
         case Type.INCOMMING: return Stringx.format("En %s Arayanlar", interval);
         case Type.OUTGOING:  return Stringx.format("En %s Arananlar", interval);
         case Type.MISSED:    return Stringx.format("En %s Cevapsız Kalanlar", interval);
         case Type.REJECTED:  return Stringx.format("En %s Reddedilenler", interval);
         case Type.BLOCKED:   return Stringx.format("En %s Engellenenler", interval);
         case Type.UNREACHED: return Stringx.format("En %s Ulaşamayanlar", interval);
         
         default: return null;//@on
      }
   }
   
   /**
    * Kişinin, arama sayısının sıra numarasına göre yorum yap.<br>
    * Kişinin arama sayısı, diğer kişilerin arama sayısı ile karşılaştırılır ve sıralanır.
    * Karşılaştırma sonucunda bu yorumlanmakta olan aramaya bir sıralama numarası verilir.
    * Bu builgiler {@linkplain RankInfo} nesnesi içindedir.
    *
    * @param listener Activity'de gösterilecek olan dialog için tetikleyici
    * @param type     Aramanın türü
    * @param rankInfo Sıra ile ilgili bilgiler
    * @return Yorum
    */
   default CharSequence getRankComment(View.OnClickListener listener, int type, RankInfo rankInfo, String name){
      
      CharSequence mostString = getRankString(type, rankInfo.getMostRank() < 9 || rankInfo.getRank() < 6);
      
      if(mostString == null){
         
         Timber.w("Ara kelime bulunamadı");
         return null;
      }
      
      return new Spanner()
            .append(Stringx.format("%s ", !Stringx.isPhoneNumber(name) ? Stringx.toTitle(name) : "Bu kişi"), Spans.bold())
            .append(Stringx.format("%s", mostString), getLinkStyles(listener))
            .append(Stringx.format(" kişiler listesinde %s %s sırada. ", rankInfo.getRankCount() == 1 ? "tek başına" : Stringx.format("%d kişi ile birlikte", rankInfo.getRankCount() - 1), rankInfo.getRank() == rankInfo.getMostRank() ? "son" : Stringx.format("%d.", rankInfo.getRank())));
   }
   
   /**
    * Arama sayısıyla ilgili yorumlarda kullanılacak ara cümle.<br><br>
    * <p>
    * //%s = isMost (az | çok)
    * <ul>
    *    <li>Type.INCOMMING --> "en %s arayan"</li>
    *    <li>Type.OUTGOING  --> "en %s aradığın"</li>
    *    <li>Type.MISSED    --> "en %s cevapsız bıraktığın"</li>
    *    <li>Type.REJECTED  --> "en %s reddettiğin"</li>
    *    <li>Type.BLOCKED   --> "en %s engellediğin"</li>
    * </ul>
    *
    * @param type   Aramanın türü
    * @param isMost Çok mu?
    * @return Yorum
    */
   default CharSequence getRankString(int type, boolean isMost){
      
      String interval = Stringx.format("%s", isMost ? "çok" : "az");
      //@off
      switch(type){
         
         case Type.INCOMMING: return Stringx.format("en %s arayan", interval);
         case Type.OUTGOING:  return Stringx.format("en %s aradığın", interval);
         case Type.MISSED:    return Stringx.format("en %s cevapsız bıraktığın", interval);
         case Type.REJECTED:  return Stringx.format("en %s reddettiğin", interval);
         case Type.BLOCKED:   return Stringx.format("en %s engellediğin", interval);
         default:             return null;
      }//@on
   }
   
   /**
    * Sadece 1 arama kaydı bulunan kişiler ile ilgili yorum.
    * Eğer yorumlanmakta olan kayıt, kişinin tek arama kaydı değilse bu method çağrılmaz.
    *
    * @param listener Activity'de gösterilecek olan dialog için tetikleyici
    * @param count    tek bir arama kaydı olan kişi sayısı
    * @return yorum
    */
   default CharSequence onlyHasOneRecordInTheLogComment(View.OnClickListener listener, int count){
      
      return new Spanner("Bu kişi haricinde, sadece 1 kaydı bulunan ")
            .append(Stringx.format("%d kişi daha", count), getLinkStyles(listener))
            .append(" var. ");
   }
   
   /**
    * Sadece 1 kaydı olan bu kişiden başka bir kişi yoksa bu method çağrılır.
    *
    * @return yorum
    */
   default CharSequence thereIsNoOneHasOneRecordInTheLogComment(){
      
      return new Spanner("Bu kişi haricinde, sadece 1 kaydı bulunan başka bir kişi yok. ");
   }
   
   /**
    * Eğer 1 kaydı olan başka kişiler varsa bu kişiler gösterileceği zaman kullanılacak dialogun başlığı bu method ile alınır.
    * Bu açılacak olan dialog penceresinin linki {@linkplain #onlyHasOneRecordInTheLogComment(View.OnClickListener, int)}
    * metodunda verilmeli.
    *
    * @return dialog başlığı
    */
   default CharSequence getOneRecordTitle(){
      
      return "1 Kaydı Olanlar";
   }
   
   
}
