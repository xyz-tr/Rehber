package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;

import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.xyz.main.page.ShowMe;

import lombok.AccessLevel;
import lombok.Getter;


public abstract class CallLogShowMe extends CallLogView implements ShowMe{
   
   /**
    * Bu sayfa mı gösterimde
    */
   @Getter(AccessLevel.PROTECTED) private boolean isShowTime;
   
   @Override
   public void showTime(boolean isShowTime){
   
      this.isShowTime = isShowTime;
   
      if(this.isShowTime){
   
         if(getActivity() == null) return;
         
         setTitle();
   
         if(!Phone.isPermissionsGrant(getContext(), getCallsPermissions())){
      
            requestCallPermissions();
         }
         else if(!isAnyLoad()){
            
            loadCalls();
         }
      }
      else{
         //ShowTime olmasa bile
         //Eğer izin varsa ve daha önce yükleme yapılmamışsa yüklemeyi başlat
         if(Phone.isPermissionsGrant(getContext(), getCallsPermissions()) && !isAnyLoad()){
            
            loadCalls();
         }
      }
   }
   
   protected abstract String[] getCallsPermissions();
   
   /**
    * Kayıtların yüklenmesi hiç denenmiş mi?
    * 
    * @return yükleme yapılmışsa {@code true}
    */
   protected abstract boolean isAnyLoad();
   
   /**
    * Kayıtları yükle
    */
   protected abstract void loadCalls();
   
   protected abstract void setTitle();
   
   abstract protected void requestCallPermissions();
   
}
