package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Send;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.BackupCallsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.OnDeleteCallListener;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;

import java.io.File;
import java.util.List;


public class ShowBackupCallsActivity extends AppCompatActivity implements OnDeleteCallListener{
   
   private FastScrollRecyclerView recyclerView;
   private List<ICall>            phoneCalls;
   private File                   file;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_show_backup_calls);
      
      setToolbar();
      
      if(!getArguments()){
   
         Send.globalMessage(getString(R.string.gerekli_bilgiler_sağlanmadı));
         onBackPressed();
      }
      
      setTitle(BackupItem.convertBackupName(file.getName()));
      setSubTitle();
      
      recyclerView = findViewById(R.id.recyclerView);
      recyclerView.setAdapter(new BackupCallsAdapter(phoneCalls, this));
      ColorController.setRecyclerColor(this, recyclerView);
   }
   
   private boolean getArguments(){
      
      Intent i = getIntent();
      
      if(i == null){
         
         return false;
      }
   
      file       = (File) i.getSerializableExtra(RecyclerViewParty.FILE);
      phoneCalls = BackupCallLogActivity.readCalls(file.getAbsolutePath());
   
      return phoneCalls != null;
   }
   
   private void setToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      int color = u.getPrimaryColor(this);
      toolbar.setBackgroundColor(color);
      getWindow().setStatusBarColor(u.darken(color, .9F));
   }
   
   private void setTitle(String title){
      
      //noinspection ConstantConditions
      getSupportActionBar().setTitle(title);
   }
   
   private void setSubTitle(){
      
      //noinspection ConstantConditions
      getSupportActionBar().setSubtitle(phoneCalls.size() + " Kayıt");
   }
   
   @Override
   public void onBackPressed(){
      
      Intent i = new Intent();
      
      i.putExtra(RecyclerViewParty.FILE, file);
      setResult(RESULT_OK, i);
      
      super.onBackPressed();
      Bungee.slideRight(this);
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public void onDeleteCall(int position){
      
      BackupCallLogActivity.deletedBackupCalls.add(phoneCalls.get(position));
      
      phoneCalls.remove(position);
      getAdater().notifyItemRemoved(position);
      
      setSubTitle();
   }
   
   private BackupCallsAdapter getAdater(){
      
      return (BackupCallsAdapter) recyclerView.getAdapter();
   }
}
