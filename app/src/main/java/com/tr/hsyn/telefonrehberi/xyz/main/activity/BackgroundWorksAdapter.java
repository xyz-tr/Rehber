package com.tr.hsyn.telefonrehberi.xyz.main.activity;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.WorkStat;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.val;


public class BackgroundWorksAdapter extends RecyclerView.Adapter<BackgroundWorksAdapter.ViewHolder>{
   
   @Getter @Setter private ItemSelectListener listener;
   private final           List<WorkStat>     works;
   
   public BackgroundWorksAdapter(List<WorkStat> works, ItemSelectListener listener){
      
      this.works    = works;
      this.listener = listener;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_background_works_work_item, parent, false), listener);
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      val item   = works.get(position);
      val reason = item.getReason();
      int index  = reason.indexOf(':');
      
      String className, task;
      
      if(index != -1){
         
         className = reason.substring(0, index);
         task      = reason.substring(index + 1);
      }
      else{
         
         className = "Bulunamadı";
         task      = reason;
      }
      
      holder.source.setText(className);
      holder.reason.setText(task);
      
      setStatus(holder, item);
   }
   
   private void setStatus(@NonNull ViewHolder holder, WorkStat workStat){
      
      boolean done  = workStat.isDone();
      int     color = done && workStat.getError() == null ? Color.GREEN : done ? Color.RED : Color.BLUE;
      
      if(workStat.getCompletableFuture() != null){
         
         if(done && workStat.getCompletableFuture().isCancelled()){
            
            color = Color.GRAY;
         }
      }
      
      u.setTintDrawable(holder.status.getBackground(), color);
   }
   
   @Override
   public int getItemCount(){
      
      return works.size();
   }
   
   @Override
   public void onViewRecycled(@NonNull ViewHolder holder){
      
      super.onViewRecycled(holder);
      
      val item = works.get(holder.getAdapterPosition());
      
      setStatus(holder, item);
   }
   
   static class ViewHolder extends RecyclerView.ViewHolder{
      
      TextView source;
      TextView reason;
      View     status;
      
      ViewHolder(@NonNull View itemView, ItemSelectListener listener){
         
         super(itemView);
         
         source = itemView.findViewById(R.id.source);
         reason = itemView.findViewById(R.id.reason);
         status = itemView.findViewById(R.id.status);
         
         itemView.findViewById(R.id.item).setOnClickListener(v -> onClick(v, listener));
      }
      
      private void onClick(@SuppressWarnings("unused") View v, ItemSelectListener listener){
         
         if(listener != null) listener.onItemSelected(getAdapterPosition());
      }
   }
}
