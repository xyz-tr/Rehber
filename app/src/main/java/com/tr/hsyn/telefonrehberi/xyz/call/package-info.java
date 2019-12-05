/**
 * <h1>&nbsp;package call</h1>
 * 
 * <p>
 *      &nbsp; Arama kayıtlarının ana dizini.<br>
 *    ̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅̅͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠ ͠
 *    
 * <p>
 *    Uygulama kodlarının en üst dizini {@linkplain com.tr.hsyn.telefonrehberi.xyz}.
 *    Buna kısaca {@code xyz} diyelim.<br>
 *    {@linkplain com.tr.hsyn.telefonrehberi.xyz.call} dizini ise 
 *    {@code xyz} dizininin altındaki ana dizinlerden biri.<br> 
 *    Buna da kısaca {@code call} dizini diyelim.<br>
 *    {@code call} dizini, arama kayıtları ile ilgili tüm varlıkları barındırır.<br>
 *       <br>
 *    <p>
 *       <b>Teoriye göre,</b> bu dizinin ({@code call}) altında kalan varlıkların
 *       çıkabilecekleri en üst dizin {@code call} dizini.<br>
 *       Yani burası onlar için <b>ROOT,</b>
 *       ve daha yukarı <u>çıkmamaları</u> gerek.
 *       Ancak bu dizinin üstünde {@code xyz} dizini var ve bu dizinde
 *       uygulama genelinde kullanılan bir çok yardımcı 
 *       araçlar sunmakta olan {@link com.tr.hsyn.telefonrehberi.util} dizini bulunuyor.
 *       Buna da kısaca {@code util} diyelim.
 *       
 *  <pre>
 *     
 *    com.tr.hsyn.telefonrehberi
 *                             |
 *                             | --> util
 *                             | --> xyz
 *                                     |
 *                                     | --> call
 *                                  
 *                                     
 *  </pre>
 *       
 *    <p>   
 *       Bu dizin ({@code util}) uygulamadan bağımsız kodlar sağladığı için 
 *       uygulama kodlarından ayrılması gerektiği düşüncesiyle 
 *       {@code xyz} dizinine girmedi ve 
 *       uygulamanın her yerinden erişim sağlanmakta.
 *       Yani uygulama genelinde <b>ROOT</b> görevi ihmal edilmekte.<br>
 *          
 *          
 *  <p>
 *     <b>Bir başka teoriye göre ise</b> dizinin içindeki varlıklar root dizininin üstüne erişebilirler,
 *     ancak dizinin içindeki varlıklara root dizini dışından <u>erişilmemesi</u> gerek.
 *     Şuanki programlama teknikleri açısından bu ikinci teori kulağa daha hoş geliyor.
 *     Çünkü birinci teoriyi uygulamak kesinlikle imkansız. Bunun en basit ve en bariz örneği
 *     uygulamaya dışarıdan eklenen kütüphaneler.
 *     Root dizini bir yana, dizinde bile olmayan bu kütüphanelerdeki varlıklar 
 *     uygulamanın herhangi bir yerinde ortaya çıkabiliyor.
 *     
 *    
 * 
 * @see com.tr.hsyn.telefonrehberi.util
 * @see com.tr.hsyn.telefonrehberi.xyz
 * @author hsyn 2019-12-03 15:53:53
 */
package com.tr.hsyn.telefonrehberi.xyz.call;