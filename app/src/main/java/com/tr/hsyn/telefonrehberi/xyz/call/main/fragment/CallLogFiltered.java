package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.main.Filter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.RandomCallsActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.backup.BackupCallLogActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.most.MostCallsActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.activity.search.CallLogSearchActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.CallFilterDialog;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogAction;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogCloseListener;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


/**
 * <h1>CallsFragment</h1>
 * <p>
 * Bu sınıf arama kayıtlarını filtreleme işlemini yönetir.
 */
public abstract class CallLogFiltered extends CallLogLoaded implements DialogAction{
   
   
   /**
    * Kayıtları yeniden yüklemeye gerek var mı?
    */
   private static                         boolean  needRefresh;
   /**
    * Seçilen menü elemanını tutacak. Ancak seçilen her menü elemanını değil.
    */
   private                                MenuItem menuItem;
   @Getter(AccessLevel.PROTECTED) private int      currentFilter;
   
   /**
    * Kayıtları filtremek için dialog
    */
   private CallFilterDialog callFilterDialog;
   
   @Override
   protected void onCallsLoaded(List<ICall> calls, Throwable throwable){
      
      super.onCallsLoaded(calls, throwable);
      
      if(currentFilter != ALL_CALLS){
   
         selectFilter(currentFilter);
      }
   }
   
   @Override
   public void onDialogClose(DialogInterface dialogInterface){
   
      menuItem.setEnabled(true);
      callFilterDialog = null;
   }
   
   @Override
   public void onDialogShow(DialogInterface dialogInterface){
      
   }
   
   /**
    * Kayıtların filreleme türünü uygula.
    * Bu metod kayıtlar her yüklendiğinde çağrılır.
    * Eğer tüm aramalar yükleniyorsa çağrılmaz.
    * Bu metodun çağrılması için tüm aramalar haricinde herhangi bir filtreme kullanılmış olmalı.
    * Mesela giden aramalar, gelen aramalar, en uzun konuşulan aramalar vs.
    *
    * @param filter Tür
    */
   private void selectFilter(int filter){
      
      if(isPrimitiveType(filter)){
         
         Worker.onBackground(this::getFilteredCalls, "CallLogFilteredCallsFragment:Filtrelenmiş arama kayıtlarını alma")
               .whenCompleteAsync(this::onLoadFilteredCalls, Worker.getMainThreadExecutor());
      }
      else{
   
         startMostCallActivity(filter);
      }
   }
   
   private List<ICall> getFilteredCalls(){
      
      return getCallConst().getCallStory().getFilteredCalls(currentFilter);
   }
   
   private void onLoadFilteredCalls(List<ICall> filteredCalls, Throwable throwable){
      
      getAdapter().setCalls(filteredCalls);
      whenCallsLoaded(filteredCalls);
   }
   
   /**
    * Seçilen filtre ile kayıtları göstermek için activity'yi başlat.
    *
    * @param filter Filtre
    */
   private void startMostCallActivity(int filter){
      
      Intent intent = new Intent(getContext(), MostCallsActivity.class);
      intent.putExtra(MostCallsActivity.EXTRA_CALL_TYPE, filter);
      
      startActivity(intent);
      Bungee.slideDown(getContext());
      
   }
   
   /**
    * Seçilen filtre basit filtreleme türlerinden biri mi?
    *
    * @param type Tür
    * @return Basit filtreleme ise {@code true}
    */
   private static boolean isPrimitiveType(int type){
      
      switch(type){
         
         case ALL_CALLS:
         case Filter.INCOMMING:
         case Filter.OUTGOING:
         case Filter.MISSED:
         case Filter.REJECTED:
         case Filter.BLOCKED:
         case Filter.DELETED:
         case Filter.MOST_SPEAKING:
         case Filter.MOST_TALKING:
            
            return true;
         
         default: return false;
      }
   }
   
   @Override
   public void onResume(){
      
      super.onResume();
      
      checkNeedRefresh();
      
   }
   
   @Override
   public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
   
     // menu.clear();
      super.onCreateOptionsMenu(menu, inflater);
      inflater.inflate(R.menu.call_fragment_menu, menu);
      
   }
   
   @Override
   public boolean onOptionsItemSelected(MenuItem item){
      
      switch(item.getItemId()){
         
         
         case R.id.menu_search:
            
            val kick = Kickback_CallDetailBox.getInstance();
            
            kick.setCalls(new ArrayList<>(getAdapter().getCalls()));
            Intent intent = new Intent(getContext(), CallLogSearchActivity.class);
            startActivity(intent);
            
            Bungee.fade(getContext());
            return true;
         
         case R.id.menu_delete_calls_db:
            
            askDeleteDatabase();
            return true;
         
         case R.id.menu_random_calls:
            
            startRandomCalls();
            return true;
         
         case R.id.delete_all_calls:
            
            deleteCalls(getCalls());
            return true;
         
         case R.id.filter_menu_item:
            
            menuItem = item;
            item.setEnabled(false);
            showFilterDialog();
            return true;
         
         case R.id.menu_call_log_backup:
            
            startActivity(new Intent(getContext(), BackupCallLogActivity.class));
            Bungee.slideLeft(getActivity());
            return true;
         
      }
      
      return super.onOptionsItemSelected(item);
   }
   
   /**
    * Filtreleme için dialoğu başlat
    */
   private void showFilterDialog(){
   
      callFilterDialog = new CallFilterDialog(getActivity(), currentFilter);
      callFilterDialog.setFilterListener(this::onFilterSelected);
      callFilterDialog.setCloseListener(new DialogCloseListener(this));
   }
   
   /**
    * Bir filtre seçildiğinde.
    *
    * @param v     Tıkanan view
    * @param index Seçilen filtrenin index'i
    */
   private void onFilterSelected(@SuppressWarnings("unused") View v, int index){
   
      Timber.d("Filter selected : %d - %s", index, getCallsTitle(index));
      
      //Sadece seçilen filtrenin basit türlerden biri olması durumunda index'i değiştir.
      //Çünkü diğer türler için ayrı bir activity açılıyor ve bu durumda filtreleme listesinde
      //seçili olan filtrenin değişmemesi gerek
      if(isPrimitiveType(index)) currentFilter = index;
      
      selectFilter(index);
   }
   
   /**
    * Aramanın türüne göre başlığı al.
    *
    * @param filter tür
    * @return Başlık
    */
   @SuppressWarnings("OverlyComplexMethod")
   @AnyThread
   @Override
   protected final String getCallsTitle(final int filter){
      
      final String title;
      //@off
      switch(filter){
         
         case ALL_CALLS       : title = getString(R.string.call_log); break;
         case Filter.INCOMMING                 : title = getString(R.string.incomming_calls); break;
         case Filter.OUTGOING                  : title = getString(R.string.outgoing_calls); break;
         case Filter.MISSED                    : title = getString(R.string.missed_calls); break;
         case Filter.REJECTED                  : title = getString(R.string.rejected_calls); break;
         case Filter.BLOCKED                   : title = getString(R.string.blocked_call); break;
         case Filter.DELETED                   : title = getString(R.string.deleted_calls); break;
         case Filter.MOST_SPEAKING             : title = getString(R.string.most_speaking); break;
         case Filter.MOST_TALKING              : title = getString(R.string.most_talking); break;
        
         default: title = "Aramalar";
      } //@on
      
      return title;
   }
   
   private void startRandomCalls(){
      
      Intent intent = new Intent(getContext(), RandomCallsActivity.class);
      
      startActivity(intent);
      Bungee.slideDown(getContext());
      
   }
   
   /**
    * Fragment pause durumuna geçip geri döndüğünde yeniden yükleme yapmanın gerekli olup olmadığını kontrol et
    */
   private void checkNeedRefresh(){
      
      if(needRefresh){
         
         needRefresh = false;
         loadCalls();
      }
      
   }
   
   @Override
   protected void loadCalls(){
      
      if(getContext() == null) return;
      
      if(isShowTime()){
         
         super.loadCalls();
      }
      else{
         
         if(EasyPermissions.hasPermissions(getContext(), getCallsPermissions())){
            
            super.loadCalls();
         }
      }
   }
   
   /**
    * Arama kayıtlarının yeniden yüklenmesi gerekiyor mu?
    *
    * @param needRefresh Gerekli mi?
    */
   public static void setNeedRefresh(boolean needRefresh){
   
      CallLogFiltered.needRefresh = needRefresh;
   }
   
   
}
