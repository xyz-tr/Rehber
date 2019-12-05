package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.contact.Group;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.CheckedChangeListener;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.List;


public class SelectLabelAdapter extends RecyclerView.Adapter<SelectLabelAdapter.ViewHolder>{
   
   private CheckedChangeListener listener;
   private List<Group>           groups;
   
   public SelectLabelAdapter(List<Group> groups){
      
      this.groups = groups;
   }
   
   public void setListener(CheckedChangeListener listener){
      
      this.listener = listener;
   }
   
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
   
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_label_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      holder.label.setText(groups.get(position).getTitle());
   }
   
   @Override
   public int getItemCount(){
      
      return groups.size();
   }
   
   class ViewHolder extends RecyclerView.ViewHolder{
      
      TextView label;
      CheckBox checkBox;
      
      public ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         label = itemView.findViewById(R.id.labelIcon);
         checkBox = itemView.findViewById(R.id.checkBox);
         
         
         itemView.setBackgroundResource(MainActivity.getWellRipple());
         
         itemView.setOnClickListener(v -> {
            
            if(listener != null){ listener.checkedChange(v, checkBox.isChecked(), getAdapterPosition()); }
         });
      }
      
   }
}
