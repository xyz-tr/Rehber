package com.tr.hsyn.telefonrehberi.xyz.call.detail.actor;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.xyz.ICommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.CallType;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.Commentators;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.Comments;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mood.Moods;

import java.lang.ref.WeakReference;

//@off
/**
 * <h1>Speaker</h1>
 * 
 * <p>
 *    Yorumcuyu seçer, yönlendirir. Programın sunucusu.
 *    Yorumcuya gerekli bilgileri verir ve yorum yapmasını ister.
 *
 * @author hsyn
 * @date 2019-4-15 15:13:20
 */
//@on
public interface ISpeaker{
   
   
   /**
    * Yorumcunun yorumlarını alır.
    * Varsayılan ayarlar kullanılacaksa, dışarıdan çağrılacak tek fonksiyon budur ve yeterlidir.
    *
    * @param call                  Yorumu yapılacak arama.
    * @param activityWeakReference Activity weak
    * @return Yorum
    */
   default CharSequence commentateCall(ICall call, WeakReference<Activity> activityWeakReference){
      
      if(activityWeakReference.get() == null) return null;
      
      ICommentator<ICall> commentator = selectCommentator(call, activityWeakReference);
      
      return callCommentator(commentator, call);
   }
   
   /**
    * Yorumcuya yorumu yaptırır.
    *
    * @param commentator Yorumcu
    * @return Yorum
    */
   default CharSequence callCommentator(@NonNull ICommentator<ICall> commentator, ICall call){
      
      return commentator.commentate(call);
   }
   
   /**
    * Yorumcuyu seçer.
    * Yorumlar başlamadan önce yorumcu seçilir.
    * Yorumcular dosyada kayıtlı olmalı.
    * Kayıtlı bir yorumcu yoksa varsayılan yorumcu otomatik olarak iş başına geçer.
    * Normalde dışarıdan çağrılması gereksizdir ama işleyişi değiştirmek isteyen override edebilir.
    * Burada değiştirebileceğin şey yorumcunun seçimi.
    * Eğer farklı bir yorumcu seçimi yapmak istiyorsan buradan yapabilirsin.
    * Ancak bu pek beklenen bir durum değil.
    * Beklenen durum, değişik yorum türleri kaydedip onları seçmek.
    *
    * @param call      Yorum yapılacak arama
    * @param resources Activity
    * @return Yorumcu
    */
   default ICommentator<ICall> selectCommentator(ICall call, WeakReference<Activity> resources){
      
      int      type         = call.getType();
      IComment commentStore = selectCommetStore(type);
      
      return Comments.createCommentator(resources, commentStore, Commentators.getCommentator(), type);
   }
   
   /**
    * Yorum türünü seçer.
    * Beklenen durum yorumcuları değil yorum türlerini çoğaltmak.
    * Her yorumcunun kendine has yorumu olmak zorunda değil.
    * Her yorumcu istediği yorum türüyle yorum yapabilir.
    * Yani yeni bir yorumcu, var olan yorum türlerini kullanabilir.
    * Ama farklı bir yorumcu oluşturup, bu yorumcuya özel yorum türleri de yazılabilir.
    * Yorumcu da, yorum türleri de dosyada kayıtlı olmalı.
    * Kayıtlı bir tür yoksa varsayılan yorumcu ve varsayılan yorumlar seçilir.
    *
    * @param type      Yorum türü
    * @return Yorumları içeren nesne
    * @see Moods
    */
   default IComment selectCommetStore(@CallType int type){
      
      int mood = Moods.getMood(type);
      
      return Comments.createStore(type, mood);
   }
   
}
