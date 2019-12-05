package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.view.View;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.util.Rank;
import com.tr.hsyn.telefonrehberi.util.RankInfo;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.Group;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Count;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.MostCallListDialog;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.val;
import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.Commentator.COMPARE_SIZE;

//@off
/**
 * <h1>CountCommentator</h1>
 * 
 * <p>
 *    Sayısal yorumlar için varsayılan yorumları sağlar.
 * 
 * @author hsyn 2019-12-02 14:11:05
 */
//@on
public interface CountCommentator extends Count, ICommentator{
   
   
   /**
    * Geçerli arama kaydının yönünde gerçekleşen diğer aramalarla ilgili bilgiler.
    * Mesela geçerli arama bir cevapsız çağrı ise, bu kişinin başka kaç cevapsız çağrısı var?
    * Var mı yok mu?
    * Varsa göster.
    */
   @Override
   default CharSequence commentateCount(){
      
      List<ICall>  allCallsOfThisGuy            = getHistory().getCalls();
      List<ICall>  allCallsOfThisGuyInDirection = getHistory().getCalls(getCall().getType());
      final String callType                     = ICall.getTypeStr(getActivityReference().get(), getCall().getType());
      
      Spanner comment = new Spanner();
      
      val commentStore = getCountCommentStore();
      
      if(allCallsOfThisGuyInDirection.size() == 1){
         
         if(allCallsOfThisGuy.size() == 1){
            
            //* Bu kişiye ait tek arama kaydı
            comment.append(commentStore.thisIsOnlyOneCallComment(true));
            comment.append(lookOneCalls(true));
         }
         else{
            
            comment.append(commentStore.thisIsOnlyOneCallComment(false));
            //* Bu kayıttan başka kayıtlar da var ama bu kayıt bu yöndeki tek kaydı
            //? Bu durumda bu yönde sadece bir kaydı olanlara bak
            comment.append(lookOneCalls(false));
         }
      }
      else{
         
         final String title = Stringx.toTitle(callType) + "lar";
         Collections.sort(allCallsOfThisGuyInDirection);
         
         View.OnClickListener listener = v -> {
            
            if(isDialogOpen()) return;
            
            showList(title, allCallsOfThisGuyInDirection, getCall());
         };
         
         comment.append(commentStore.getTotalCallComment(listener, allCallsOfThisGuyInDirection.size()));
      }
      
      withCallCount();
      
      return comment;
   }
   
   /**
    * Verilen arama türüne ait ya da tüm arama kayıtları genelinde sadece tek bir kaydı bulunan aramaları araştırır ve sonucu yorumlar.
    *
    * @param isAll Tüm arama kayıtları geneline bakılacaksa {@code true}.
    */
   default CharSequence lookOneCalls(boolean isAll){
      
      Spanner comment      = new Spanner();
      val     commentStore = getCountCommentStore();
      
      if(isAll){
         
         val oneCalls = Stream.of(getHistory().getCalls())
                              .groupBy(cx -> Contacts.normalizeNumber(cx.getNumber()))
                              .filter(key -> key.getValue().size() == 1)
                              .flatMap(map -> Stream.of(map.getValue()))
                              .toList();
         
         if(oneCalls.size() == 1){
            
            //Geçerli kişi haricinde tek arama kaydı olan başka bir kişi yok
            comment.append(commentStore.thereIsNoOneHasOneRecordInTheLogComment());
         }
         else{
            
            //Tek kaydı olan kişiler
            oneCalls.remove(getCall());
            
            val title = commentStore.getOneRecordTitle();
            View.OnClickListener listener = v -> {
               
               if(!isDialogOpen()) showList(title, oneCalls);
            };
            
            comment.append(commentStore.onlyHasOneRecordInTheLogComment(listener, oneCalls.size()));
         }
         
         return comment;
      }
      
      
      final int type = getCall().getType();
      
      if(isValidType(type)){
         
         Map<String, List<ICall>> saperatedCalls = getCallStory().createGroups(type);
         List<ICall>              oneTimeCalls   = new ArrayList<>();
         
         for(Map.Entry<String, List<ICall>> entry : saperatedCalls.entrySet()){
            
            boolean isValid = entry.getValue().size() == 1;
            
            if(isValid){
               
               ICall onePhoneCall = entry.getValue().get(0);
               
               if(!onePhoneCall.equals(getCall())){
                  oneTimeCalls.add(onePhoneCall);
               }
            }
         }
         if(oneTimeCalls.isEmpty()){
            
            comment.append(commentStore.getNoOneTimeCallComment(getCallName()));
            return comment;
         }
         
         Collections.sort(oneTimeCalls);
         
         CharSequence charSequence = commentStore.thisIsOnlyOneCallDialogTitle();
         
         View.OnClickListener listener = v -> {
            
            if(!isDialogOpen()) showList(charSequence, oneTimeCalls);
         };
         
         comment.append(commentStore.getOneTimeCallComment(listener, oneTimeCalls.size(), getCallName()));
      }
      
      return comment;
   }
   
   static boolean isValidType(int type){
      
      switch(type){
         
         case Type.INCOMMING:
         case Type.OUTGOING:
         case Type.MISSED:
         case Type.REJECTED:
            return true;
         
         default:
            Timber.w("Yanlış arama türü ile ile çağrı yapıldı - type = %d", type);
            return false;
      }
   }
   
   /**
    * Arama sayısına göre yorumlar.
    */
   default CharSequence withCallCount(){
      
      int          type    = getCall().getType();
      CharSequence comment = null;
      
      if(type == Type.BLOCKED || isValidType(type)){
         
         List<Group<String>> mostCalls = CallStory.setRankByCallSize(getCallStory().createGroupList(getCallStory().getCalls(type)), true);
         
         int rank      = getPairRank(mostCalls, getCall());
         int rankCount = getRankCount(mostCalls, rank);
         
         if(rank != -1){
            
            comment = withRank(type, new Rank(rank, rankCount), mostCalls);
         }
      }
      
      return comment;
   }
   
   /**
    * Numaraya karşılık arama listesi olan {@code CallGroup} listesinde,
    * gerçekleşen aramanın {@code rank} değeri döndürülür.
    * Yani aramanın kaçıncı sırada olduğu bulunur.
    *
    * @param callListPairs Liste
    * @return Sıra
    */
   static int getPairRank(List<Group<String>> callListPairs, ICall call){
      
      for(int i = 0; i < callListPairs.size(); i++){
         
         if(Contacts.matchNumbers(call.getNumber(), callListPairs.get(i).getGroupItem())){
            
            return callListPairs.get(i).getRank();
         }
      }
      
      return -1;
   }
   
   /**
    * Verilen listede, verilen {@code rank} değerine sahip kaç arama kaydı olduğunu bulur.
    *
    * @param callListPairs Liste. Bu listenin {@code rank} değerleri işlenmiş olması gerek.
    * @param rank          Sayısı bulunacak {@code rank} değeri.
    * @return {@code rank} adeti.
    */
   static <T> int getRankCount(Iterable<Group<T>> callListPairs, int rank){
      
      return (int) Stream.of(callListPairs).filter(p -> p.getRank() == rank).count();
   }
   
   /**
    * Aramanın listedeki sırasına göre yorum yapar. {@link #withCallCount()} metodunun alt metodu.
    *
    * @param type      Aramanın türü
    * @param rankInfo  Sıra bilgisi
    * @param mostCalls Liste.
    *                  Listedeki sıralamanın, kişilerin arama sayısına göre çoktan aza doğru olduğu varsayılır.
    */
   default CharSequence withRank(int type, RankInfo rankInfo, List<Group<String>> mostCalls){
      
      int mostRank = getMostRank(mostCalls);
      
      Spanner comment      = null;
      val     commentStore = getCountCommentStore();
      
      if(mostRank > COMPARE_SIZE){
         
         int rank      = rankInfo.getRank();
         int rankCount = rankInfo.getRankCount();
         
         CharSequence title = commentStore.getRankTitle(type, mostRank < 9 || rank < 6);
         
         if(title != null){
            
            View.OnClickListener listener = v -> {
               
               if(!isDialogOpen()){
                  
                  setMostCallListDialog(new MostCallListDialog(getActivityReference().get(),
                                                               MostCallListDialog.getType(type),
                                                               title,
                                                               getCall(),
                                                               mostCalls));
                  
                  getMostCallListDialog().setCloseListener(getDialogListener());
                  
                  getMostCallListDialog().show();
               }
            };
            
            if(mostCalls.size() < COMPARE_SIZE){ return null; }
            
            comment = new Spanner().append(commentStore.getRankComment(
                  listener,
                  type,
                  new Rank(rank, rankCount, mostRank),
                  getCallName()));
         }
      }
      
      return comment;
   }
   
   /**
    * Listedeki en yüksek sıra numarasını verir.
    * Eğer listeye sıra numaraları yazılmamışsa sıfır döndürür.
    *
    * @param callListPairs Liste
    * @return En yüksek sıra numarası
    */
   default <T> int getMostRank(Iterable<Group<T>> callListPairs){
      
      int rank = 0;
      
      for(Group<T> pair : callListPairs){
         
         if(rank < pair.getRank()){
            
            rank = pair.getRank();
         }
      }
      
      return rank;
   }
   
   default com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Count  getCountCommentStore(){
      
      return (com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.kind.Count ) getCommentStore();
   }
   
}
