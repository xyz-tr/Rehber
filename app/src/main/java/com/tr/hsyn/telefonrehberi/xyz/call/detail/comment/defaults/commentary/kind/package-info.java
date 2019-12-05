/**
 * <h1>package kind</h1>
 *
 * <p>
 *    Yorumcular arama kayıtlarını yorumlarken
 *    istedikleri noktaya yorum yaparlar.
 *    Farklı yorumcular farklı noktalara yorum yapabilir.
 *    Birinin yorumlamadığı noktayı diğeri yorumlayabilir.
 *    Veya aynı konuyu farklı şekilde işleyebilirler.
 *    
 * <p>
 *    Uygulamada varsayılan bir yorumcu mevcut. ({@link com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator})
 *    Yorumcu, yorum yapılacak noktaları belirler ve
 *    yorum için gereken parametreleri bir arayüz ile bildirir.
 *    Ancak yorumun nasıl olacağını, yani yorumun içeriğini bildirmez.
 *    İçeriği <b>ruh haline</b> bırakır.
 *    Yorum yapma anı geldiğinde o anki ruh haline göre ({@linkplain com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.mood.Mood}) içeriği 
 *    bakkal amcadan alır.
 *    
 *    
 * @author hsyn 2019-12-01 13:21:28
 */
package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind;