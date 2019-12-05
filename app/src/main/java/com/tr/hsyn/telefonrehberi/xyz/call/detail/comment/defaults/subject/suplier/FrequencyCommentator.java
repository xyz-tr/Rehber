package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Frequency;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.val;

//@off
/**
 * <h1>FrequencyCommentator</h1>
 * 
 * <p>
 *    Arama sıklığı konusunda varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:12:16
 */
//@on
public interface FrequencyCommentator extends Frequency, ICommentator{
   
   @Override
   default CharSequence commentateFrequency(){
      
      //Kişiye ait arama kaydı sayısı taban limiti. Aynı zaman karşılaştırma yapmak için gereken en az gün sayısı
      final int limit = 14;
      
      List<ICall> calls = getHistory().getCalls();
      
      //limitin altında ise bulaşma
      if(calls.size() < limit) return null;
      
      //Büyükten küçüğe
      Collections.sort(calls);//Sınıf Comparable arayüzü için tarihi kullanıyor. Önce büyükler
      
      //ilk ve son kaydı bul
      //arasında en az 10 gün varsa sıklığını araştır
      
      //En taze kayıt
      val lastCall = calls.get(0);//Sıralama tarihe göre olduğu için ilk eleman son kayıttır.
      
      //En eski kayıt
      val firstCall = calls.get(calls.size() - 1);
      
      long estimatedTime = lastCall.getDate() - firstCall.getDate();
      
      //İlk ve son kayıt arasında kaç gün var?
      long day = TimeUnit.MILLISECONDS.toDays(estimatedTime);
      
      Logger.d("Karşılaştırma yapmak için gereken gün sayısı : %d%n" +
               "Bulunan gün sayısı                           : %d", limit, day);
      
      //Yazmaya başla
      Spanner comment      = new Spanner();
      val     commentStore = getFrequencyCommentStore();
      
      if(day >= limit){
         
         //'Bu kişiyle 3 Aylık bir görüşme geçmişiniz var'
         comment.append(commentStore.getEstimatedCallHistoryComment(estimatedTime));
         
         
      }
      
      return comment;
   }
   
   default com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Frequency getFrequencyCommentStore(){
      
      return (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Frequency) getCommentStore();
   }
   
}
