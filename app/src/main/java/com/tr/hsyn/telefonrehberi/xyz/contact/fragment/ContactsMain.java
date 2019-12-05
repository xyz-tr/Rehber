package com.tr.hsyn.telefonrehberi.xyz.contact.fragment;


import android.net.Uri;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;

import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.PerfectSort;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.ContactAdded;
import com.tr.hsyn.telefonrehberi.util.event.ContactDeleted;
import com.tr.hsyn.telefonrehberi.util.event.EventUpdatedContact;
import com.tr.hsyn.telefonrehberi.util.observable.Observe;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.swipe.ContactSwipeCallBack;
import com.tr.hsyn.telefonrehberi.util.swipe.SwipeListener;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.IMainContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.INumber;
import com.tr.hsyn.telefonrehberi.xyz.contact.IPhoneContact;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail.ContactDetailsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_ContactBox;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

//TODO Seçilebilir kişi listesi

/**
 * <h1>ContactsFragmentMain</h1>
 *
 * @author hsyn 2019-11-01 13:08:50
 */
public abstract class ContactsMain extends ContactsAccount implements SwipeListener, ItemSelectListener{
   
   /**
    * Kişiler
    */
   private List<IMainContact> contacts;
   
   /**
    * Rehberin yüklenmeye başladığı zaman
    */
   @Setter(AccessLevel.PROTECTED) private long loadStartTime;
   
   /**
    * Rehberdeki kişi sayısını takip edecek
    */
   private Observe<Integer> contactSize = new Observe<>(0);
   
   /**
    * Listede bir kişi üzerinde soldan sağa kaydırma yapıldığında haberdar olmak için
    */
   @Getter(AccessLevel.PROTECTED) private ContactSwipeCallBack contactSwipeCallBack;
   
   /**
    * Kişinin detay sayfası açılırken ripple efekti için küçük bir geçikme süresi var.
    * Bu süre içinde kişiye tekrar dokunulursa iki veya daha fazla detay sayfası açılır.
    * Ancak bu değişken sayfanın yanlızca bir kez açılmasını sağlayacak.
    */
   private boolean detailsOpen;
   
   /**
    * Kişiler alındığında buraya düşecek
    *
    * @param contacts  Kişiler
    * @param throwable hata yoksa {@code null}
    */
   @Override
   protected void onContactsLoaded(final List<IMainContact> contacts, Throwable throwable){
      
      super.onContactsLoaded(contacts, throwable);
      Kickback_ContactBox.getInstance().setContacts(contacts);
      
      if(contacts != null){
         
         this.contacts = contacts;
         recyclerView.setAdapter(new ContactAdapter(this.contacts, this));
         //noinspection ConstantConditions
         recyclerView.getAdapter().notifyDataSetChanged();
         setTitle();
         checkEmpty();
         whenContactsLoaded(contacts);
      }
      else{
         
         if(throwable != null){
            
            Logger.w("Rehber kayıtları alınamadı : %s", throwable.getMessage());
         }
         else{
            
            Logger.w("Hata olmamasına rağmen rehber kayıtları alınamadı");
         }
      }
      
      Logger.w("Contacts Load Time : %dms [size=%d]", System.currentTimeMillis() - loadStartTime, contacts != null ? contacts.size() : -1);
   }
   
   /**
    * Rehberde kimse var mı yokmu kontrol et.
    */
   private void checkEmpty(){
      
      if(contacts == null || contacts.size() == 0){
         
         emptyView.setVisibility(View.VISIBLE);
      }
      else{
         
         emptyView.setVisibility(View.GONE);
      }
   }
   
   /**
    * Kişiler yüklenip görsellere aktarıldıktan sonra.
    *
    * @param contacts Kişiler
    */
   protected void whenContactsLoaded(List<IMainContact> contacts){
      
      setLastScrollPosition();
   }
   
   private void setLastScrollPosition(){
      
      recyclerView.scrollToPosition(getLastlistPosition());
   }
   
   @Override
   protected void onViewInflated(View view){
      
      super.onViewInflated(view);
      contactSwipeCallBack = new ContactSwipeCallBack(0, ItemTouchHelper.RIGHT, view.getContext(), this);
      contactSwipeCallBack.setBgColor(u.getPrimaryColor(view.getContext()));
      ItemTouchHelper itemTouchHelper = new ItemTouchHelper(contactSwipeCallBack);
      itemTouchHelper.attachToRecyclerView(recyclerView);
      contactSize.setObserver(i -> getSubTitle().setValue(i.toString()));
   }
   
   @Override
   public void onSwipe(int index){
      
      getAdapter().notifyItemChanged(index);
      
      assert getAdapterContacts() != null;
      IMainContact contact = getAdapterContacts().get(index);
      
      if(getContext() == null || contact == null){ return; }
      
      if(!Contacts.hasNumber(getContext(), contact.getContactId())){
         
         Show.globalMessage(getActivity(), getString(R.string.no_number));
         return;
      }
      
      List<INumber> numbers = Contacts.getNumbers(getContext().getContentResolver(), contact.getContactId());
      
      if(numbers == null){
         
         Show.globalMessage(getActivity(), getString(R.string.no_number));
         return;
      }
      
      
      for(INumber number : numbers){
         
         if(number.isPrimary()){
            
            Phone.requestCall(number.getNumber());
            return;
         }
      }
      
      Phone.requestCall(numbers.get(0).getNumber());
   }
   
   /**
    * Adapter'daki kişi listesi
    *
    * @return Kişi listesi
    */
   @Override
   protected List<IMainContact> getAdapterContacts(){
      
      if(getAdapter() == null) return null;
      
      return getAdapter().getContacts();
   }
   
   /**
    * Rehberin listesini tutan recyclerView'ın adapter'ı.
    *
    * @return ContactAdapter
    */
   protected ContactAdapter getAdapter(){
      
      return (ContactAdapter) recyclerView.getAdapter();
   }
   
   @Override
   public void onResume(){
      
      super.onResume();
      
      detailsOpen = false;
   }
   
   @Override
   public void onItemSelected(int position){
      
      if(detailsOpen) return;
      
      IMainContact contact = getAdapterContacts().get(position);
      
      if(getContext() == null) return;
      
      detailsOpen = true;
      
      Worker.onMain(() -> ContactDetailsActivity.startActivity(getContext(), contact.getContactId()), 50L);
   }
   
   @Subscribe
   public void onContactUpdated(EventUpdatedContact event){
      
      IMainContact contact = IMainContact.create(event.getUpdatedContact());
      
      Logger.w("Güncellenen kişi : %s", contact);
      
      int index = getIndex(contact.getContactId());
      
      if(index == -1){
         
         Logger.w("Güncellenen kişi listede bulunmuyor");
         return;
      }
      
      contacts.set(index, contact);
      getAdapter().notifyItemChanged(index);
      whenContactUpdated(event);
   }
   
   protected abstract void whenContactUpdated(EventUpdatedContact event);
   
   private int getIndex(String id){
      
      for(int i = 0; i < contacts.size(); i++){
         
         val contact = contacts.get(i);
         
         if(contact.equals(id)) return i;
      }
      
      return -1;
   }
   
   @Subscribe
   public void onContactAdded(ContactAdded event){
      
      try{
         
         Uri uri = event.getUri();
         Logger.d(uri);
         
         IMainContact newContact = Contacts.getContact(getContext(), uri);
         
         if(newContact == null){
            
            Logger.w("new contact = null");
            return;
         }
         
         
         Logger.w("New Contact : %s", newContact);
         
         if(contacts.isEmpty()){
            
            contacts.add(newContact);
            getAdapter().notifyItemInserted(0);
         }
         else{
            
            boolean added = false;
            for(int i = 0; i < contacts.size(); i++){
               
               IMainContact contact = contacts.get(i);
               
               String name  = newContact.getName();
               String name2 = contact.getName();
               
               if(name == null || name.trim().isEmpty()){ name = "0"; }
               if(name2 == null || name2.trim().isEmpty()){ name2 = "0"; }
               
               if(PerfectSort.isSmall(name, name2)){
                  
                  contacts.add(i, newContact);
                  getAdapter().notifyItemInserted(i);
                  added = true;
                  showTime(isShowTime());
                  break;
               }
            }
            
            if(!added){
               
               contacts.add(newContact);
               getAdapter().notifyItemInserted(contacts.size() - 1);
            }
         }
         
         whenNewContactAdded(newContact);
      }
      catch(Exception e){
         
         Logger.w("Yeni kişi alınamadı : %s", e.toString());
      }
   }
   
   protected abstract void whenNewContactAdded(IMainContact newContact);
   
   @Subscribe
   public void onContactDeleted(ContactDeleted event){
      
      if(getContext() == null) return;
      
      IPhoneContact contact = event.getContact();
      
      int index = contacts.indexOf(contact);
      
      if(index != -1){
         
         contacts.remove(index);
         getAdapter().notifyItemRemoved(index);
         
         whenContactDeleted(contact);
         
         showTime(isShowTime());
         
         Toast.makeText(getContext(), getString(R.string.contact_deleted, event.getContact().getName()), Toast.LENGTH_SHORT).show();
      }
   }
   
   protected abstract void whenContactDeleted(IPhoneContact contact);
   
   
}
