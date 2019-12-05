package com.tr.hsyn.telefonrehberi.xyz.contact.activity.stored;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.orhanobut.logger.Logger;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tr.hsyn.telefonrehberi.BuildConfig;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.PerfectSort;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.Toolbarx;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.CloseRequest;
import com.tr.hsyn.telefonrehberi.util.event.EventRefreshContacts;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Message;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.cloud.Kickback_AccountBox;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.Contact;
import com.tr.hsyn.telefonrehberi.xyz.contact.story.ContactStory;
import com.tr.hsyn.telefonrehberi.xyz.main.listener.ItemSelectListener;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.val;


@SuppressWarnings("AnonymousInnerClassMayBeStatic")
public class ContactsActivity extends AppCompatActivity implements ItemSelectListener{
   
   @BindView(R.id.toolbar)       Toolbar                toolbar;
   @BindView(R.id.recycler_view) FastScrollRecyclerView recycler_view;
   @BindView(R.id.progress)      ProgressBar            progress;
   @BindView(R.id.empty)         View                   empty;
   private                       ContactStory           contactStory;
   private                       List<Contact>          contacts;
   
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.activity_stored_contacts);
      
      ButterKnife.bind(this);
      contactStory = ContactStory.create(this);
      
      setupViews();
   }
   
   void setupViews(){
      
      Toolbarx.setToolbar(this, toolbar);
      int color = u.getPrimaryColor(this);
      recycler_view.setThumbColor(color);
      recycler_view.setPopupBgColor(color);
      
      progress.setVisibility(View.VISIBLE);
      
      prepareContacts();
   }
   
   void prepareContacts(){
      
      Worker.onBackground(() -> contactStory.get(), "StoredContactsActivity:Kayıtlı kişileri alma")
            .thenApply(ContactsActivity::sortContacts)
            .whenCompleteAsync(this::onContactsLoaded, Worker.getMainThreadExecutor());
   }
   
   private void onContactsLoaded(List<Contact> contacts, Throwable throwable){
      
      progress.setVisibility(View.GONE);
      this.contacts = contacts;
      
      checkEmpty();
      
      if(contacts == null){
         
         if(throwable != null){
            
            Show.globalMessage(this, "Kayıtlı kişiler alınamadı : " + throwable.getMessage());
         }
         else{
            
            Show.globalMessage(this, "Kayıtlı kişiler alınamadı");
         }
         
         return;
      }
      
      setCount(contacts.size());
      
      recycler_view.setAdapter(new StoredContactsAdapter(contacts, this));
   }
   
   private void checkEmpty(){
      
      if(contacts == null || contacts.size() == 0){
         
         empty.setVisibility(View.VISIBLE);
         setCount(0);
      }
      else{
         
         empty.setVisibility(View.GONE);
      }
   }
   
   private void setCount(int count){
      
      //noinspection ConstantConditions
      getSupportActionBar().setSubtitle(String.valueOf(count));
   }
   
   private static List<Contact> sortContacts(List<Contact> contacts){
      
      Collections.sort(contacts, PerfectSort.comparator(Contact::getName));
      return contacts;
   }
   
   @Override
   protected void onDestroy(){
      
      contactStory.close();
      contactStory = null;
      toolbar      = null;
      super.onDestroy();
   }
   
   @Override
   public void onItemSelected(int position){
      
      val contact = contacts.get(position);
      Logger.d("Selected : %s%n%s", contact.toString(), contact.getDescription());
      
      createDialog(position);
   }
   
   private void createDialog(int index){
      
      ContactInfoActivityDialog.show(this, getView(index), null);
      
   }
   
   private View getView(int index){
      
      val contact = contacts.get(index);
      
      @SuppressLint("InflateParams")
      View view = getLayoutInflater().inflate(R.layout.stored_contacts_dialog, null, false);
      
      TextView title        = view.findViewById(R.id.title);
      TextView number       = view.findViewById(R.id.number);
      TextView save_date    = view.findViewById(R.id.save_date);
      TextView look_count   = view.findViewById(R.id.look_count);
      TextView look_date    = view.findViewById(R.id.look_date);
      TextView updated_date = view.findViewById(R.id.updated_date);
      TextView description  = view.findViewById(R.id.description);
      TextView deleted_date = view.findViewById(R.id.deleted_date);
      
      
      title.setText(contact.getName());
      number.setText(contact.getNumber());
      save_date.setText(getDate(contact.getSavedDate()));
      look_count.setText(String.valueOf(contact.getLookCount()));
      look_date.setText(getDate(contact.getLastLookDate()));
      updated_date.setText(getDate(contact.getUpdatedDate()));
      description.setText(contact.getDescription());
      deleted_date.setText(getDate(contact.getDeletedDate()));
      
      if(contact.getDeletedDate() != 0L){
         
         View reload = view.findViewById(R.id.reload);
         reload.setVisibility(View.VISIBLE);
         
         reload.setOnClickListener(v -> {
            
            reload.setEnabled(false);
            EventBus.getDefault().post(new CloseRequest(){});
            reload(index);
         });
      }
      
      if(BuildConfig.DEBUG){
         
         view.findViewById(R.id.delete).setOnClickListener(v -> {
            
            v.setEnabled(false);
            onClickDelete(index);
         });
      }
      else{
         
         view.findViewById(R.id.delete).setVisibility(View.GONE);
      }
      
      return view;
   }
   
   private static String getDate(long date){
      
      return date != 0L ? Time.getShortDateShortTime(date) : "-";
   }
   
   private void onClickDelete(int index){
      
      EventBus.getDefault().post(new CloseRequest(){});
      
      delete(index);
   }
   
   private void delete(int index){
      
      val contact = contacts.get(index);
      val key     = contact.getNumber();
      
      if(contactStory.delete(key)){
         
         contacts.remove(index);
         //noinspection ConstantConditions
         recycler_view.getAdapter().notifyItemRemoved(index);
         checkEmpty();
      }
      else{
         
         Logger.w("Kişi silinemedi : %s", key);
      }
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      Bungee.inAndOut(this);
   }
   
   private void reload(int index){
      
      val contact = contacts.get(index);
      
      Worker.onBackground(() -> {
   
         Account account = Kickback_AccountBox.getInstance().getProperAccount();
         
         boolean isAdded = Contacts.addContact(this, contact.getName(), contact.getNumber(), account);
   
         if(isAdded){
      
            contact.setDeletedDate(0L);
      
            if(contactStory.update(contact)){
         
               Logger.d("Kişi geri yüklendi : %s", contact);
         
               Worker.onMain(() -> reloaded(index));
            }
            else{
         
               Logger.w("Kişi geri yüklenemedi : %s", contact);
            }
         }
         else{
      
            Logger.w("Kişi sistem kayıtlarına eklenemedi : %s", contact);
         }
      });
   }
   
   private void reloaded(int index){
      
      //noinspection ConstantConditions
      recycler_view.getAdapter().notifyItemChanged(index);
      val contact = contacts.get(index);
      
      final long duration = 300L;
      
      Message.builder()
             .message("%s geri yüklendi", contact.getName())
             .delay(duration)
             .build()
             .showOn(this);
      
      EventBus.getDefault().post(new EventRefreshContacts());
   }
}
