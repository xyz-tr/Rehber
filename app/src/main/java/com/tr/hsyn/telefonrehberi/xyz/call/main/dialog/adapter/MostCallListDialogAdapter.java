package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.adapter;

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
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most.Model;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;

import java.util.List;


public class MostCallListDialogAdapter extends RecyclerView.Adapter<MostCallListDialogAdapter.ViewHolder>{
   
   private List<Model> callModels;
   private int         type;
   private ICall       phoneCall;
   private int         index;
   
   public MostCallListDialogAdapter(List<Model> callModels, int type, ICall phoneCall){
   
      this.callModels = callModels;
      this.type       = type;
      this.phoneCall  = phoneCall;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
      
      return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_most_call_item, viewGroup, false));
   }
   
   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position){
      
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
   
         case Filter.MOST_BLOCKED_CALLS:
      
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.blocked_call));
            holder.count.setText(call.getCount() + " " + holder.itemView.getContext().getString(R.string.blocked_call));
            break;
         
         case Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL:
            
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.most_duration_incomming_call));
            holder.count.setText(u.formatMilliseconds(call.getCount()));
            break;
         
         case Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL:
            
            holder.type.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.most_duration_outgoing_call));
            holder.count.setText(u.formatMilliseconds(call.getCount()));
            break;
         
         
         case Filter.QUICKEST_MISSED_CALLS:
         case Filter.LONGEST_MISSED_CALLS:
         case Filter.QUICKEST_ANSWERED_CALLS:
         case Filter.LONGEST_ANSWERED_CALLS:
         case Filter.LONGEST_MAKE_ANSWERED_CALLS:
         case Filter.QUICKEST_MAKE_ANSWERED_CALLS:
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
   
      if(this.phoneCall == null){ return; }
      
      if(Contacts.matchNumbers(call.getPair().getGroupItem(), this.phoneCall.getNumber())){
   
         index = position;
         holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dialog_call_list_selected_item_background));
      }
      else{
         
         holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_background));
      }
   }
   
   @Override
   public void onViewRecycled(@NonNull ViewHolder holder){
      
      super.onViewRecycled(holder);
   
      if(phoneCall == null){ return; }
      
      if(holder.getAdapterPosition() == index){
         
         holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.dialog_call_list_selected_item_background));
      }
      else{
         
         holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.light_background));
      }
   }
   
   @Override
   public int getItemCount(){
      
      return callModels.size();
   }
   
   final class ViewHolder extends RecyclerView.ViewHolder{
      
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
         
      }
      
   }
   
}
