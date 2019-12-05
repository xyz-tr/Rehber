package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.observable.Observe;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.CallAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.HaveTitle;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.TitleController;

import lombok.Getter;


public abstract class CallLogTitle extends CallLogShowMe implements HaveTitle{
   
   @Getter private final Observe<String> title    = new Observe<>("Arama Kayıtları");
   @Getter private final Observe<String> subTitle = new Observe<>("");
   
   /**
    * Tüm Aramalar kodu
    */
   public static final int ALL_CALLS = 0;
   
   @Override
   protected void setTitle(){
      
      if(isShowTime()){
         
         title.setValue(getCurrentFilter() == ALL_CALLS ? getString(R.string.call_log) : getCallsTitle(getCurrentFilter()));
         
         if(getAdapter() != null)
            subTitle.setValue(String.valueOf(getAdapter().getItemCount()));
         
      }
   }
   
   protected abstract CallAdapter getAdapter();
   
   protected abstract String getCallsTitle(int filter);
   
   protected abstract int getCurrentFilter();
   
   public void setTitleController(TitleController titleController){
      
      if(titleController != null){
         
         title.setChangeOnSameValue(true);
         subTitle.setChangeOnSameValue(true);
         title.setObserver(t -> setTitle(titleController, t));
         subTitle.setObserver(st -> setSubTitle(titleController, st));
      }
   }
   
   
}
