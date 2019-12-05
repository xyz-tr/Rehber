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
import com.tr.hsyn.telefonrehberi.util.Adapterx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnDeleteCallListener;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class ShowMostCallAdapter extends RecyclerView.Adapter<ShowMostCallAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
   
   private static final String               unknownDuration = "--:--";
   private final        List<ICall>          phoneCalls;
   private              OnDeleteCallListener deleteCallListener;
   private              ItemSelectListener   clickListener;
   
   public ShowMostCallAdapter(List<ICall> phoneCalls, OnDeleteCallListener deleteCallListener, ItemSelectListener listener){
   
      this.phoneCalls         = phoneCalls;
      this.deleteCallListener = deleteCallListener;
      clickListener           = listener;
   }
   
   public List<ICall> getPhoneCalls(){
      
      return phoneCalls;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
      
      return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.call_details_item, viewGroup, false));
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
   
      holder.duration.setText(unknownDuration);
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
            
            break;//todo tamamla
         
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
   
   private void setRinging(ICall phoneCall,  ViewHolder holder){
      
      if(phoneCall.getRingingDuration() != 0){
         
         holder.durationRinging.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
      }
      else{
   
         holder.durationRinging.setText(unknownDuration);
      }
   }
   
   @Override
   public int getItemCount(){
      
      return phoneCalls.size();
   }
   
   public boolean remove(ICall phoneCall){
      
      return Adapterx.remove(this, phoneCalls, phoneCall);
   }
   
   @NonNull
   @Override
   public String getSectionName(int position){
   
      return u.formatShortDate(phoneCalls.get(position).getDate());
   }
   
   final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      final TextView  name;
      final TextView  number;
      final TextView  duration;
      final TextView  date;
      final ImageView image;
      final ImageView type;
      final ImageView delete;
      final ImageView durationRingingIcon;
      final TextView  durationRinging;
      
      
      ViewHolder( View itemView){
         
         super(itemView);
         
         name                = itemView.findViewById(R.id.name);
         number              = itemView.findViewById(R.id.number);
         duration            = itemView.findViewById(R.id.callDuration);
         date                = itemView.findViewById(R.id.date);
         image               = itemView.findViewById(R.id.image);
         type                = itemView.findViewById(R.id.type);
         delete              = itemView.findViewById(R.id.delete);
         durationRingingIcon = itemView.findViewById(R.id.ringingIcon);
         durationRinging     = itemView.findViewById(R.id.ringingDuration);
         
         itemView.findViewById(R.id.call_item).setOnLongClickListener(this::onLongClick);
         itemView.findViewById(R.id.call_item).setOnClickListener(this);
         delete.setOnClickListener((v) -> delete(getAdapterPosition()));
         itemView.findViewById(R.id.call_item).setBackgroundResource(MainActivity.getWellRipple());
      }
      
      private void delete(int position){
         
         deleteCallListener.onDeleteCall(position);
      }
      
      private boolean onLongClick(View view){
         
         return u.copyToClipboard(itemView.getContext(), phoneCalls.get(getAdapterPosition()).getNumber());
      }
      
      @Override
      public void onClick(View v){
         
         clickListener.onItemSelected(getAdapterPosition());
      }
   }
   
   
}
