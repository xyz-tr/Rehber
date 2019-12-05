package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.xyz.main.page.ShowMe;

import lombok.Getter;


/**
 * <h1>ContactsFragmentShow</h1>
 * Bu fragment, sahnede olup olmama durumuna göre yapılması gerekenleri yapar.
 * 
 * @author hsyn 2019-11-02 11:05:46
 */
public abstract class ContactsShow extends ContactsBase implements ShowMe{
   
   
   @Getter private boolean isShowTime;
   
   @Override
   public void showTime(boolean isShowTime){
      
      this.isShowTime = isShowTime;
      
      if(!isShowTime) return;
      
      setTitle();
      
      if(getContext() == null) return;
      
      if(!Phone.isPermissionsGrant(getContext(), getPermissions()))
         requestPermissions();
   }
   
   /**
    * Rehber için gerekli izinleri kullanıcıya sor.
    */
   protected abstract void requestPermissions();
}
