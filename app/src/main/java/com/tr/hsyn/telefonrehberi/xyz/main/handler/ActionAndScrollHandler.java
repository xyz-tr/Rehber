package com.tr.hsyn.telefonrehberi.xyz.main.handler;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tr.hsyn.telefonrehberi.util.ui.snack.SnackBarListener;
import com.tr.hsyn.telefonrehberi.xyz.main.MainConsts;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.FastScrollListener;
import com.tr.hsyn.telefonrehberi.xyz.main.page.Page;
import com.tr.hsyn.telefonrehberi.xyz.main.page.PageChangeListener;


public class ActionAndScrollHandler implements SnackBarListener, FastScrollListener, ViewPager.OnPageChangeListener, Page{
   
   private       boolean              isSnackbarOpen;
   private       boolean              isFastScrollStart;
   private       boolean              isActionbarHiden;
   private       FloatingActionButton actionButton;
   private       PageChangeListener[] pageChangeListeners;
   private ViewPager viewPager;
   
   public ActionAndScrollHandler(FloatingActionButton actionButton, PageChangeListener[] pageChangeListeners, ViewPager viewPager){
   
      this.actionButton        = actionButton;
      this.pageChangeListeners = pageChangeListeners;
      this.viewPager = viewPager;
      viewPager.addOnPageChangeListener(this);
   }
   
   @Override
   public void onSnackBarStarted(Object object){
      
      isSnackbarOpen = true;
      
      int   h = (int) object;
      hideActionButton(h);
      
   }
   
   @SuppressLint("RestrictedApi")
   private void hideActionButton(){
      
      long duration      = 500L;
      int  TRANSLATION_Y = -142;
      
      if(getCurrentPage() == MainConsts.PAGE_CONTACTS){
         
         if(!isFastScrollStart){
            
            ViewCompat.animate(actionButton).rotation(360).translationY(TRANSLATION_Y).setDuration(duration).start();
         }
         else{
            
            ViewCompat.animate(actionButton).rotation(360).translationY(TRANSLATION_Y).alpha(0).withEndAction(() -> actionButton.setVisibility(View.GONE)).setDuration(duration).start();
         }
      }
      else{
         
         ViewCompat.animate(actionButton).rotation(360).translationY(TRANSLATION_Y).alpha(0).withEndAction(() -> actionButton.setVisibility(View.GONE)).setDuration(duration).start();
      }
      
      
      isActionbarHiden = true;
   }
   
   private void hideActionButton(int y){
      
      int TRANSLATION_Y = -y;
      
      if(getCurrentPage() == MainConsts.PAGE_CONTACTS){
         
         if(!isFastScrollStart){
            
            ViewCompat.animate(actionButton).rotation(360).translationY(TRANSLATION_Y).setDuration(600).start();
         }
         else{
            
            ViewCompat.animate(actionButton).rotation(360).translationY(TRANSLATION_Y).alpha(0).withEndAction(() -> actionButton.hide()).setDuration(600).start();
         }
      }
      else{
         
         ViewCompat.animate(actionButton).rotation(360).translationY(TRANSLATION_Y).alpha(0).withEndAction(() -> actionButton.hide()).setDuration(600).start();
      }
      
      
      isActionbarHiden = true;
   }
   
   @Override
   public void onSnackBarFinished(Object object, boolean actionPressed){
      
      isSnackbarOpen = false;
      showActionButton();
   }
   
   @SuppressLint("RestrictedApi")
   public void showActionButton(){
      
      if(!isSnackbarOpen && !isFastScrollStart && getCurrentPage() == MainConsts.PAGE_CONTACTS){
         
         actionButton.show();
         
         ViewCompat.animate(actionButton).rotation(0).translationY(0).alpha(1).setDuration(600).start();
         isActionbarHiden = false;
      }
   }
   
   @Override
   public void onFastScrollStart(){
      
      isFastScrollStart = true;
      hideActionButton();
   }
   
   @Override
   public void onFastScrollStop(){
      
      isFastScrollStart = false;
      showActionButton();
   }
   
   @Override
   public void onPageScrolled(int i, float v, int i1){}
   
   @Override
   public void onPageSelected(int position){
      
      if(position == MainConsts.PAGE_CALL_LOG){ hideActionButton(); }
      if(position == MainConsts.PAGE_CONTACTS && !isSnackbarOpen){
         showActionButton();
      }
      
      if(pageChangeListeners == null){ return; }
      
      notifyPageListeners(position);
   }
   
   @Override
   public void onPageScrollStateChanged(int i){}
   
   private void notifyPageListeners(final int index){
      
      for(PageChangeListener pageChangeListener : pageChangeListeners){
         
         if(pageChangeListener != null){
            pageChangeListener.onPageChange(index);
         }
      }
   }
   
   @Override
   public int getCurrentPage(){
      
      return viewPager.getCurrentItem();
   }
}
