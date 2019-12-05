package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;


import android.graphics.Color;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Message;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.service.RandomCallsService;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import java.util.List;


/**
 * <h1>CallLoadingFragment</h1>
 * <p>
 * Arama kayıtlarının alınması işlemini izleyen sınıf.
 */
public abstract class CallLogLoading extends CallLogLoader implements SwipeRefreshLayout.OnRefreshListener{
   
   @Override
   protected void onViewInflated(View view){
      
      final int distanceTrigger = 600;
      final int distanceSlide = 400;
      
      ColorController.setIndaterminateProgressColor(view.getContext(), progressBar);
      int color = u.getPrimaryColor(view.getContext());
      refreshLayout.setDistanceToTriggerSync(distanceTrigger);
      refreshLayout.setSlingshotDistance(distanceSlide);
      refreshLayout.setOnRefreshListener(this);
      refreshLayout.setColorSchemeColors(color);
      setHasOptionsMenu(true);
   }
   
   @Override
   protected void onCallsLoading(){
      
      progressBar.setVisibility(View.VISIBLE);
   }
   
   @Override
   protected void onCallsLoaded(List<ICall> calls, Throwable throwable){
      
      progressBar.setVisibility(View.GONE);
      refreshLayout.setRefreshing(false);
   }
   
   @Override
   public void onRefresh(){
      
      if(RandomCallsService.isRunning()){
         
         final long duration = 5000L;
         
         Message.builder()
                .message(new Spanner("Rastgele kayıt üretimi devam ettiği için yenileme ")
                               .append("reddedildi.", Spans.foreground(Color.parseColor("#E57373")), Spans.underline(), Spans.boldItalic()))
                .duration(duration)
                .type(Show.WARN)
                .build()
                .showOn(getActivity());
         
         
         refreshLayout.setRefreshing(false);
      }
      else{
         
         refreshLayout.setRefreshing(true);
         loadCalls();
      }
      
      
   }
   
   
}
