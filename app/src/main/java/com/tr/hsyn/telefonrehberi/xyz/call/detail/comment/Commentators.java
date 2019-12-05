package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment;

import androidx.annotation.IntDef;

import com.google.common.base.Preconditions;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.paperdb.Paper;

//@off
/**
 * <h1>Commentators</h1>
 * 
 * <p>
 *    Yorumcu çeşitleri.
 *
 * @author hsyn
 * @date 15-04-2019
 */
//@on
public enum Commentators{
   ;
   
   /**
    * Varsayılan yorumcu
    */
   public static final int COMMENTATOR_TYPE_DEFAULT  = 0;
   /**
    * Farklı bir yorumcu (örnek olsun diye)
    */
   public static final int COMMENTATOR_TYPE_CUSTOM_1 = 1;
   
   private static final String PREF_COMMENTATOR_TYPE = "type_book";
   private static final String KEY_TYPE              = "type_commentator";
   private static final String KEY_TYPES             = "types_commentator";
   
   /**
    * Yorumcuyu değiştir.
    *
    * @param commentatorType yorumcunun türünün id kodu.
    */
   public static void changeCommentator(@Type int commentatorType){
      
      Collection<Integer> typeIds = getCommentators().keySet();
      
      if(!typeIds.contains(commentatorType))
         throw new RuntimeException(Stringx.format("Kayıtlı olmayan yorumcu aktif edilemez - id : %d", commentatorType));
      
      Paper.book(PREF_COMMENTATOR_TYPE).write(KEY_TYPE, commentatorType);
   }
   
   /**
    * Kayıtlı tüm yorumcu türlerini döndürür.
    *
    * @return yorumcu türleri
    */
   public static Map<Integer, String> getCommentators(){
      
      Map<Integer, String> registeredTypes = Paper.book(PREF_COMMENTATOR_TYPE).read(KEY_TYPES, new HashMap<>());
      
      registeredTypes.put(COMMENTATOR_TYPE_DEFAULT, "Varsayılan Yorumcu");
      
      return registeredTypes;
   }
   
   /**
    * Yeni bir yorumcu türü ekle.
    * Verilecek id değeri ile önceden kayıtlı bir yorumcu olmamalı.
    * Bu yüzden önce tüm yorumcu id'lerini al.
    *
    * @param id          id
    * @param description yorumcunun kısa bir açıklaması
    * @return kayıt başarılı ise {@code true}
    */
   public static boolean addCommentator(int id, String description){
      
      Preconditions.checkArgument(description == null || description.trim().isEmpty(), "Eklenecek olan yorumcu için boş bir açıklama kabul edilmez");
      
      Map<Integer, String> types = getCommentators();
      
      Preconditions.checkArgument(types.get(id) == null, "Aynı id değeri ile yorumcu eklenemez : %d", id);
      
      types.put(id, description);
      
      updateCommentators(types);
      return true;
   }
   
   /**
    * Yorumcu türleri listesini yenisiyle değiştir.
    *
    * @param types yeni türler
    */
   private static void updateCommentators(Map<Integer, String> types){
      
      Paper.book(PREF_COMMENTATOR_TYPE).write(KEY_TYPES, types);
   }
   
   /**
    * Geçerli olan yorumcunun türünü döndürür.
    *
    * @return Yorumcunun türü
    */
   public static int getCommentator(){
      
      return Paper.book(PREF_COMMENTATOR_TYPE).read(KEY_TYPE, COMMENTATOR_TYPE_DEFAULT);
   }
   
   @Retention(RetentionPolicy.SOURCE)
   @IntDef({COMMENTATOR_TYPE_DEFAULT})
   public @interface Type{}
   
}
