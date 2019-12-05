package com.tr.hsyn.telefonrehberi.xyz.call.main.adapter;


import android.annotation.SuppressLint;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.BackupFileDeleted;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup.BackupCallLogActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup.BackupItem;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class CallLogBackupAdapter extends RecyclerView.Adapter<CallLogBackupAdapter.ViewHolder>{
   
   private List<BackupItem>   backups;
   private ItemSelectListener itemSelectListener;
   
   public CallLogBackupAdapter(List<BackupItem> backups){
      
      this.backups = backups;
   }
   
   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
      
      return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.backup_item, viewGroup, false));
   }
   
   @SuppressLint("SetTextI18n")
   @Override
   public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i){
      
      final BackupItem item = backups.get(i);
      
      viewHolder.name.setText(item.getName());
      
      Worker.onBackground(() -> {
         
         final List<ICall> phoneCalls = BackupCallLogActivity.readCalls(item.getFile().getAbsolutePath());
         
         if(phoneCalls != null){
            
            viewHolder.itemView.post(() -> {
               
               viewHolder.count.setText(u.format("%d Kayıt", phoneCalls.size()));
               
               TransitionManager.beginDelayedTransition((ViewGroup) viewHolder.itemView);
               
               viewHolder.progressBar.setVisibility(View.GONE);
               viewHolder.count.setVisibility(View.VISIBLE);
            });
         }
         else{
            
            viewHolder.itemView.post(() -> {
               
               viewHolder.count.setText("Kayıt Okunamadı");
               
               item.setError(true);
               TransitionManager.beginDelayedTransition((ViewGroup) viewHolder.itemView);
               
               viewHolder.progressBar.setVisibility(View.GONE);
               viewHolder.count.setVisibility(View.VISIBLE);
            });
         }
         
      }, 1000L);
   }
   
   @Override
   public int getItemCount(){
      
      return backups.size();
   }
   
   public List<BackupItem> getBackups(){
      
      return backups;
   }
   
   public void setBackups(List<BackupItem> backups){
      
      this.backups = backups;
   }
   
   public CallLogBackupAdapter setItemSelectListener(ItemSelectListener itemSelectListener){
      
      this.itemSelectListener = itemSelectListener;
      return this;
   }
   
   public final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
      
      TextView name, count;
      ProgressBar progressBar;
      ImageView   delete;
      final View itemView;
      
      public ViewHolder(View item){
         
         super(item);
         this.itemView = item;
         
         name        = item.findViewById(R.id.name);
         count       = item.findViewById(R.id.count);
         progressBar = item.findViewById(R.id.progress);
         delete      = item.findViewById(R.id.delete);
         
         delete.setOnClickListener((v) -> onDeleteBackup(getAdapterPosition()));
         item.findViewById(R.id.root).setOnClickListener(this);
         
         item.findViewById(R.id.root).setBackgroundResource(MainActivity.getWellRipple());
      }
      
      
      public void onDeleteBackup(int index){
         
         BackupItem item = backups.get(index);
         
         Worker.onBackground(() -> {
            
            final List<ICall> phoneCalls = BackupCallLogActivity.readCalls(item.getFile().getAbsolutePath());
            
            if(item.getFile().delete()){
               
               itemView.post(() -> {
                  
                  backups.remove(index);
                  notifyItemRemoved(index);
               });
               
               EventBus.getDefault().post(new BackupFileDeleted(item, index, phoneCalls));
            }
         }, "CallLogBackupAdapter:Arama kaydı yediğini silme");
      }
      
      @Override
      public void onClick(View v){
         
         if(itemSelectListener != null){
            itemSelectListener.onItemSelected(getAdapterPosition());
         }
      }
   }
   
}
