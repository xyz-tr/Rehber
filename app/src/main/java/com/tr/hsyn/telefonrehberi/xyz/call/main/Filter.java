package com.tr.hsyn.telefonrehberi.xyz.call.main;

import com.tr.hsyn.telefonrehberi.xyz.call.Type;


/**
 * <h1>Filter</h1>
 * 
 * <p>
 *    Arama kayıtları için filtreleme türleri.
 * 
 * @author hsyn 2019-12-03 14:06:12
 */
public enum Filter{
   ;
   
   /**
    * Tüm aramalar
    */
   public static final int ALL_CALLS = 0;
   
   public static final int INCOMMING = Type.INCOMMING;
   public static final int OUTGOING  = Type.OUTGOING;
   public static final int MISSED    = Type.MISSED;
   public static final int REJECTED  = Type.REJECTED;
   public static final int BLOCKED   = Type.BLOCKED;
   
   /**
    * Telefonun standart arama kaydı uygulamasından silinen aramalar
    */
   public static final int DELETED = 7;
   
   /**
    * En çok aranalar
    */
   public static final int MOST_OUTGOING_CALLS = 8; //8
   
   /**
    * En çok arayanlar
    */
   public static final int MOST_INCOMMING_CALLS = 9; //9
   
   /**
    * En çok çağrı atanlar
    */
   public static final int MOST_MISSED_CALLS = 10; //10
   
   /**
    * En çok reddedilenler
    */
   public static final int MOST_REJECTED_CALLS = 11; //11
   
   /**
    * En çok konuşanlar (Gelen aramalardaki konuşma süresine)
    */
   public static final int MOST_SPEAKING = 12;
   
   /**
    * En çok konuştukların (Giden aramalardaki konuşma süresine)
    */
   public static final int MOST_TALKING = 13;
   
   /**
    * En çok konuştukların
    */
   public static final int MOST_DURATION_INCOMMING_CALLS_TOTAL = 14; //14
   
   /**
    * En çok konuşanlar
    */
   public static final int MOST_DURATION_OUTGOING_CALLS_TOTAL = 15; //15
   
   /**
    * En kısa çağrı atanlar
    */
   public static final int QUICKEST_MISSED_CALLS = 16; //16
   
   /**
    * En uzun çağrı atanlar
    */
   public static final int LONGEST_MISSED_CALLS = 17; //17
   
   /**
    * En çabuk cevap verilenler
    */
   public static final int QUICKEST_ANSWERED_CALLS = 18; //18
   
   /**
    * En geç cevap verilenler
    */
   public static final int LONGEST_ANSWERED_CALLS = 19; //19
   
   /**
    * En geç cevap verenler
    */
   public static final int LONGEST_MAKE_ANSWERED_CALLS = 20; //20
   
   /**
    * En çabuk cevap verenler
    */
   public static final int QUICKEST_MAKE_ANSWERED_CALLS = 21; //21
   
   /**
    * En çabuk reddedilenler
    */
   public static final int QUICKEST_REJECTED_CALLS = 22;
   
   /**
    * En geç reddedilenler
    */
   public static final int LONGEST_REJECTED_CALLS = 23;
   
   public static final int MOST_BLOCKED_CALLS = 24;
   
}
