package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.mood;

import androidx.annotation.IntDef;

import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.CallType;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import io.paperdb.Paper;

//@off
/**
 * <p>
 *    Yorumcunun ruh hali.<br>
 *    Bu hali kullanıcı belirler.
 *    Varsayılan ruh hali {@link #DEFAULT} değeridir.
 *    
 * <p>
 *    Bu sınıf ruh halini öğrenmek ve değiştirmek için metotlar sunar.
 *
 * @author hsyn
 * @date 15-04-2019
 */
//@on
public enum Moods{
   ;
   
   /**
    * Varsayılan ruh hali
    */
   @Mood public static final int DEFAULT = 0;
   /**
    * Mutlu ruh hali
    */
   @Mood public static final int HAPPY   = 1;
   
   private static final String KEY_MOOD = "mood";
   private static final String BOOK     = "book_mood";
   
   public static int getMood(@CallType int callType){
      
      if(!Type.isValidCallType(callType))
         throw new RuntimeException(Stringx.format("Geçersiz arama türü : %d - %s", callType, Type.getTypeStr(callType)));
      
      return Paper.book(BOOK).read(KEY_MOOD, 0);
   }
   
   public static void saveMood(@Mood int mood){
      
      if(isInvalidMood(mood))
         throw new RuntimeException(Stringx.format("Geçersiz mood : %d", mood));
      
      Paper.book(BOOK).write(KEY_MOOD, mood);
   }
   
   public static boolean isInvalidMood(int mood){
      
      boolean valid;
      
      switch(mood){
         
         case 0:
         case 1:
            valid = false;
            break;
         
         default: valid = true;
      }
      
      return valid;
   }
   
   @Retention(RetentionPolicy.SOURCE)
   @IntDef({DEFAULT, HAPPY})
   public @interface Mood{}
   
}
