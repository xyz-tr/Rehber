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
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;


public class SelectAccountAdapter extends RecyclerView.Adapter<SelectAccountAdapter.ViewHolder>{
   
   private ItemSelectListener listener;
   private List<String>       accounts;
   
   public SelectAccountAdapter(List<String> accounts){
      
      this.accounts = accounts;
   }
   
   public void setClickListener(ItemSelectListener listener){
      
      this.listener = listener;
   }
   
   
   
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
   
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_select_account_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
    
      holder.textView.setText(accounts.get(position));
   
      Drawable drawable = TextDrawable
            .builder()
            .beginConfig()
            .useFont(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.z))
            .endConfig()
            .buildRound(accounts.get(position).substring(0, 1).toUpperCase(), u.colorGenerator.getRandomColor());
      
      holder.imageView.setImageDrawable(drawable);
   }
   
   @Override
   public int getItemCount(){
      
      return accounts.size();
   }
   
   class ViewHolder extends RecyclerView.ViewHolder{
   
      ImageView imageView;
      TextView  textView;
   
      public ViewHolder(@NonNull View itemView){
      
         super(itemView);
         
         imageView = itemView.findViewById(R.id.accountImage);
         textView = itemView.findViewById(R.id.accountName);
         
         itemView.setBackgroundResource(MainActivity.getWellRipple());
         
         itemView.setOnClickListener(v ->{
            
            if(listener != null) listener.onItemSelected(getAdapterPosition());
         });
         
         
      }
   
   }
}
