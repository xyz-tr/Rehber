package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.event.EventUpdatedContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.PhoneContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.Contact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.ContactStory;

import org.greenrobot.eventbus.EventBus;

import lombok.val;
import lombok.var;
import timber.log.Timber;


public abstract class ContactDetailsUpdate extends ContactDetailsAction{
   
   
   private ContactStory contactStory;
   
   /**
    * Güncelleme olup olmadığını bildirecek. Güncelleme yoksa daima {@code false}
    */
   private static boolean      updated;
   /**
    * Güncellenen kişiyi tutacak. Güncelleme yoksa daima {@code null}
    */
   private static PhoneContact oldContact;
   
   @Override
   protected void onCreate(Bundle bundle){
      
      super.onCreate(bundle);
      
      contactStory = ContactStory.create(this);
      updateContact();
   }
   
   /**
    * Kişi güncellendiğinde static bir değişken set edilir ve activity sonlandırılır,
    * güncel bilgilerin olduğu yeni bir Activity yaratılır. Bu yüzden activity'nin başında,
    * görsellerin kullanılabilir olduğu andan hemen sonra çağrılır ve set edilen değişkeni kontrol eder.
    * Kişinin güncellenmiş olup olmadığı burada ortaya çıkar.
    * Eğer kişi güncellenmiş değilse bakılma sayısını 1 arttırır ve geri döner.
    * Bu Activity'nin alt sınıfları bu metodu override ederlerse
    * metodun başında ya da sonunda {@code super.updateContact()} çağrısını mutlaka yapmalı.
    */
   protected void updateContact(){
      
      Worker.onBackground(() -> {
         
         var number = contact.getNumber();
         
         if(number != null && !number.trim().isEmpty()){
            
            val key = Contacts.createKeyFromNumber(number);
            
            Contact contactStored = contactStory.get(key);
            
            if(contactStored == null){
               
               contactStored = new Contact(contact.getName(), key);
            }
            
            Timber.d(contactStored.toString());
            
            Contact finalContactStored = contactStored;
            
            Worker.onMain(() -> onReadyStoredContact(new Contact(finalContactStored)));
            
            val accounts = contact.getGoogleAccounts();
            
            if(accounts != null && !accounts.isEmpty() && !updated){
               
               incLookCount();
            }
         }
         
         
         Worker.onMain(() -> {
            
            if(!updated){
               
               if(oldContact != null){
                  
                  oldContact = null;
               }
               
               return;
            }
            
            updated = false;
            
            Logger.d("Açılan bu Activity güncellenmiş bir kişiye ait");
            
            EventBus.getDefault().post(new EventUpdatedContact(oldContact, contact));
            
         });
      }, "ContactDetails:Rehber kaydının güncellenmesi");
   }
   
   /**
    * Veri tabanına kaydedilen bilgilerin bu activity'nin açılmasından
    * dolayı <u>güncellenmeden önceki bilgileri</u> taşıyan kopya nesne.
    * Eğer kişi ilk defa açılıyorsa sadece temel bilgiler olan id, isim ve numaralar var olur.
    * Diğer bilgiler varsayılan değerlerindedir.
    *
    * @param contactStored Kaydedilmiş kaydın güncellenmemiş kopyası
    */
   protected abstract void onReadyStoredContact(Contact contactStored);
   
   /**
    * Bakılma sayısını bir arttır.
    */
   private void incLookCount(){
      
      if(contactStory.incLookCount(contact.getNumber())){
         
         Timber.d("Bakılma sayısı güncellendi");
      }
      else{
         
         Timber.w("Bakılma sayısı güncellenemiyor");
      }
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      if(data == null){ return; }
      
      if(RC_EDIT_CONTACT == requestCode){
         
         if(resultCode == RESULT_OK){
            
            if(data.getData() == null){ return; }
            
            onEditCompleted(data.getData());
         }
         
      }
      else if(requestCode == REQUEST_CODE_NEW_GROUP_RINGTONE && resultCode == RESULT_OK){
         
         Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
         
         if(ringtoneUri != null){
            
            setRingtoneToGroup(ringtoneUri);
         }
      }
   }
   
   /**
    * Kullanıcı güncellemeyi tamamladıktan hemen sonra çağrılıyor.
    * Burası güncellemenin kesinlikle olduğu ve uri nesnesinin kesinlikle {@code null} olmadığı yerdir.
    *
    * @param uri Güncellenen kişinin rehberdeki yeri
    */
   private void onEditCompleted(@NonNull final Uri uri){
      
      IMainContact updatedContact = Contacts.getContact(this, uri);
      
      if(updatedContact == null){
         
         Timber.w("Güncellenen kişi null");
         
         //Eğer bir aksilik olursa kişiler yeniden yüklensin
         EventBus.getDefault().post(new EventRefreshContacts());
         //ContactsFragment.setNeedRefresh();
         
         onBackPressed();
         return;
      }
      
      Timber.d("Kişi güncellendi : %s", updatedContact);
      
      //set updated
      ContactDetailsUpdate.oldContact = new PhoneContact(contact);
      updated                         = true;
      
      //restart activity
      ContactDetailsActivity.startActivity(this, updatedContact.getContactId());
      
      //go fuck then
      finishAndRemoveTask();
   }
   
   @Override
   public final void onBackPressed(){
      
      super.onBackPressed();
      
      Bungee.slideLeft(this);
      
   }
   
   @Override
   protected void onDestroy(){
      
      contactStory.close();
      contactStory = null;
      super.onDestroy();
   }
}
