package com.tr.hsyn.telefonrehberi.util.phone;

import android.Manifest;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.event.CallNumberRequest;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;

import org.greenrobot.eventbus.EventBus;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import pub.devrel.easypermissions.EasyPermissions;

import static android.content.Context.TELEPHONY_SERVICE;


public class Phone{
   
   @SuppressLint({"MissingPermission", "HardwareIds"})
   public static String getImei(Context context){
      
      TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      return telephonyManager.getDeviceId();
   }
   
   @Nullable
   public static Drawable getIconForAccount(Context context, Account account){
      
      AuthenticatorDescription[] descriptions = AccountManager.get(context).getAuthenticatorTypes();
      PackageManager             pm           = context.getPackageManager();
      
      for(AuthenticatorDescription description : descriptions){
         
         if(description.type.equals(account.type)){
            
            return pm.getDrawable(description.packageName, description.smallIconId, null);
         }
      }
      return null;
   }
   
   public static boolean isAirplaneModeOn(@NonNull Context context){
      
      return Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
   }
   
   public static boolean isOnline(Context context){
      
      ConnectivityManager c = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      
      if(c == null) return false;
      
      NetworkInfo networkInfo = c.getActiveNetworkInfo();
      
      return networkInfo != null && networkInfo.isConnected();
   }
   
   public static boolean isWifiConnected(Context context){
      
      ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      
      if(connManager == null) return false;
      
      NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
      
      return mWifi != null && mWifi.isConnected();
   }
   
   public static boolean isScreenOn(Context context){
      
      if(context == null){
         return false;
      }
      
      DisplayManager displayManager = (DisplayManager) context.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
      
      if(displayManager == null){
         
         Logger.w("DisplayManager null");
         return false;
      }
      
      for(Display display : displayManager.getDisplays()){
         
         if(display.getState() != Display.STATE_OFF){
            
            return true;
         }
      }
      
      return false;
   }
   
   public static boolean isServiceRunning(Context context, Class<?> clazz){
      
      if(context == null) return false;
      
      final String serviceName = clazz.getName();
      
      ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
      
      if(manager == null) return false;
      
      for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
         
         if(serviceName.equals(service.service.getClassName())){
            
            return true;
         }
      }
      
      return false;
   }
   
   public static void openNotificationAccessSetting(Context context){
      
      Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
      context.startActivity(intent);
      
   }
   
   /**
    * Gerekli izinlerin verilip verilmediğini kontrol et.
    *
    * @return İzinler verilmiş ise {@code true}
    */
   public static boolean isPermissionsGrant(Context context, String... permissions){
      
      if(context == null) return false;
      
      return EasyPermissions.hasPermissions(context, permissions);
   }
   
   public static boolean isValidEmailAddress(String email){
      
      boolean result = true;
      
      try{
         
         InternetAddress emailAddr = new InternetAddress(email);
         emailAddr.validate();
      }
      catch(AddressException ex){
         result = false;
      }
      
      return result;
   }
   
   public static void requestCall(String number){
      
      EventBus.getDefault().post(new CallNumberRequest(number));
   }
   
   private static boolean isTelephonyEnabled(Context context){
      
      TelephonyManager mTelephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
      
      if(mTelephonyManager == null) return false;
   
      return mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
      
   }
   
   private static boolean checkBeforeCall(Context context){
      
      if(isAirplaneModeOn(context)){
         
         Toast.makeText(context, context.getString(R.string.msg_airplane_mode_on), Toast.LENGTH_SHORT).show();
         return false;
      }
      
      if(!isTelephonyEnabled(context)){
         
         Toast.makeText(context, context.getString(R.string.msg_simcard_not_ready), Toast.LENGTH_SHORT).show();
         return false;
      }
      
      return true;
   }
   
   public static void makeCall(Activity activity, String number){
      
      if(EasyPermissions.hasPermissions(activity, Manifest.permission.CALL_PHONE)){
         
         if(!checkBeforeCall(activity)){ return; }
         
         activity.startActivity(new Intent(Intent.ACTION_CALL).setData(Uri.parse("tel:" + Uri.encode(number))));
         Toast.makeText(activity, activity.getString(R.string.calling, number), Toast.LENGTH_SHORT).show();
      }
   }
   
}
