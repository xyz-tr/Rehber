package com.tr.hsyn.telefonrehberi.xyz.call.detail;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.simmorsal.recolor_project.ReColor;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;


public class NotificationsNoSignActivity extends AppCompatActivity{
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_notifications_no_sign);
      
      new ReColor(this).setStatusBarColor(null, u.colorToString(u.getColor(this, R.color.yellow_700)), 1000);
   }
}
