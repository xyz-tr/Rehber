package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.ArrayList;
import java.util.List;


public class GroupOptionsAdapter extends RecyclerView.Adapter<GroupOptionsAdapter.ViewHolder> {
   
   private List<String>       options;
   private List<Integer>      disabledItems;
   private ItemSelectListener listener;
   
   public GroupOptionsAdapter setClickListener(ItemSelectListener listener){
      
      this.listener = listener;
      return this;
   }
   
  
   
   public GroupOptionsAdapter(List<String> options){
      
      this(options, new ArrayList<>());
   }
   
   public GroupOptionsAdapter(List<String> options, List<Integer> disabledItems){
      
      this.options       = options;
      this.disabledItems = disabledItems;
   }
   
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
   
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_options_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      holder.option.setText(options.get(position));
      holder.option.setEnabled(!disabledItems.contains(position));
      
   }
   
   @Override
   public void onViewRecycled(@NonNull ViewHolder holder){
      
      super.onViewRecycled(holder);
   
      holder.option.setEnabled(disabledItems.contains(holder.getAdapterPosition()));
      
   }
   
   @Override
   public int getItemCount(){
      
      return options.size();
   }
   
   class ViewHolder extends RecyclerView.ViewHolder{
   
      TextView option;
   
      public ViewHolder(@NonNull View itemView){
      
         super(itemView);
   
         option = itemView.findViewById(R.id.option);
   
         View main = itemView.findViewById(R.id.optionMain);
         main.setBackgroundResource(MainActivity.getWellRipple());
         
         main.setOnClickListener(v -> {
   
            if(listener != null){ listener.onItemSelected(getAdapterPosition()); }
         });
      }
   
   }
   
   
}
