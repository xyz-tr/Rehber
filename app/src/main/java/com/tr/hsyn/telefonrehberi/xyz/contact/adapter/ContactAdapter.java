package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;

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
import com.tr.hsyn.telefonrehberi.util.Adapterx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.LongClickListener;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter{
   
   private       ItemSelectListener contactSelectListener;
   private       LongClickListener  longClickListener;
   private       LayoutInflater     inflater;
   private final List<IMainContact> contacts;
   
   public ContactAdapter(final List<IMainContact> contacts, ItemSelectListener contactSelectListener){
      
      this.contacts              = contacts;
      this.contactSelectListener = contactSelectListener;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      if(inflater == null){
         
         inflater = LayoutInflater.from(parent.getContext());
      }
      
      return new ViewHolder(inflater.inflate(R.layout.contact_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      final IMainContact contact = contacts.get(position);
      String             name    = contact.getName();
      
      if(name != null){
         
         holder.name.setText(name);
      }
      else{
         
         name = "?";
         holder.name.setText(holder.name.getContext().getString(R.string.no_name));
      }
      
      Drawable drawable = TextDrawable
            .builder()
            .beginConfig()
            .useFont(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.z))
            .endConfig()
            .buildRound(getLetter(name), u.colorGenerator.getRandomColor());
      
      holder.image.setImageDrawable(drawable);
   }
   
   @NonNull
   public static String getLetter(String name){
      
      if(name == null){ name = ""; }
      
      if(name.trim().isEmpty()){ return "?"; }
      
      if(!isPhoneNumber(name)){ return name.substring(0, 1).toUpperCase(); }
      
      return "?";
   }
   
   @Override
   public int getItemCount(){ return contacts.size(); }
   
   @NonNull
   @Override
   public String getSectionName(int position){
      
      return getLetter(contacts.get(position).getName());
   }
   
   public boolean remove(IMainContact contact){
      
      return Adapterx.remove(this, contacts, contact);
   }
   
   public List<IMainContact> getContacts(){
      
      return contacts;
   }
   
   public ContactAdapter setLongClickListener(final LongClickListener longClickListener){
      
      this.longClickListener = longClickListener;
      return this;
   }
   
   final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
      
      final TextView  name;
      final ImageView image;
      
      ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         name  = itemView.findViewById(R.id.name);
         image = itemView.findViewById(R.id.image);
         
         itemView.setOnClickListener(this);
         itemView.setOnLongClickListener(this);
         itemView.setBackgroundResource(MainActivity.getWellRipple());
      }
      
      @Override
      public void onClick(View view){
         
         onClicked(view, getAdapterPosition());
      }
      
      private void onClicked(final View view, final int position){
         
         if(contactSelectListener != null){
            contactSelectListener.onItemSelected(position);
         }
      }
      
      @Override
      public boolean onLongClick(View v){
         
         if(longClickListener != null){
            longClickListener.onLongClick(v, getAdapterPosition());
         }
         return true;
      }
   }
}
