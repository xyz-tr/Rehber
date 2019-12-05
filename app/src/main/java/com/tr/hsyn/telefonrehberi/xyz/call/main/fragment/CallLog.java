package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;

import android.Manifest;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.database.CallLogDB;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.CallLogChangeListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.service.NotedCall;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.Callback;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.CallsDiff;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.CallsDiffInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import io.paperdb.Paper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;


/**
 * <h1>CallLogFragment</h1>
 * <p>
 * Bu sınıf arama kayıtlarının en üst düzey yöneticisi.
 *
 * @author hsyn
 * @date 2019-07-28 09:52:55
 */
public class CallLog extends CallLogFiltered implements Callback, Serializable{
   
   
   @Getter(AccessLevel.PROTECTED) private ICallLog              databaseOperator;
   private                                CallsDiff             callsDiff;
   private                                CallLogChangeListener callLogChangeListener;
   @Getter private final                  Kickback_CallsConst   callConst = Kickback_CallsConst.getInstance();
   
   @Override
   protected final void onViewInflated(View view){
      
      super.onViewInflated(view);
      
      EventBus.getDefault().register(this);
   }
   
   @Override
   protected void whenCallsLoaded(List<ICall> calls){
      
      super.whenCallsLoaded(calls);
      callsDiff.start();
      callLogChangeListener.startListen();
      controlNotedCalls();
   }
   
   private void controlNotedCalls(){
      
      val book    = Paper.book("noted_call");
      val numbers = book.getAllKeys();
      
      if(numbers != null && !numbers.isEmpty()){
         
         List<NotedCall> calls = new LinkedList<>();
         Stream.of(numbers).forEach(key -> calls.add(book.read(key, null)));
         
         StringBuilder stringBuilder = new StringBuilder(Stringx.format("Not edilen %d arama var%n", numbers.size()));
         
         if(getContext() == null){
            
            stringBuilder.append(calls);
         }
         else{
            
            int i = 1;
            for(val noted : calls){
               
               String name = Contacts.getContactName(getContext().getContentResolver(), noted.getNumber());
               
               val value = Stringx.from("%d. name='$name', number='%s', type='$type', date='%s'%n")
                                  .arg(1, i)
                                  .key("name", name)
                                  .arg(2, noted.getNumber())
                                  .key("type", Type.getTypeStr(noted.getType()))
                                  .arg(3, Time.getDate(noted.getDate()))
                                  .format();
               
               stringBuilder.append(value);
               i++;
            }
            
            Logger.w(stringBuilder.toString());
         }
      }
   }
   
   @Override
   public void onDetach(){
      
      super.onDetach();
      callLogChangeListener.stopListen();
      callLogChangeListener = null;
      databaseOperator.close();
      databaseOperator = null;
      callsDiff        = null;
      EventBus.getDefault().unregister(this);
   }
   
   @Override
   public final void onActivityCreated(@Nullable final Bundle savedInstanceState){
      
      super.onActivityCreated(savedInstanceState);
      
      val owner = getViewLifecycleOwner();
      
      Kickback_CallDetailBox.getInstance();
      Kickback_CallsConst.getInstance();
      Kickback_CallsConst.setLifecycleObserver(owner);
      Kickback_CallDetailBox.setLifecycleObserver(owner);
      
      
      val context = getContext();
      
      if(context == null){
         
         Logger.w("Activity henüz piyasaya çıkmamış");
      }
      else{
   
         databaseOperator      = new CallLogDB(getContext());
         callsDiff             = new CallsDiff(context.getContentResolver(), this, databaseOperator, false, 1);
         callLogChangeListener = new CallLogChangeListener(databaseOperator);
         
         loadCalls();
      }
   }
   
   @Override
   public void onCallsDiffResult(CallsDiffInfo callsDiffInfo){
      
      //off
      val report = Stringx.format("%s", TextUtils.join(" ", callsDiffInfo.getReport().toString().split("\n")));
      val log = Stringx.format("New Calls     : %s%n" +
                               "Removed Calls : %s%n" +
                               "Report        : %s", callsDiffInfo.getNewCalls(), callsDiffInfo.getRemovedCalls(), report);
      
      Logger.d(log);
      
      //on
   }
   
   @Override
   protected String[] getCallsPermissions(){
      
      return new String[]{
            
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.PROCESS_OUTGOING_CALLS
      };
   }
}
