package com.tr.hsyn.telefonrehberi.xyz.call.main.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most.Model;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;


public class MostCallsAdapter extends RecyclerView.Adapter<MostCallsAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
   
   private       List<Model>        callModels;
   private final int                type;
   private       ItemSelectListener clickListener;
   
   public MostCallsAdapter setClickListener(ItemSelectListener listener){
      
      clickListener = listener;
      return this;
   }
   
   public MostCallsAdapter(List<Model> callModels, int type){
      
      this.callModels = callModels;
      this.type       = type;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_item2, parent, false));
   }
   
   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      Model call = callModels.get(position);
      
      holder.name.setText(call.getName());
      
      switch(type){
         
         case Filter.MOST_OUTGOING_CALLS:
   
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.outgoing_call));
            holder.count.setText(call.getCount() + " " + holder.itemView.getContext().getString(R.string.outgoing_call));
            break;
            
         case Filter.MOST_INCOMMING_CALLS:
   
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.incomming_call));
            holder.count.setText(call.getCount() + " " + holder.itemView.getContext().getString(R.string.incomming_call));
            break;
         
         case Filter.MOST_MISSED_CALLS:
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.missed_call));
            holder.count.setText(call.getCount() + " " + holder.itemView.getContext().getString(R.string.missed_call));
            break;
         
         case Filter.MOST_REJECTED_CALLS:
            
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rejected_call));
            holder.count.setText(call.getCount() + " " + holder.itemView.getContext().getString(R.string.rejected_call));
            break;
         
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL:
            
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.most_duration_incomming_call));
            holder.count.setText(u.formatSeconds((int) call.getPair().getTotalDuration()));
            break;
         
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL:
            
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.most_duration_outgoing_call));
            holder.count.setText(u.formatSeconds((int) call.getPair().getTotalDuration()));
            break;
   
         case Filter.LONGEST_ANSWERED_CALLS:
         case Filter.QUICKEST_ANSWERED_CALLS:
         case Filter.QUICKEST_MISSED_CALLS:
         case Filter.LONGEST_MISSED_CALLS:
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
         case Filter.QUICKEST_REJECTED_CALLS:
         case Filter.LONGEST_REJECTED_CALLS:
            
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.duration));
            holder.count.setText(u.formatMilliseconds(call.getPair().getTotalDuration()));
            
            break;
            
      }
      
      Drawable drawable = TextDrawable
            .builder()
            .beginConfig()
            .useFont(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.z))
            .bold()
            .endConfig()
            .buildRound("" + call.getPair().getRank(), u.colorGenerator.getRandomColor());
      holder.order.setImageDrawable(drawable);
      
   }
   
   @Override
   public int getItemCount(){
      
      return callModels.size();
   }
   
   @NonNull
   @Override
   public String getSectionName(int position){
      
      return String.valueOf(callModels.get(position).getPair().getRank());
   }
   
   final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      ImageView order;
      ImageView type;
      TextView  name;
      TextView  count;
      
      
      ViewHolder(View itemView){
         
         super(itemView);
         
         order = itemView.findViewById(R.id.image);
         type  = itemView.findViewById(R.id.type);
         name  = itemView.findViewById(R.id.name);
         count = itemView.findViewById(R.id.count);
         
         itemView.findViewById(R.id.mainLayout).setOnClickListener(this);
         itemView.findViewById(R.id.mainLayout).setBackgroundResource(MainActivity.getWellRipple());
      }
      
      @Override
      public void onClick(View v){
   
         if(clickListener != null){ clickListener.onItemSelected(getAdapterPosition()); }
      }
      
      
   }
   
   
}
