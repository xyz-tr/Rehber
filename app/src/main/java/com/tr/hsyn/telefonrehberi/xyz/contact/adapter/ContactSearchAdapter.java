package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.annimon.stream.Stream;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.listener.SearchTextChangeListener;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.ISimpleContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.ContactSelectListener;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class ContactSearchAdapter extends RecyclerView.Adapter<ContactSearchAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter, SearchTextChangeListener{
   
   private final   List<ISimpleContact>  contactList;
   private         List<ISimpleContact>  filteredList = new ArrayList<>();
   private         ContactSelectListener selectListener;
   private         String                searchText   = Stringx.EMPTY;
   private         boolean               isNumber;
   @Setter private int                   markColor    = Color.GREEN;
   
   public ContactSearchAdapter(List<ISimpleContact> contactList){
      
      this.contactList = contactList;
      filteredList.addAll(contactList);
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_with_number, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      ISimpleContact contact = filteredList.get(position);
      String         name    = contact.getName();
      String         number  = contact.getNumber();
      
      if(name == null) name = Stringx.EMPTY;
      
      if(number == null){
         
         number = Stringx.EMPTY;
         holder.number.setVisibility(View.GONE);
      }
      
      if(!searchText.isEmpty()){
         
         if(!isNumber){
            
            Integer[] indexes = Stringx.indexOfMatches(name.toLowerCase(), searchText.toLowerCase());
            
            //u.log.d(Arrays.toString(indexes));
            
            Spanner spanner = new Spanner(name);
            
            for(int i = 0; i < indexes.length - 1; i += 2){
               
               spanner.setSpans(indexes[i], indexes[i + 1], Spans.background(markColor));
            }
            
            holder.name.setText(spanner);
            holder.number.setText(CallStory.formatNumberForDisplay(number));
         }
         else{
            
            Integer[] indexes = Stringx.indexOfMatches(number, searchText);
            //u.log.d(Arrays.toString(indexes));
            Spanner spanner = new Spanner(number);
            
            for(int i = 0; i < indexes.length - 1; i += 2){
               
               spanner.setSpans(indexes[i], indexes[i + 1], Spans.background(Color.GREEN));
            }
            
            holder.name.setText(name);
            holder.number.setText(spanner);
         }
      }
      else{
         
         holder.name.setText(name);
         holder.number.setText(CallStory.formatNumberForDisplay(number));
         
         if(isPhoneNumber(name)){
            
            holder.number.setVisibility(View.GONE);
            holder.name.setText(number);
         }
      }
      
      Drawable drawable =
            TextDrawable.builder()
                        .beginConfig()
                        .useFont(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.z))
                        .endConfig()
                        .buildRound(ContactAdapter.getLetter(name), u.colorGenerator.getRandomColor());
      holder.image.setImageDrawable(drawable);
   }
   
   /**
    * Returns the total number of items in the data setValue held by the adapter.
    *
    * @return The total number of items in this adapter.
    */
   @Override
   public int getItemCount(){
      
      return filteredList.size();
   }
   
   @NonNull
   @Override
   public String getSectionName(int position){
      
      return ContactAdapter.getLetter(filteredList.get(position).getName());
   }
   
   @Override
   public void onSearchTextChange(String text){
      
      if(text == null) return;
      
      searchText = text;
      
      if(text.isEmpty()){
         
         filteredList.clear();
         filteredList.addAll(contactList);
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
   
   private List<ISimpleContact> searchByName(String s){
      
      return Stream.of(contactList).filter(c -> Stringx.isMatch(c.getName() == null ? "" : c.getName().toLowerCase(), s.toLowerCase())).toList();
   }
   
   private List<ISimpleContact> searchByNumber(String s){
      
      return Stream.of(contactList).filter(c -> Stringx.isMatch(c.getNumber(), s)).toList();
   }
   
   public List<ISimpleContact> getFilteredList(){
      
      return filteredList;
   }
   
   public ContactSearchAdapter setContactSelectListener(ContactSelectListener listener){
      
      selectListener = listener;
      return this;
   }
   
   class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      TextView  name;
      TextView  number;
      ImageView image;
      
      ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         name   = itemView.findViewById(R.id.name);
         number = itemView.findViewById(R.id.item);
         image  = itemView.findViewById(R.id.image);
         
         itemView.findViewById(R.id.mainLayout).setOnClickListener(this);
      }
      
      
      @Override
      public void onClick(View v){
         
         if(selectListener != null){
            selectListener.onContactSelect(filteredList.get(getAdapterPosition()).getContactId());
         }
      }
      
      
   }
   
}
