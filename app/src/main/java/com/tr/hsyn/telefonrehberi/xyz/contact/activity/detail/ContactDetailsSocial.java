package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.SocialAccountInfo;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.val;
import timber.log.Timber;


public abstract class ContactDetailsSocial extends ContactDetailsGroups{
   
   /**
    * Sosyal hesap bilgileri için kullanılacak
    */
   protected            String[]                      DATA_COLS               = {
         
         ContactsContract.Data._ID,
         ContactsContract.Data.CONTACT_ID,
         ContactsContract.Data.MIMETYPE,
         ContactsContract.Data.DATA3,
         ContactsContract.Data.DATA2
   };
   /**
    * Kişinin sosyal hesap bilgilerini tutacak
    */
   private final        Collection<SocialAccountInfo> socialAccountInfos      = new ArrayList<>();
   /**
    * Whatsapp'ın mesajlar için kullandığı tür
    */
   private static final String                        WHATSAPP_MIMETYPE_CHAT  = "vnd.android.cursor.item/vnd.com.whatsapp.profile";
   /**
    * Whatsapp'ın sesli arama için kullandığı tür
    */
   private static final String                        WHATSAPP_MIMETYPE_VOICE = "vnd.android.cursor.item/vnd.com.whatsapp.video.call";
   /**
    * Whatsapp'ın video araması için kullandığı tür
    */
   private static final String                        WHATSAPP_MIMETYPE_VIDEO = "vnd.android.cursor.item/vnd.com.whatsapp.voip.call";
   /**
    * Whatsapp türleri
    */
   protected            String[]                WHATS_APP_MIMETYPES      = {
         
         WHATSAPP_MIMETYPE_CHAT,
         WHATSAPP_MIMETYPE_VOICE,
         WHATSAPP_MIMETYPE_VIDEO
   };
   /**
    * Facebook'un mesajlar için kullandığı tür
    */
   private static final String                  MESSENGER_MIMETYPE_CHAT  = "vnd.android.cursor.item/com.facebook.messenger.chat";
   /**
    * Facebook'un sesli arama için kullandığı tür
    */
   private static final String                  MESSENGER_MIMETYPE_VOICE = "vnd.android.cursor.item/com.facebook.messenger.audiocall";
   /**
    * Facebook'un video araması için kullandığı tür
    */
   private static final String                  MESSENGER_MIMETYPE_VIDEO = "vnd.android.cursor.item/com.facebook.messenger.videocall";
   /**
    * Messenger türleri
    */
   protected            String[]                MESSENGER_MIMETYPES      = {
         
         MESSENGER_MIMETYPE_CHAT,
         MESSENGER_MIMETYPE_VOICE,
         MESSENGER_MIMETYPE_VIDEO
   };
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      getSocialInfos();
   }
   
   /**
    * Kişinin facebook ve whatsapp hesapları kontrol ediliyor
    */
   private void getSocialInfos(){
      
      Worker.onBackground(() -> {
         
         getSocialInfo(ContactDetailsSocial.SocialType.WHATSAPP);
         getSocialInfo(ContactDetailsSocial.SocialType.MESSENGER);
         
      }, "ContactDetailsSetup:Kişinin sosyal hesaplarını alma - " + contact.getName());
   }
   
   /**
    * Kişinin facebook ve ya whatsapp hesabı kontrol ediliyor.
    *
    * @param socialType Sosyal hesap türü {@link ContactDetailsSocial.SocialType}
    */
   private void getSocialInfo(ContactDetailsSocial.SocialType socialType){
      
      Uri      uri           = ContactsContract.Data.CONTENT_URI;
      String[] selectionArgs = {contact.getContactId(), WHATS_APP_MIMETYPES[0], WHATS_APP_MIMETYPES[1], WHATS_APP_MIMETYPES[2]};
      String   selection     = u.format("%s =? and (%s =? OR %s =? OR %s =?)", DATA_COLS[1], DATA_COLS[2], DATA_COLS[2], DATA_COLS[2]);
      
      switch(socialType){
         
         case WHATSAPP:
            
            break;
         
         case MESSENGER:
            
            selectionArgs = new String[]{contact.getContactId(), MESSENGER_MIMETYPES[0], MESSENGER_MIMETYPES[1], MESSENGER_MIMETYPES[2]};
            break;
      }
      
      Cursor cursor = getContentResolver().query(
            uri,
            DATA_COLS,
            selection,
            selectionArgs,
            null
      );
      
      if(cursor == null){ return; }
      
      if(cursor.getCount() == 0){
         
         cursor.close();
         return;
      }
      
      getSocialInfo(cursor, socialType);
   }
   
   /**
    * Kişinin sosyal hesabı var ve bu hesaba dair arama yapma bilgileri alınıyor.
    *
    * @param cursor     Cursor
    * @param socialType Sosyal hesap türü
    */
   private void getSocialInfo(@NonNull Cursor cursor, ContactDetailsSocial.SocialType socialType){
      
      //final List<SocialAccountInfo> socialAccountInfoList = new ArrayList<>();
      List<String> numbers = null;
      
      if(socialType == ContactDetailsSocial.SocialType.WHATSAPP){
         
         numbers = getWhatsAppNumbers(cursor);
      }
      
      cursor.moveToFirst();
      
      for(int i = 0; i < cursor.getCount() / 3; i++){
         
         String number = null;
         
         if(numbers != null){
            
            number = numbers.get(i);
         }
         else{
            
            val _numbers = contact.getNumbers();
            
            if(_numbers != null && _numbers.size() != 0){
               
               number = _numbers.get(0).getNumber();
            }
         }
         
         SocialAccountInfo socialAccountInfo = new SocialAccountInfo();
         socialAccountInfo.setNumber(number);
         socialAccountInfo.setSocialType(socialType);
         
         do{
            
            if(socialType == ContactDetailsSocial.SocialType.WHATSAPP){
               
               String _data3  = cursor.getString(cursor.getColumnIndex(DATA_COLS[3]));
               String _number = _data3.replaceAll("[a-zA-ZüöışçğÜÖŞĞÇ ]", "");
               
               if(!_number.equals(number)){ continue; }
               
            }
            
            String mimetype = cursor.getString(cursor.getColumnIndex(DATA_COLS[2]));
            String dataId   = cursor.getString(cursor.getColumnIndex(DATA_COLS[0]));
            
            if(socialType == ContactDetailsSocial.SocialType.WHATSAPP){
               
               switch(mimetype){
                  
                  case WHATSAPP_MIMETYPE_CHAT:
                     
                     socialAccountInfo.setChatDataId(dataId);
                     break;
                  
                  case WHATSAPP_MIMETYPE_VOICE:
                     
                     socialAccountInfo.setVoiceDataId(dataId);
                     break;
                  
                  case WHATSAPP_MIMETYPE_VIDEO:
                     
                     socialAccountInfo.setVideoDataId(dataId);
                     break;
               }
            }
            else if(socialType == ContactDetailsSocial.SocialType.MESSENGER){
               
               switch(mimetype){
                  
                  case MESSENGER_MIMETYPE_CHAT:
                     
                     socialAccountInfo.setChatDataId(dataId);
                     break;
                  
                  case MESSENGER_MIMETYPE_VOICE:
                     
                     socialAccountInfo.setVoiceDataId(dataId);
                     break;
                  
                  case MESSENGER_MIMETYPE_VIDEO:
                     
                     socialAccountInfo.setVideoDataId(dataId);
                     break;
               }
            }
         }
         while(cursor.moveToNext());
         
         socialAccountInfos.add(socialAccountInfo);
      }
      
      cursor.close();
   }
   
   /**
    * Kişinin whatsapp numaraları.
    *
    * @param cursor cursor
    * @return numaralar
    */
   private List<String> getWhatsAppNumbers(Cursor cursor){
      
      List<String> numbers = new ArrayList<>();
      
      while(cursor.moveToNext()){
         
         String data3  = cursor.getString(cursor.getColumnIndex(DATA_COLS[3]));
         String number = data3.replaceAll("[a-zA-ZüöışçğÜÖŞĞÇ ]", "");
         
         if(!numbers.contains(number)){ numbers.add(number); }
      }
      
      return numbers;
   }
   
   
   @Override
   protected void onAccountClick(View v){
      
      u.keepGoing(() -> {
         
         Account account = (Account) v.getTag();
         
         if(account == null) return;
         
         int[] location = new int[2];
         v.getLocationInWindow(location);
         
         switch(account.name){
            
            case "WhatsApp":
               
               showSocialAccountPopup(location, account, SocialType.WHATSAPP);
               break;
            
            case "Messenger":
               
               showSocialAccountPopup(location, account, SocialType.MESSENGER);
               break;
         }
      });
   }
   
   /**
    * Sosyal bir hesaba tıklandığında açılacak popup ayarlanıyor.
    *
    * @param location   Popup'ın açılacağı yer
    * @param account    Hesap
    * @param socialType Sosyal Tür
    */
   @SuppressLint("SetTextI18n")
   private void showSocialAccountPopup(@NonNull final int[] location, @NonNull final Account account, @NonNull final SocialType socialType){
      
      final PopupWindow popup = new PopupWindow(this);
      
      @SuppressLint("InflateParams") ViewGroup layout    = (ViewGroup) getLayoutInflater().inflate(R.layout.contact_detail_account_popup_layout, null, false);
      ViewGroup                                popupRoot = layout.findViewById(R.id.popupRootLinearLayout);
      
      for(SocialAccountInfo accountInfo : socialAccountInfos){
         
         if(accountInfo.getSocialType() != socialType){ continue; }
         
         View view = getLayoutInflater().inflate(R.layout.contact_detail_account_popup_item, popupRoot, false);
         
         View      headerBackground = view.findViewById(R.id.headerBackground);
         ImageView accountIcon      = view.findViewById(R.id.accountIcon);
         TextView  acountHeader     = view.findViewById(R.id.accountText);
         
         
         accountIcon.setImageDrawable(Phone.getIconForAccount(this, account));
         
         if(accountInfo.getSocialType() == SocialType.WHATSAPP){
            
            val title = Stringx.format("%s %s", CallStory.formatNumberForDisplay(accountInfo.getNumber()), Contacts.getNumberTypeString(this, contact.getNumber(accountInfo.getNumber()).getType()));
            acountHeader.setText(title);
            
            ImageView message   = view.findViewById(R.id.messageIcon);
            ImageView voiceCall = view.findViewById(R.id.voiceCallIcon);
            ImageView videoCall = view.findViewById(R.id.videoCallIcon);
            
            message.setImageResource(R.drawable.whatsapp_message);
            voiceCall.setImageResource(R.drawable.whatsapp_voice_call);
            videoCall.setImageResource(R.drawable.whatsapp_video_call);
         }
         else{
            
            acountHeader.setText("Messenger");
         }
         
         
         popupRoot.addView(view);
         
         headerBackground.setBackgroundColor(u.lighter(u.getPrimaryColor(this), .2f));
         
         View chat  = view.findViewById(R.id.chatParent);
         View voice = view.findViewById(R.id.voiceParent);
         View video = view.findViewById(R.id.videoParent);
         
         chat.setTag(accountInfo.getChatDataId());
         voice.setTag(accountInfo.getVoiceDataId());
         video.setTag(accountInfo.getVideoDataId());
         
         chat.setBackgroundResource(wellRipple);
         voice.setBackgroundResource(wellRipple);
         video.setBackgroundResource(wellRipple);
         
         chat.setOnClickListener((v) -> onSosialAccountIClick(v, popup));
         voice.setOnClickListener((v) -> onSosialAccountIClick(v, popup));
         video.setOnClickListener((v) -> onSosialAccountIClick(v, popup));
         
      }
      
      DisplayMetrics displayMetrics = new DisplayMetrics();
      getWindow().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
      
      int w = displayMetrics.widthPixels - 150;
      //int h = displayMetrics.heightPixels / 10;
      
      popup.setContentView(layout);
      popup.setFocusable(true);
      popup.setOutsideTouchable(true);
      popup.setWidth(w);
      //popup.setHeight(h);
      popup.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.contact_detail_account_popup_background));
      popup.setElevation(50);
      popup.setAnimationStyle(R.style.ContactDetailPopupAnimation);
      
      popup.showAtLocation(layout, Gravity.NO_GRAVITY, location[0] + 30, location[1] - 50);
      
   }
   
   /**
    * Kişinin bağlı olduğu sosyal hesaplardan birine dokunulmuştur.
    *
    * @param view  Dokunulan nesne
    * @param popup Sosyal hesaplar için popup açmak için
    */
   private void onSosialAccountIClick(View view, PopupWindow popup){
      
      String dataId = (String) view.getTag();
      Uri    uri    = Uri.withAppendedPath(ContactsContract.Data.CONTENT_URI, dataId);
      
      Cursor cursor = getContentResolver().query(
            uri,
            DATA_COLS,
            null,
            null,
            null
      
      );
      
      if(cursor == null){ return; }
      
      if(cursor.moveToFirst()){
         
         final String mimetype = cursor.getString(cursor.getColumnIndex(DATA_COLS[2]));
         
         SocialType socialType = SocialType.MESSENGER;
         
         if(mimetype.contains("com.whatsapp")){
            
            socialType = SocialType.WHATSAPP;
         }
         
         cursor.close();
         
         SocialType finalSocialType = socialType;
         Worker.onMain(() -> openSocialApp(mimetype, finalSocialType), 100);
         
         Worker.onMain(popup::dismiss, 600);
      }
   }
   
   /**
    * Sosyal uygulamayı başlatır.
    *
    * @param mimetype   Başlatmanın türü. Mesaj, video araması, normal arama
    * @param socialType Sosyal tür
    */
   private void openSocialApp(String mimetype, SocialType socialType){
      
      /*
       *  WhatsApp package : com.whatsapp
       *
       *  vnd.android.cursor.item/vnd.com.whatsapp.profile - message
       *  vnd.android.cursor.item/vnd.com.whatsapp.voip.call - video call
       *  vnd.android.cursor.item/vnd.com.whatsapp.video.call - voice call
       *
       *  ****************************************************
       *
       *  Messenger package : com.facebook.orca
       *
       *  vnd.android.cursor.item/com.facebook.messenger.chat
       *  vnd.android.cursor.item/com.facebook.messenger.audiocall
       *  vnd.android.cursor.item/com.facebook.messenger.videocall
       *
       */
      
      String packageName = "com.whatsapp";
      
      switch(socialType){
         
         case WHATSAPP:
            break;
         case MESSENGER:
            
            packageName = "com.facebook.orca";
            break;
      }
      
      
      Uri      uri           = ContactsContract.Data.CONTENT_URI;
      String[] cols          = {ContactsContract.Data._ID, ContactsContract.Data.CONTACT_ID, ContactsContract.Data.MIMETYPE};
      String   selection     = u.format("%s =? and %s =?", cols[1], cols[2]);
      String[] selectionArgs = {contact.getContactId(), mimetype};
      Context  context       = getApplicationContext();
      
      
      assert context != null;
      Cursor cursor = context.getContentResolver().query(
            uri,
            cols,
            selection,
            selectionArgs,
            null
      );
      
      if(cursor == null){
         
         Timber.w("cursor = null");
         return;
      }
      
      
      if(cursor.moveToNext()){
         
         String dataId  = cursor.getString(cursor.getColumnIndex(cols[0]));
         Uri    dataUri = Uri.withAppendedPath(uri, dataId);
         
         cursor.close();
         
         Intent socialIntent = new Intent(Intent.ACTION_VIEW);
         socialIntent.setDataAndType(dataUri, mimetype);
         socialIntent.setPackage(packageName);
         
         startActivity(socialIntent);
      }
   }
   
   
   /**
    * Sosyal hesapları temsil edecek
    */
   public enum SocialType{
      
      WHATSAPP, MESSENGER
   }
   
   
}
