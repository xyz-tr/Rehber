package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.incomming;


import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Relation;


/**
 * <h1>IncommingCommentStore</h1>
 *
 * <b>Gelen arama yorumcusu.</b><br>
 *
 * @author hsyn 2019-11-27 22:30:47
 */
//@off
public interface IIncomming extends  IncommingSpeaking,
                                    IncommingResponse,
                                    IncommingCount,
                                    IncommingTimeTable,
                                    IncommingRinging,
                                    IncommingFrequency,
                                    Relation{
   
   
}
