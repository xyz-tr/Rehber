package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Group;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most.Model;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.adapter.MostCallListDialogAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;

import java.util.ArrayList;
import java.util.List;


public class MostCallListDialog{
   
   private ProgressBar         progressBar;
   private List<Group<String>> mostCalls;
   private int                 type;
   private RecyclerView        recyclerView;
   private ICall               phoneCall;
   private AlertDialog         alertDialog;
   private View                root;
   
   @SuppressLint("InflateParams")
   public MostCallListDialog(Activity activity,
                             int type,
                             CharSequence title,
                             ICall phoneCall,
                             @NonNull List<Group<String>> mostCalls){
      
      this.type      = type;
      this.phoneCall = phoneCall;
      this.mostCalls = mostCalls;
      
      AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
      
      LayoutInflater layoutInflater = activity.getLayoutInflater();
      
      root = layoutInflater.inflate(R.layout.dialog_list_most_call, null, false);
      
      dialog.setView(root);
      
      TextView titleText = root.findViewById(R.id.titleText);
      recyclerView = root.findViewById(R.id.recyclerView);
      titleText.setText(title);
      
      recyclerView.setLayoutManager(new LinearLayoutManager(activity));
      
      dialog.setCancelable(true);
      View header = root.findViewById(R.id.header);
      header.setBackgroundColor(u.getPrimaryColor(activity));
      
      progressBar = root.findViewById(R.id.progress);
      
      alertDialog = dialog.create();
      //noinspection ConstantConditions
      alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      Worker.onBackground(
            this::workOnBackground,
            this::onWorkResult
      );
   }
   
   private List<Model> workOnBackground(){
      
      boolean isDuration = false;
      
      switch(type){
         
         case Filter.QUICKEST_ANSWERED_CALLS:
         case Filter.LONGEST_ANSWERED_CALLS:
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
         case Filter.QUICKEST_REJECTED_CALLS:
            isDuration = true;
            break;
      }
      
      
      return getModels(mostCalls, isDuration);
   }
   
   private static List<Model> getModels(List<Group<String>> mostCalls, boolean isDuration){
      
      List<Model> callModels = new ArrayList<>();
      
      for(int i = 0; i < mostCalls.size(); i++){
         
         Group<String> pair = mostCalls.get(i);
         
         String name = pair.getCalls().get(0).getName();
         
         if(name == null){ name = pair.getGroupItem(); }
         
         Model model = new Model(name, isDuration ? pair.getTotalDuration() : pair.getCalls().size(), pair);
         
         callModels.add(model);
      }
      
      return callModels;
   }
   
   private void onWorkResult(List<Model> result){
      
      recyclerView.setAdapter(new MostCallListDialogAdapter(result, type, phoneCall));
      progressBar.setVisibility(View.GONE);
   }
   
   public void reset(){
      
      progressBar  = null;
      recyclerView = null;
      alertDialog  = null;
      root         = null;
      
   }
   
   public void show(){
      
      alertDialog.show();
   }
   
   public static int getType(int type){
      
      switch(type){
         
         case Type.INCOMMING: return Filter.MOST_INCOMMING_CALLS;
         case Type.OUTGOING: return Filter.MOST_OUTGOING_CALLS;
         case Type.MISSED: return Filter.MOST_MISSED_CALLS;
         case Type.REJECTED: return Filter.MOST_REJECTED_CALLS;
         case Type.BLOCKED: return Filter.MOST_BLOCKED_CALLS;
         
         default: return -1;
      }
   }
   
   public void setCloseListener(DialogListener closeListener){
      
      alertDialog.setOnDismissListener(closeListener.getDialogListener());
      alertDialog.setOnCancelListener(closeListener.getDialogListener());
      alertDialog.setOnShowListener(closeListener.getDialogListener());
   }
   
}
