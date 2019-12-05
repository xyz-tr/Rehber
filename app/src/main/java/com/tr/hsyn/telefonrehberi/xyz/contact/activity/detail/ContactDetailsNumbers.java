package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Message;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.INumber;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.List;

import lombok.val;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


public abstract class ContactDetailsNumbers extends ContactDetailsView{
   
   /**
    * Detayları gösterilecek olan rehber kaydı
    */
   protected              IPhoneContact contact;
   protected              int           wellRipple       = MainActivity.getWellRipple();
   private                String        numberToCall;
   
   /**
    * Kişinin {@linkplain ContactsContract.Contacts#_ID} değeri
    */
   public final static    String        EXTRA_CONTACT_ID = "contact_id";
   /**
    * Kişinin bilgileri güncellendiğinde kullanılacak olan {@code requestCode}.
    */
   protected final static int           RC_EDIT_CONTACT  = 4;
   protected final static int           RC_CALL          = 34;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      //İlk iş activity'nin başlama sebebi olan kaydı almak
      contact = getPhoneContact();
      
      
      if(contact == null){
         
         u.toast(this, "Böyle bir kişi yok");
         onBackPressed();
         return;
      }
      
      Timber.d(contact.toString());
      setupNumbers();
      setTitle();
   
      Worker.onBackground(() -> onDetailedContactFound(contact));
   }
   
   /**
    * Detayları gösterilecek kişi tespit edildiğinde arkaplanda çağrılır.
    * 
    * @param phoneContact contact
    */
   @WorkerThread
   protected void onDetailedContactFound(@SuppressWarnings("unused") IPhoneContact phoneContact){}
   
   private IPhoneContact getPhoneContact(){
      
      val intent = getIntent();
      
      if(intent == null || !intent.hasExtra(EXTRA_CONTACT_ID)){
         
         u.toast(this, "Kişinin id değeri yok");
         return null;
      }
      
      val contactId = intent.getStringExtra(EXTRA_CONTACT_ID);
      
      return Contacts.getPhoneContact(getContentResolver(), contactId);
   }
   
   /**
    * Kişinin numaraları varsa göster,
    * yok ise kayıtlı numara olmadığını göster.
    */
   private void setupNumbers(){
      
      final List<INumber> numbers = contact.getNumbers();
      
      if(numbers == null){
         
         final ViewGroup numbersLayout = findViewById(R.id.numbers);
         
         final View noNumber = getLayoutInflater().inflate(R.layout.contact_detail_no_number, numbersLayout, false);
         
         numbersLayout.addView(noNumber);
         
         final View rootRelative = noNumber.findViewById(R.id.rootRelativeLayout);
         rootRelative.setBackgroundResource(wellRipple);
         rootRelative.setOnClickListener((e) -> onEditContact());
         
         final ImageView icon = rootRelative.findViewById(R.id.icon);
         u.setTintDrawable(icon.getDrawable(), getPrimaryColor());
         return;
      }
      
      final ViewGroup numbersLayout = findViewById(R.id.numbers);
      
      for(INumber contactNumber : numbers){
         
         final String   number     = Contacts.normalizeNumber(contactNumber.getNumber());
         final View     numberItem = getLayoutInflater().inflate(R.layout.number_item, numbersLayout, false);
         final TextView numberText = numberItem.findViewById(R.id.number);
         TextView       type       = numberItem.findViewById(R.id.type);
         ImageView      makeCall   = numberItem.findViewById(R.id.makeCall);
         ImageView      messages   = numberItem.findViewById(R.id.title_layout);
         
         numberItem.setBackgroundResource(MainActivity.getWellRipple());
         
         type.setText(getPhoneType(contactNumber.getType()));
         
         numberItem.findViewById(R.id.rootRelativeLayout).setOnLongClickListener(v -> u.copyToClipboard(this, number));
         numberText.setText(CallStory.formatNumberForDisplay(contactNumber.getNumber()));
         
         numbersLayout.addView(numberItem);
         
         messages.setOnClickListener((v) -> openMessages(number));
         makeCall.setOnClickListener((v) -> call(number));
         
         u.setTintDrawable(makeCall.getDrawable(), getPrimaryColor());
         u.setTintDrawable(messages.getDrawable(), getPrimaryColor());
      }
   }
   
   /**
    * Kişiyi edit etmek için telefon uygulamasına postala
    */
   protected final void onEditContact(){
      
      try{
         
         Intent intent = new Intent(Intent.ACTION_EDIT);
         intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contact.getContactId())));
         intent.putExtra("finishActivityOnSaveCompleted", true);
         
         startActivityForResult(intent, RC_EDIT_CONTACT);
      }
      catch(Exception e){
         
         Message.builder()
                .message("Bu işlem için gereken uygulama yüklü değil")
                .build()
                .showOn(this);
      }
   }
   
   /**
    * Kişinin telefon numarası türünün string karşılığını döndür.
    *
    * @param type Tür
    * @return Cep, Ev, İş veya Diğer
    */
   protected String getPhoneType(int type){
      
      switch(type){
         
         case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
            
            return getString(R.string.type_mobile);
         
         case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
            
            return getString(R.string.type_home);
         
         case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
            
            return getString(R.string.type_work);
         
         default:
            return getString(R.string.type_other);
      }
   }
   
   private void call(String number){
      
      if(EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)){
         
         Phone.makeCall(this, number);
      }
      else{
         
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            
            numberToCall = number;
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RC_CALL);
         }
      }
   }
   
   /**
    * Verilen numara için mesajları aç.
    *
    * @param number Numara
    */
   protected void openMessages(String number){
      
      try{
         
         startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("sms:" + Uri.encode(number))));
         Toast.makeText(getApplicationContext(), getString(R.string.messages_opening, number), Toast.LENGTH_SHORT).show();
      }
      catch(ActivityNotFoundException e){
         
         Show.globalMessage(this, "İşlemi gerçekleştirecek bir uygulama yok");
      }
   }
   
   /**
    * Toolbar'da kişinin ismi görünecek.
    * Eğer kişinin ismi {@code null} veya boş string ise 'İsimsiz' olarak adlandırılacak.
    */
   private void setTitle(){
      
      String name = contact.getName();
      
      if(name != null && !name.trim().isEmpty()){
         
         collapsingToolbarLayout.setTitle(name);
      }
      else{
         
         collapsingToolbarLayout.setTitle(getString(R.string.no_name));
      }
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, ContactDetailsNumbers.this);
   }
   
   /**
    * Verilen numarayı ara.
    * Arama sisteme yönlendirilmeden önce burada küçük bir hazırlık yapılıyor.
    */
   @AfterPermissionGranted(RC_CALL)
   protected void call(){
      
      String number = numberToCall;
      numberToCall = null;
      
      if(number != null){
         
         Phone.makeCall(this, number);
      }
   }
}
