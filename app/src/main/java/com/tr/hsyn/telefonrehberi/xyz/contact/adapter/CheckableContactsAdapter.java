package com.tr.hsyn.telefonrehberi.xyz.contact.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.listener.CheckedChangeListener;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.List;


public class CheckableContactsAdapter extends RecyclerView.Adapter<CheckableContactsAdapter.ViewHolder>{
   
   private CheckedChangeListener checkedChangeListener;
   private List<IMainContact>    contacts;
   
   public CheckableContactsAdapter(List<IMainContact> contacts){
      
      this.contacts = contacts;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.checkable_contact_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      IMainContact contact = contacts.get(position);
      holder.name.setText(contact.getName());
      
      String name = contact.getName();
      
      if(name == null || name.trim().isEmpty()){
         
         name = "?";
      }
      
      TextDrawable drawable = TextDrawable.builder().buildRound(name.substring(0, 1), u.colorGenerator.getRandomColor());
      holder.image.setImageDrawable(drawable);
   }
   
   @Override
   public int getItemCount(){
      
      return contacts.size();
   }
   
   public CheckableContactsAdapter setCheckedChangeListener(CheckedChangeListener checkedChangeListener){
      
      this.checkedChangeListener = checkedChangeListener;
      return this;
   }
   
   class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      TextView  name;
      ImageView image;
      CheckBox  checkBox;
      
      ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         name     = itemView.findViewById(R.id.name);
         image    = itemView.findViewById(R.id.image);
         checkBox = itemView.findViewById(R.id.checkBox);
         
         itemView.setBackgroundResource(MainActivity.getWellRipple());
         
         itemView.setOnClickListener(this);
      }
      
      @Override
      public void onClick(View v){
         
         checkBox.setChecked(!checkBox.isChecked());
         
         if(checkedChangeListener != null){
            
            checkedChangeListener.checkedChange(v, checkBox.isChecked(), getAdapterPosition());
         }
         
      }
      
   }
   
}
