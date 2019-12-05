package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getDurationStyles;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>RingingCommentStore</h1>
 * <p>
 * Telefonun çalma süresine dair yorumların bildirimi.
 *
 * @author hsyn 2019-11-27 23:23:28
 */
public interface Ringing extends IComment{
   
   
   /**
    * Karşılaştırma sonucunda yorumlanan aramanın en uzun ya da en kısa çalan arama olduğu ortaya çıkmış.
    * Bu karşılaştırma aramanın yönüne göre yapılmış karşılaştırma.
    *
    * @param listener dinleyici karşılaştırma yapılan diğer arama kayıtlarını en uzundan
    *                 kısaya ya da en kısadan uzuna doğru olan bir listede göstecek.
    *                 Bu listeye yorumlanan arama kaydı da dahil ve her iki durumda da listenin başında olacak.
    * @param isMost   en uzun | en kısa
    * @param count    aynı çalma süresine sahip kayıt sayısı.
    *                 Eğer bu sayı 1 ise yorumlanan aramanın kendisidir.
    *                 Yani aynı süreye sahip başka kayıt yok demektir.
    *                 Yani tek başına listede birinci demektir.
    * @return yorum
    */
   CharSequence getMostRingingCommentInDirection(View.OnClickListener listener, boolean isMost, int count);
   
   /**
    * Karşılaştırma sonucunda yorumlanan aramanın en uzun ya da en kısa çalan arama olduğu ortaya çıkmış.
    * Bu karşılaştırma aramanın yönüne göre ve sadece kişinin kayıtları ile yapılmış karşılaştırma.
    *
    * @param listener dinleyici karşılaştırma yapılan diğer arama kayıtlarını en uzundan
    *                 kısaya ya da en kısadan uzuna doğru olan bir listede göstecek.
    *                 Bu listeye yorumlanan arama kaydı da dahil ve her iki durumda da listenin başında olacak.
    * @param isMost   en uzun | en kısa
    * @param count    aynı çalma süresine sahip kayıt sayısı.
    *                 Eğer bu sayı 1 ise yorumlanan aramanın kendisidir.
    *                 Yani aynı süreye sahip başka kayıt yok demektir.
    *                 Yani yorumlanan arama tek başına listede birinci demektir.
    * @return yorum
    */
   CharSequence getMostRingingCommentForPersonInDirection(View.OnClickListener listener, boolean isMost, int count);
   
   /**
    * Karşılaştırma sonucunda yorumlanan aramanın en uzun ya da en kısa çalan arama olduğu ortaya çıkmış.
    * Bu karşılaştırma tüm arama kayıtları ile yapılmış karşılaştırma.
    *
    * @param listener dinleyici karşılaştırma yapılan diğer arama kayıtlarını en uzundan
    *                 kısaya ya da en kısadan uzuna doğru olan bir listede göstecek.
    *                 Bu listeye yorumlanan arama kaydı da dahil ve her iki durumda da listenin başında olacak.
    * @param isMost   en uzun | en kısa
    * @param count    aynı çalma süresine sahip kayıt sayısı.
    *                 Eğer bu sayı 1 ise yorumlanan aramanın kendisidir.
    *                 Yani aynı süreye sahip başka kayıt yok demektir.
    *                 Yani yorumlanan arama tek başına listede birinci demektir.
    * @return yorum
    */
   CharSequence getMostRingingComment(View.OnClickListener listener, boolean isMost, int count);
   
   /**
    * Aramanın yönündeki tüm arama kayıtları üzerinden alınmış ortalama.
    * 
    * @param ringingDuration çalma süresi
    * @param average ortalama
    * @return yorum
    */
   CharSequence getRingingAverage(long ringingDuration, double average);
   
   /**
    * Telefonun çalma süresini söyle.
    *
    * @param duration Süre (milisaniye)
    * @return Yorum
    */
   default CharSequence getRingingDuration(long duration){
      
      final long oneSecond = 1000L;
      return new Spanner("Telefonun çalma süresi ")
            .append(Stringx.format("%.1f saniye", (float) duration / oneSecond), getDurationStyles())
            .append(". ");
   }
   
   /**
    * Çalma süresinin diğer aramaların çalma süreleriyle karşılaştırılmasının sonuçları.
    *
    * @param listener dinleyici karşılaştırma yapılan diğer arama kayıtlarını gösterecek.
    *                 Yorumlanmakta olan kayıt da buna dahil ve listede hangi sırada ise o sırada gösterilecek.
    *                 Yorumlanan arama ya en uzun ya da en kısa çalan arama kayıtları içinde olacak.
    *                 Yani gösterilecek liste bu iki listeden biri olacak.
    * @param rank     sıra
    * @param isLong   en uzun | en kısa
    * @return yorum
    */
   default CharSequence getRingingRankComment(View.OnClickListener listener, int rank, boolean isLong){
      
      String most = isLong ? "en uzun çağrılar" : "en kısa çağrılar";
      
      return new Spanner("Bu kayıt bu süre ile ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" listesinde %d. sırada. ", rank));
   }
   
   /**
    * Dialog başlığı.
    * 
    * @param isMost en uzun | en kısa
    * @return başlık
    */
   default String getRingingTitle(boolean isMost){
      
      return isMost ? "En Uzun Çalan" : "En Kısa Çalan";
   }
   
   
}
