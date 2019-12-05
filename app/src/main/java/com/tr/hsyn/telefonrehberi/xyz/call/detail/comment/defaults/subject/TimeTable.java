package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject;


/**
 * <h1></h1>
 * 
 * <p>
 *    Zamansal konu.
 * 
 * @author hsyn 2019-12-02 11:13:48
 */
public interface TimeTable extends ISubject{
   
   /**
    * Zamana göre yorumla
    *
    * @return yorum
    */
   CharSequence commentateTimeTable();
}
