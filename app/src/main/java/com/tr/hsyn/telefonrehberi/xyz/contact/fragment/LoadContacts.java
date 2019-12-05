package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;


import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public abstract class LoadContacts extends ContactsLoader implements SwipeRefreshLayout.OnRefreshListener, EasyPermissions.PermissionCallbacks{
   
   @Override
   protected final List<IMainContact> getContacts(){
      
      return Contacts.getMainContacts(getContext(), getSelectedAccount());
   }
   
   @Override
   protected void onContactsLoaded(List<IMainContact> contacts, Throwable throwable){
      
      progressBar.setVisibility(View.GONE);
      refreshLayout.setRefreshing(false);
   }
   
   @Override
   void onContactsLoading(){
      
      progressBar.setVisibility(View.VISIBLE);
   }
   
   @Override
   protected void onViewInflated(View view){
      
      setRefresh(view.getContext());
   }
   
   @Override
   public void onPermissionsGranted(int requestCode, @NonNull List<String> perms){
      
   }
   
   @Override
   public void onPermissionsDenied(int requestCode, @NonNull List<String> perms){
   
      if(requestCode == REQUEST_CODE_CONTACT_PERMISSIONS){
   
         Toast.makeText(getContext(), "İzin Reddedildi", Toast.LENGTH_LONG).show();
      }
      
      
   }
   
   private void setRefresh(Context context){
      
      if(getContext() == null){ return; }
      
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeColors(u.getPrimaryColor(context));
   }
   
   @Override
   public void onRefresh(){
      
      setLastlistPosition(0);
      refreshLayout.setRefreshing(true);
      loadContacts();
   }
   
   @Override
   protected void loadContacts(){
      
      setLoadStartTime(System.currentTimeMillis());
      
      if(Phone.isPermissionsGrant(getContext(), getPermissions())){
         
         onPermissionsGranted();
      }
      else{
         
         requestPermissions();
      }
   }
   
   protected abstract void setLoadStartTime(long time);
}
