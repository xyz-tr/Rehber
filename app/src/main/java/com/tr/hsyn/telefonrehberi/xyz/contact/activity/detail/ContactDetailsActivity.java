package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.event.ContactDeleted;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.Contact;

import org.greenrobot.eventbus.EventBus;

import timber.log.Timber;

//todo Kişilerle ilgili daha ne bilgiler toplanabilir?

/**
 * <h1>ContactDetailsActivity</h1>
 * Bu activity bir rehber kaydının detaylarını gösterecek.
 * {@link #startActivity(Context, String)} metodu ile başlatılabilir, 
 * ya da {@linkplain android.provider.ContactsContract.Contacts#_ID} değeri {@linkplain #EXTRA_CONTACT_ID} ile verilerek başlatılabilir.
 * 
 * @author hsyn 2019-11-08 10:43:13
 */
@SuppressLint("Registered")
public class ContactDetailsActivity extends ContactDetailsCallSummary{
   
   @Override
   protected void onCreate(Bundle bundle){
      
      super.onCreate(bundle);
      
      EventBus.getDefault().register(this);
   }
   
   @Override
   protected void onReadyStoredContact(Contact contactStored){
      
      
   }
   
   @Override
   protected void onDetailedContactFound(IPhoneContact phoneContact){
      
      super.onDetailedContactFound(phoneContact);
      
      //if(!Phone.isPermissionsGrant(this, CALL_PERMISSIONS)) return;
      
      
   }
   
   @Override
   protected void onDestroy(){
      
      EventBus.getDefault().unregister(this);
      super.onDestroy();
   }
   
   @Override
   protected void onRingToneChanged(String ringToneName){
      
   }
   
   @Override
   protected void onContactDeleted(boolean success){
      
      if(success){
         
         EventBus.getDefault().post(new ContactDeleted(contact));
      }
      else{
         
         Timber.w("Kişi silinemedi : %s", contact.toString());
      }
      
      onBackPressed();
   }
   
   /**
    * Activity başlat.
    *
    * @param context   context
    * @param contactId contact id (not raw id)
    */
   public static void startActivity(@NonNull final Context context, @NonNull final String contactId){
      
      Intent intent = new Intent(context, ContactDetailsActivity.class);
      intent.putExtra(EXTRA_CONTACT_ID, contactId);
      context.startActivity(intent);
      Bungee.slideRight(context);
   }
   
}
