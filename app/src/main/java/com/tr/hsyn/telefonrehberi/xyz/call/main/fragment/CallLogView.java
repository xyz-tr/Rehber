package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.swipe.SwipeListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import butterknife.BindView;
import butterknife.ButterKnife;


public abstract class CallLogView extends Fragment implements SwipeListener{
   
   @BindView(R.id.progressBar)  ProgressBar            progressBar;
   @BindView(R.id.refresh)      SwipeRefreshLayout     refreshLayout;
   @BindView(R.id.recyclerView) FastScrollRecyclerView recyclerView;
   @BindView(R.id.empty)        View                   emptyView;
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
      
      View view = inflater.inflate(R.layout.fragment_call_list, container, false);
      
      ButterKnife.bind(this, view);
      
      int color = ColorController.getPrimaryColor().getValue();
      
      recyclerView.allowThumbInactiveColor(false);
      recyclerView.setPopupBgColor(color);
      recyclerView.setThumbColor(color);
      
      onViewInflated(view);
      return view;
   }
   
   abstract protected void onViewInflated(View view);
   
   
}
