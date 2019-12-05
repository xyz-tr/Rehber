package com.tr.hsyn.telefonrehberi.xyz.call.detail.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import com.amulyakhare.textdrawable.TextDrawable;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.concurrent.Bool;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.DeleteCall;
import com.tr.hsyn.telefonrehberi.util.event.EventReCommentCall;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.CustomViewPager;
import com.tr.hsyn.telefonrehberi.util.ui.htext.base.HTextView;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.database.CallLogDB;
import com.tr.hsyn.telefonrehberi.xyz.call.database.ICallLog;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.NotificationsNoSignActivity;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.actor.NoteEditor;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud.Kickback_CallDetailBox;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.dialog.NoteDialog;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.fragment.CallDetailsFragment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.listener.CommentListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogAction;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.main.page.PageAdapter;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;


/**
 * <h1>CallDetails</h1>
 *
 * <p>Arama kaydı detay ekranı.
 * Detaylar iki parçadan oluşuyor.
 * Biri, kaydın bilgileri,
 * diğeri ise yorumlar.
 * Yorumlar, bir {@code ViewPager} içinde alt kısmı,
 * bilgiler ise üst kısmı oluştuyor.</p>
 *
 * @author hsyn 05-05-2019 23:44:52
 */
public class CallDetails extends AppCompatActivity{
   
   
   @BindView(R.id.viewPager)   CustomViewPager        viewPager;
   @BindView(R.id.toolbar)     Toolbar                toolbar;
   @BindView(R.id.myTitle)     HTextView              myTitle;
   @BindView(R.id.dateHistory) HTextView              dateHistory;
   @BindView(R.id.number)      HTextView              number;
   @BindView(R.id.callTypeStr) HTextView              typeStr;
   @BindView(R.id.timeTime)    HTextView              timeStr;
   @BindView(R.id.dateTime)    HTextView              dateStr;
   @BindView(R.id.type)        ImageView              letter;
   @BindView(R.id.progress)    ProgressBar            progress;
   private                     List<ICall>            phoneCalls;
   private                     ICall                  currentCall;
   private                     PageAdapter            pageAdapter;
   private                     Kickback_CallDetailBox callDetailBox = Kickback_CallDetailBox.getInstance();
   private                     CallDetailsFragment[]  fragments;
   private                     boolean                isNoteDialogOpen;
   private                     ICallLog               db;
   
   
   @Override
   protected final void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.activity_call_details2);
      ButterKnife.bind(this);
      progress.setIndeterminateDrawable(new CubeGrid());
      findViewById(R.id.detailsRoot).setBackgroundColor(u.getPrimaryColor(this));
      Kickback_CallDetailBox.setLifecycleObserver(this);
      db = new CallLogDB(this);
      setupViews();
      setPage();
      
   }
   
   @Override
   public void onConfigurationChanged(@NonNull Configuration newConfig){
      
      super.onConfigurationChanged(newConfig);
      
      if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
         
         findViewById(R.id.mainHeader).setVisibility(View.GONE);
      }
      else{
         
         findViewById(R.id.mainHeader).setVisibility(View.VISIBLE);
      }
      
   }
   
   @Override
   protected void onDestroy(){
      
      super.onDestroy();
      
      if(db != null) db.close();
   }
   
   private void setPage(){
      
      phoneCalls = callDetailBox.getCalls();
      
      if(phoneCalls == null){
         
         Timber.w("Arama kayıtları set edilmemiş - Kickback_CallDetailBox");
         
         finishAndRemoveTask();
         return;
      }
      
      setFragments();
      setViewPager();
      setCurrentCallDetail();
   }
   
   private void setCurrentCallDetail(){
      
      ViewPager.OnPageChangeListener pageChageListener = new PageChageListener();
      
      viewPager.addOnPageChangeListener(pageChageListener);
      
      int currentIndex = 0;
      currentCall = getCurrentCall();
      
      if(currentCall != null){
         
         currentIndex = phoneCalls.indexOf(currentCall);
      }
      else{
         
         currentCall = phoneCalls.get(currentIndex);
      }
      
      if(currentIndex != 0){
         
         viewPager.setCurrentItem(currentIndex);
      }
      else{
         
         pageChageListener.onPageSelected(currentIndex);
      }
      
      setPageState(currentCall.getDate());
      Logger.d(currentCall);
   }
   
   private Call getCurrentCall(){
      
      return getIntent().getParcelableExtra("call");
   }
   
   /**
    * Yorumun alınıp alınmamasına göre viewpager'ın kaydırma olayını aktif ve pasif yap.
    *
    * @param date yorumu alınan arama kaydının tarihi. Bir nevi id numarası
    */
   private void setPageState(long date){
      
      boolean isOkey = isOkey();
      
      //Görünümde olan arama kaydı ise işlem yap
      if(date == currentCall.getDate()){
         
         viewPager.setPagingEnabled(isOkey);
         
         //Yanlızca görümünde olan eleman için olmalı
         progress.setVisibility(isOkey ? View.GONE : View.VISIBLE);
      }
      else{
         
         //Eğer yorum yükleniyorsa ve progress ortada yoksa göster
         
         if(!isOkey){
            
            if(progress.getVisibility() != View.VISIBLE){
               
               progress.setVisibility(View.VISIBLE);
            }
         }
      }
   }
   
   private boolean isOkey(){
      
      return ((CommentListener) pageAdapter.getItem(viewPager.getCurrentItem())).onComment(currentCall.getDate());
   }
   
   private void setViewPager(){
      
      viewPager.setOffscreenPageLimit(3);
      pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments);
      
      viewPager.setAdapter(pageAdapter);
   }
   
   private void setFragments(){
      
      fragments = new CallDetailsFragment[phoneCalls.size()];
      
      for(int i = 0; i < fragments.length; i++){
         
         ICall phoneCall = phoneCalls.get(i);
         
         fragments[i] = CallDetailsFragment.getInstance(phoneCall, new OnCommentListener());
      }
   }
   
   private void setupViews(){
      
      View view = findViewById(R.id.date);
      
      view.setBackgroundColor(u.getPrimaryColor(this));
      
      setupToolbar();
   }
   
   private void setupToolbar(){
      
      toolbar.inflateMenu(R.menu.call_details);
      toolbar.setBackgroundColor(u.getPrimaryColor(this));
      toolbar.setOnMenuItemClickListener(menuItem -> {
         
         
         if(menuItem.getItemId() == R.id.call_details_delete){
            
            onDelete();
            return true;
         }
         
         if(menuItem.getItemId() == R.id.menu_notifications){
            
            onNotifications();
            return true;
         }
         
         if(menuItem.getItemId() == R.id.reminder){
            
            onMenuNote();
            return true;
         }
         
         
         return false;
      });
      
      toolbar.setNavigationOnClickListener(v -> onBackPressed());
   }
   
   private void onMenuNote(){
      
      if(isNoteDialogOpen) return;
      
      isNoteDialogOpen = true;
      
      int textColor  = u.getColor(this, R.color.blue_400);
      int idColor    = u.getColor(this, R.color.blue_700);
      int timeColor  = u.getColor(this, R.color.green_600);
      int oneColor   = u.getColor(this, R.color.black25PercentColor);
      int milisColor = u.getColor(this, R.color.yellow_800);
      
      
      Spanner deepNote = new Spanner()
            .append("\u235F", Spans.bold(), Spans.scaleSize(1.2f))
            .append(" Not yazmakta olduğun arama kaydının kimlik numarası ")
            .append(u.format("%d", currentCall.getDate()), Spans.bold(), Spans.underline(), Spans.foreground(idColor))
            .append(". Bu sayı, elektronik sistemlerde zamanın başlangıcı olarak kabul edilen ")
            .append(u.format("%s", Time.getDate(0)), Spans.foreground(timeColor), Spans.bold())
            .append(" tarihinden aramanın gerçekleştiği zamana kadar geçen ")
            .append("milisaniye", Spans.bold(), Spans.foreground(milisColor))
            .append("lerdir ve bir telefonda iki arama kaydının aynı milisaniyelere sahip olması ")
            .append("imkansızdır", Spans.bold(), Spans.underline(), Spans.foreground(oneColor))
            .append(". ");
      
      
      Spanner description = new Spanner("Sana arama hakkında birşeyler hatırlatacak bir not yazabilirsin. Mesela ")
            .append("Babam doğum günümü kutladı", Spans.bold(), Spans.foreground(textColor))
            .append(". Ya da ")
            .append("Partiye davet edildim", Spans.bold(), Spans.foreground(textColor))
            .append(". Ya da ")
            .append("Sırrımı bilen bir kişi daha oldu", Spans.bold(), Spans.foreground(textColor))
            .append(" gibi şeyler.");
      
      
      NoteEditor noteEditor = note -> Worker.onBackground(() -> {
         
         boolean update = db.update(currentCall);
         
         Runnable trueAction  = () -> Timber.d("Note kaydedildi : %s", currentCall.getNote());
         Runnable falseAction = () -> Timber.w("Note kaydedilemedi : %s", currentCall.getNote());
         
         Bool.ifFalseTrue(update, trueAction, falseAction);
      }, "CallCommentator:Arama kaydına eklenen notu veri tabanına işleme");
      
      
      NoteDialog noteDialog = new NoteDialog(
            new WeakReference<>(this),
            description,
            currentCall.getNote() != null,
            deepNote,
            currentCall,
            noteEditor)
            .setOnCompleteListener(CallDetails::reComment);
      
      noteDialog.setDialogAction(new DialogAction(){
         
         @Override
         public void onDialogClose(DialogInterface dialogInterface){
            
            isNoteDialogOpen = false;
         }
         
         @Override
         public void onDialogShow(DialogInterface dialogInterface){
            
         }
      });
      noteDialog.show();
   }
   
   /**
    * Yorumun yeniden yüklenmesi gerektiğini bildirmek için broadcast gönderir.
    */
   private static void reComment(){
      
      EventBus.getDefault().post(new EventReCommentCall());
   }
   
   private void onNotifications(){
      
      Intent intent = new Intent(this, NotificationsNoSignActivity.class);
      
      startActivity(intent);
   }
   
   public final void onDelete(){
      
      Timber.d("Delete requested");
      
      if(currentCall != null){
         
         Timber.d("Sent delete request : %s", Stringx.overWrite(currentCall.getNumber()));
         EventBus.getDefault().post(new DeleteCall(currentCall));
      }
      else{
         
         Timber.w("Geçerli arama kaydı null");
      }
      
      onBackPressed();
   }
   
   @Override
   public final void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideRight(this);
   }
   
   @Override
   public final boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   public static Intent createIntent(@NonNull final Context context, @NonNull final ICall phoneCall){
      
      Intent intent = new Intent(context, CallDetails.class);
      intent.putExtra("call", (Parcelable) phoneCall);
      return intent;
   }
   
   @SuppressLint("ParcelCreator")
   private class OnCommentListener implements CommentListener{
      
      @Override
      public boolean onComment(long id){
         
         setPageState(id);
         
         return false;
      }
      
      @Override
      public int describeContents(){
         
         return 0;
      }
      
      @Override
      public void writeToParcel(Parcel dest, int flags){
         
      }
   }
   
   private class PageChageListener implements ViewPager.OnPageChangeListener{
      
      @Override
      public final void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){
         
         //Gerek yok
      }
      
      @Override
      public final void onPageSelected(int position){
         
         currentCall = phoneCalls.get(position);
         changeCallHead(position);
         setPageState(currentCall.getDate());
         
         if(!fragments[position].isComment) progress.setVisibility(View.VISIBLE);
         else progress.setVisibility(View.GONE);
      }
      
      @Override
      public void onPageScrollStateChanged(int state){
         
         //Gerek yok
      }
      
      private void changeCallHead(int index){
         
         myTitle.animateText(getCallName(index));
         
         Time timeDate = new Time(currentCall.getDate());
         
         typeStr.animateText(Type.getTypeStr(CallDetails.this, currentCall.getType()));
         dateStr.animateText(timeDate.getDateStr());
         timeStr.animateText(timeDate.getTimeStr());
         dateHistory.animateText(u.format("(%s)", Time.getDateHistory(currentCall.getDate())));
         
         /*000000000000000000000000000000000000000000000000000000000000000000000*/
         
         
         View headerBackground = findViewById(R.id.mainHeader);
         
         int pColor = u.getPrimaryColor(CallDetails.this);
         
         getWindow().setStatusBarColor(pColor);
         headerBackground.setBackgroundColor(pColor);
         
         
         number.animateText(CallStory.formatNumberForDisplay(currentCall.getNumber()));
         
         
         Drawable drawable;
         
         TextDrawable.IShapeBuilder builder = TextDrawable.builder()
                                                          .beginConfig()
                                                          .textColor(pColor)
                                                          .fontSize(120)
                                                          .useFont(ResourcesCompat.getFont(CallDetails.this, R.font.poppins_black))
                                                          .endConfig();
         
         int tColor = ContextCompat.getColor(CallDetails.this, R.color.white);
         
         String tname = currentCall.getName();
         
         if(tname == null || tname.isEmpty() || tname.charAt(0) == '+' || Character.isDigit(tname.charAt(0))){
            
            drawable = builder.buildRound("?", tColor);
         }
         else{
            
            drawable = tname.length() == 1 ? builder.buildRound(tname.toUpperCase(), tColor) : builder.buildRound(tname.substring(0, 1).toUpperCase(), tColor);
         }
         
         letter.setImageDrawable(drawable);
      }
      
      private String getCallName(int index){
         
         String title = currentCall.getName();
         
         if(title == null || title.trim().isEmpty()){
            
            title = phoneCalls.get(index).getNumber();
         }
         
         return title;
      }
   }
   
   
}
