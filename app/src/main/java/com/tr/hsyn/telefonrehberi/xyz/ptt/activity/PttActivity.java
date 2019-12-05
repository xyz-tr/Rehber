package com.tr.hsyn.telefonrehberi.xyz.ptt.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.main.page.PageAdapter;
import com.tr.hsyn.telefonrehberi.xyz.ptt.fragment.InboxFragment;
import com.tr.hsyn.telefonrehberi.xyz.ptt.fragment.OutboxFragment;
import com.tr.hsyn.telefonrehberi.xyz.ptt.fragment.SentFragment;

import lombok.val;


public class PttActivity extends AppCompatActivity{
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_ptt);
      
      val fragments = new Fragment[]{
            
            new OutboxFragment(),
            new InboxFragment(),
            new SentFragment()
      };
      
      String[] titles = {"Giden", "Gelen", "Gönderilen"};
      
      PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments, titles);
      
      ViewPager viewPager = findViewById(R.id.view_pager);
      
      viewPager.setOffscreenPageLimit(3);
      viewPager.setAdapter(pageAdapter);
      
      
      TabLayout tabs = findViewById(R.id.tabs);
      tabs.setupWithViewPager(viewPager);
      
      
   }
}