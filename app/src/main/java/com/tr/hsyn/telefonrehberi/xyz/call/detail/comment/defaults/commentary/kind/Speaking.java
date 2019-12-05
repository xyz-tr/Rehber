package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;

import android.view.View;

import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getDurationStyles;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.Style.getLinkStyles;


/**
 * <h1>SpeakingCommentStore</h1>
 * <p>
 * Konuşma süresine göre yorumlar yaparken yorumcuların çağıracağı metotlar.<br>
 *
 * @author hsyn 2019-11-23 00:09:03
 */
public interface Speaking extends IComment{
   
   /**
    * Yorumu yapılan aramanın yönündeki arama kayıtları için yapılan karşılaştırmanın yorumu.
    *
    * @param listener Belirlenen aramaları göstermek için
    * @param isMost   En uzun veya en kısa
    * @return Yarum
    */
   CharSequence commentMostSpeakingInDirection(View.OnClickListener listener, boolean isMost);
   
   /**
    * Yorumu yapılan aramanın sahibine ait arama kayıtları için yapılan karşılaştırmanın yorumu.
    *
    * @param listener Belirlenen aramaları göstermek için
    * @param isMost   En uzun veya en kısa
    * @return Yarum
    */
   CharSequence commentMostSpeakingForPersonInTheLog(View.OnClickListener listener, boolean isMost);
   
   /**
    * Konu olan aramanın yönüne ve sahibine ait aramalar için yapılan karşılaştırmanın yorumu.
    *
    * @param listener Belirlenen aramaları gösterecek
    * @param isMost   En uzun-En kısa
    * @return Yorum
    */
   CharSequence commentMostSpeakingForPersonInDirection(View.OnClickListener listener, boolean isMost);
   
   /**
    * Tüm arama kayıtları için yapılan karşılaştırmanın yorumu.
    * Bu yoruma aynı konuşma sürelerine sahip kişiler de dahil ediliyor.
    *
    * @param listener     Karşılaştırmanın yapıldığı aramalar
    * @param listenerPart Karşılaştırmada aynı sürelere sahip olan aramalar
    * @param isMost       En uzun mu en kısa mı?
    * @param count        Aynı sürelere sahip aramaların sayısı
    * @return Yorum
    */
   CharSequence commentMostSpeakingInTheLog(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count);
   
   /**
    * Yorumu yapılan aramanın yönündeki arama kayıtları için yapılan karşılaştırmanın yorumu.
    * Bu yoruma aynı konuşma sürelerine sahip kişiler de dahil ediliyor.
    *
    * @param listener     Karşılaştırmanın yapıldığı aramalar
    * @param listenerPart Karşılaştırmada aynı sürelere sahip olan aramalar
    * @param isMost       En uzun mu en kısa mı?
    * @param count        Aynı sürelere sahip aramaların sayısı
    * @return Yorum
    */
   CharSequence commentMostSpeakingInDirection(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count);
   
   /**
    * Yorumu yapılan aramanın sahibine ait arama kayıtları için yapılan karşılaştırmanın yorumu.
    * Bu yoruma aynı konuşma sürelerine sahip aramalar da dahil ediliyor.
    *
    * @param listener     Karşılaştırmanın yapıldığı aramalar
    * @param listenerPart Karşılaştırmada aynı sürelere sahip olan aramalar
    * @param isMost       En uzun mu en kısa mı?
    * @param count        Aynı sürelere sahip aramaların sayısı
    * @return Yorum
    */
   CharSequence commentMostSpeakingForPersonInTheLog(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count);
   
   /**
    * Yorumu yapılan aramanın yönüne ve sahibine ait arama kayıtları için yapılan karşılaştırmanın yorumu.
    * Bu yoruma aynı konuşma sürelerine sahip aramalar da dahil ediliyor.
    *
    * @param listener     Karşılaştırmanın yapıldığı aramalar
    * @param listenerPart Karşılaştırmada aynı sürelere sahip olan aramalar
    * @param isMost       En uzun mu en kısa mı?
    * @param count        Aynı sürelere sahip aramaların sayısı
    * @return Yorum
    */
   CharSequence commentMostSpeakingForPersonInDirection(View.OnClickListener listener, View.OnClickListener listenerPart, boolean isMost, int count);
   
   /**
    * Tüm arama kayıtları için yapılan karşılaştırmanın yorumu.
    *
    * @param listener Belirlenen aramaları göstermek için
    * @param isMost   En uzun veya en kısa
    * @return Yarum
    */
   default CharSequence commentMostSpeakingInTheLog(View.OnClickListener listener, boolean isMost){
      
      return new Spanner("Bu arama, tüm kayıtlar içinde ")
            .append(commentMostSpeaking(isMost, listener));
   }
   
   /**
    * En uzun ya da en kısa konuşma.
    *
    * @param isMost   En uzun ya da en kısa
    * @param listener Belirlenen aramaları göstermek için
    * @return Yorum cümle
    */
   default CharSequence commentMostSpeaking(boolean isMost, View.OnClickListener listener){
      
      String speak = Stringx.format("en %s konuştuğun", isMost ? "uzun" : "kısa");
      
      return new Spanner()
            .append(Stringx.format("%s", speak), getLinkStyles(listener))
            .append(" arama. ");
   }
   
   /**
    * Aynı süreye sahip aramaların ifadesi.
    * Eğer yorumlanmakta olan kaydın süresiyle aynı olan başka bir kayıt yoksa bu method çağrılmaz.
    *
    * @param listenerPart Aynı süreye sahip aramaları göstermek için
    * @param count        Aynı süreye sahip aramaların sayısı
    * @return Yorum
    */
   default CharSequence commentSameSpeakingDurationCount(View.OnClickListener listenerPart, int count){
      
      return new Spanner("Ancak aynı konuşma süresine sahip ")
            .append(Stringx.format("%d arama kaydı", count), getLinkStyles(listenerPart))
            .append(" daha var. ");
   }
   
   /**
    * Konuşmanın olmadığını ifade eden yorum.
    * Bu yorum sadece giden aramalar için geçerli.
    *
    * @return Yorum
    */
   default CharSequence commentNoSpeaking(){
      
      return null;
   }
   
   /**
    * Konuşma süresini ifade eden yorum.
    *
    * @param speakingDuration Konuşma süresi
    * @return Yorum
    */
   default CharSequence commentSpeakingDuration(long speakingDuration){
      
      if(speakingDuration == 0L) return null;
      
      return new Spanner("Konuşma süresi ")
            .append(Stringx.format("%s", Time.getDuration(speakingDuration)), getDurationStyles())
            .append(". ");
   }
   
   /**
    * Konuşma ortalamasına göre yorum.
    *
    * @param speakingDuration Konuşma süresi (milisaniye)
    * @param average          Ortalama (saniye)
    * @return Yorum
    */
   default CharSequence commentSpeakingAverage(long speakingDuration, double average){
      
      boolean   isLong    = ((double) speakingDuration / 1000) > average;
      final int oneMinute = 60;// :)
      
      return new Spanner("Genel konuşma ortalaman ")
            .append(Stringx.format("%.1f dakika", average / oneMinute), Spans.bold())
            .append(Stringx.format(". Bu ortalamaya göre bu aramada %s konuştuğun söylenebilir. ", isLong ? "uzun" : "kısa"));
   }
   
   /**
    * Konuşma süresine göre yapılan karşılaştırma sonucunda verilen numaraya göre yorum yap.
    *
    * @param listener dinleyici
    * @param rank     sıralama
    * @param isMost   uzun | kısa
    * @return yorum
    */
   default CharSequence commentSpeakingDurationRank(View.OnClickListener listener, int rank, boolean isMost){
      
      String most = isMost ? "en uzun konuştuğun" : "en kısa konuştuğun";
      
      return new Spanner("Bu arama ")
            .append(Stringx.format("%s", most), getLinkStyles(listener))
            .append(Stringx.format(" aramalar listesinde %d. sırada. ", rank));
   }
}
