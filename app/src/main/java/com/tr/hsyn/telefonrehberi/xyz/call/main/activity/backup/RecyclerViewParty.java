package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.dialog.SelectDialog;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.CallLogBackupAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.SelectDialogListener;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import java.util.List;


@SuppressWarnings("WeakerAccess")
public class RecyclerViewParty implements ItemSelectListener{
   
   private      BackupCallLogActivity activity;
   private      RecyclerView          recyclerView;
   private      CallLogBackupAdapter  adapter;
   private      ItemSelectListener    clickListener;
   static final String                FILE = "file";
   private      SelectDialogListener  listener;
   
   public void setItemClickListener(ItemSelectListener listener){
      
      this.clickListener = listener;
   }
   
   public RecyclerViewParty(BackupCallLogActivity activity, RecyclerView recyclerView, CallLogBackupAdapter adapter, SelectDialogListener listener){
      
      this.activity     = activity;
      this.recyclerView = recyclerView;
      this.adapter      = adapter;
      this.listener     = listener;
      
      recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
      recyclerView.setAdapter(adapter.setItemSelectListener(this));
      
   }
   
   public void addItem(BackupItem item){
      
      adapter.getBackups().add(item);
      adapter.notifyItemInserted(adapter.getItemCount() - 1);
   }
   
   public void addItem(int index, BackupItem item){
      
      getAdapterList().add(index, item);
      adapter.notifyItemInserted(index);
   }
   
   public void setAdapter(CallLogBackupAdapter adapter){
      
      this.adapter = adapter;
   }
   
   public void setAdapterList(List<BackupItem> items){
      
      adapter.setBackups(items);
      adapter.notifyDataSetChanged();
   }
   
   private List<BackupItem> getAdapterList(){
      
      return adapter.getBackups();
   }
   
   @Override
   public void onItemSelected(int position){
      
      if(clickListener != null){ clickListener.onItemSelected(position); }
      
      new SelectDialog.Builder(activity)
            .setItems(new String[]{"Geri Yükle", "Göster", "Sil"})
            .setListener(listener)
            .setTitle(BackupItem.convertBackupName(getAdapterList().get(position).getFile().getName()))
            .setTitleIcon(activity.getDrawable(R.drawable.question_icon))
            .build();
      
      /*Intent i = new Intent(activity, BackupActionsActivity.class);
      i.putExtra(FILE, getAdapterList().getValue(position).getFile());
   
      activity.startActivityForResult(i, BackupCallLogActivity.RC_ACTIONS);
      Bungee.slideUp(activity);*/
      
   }
   
   public RecyclerView getRecyclerView(){
      
      return recyclerView;
   }
   
}
