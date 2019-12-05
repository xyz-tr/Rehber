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
import com.tr.hsyn.telefonrehberi.util.event.CallNumberRequest;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class CallAdapter extends RecyclerView.Adapter<CallAdapter.ViewCallHolder> implements FastScrollRecyclerView.SectionedAdapter{
   
   static final  String             UNKNOWN_DURATION = "--:--";
   private       ItemSelectListener itemSelectListener;
   private       List<ICall>         phoneCalls;
   private LayoutInflater           inflater;
   
   public CallAdapter(final List<ICall> phoneCalls, ItemSelectListener itemSelectListener){
      
      this.phoneCalls         = phoneCalls;
      this.itemSelectListener = itemSelectListener;
   }
   
   @NonNull
   @Override
   public ViewCallHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType){
   
      if(inflater == null){
      
         inflater = LayoutInflater.from(parent.getContext());
      }
      
      return new ViewCallHolder(inflater.inflate(R.layout.call_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(ViewCallHolder holder, int position){
      
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
      
      Drawable drawable = TextDrawable.builder().buildRound(letter.toUpperCase(), color);
      holder.image.setImageDrawable(drawable);
      
      
      holder.duration.setText(UNKNOWN_DURATION);
      setRinging(phoneCall, holder);
      
      switch(phoneCall.getType()){
         
         case Type.INCOMMING:
         case Type.INCOMMING_WIFI:
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.incomming_call));
            holder.duration.setText(u.formatSeconds(phoneCall.getDuration()));
            break;
         case Type.OUTGOING:
         case Type.OUTGOING_WIFI:
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
         case Type.GETREJECTED:
            
            //senin reddedildiğin arama
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.get_rejected_call));
            holder.type.setScaleX(0.8F);
            holder.type.setScaleY(0.8F);
            break;
         
         case Type.UNREACHED:
            
            //senin ulaşamadığın arama
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.un_reached_call));
            holder.type.setScaleX(0.8F);
            holder.type.setScaleY(0.8F);
            break;
         case Type.UNRECIEVED:
            
            //sana ulaşmayan arama
            holder.type.setImageDrawable(holder.itemView.getContext().getDrawable(R.drawable.un_recieved_call));
            holder.type.setScaleX(0.8F);
            holder.type.setScaleY(0.8F);
            break;
         
         default: break;
      }
      
      u.setTintDrawable(holder.makeCall.getDrawable(), color);
   }
   
   private void setRinging(ICall phoneCall, CallAdapter.ViewCallHolder holder){
      
      if(phoneCall.getRingingDuration() != 0){
         
         holder.durationRinging.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
      }
      else{
         
         holder.durationRinging.setText(UNKNOWN_DURATION);
      }
   }
   
   @Override
   public int getItemCount(){
      
      return phoneCalls.size();
   }
   
   @Override
   public void onViewRecycled(@NonNull ViewCallHolder holder){
      
      super.onViewRecycled(holder);
      
      int index = holder.getAdapterPosition();
      
      if(index == -1) return;
      
      ICall phoneCall = phoneCalls.get(index);
      
      setRinging(phoneCall, holder);
      
      holder.itemView.clearAnimation();
   }
   
   @NonNull
   @Override
   public String getSectionName(int position){
      
      return u.formatShortDate(phoneCalls.get(position).getDate());
   }
   
   public boolean remove(ICall phoneCall){
      
      return Adapterx.remove(this, phoneCalls, phoneCall);
   }
   
   public List<ICall> getCalls(){
      
      return phoneCalls;
   }
   
   public void setCalls(List<ICall> calls){
      
      phoneCalls = calls;
      
      notifyDataSetChanged();
   }
   
   final class ViewCallHolder extends RecyclerView.ViewHolder{
      
      TextView  name;
      TextView  number;
      TextView  duration;
      TextView  durationRinging;
      TextView  date;
      ImageView image;
      ImageView durationIcon;
      ImageView durationRingingIcon;
      ImageView type;
      ImageView makeCall;
      
      
      ViewCallHolder(View itemView){
         
         super(itemView);
         
         name                = itemView.findViewById(R.id.name);
         number              = itemView.findViewById(R.id.number);
         duration            = itemView.findViewById(R.id.callDuration);
         date                = itemView.findViewById(R.id.date);
         image               = itemView.findViewById(R.id.image);
         type                = itemView.findViewById(R.id.type);
         makeCall            = itemView.findViewById(R.id.makeCall);
         durationIcon        = itemView.findViewById(R.id.durationIcon);
         durationRingingIcon = itemView.findViewById(R.id.ringingIcon);
         durationRinging     = itemView.findViewById(R.id.ringingDuration);
         
         View view = itemView.findViewById(R.id.call_item);
         
         view.setOnClickListener(this::onCallItemClicked);
         view.setOnLongClickListener(this::onLongClick);
         makeCall.setOnClickListener(v -> makeCall(phoneCalls.get(getAdapterPosition())));
         
         view.setBackgroundResource(MainActivity.getWellRipple());
      }
      
      private void makeCall(ICall phoneCall){
   
         EventBus.getDefault().post(new CallNumberRequest(phoneCall.getNumber()));
         
      }
      
      private void onCallItemClicked(@SuppressWarnings("unused") View v){
         
         itemClicked(getAdapterPosition());
      }
      
      private void itemClicked(int index){
         
         if(itemSelectListener != null){
            itemSelectListener.onItemSelected(index);
         }
      }
      
      private boolean onLongClick(@SuppressWarnings("unused") View view){
         
         return u.copyToClipboard(itemView.getContext(), phoneCalls.get(getAdapterPosition()).getNumber());
      }
      
   }
   
   
}
