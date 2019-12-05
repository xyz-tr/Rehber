package com.tr.hsyn.telefonrehberi.xyz.ptt.activity.relation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_ContactBox;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.paperdb.Paper;
import timber.log.Timber;


public class RelationsActivity extends AppCompatActivity{
   
   private volatile List<IMainContact> contacts;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_relations);
      
      contacts = Kickback_ContactBox.getInstance().getContacts();
      checkContacts();
   }
   
   @Override
   protected void onDestroy(){
      
      EventBus.getDefault().unregister(this);
      super.onDestroy();
   }
   
   
   private void checkContacts(){
      
      
      Timber.d("Rehber kayıtları alındı [%d]", contacts.size());
      
      String pref = "relation";
      String key  = "relations";
      
      List<Relation> relations = Paper.book(pref).read(key, null);
      
      if(relations == null){
         
         Timber.d("Daha önce kayıtlı bağlantı yok");
      }
      
   }
}
