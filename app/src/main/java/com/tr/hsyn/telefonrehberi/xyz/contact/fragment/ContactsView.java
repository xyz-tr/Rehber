package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.FastScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.AccessLevel;
import lombok.Getter;


public abstract class ContactsView extends ContactsScrollListener{
   
   //region fields
   /**
    * Rehber için gerekli izinler
    */
   @Getter final String[] permissions = {
         
         Manifest.permission.GET_ACCOUNTS,
         Manifest.permission.READ_CONTACTS,
         Manifest.permission.WRITE_CONTACTS
   };
   
   /**
    * Kişilerin gösterileceği liste
    */
   @BindView(R.id.recyclerView)
   @Getter(AccessLevel.PROTECTED) FastScrollRecyclerView recyclerView;
   
   /**
    * Kişiler ilk yüklenirken veya yenileme yapıldığında gösterilecek progress bar
    */
   @BindView(R.id.progressBar)
   @Getter(AccessLevel.PROTECTED) ProgressBar progressBar;
   
   /**
    * Rehberde kişi yoksa gösterilecek görsel eleman
    */
   @BindView(R.id.empty)
   @Getter(AccessLevel.PROTECTED) View emptyView;
   
   /**
    * Yukarıdan aşağı kaydırma hareketiyle yenileme yapmayı sağlayan eleman
    */
   @BindView(R.id.refresh)
   @Getter(AccessLevel.PROTECTED) SwipeRefreshLayout refreshLayout;
   
   /**
    * Listede fast scroll olayını dinlemek isteyenler
    */
   @Getter(AccessLevel.PROTECTED) private final List<FastScrollListener> fastScrollListeners = new ArrayList<>();
   
   //endregion
   
   @Override
   public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
      
      View view = inflater.inflate(R.layout.fragment_contact_list, container, false);
      
      ButterKnife.bind(this, view);
      
      ColorController.setIndaterminateProgressColor(container.getContext(), progressBar);
      
      refreshLayout.setDistanceToTriggerSync(600);
      refreshLayout.setSlingshotDistance(400);
      
      setRecyclerColor();
      setHasOptionsMenu(true);
      recyclerView.setOnFastScrollStateChangeListener(this);
      onViewInflated(view);
      
      return view;
   }
   
   /**
    * RecyclerView için renk ayarı
    */
   private void setRecyclerColor(){
      
      int color = ColorController.getPrimaryColor().getValue();
      recyclerView.setThumbColor(color);
      recyclerView.setPopupBgColor(color);
   }
   
   /**
    * Fragment'ın {@code onCreateView} metotodu işlemini tamamladığında çağrılıyor.
    *
    * @param view Fragment'ın inflate ettiği view
    */
   protected abstract void onViewInflated(View view);
}
