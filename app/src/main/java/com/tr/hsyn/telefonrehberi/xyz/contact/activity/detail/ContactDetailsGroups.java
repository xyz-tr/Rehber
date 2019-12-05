package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Message;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.contact.Account;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.Group;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.GroupActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.activity.group.LabeledContactsActivity;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.GroupOptionsAdapter;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.SelectAccountAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lombok.val;
import timber.log.Timber;


public abstract class ContactDetailsGroups extends ContactDetailsEmail{
   
   /**
    * Seçilen grup
    */
   private                Group   selectedGroup;
   /**
    * Seçilen hesap
    */
   private                Account selectedAccountForGroup;
   /**
    * Kişinin ait olduğu bir grubun zil sesi değiştirilmek istendiğinde kullanılacak olan {@code requestCode}.
    */
   protected static final int     REQUEST_CODE_NEW_GROUP_RINGTONE = 2;
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      //Eğer bir google hesabı yoksa grup olayına girmeyelim
      if(getProperAccount() != null){
         
         setupGroups();
      }
      else{
         
         //container'a margin verildiği için grup olayı yoksa bu view'ı tamamen kaldırmak gerekiyor
         findViewById(R.id.labels_container).setVisibility(View.GONE);
      }
   }
   
   /**
    * Verilen isimde bir grup olup olamadığını öğren.
    *
    * @param context Context
    * @param name    İsim
    * @param account Hesap. İsim bu hesabı dikkate alacak sadece.
    *                Farklı hesaplarda aynı grup ismi olmasında bir sakınca yok.
    * @return Verilen isimde bir grup varsa {@code true}
    */
   public static boolean groupNameExist(Context context, String name, Account account){
      
      List<String> titles = Contacts.getGroupTitles(context, account);
      
      for(String t : titles){
         
         if(GroupActivity.getTranslatedGroupName(context, t).equals(name)){
            return true;
         }
      }
      
      return false;
   }
   
   /**
    * Kişinin üyesi olduğu grupların bilgisi alınıyor.
    * Ve bilgiler ilgili görsellere aktarılıyor.
    */
   private void setupGroups(){
      
      final List<String> groupIdList = Contacts.getGroupIdList(this, contact.getContactId(), getProperAccount().name);
      
      if(groupIdList == null){
         
         Timber.w("Group bilgileri alınamadı : %s", contact.getName());
         return;
      }
      
      final List<Group> groups = Contacts.getGroupList(this, groupIdList, getProperAccount());
      
      if(groups == null){
         
         Timber.w("Grup(lar) alınamadı : %s", groupIdList);
         return;
      }
      
      //Bir kişi en az bir grubun üyesidir (Kişilerim)
      
      setGroupsTitle(groups);
      
      if(groups.size() != 0){ setGroupLabels(groups); }
   }
   
   /**
    * Yeni grubun bağlı olacağı hesabı seçmek için dialoğu başlatır.
    * Bu dialog birden fazla hesap varsa çalışacak.
    * Tek hesap varsa direk o hesabı seçecek.
    *
    * @param accounts Kişiye ait tüm hesaplar
    */
   private void selectAccountForNewGroup(List<Account> accounts){
      
      AlertDialog                         dialog         = new AlertDialog.Builder(this).create();
      @SuppressLint("InflateParams") 
      View accountsLayout = getLayoutInflater().inflate(R.layout.group_select_account, null, false);
      
      dialog.setView(accountsLayout);
      
      RecyclerView recyclerView = accountsLayout.findViewById(R.id.recyclerViewAccounts);
      //recyclerView.setLayoutManager(new LinearLayoutManager(this));
      
      SelectAccountAdapter adapter = new SelectAccountAdapter(Stream.of(accounts).map(a -> a.name).toList());
      recyclerView.setAdapter(adapter);
      
      adapter.setClickListener((i) -> {
         
         selectedAccountForGroup = accounts.get(i);
         
         Worker.onMain(() -> {
            
            dialog.dismiss();
            getNewGroupName("Yeni Grup İsmi", this::createNewGroup);
            
         }, 300);
         
      });
      
      //noinspection ConstantConditions
      u.keepGoing(() -> dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationShrinkRight);
      
      dialog.show();
      
   }
   
   /**
    * Kullanıcı kişinin ait olduğu gruplardan birine dukunmuştur.
    * Burada seçilen grup tüm üyeleri ile gösterilmek üzere {@link LabeledContactsActivity} başlatılır.
    *
    * @param clickedLabel Seçilen hesabın görseli. Bu nesne seçilen grup bilgisini taşıyor.
    */
   private void labelClickHandler(View clickedLabel){
      
      Parcelable label = (Group) clickedLabel.getTag();
      
      Intent i = new Intent(this, LabeledContactsActivity.class);
      i.putExtra(LabeledContactsActivity.EXTRA_LABEL, label);
      
      Worker.onMain(() -> {
         
         startActivity(i);
         Bungee.slideUp(this);
      }, 200);
   }
   
   /**
    * Kullanıcı kişinin ait olduğu gruplardan birine uzun dokunmuştur.
    * Bu hareket kişiye o grupla ilgili yapılabilecek işlemleri göstermek üzere bir dialog başlatır.
    *
    * @param view Uzun dokunulan görsel
    * @return {@code true} dokunma hareketinin işlendiği anlamına geliyor.
    */
   private boolean onLongClickLabel(View view){
      
      Group label = (Group) view.getTag();
      
      String ringtoneName;
      
      if(label.getRingTone() == null){
         
         ringtoneName = Contacts.getDefaultRingTone(this);
      }
      else{
         
         ringtoneName = Contacts.getRingtoneName(this, label.getRingTone());
      }
      
      if(ringtoneName == null){ ringtoneName = "Yok"; }
      
      
      String[] options = {
            
            getString(R.string.hide),
            getString(R.string.delete),
            getString(R.string.send_message),
            getString(R.string.assign_ringtone, ringtoneName),
            getString(R.string.rename)
      };
      
      
      AlertDialog.Builder                 dialog        = new AlertDialog.Builder(this);
      @SuppressLint("InflateParams") View optionsLayout = getLayoutInflater().inflate(R.layout.group_options, null, false);
      
      dialog.setCancelable(true);
      dialog.setView(optionsLayout);
      
      List<Integer> disabledItems;
      
      if(label.isReadOnly()){
         
         disabledItems = Arrays.asList(1, 4);
      }
      else{
         
         disabledItems = Collections.emptyList();
      }
      
      
      GroupOptionsAdapter adapter      = new GroupOptionsAdapter(Arrays.asList(options), disabledItems);
      RecyclerView        recyclerView = optionsLayout.findViewById(R.id.recyclerViewGroup);
      
      recyclerView.setLayoutManager(new LinearLayoutManager(this));
      recyclerView.setAdapter(adapter);
      
      
      AlertDialog dialog1 = dialog.create();
      
      assert dialog1.getWindow() != null;
      assert dialog1.getWindow().getAttributes() != null;
      
      u.keepGoing(() -> dialog1.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationShrink);
      dialog1.show();
      
      
      TextView  title = optionsLayout.findViewById(R.id.groupTitle);
      ImageView icon  = optionsLayout.findViewById(R.id.groupIcon);
      
      title.setText(GroupActivity.getTranslatedGroupName(this, label.getTitle()));
      icon.setImageDrawable(getGroupIconDrawable(title.getText().toString()));
      
      adapter.setClickListener((position) -> {
         
         switch(position){
            
            case 0:
               
               if(setGroupHidden(label)){
                  
                  ViewGroup labelLayout = findViewById(R.id.labelLayout);
                  
                  if(labelLayout == null){
                     
                     Timber.w("labelLayout bulunamadı");
                     return;
                  }
                  
                  labelLayout.removeView(view);
               }
               
               break;
            
            case 1:
               
               if(label.isReadOnly()) return;
               
               deleteGroup(label, view);
               break;
            
            case 2:
               
               GroupActivity.sendMessageToGroup(this, label);
               break;
            
            case 3:
               
               selectedGroup = label;
               openSystemRingToneDialog(REQUEST_CODE_NEW_GROUP_RINGTONE);
               break;
            
            case 4:
               
               if(label.isReadOnly()) return;
               renameGroup(view, label);
               break;
         }
         
         dialog1.dismiss();
      });
      
      return true;
   }
   
   /**
    * Grubu yeniden adlandır.
    *
    * @param v     View
    * @param group Grup
    */
   private void renameGroup(View v, Group group){
      
      Account account = Contacts.getGroupAccount(this, group.getId());
      
      if(account == null){
         
         Timber.w("Hesap bilgisi alınamadı");
         return;
      }
      
      getNewGroupName("Yeni Grup İsmi", (newName) -> {
         
         if(groupNameExist(this, newName, account)){
            
            Message.builder()
                   .type(Show.WARN)
                   .duration(2000L)
                   .message("Aynı isimde bir grup zaten var")
                   .build()
                   .showOn(this);
            
            return;
         }
         
         if(Contacts.updateGroupName(this, group.getId(), newName)){
            
            TextView name = v.findViewById(R.id.labelIcon);
            TransitionManager.beginDelayedTransition(findViewById(R.id.labelLayout));
            name.setText(newName);
            
            group.setTitle(newName);
            
            Message.builder()
                   .message("Grup güncellendi")
                   .build()
                   .showOn(this);
            
         }
         else{
            
            Message.builder()
                   .message("Grup güncellenemedi")
                   .type(Show.ERROR)
                   .build()
                   .showOn(this);
         }
         
      });
      
   }
   
   /**
    * Kullanıcıdan yeni grup ismi al.
    *
    * @param dialogTitle Dialog başlığı
    * @param callBack    İsim alındığında çağrılacak
    */
   private void getNewGroupName(@SuppressWarnings("SameParameterValue") CharSequence dialogTitle, GetNameCallBack callBack){
      
      AlertDialog.Builder                 dialog     = new AlertDialog.Builder(this);
      @SuppressLint("InflateParams") View nameLayout = getLayoutInflater().inflate(R.layout.get_string_layout, null, false);
      
      dialog.setView(nameLayout);
      dialog.setCancelable(true);
      
      EditText editText = nameLayout.findViewById(R.id.edittext);
      View     okey     = nameLayout.findViewById(R.id.okey);
      TextView title    = nameLayout.findViewById(R.id.title);
      title.setText(dialogTitle);
      
      AlertDialog dialog2 = dialog.create();
      //noinspection ConstantConditions
      dialog2.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationShrinkUp;
      dialog2.show();
      
      okey.setOnClickListener((v) -> {
         
         dialog2.dismiss();
         
         String name = editText.getText().toString();
         
         name = name.trim();
         
         if(name.isEmpty()){ return; }
         
         if(callBack != null){ callBack.run(name); }
      });
   }
   
   /**
    * Belirtilen grubu sil.
    * Silme işleminden sonra grubun görsel elemanı da ekrandan kaldırılacak.
    *
    * @param group Grup
    * @param v     Silinecek olan grubun gösterildiği görsel eleman
    */
   private void deleteGroup(Group group, View v){
      
      if(Contacts.deleteGroup(this, group.getId())){
         
         ViewGroup viewGroup = findViewById(R.id.labelLayout);
         
         ViewCompat
               .animate(v)
               .translationZ(-400F)
               .translationX(600F)
               .scaleY(0.5F)
               .setDuration(400)
               .alpha(0F).withEndAction(() -> viewGroup.removeView(v)).start();
         
         Message.builder()
                .message(getString(R.string.deleted, group.getTitle()))
                .build()
                .showOn(this);
         
      }
      else{
         
         Message.builder()
                .message(getString(R.string.not_deleted, group.getTitle()))
                .build()
                .showOn(this);
      }
   }
   
   /**
    * Belirtilen grubu gizle.
    *
    * @param group Grup
    * @return İşlem başarılı ise {@code true}
    */
   private boolean setGroupHidden(Group group){
      
      if(Contacts.setGroupVisibility(this, group.getId(), "0")){
         
         group.setVisable(false);
         
         Message.builder()
                .message(getString(R.string.msg_group_invisable, GroupActivity.getTranslatedGroupName(this, group.getTitle())))
                .build()
                .showOn(this);
         
         return true;
      }
      
      return false;
   }
   
   /**
    * Grubun icon'unu almak için.
    *
    * @param groupTitle Icon'u alınacak grubun ismi
    * @return Icon
    */
   private Drawable getGroupIconDrawable(String groupTitle){
      
      if(groupTitle.equals(getString(R.string.contact_group_my_contacts))){
         return ContextCompat.getDrawable(this, R.drawable.groups_mycontacts);
      }
      else if(groupTitle.equals(getString(R.string.contact_group_family))){
         return ContextCompat.getDrawable(this, R.drawable.group_family);
      }
      else if(groupTitle.equals(getString(R.string.contact_group_coworkers))){
         return ContextCompat.getDrawable(this, R.drawable.groups_coworkers);
      }
      else if(groupTitle.equals(getString(R.string.contact_group_friends))){
         return ContextCompat.getDrawable(this, R.drawable.group_friends);
      }
      else if(groupTitle.equals(getString(R.string.contact_group_started))){
         return ContextCompat.getDrawable(this, R.drawable.groups_favorite);
      }
      else{ return ContextCompat.getDrawable(this, R.drawable.groups_else); }
   }
   
   /**
    * Oluşturulan yeni grubu görsele ekler.
    *
    * @param label Yeni Grup
    */
   private void addGroup(Group label){
      
      final ViewGroup labelLayout = findViewById(R.id.labelLayout);
      
      if(labelLayout == null){ return; }
      
      final LayoutInflater layoutInflater    = getLayoutInflater();
      final View           labelTextViewCard = layoutInflater.inflate(R.layout.label_textview, labelLayout, false);
      final TextView       labelTextView     = labelTextViewCard.findViewById(R.id.labelIcon);
      
      labelTextView.setText(label.getTitle());
      
      final GradientDrawable drawable = (GradientDrawable) labelTextView.getBackground();
      drawable.setStroke(2, u.lighter(u.getPrimaryColor(this), .5f));
      
      TransitionManager.beginDelayedTransition(labelLayout);
      labelLayout.addView(labelTextViewCard);
      
      labelTextViewCard.setOnClickListener(this::labelClickHandler);
      labelTextViewCard.setOnLongClickListener(this::onLongClickLabel);
      
      labelTextViewCard.setTag(label);
   }
   
   /**
    * Yeni grup oluşturma butonuna tıklandığında.
    * Grup oluşturmak için dialoğu başlatır.
    */
   private void onClickCreateNewGroup(){
      
      List<Account> googleAccounts = getGoogleAccounts();
      
      if(googleAccounts == null || googleAccounts.isEmpty()){
         
         Timber.w("Google hesabı yok");
         return;
      }
      
      if(googleAccounts.size() > 1){
         
         selectAccountForNewGroup(googleAccounts);
         getNewGroupName("Yeni Grup İsmi", this::createNewGroup);
      }
      else{
         
         selectedAccountForGroup = googleAccounts.get(0);
         getNewGroupName("Yeni Grup İsmi", this::createNewGroup);
      }
   }
   
   /**
    * Belirtilen isimde yeni bir grup oluştur.
    * Aynı isimde bir grup zaten var ise hiç bir işlem yapılmaz.
    *
    * @param name İsim
    */
   private void createNewGroup(@NonNull String name){
      
      
      if(selectedAccountForGroup == null){
         
         //selectAccountForNewGroup();
         return;
      }
      
      List<String> titles = Contacts.getGroupTitles(this, selectedAccountForGroup);
      
      if(titles.contains(name)){
         
         Message.builder()
                .message("Bu isimde bir grup zaten var")
                .build()
                .showOn(this);
         return;
      }
      
      if(Contacts.createNewGroup(this, name, selectedAccountForGroup)){
         
         Group newGroup = Contacts.getGroup(this, selectedAccountForGroup, name);
         
         if(newGroup != null){
            
            if(Contacts.addLabel(this, contact.getContactId(), newGroup.getId())){
               
               addGroup(newGroup);
               
               Show.globalMessage(this, "Kişi gruba eklendi");
            }
            else{
               
               Show.globalMessage(this, "Kişi gruba eklenemedi");
            }
         }
         else{
            
            Show.globalMessage(this, "Yeni grup bilgileri alınamadı");
         }
      }
      else{
         
         Show.globalMessage(this, "Yeni grup oluşturulamadı");
      }
      
   }
   
   /**
    * Kişinin bağlı olduğu Google hesaplarını döndürür.
    *
    * @return Account list
    */
   @Nullable
   protected final List<Account> getGoogleAccounts(){
      
      val _accounts = contact.getAccounts();
      
      if(_accounts == null) return null;
      
      final String type = getString(R.string.account_type_google);
      
      return Stream.of(_accounts).filter(cx -> cx.type.equals(type)).toList();
   }
   
   /**
    * Kişinin üyesi olduğu gruplar alındığında <code>title</code> değerleri ingilizce.
    * Bu değerleri türkçeye çevirip geri setValue ediyor.
    *
    * @param groups Gruplar
    */
   private void setGroupsTitle(List<Group> groups){
      
      for(int i = 0; i < groups.size(); i++){
         
         Group group = groups.get(i);
         group.setTitle(GroupActivity.getTranslatedGroupName(this, group.getTitle()));
      }
   }
   
   /**
    * Labels, activity'de gösterilen gruplar oluyor.
    * Yani rehberdeki gruplar activity'de label adını alıyor.
    * Çok da gerekli değil ama ikisini birbirinden ayırmak için.
    *
    * @param groups Gruplar
    */
   private void setGroupLabels(Iterable<Group> groups){
      
      //Detay ekranında görseller tek bir NestedScrollView içinde.
      //Ve bunun içindeki ilk ve tek view 'detailsRoot' (LinearLayout)
      //Diğer tüm görseller de 'detailsRoot' içinde.
      final ViewGroup labels_container = findViewById(R.id.labels_container);
      
      //Eğer kişinin numarası yoksa bu view tamamen kaldırılacağı için null gelecek
      if(labels_container == null){ return; }
      
      final LayoutInflater layoutInflater = getLayoutInflater();
      final View           labels         = layoutInflater.inflate(R.layout.contact_detail_labels, labels_container, false);
      final ViewGroup      labelLayout    = labels.findViewById(R.id.labelLayout);
      final ImageView      labelIcon      = labels.findViewById(R.id.labelIcon);
      
      labels.findViewById(R.id.rootRelativeLayout).setBackgroundResource(wellRipple);
      u.setTintDrawable(labelIcon.getDrawable(), getPrimaryColor());
      
      for(Group label : groups){
         
         final View     labelTextViewCard = layoutInflater.inflate(R.layout.label_textview, labelLayout, false);
         final TextView labelTextView     = labelTextViewCard.findViewById(R.id.labelIcon);
         
         labelTextView.setText(label.getTitle());
         
         final GradientDrawable drawable = (GradientDrawable) labelTextView.getBackground();
         drawable.setStroke(2, u.lighter(getPrimaryColor(), .5f));
         labelLayout.addView(labelTextViewCard);
         labelTextViewCard.setOnClickListener(this::labelClickHandler);
         labelTextViewCard.setOnLongClickListener(this::onLongClickLabel);
         
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            labelTextViewCard.setForeground(ContextCompat.getDrawable(this, MainActivity.getWellRipple()));
         }
         
         labelTextViewCard.setTag(label);
      }
      
      labels.findViewById(R.id.createNewGroup).setOnClickListener((v) -> onClickCreateNewGroup());
      labels_container.addView(labels);
   }
   
   /**
    * Grubun zil sesini değiştir.
    *
    * @param ringtone Zil sesi
    */
   protected void setRingtoneToGroup(@NonNull final Uri ringtone){
      
      selectedGroup.setRingTone(ringtone.toString());
      
      List<String> members = Contacts.getGroupMembers(this, selectedGroup.getId());
      
      if(!members.isEmpty()){
         
         ContentValues values = new ContentValues();
         values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, ringtone.toString());
         
         String where = u.format("%s IN (%s)", ContactsContract.Contacts._ID, Contacts.joinToString(members));
         
         getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, values, where, null);
      }
      
      Ringtone rTone = RingtoneManager.getRingtone(this, ringtone);
      
      if(rTone != null){
         
         if(Contacts.setRingToneOfGroup(this, selectedGroup.getId(), ringtone)){
            
            String rTitle = rTone.getTitle(this);
            
            if(rTitle != null){
               
               Show.globalMessage(this, getString(R.string.ringtone_set, rTitle));
               
               onChangeGroupRingtone(rTitle);
            }
         }
      }
      else{
         
         Message.builder()
                .message("Zil sesi alınamadı")
                .type(Show.ERROR)
                .build()
                .showOn(this);
      }
   }
   
   private void onChangeGroupRingtone(String ringtoneName){
      
      ViewGroup ringtoneLayout = findViewById(R.id.change_ringtone);
      
      if(ringtoneLayout == null) return;
      
      TextView ringtoneText = ringtoneLayout.findViewById(R.id.ringtone);
      
      if(ringtoneText == null) return;
      
      TransitionManager.beginDelayedTransition(ringtoneLayout);
      ringtoneText.setText(ringtoneName);
   }
   
   /**
    * Dialog'ta yazılan ismi almak için kullanılacak callback
    */
   private interface GetNameCallBack{
      
      void run(String newName);
      
   }
}
