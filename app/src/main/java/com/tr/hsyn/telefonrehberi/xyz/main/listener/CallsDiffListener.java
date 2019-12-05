package com.tr.hsyn.telefonrehberi.xyz.main.listener;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.Callback;
import com.tr.hsyn.telefonrehberi.xyz.main.analize.CallsDiffInfo;


public class CallsDiffListener implements Callback{
   
   @Override
   public void onCallsDiffResult(CallsDiffInfo callsDiffInfo){
   
      Logger.d(callsDiffInfo.getReport().toString());
   }
}
