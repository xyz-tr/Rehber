package com.tr.hsyn.telefonrehberi.xyz.call.story;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import androidx.annotation.NonNull;

import com.annimon.stream.Collectors;
import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.ToIntFunction;
import com.annimon.stream.function.ToLongFunction;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.CallGroup;
import com.tr.hsyn.telefonrehberi.xyz.call.Group;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.history.Historicall;
import com.tr.hsyn.telefonrehberi.xyz.call.history.History;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.CallUpdateListener;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.util.Collection.filter;


@SuppressLint("MissingPermission")
public interface CallStory extends Type{
   
   String[] CALL_COLUMNS = {
         
         CallLog.Calls.CACHED_NAME,
         CallLog.Calls.NUMBER,
         CallLog.Calls.TYPE,
         CallLog.Calls.DURATION,
         CallLog.Calls.DATE,
         CallLog.Calls._ID
   };
   
   boolean remove(ICall call);
   
   List<Group<String>> getMostCalls(int type);
   
   List<ICall> getFilteredCalls(int type);
   
   static CallStory createStory(List<ICall> calls){
      
      return new PhoneCallStory(calls);
   }
   
   static boolean exist(@NonNull Context context, long date){
      
      final Cursor cursor = context.getContentResolver().query(
            CallLog.Calls.CONTENT_URI,
            CALL_COLUMNS,
            CALL_COLUMNS[4] + "=?",
            new String[]{String.valueOf(date)},
            null);
      
      
      if(cursor == null){
         
         return false;
      }
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return false;
      }
      
      cursor.close();
      return true;
   }
   
   static boolean isRejected(ContentResolver contentResolver, String number, long date){
      
      List<ICall> callList   = getRejectedCalls(contentResolver);
      boolean     isRejected = false;
      
      for(ICall call : callList){
         
         if(Contacts.matchNumbers(call.getNumber(), number) && Math.abs(call.getDate() - date) < 5000L){
            
            isRejected = true;
         }
      }
      
      return isRejected;
   }
   
   static List<ICall> getRejectedCalls(ContentResolver contentResolver){
      
      List<ICall> callList = getSystemCallLogCalls(contentResolver);
      
      return Stream.of(callList).filter(cx -> cx.getType() == REJECTED).toList();
   }
   
   /**
    * Sistemdeki tüm arama kayıtlarını döndür.
    *
    * @param contentResolver contentResolver
    * @return Kayıtlar
    */
   @NonNull
   static List<ICall> getSystemCallLogCalls(ContentResolver contentResolver){
      
      List<ICall> callList = new ArrayList<>();
      Cursor      cursor;
      
      if(contentResolver != null && (cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                                                                    CALL_COLUMNS, null, null,
                                                                    CALL_COLUMNS[4] + " desc")) != null && cursor.getCount() != 0){
         
         int nameColumn     = cursor.getColumnIndex(CALL_COLUMNS[0]);
         int numberColumn   = cursor.getColumnIndex(CALL_COLUMNS[1]);
         int typeColumn     = cursor.getColumnIndex(CALL_COLUMNS[2]);
         int durationColumn = cursor.getColumnIndex(CALL_COLUMNS[3]);
         int dateColumn     = cursor.getColumnIndex(CALL_COLUMNS[4]);
         int idColumn       = cursor.getColumnIndex(CALL_COLUMNS[5]);
         
         while(cursor.moveToNext()){
            
            callList.add(new Call(cursor.getString(idColumn),
                                  cursor.getString(nameColumn),
                                  cursor.getString(numberColumn),
                                  cursor.getLong(dateColumn),
                                  cursor.getInt(typeColumn),
                                  cursor.getInt(durationColumn)));
         }
         
         cursor.close();
         
      }
      
      return callList;
   }
   
   /**
    * Android sistemindeki kaydı siler.
    *
    * @param contentResolver cr
    * @param date            Silinecek olan kaydın tarihi
    * @return Silinirse {@code true}, böyle bir yıt yoksa ya da herhangi bir sebeple silinemezse {@code false}.
    */
   
   static boolean removeCall(ContentResolver contentResolver, long date){
      
      return contentResolver.delete(
            CallLog.Calls.CONTENT_URI,
            CALL_COLUMNS[4] + "=?",
            new String[]{String.valueOf(date)}) > 0;
   }
   
   static boolean removeCall(ContentResolver contentResolver, String id){
      
      return contentResolver.delete(
            CallLog.Calls.CONTENT_URI,
            CALL_COLUMNS[5] + "=?",
            new String[]{id}) > 0;
   }
   
   @SuppressLint("MissingPermission")
   @NonNull
   static List<Call> getLastCalls(ContentResolver contentResolver){
      
      List<Call> callList = new ArrayList<>();
      Cursor     cursor;
      
      if(contentResolver != null && ((cursor = contentResolver.query(CallLog.Calls.CONTENT_URI,
                                                                     CALL_COLUMNS, null, null,
                                                                     CALL_COLUMNS[4] + " desc limit 5")) != null) && cursor.getCount() != 0){
         
         int nameColumn     = cursor.getColumnIndex(CALL_COLUMNS[0]);
         int numberColumn   = cursor.getColumnIndex(CALL_COLUMNS[1]);
         int typeColumn     = cursor.getColumnIndex(CALL_COLUMNS[2]);
         int durationColumn = cursor.getColumnIndex(CALL_COLUMNS[3]);
         int dateColumn     = cursor.getColumnIndex(CALL_COLUMNS[4]);
         int idColumn       = cursor.getColumnIndex(CALL_COLUMNS[5]);
         
         
         while(cursor.moveToNext()){
            
            String name   = cursor.getString(nameColumn);
            String number = cursor.getString(numberColumn);
            
            if(name == null || name.trim().isEmpty()){
               
               name = Contacts.getContactName(contentResolver, number);
            }
            
            callList.add(new Call(cursor.getString(idColumn),
                                  name,
                                  number,
                                  cursor.getLong(dateColumn),
                                  cursor.getInt(typeColumn),
                                  cursor.getInt(durationColumn)));
         }
         
         cursor.close();
      }
      
      
      return callList;
   }
   
   /**
    * Telefon numarasını biraz daha okunaklı hale getirir.
    * <p>
    * 5434937530   -->  543 493 7530
    * +905434937530 --> +90 543 493 7530
    * 05434937530  -->  0543 493 7530
    * 905434937530 -->  90 543 493 7530
    * <p>
    * Numaranın başında '*, #' karakterleri varsa değişiklik yapılmadan numara geri döndürülür.
    *
    * @param number Telefon numarası
    * @return Biraz daha okunaklı telefon numarası
    */
   static String formatNumberForDisplay(String number){
      
      number = Contacts.normalizeNumber(number);
      
      if(!number.isEmpty() && number.charAt(0) != '*' && number.charAt(0) != '#'){
         
         //Edit number
         StringBuilder stringBuilder = new StringBuilder(number);
         
         final int fullNumber     = 0xd;//+90 543 493 7530
         final int oneMinusNumber = 0xc;//90 543 493 7530
         final int twoMinusNumber = 0xb;//0543 493 7530
         
         if(number.length() == fullNumber){
            
            stringBuilder.insert(3, " ");
            stringBuilder.insert(7, " ");
            stringBuilder.insert(twoMinusNumber, " ");
         }
         
         else if(number.length() == oneMinusNumber){
            
            stringBuilder.insert(2, " ");
            stringBuilder.insert(6, " ");
            stringBuilder.insert(10, " ");
         }
         
         else if(number.length() == twoMinusNumber){
            
            stringBuilder.insert(4, " ");
            stringBuilder.insert(8, " ");
         }
         //543 493 7530
         else if(number.length() == 10){
            
            stringBuilder.insert(3, " ");
            stringBuilder.insert(7, " ");
         }
         
         number = stringBuilder.toString();
      }
      
      return number;
   }
   
   static <T> List<Group<T>> setRankByCallSize(List<Group<T>> groups, boolean isDESC){
      
      Collections.sort(groups, ComparatorCompat.comparingInt(group -> group.getCalls().size()));
      
      if(isDESC){
         
         Collections.reverse(groups);
      }
      
      int rank = 1;
      int size = groups.size();
      
      for(int i = 0; i < size; i++){
         
         Group<T> currentGroup = groups.get(i);
         currentGroup.setRank(rank);
         
         if(i != size - 1){
            
            Group<T> nextGroup = groups.get(i + 1);
            
            if(currentGroup.getCalls().size() != nextGroup.getCalls().size()){
               
               rank++;
            }
         }
      }
      
      return groups;
   }
   
   static <T> List<Group<T>> setRankByRinging(List<Group<T>> groups, boolean isDESC){
      
      for(Group<T> group : groups){
         
         long total = 0;
         
         for(ICall call : group.getCalls()){
            
            total += call.getRingingDuration();
         }
         
         group.setTotalDuration(total / group.getCalls().size());
      }
      
      Collections.sort(groups, ComparatorCompat.comparingLong(Group::getTotalDuration));
      
      if(isDESC){
         
         Collections.reverse(groups);
      }
      
      return setDurationRanks(groups, true);
   }
   
   static <T> List<Group<T>> setDurationRanks(List<Group<T>> groups, boolean isMillis){
      
      final long oneSec = 1000L;
      int        rank   = 1;
      int        size   = groups.size();
      
      for(int i = 0; i < size; i++){
         
         Group<T> currentGroup = groups.get(i);
         currentGroup.setRank(rank);
         
         if(i != size - 1){
            
            Group<T> nextGroup = groups.get(i + 1);
            
            if(isMillis){
               
               if(currentGroup.getTotalDuration() / oneSec != nextGroup.getTotalDuration() / oneSec){
                  
                  rank++;
               }
            }
            else{
               
               if(currentGroup.getTotalDuration() != nextGroup.getTotalDuration()){
                  
                  rank++;
               }
            }
            
            
         }
      }
      
      return groups;
   }
   
   static <T> List<Group<T>> setRankByDuration(List<Group<T>> groups, boolean isDESC){
      
      for(Group<T> group : groups){
         
         long total = 0;
         
         for(ICall call : group.getCalls()){
            
            total += call.getDuration();
         }
         
         group.setTotalDuration(total / group.getCalls().size());
      }
      
      Collections.sort(groups, ComparatorCompat.comparingLong(Group::getTotalDuration));
      
      if(isDESC){
         
         Collections.reverse(groups);
      }
      
      return setDurationRanks(groups, false);
   }
   
   static boolean addCallIntoSystem(ContentResolver contentResolver, ICall phoneCall){
      
      ContentValues values = new ContentValues();
      
      values.put(CALL_COLUMNS[0], phoneCall.getName());
      values.put(CALL_COLUMNS[1], phoneCall.getNumber());
      values.put(CALL_COLUMNS[2], phoneCall.getType());
      values.put(CALL_COLUMNS[3], phoneCall.getDuration());
      values.put(CALL_COLUMNS[4], phoneCall.getDate());
      values.put(CallLog.Calls.IS_READ, 1);
      values.put(CallLog.Calls.PHONE_ACCOUNT_ID, "xyz");
      
      return contentResolver.insert(CallLog.Calls.CONTENT_URI, values) != null;
   }
   
   static boolean putToSystemCall(ContentResolver contentResolver, ICall phoneCall){
      
      ContentValues values = new ContentValues();
      
      values.put(CALL_COLUMNS[0], phoneCall.getName());
      values.put(CALL_COLUMNS[1], phoneCall.getNumber());
      values.put(CALL_COLUMNS[2], phoneCall.getType());
      values.put(CALL_COLUMNS[3], phoneCall.getDuration());
      values.put(CALL_COLUMNS[4], phoneCall.getDate());
      
      return contentResolver.update(CallLog.Calls.CONTENT_URI, values, CALL_COLUMNS[4] + "= ?", new String[]{String.valueOf(phoneCall.getDate())}) > 0;
   }
   
   default List<ICall> getIncommingCalls(String number){
      
      return Stream.of(getCalls(INCOMMING)).filter(cx -> Contacts.matchNumbers(cx.getNumber(), number)).toList();
   }
   
   default List<ICall> getCalls(int type){
      
      return filter(getCalls(), cx -> cx.getType() == type);
   }
   
   /**
    * Tüm arama kayıtları.
    *
    * @return Arama kayıtları
    */
   List<ICall> getCalls();
   
   void setCalls(List<ICall> calls);
   
   default List<ICall> getRingingCalls(int type){
      
      return filter(getCalls(), cx -> cx.getType() == type && CallPredicate.PREDICATE_RINGING.test(cx));
   }
   
   static List<ICall> getRingingCalls(List<ICall> calls, int type){
      
      return filter(calls, cx -> cx.getType() == type && CallPredicate.PREDICATE_RINGING.test(cx));
   }
   
   static List<ICall> getRingingCalls(List<ICall> calls){
      
      return filter(calls, CallPredicate.PREDICATE_RINGING);
   }
   
   static List<ICall> getSpeakingCalls(List<ICall> calls){
      
      return filter(calls, CallPredicate.PREDICATE_RINGING);
   }
   
   default List<ICall> getSpeakingCalls(int type){
      
      return filter(getCalls(), cx -> cx.getType() == type && CallPredicate.PREDICATE_SPEAKING.test(cx));
   }
   
   static List<ICall> getSpeakingCalls(List<ICall> calls, int type){
      
      return filter(calls, cx -> cx.getType() == type && CallPredicate.PREDICATE_SPEAKING.test(cx));
   }
   
   default List<ICall> getSpeakingCalls(int type, Predicate<ICall> predicate){
      
      return filter(getCalls(), cx -> CallPredicate.PREDICATE_TYPE.apply(cx, type) && predicate.test(cx));
   }
   
   default List<ICall> getResponsedCalls(int type){
      
      if(type == REJECTED){
         
         Timber.d("REJECTED_CALL");
         return Stream.of(getRejectedCalls()).filter(ICall::isSharable).toList();
      }
      
      
      return filter(getCalls(),
                    cx ->
                          CallPredicate.PREDICATE_TYPE.apply(cx, type) &&
                          CallPredicate.PREDICATE_RESPONSE.test(cx));
   }
   
   default List<ICall> getRejectedCalls(){
   
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.REJECTED)).toList();
   }
   
   default List<ICall> getCalls(Collection<String> numbers, int type){
      
      List<ICall> callList = new ArrayList<>();
      
      for(String number : numbers){
         
         callList.addAll(getCalls(number, type));
      }
      
      return callList;
   }
   
   default List<ICall> getCalls(String number, int type){
      
      return filter(getCalls(), cx -> cx.getType() == type && Contacts.matchNumbers(cx.getNumber(), number));
   }
   
   default List<ICall> getCalls(Predicate<ICall> predicate){
      
      return filter(getCalls(), predicate);
   }
   
   default List<ICall> getCalls(int type, Predicate<? super ICall> predicate){
      
      return filter(getCalls(), cx -> cx.getType() == type && predicate.test(cx));
   }
   
   default Map<String, List<ICall>> createGroups(int... types){
      
      Map<String, List<ICall>> map = new HashMap<>();
      
      for(int type : types){
         
         map.putAll(createGroups(type));
      }
      
      return map;
   }
   
   default Map<String, List<ICall>> createGroups(int type){
      
      return Stream.of(getCalls())
                   .filter(cx -> cx.getType() == type)
                   .groupBy(cx -> Contacts.normalizeNumber(cx.getNumber()))
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
   }
   
   default <T> Map<T, List<ICall>> createGroups(Function<ICall, T> function){
      
      return Stream.of(getCalls())
                   .groupBy(function)
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
   }
   
   default List<Group<String>> createGroupList(int type){
      
      return createGroupList(getCalls(type));
   }
   
   default List<Group<String>> createGroupList(List<ICall> calls){
      
      List<Group<String>> groups = new ArrayList<>();
      
      for(int i = 0; i < calls.size(); i++){
         
         ICall cx = calls.get(i);
         
         String number = Contacts.normalizeNumber(cx.getNumber());
         
         List<ICall> callList = new ArrayList<>();
         callList.add(cx);
         
         Group<String> pair = new CallGroup<>(number, callList);
         
         if(groups.contains(pair)){
            
            groups.get(groups.indexOf(pair)).addCall(cx);
         }
         else{
            
            groups.add(pair);
         }
      }
      
      return groups;
   }
   
   default double getAverage(Predicate<? super ICall> predicate, ToLongFunction<? super ICall> function){
      
      return Stream.of(getCalls())
                   .filter(predicate)
                   .collect(Collectors.averagingLong(function));
   }
   
   default double getSpeakingAverage(@NonNull final List<ICall> calls){
      
      return Stream.of(calls)
                   .collect(Collectors.averagingInt(ICall::getDuration));
   }
   
   default double getResponseAverage(@NonNull final List<ICall> calls){
      
      return Stream.of(calls)
                   .collect(Collectors.averagingLong(ICall::getRingingDuration));
   }
   
   default double getAverage(ToIntFunction<? super ICall> function){
      
      return Stream.of(getCalls())
                   .collect(Collectors.averagingInt(function));
   }
   
   default double getAverage(List<? extends ICall> calls, ToLongFunction<? super ICall> function){
      
      return Stream.of(calls)
                   .collect(Collectors.averagingLong(function));
   }
   
   default double getAverage(List<? extends ICall> calls, ToIntFunction<? super ICall> function){
      
      return Stream.of(calls)
                   .collect(Collectors.averagingInt(function));
   }
   
   static double getRingingAverage(List<ICall> calls){
      
      return Stream.of(calls)
                   .collect(Collectors.averagingLong(ICall::getRingingDuration));
   }
   
   default double getRingingAverage(int type){
      
      return Stream.of(getCalls(type))
                   .collect(Collectors.averagingLong(ICall::getRingingDuration));
   }
   
   default double getSpeakingAverage(int type){
      
      return getAverage(cx -> cx.getType() == type && CallPredicate.PREDICATE_SPEAKING.test(cx), ICall::getDuration);
   }
   
   default double getAverage(Predicate<? super ICall> predicate, ToIntFunction<? super ICall> function){
      
      return Stream.of(getCalls())
                   .filter(predicate)
                   .collect(Collectors.averagingInt(function));
   }
   
   /**
    * Geçmiş oluştur.<br></br>
    * Verilen arama kaydının sahibine ait tüm arama kayıtlarından oluşan
    * bir nesne oluşturur.
    * Bu nesneye <b>geçmiş</b> nesnesi diyorum.
    *
    * @param val arama kaydı
    * @return geçmiş nesnesi
    */
   default Historicall<ICall> createHistory(ICall val){
      
      return History.create(val, getCalls(val.getNumber()));
   }
   
   /**
    * @param number telefon numarası
    * @return verilen numaraya ait tüm arama kayıtlarının listesi
    */
   default List<ICall> getCalls(String number){
      
      String formatNumber = Contacts.formatNumber(number);
      
      return getCalls().stream().filter(cx -> formatNumber.equals(Contacts.formatNumber(cx.getNumber()))).collect(java.util.stream.Collectors.toList());
   }
   
   default Historicall<IPhoneContact> createHistory(IPhoneContact val){
      
      assert val.getNumberList() != null;
      return History.create(val, getCalls(val.getNumberList()));
   }
   
   /**
    * Verilen nnumaralara ait tüm arama kayıtlarını döndür.
    *
    * @param numbers Numaralar
    * @return Kayıtlar
    */
   default List<ICall> getCalls(Iterable<String> numbers){
      
      List<ICall> callList = new ArrayList<>();
      
      for(String number : numbers){
         
         callList.addAll(getCalls(number));
      }
      
      return callList;
   }
   
   static void updateCachedNames(List<ICall> calls, Context context, CallUpdateListener listener){
      
      for(int i = 0; i < calls.size(); i++){
         
         ICall call = calls.get(i);
         
         if(call.getName() != null) continue;
         
         String name = Contacts.getContactName(context.getContentResolver(), call.getNumber());
         
         if(name == null) continue;
         
         call.setName(name);
         
         if(!addToCachedName(context, call.getDate(), name)){
            
            Timber.w("Arama sahibinin ismi kaydedilemedi : %s", Stringx.overWrite(call.getNumber()));
         }
         else{
            
            Timber.d("Arama sahibinin ismi sisteme kaydedildi : %s", Stringx.overWrite(name));
            
            if(listener != null){
               
               int finalI = i;
               Worker.onMain(() -> listener.onCallUpdated(finalI, call));
            }
         }
      }
   }
   
   @SuppressLint("MissingPermission")
   static boolean addToCachedName(Context context, long date, @NonNull final String name){
      
      boolean res = false;
      
      if(context != null){
         
         ContentValues contentValues = new ContentValues();
         contentValues.put(CallLog.Calls.CACHED_NAME, name);
         
         if(EasyPermissions.hasPermissions(context, Manifest.permission.WRITE_CALL_LOG)){
            
            int i = context.getContentResolver().update(CallLog.Calls.CONTENT_URI, contentValues, CallLog.Calls.DATE + " = ?", new String[]{String.valueOf(date)});
            
            res = i > 0;
         }
         else{
            
            Timber.w("yazma izni yok");
         }
      }
      
      return res;
   }
   
   default List<ICall> getIncommingCalls(Predicate<ICall> predicate){
      
      return Stream.of(getIncommingCalls()).filter(predicate).toList();
   }
   
   default List<ICall> getIncommingCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.INCOMMING)).toList();
   }
   
   default List<ICall> getOutgoingCalls(Predicate<ICall> predicate){
      
      return Stream.of(getOutgoingCalls()).filter(predicate).toList();
   }
   
   default List<ICall> getOutgoingCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.OUTGOING)).toList();
   }
   
   default List<ICall> getMissedCalls(Predicate<ICall> predicate){
      
      return Stream.of(getCalls(Type.MISSED)).filter(predicate).toList();
   }
   
   default List<ICall> getRejectedCalls(Predicate<ICall> predicate){
      
      return Stream.of(getRejectedCalls()).filter(CallPredicate.PREDICATE_RINGING).toList();
   }
   
   default List<ICall> getDeletedCalls(){
      
      return filter(getCalls(), call -> call.getDeletedDate() != 0);
   }
   
   default List<ICall> getMissedCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.MISSED)).toList();
   }
   
   default List<ICall> getBlockedCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.BLOCKED)).toList();
   }
   
   default List<ICall> getUnreachedCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.UNREACHED)).toList();
   }
   
   default List<ICall> getUnrecievedCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.UNRECIEVED)).toList();
   }
   
   default List<ICall> getGetRejectedCalls(){
      
      return Stream.of(getCalls()).filter(cx -> CallPredicate.PREDICATE_TYPE.apply(cx, Type.GETREJECTED)).toList();
   }
   
   default List<ICall> getRingingCalls(){
      
      return filter(getCalls(), CallPredicate.PREDICATE_RINGING);
   }
   
   default List<ICall> getSpeakingCalls(){
      
      return filter(getCalls(), CallPredicate.PREDICATE_SPEAKING);
   }
   
   default List<ICall> getResponsedCalls(){
      
      return filter(getCalls(), cx -> CallPredicate.PREDICATE_SPEAKING.test(cx) && CallPredicate.PREDICATE_RINGING.test(cx));
   }
   
   default double getSpeakingAverage(){
      
      return getAverage(CallPredicate.PREDICATE_SPEAKING, CallPredicate.FUNCTION_SPEAKING_AVERAGE);
   }
   
   
}






































