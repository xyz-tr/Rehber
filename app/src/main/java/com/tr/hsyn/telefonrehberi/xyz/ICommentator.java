package com.tr.hsyn.telefonrehberi.xyz;


/**
 * <h1>ICommentator</h1>
 * <b>Yorumcu.</b>
 * 
 * 
 * @param <T> Yorumlanacak konunun türü
 * 
 * @author hsyn 2019-11-25 12:35:42
 */
@FunctionalInterface
public interface ICommentator<T>{
   
   /**
    * 
    * @param subject yorumlanacak konu
    * @return yorum
    */
   CharSequence commentate(T subject);
   
}
