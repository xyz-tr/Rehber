package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import androidx.fragment.app.Fragment;

import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.FastScrollListener;

import java.util.List;


public abstract class ContactsScrollListener extends Fragment implements OnFastScrollStateChangeListener{
   
   protected abstract List<FastScrollListener> getFastScrollListeners();
   
   public void addFastScrollListener(final FastScrollListener fastScrollListener){
      
      getFastScrollListeners().add(fastScrollListener);
   }
   
   @Override
   public void onFastScrollStart(){
   
      notifyFastScrollListeners(true);
   }
   
   @Override
   public void onFastScrollStop(){
   
      notifyFastScrollListeners(false);
   }
   
   private void notifyFastScrollListeners(final boolean start){
   
      for(FastScrollListener fastScrollListener : getFastScrollListeners()){
      
         if(fastScrollListener != null){
         
            if(start){
            
               fastScrollListener.onFastScrollStart();
            }
            else{
            
               fastScrollListener.onFastScrollStop();
            }
         }
      }
   }
}
