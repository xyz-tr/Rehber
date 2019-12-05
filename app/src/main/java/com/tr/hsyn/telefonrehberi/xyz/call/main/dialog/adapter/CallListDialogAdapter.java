package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.List;


public class CallListDialogAdapter extends RecyclerView.Adapter<CallListDialogAdapter.CallViewHolder>{
   
   private              int         index;
   private final        List<ICall> phoneCalls;
   private final        ICall       phoneCall;
   private final        int[]       orders;
   private static final String      unknownDuration = "--:--";
   
   public CallListDialogAdapter(@NonNull final List<ICall> phoneCalls){
      
      this(phoneCalls, null);
   }
   
   public CallListDialogAdapter(@NonNull final List<ICall> phoneCalls, @Nullable final ICall phoneCall){
      
      this(phoneCalls, phoneCall, null);
   }
   
   public CallListDialogAdapter(@NonNull final List<ICall> phoneCalls, @Nullable final ICall phoneCall, final int[] orders){
      
      this.phoneCall  = phoneCall;
      this.phoneCalls = phoneCalls;
      this.orders     = orders;
   }
   
   @NonNull
   @Override
   public CallViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
      
      return new CallViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dialog_call_list_item, viewGroup, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull CallViewHolder holder, int position){
      
      final ICall currentPhoneCall = phoneCalls.get(position);
      
      holder.number.setText(CallStory.formatNumberForDisplay(currentPhoneCall.getNumber()));
      holder.date.setText(u.formatDate(currentPhoneCall.getDate()));
      
      String name   = currentPhoneCall.getName();
      String letter = "?";
      
      if(name == null){
         
         name = currentPhoneCall.getNumber();
      }
      else{
         
         letter = name.substring(0, 1);
      }
      
      holder.name.setText(name);
      
      final int color = u.colorGenerator.getRandomColor();
      
      if(orders != null){
         
         letter = String.valueOf(orders[position]);
      }
      
      Drawable drawable = TextDrawable.builder().buildRound(letter.toUpperCase(), color);
      holder.image.setImageDrawable(drawable);
      
      holder.duration.setText(unknownDuration);
      holder.durationRinging.setText(unknownDuration);
      setRinging(phoneCall, holder);
      
      switch(currentPhoneCall.getType()){
         
         case Type.INCOMMING:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.incomming_call));
            holder.duration.setText(u.formatSeconds(currentPhoneCall.getDuration()));
            break;
         case Type.OUTGOING:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.outgoing_call));
            holder.duration.setText(u.formatSeconds(currentPhoneCall.getDuration()));
            break;
         case Type.MISSED:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.missed_call));
            //holder.duration.setText(u.formatMilliseconds(currentPhoneCall.getRingingDuration()));
            break;
         case Type.REJECTED:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.rejected_call));
            //holder.duration.setText(u.formatMilliseconds(currentPhoneCall.getRingingDuration()));
            break;
         case Type.BLOCKED:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.blocked_call));
            //holder.duration.setText(u.formatMilliseconds(currentPhoneCall.getRingingDuration()));
            holder.type.setScaleX(0.8F);
            holder.type.setScaleY(0.8F);
            holder.duration.setText(u.formatSeconds(currentPhoneCall.getDuration()));
            break;
         
         
         case Type.UNREACHED:
            
            //todo tamamla
            break;
         
         default: break;
      }
      
      if(this.phoneCall == null){
         
         return;
      }
      
      if(this.phoneCall.equals(currentPhoneCall)){
         
         index = position;
         holder.itemView.setBackgroundColor(u.getColor(holder.itemView.getContext(), R.color.dialog_call_list_selected_item_background));
      }
      else{
         
         holder.itemView.setBackgroundResource(MainActivity.getWellRipple());
      }
   }
   
   private void setRinging(ICall phoneCall, CallViewHolder holder){
      
      ICall call = phoneCalls.get(holder.getAdapterPosition());
      
      switch(phoneCalls.get(holder.getAdapterPosition()).getType()){
         
         case Type.UNREACHED:
         case Type.UNRECIEVED:
         case Type.BLOCKED:
            
            holder.durationRingingIcon.setVisibility(View.GONE);
            holder.durationRinging.setText("");
            return;
         
         default: break;
      }
      
      
      if(call.getRingingDuration() != 0){
         
         holder.durationRinging.setText(u.formatMilliseconds(call.getRingingDuration()));
      }
   }
   
   @Override
   public int getItemCount(){
      
      return phoneCalls.size();
   }
   
   @Override
   public void onViewRecycled(@NonNull CallViewHolder holder){
      
      super.onViewRecycled(holder);
      
      setRinging(phoneCall, holder);
      
      if(phoneCall == null){
         return;
      }
      
      if(holder.getAdapterPosition() == index){
         
         holder.itemView.setBackgroundColor(u.getColor(holder.itemView.getContext(), R.color.dialog_call_list_selected_item_background));
      }
      else{
         
         holder.itemView.setBackgroundResource(MainActivity.getWellRipple());
      }
   }
   
   static final class CallViewHolder extends RecyclerView.ViewHolder{
      
      final TextView  name;
      final TextView  number;
      final TextView  duration;
      final TextView  date;
      final ImageView image;
      final ImageView durationIcon;
      final ImageView type;
      final TextView  durationRinging;
      final ImageView durationRingingIcon;
      
      CallViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         
         name                = itemView.findViewById(R.id.name);
         number              = itemView.findViewById(R.id.number);
         duration            = itemView.findViewById(R.id.callDuration);
         date                = itemView.findViewById(R.id.date);
         image               = itemView.findViewById(R.id.image);
         type                = itemView.findViewById(R.id.type);
         durationIcon        = itemView.findViewById(R.id.durationIcon);
         durationRinging     = itemView.findViewById(R.id.ringingDuration);
         durationRingingIcon = itemView.findViewById(R.id.ringingIcon);
         
         itemView.setBackgroundResource(MainActivity.getWellRipple());
      }
   }
}
