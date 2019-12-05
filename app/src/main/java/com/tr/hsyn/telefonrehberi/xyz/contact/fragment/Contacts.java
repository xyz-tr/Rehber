package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;


import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.ColorChanged;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.event.EventUpdatedContact;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.Contact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.ContactStory;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.ContactsDiff;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.ContactsDiffInfo;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.List;
import java.util.function.IntConsumer;

import lombok.val;
import lombok.var;
import timber.log.Timber;


public class Contacts extends ContactsMain implements ContactsDiff.Callback, Serializable{
   
   /**
    * Kaydedilmiş rehber kayıtları yöneticisi
    */
   private volatile ContactStory contactStory;
   
   /**
    * Renk değişikliği olduğunda
    */
   private final IntConsumer colorAnimationConsumer = i -> {
      
      recyclerView.setPopupBgColor(i);
      recyclerView.setThumbColor(i);
   };
   
   @Override
   public void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      
      EventBus.getDefault().register(this);
      
      val context = getContext();
      
      if(context == null){
         
         Logger.w("Activity daha piyasada yok");
         return;
      }
      
      contactStory = ContactStory.create(context);
   }
   
   @Override
   public void onDestroy(){
      
      contactStory.close();
      contactStory = null;
      EventBus.getDefault().unregister(this);
      
      super.onDestroy();
   }
   
   @Override
   protected void whenContactsLoaded(List<IMainContact> contacts){
      
      super.whenContactsLoaded(contacts);
      
      //noinspection ConstantConditions
      ContactsDiff contactsDiff = new ContactsDiff(getContext().getContentResolver(), contactStory, this, true, 1);
      contactsDiff.start();
   }
   
   @Override
   public void onActivityCreated(@Nullable Bundle savedInstanceState){
      
      super.onActivityCreated(savedInstanceState);
      loadContacts();
   }
   
   @Override
   protected void whenContactUpdated(EventUpdatedContact event){
      
      Worker.onBackground(() -> contactStory.update(event.getOldContact(), event.getUpdatedContact()), "ContactsFragment:Güncellenen kişiyi veri tabanına işleme", true)
            .whenCompleteAsync((r, t) -> contactStory.close(), Worker.getMainThreadExecutor());
   }
   
   @Override
   protected void whenNewContactAdded(IMainContact newContact){
      
      if(getContext() == null) return;
      
      Worker.onBackground(() -> {
         
         val contact = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.getPhoneContact(getContext().getContentResolver(), newContact.getContactId());
         
         val r = contactStory.add(new Contact(contact.getName(), contact.getNumber()));
         
         if(r){
            
            Logger.w("Yeni kişi kaydedildi : %s", contact);
         }
         else{
            
            Logger.w("Yeni kişi kaydedilemedi : %s", contact);
         }
      }, "ContactsFragment:Rehbere eklenen kişiyi veri tabanına kaydetme", true);
   }
   
   @Override
   protected void whenContactDeleted(IPhoneContact contact){
      
      Worker.onBackground(() -> {
         
         val dbContact = contactStory.get(contact.getNumber());
         
         if(dbContact != null){
            
            if(contactStory.setDeleted(dbContact)){
               
               Logger.d("Kişi silindi olarak işaretlendi : %s", dbContact);
            }
         }
      }, "ContactsFragment:Silinen kişi için veri tabanını güncelleme", true);
      
      
   }
   
   @Override
   public void onContactsDiffResult(ContactsDiffInfo contactsDiffInfo){
      
      if(contactsDiffInfo == null) return;
      
      var report = contactsDiffInfo.getReport();
      
      if(report == null || report.isEmpty()){
         
         report = "-";
      }
      else{
         
         report = Stringx.format("%s", TextUtils.join(" ", report.split("\n")));
      }
      
      String log = Stringx.format("New Contacts     : %s%n" +
                                  "Removed Contacts : %s%n" +
                                  "Report           : %s", contactsDiffInfo.getNewContacts(), contactsDiffInfo.getRemovedContacts(), report);
      
      
      Logger.d(log);
      
      int i = 1;
      
      if(contactsDiffInfo.getNewContacts() != null){
         
         for(val item : contactsDiffInfo.getNewContacts()){
            
            if(contactStory.add(item.getName(), item.getNumber())){
               
               Timber.d("%d. %s [eklendi]", i++, Stringx.overWrite(item.getNumber()));
            }
            else{
               
               Timber.w("%d. %s [eklenemedi]", i++, Stringx.overWrite(item.getNumber()));
            }
         }
      }
      
      if(contactsDiffInfo.getRemovedContacts() != null){
         
         i = 1;
         
         for(val item : contactsDiffInfo.getRemovedContacts()){
            
            var dbContact = contactStory.get(item.getNumber());
            
            if(dbContact != null && dbContact.getDeletedDate() != 0L) continue;
            
            if(dbContact == null) dbContact = new Contact(item.getName(), item.getNumber());
            
            if(contactStory.setDeleted(dbContact)){
               
               Timber.d("%d. %s %s [silindi]", i++, item.getName(), item.getNumber());
            }
            else{
               
               Timber.d("%d. %s %s [silinmedi]", i++, item.getName(), item.getNumber());
            }
         }
      }
   }
   
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onChangePrimaryColor(ColorChanged event){
      
      int newColor = event.getColor();
      
      ColorController.runColorAnimation(colorAnimationConsumer);
      
      getContactSwipeCallBack().setBgColor(newColor);
      refreshLayout.setColorSchemeColors(newColor);
      ColorController.setIndaterminateProgressColor(getContext(), progressBar);
   }
   
   @Subscribe
   public void needRefresh(EventRefreshContacts event){
      
      loadContacts();
   }
}
