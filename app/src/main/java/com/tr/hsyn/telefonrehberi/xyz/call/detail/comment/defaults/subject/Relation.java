package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject;

/**
 * <h1>Relation</h1>
 * 
 * <p>
 *    İlişkisel yorum yapacak yorumcular için ilişkisel konu bildirimi.
 * 
 * @author hsyn 2019-12-02 11:10:35
 */
public interface Relation extends ISubject{
   
   /**
    * Aramanın başka aramalarla olan ilişkisini yorumla.
    */
   CharSequence commentateRelation();
}
