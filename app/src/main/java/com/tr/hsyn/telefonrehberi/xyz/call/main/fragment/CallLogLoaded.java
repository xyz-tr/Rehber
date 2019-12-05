package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableList;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.mikepenz.itemanimators.SlideRightAlphaAnimator;
import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Dialog;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.CallDeleted;
import com.tr.hsyn.telefonrehberi.util.event.ColorChanged;
import com.tr.hsyn.telefonrehberi.util.event.DeleteCall;
import com.tr.hsyn.telefonrehberi.util.event.EventDeleteSearchCall;
import com.tr.hsyn.telefonrehberi.util.event.NewCall;
import com.tr.hsyn.telefonrehberi.util.observable.Observe;
import com.tr.hsyn.telefonrehberi.util.swipe.SwipeCallBack;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.database.CallLogDB;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.activity.CallDetails;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.main.adapter.CallAdapter;
import com.tr.hsyn.telefonrehberi.xyz.call.main.listener.CallUpdateListener;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.main.MainConsts;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.val;
import timber.log.Timber;


/**
 * <h1>CallLoadedFragment</h1>
 * <p>
 * Bu sınıf arama kayıtları yüklendiğinde devreye girer.
 * Ve tüm kayıtlar üzerinde ekleme çıkarma güncelleme işlemlerini yönetir.
 */

public abstract class CallLogLoaded extends CallLogLoading implements ItemSelectListener, CallUpdateListener{
   
   /**
    * Kaydırma işlemini izleyecek olan nesne
    */
   @Getter(AccessLevel.PROTECTED) private SwipeCallBack callSwipeCallBack;
   
   private IntConsumer animationConsumer = i -> {
      
      recyclerView.setPopupBgColor(i);
      recyclerView.setThumbColor(i);
   };
   
   /**
    * Arama kayıtları
    */
   @Getter(AccessLevel.PROTECTED) private ObservableList<ICall> calls;
   
   /**
    * Arama kaydı sayısını tutacak
    */
   @Getter(AccessLevel.PROTECTED) private final Observe<Integer> callSize = new Observe<>(0);
   
   /**
    * Arama kayıtları için adapter
    */
   private final CallAdapter  callAdapter  = new CallAdapter(new ArrayList<>(), this);
   private final ListObserver listObserver = new ListObserver();
   
   @Override
   protected void onViewInflated(View view){
      
      super.onViewInflated(view);
      
      final long removeDuration = 500L;
      val        animator       = new SlideRightAlphaAnimator();
      animator.setRemoveDuration(removeDuration);
      animator.withInterpolator(new AccelerateInterpolator());
      
      int color = ColorController.getPrimaryColor().getValue();
      
      callSwipeCallBack = new SwipeCallBack(0, ItemTouchHelper.LEFT, view.getContext(), this);
      callSwipeCallBack.setBgColor(color);
      
      ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callSwipeCallBack);
      itemTouchHelper.attachToRecyclerView(recyclerView);
      
      recyclerView.setItemAnimator(animator);
      recyclerView.setAdapter(callAdapter);
      callSize.setObserver(this::onCallSizeChanged);
   }
   
   @Override
   protected void onCallsLoaded(List<ICall> calls, Throwable throwable){
      
      super.onCallsLoaded(calls, throwable);
      
      if(ALL_CALLS == getCurrentFilter()){
         
         if(null != calls){
            
            this.calls = new ObservableArrayList<>();
            this.calls.addAll(calls);
            this.calls.addOnListChangedCallback(listObserver);
            
            if(null == getCallConst().getCallStory()){
               
               //! Sadece tüm arama kayıtları yükleniyorsa 
               getCallConst().setCallStory(CallStory.createStory(this.calls));
            }
            else{
               
               getCallConst().getCallStory().setCalls(this.calls);
            }
            
            callAdapter.setCalls(this.calls);
            callSize.setValue(this.calls.size());
            whenCallsLoaded(this.calls);
         }
      }
   }
   
   protected abstract Kickback_CallsConst getCallConst();
   
   /**
    * Kayıtlar yüklendi ve görsellere aktarıldı
    */
   protected void whenCallsLoaded(List<ICall> calls){
      
      checkEmpty();
      setTitle();
      updateCachedNames();
   }
   
   private void updateCachedNames(){
      
      Worker.onBackground(() -> CallStory.updateCachedNames(calls, getContext(), this),
                          "CallLogLoaded:İsimsiz arama kayıtlarını güncelleme",
                          true);
   }
   
   /**
    * Kayıtların sayısı değiştiğinde.
    *
    * @param value Yeni sayı
    */
   private void onCallSizeChanged(Integer value){
      
      if(isShowTime() && null != getAdapter()){
         
         getSubTitle().setValue(value.toString());
      }
      
      checkEmpty();
   }
   
   /**
    * Listenin adapter'ı.
    *
    * @return CallAdapter
    */
   @Override
   protected final CallAdapter getAdapter(){
      
      return ((CallAdapter) recyclerView.getAdapter());
   }
   
   /**
    * Arama kaydı yoksa bunu bir görsel ile göster.
    */
   private void checkEmpty(){
      
      if(null == getAdapter()){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      else{
         
         if(0 == getAdapter().getItemCount()){
            
            emptyView.setVisibility(View.VISIBLE);
         }
         else{
            
            emptyView.setVisibility(View.GONE);
         }
      }
   }
   
   @Override
   public void onCallUpdated(int index, ICall call){
   
      if(getDatabaseOperator().update(call)){
   
         Logger.d("İsim güncellendi : %s", call);
      }
      else{
   
         Logger.w("İsim güncellenemedi : %s", call);
      }
      
      /*if(getCurrentFilter() == ALL_CALLS){
         
         calls.set(index, call);
      }
      else{
         
         getAdapterCalls().set(index, call);
      }*/
   }
   
   protected abstract ICallLog getDatabaseOperator();
   
   @Override
   public void onDetach(){
      
      super.onDetach();
      if(calls != null) calls.removeOnListChangedCallback(listObserver);
   }
   
   @Override
   public void onItemSelected(int position){
      
      if(null != getContext()){
         
         ICall call = getAdapterCalls().get(position);
         
         Kickback_CallDetailBox.getInstance().setCalls(new ArrayList<>(getAdapterCalls()));
         Intent intent = CallDetails.createIntent(getContext(), call);
         
         Worker.onMain(() -> {
            
            startActivity(intent);
            Bungee.slideLeft(getContext());
            
         }, MainConsts.DELAY_ACTIVITY_START);
      }
   }
   
   /**
    * Listede gösterilen kayıtları döndür.
    *
    * @return Kayıtlar
    */
   private List<ICall> getAdapterCalls(){
      
      return getAdapter().getCalls();
   }
   
   @Override
   public void onSwipe(int index){
      
      delete(index);
   }
   
   /**
    * Kaydı sil.
    * Tüm kayıtları içeren listenin içinden.
    * Zaten burada başka bir liste yok.
    *
    * @param index Silinecek kaydın index'i
    */
   private void delete(final int index){
      
      if(getContext() != null){
         
         if(ALL_CALLS == getCurrentFilter()){
            
            if(index >= calls.size() || 0 > index){
               
               Logger.w("Silinen arama kaydı bulunamadı : %d", index);
            }
            else{
               
               ICall call = calls.remove(index);
               
               if(CallStory.removeCall(getContext().getContentResolver(), call.getDate())){
                  
                  call.setDeleted();
                  Timber.d("Arama sistem kayıtlarından silindi : %s", call);
                  
                  EventBus.getDefault().post(new CallDeleted(call));
               }
               else{
                  
                  Logger.w("Arama sistem kayıtlarından silinemedi : %s", call);
               }
            }
         }
         else{
            
            val fCalls = getAdapter().getCalls();
            
            if(index >= fCalls.size() || 0 > index){
               
               Logger.w("Silinen arama kaydı bulunamadı : %d", index);
            }
            else{
               
               getAdapter().notifyItemRemoved(index);
               
               val remove = fCalls.remove(index);
               getCallConst().getCallStory().remove(remove);
               //remove.setDeletedDate(System.currentTimeMillis());
               
               callSize.setValue(fCalls.size());
               
               CallStory.removeCall(getContext().getContentResolver(), remove.getId());
               
               EventBus.getDefault().post(new CallDeleted(remove));
            }
         }
      }
   }
   
   /**
    * Renk değiştiğinde.
    *
    * @param event Renk bilgisi taşıyan nesne
    */
   @Subscribe
   public void onChangePrimaryColor(ColorChanged event){
      
      int newColor = event.getColor();
      ColorController.runColorAnimation(animationConsumer);
      
      callSwipeCallBack.setBgColor(newColor);
      refreshLayout.setColorSchemeColors(newColor);
      ColorController.setIndaterminateProgressColor(getContext(), progressBar);
   }
   
   /**
    * Diğer activity ve fragment'larda bir kayıt silindiğinde.
    *
    * @param event Silinen kaydı tutan nesne
    */
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onCallDelete(DeleteCall event){
      
      if(null == getContext()){
         
         Logger.w("Context yok");
      }
      else{
         
         ICall call  = event.getPhoneCall();
         int   index = getAdapterCalls().indexOf(call);
         
         if(index != -1){
            
            final long delay = 400L;
            Worker.onMain(() -> delete(index), delay);
         }
         else{
            //Silinen kayıt gösterimde olan bir listede (adapter'da) olmayabilir
            //Tüm aramalar içinden sil
            if(getCallConst().getCallStory().remove(call)){
               
               CallStory.removeCall(getContext().getContentResolver(), call.getId());
               EventBus.getDefault().post(new CallDeleted(call));
            }
         }
      }
   }
   
   /**
    * Yeni bir arama gerçekleşmiş.
    *
    * @param newCall Yeni arama kaydı
    */
   @Subscribe(threadMode = ThreadMode.MAIN)
   public void onNewCall(NewCall newCall){
      
      calls.add(0, newCall.getCall());
   }
   
   @Subscribe
   public void onDeleteSearchCall(EventDeleteSearchCall event){
      
      delete(getAdapterCalls().indexOf(event.getCall()));
   }
   
   /**
    * Arama kaydını tarih sırasına göre uygun yere yerleştir.
    *
    * @param call Kayıt
    * @return Yerleştirilen index
    */
   private int insertCall(ICall call){
      
      List<ICall> calls = getAdapterCalls();
      
      for(int i = 0; i < calls.size(); i++){
         
         if(call.getDate() >= calls.get(i).getDate()){
            
            calls.add(i, call);
            return i;
         }
      }
      
      calls.add(call);
      return calls.size() - 1;
      
   }
   
   private class ListObserver extends ObservableList.OnListChangedCallback<ObservableList<ICall>>{
      
      
      @Override
      public void onChanged(ObservableList<ICall> sender){
         
         
      }
      
      @Override
      public void onItemRangeChanged(ObservableList<ICall> sender, int positionStart, int itemCount){
         
         if(ALL_CALLS == getCurrentFilter())
            getAdapter().notifyItemRangeChanged(positionStart, itemCount);
      }
      
      @Override
      public void onItemRangeInserted(ObservableList<ICall> sender, int positionStart, int itemCount){
         
         if(ALL_CALLS == getCurrentFilter()){
            
            getAdapter().notifyItemRangeInserted(positionStart, itemCount);
            callSize.setValue(getAdapterCalls().size());
         }
      }
      
      @Override
      public void onItemRangeMoved(ObservableList<ICall> sender, int fromPosition, int toPosition, int itemCount){
         
      }
      
      @Override
      public void onItemRangeRemoved(ObservableList<ICall> sender, int positionStart, int itemCount){
         
         if(ALL_CALLS == getCurrentFilter()){
            
            getAdapter().notifyItemRangeRemoved(positionStart, itemCount);
            callSize.setValue(getAdapterCalls().size());
         }
      }
   }
   
   /**
    * Tüm arama kayıtlarını silmek için dialoğu başlat.
    *
    * @param phoneCalls Tüm kayıtlar
    */
   @SuppressLint("ResourceType")
   void deleteCalls(List<ICall> phoneCalls){
      
      if(null == getActivity()) return;
      
      Dialog.confirm(
            getActivity(),
            u.format("%d Arama Kaydı silinecek?", phoneCalls.size()),
            "Sil",
            (di, w) -> {
               
               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
               
               @SuppressLint("InflateParams")
               View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_all_calls, null, false);
               
               TextView    totalText     = view.findViewById(R.id.progressTextTotal);
               TextView    progressText  = view.findViewById(R.id.progressText);
               TextView    deletedNumber = view.findViewById(R.id.deletedNumber);
               Button      cancel        = view.findViewById(R.id.cancel);
               ProgressBar progressBar   = view.findViewById(R.id.progress);
               
               totalText.setText(u.format("/%d", phoneCalls.size()));
               progressText.setText("0");
               progressBar.setMax(phoneCalls.size());
               progressBar.setProgress(0);
               
               
               builder.setCancelable(false);
               
               builder.setView(view);
               AlertDialog alertDialog = builder.create();
               //noinspection ConstantConditions
               alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
               
               alertDialog.show();
               
               progressBar.setIndeterminate(false);
               
               AtomicBoolean isCancel = new AtomicBoolean(false);
               
               cancel.setOnClickListener((v) -> isCancel.set(true));
               
               val coptList = new ArrayList<>(phoneCalls);
               phoneCalls.clear();
               
               int size = coptList.size();
               
               if(getContext() == null){
                  
                  Logger.w("Context yok");
                  return;
               }
               
               final ContentResolver contentResolver = getContext().getContentResolver();
               
               Worker.onBackground(() -> {
                  
                  List<ICall> notDeletedPhoneCalls = new ArrayList<>();
                  List<ICall> deletedPhoneCalls    = new ArrayList<>();
                  
                  AtomicInteger atomicInteger = new AtomicInteger(1);
                  
                  while(!coptList.isEmpty()){
                     
                     if(isCancel.get()){ break; }
                     
                     val phoneCall = coptList.remove(0);
                     
                     deletedNumber.post(() -> deletedNumber.setText(phoneCall.getNumber()));
                     progressText.post(() -> progressText.setText(String.valueOf(atomicInteger.get())));
                     progressBar.post(() -> progressBar.setProgress(atomicInteger.get()));
                     atomicInteger.incrementAndGet();
                     
                     if(CallStory.removeCall(contentResolver, phoneCall.getId())){
                        
                        deletedPhoneCalls.add(phoneCall);
                        EventBus.getDefault().post(new CallDeleted(phoneCall));
                     }
                     else{
                        
                        notDeletedPhoneCalls.add(phoneCall);
                     }
                  }
                  
                  StringBuilder stringBuilder = new StringBuilder();
                  
                  if(!notDeletedPhoneCalls.isEmpty()){
                     
                     stringBuilder.append(u.format("Silinemeyen kayıt sayısı : %d%n", notDeletedPhoneCalls.size()));
                  }
                  else{
                     
                     stringBuilder.append("Silinemeyen kayıt olmadı\n");
                  }
                  
                  
                  if(deletedPhoneCalls.size() == size){
                     
                     stringBuilder.append(u.format("%d kayıt silindi", size));
                  }
                  else{
                     
                     stringBuilder.append(u.format("%d kayıttan %d tanesi silindi", size, deletedPhoneCalls.size()));
                  }
                  
                  alertDialog.cancel();
                  
                  Worker.onMain(() -> {
                     
                     loadCalls();
                     
                     Dialog.alert(getActivity(), stringBuilder.toString());
                  });
               }, "CallLoadedFragment:Tüm arama kayıtlarını silme");
            }
      );
   }
   
   /**
    * Uygulamanın veri tabanı silinmek istendiğinde onay iste.
    */
   void askDeleteDatabase(){
      
      Dialog.confirm(
            getActivity(),
            "Uygulamanın arama kayıtları veri tabanı silinecek?",
            "Sil",
            (di, w) -> onDeletedDBFile(new CallLogDB(getContext()).removeDbFile()));
   }
   
   /**
    * Uygulamanın veri tabanı silindiğinde.
    *
    * @param isDeleted Silinmiş ise {@code true}
    */
   private void onDeletedDBFile(boolean isDeleted){
      
      if(isDeleted){
         
         Show.globalMessage(getActivity(), "Veritabanı dosyası silindi");
      }
      else{
         
         Show.globalMessage(getActivity(), "Veritabanı dosyası silinemedi");
      }
      
      super.loadCalls();
   }
   
}
