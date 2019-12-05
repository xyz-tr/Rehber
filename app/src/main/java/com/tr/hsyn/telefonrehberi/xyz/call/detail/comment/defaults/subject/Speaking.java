package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject;


/**
 * <h1>Speaking</h1>
 * 
 * <p>
 *    Konuşma konusu.
 * 
 * @author hsyn 2019-11-25 12:40:54
 */
public interface Speaking extends ISubject{
   
   /**
    * Konuşma süresini yorumla.
    * 
    * @return yorum
    */
   CharSequence commentateSpeakingDuration();
   
   /**
    * Konuşma süresinin uzunluğunu yorumla.
    * 
    * @return yorum
    */
   CharSequence commentateMostSpeaking();
   
   
}
