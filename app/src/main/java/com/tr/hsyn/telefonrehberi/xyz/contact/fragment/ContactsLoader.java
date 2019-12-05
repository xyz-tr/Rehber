package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public abstract class ContactsLoader extends ContactsShow{
   
   
   /**
    * Rehber izinleri için kullanılacak request code
    */
   static final int REQUEST_CODE_CONTACT_PERMISSIONS = 33;
   
   @Override
   protected void requestPermissions(){
      
      requestPermissions(permissions, REQUEST_CODE_CONTACT_PERMISSIONS);
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
   }
   
   @AfterPermissionGranted(REQUEST_CODE_CONTACT_PERMISSIONS)
   protected void onPermissionsGranted(){
      
      onContactsLoading();
   
      Logger.i("Granted Contact Permissions%n" +
                       "===========================%n" +
               "%s", TextUtils.join("\n", getPermissions()));
      
      Worker.onBackground(this::getContacts, "ContactsFragmentContactsLoader:Rehber kayıtlarını alma")
            .whenCompleteAsync(this::onContactsLoaded, Worker.getMainThreadExecutor());
   }
   
   abstract protected List<IMainContact> getContacts();
   
   abstract protected void onContactsLoaded(List<IMainContact> contacts, Throwable throwable);
   
   abstract void onContactsLoading();
}
