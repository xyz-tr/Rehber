package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject;


/**
 * <h1>Response</h1>
 * 
 * <p>
 *    Tepkisel konu.
 * 
 * @author hsyn 2019-11-27 13:07:59
 */
public interface Response extends ISubject{
   
   /**
    * Cevap verme süresini yorumla.
    *
    * @return yorum
    */
   CharSequence commentateResponse();
}
