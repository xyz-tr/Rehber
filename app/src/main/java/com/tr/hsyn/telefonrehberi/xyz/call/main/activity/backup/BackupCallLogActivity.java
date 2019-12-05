package com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.BackupFileDeleted;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Message;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.util.ui.snack.SnackBarListener;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.CallLogBackupAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.SelectDialogListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.Kickback_CallsConst;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.val;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


//todo Yedeklenen bir dosyaya seçilecek bir arama kaydının eklenmesini sağla

public class BackupCallLogActivity extends AppCompatActivity implements SnackBarListener, ItemSelectListener, SelectDialogListener{
   
   
   private                        View                 emptyView;
   private                        List<ICall>          phoneCalls;
   private                        List<BackupItem>     backupItems;
   private                        ProgressBar          progress;
   private                        FloatingActionButton actionButton;
   private                        RecyclerViewParty    recyclerView;
   private                        MenuItem             menuItemCreateBackup;
   private                        BackupSize           backupSize;
   private                        int                  selectedItemPosition = -1;
   private static                 String               folderName           = ".com.tr.hsyn.telefonrehberi.backup.call.log";
   @Getter @Setter private static boolean              error;
   public static final            List<ICall>           deletedBackupCalls   = new ArrayList<>();
   public static final            String               BACKUP               = "backup";
   public static final            String               BACKUP_FOLDER        = "backupFolder";
   public static final            int                  RELOAD               = 0;
   public static final            int                  SHOW                 = 1;
   public static final            int                  DELETE               = 2;
   //public static final  int                  RC_ACTIONS         = 4;
   private static final           int                  RC_STORAGE           = 4;
   private static final           int                  RC_DELETED_CALLS     = 8;
   private static final           String[]             PERMISSIONS          = {
         
         Manifest.permission.READ_EXTERNAL_STORAGE,
         Manifest.permission.WRITE_EXTERNAL_STORAGE,
         Manifest.permission.READ_CALL_LOG,
         Manifest.permission.WRITE_CALL_LOG
   };
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_backup_call_log);
      
      folderName = getFolderName();
      
      setToolbar();
      
      recyclerView = new RecyclerViewParty(this,
                                           findViewById(R.id.backups),
                                           new CallLogBackupAdapter(new ArrayList<>()),
                                           this);
      
      recyclerView.setItemClickListener(this);
      
      emptyView    = findViewById(R.id.emptyView);
      progress     = findViewById(R.id.progress);
      actionButton = findViewById(R.id.actionButton);
      actionButton.setOnClickListener((v) -> onCreateButtonClicked());
      EventBus.getDefault().register(this);
      
      start();
   }
   
   @Override
   protected void onDestroy(){
      
      super.onDestroy();
      
      EventBus.getDefault().unregister(this);
   }
   
   @Override
   public void onSnackBarStarted(Object object){
      
      int h = (int) object;
      
      hideActionButton(h);
   }
   
   @Override
   public void onSnackBarFinished(Object object, boolean actionPressed){
      
      showActionButton();
   }
   
   private void showActionButton(){
      
      ViewCompat.animate(actionButton).rotation(0).translationY(0).setDuration(600).start();
   }
   
   private void hideActionButton(int h){
      
      ViewCompat.animate(actionButton).rotation(360).translationY(-h).setDuration(600).start();
   }
   
   @Override
   public void onItemSelected(int position){
      
      selectedItemPosition = position;
   }
   
   @Override
   public void onSelectDialogItem(int index){
      
      val item = backupItems.get(selectedItemPosition);
      
      if(item.isError()){
         
         Message.builder()
                .message("Dosya okunamıyor")
                .type(Show.ERROR)
                .build()
                .showOn(this);
         
         return;
      }
      
      
      File file = item.getFile();
      
      switch(index){
         
         case RELOAD:
            
            onReload(file);
            break;
         
         case SHOW:
            
            onShow(file);
            break;
         
         case DELETE:
            
            onDelete();
            break;
      }
   }
   
   private void onReload(File file){
      
      new ReloadBackupDialog(this, file);
   }
   
   private void onShow(Serializable file){
      
      if(file == null){
         
         Toast.makeText(this, getString(R.string.dosya_alınamadı), Toast.LENGTH_SHORT).show();
         return;
      }
      
      
      Intent i = new Intent(this, ShowBackupCallsActivity.class);
      i.putExtra(RecyclerViewParty.FILE, file);
      startActivityForResult(i, RC_DELETED_CALLS);
      Bungee.slideLeft(this);
   }
   
   private void onDelete(){
      
      if(selectedItemPosition == -1){ return; }
      
      BackupItem backupItem = backupItems.remove(selectedItemPosition);
      
      Worker.onBackground(() -> {
         
         List<ICall> phoneCalls = readCalls(backupItem.getFile().getAbsolutePath());
         
         Worker.onMain(() -> {
            
            if(!backupItem.getFile().delete()){
               
               Toast.makeText(this, getString(R.string.dosya_silinemedi), Toast.LENGTH_SHORT).show();
               return;
            }
            
            getAdapter().notifyItemRemoved(selectedItemPosition);
            
            onBackupFileDeleted(new BackupFileDeleted(backupItem, selectedItemPosition, phoneCalls));
            
         });
         
      }, "BackupCallLogActivity:Arama kaydı yediğini silindiğinde ara işlemleri tamamlama");
   }
   
   public static List<ICall> readCalls(String fileAbsolutePath){
      
      File file = new File(fileAbsolutePath);
      
      FileInputStream   fileInputStream;
      ObjectInputStream objectInputStream;
      
      try{
         
         fileInputStream   = new FileInputStream(file);
         objectInputStream = new ObjectInputStream(fileInputStream);
         
         
         //noinspection unchecked
         List<ICall> phoneCalls = (List<ICall>) objectInputStream.readObject();
         
         objectInputStream.close();
         fileInputStream.close();
         
         if(phoneCalls == null){
            
            Timber.w("Arama kayıtları okunamadı");
         }
         
         return phoneCalls;
         
      }
      catch(Exception e){
         Timber.w(e);
         error = true;
      }
      
      return null;
   }
   
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onBackupFileDeleted(BackupFileDeleted event){
      
      backupSize.setSize(backupItems.size());
      Timber.d("backup silindi");
      
      if(backupItems.isEmpty()){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      
      Message.builder()
             .message(u.format("%s Silindi", event.getBackupItem().getName()))
             .duration(5000L)
             .actionMessage("Geri Al")
             .clickListener((v) -> unDoDeletedFile(event))
             .delay(500)
             .build()
             .showOn(this);
   }
   
   private void unDoDeletedFile(final BackupFileDeleted event){
      
      Worker.onBackground(() -> {
         
         try{
            
            if(event.getBackupItem().getFile().createNewFile()){
               
               if(writeCalls(event.getBackupItem().getFile(), event.getPhoneCalls()) != null){
                  
                  recyclerView.getRecyclerView().post(() -> {
                     
                     recyclerView.addItem(event.getIndex(), event.getBackupItem());
                     
                     recyclerView.getRecyclerView().post(() -> backupSize.setSize(backupItems.size()));
                  });
               }
            }
         }
         catch(IOException e){
            e.printStackTrace();
         }
      }, "BackupCallLogActivity:Silinen arama kaydı yediğini geri getirme");
      
      
   }
   
   public File writeCalls(File file, List<ICall> phoneCalls){
      
      if(!file.exists()){
         
         if(!create(file)){
            
            Timber.w("Dosya oluşturulamadı");
            return null;
         }
      }
      
      
      FileOutputStream   fileOutputStream;
      ObjectOutputStream objectOutputStream;
      
      try{
         
         fileOutputStream   = new FileOutputStream(file);
         objectOutputStream = new ObjectOutputStream(fileOutputStream);
         
         objectOutputStream.writeObject(phoneCalls);
         objectOutputStream.close();
         fileOutputStream.close();
         return file;
      }
      catch(Exception e){
         
         Timber.w(e);
      }
      
      return null;
   }
   
   @SuppressWarnings("BooleanMethodIsAlwaysInverted")
   private boolean create(File file){
      
      try{
         return file.createNewFile();
      }
      catch(IOException e){
         e.printStackTrace();
      }
      
      return false;
   }
   
   private CallLogBackupAdapter getAdapter(){
      
      return (CallLogBackupAdapter) recyclerView.getRecyclerView().getAdapter();
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      getMenuInflater().inflate(R.menu.backup_activity, menu);
      
      return true;
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      
      
      //noinspection SwitchStatementWithTooFewBranches
      switch(item.getItemId()){
         
         
         case R.id.menu_new_backup_file:
            
            item.setEnabled(false);
            menuItemCreateBackup = item;
            createNewBackup();
            return true;
      }
      
      
      return false;
   }
   
   private void createNewBackup(){
      
      if(EasyPermissions.hasPermissions(this, Manifest.permission.READ_CALL_LOG)){
         
         progress.setVisibility(View.VISIBLE);
         
         Worker.onBackground(
               () -> writeCalls(getNewBackupFile(), phoneCalls), "BackupCallLogActivity:Arama kayıtları için backup dosyası oluşturma")
               .whenCompleteAsync(this::onNewBackupFileCreated, Worker.getMainThreadExecutor());
         
      }
      else{
         
         Message.builder()
                .message("Arama kayıtlarına erişim izni yok")
                .type(Show.WARN)
                .actionMessage("izin ver")
                .clickListener(v -> start())
                .cancel(true)
                .build()
                .showOn(this);
      }
      
   }
   
   private void start(){
      
      if(EasyPermissions.hasPermissions(this, PERMISSIONS)){
         
         onPermissionsGranted();
      }
      else{
         
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(PERMISSIONS, RC_STORAGE);
         }
      }
   }
   
   @AfterPermissionGranted(RC_STORAGE)
   private void onPermissionsGranted(){
      
      phoneCalls = Kickback_CallsConst.getInstance().getCallStory().getCalls();
      
      Message.builder()
             .message("Arama kayıtları toplandı")
             .cancel(true)
             .duration(4000L)
             .delay(300L)
             .build()
             .showOn(this);
      
      if(isExternalStorageWritable()){
         
         test();
      }
      else{
         
         Show.globalMessage(this, "Hafıza yazılabilir değil..");
      }
      
      
   }
   
   private void test(){
      
      if(phoneCalls == null){
         
         Timber.w("Gerekli bilgiler alınamadı");
         return;
      }
      
      File backupFolder = setFolderName(folderName);
      
      if(backupFolder == null){ return; }
      
      backupItems = new ArrayList<>();
      File[] files = backupFolder.listFiles();
      
      assert files != null;
      if(files.length == 0){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      
      Timber.d("%d tane yedek var", files.length);
      
      
      for(File file : files){
         
         BackupItem backupItem = new BackupItem();
         backupItem.setFile(file);
         
         backupItems.add(backupItem);
         
         Timber.d("backupFile : %s", file.getName());
      }
      
      Collections.sort(backupItems, (o1, o2) -> Long.compare(Long.parseLong(o2.getFile().getName()), Long.parseLong(o1.getFile().getName())));
      
      recyclerView.setAdapterList(backupItems);
      
      backupSize.setSize(backupItems.size());
   }
   
   private File setFolderName(String folderName){
      
      File folder = new File(Environment.getExternalStorageDirectory(), folderName);
      
      if(folder.exists()){
         
         if(folder.isFile()){
            
            Timber.d("Aynı isimde bir dosya da mevcut : %s", folder.getAbsolutePath());
            Show.globalMessage(this, getString(R.string.msg_backup_activity_folder_already_exist_with_a_filename), 6000);
            
            //todo yeni bir klasör ismi al
            
            return null;
         }
         
         if(folder.isDirectory()){
            
            Timber.d("Klasör mevcut : %s", folder.getAbsolutePath());
            return folder;
         }
      }
      else{
         
         Timber.d("Klasör mevcut değil : %s", folderName);
         
         if(folder.mkdir()){
            
            Timber.d("Klasör oluşturuldu : %s", folder.getAbsolutePath());
            return folder;
         }
         else{
            
            Timber.d("Klasör oluşturulamadı : %s", folderName);
         }
      }
      
      return null;
   }
   
   public boolean isExternalStorageWritable(){
      
      return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
   }
   
   private File getNewBackupFile(){
      
      return new File(getBackupFolder(), getNewFileName());
   }
   
   private File getBackupFolder(){
      
      return new File(Environment.getExternalStorageDirectory(), folderName);
      
   }
   
   private String getNewFileName(){
      
      return String.valueOf(new Date().getTime());
   }
   
   private void onNewBackupFileCreated(File file, Throwable throwable){
      
      progress.setVisibility(View.GONE);
      
      if(file == null){
         
         Timber.d("Yeni yedek oluşturulamadı");
         return;
      }
      
      emptyView.setVisibility(View.GONE);
      
      BackupItem item = new BackupItem();
      item.setFile(file);
      
      recyclerView.addItem(item);
      
      Show.globalMessage(this, getString(R.string.msg_new_backup_created, BackupItem.convertBackupName(file.getName())));
      
      if(menuItemCreateBackup != null){ menuItemCreateBackup.setEnabled(true); }
      
      backupSize.setSize(backupItems.size());
      
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideRight(this);
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      if(requestCode == RC_DELETED_CALLS){
         
         if(resultCode == RESULT_OK){
            
            if(data == null){ return; }
            
            File file = (File) data.getSerializableExtra(RecyclerViewParty.FILE);
            
            onDeletedBackupCalls(file);
         }
      }
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
   }
   
   private void onCreateButtonClicked(){
      
      createNewBackup();
   }
   
   private void setToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      int color = u.getPrimaryColor(this);
      
      toolbar.setBackgroundColor(color);
      getWindow().setStatusBarColor(color);
      
      backupSize = new BackupSize(getSupportActionBar());
      
   }
   
   private void onDeletedBackupCalls(File file){
      
      if(deletedBackupCalls.isEmpty()){
         
         Timber.d("Silinen bir kayıt yok");
         return;
      }
      
      List<ICall> phoneCalls = readCalls(file.getAbsolutePath());
      
      if(phoneCalls == null){
         
         Toast.makeText(this, getString(R.string.dosya_okunamadi), Toast.LENGTH_SHORT).show();
         return;
      }
      
      if(phoneCalls.size() == deletedBackupCalls.size()){
         
         onDelete();
         return;
      }
      
      if(!file.delete()){
         
         Toast.makeText(this, getString(R.string.dosya_silinemedi), Toast.LENGTH_SHORT).show();
         return;
      }
      
      if(!create(file)){
         
         Toast.makeText(this, getString(R.string.dosya_oluşturulamadı), Toast.LENGTH_SHORT).show();
         return;
      }
      
      phoneCalls.removeAll(deletedBackupCalls);
      
      writeCalls(file, phoneCalls);
      
      deletedBackupCalls.clear();
      
      Show.globalMessage(this, getString(R.string.kayıtlar_düzenleniyor));
      test();
      
   }
   
   private String getFolderName(){
      
      return getSharedPreferences(BACKUP, MODE_PRIVATE).getString(BACKUP_FOLDER, folderName);
   }
   
   public static class BackupSize{
      
      private final ActionBar actionBar;
      
      BackupSize(ActionBar actionBar){
         
         this.actionBar = actionBar;
      }
      
      public void setSize(int size){
         
         actionBar.setSubtitle(String.valueOf(size));
      }
      
   }
   
   
}
