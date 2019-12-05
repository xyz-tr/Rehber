package com.tr.hsyn.telefonrehberi.xyz.ptt.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.annimon.stream.ComparatorCompat;
import com.annimon.stream.Stream;
import com.github.florent37.viewanimator.ViewAnimator;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventMessageState;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.FlipImageView;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.IOutbox;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.IPTTService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import timber.log.Timber;


public class OutboxFragment extends Fragment{
   
   private volatile ViewGroup         messagesContainer;
   private volatile IOutbox           outbox;
   private volatile View              progress;
   //private volatile View              topElements;
   private volatile View              scroolView;
   private volatile Map<String, View> items    = new HashMap<>();
   private          View              emptyView;
   private          LayoutInflater    inflater;
   private          TextView          title;
   private          Switch            switchSendMessages;
   private          View              root;
   private          int               lastSize = -1;
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
      
      root          = inflater.inflate(R.layout.fragment_outbox, container, false);
      this.inflater = inflater;
      
      View messageWatchState = root.findViewById(R.id.message_watch_state);
      
      outbox = IPTTService.getService(getContext()).getOutbox();
      u.setTintDrawable(messageWatchState.getBackground(), outbox.isWatchingOutbox() ? Color.GREEN : Color.RED);
      EventBus.getDefault().register(this);
      
      Worker.onBackground(() -> {
         
         scroolView        = root.findViewById(R.id.scrool_view);
         progress          = root.findViewById(R.id.progress);
         title             = root.findViewById(R.id.title);
         messagesContainer = root.findViewById(R.id.messages_container);
         emptyView         = root.findViewById(R.id.empty_view);
         
         
         outbox.setStopSending(true);
         outbox.setNewFileListener(this::addFile);
         
         int   primaryColor = u.getPrimaryColor(getContext());
         float factor       = 0.97F;
         switchSendMessages = root.findViewById(R.id.stop_send_message_switch);
         switchSendMessages.setOnCheckedChangeListener(this::onSendMessagesSwitch);
         
         Worker.onBackground(this::getFiles, "OutboxFragment:Mail dosyalarını alma")
               .whenCompleteAsync(this::setupFiles, Worker.getMainThreadExecutor());
         
         Worker.onMain(() -> {
            
            switchSendMessages.setChecked(outbox.isStopSending());
            root.setBackgroundColor(u.lighter(primaryColor, factor));
         });
         
      }, "OutboxFragment:Kullanıma hazır hale getirme");
      
      return root;
   }
   
   @Override public void onDestroy(){
      
      super.onDestroy();
      
      EventBus.getDefault().unregister(this);
      outbox.setNewFileListener(null);
      outbox.setStopSending(false);
   }
   
   private void onSendMessagesSwitch(CompoundButton buttonView, boolean isChecked){
   
      outbox.setStopSending(isChecked);
   }
   
   private void addFile(File file){
      
      View view = createItemView(file);
      
      items.put(file.getName(), view);
      checkEmpty();
      
      TransitionManager.beginDelayedTransition(messagesContainer);
      
      messagesContainer.addView(view, 1);
      
      TransitionManager.beginDelayedTransition(root.findViewById(R.id.title_layout));
      title.setText(Stringx.format("Gönderilmeyi bekleyen %d mesaj", items.size()));
   }
   
   @SuppressLint("SetTextI18n")
   private void setupFiles(File[] files, Throwable throwable){
   
      ViewAnimator.animate(progress).fadeOut().duration(1000).start();
      
      if(getContext() == null) return;
      
      if(files != null){
   
         setMessageCount(files.length);
   
         for(File file : files){
      
            View view = createItemView(file);
      
            items.put(file.getName(), view);
            messagesContainer.addView(view);
         }
   
         if(items.size() != 0){
      
            onLoadComplete();
         }
   
         checkEmpty();
      }
      
      if(throwable != null){
         
         Timber.w("Mail dosyaları alınırken hata oldu : %s", throwable.getMessage());
      }
   }
   
   private View createItemView(File file){
      
      View view = inflater.inflate(R.layout.outbox_item, messagesContainer, false);
      
      View send = view.findViewById(R.id.send);
      send.setTag(file);
      send.setOnClickListener(this::onClickSend);
      
      TextView fileName    = view.findViewById(R.id.file_name);
      TextView messageDate = view.findViewById(R.id.message_date);
      
      String _fileName = file.getName().substring(0, file.getName().indexOf('.'));
      
      fileName.setText(_fileName);
      messageDate.setText(Time.getDate(file.lastModified()));
      
      SmoothProgressBar bar = view.findViewById(R.id.message_progress);
      bar.progressiveStop();
      
      View content = view.findViewById(R.id.content);
      
      content.setBackgroundResource(MainActivity.getWellRipple());
      content.setOnClickListener(v -> onClickItem(file));
      
      return view;
   }
   
   private void onClickItem(File file){
      
      Timber.d("select : %s", file.getName());
      
      //todo mesajın içeriği gösterilebilir.
      
      
   }
   
   private void onClickSend(View view){
      
      view.setEnabled(false);
      File file = (File) view.getTag();
      
      if(file == null){
         
         Timber.w("Mesaj dosyası alınamadı");
         return;
      }
      
      IMailMessage mailMessage = IMailMessage.createFromXml(file);
      
      if(mailMessage != null){
         
         outbox.sendMessage(mailMessage);
      }
      else{
         
         Timber.w("Mesaj dosyası okunamadı");
      }
   }
   
   @Subscribe(threadMode = ThreadMode.MAIN)
   void onMessageStateChange(EventMessageState event){
      
      File file = event.getMessage().getFile();
      
      switch(event.getState()){
         
         case SENDING:
            
            onMessageSending(file);
            break;
         
         case SENT:
            
            onMessageSent(file);
            break;
         
         case FAILED:
            
            onMessageFail(file);
            break;
      }
   }
   
   private void onMessageSending(File file){
      
      View view = items.get(file.getName());
      
      if(view != null){
         
         SmoothProgressBar bar = view.findViewById(R.id.message_progress);
         bar.setVisibility(View.VISIBLE);
         bar.progressiveStart();
      }
   }
   
   private void onMessageSent(File file){
      
      if(getContext() == null) return;
      
      ViewGroup view = (ViewGroup) items.get(file.getName());
      
      if(view != null){
         
         SmoothProgressBar bar = view.findViewById(R.id.message_progress);
         bar.progressiveStop();
         
         FlipImageView send = view.findViewById(R.id.send);
         
         send.setFlippedDrawable(ContextCompat.getDrawable(getContext(), R.drawable.done));
         send.setFlipped(true, true);
         
         Worker.onMain(() -> removeView(file.getName()), 2000);
      }
   }
   
   private void removeView(String key){
      
      View view = items.get(key);
      
      if(view != null){
         
         items.remove(key);
         TransitionManager.beginDelayedTransition(messagesContainer);
         messagesContainer.removeView(view);
         
         setMessageCount(items.size());
         
         checkEmpty();
         
      }
      
   }
   
   private void checkEmpty(){
      
      int size = items.size();
      
      if(size == lastSize) return;
      
      lastSize = size;
      
      if(size == 0){
         
         //topElements.setVisibility(View.GONE);
         scroolView.setVisibility(View.GONE);
         
         emptyView.setVisibility(View.VISIBLE);
         ViewAnimator.animate(emptyView).zoomIn().duration(800).start();
      }
      else{
         
         if(size == 1){
            
            if(scroolView.getVisibility() == View.GONE){
               
               scroolView.setVisibility(View.VISIBLE);
               
               onLoadComplete();
            }
         }
         
         if(emptyView.getVisibility() == View.INVISIBLE) return;
         
         ViewAnimator.animate(emptyView).zoomOut().duration(400).start();
         
         emptyView.setVisibility(View.INVISIBLE);
      }
      
   }
   
   private void onLoadComplete(){
      
      ViewAnimator.animate(scroolView)
                  .alpha(0, 1)
                  .translationY(-1600, 0)
                  .decelerate()
                  .duration(600)
                  .start();
   }
   
   private void setMessageCount(int count){
      
      String _title;
      
      if(count == 0){
         
         _title = "Gönderilmeyi bekleyen mesaj yok";
      }
      else{
         
         _title = Stringx.format("Gönderilmeyi bekleyen %d mesaj", count);
      }
      
      ViewAnimator.animate(title).fadeOut().duration(600).onStop(() -> title.setText(_title)).thenAnimate(title).slideBottomIn().duration(600).start();
      
   }
   
   private void onMessageFail(File file){
      
      if(getContext() == null) return;
      ViewGroup view = (ViewGroup) items.get(file.getName());
      
      if(view != null){
         
         SmoothProgressBar bar = view.findViewById(R.id.message_progress);
         bar.progressiveStop();
         
         
         FlipImageView send = view.findViewById(R.id.send);
         
         send.setFlippedDrawable(ContextCompat.getDrawable(getContext(), R.drawable.error));
         send.setFlipped(true, true);
         send.setOnClickListener(null);
         
         send.setEnabled(true);
      }
   }
   
   private File[] getFiles(){
      
      return Stream.of(outbox.getOutFiles()).sorted(ComparatorCompat.comparingLong(File::lastModified).reversed()).toArray(File[]::new);
   }
}
