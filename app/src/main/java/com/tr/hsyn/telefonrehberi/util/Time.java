package com.tr.hsyn.telefonrehberi.util;


import androidx.annotation.NonNull;

import java.util.Date;
import java.util.Locale;

import lombok.Getter;


public class Time{
   
   public static final long oneMinute = 60000L; //my favor//
   public static final long oneHour   = oneMinute * 60L;
   public static final long oneDay    = oneHour * 24L;
   public static final long oneWeek   = oneDay * 7L;
   public static final long oneMounth = oneWeek * 4L;
   public static final long oneYear   = oneMounth * 12L;
   
   private         String dayNight;
   private         String whatTimeIsIt;
   @Getter private String mounth;
   private         String hour;
   private         String minute;
   @Getter private String dayName;
   @Getter private String dayValue;
   @Getter private String year;
   @Getter private String mounthName;
   @Getter private String mounthValue;
   
   public Time(long date){
      
      this(new Date(date));
   }
   
   public Time(Date date){
   
      dayName     = String.format("%tA", date);
      mounth      = String.format(new Locale("tr"), "%te %<tB", date);
      minute      = String.format("%tM", date);
      hour        = String.format("%tH", date);
      year        = String.format("%tY", date);
      mounthName  = String.format(new Locale("tr"), "%tB", date);
      mounthValue  = String.format(new Locale("tr"), "%tm", date);
      dayValue = String.format(new Locale("tr"), "%te", date);
      
      dayNight     = whatIsDayNight(Integer.parseInt(hour), Integer.parseInt(minute));
      whatTimeIsIt = whatIsOClock();
   }
   
   private String whatIsDayNight(int hour, int minute){
      
      if(hour >= 5 && hour <= 7){ return "Sabahın körü"; }
      if(hour >= 8 && hour <= 10){ return "Sabah"; }
      if(hour == 11 && minute <= 40){ return "Öğlene doğru"; }
      if(hour >= 11 && hour < 13){ return "Öğlen"; }
      if(hour >= 13 && hour < 17){ return "Öğleden sonra"; }
      if(hour == 17){ return "Akşam üstü"; }
      if(hour >= 18 && hour < 22){ return "Akşam"; }
      if(hour >= 22 && hour <= 23){ return "Gece"; }
      if(hour == 0){ return "Gece yarısı"; }
      if(hour >= 1 && hour <= 3){ return "Gece yarısından sonra"; }
      if(hour >= 3 && hour < 5){ return "Sabaha karşı"; }
      
      return "";
   }
   
   private String whatIsOClock(){
      
      return String.format("%s %s %s %s saat %s:%s", mounth, year, dayName, dayNight, hour, minute);
   }
   
   /**
    * @return '12 Nisan 1981 Perşembe 00:31:33\n' formatında string
    */
   public static String dateStamp(){
      
      return getDate(new Date()) + "\n";
   }
   
   /**
    * Verilen zamanı döndür.
    *
    * @param date Zaman
    * @return '12 Nisan 1981 Perşembe 00:31:33' formatında string
    */
   public static String getDate(Date date){
      
      return getDate(date.getTime());
   }
   
   /**
    * Verilen zamanı döndür.
    *
    * @param milis Zaman
    * @return '12 Nisan 1981 Perşembe 00:31:33' formatında string
    */
   public static String getDate(long milis){
      
      return String.format(new Locale("tr"), "%te %<tB %<tY %<tA %<tH:%<tM:%<tS", new Date(milis));
   }
   
   /**
    * Şimdiki zamanı döndür.
    * 
    * @return '14 Kasım 2019 Perşembe Gece saat 22:03' formatında
    */
   public static String whatTimeIsIt(){
      
      return new Time(new Date()).whatTimeIsIt;
   }
   
   /**
    * Verilen tarihin saatini döndür. 15:23:45
    *
    * @param time Tarih
    * @return 'saat:dakika:saniye' formatında
    */
   public static String getTime(long time){
      
      return String.format("%tH:%<tM:%<tS", new Date(time));
   }
   
   /**
    * Verilen zamanın şimdiye oranla ne kadar önce olduğunu döndür.<br>
    * <br>
    * Örnekler<br>
    * --------------------------------<br>
    * 3 gün önce<br>
    * 2 hafta önce<br>
    * 3 ay önce<br>
    * 6 saat önce<br>
    * 15 dakika önce<br>
    * --------------------------------<br>
    *
    * @param milis zaman
    * @return Zamanın ne kadar önce olduğunu bildiren string
    */
   @NonNull
   public static String getDateHistory(long milis){
      
      //Time time    = new Time(new Date(milis));
      long now     = getTime();
      long difLong = now - milis;
      long difDay  = getAllDay() - getAllDay(milis);
      
      if(difDay == 0){
         
         if(difLong < oneMinute){
            
            return "bir dakika önce";
         }
         else if(difLong < oneHour){
            
            return format("%d dakika önce", difLong / oneMinute);
         }
         
         return format("%d saat önce", difLong / oneHour);
      }
      
      if(difDay < 7){
         
         if(difDay == 1){
            
            return "dün";
         }
         else if(difDay == 2){
            
            return "evvelsi gün";
         }
      }
      
      if(difDay == 7){
         
         return "geçen hafta";
      }
      
      if(difDay < 14){
         
         return format("%d gün önce", difDay);
      }
      
      if(difDay < 50){
         
         return format("%d hafta önce", difDay / 7);
      }
      
      if(difDay < (365 * 2)){
         
         return format("%d ay önce", difDay / 30);
      }
      
      
      return format("%d yıl önce", difDay / 365);
   }
   
   /**
    * @return zamanın başlangıcından şimdiye kadar geçen milisaniye sayısı.
    */
   public static long getTime(){
      
      return System.currentTimeMillis();
   }
   
   /**
    * Zamanın başlangıcından şimdiye kadar tam geçen gün sayısı.
    *
    * @return Gün sayısı
    */
   public static long getAllDay(){
      
      return System.currentTimeMillis() / oneDay;
   }
   
   /**
    * Verilen tarih için geçen tam gün sayısını döndür.
    *
    * @param date Tarih
    * @return Gün sayısı
    */
   private static long getAllDay(long date){
      
      return date / oneDay;
   }
   
   private static String format(String msg, Object... args){
      
      return String.format(new Locale("tr", "TR"), msg, args);
   }
   
   /**
    * Verilen milisaniyelerin ne kadar zaman ettiğini döndür.<br>
    * <br>
    * Önekler<br>
    * --------------------------------------------<br>
    * 3 gün 5 saat 40 dakika 20 saniye<br>
    * 2 hafta 1 gün 5 dakika 55 saniye<br>
    * 3 ay 1 hafta 3 saat 10 saniye<br>
    * 6 saat 22 dakika 30 saniye<br>
    * 15 dakika 44 saniye<br>
    * 3 yıl 5 ay 6 gün 10 saat 43 dakika 57 saniye<br>
    * --------------------------------------------<br>
    *
    * @param duration Süre
    * @return Sürenin ne kadar zaman ettiğini bildiren string
    */
   @NonNull
   public static String getDuration(long duration){
      
      final long oneSec = 1000L;
      
      if(duration < oneSec){
         
         return format("%d milisaniye", duration);
      }
      
      if(duration < oneMinute){
         
         return format("%d saniye", duration / oneSec);
      }
      
      if(duration < oneHour){
         
         return format("%d dakika %s", duration / oneMinute, getDuration(duration % oneMinute));
      }
      
      if(duration < oneDay){
         
         return format("%d saat %s", duration / oneHour, getDuration(duration % oneHour));
      }
      
      if(duration < oneWeek){
         
         return format("%d gün %s", duration / oneDay, getDuration(duration % oneDay));
      }
      
      if(duration < oneMounth){
         
         return format("%d hafta %s", duration / oneWeek, getDuration(duration % oneWeek));
      }
      
      if(duration < oneYear){
         
         return format("%d ay %s", duration / oneMounth, getDuration(duration % oneMounth));
      }
      
      return format("%d yıl %s", duration / oneYear, getDuration(duration % oneYear));
      
   }
   
   /**
    * Zamanın başlangıcından verilen zamana kadar olan zaman içinde rasgele bir zaman döndür.
    *
    * @param end Zamanın sınır noktası
    * @return Verilen sınır noktasından önce bir zaman
    */
   public static long getRandomDate(long end){
      
      return new MRandom().nextLong(end);
   }
   
   /**
    * Verilen iki zaman arasında rastgele bir zaman döndür.
    *
    * @param start Zamanın başlangıç noktası
    * @param end   Zamanın bitiş noktası
    * @return İki zaman arasında bir zaman
    */
   public static long getRandomDate(long start, long end){
      
      return new MRandom().nextLong(start, end);
   }
   
   /**
    * Verilen zamanı döndür.
    *
    * @param milis Zaman
    * @return '12 Nisan 1981 Perşembe 00:31:33' formatında string
    */
   public static String getDate(String milis){
      
      try{
         
         return getDate(Long.parseLong(milis));
      }
      catch(Exception ignore){}
      
      
      return milis;
   }
   
   /**
    * Verilen zamanı döndür.
    *
    * @param milis Zaman
    * @return '12 Nisan 1981' formatında string
    */
   public static String getShortDate(long milis){
      
      return String.format(new Locale("tr"), "%te %<tB %<tY", new Date(milis));
   }
   
   /**
    * Verilen zamanı döndür.
    * 
    * @param milis zaman
    * @return '12 Nisan 1981 23:50:15' formatında string
    */
   public static String getShortDateShortTime(long milis){
      
      return getShortDate(milis) + " " + getTime(milis);
   }
   
   /**
    * Verilen zamanı döndür.
    * 
    * @param milis zaman
    * @return 22:16:49:150000000 formatında
    */
   public static String getTimeWithNano(long milis){
      
      return String.format(new Locale("tr"), "%tH:%<tM:%<tS:%<tN", new Date(milis));
   }
   
   /**
    * Verilen zamanı döndür.
    * 
    * @param milis zaman ms
    * @return 12.04.1981 00:15:10 formatında
    */
   public static String getDateTime(long milis){
      
      return String.format(new Locale("tr"), "%td.%<tm.%<tY %<tH:%<tM:%<tS", new Date(milis));
   }
   
   /**
    * Zamanı döndür.
    *
    * @return '12 Nisan 1981 Perşembe' formatında string
    */
   public String getDateStr(){
      
      return format("%s %s %s", mounth, year, dayName);
   }
   
   /**
    * Saati döndür.
    *
    * @return 'Öğleden sonra saat 14:20' formatında string
    */
   public String getTimeStr(){
      
      return format("%s saat %s:%s", dayNight, hour, minute);
   }
   
   private String getWhatTimeIsIt(){
      
      return whatTimeIsIt;
   }
   
   /**
    * Zamanın başlangıcından şimdiye kadar olan zaman içinde rastgele bir zaman.
    *
    * @return Bir zaman
    */
   public static long getRandomDate(){
      
      return new MRandom().nextLong(getTime());
   }
}
