package com.tr.hsyn.telefonrehberi.xyz.call.main.random;

import com.tr.hsyn.telefonrehberi.util.MRandom;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.contact.ISimpleContact;

import java.util.List;


/**
 * <h1>CallGenerator</h1>
 * <p>Rehberden rastgele kişiler seçip, bunlara rastgele arama kayıtları üretmek için.</p>
 *
 * @author hsyn
 */
public class CallGenerator implements ICallGenerator{
   
   private static final int SPEAK_OR_NOT = 2;
   private static final int NOT          = 0;
   private static final int                            SPEAKING_DURATION = 900;
   private static final int                            TYPES             = 12;
   private final        List<? extends ISimpleContact> contactSearchModels;
   private final        MRandom                        random            = new MRandom();
   private final        long                           dateEnd;
   private final        long                           dateStart;
   private final        List<Integer>                  callTypes;
   private final        boolean                        isRingtone;
   private final        boolean                        isShare;
   
   public CallGenerator(List<? extends ISimpleContact> contactSearchModels, List<Integer> callTypes, long start, long end, boolean ringtone, boolean isShare){
      
      this.contactSearchModels = contactSearchModels;
      this.callTypes           = callTypes;
      this.dateStart           = start;
      this.dateEnd             = end;
      isRingtone               = ringtone;
      this.isShare             = isShare;
   }
   
   @Override
   public final Call getCall(){
      
      ISimpleContact randomContact = contactSearchModels.get(random.nextInt(contactSearchModels.size()));
      
      long randomDate            = getRandomDate();
      int  randomType            = getRandomType();
      int  randomDuration        = getRandomDuration(randomType);
      long randomRingingDuration = isRingtone ? random.nextLong(60000L) : 0;
      
      
      Call phoneCall = new Call(String.valueOf(randomDate),
                                randomContact.getName(),
                                randomContact.getNumber(),
                                randomDate,
                                randomType,
                                randomDuration);
      
      phoneCall.setRingingDuration(randomRingingDuration);
      phoneCall.setSpeakingDuration(randomDuration * 1000L);
      
      if(isShare){
         
         phoneCall.setSharable(true);
      }
      
      return phoneCall;
   }
   
   private int getRandomDuration(int randomType){
      
      int randomDuration = random.nextInt(SPEAKING_DURATION);
      
      if(randomType == Type.INCOMMING || randomType == Type.OUTGOING){
         
         if(randomType == Type.OUTGOING && (random.nextInt(SPEAK_OR_NOT) == NOT)){
            
            randomDuration = 0;
         }
      }
      else{
         
         randomDuration = 0;
      }
      
      return randomDuration;
   }
   
   private int getRandomType(){
      
      int randomType = random.nextInt(TYPES);
      
      while(!callTypes.contains(randomType)){
         
         randomType = random.nextInt(TYPES);
      }
      
      return randomType;
   }
   
   private long getRandomDate(){
      
      long now        = System.currentTimeMillis();
      long randomDate = Time.getRandomDate(dateStart, dateEnd);
      
      while(randomDate >= now){
         
         randomDate = Time.getRandomDate(dateStart, dateEnd);
      }
      
      return randomDate;
   }
   
}
