package com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.SelectDialogListener;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import timber.log.Timber;


@SuppressWarnings({"MethodParameterOfConcreteClass", "MethodReturnOfConcreteClass"})
public class SelectDialogAdapter extends RecyclerView.Adapter<SelectDialogAdapter.ViewHolder>{
   
   private CharSequence[]       items;
   private SelectDialogListener listener;
   
   public SelectDialogAdapter(CharSequence[] items, SelectDialogListener listener){
      
      this.items    = items;
      this.listener = listener;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
      
      return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.select_dialog_item, parent, false));
   }
   
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position){
      
      CharSequence item = items[position];
      
      holder.item.setText(item);
   }
   
   @Override
   public int getItemCount(){
      
      return items.length;
   }
   
   @SuppressWarnings("ClassNameSameAsAncestorName")
   final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      final TextView item;
      
      
      ViewHolder(@NonNull View itemView){
         
         super(itemView);
         
         item = itemView.findViewById(R.id.item);
         
         itemView.setOnClickListener(this);
         
         itemView.setBackgroundResource(MainActivity.getWellRipple());
      }
      
      @Override
      public void onClick(View v){
         
         if(listener != null) listener.onSelectDialogItem(getAdapterPosition());
         else{
            
            Timber.w("There is no listener");
         }
      }
      
   }
   
}
