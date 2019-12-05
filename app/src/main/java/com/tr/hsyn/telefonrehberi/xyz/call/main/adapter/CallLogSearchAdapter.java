package com.tr.hsyn.telefonrehberi.xyz.call.main.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.annimon.stream.Stream;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Adapterx;
import com.tr.hsyn.telefonrehberi.util.listener.SearchTextChangeListener;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.CallMaker;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class CallLogSearchAdapter extends RecyclerView.Adapter<CallLogSearchAdapter.SearchViewHolder> implements SearchTextChangeListener, FastScrollRecyclerView.SectionedAdapter{
   
   
   private final CallMaker          caller;
   private       List<ICall>        phoneCalls;
   private       ItemSelectListener itemSelectListener;
   private       String             searchText   = Stringx.EMPTY;
   private       List<ICall>         filteredList = new ArrayList<>();
   private       boolean            isNumber;
   
   public CallLogSearchAdapter(List<ICall> phoneCalls, CallMaker caller, ItemSelectListener listener){
   
      this.phoneCalls    = phoneCalls;
      this.caller        = caller;
      itemSelectListener = listener;
      filteredList.addAll(phoneCalls);
   }

   public List<ICall> getPhoneCalls(){
      
      return phoneCalls;
   }
   
   public List<ICall> getFilteredList(){
      
      return filteredList;
   }
   
   @NonNull
   @Override
   public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new SearchViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.call_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull SearchViewHolder holder, int position){
      
      final ICall phoneCall = filteredList.get(position);
      
      holder.number.setText(CallStory.formatNumberForDisplay(phoneCall.getNumber()));
      holder.date.setText(u.formatDate(phoneCall.getDate()));
      
      String name   = phoneCall.getName();
      String number = phoneCall.getNumber();
      String letter = ContactAdapter.getLetter(name);
      
      if(name == null){
         
         name = phoneCall.getNumber();
      }
      
      holder.name.setText(name);
      holder.duration.setText(CallAdapter.UNKNOWN_DURATION);
      setRinging(phoneCall, holder);
      
      if(!searchText.isEmpty()){
         
         if(!isNumber){
            
            Integer[] indexes = Stringx.indexOfMatches(name.toLowerCase(), searchText.toLowerCase());
   
            Timber.d(Arrays.toString(indexes));
            
            Spanner spanner = new Spanner(name);
            
            for(int i = 0; i < indexes.length - 1; i += 2){
               
               spanner.setSpans(indexes[i], indexes[i + 1], Spans.background(Color.GREEN));
            }
            
            holder.name.setText(spanner);
            holder.number.setText(CallStory.formatNumberForDisplay(number));
         }
         else{
            
            Integer[] indexes = Stringx.indexOfMatches(number, searchText);
            Timber.d(Arrays.toString(indexes));
            Spanner spanner = new Spanner(number);
            
            for(int i = 0; i < indexes.length - 1; i += 2){
               
               spanner.setSpans(indexes[i], indexes[i + 1], Spans.background(Color.GREEN));
            }
            
            holder.name.setText(name);
            holder.number.setText(spanner);
         }
      }
      
      int color = u.colorGenerator.getRandomColor();
      
      Drawable drawable = TextDrawable.builder().buildRound(letter.toLowerCase(), color);
      holder.image.setImageDrawable(drawable);
      
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
   
   @Override
   public int getItemCount(){
      
      return filteredList.size();
   }
   
   @Override
   public void onViewRecycled(@NonNull SearchViewHolder holder){
      
      super.onViewRecycled(holder);
      
      int index = holder.getAdapterPosition();
      
      if(index == -1) return;
      
      ICall phoneCall = filteredList.get(index);
      
      setRinging(phoneCall, holder);
      
      holder.itemView.clearAnimation();
   }
   
   private void setRinging(ICall phoneCall, SearchViewHolder holder){
      
      if(phoneCall.getRingingDuration() != 0){
         
         holder.durationRinging.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
      }
      else{
         
         holder.durationRinging.setText(CallAdapter.UNKNOWN_DURATION);
      }
   }
   
   public boolean remove(Call phoneCall){
      
      return Adapterx.remove(this, filteredList, phoneCall);
   }
   
   @Override
   public void onSearchTextChange(String text){
      
      searchText = Stringx.trimLeft(text);
      
      if(text.isEmpty()){
         
         filteredList.clear();
         filteredList.addAll(phoneCalls);
      }
      else{
         
         if(!isPhoneNumber(text)){
            
            filteredList = searchByName(text.toLowerCase());
            isNumber     = false;
         }
         else{
            
            filteredList = searchByNumber(text);
            isNumber     = true;
         }
      }
      
      notifyDataSetChanged();
   }
   
   private List<ICall> searchByName(String searchText){
      
      return Stream.of(phoneCalls).filter(c -> Stringx.isMatch(c.getName() != null ? c.getName().toLowerCase() : "", searchText.toLowerCase())).toList();
   }
   
   private List<ICall> searchByNumber(String searchText){
      
      return Stream.of(phoneCalls).filter(c -> Stringx.isMatch(c.getNumber(), searchText)).toList();
   }
   
   public List<ICall> getCalls(){
      
      return filteredList;
   }
   
   @NonNull
   @Override
   public String getSectionName(int position){
      
      return u.formatShortDate(filteredList.get(position).getDate());
   }
   
   final class SearchViewHolder extends RecyclerView.ViewHolder{
      
      final TextView  name;
      final TextView  number;
      final TextView  duration;
      final TextView  durationRinging;
      final TextView  date;
      final ImageView image;
      final ImageView durationIcon;
      final ImageView durationRingingIcon;
      final ImageView type;
      final ImageView makeCall;
      
      
      SearchViewHolder(@NonNull View itemView){
         
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
         makeCall.setOnClickListener(v -> makeCall(filteredList.get(getAdapterPosition())));
         
         view.setBackgroundResource(MainActivity.getWellRipple());
      }
      
      private void onCallItemClicked(View v){
         
         itemClicked(getAdapterPosition());
      }
      
      private boolean onLongClick(View view){
         
         return u.copyToClipboard(itemView.getContext(), filteredList.get(getAdapterPosition()).getNumber());
      }
      
      private void makeCall(ICall phoneCall){
         
         if(caller != null){
            caller.call(phoneCall.getNumber());
         }
      }
      
      private void itemClicked(int position){
         
         if(itemSelectListener != null){
            itemSelectListener.onItemSelected(position);
         }
      }
      
   }
   
   
}
