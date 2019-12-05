package com.tr.hsyn.telefonrehberi.xyz.ptt.activity.relation;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class RelationsActivityAdapter extends RecyclerView.Adapter<RelationsActivityAdapter.ViewHolder>{
   
   
   
   
   @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return null;
   }
   
   @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
   }
   
   @Override public int getItemCount(){
      
      return 0;
   }
   
   final class ViewHolder extends RecyclerView.ViewHolder{
   
      TextView email;
      TextView name;
      TextView number;
   
      public ViewHolder(@NonNull View itemView){
      
         super(itemView);
         
         
         
      }
   }
}
