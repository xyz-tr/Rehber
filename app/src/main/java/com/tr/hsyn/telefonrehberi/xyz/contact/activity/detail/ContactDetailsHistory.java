package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.ShowCallsActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.Kickback_CallsConst;

import java.util.ArrayList;
import java.util.Collection;


public abstract class ContactDetailsHistory extends ContactDetailsAccount{
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
   
      if(contact.getNumbers() != null){
   
         setupHistory();
      }
      
   }
   
   /**
    * Kişinin arama kayıtlarını açacak olan görseli ayarla.
    * Eğer kişinin hiç arama kaydı yoksa 'geçmiş yok' diyecek.
    * Yani burası numarası olan her kişi için çalışacak.
    * Ancak eğer numarası yoksa ya da arama kaydı izinleri verilmemiş ise görsel kaldırılacak.
    */
   private void setupHistory(){
      
      Kickback_CallsConst callsConst = Kickback_CallsConst.getInstance();
      //arama kayıtları alınmamışsa dön
      if(callsConst.getCallStory() == null){
         
         //show_history_layout zaten GONE, sıkıntı yok
         return;
      }
      
      View show_history_layout = findViewById(R.id.show_history_layout);
      View showHistory         = findViewById(R.id.contact_history_item);
      View progressBar         = findViewById(R.id.historyProgress);
      
      final ImageView view = findViewById(R.id.contact_history_icon);
      u.setTintDrawable(view.getDrawable(), u.lighter(getPrimaryColor(), .3f));
      
      show_history_layout.setVisibility(View.VISIBLE);
      
      if(Phone.isPermissionsGrant(this, Manifest.permission.READ_CALL_LOG)){
         
         if(!contact.getNumbers().isEmpty()){
            
            showHistory.setBackgroundResource(wellRipple);
            progressBar.setVisibility(View.VISIBLE);
            
            showHistory.setOnClickListener((v) -> Worker.onMain(this::openHistory, 300));
            
            Worker.onBackground(() -> {
               
               assert contact.getNumberList() != null;
               
               if(callsConst.getCallStory().getCalls(contact.getNumberList()).size() == 0){
                  
                  runOnUiThread(() -> ((TextView) showHistory.findViewById(R.id.text)).setText(getString(R.string.no_history)));
               }
               
               runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }, "ContactDetailsBase:Rehber kaydına ait arama kaydı olup olmadığını kontrol etme");
         }
         else{
            
            show_history_layout.setVisibility(View.GONE);
         }
      }
      else{
         
         show_history_layout.setVisibility(View.GONE);
      }
   }
   
   /**
    * Kişinin arama kayıtlarını aç.
    */
   protected void openHistory(){
      
      Collection<String> numbers = contact.getNumberList();
      
      if(numbers == null){ return; }
      
      Intent i = new Intent(this, ShowCallsActivity.class);
      i.putExtra(ShowCallsActivity.EXTRA_NUMBERS, new ArrayList<>(numbers));
      i.putExtra(ShowCallsActivity.EXTRA_TITLE, contact.getName());
      
      Worker.onMain(() -> {
         
         startActivity(i);
         Bungee.slideRight(this);
      }, 150);
   }
   
   /**
    * Verilen arama türünün string karşılığını ver.
    *
    * @param type Arama türü
    * @return String karşılığı. Gelen giden vs.
    */
   private String getCallTypeString(int type){
      
      switch(type){
         
         case Type.INCOMMING: return getString(R.string.incomming);
         case Type.OUTGOING: return getString(R.string.outgoing);
         case Type.MISSED: return getString(R.string.missed);
         case Type.REJECTED: return getString(R.string.rejected);
         case Type.BLOCKED: return getString(R.string.blocked);
         
         default: return "";
      }
   }
   
   /**
    * Geçmişi arama türüne göre aç.
    *
    * @param type Arama türü
    */
   protected void openHistory(int type){
      
      String subtitle = getCallTypeString(type);
      
      if(subtitle.isEmpty()){
         
         openHistory();
         return;
      }
      
      Intent i = new Intent(this, ShowCallsActivity.class);
      i.putExtra(ShowCallsActivity.EXTRA_NUMBERS, new ArrayList<>(contact.getNumberList()));
      i.putExtra(ShowCallsActivity.EXTRA_TYPE, type);
      i.putExtra(ShowCallsActivity.EXTRA_TITLE, contact.getName());
      i.putExtra(ShowCallsActivity.EXTRA_SUB_TITLE, subtitle + " ");
      
      Worker.onMain(() -> {
         
         startActivity(i);
         Bungee.slideRight(this);
      }, 150);
   }
   
}
