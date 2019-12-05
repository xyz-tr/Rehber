package com.tr.hsyn.telefonrehberi.xyz.call.main.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.CallLogFiltered;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnFilterItemClickListener;


public class CallFilterAdapter extends RecyclerView.Adapter<CallFilterAdapter.ViewHolder>{
   
   
   private String[]                  filters;
   private OnFilterItemClickListener listener;
   private int                       selected;
   
   public CallFilterAdapter(String[] filters, int selected){
      
      this.filters  = filters;
      this.selected = selected;
   }
   
   public CallFilterAdapter setClickListener(OnFilterItemClickListener listener){
      
      this.listener = listener;
      return this;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_filter_dialog_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      holder.filter.setText(filters[position]);
      
      if(position == selected){
         
         holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.call_filter_item_selected_color));
      }
      
      if(position > 3) position++;
      
      switch(position){
         
         case CallLogFiltered.ALL_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.call_history_24_hours, null));
            break;
         
         case Filter.INCOMMING:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.incomming_call, null));
            break;
         
         case Filter.OUTGOING:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.outgoing_call, null));
            break;
         
         case Filter.MISSED:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.missed_call, null));
            break;
         
         case Filter.REJECTED:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.rejected_call, null));
            break;
   
         case Filter.BLOCKED:
      
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.blocked_call, null));
            break;
         
         case Filter.DELETED:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.rubbish_bin, null));
            break;
         
         case Filter.MOST_INCOMMING_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_incomming, null));
            break;
         
         case Filter.MOST_OUTGOING_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_outgoing, null));
            break;
         
         case Filter.MOST_MISSED_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_missed, null));
            break;
         
         case Filter.MOST_REJECTED_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_rejected, null));
            break;
         
         case Filter.MOST_SPEAKING:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_duration_incomming_call, null));
            break;
         
         case Filter.MOST_TALKING:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_duration_outgoing_call, null));
            break;
         
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_duration_incomming_total, null));
            break;
         
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.most_duration_outgoing_total, null));
            break;
         
         case Filter.LONGEST_MISSED_CALLS:
         case Filter.QUICKEST_MISSED_CALLS:
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.longest_missed_call, null));
            break;
         
         case Filter.QUICKEST_ANSWERED_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.quickest_answered_calls, null));
            break;
         
         case Filter.LONGEST_ANSWERED_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.longest_answered_calls, null));
            break;
         
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.quickest_make_answered_calls, null));
            break;
         
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
            
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.longest_make_answered_calls, null));
            break;
   
         case Filter.QUICKEST_REJECTED_CALLS:
      
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.quick_rejected_calls, null));
            break;
   
         case Filter.LONGEST_REJECTED_CALLS:
      
            holder.callTypeIcon.setImageDrawable(ResourcesCompat.getDrawable(holder.itemView.getResources(), R.drawable.longest_rejected_call, null));
            break;
         
         default: break;
      }
      
   }
   
   @Override
   public void onViewRecycled(@NonNull ViewHolder holder){
      
      super.onViewRecycled(holder);
      holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ripple));
      
   }
   
   @Override
   public int getItemCount(){
      
      return filters.length;
   }
   
   class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      TextView  filter;
      ImageView callTypeIcon;
      
      ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         filter       = itemView.findViewById(R.id.filterItemText);
         callTypeIcon = itemView.findViewById(R.id.type);
         
         itemView.setOnClickListener(this);
      }
      
      @Override
      public void onClick(View v){
   
         if(listener != null){ listener.onFilterItemClicked(v, getAdapterPosition()); }
      }
   }
}
