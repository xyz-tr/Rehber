package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.xyz.ICommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.blocked.IBlocked;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.incomming.IIncomming;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.missed.IMissed;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.outgoing.IOutgoing;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.rejected.IRejected;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.DefaultIncomming;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.DefaultMissed;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.DefaultOutgoing;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.DefaultRejected;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mode.secondperson.DefaultBlocked;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mood.Moods;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.Blocked;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.Incomming;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.Missed;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.Outgoing;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.type.Rejected;

import java.lang.ref.WeakReference;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.Commentators.COMMENTATOR_TYPE_CUSTOM_1;
import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.Commentators.COMMENTATOR_TYPE_DEFAULT;

//@off
/**
 * <h1>Comments</h1>
 *
 * <p>
 *    İstenen türe göre yorumcunun ve yorum deposunun seçilmesi için metotlar sunar.
 *
 * @author hsyn
 * @date 15-04-2019
 */
//@on
public enum Comments{
   ;
   
   private static final String INVALID_MOOOD = "Geçersiz mod : ";
   private static final String INVALID_TYPE  = "Geçersiz tür : ";
   
   /**
    * Yorumcunun, yorumları alacağı nesneyi oluşturur.
    *
    * @param type        Yorumun yapılacağı arama türü. {@link Call#getType()} metodundan dönen tür.
    * @param commentMood Yorumun modu. {@linkplain Moods} sınıfına bak. Geçersiz bir mod belirtilirse,
    *                    aramanın türüne uygun varsayılan yorum türü kullanılır.
    * @return {@linkplain IComment} Yorumların alınacağı nesne
    */
   @NonNull
   public static IComment createStore(int type, @Moods.Mood int commentMood){
      
      if(Moods.isInvalidMood(commentMood)){
         
         commentMood = Moods.DEFAULT;
      }
      
      IComment commentStore;
      
      //@off
      switch(type){
         
         case Type.OUTGOING        :
         case Type.OUTGOING_WIFI   : commentStore = getOutgoingStore(commentMood);   break;
         case Type.INCOMMING       : 
         case Type.INCOMMING_WIFI  : commentStore = getIncommingStore(commentMood);  break;
         case Type.MISSED          : commentStore = getMissedStore(commentMood);     break;
         case Type.REJECTED        : commentStore = getRejectedStore(commentMood);   break;
         case Type.BLOCKED         : commentStore = getBlockedStore(commentMood);    break;
            
         default: throw new IllegalArgumentException(INVALID_TYPE + type);
      }
      
      //@on
      return commentStore;
   }
   
   /**
    * @param commentMood Yorum modu
    * @return Giden aramalar için yorumlar
    */
   @NonNull
   private static IOutgoing getOutgoingStore(@Moods.Mood int commentMood){
      
      switch(commentMood){
         
         case Moods.DEFAULT: return new DefaultOutgoing();
         case Moods.HAPPY:
         
         default: throw new IllegalArgumentException(INVALID_MOOOD + commentMood);
      }
   }
   
   @NonNull
   private static IIncomming getIncommingStore(@Moods.Mood int commentMood){
      
      /*
       *
       *
       *
       * */
      
      
      switch(commentMood){
         
         case Moods.DEFAULT: return new DefaultIncomming();
         case Moods.HAPPY:
         
         default: throw new IllegalArgumentException(INVALID_MOOOD + commentMood);
      }
   }
   
   @NonNull
   private static IMissed getMissedStore(@Moods.Mood int commentMood){
      
      switch(commentMood){
         
         case Moods.DEFAULT: return new DefaultMissed();
         case Moods.HAPPY:
         
         default: throw new IllegalArgumentException(INVALID_MOOOD + commentMood);
      }
   }
   
   @NonNull
   private static IRejected getRejectedStore(@Moods.Mood int commentMood){
      
      switch(commentMood){
         
         case Moods.DEFAULT: return new DefaultRejected();
         case Moods.HAPPY:
         
         default: throw new IllegalArgumentException(INVALID_MOOOD + commentMood);
      }
   }
   
   private static IBlocked getBlockedStore(@Moods.Mood int commentMood){
      
      switch(commentMood){
         
         case Moods.DEFAULT: return new DefaultBlocked();
         case Moods.HAPPY:
         
         default: throw new IllegalArgumentException(INVALID_MOOOD + commentMood);
      }
   }
   
   /**
    * Verilen türe göre, yorumu yapacak olan yorumcuyu oluşturur.
    *
    * @param resources    Activity.
    * @param commentStore Yorumcunun yorumları alacağı dükkan.
    * @param type         Yorumcunun türü
    * @param direction    Yorum yapılacak aramanın türü
    * @return Yorumcu
    */
   public static ICommentator<ICall> createCommentator(WeakReference<Activity> resources, IComment commentStore, int type, int direction){
      
      ICommentator<ICall> callCommentator;
      
      switch(type){
         
         case COMMENTATOR_TYPE_DEFAULT: callCommentator = getDefaultCommentator(resources, commentStore, direction);
            break;
         case COMMENTATOR_TYPE_CUSTOM_1: callCommentator = getCustom1Commentator(resources, commentStore, direction);
            break;
         
         default: throw new IllegalArgumentException(INVALID_TYPE + type);
      }
      
      return callCommentator;
   }
   
   /**
    * Varsayılan yorumcuyu oluşturur.
    *
    * @param resources    Yorumlarda gösterilmek istenen aramaların dialog penceresi ile sunulması için gerekli olan activity.
    * @param commentStore Yorumcunun yorumları alacağı nesne.
    * @param direction    Yorum yapılacak aramanın türü
    * @return Yorumcu
    */
   @NonNull
   private static ICommentator<ICall> getDefaultCommentator(WeakReference<Activity> resources, IComment commentStore, final int direction){
      
      ICommentator<ICall> callCommentator;
      
      //@off
      switch(direction){
         
         case Type.INCOMMING      : 
         case Type.INCOMMING_WIFI : callCommentator = new Incomming( resources,  commentStore);break;
         case Type.OUTGOING       : 
         case Type.OUTGOING_WIFI  : callCommentator = new Outgoing(resources,  commentStore);break;
         case Type.MISSED         : callCommentator = new Missed(resources,  commentStore);break;
         case Type.REJECTED       : callCommentator = new Rejected( resources,  commentStore);break;
         case Type.BLOCKED        : callCommentator = new Blocked(resources,  commentStore);break;
         
         
         default: throw new IllegalArgumentException(INVALID_TYPE + direction);
      }
      
      //@on
      return callCommentator;
   }
   
   /**
    * Farklı bir yorumcu oluşturur.
    *
    * @param resources    Yorumlarda gösterilmek istenen aramaların dialog penceresi ile sunulması için gerekli olan activity.
    * @param commentStore Yorumcunun yorumları alacağı nesne.
    * @param direction    Yorum yapılacak aramanın türü
    * @return Yorumcu
    */
   @NonNull
   private static ICommentator<ICall> getCustom1Commentator(WeakReference<Activity> resources, IComment commentStore, final int direction){
      
      ICommentator<ICall> callCommentator;
      
      //@off
      switch(direction){
         
         case Type.INCOMMING: callCommentator = getDefaultCommentator( resources, commentStore, Type.INCOMMING);break;
         case Type.OUTGOING:  callCommentator = getDefaultCommentator( resources, commentStore, Type.OUTGOING);break;
         case Type.MISSED:    callCommentator = getDefaultCommentator( resources, commentStore, Type.MISSED);break;
         case Type.REJECTED:  callCommentator = getDefaultCommentator( resources, commentStore, Type.REJECTED);break;
         case Type.BLOCKED:   callCommentator = getDefaultCommentator( resources, commentStore, Type.BLOCKED);break;
         default: throw new IllegalArgumentException(INVALID_TYPE + direction);
      }
      
      //@on
      return callCommentator;
   }
   
   
}
