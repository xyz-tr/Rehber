package com.tr.hsyn.telefonrehberi.xyz.main.page;

import androidx.viewpager.widget.ViewPager;


public interface PageChangeListener extends ViewPager.OnPageChangeListener{
   
   void onPageChange(final int pageIndex);
   
   @Override
   default void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){}
   
   @Override
   default void onPageSelected(int position){
      
      onPageChange(position);
   }
   
   @Override
   default void onPageScrollStateChanged(int state){}
}
