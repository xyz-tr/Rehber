package com.tr.hsyn.telefonrehberi.xyz.call.main.listener;


import com.tr.hsyn.telefonrehberi.util.event.CallDeleted;
import com.tr.hsyn.telefonrehberi.util.event.NewCall;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;
import com.tr.hsyn.telefonrehberi.xyz.call.listener.ICallLogChangeListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import timber.log.Timber;


/**
 * <h1>CallLogChangeListener</h1>
 * 
 * <p>
 *    Bir arama kaydı eklendiğinde ya da silindiğinde haberdar olacak olan şahıs.
 * 
 * @author hsyn 2019-12-03 14:31:52
 */
public class CallLogChangeListener implements ICallLogChangeListener{
   
   private ICallLog callOperator;
   
   public CallLogChangeListener(ICallLog callOperator){
      
      this.callOperator = callOperator;
   }
   
   @Override
   public void startListen(){
   
      EventBus.getDefault().register(this);
   }
   
   @Override
   public void stopListen(){
      
      EventBus.getDefault().unregister(this);
   }
   
   @Subscribe
   public final void onCallAdded(NewCall event){
   
      String eventTag = event.getTag();
      
      if(eventTag != null && eventTag.equals("CallManager")) return;
      
      if(callOperator.add(event.getCall())){
   
         Timber.d("Arama eklendi %s", event.getCall());
      }
      else{
   
         if(callOperator.get(event.getCall().getDate()) != null){
   
            Timber.d("Arama eklenemedi çünkü bu kayıt zaten var : %s", event.getCall());
         }
         else{
   
            Timber.w("Arama eklenemedi %s", event.getCall());
         }
      }
   }
   
   @Subscribe
   public final void onCallDeleted(CallDeleted event){
   
      if(callOperator.update(event.getDeletedCall())){
   
         Timber.d("Arama veri tabanında silinmiş olarak işaretlendi : %s", event.getDeletedCall());
      }
      else{
   
         Timber.w("Arama veri tabanında silinmiş olarak güncellenemedi : %s", event.getDeletedCall());
      }
   
      
   }
}
