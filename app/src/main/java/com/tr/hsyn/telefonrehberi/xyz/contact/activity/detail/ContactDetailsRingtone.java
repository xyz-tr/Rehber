package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import lombok.val;
import timber.log.Timber;


public abstract class ContactDetailsRingtone extends ContactDetailsMenu{
   
   /**
    * Kişinin zil sesi değiştirilmek istendiğinde kullanılacak olan {@code requestCode}.
    */
   protected static final int REQUEST_CODE_NEW_CONTACT_RINGTONE = 1;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
   
      if(contact.getNumbers() != null){
   
         setUpRingTone();
      }
   }
   
   /**
    * Kişinin zil sesi bilgileri gösteriliyor.
    */
   private void setUpRingTone(){
      
      val changeRingtoneContainer = findViewById(R.id.change_ringtone_container);
      
      if(getProperAccount() == null){
         
         return;
      }
      
      changeRingtoneContainer.setVisibility(View.VISIBLE);
      
      val      changeRingtone = findViewById(R.id.change_ringtone);
      TextView ringtoneName   = findViewById(R.id.ringtone);
      
      try{
         
         String ringtone = getRingtoneOrDefault();
         
         if(ringtone == null){
            
            changeRingtoneContainer.setVisibility(View.GONE);
            Timber.w("Zil sesi alınamadı");
            return;
         }
         
         ringtoneName.setText(ringtone);
         ImageView icon = findViewById(R.id.ringtoneIcon);
         u.setTintDrawable(icon.getDrawable(), u.lighter(u.getPrimaryColor(this), .3f));
         
         changeRingtone.setBackgroundResource(MainActivity.getWellRipple());
         changeRingtone.setOnClickListener(this::onClickChangeRingtone);
      }
      catch(Exception e){
         
         Timber.e("Kişi bilgisi alınamadı");
      }
   }
   
   /**
    * Kişinin seçilmiş zil sesini döndür.
    * Yoksa varsayılan zil sesini döndür.
    *
    * @return zil sesinin ismi
    */
   private String getRingtoneOrDefault(){
      
      String ringtone = Contacts.getRingTone(this, contact.getContactId());
      
      if(ringtone == null){
         
         Uri defaultRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
         
         ringtone = RingtoneManager.getRingtone(this, defaultRingtone).getTitle(this);
      }
      else{
         
         ringtone = RingtoneManager.getRingtone(this, Uri.parse(ringtone)).getTitle(this);
      }
      
      return ringtone;
   }
   
   /**
    * Kişinin zil sesi değiştirilmek istendiğinde.
    *
    * @param view Tıklanan view
    */
   protected void onClickChangeRingtone(@SuppressWarnings("unused") View view){
      
      openSystemRingToneDialog(REQUEST_CODE_NEW_CONTACT_RINGTONE);
   }
   
   /**
    * Sistemin zil sesi değiştirme dialoğunu başlatır.
    *
    * @param requestCode Yapılan isteği karşılamak için kullanılacak kimlik numarası
    */
   protected final void openSystemRingToneDialog(int requestCode){
      
      try{
         
         Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
         intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Zil sesi seç");
         intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
         
         startActivityForResult(intent, requestCode);
      }
      catch(Exception e){
         
         Timber.w(e);
      }
      
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      if(data == null) return;
      
      if(requestCode == REQUEST_CODE_NEW_CONTACT_RINGTONE && resultCode == RESULT_OK){
         
         Uri ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
         
         if(ringtoneUri != null){
            
            setRingtone(ringtoneUri);
         }
      }
   }
   
   /**
    * Kullanıcı kişinin zil sesini değiştirdiğinde,
    * seçilen yeni zil sesi kişinin bilgileri arasına kaydedilir.
    * Ve zil sesinin adının yazılı olduğu görsel öğe güncellenir.
    *
    * @param ringtoneUri Seçilen yeni zil sesi
    */
   private void setRingtone(Uri ringtoneUri){
      
      if(Contacts.setRingTone(this, contact.getContactId(), String.valueOf(ringtoneUri))){
         
         String name = RingtoneManager.getRingtone(this, ringtoneUri).getTitle(this);
         
         if(name != null){
            
            TextView ringtoneName = findViewById(R.id.ringtone);
            
            TransitionManager.beginDelayedTransition(findViewById(R.id.change_ringtone));
            ringtoneName.setText(name);
            onRingToneChanged(name);
         }
      }
   }
   
   /**
    * Kişinin zil sesi değiştiğinde.
    *
    * @param ringToneName Yeni zil sesinin ismi
    */
   protected abstract void onRingToneChanged(String ringToneName);
   
}
