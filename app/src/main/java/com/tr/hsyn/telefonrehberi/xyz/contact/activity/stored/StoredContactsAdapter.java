package com.tr.hsyn.telefonrehberi.xyz.contact.activity.stored;

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
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.Contact;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;



public class StoredContactsAdapter extends RecyclerView.Adapter<StoredContactsAdapter.ViewHolder>
      implements FastScrollRecyclerView.SectionedAdapter{
   
   private LayoutInflater     inflater;
   private List<Contact>      contacts;
   private ItemSelectListener listener;
   
   StoredContactsAdapter(List<Contact> contacts, ItemSelectListener listener){
      
      this.contacts = contacts;
      this.listener = listener;
   }
   
   @NonNull @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      if(inflater == null){
         
         inflater = LayoutInflater.from(parent.getContext());
      }
      
      return new ViewHolder(inflater.inflate(R.layout.contact_stored_item, parent, false), listener);
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      Contact contact = contacts.get(position);
      
      holder.name.setText(contact.getName());
      holder.number.setText(CallStory.formatNumberForDisplay(contact.getNumber()));
      
      
      Drawable drawable =
            TextDrawable.builder()
                        .beginConfig()
                        .useFont(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.z))
                        .endConfig()
                        .buildRound(
                              ContactAdapter.getLetter(contact.getName()),
                              u.colorGenerator.getRandomColor());
      
      holder.image.setImageDrawable(drawable);
      
      setDeleted(holder, contact.getDeletedDate() != 0L);
   }
   
   private void setDeleted(@NonNull ViewHolder holder, boolean deleted){
      
      if(deleted) holder.deleteIcon.setVisibility(View.VISIBLE);
      else holder.deleteIcon.setVisibility(View.GONE);
   }
   
   @Override
   public int getItemCount(){
      
      return contacts.size();
   }
   
   @Override public void onViewRecycled(@NonNull ViewHolder holder){
      
      super.onViewRecycled(holder);
      Contact contact;
      
      try{
   
         contact = contacts.get(holder.getAdapterPosition());
      }
      catch(Exception e){
         
         return;
      }
      
      if(contact == null) return;
      
      setDeleted(holder, contact.getDeletedDate() != 0L);
   }
   
   @NonNull @Override
   public String getSectionName(int position){
      
      return ContactAdapter.getLetter(contacts.get(position).getName());
   }
   
   static final class ViewHolder extends RecyclerView.ViewHolder{
      
      @BindView(R.id.image)       ImageView image;
      @BindView(R.id.delete_icon) ImageView deleteIcon;
      @BindView(R.id.name)        TextView  name;
      @BindView(R.id.item)        TextView  number;
      
      
      ViewHolder(@NonNull View itemView, ItemSelectListener listener){
         
         super(itemView);
         
         ButterKnife.bind(this, itemView);
         
         u.setTintDrawable(deleteIcon.getDrawable(), ColorController.getPrimaryColor().getValue());
         val mainContent = itemView.findViewById(R.id.main_layout);
         mainContent.setOnClickListener(v -> onClick(listener, getAdapterPosition()));
         mainContent.setBackgroundResource(MainActivity.getWellRipple());
      }
      
      void onClick(ItemSelectListener listener, int position){
         
         if(listener != null) listener.onItemSelected(position);
      }
   }
}
