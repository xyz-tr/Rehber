package com.tr.hsyn.telefonrehberi.xyz.main.page;


/**
 * <h1>ShowMe</h1>
 * ViewPager içindeki sayfalara, aksiyona geçmek için uygun zamanın bildirilmesi gerek.
 * Uygun zamandan kasıt, sayfanın görünür duruma geçmesi demek.
 * Bilindiği gibi ViewPager içinde bir çok sayfa olabilir ve bu sayfalar görünür duruma geçmeden önce var olurlar.
 * Ancak bir sayfanın var olması, onun sahnede olduğu anlamına gelmez.
 * İşte bu arayüz sahne zamanının geldiğini sayfaya bildirecek ve o sayfa sahnede olduğunu bilerek işlem yapacak.
 * Bu arayüzü sayfalar uygular ve ViewPager'a sahip olan kişi ise sahne zamanını belirler.
 * 
 * @author hsyn 2019-11-02 10:49:52
 */
public interface ShowMe{
   
   /**
    * ViewPager üzerinde sayfalar sürekli değişebilir.
    * Bir sayfa var olduğu halde sahnede olmayabilir, yani görünür durumda olmayabilir.
    * Bu metod her iki durum için, yani sahnede olma ve sahne dışında olma durumlarını sayfaya bildirir.
    * 
    * @param isShowTime sahne zamanı ise {@code true}
    */
   void showTime(boolean isShowTime);
}
