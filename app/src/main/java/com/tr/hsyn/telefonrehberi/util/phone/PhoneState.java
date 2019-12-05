package com.tr.hsyn.telefonrehberi.util.phone;

import android.content.Context;


public class PhoneState{
   
   //private Context          context;
   private boolean          screenON;
   private boolean          wifiON;
   private boolean          airplaneModeON;
   private BatteryState     batteryState;
   //private List<UsageStats> lastApps;
   
   public PhoneState(Context context) {
      //this.context = context;
      
      screenON = Phone.isScreenOn(context);
      wifiON = Phone.isWifiConnected(context);
      airplaneModeON = Phone.isAirplaneModeOn(context);
      //lastApps = UsageManager.getInstance(context).getLastUsageStats(60_000);
      batteryState = new BatteryState(context);
   }
   
   public boolean isScreenON() {
      return screenON;
   }
   
   public boolean isWifiON() {
      return wifiON;
   }
   
   public boolean isAirplaneModeON() {
      return airplaneModeON;
   }
   
   public BatteryState getBatteryState() {
      return batteryState;
   }
   
}
