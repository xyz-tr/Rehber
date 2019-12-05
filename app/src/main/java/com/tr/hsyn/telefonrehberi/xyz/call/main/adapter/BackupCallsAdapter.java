package com.tr.hsyn.telefonrehberi.xyz.call.main.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnDeleteCallListener;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.List;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class BackupCallsAdapter extends RecyclerView.Adapter<BackupCallsAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
   
   private final List<ICall>    phoneCalls;
   private OnDeleteCallListener deleteCallListener;
   
   public BackupCallsAdapter(final List<ICall> phoneCalls, OnDeleteCallListener deleteCallListener){
   
      this.phoneCalls         = phoneCalls;
      this.deleteCallListener = deleteCallListener;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_details_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
   
      final ICall phoneCall = phoneCalls.get(position);
   
      holder.number.setText(CallStory.formatNumberForDisplay(phoneCall.getNumber()));
      holder.date.setText(u.formatDate(phoneCall.getDate()));
   
      String name   = phoneCall.getName();
      String letter = ContactAdapter.getLetter(name);
   
      if(name == null || isPhoneNumber(name)){
      
         name = phoneCall.getNumber();
      }
   
      holder.name.setText(name);
   
      int color = u.colorGenerator.getRandomColor();
   
      Drawable drawable = TextDrawable.builder().buildRound(letter.toLowerCase(), color);
      holder.image.setImageDrawable(drawable);
   
      holder.duration.setText(ShowCallsAdapter.unknownDuration);
      holder.durationRinging.setText(ShowCallsAdapter.unknownDuration);
      setRinging(phoneCall, holder);
   
      switch(phoneCall.getType()){
      
         case Type.INCOMMING:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.incomming_call));
            holder.duration.setText(u.formatSeconds(phoneCall.getDuration()));
            break;
         case Type.OUTGOING:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.outgoing_call));
            holder.duration.setText(u.formatSeconds(phoneCall.getDuration()));
            break;
         case Type.MISSED:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.missed_call));
            break;
         case Type.REJECTED:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.rejected_call));
            break;
         case Type.BLOCKED:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.blocked_call));
         
            holder.type.setScaleX(0.8F);
            holder.type.setScaleY(0.8F);
            break;
      
         case Type.UNREACHED:
         
            //todo tamamla
            break;
      
         default: break;
      }
   
   
      u.setTintDrawable(holder.delete.getDrawable(), color);
      
   }
   
   @Override
   public void onViewRecycled(@NonNull ViewHolder holder){
      
      super.onViewRecycled(holder);
      
      int index = holder.getAdapterPosition();
      
      if(index == -1) return;
      
      ICall phoneCall = phoneCalls.get(index);
      setRinging(phoneCall, holder);
   }
   
   private void setRinging(ICall phoneCall, ViewHolder holder){
      
      switch(phoneCalls.get(holder.getAdapterPosition()).getType()){
         
         case Type.UNREACHED:
         case Type.UNRECIEVED:
         case Type.BLOCKED:
            
            holder.ringingImage.setVisibility(View.GONE);
            holder.durationRinging.setText("");
            return;
         
         default: break;
      }
      
      
      if(phoneCall.getRingingDuration() != 0){
         
         holder.durationRinging.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
      }
   }
   
   @Override
   public int getItemCount(){
      
      return phoneCalls.size();
   }
   
   @NonNull
   @Override
   public String getSectionName(int position){
   
      return u.formatShortDate(phoneCalls.get(position).getDate());
   }
   
   final class ViewHolder extends RecyclerView.ViewHolder{
      
      final TextView  name;
      final TextView  number;
      final TextView  duration;
      final TextView  durationRinging;
      final TextView  date;
      final ImageView image;
      final ImageView ringingImage;
      final ImageView type;
      final ImageView delete;
      
      ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         name            = itemView.findViewById(R.id.name);
         number          = itemView.findViewById(R.id.number);
         duration        = itemView.findViewById(R.id.callDuration);
         durationRinging = itemView.findViewById(R.id.ringingDuration);
         date            = itemView.findViewById(R.id.date);
         image           = itemView.findViewById(R.id.image);
         ringingImage    = itemView.findViewById(R.id.ringingIcon);
         type            = itemView.findViewById(R.id.type);
         delete          = itemView.findViewById(R.id.delete);
         
         
         itemView.findViewById(R.id.call_item).setOnLongClickListener(this::onLongClick);
         delete.setOnClickListener((v) -> delete(getAdapterPosition()));
         itemView.findViewById(R.id.call_item).setBackgroundResource(MainActivity.getWellRipple());
         
      }
      
      
      private void delete(int position){
         
         deleteCallListener.onDeleteCall(position);
      }
      
      private boolean onLongClick(View view){
         
         return u.copyToClipboard(itemView.getContext(), phoneCalls.get(getAdapterPosition()).getNumber());
      }
   }
}
