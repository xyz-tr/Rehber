package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;

import com.amulyakhare.textdrawable.TextDrawable;
import com.annimon.stream.function.ToLongFunction;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.spanner.Spans;
import com.tr.hsyn.telefonrehberi.util.text.Stringx;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.ICommentator;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.Note;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.dialog.ShowList;
import com.tr.hsyn.telefonrehberi.xyz.call.history.Historicall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.MostCallListDialog;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogAction;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.Kickback_CallsConst;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * <h1>CallCommentator</h1>
 *
 * <p>
 * <b>Varsayılan yorumcu</b><br>
 * Bu soyut sınıftan türeyen tüm sınıflar
 * bir kaç istisna haricinde tamamen bu sınıfın sunduğu imkanları
 * kullanarak yorum yaparlar.
 *
 * <p>
 * Yorum yapma olayı esasında iki bölümlü.
 * Birinci bölüm, yorum yapılacak noktaları ve parametreleri belirlemek.
 * Bu sınıf bu birinci bölümü hallediyor.
 * İkinci bölüm, yorumun içeriğini üretmek.
 *
 * @author hsyn 2019-12-01 19:04:47
 */
@SuppressWarnings("WeakerAccess")
public abstract class Commentator implements
      DialogAction, ICommentator<ICall>, Note,
      com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier.ICommentator{
   
   /**
    * Dialog açık mı?
    */
   @Getter @Setter protected       boolean                 isDialogOpen;
   /**
    * Arama kaydı listelerinin gösterildiği dialog
    */
   @Getter protected               AlertDialog             dialog;
   /**
    * Telefonun çalma süresi
    */
   @Getter protected               long                    ringingDuration;
   /**
    * Konuşma süresi
    */
   @Getter protected               long                    speakingDuration;
   /**
    * Süper market
    */
   @Getter protected               IComment                commentStore;
   /**
    * Kişinin geçmişi
    */
   @Getter protected               Historicall<ICall>      history;
   /**
    * Activity weak reference
    */
   @Getter protected               WeakReference<Activity> activityReference;
   /**
    * Yorumlanan arama kaydı
    */
   @Getter protected               ICall                   call;
   /**
    * Dialog'lara set edilen root eleman.
    */
   protected                       View                    rootDialogView;
   /**
    * Bir diğer dialog. Bu dialog 'en çok en az' karşılaştırmalarının sonuçlarını göstermek için
    */
   @Getter @Setter protected       MostCallListDialog      mostCallListDialog;
   /**
    * Bizim bakkal
    */
   @Getter protected               CallStory               callStory;
   /**
    * Call name
    */
   @Getter                         String                  callName;
   /**
    * Açılan dialog pencerelerini dinleyecek olan şahıs
    */
   @Getter protected final         DialogListener          dialogListener;
   /**
    * Karşılaştırmanın tüm aramalar içinde yapılacağını gösterir
    */
   @RecordType public static final int                     ALL_CALLS                            = 0;
   /**
    * Karşılaştırmanın yanlızca kişiye ait ve aramanın türünde olan kayıtlar içinde yapılacağını gösterir.
    */
   @RecordType public static final int                     ALL_CALLS_OF_PERSON_IN_THE_DIRECTION = 1;
   /**
    * Karşılaştırmanın tüm aramalarda, ancak aramanın yönünde olan kayıtlar içinde yapılacağını gösterir.
    */
   @RecordType public static final int                     ALL_CALLS_OF_DIRECTION               = 2;
   /**
    * Karşılaştırmanın kişiye ait tüm kayıtlar içinde yapılacağını gösterir.
    */
   @RecordType public static final int                     ALL_CALLS_OF_PERSON                  = 3;
   /**
    * Karşılaştırma yapmak için olması gereken en az arama kaydı sayısını belirler.
    */
   public static final             int                     COMPARE_SIZE                         = 3;
   
   protected Commentator(WeakReference<Activity> activityWeakReference, IComment commentStore){
      
      //@off
      callStory                    = Kickback_CallsConst.getInstance().getCallStory();assert callStory != null;
      this.activityReference   = activityWeakReference;
      this.commentStore            = commentStore;
      dialogListener               = new DialogListener(this);
      //@on
   }
   
   @Override
   public void onDialogClose(DialogInterface dialogInterface){
      
      isDialogOpen   = false;
      dialog         = null;
      rootDialogView = null;
      
      if(mostCallListDialog != null){
         
         mostCallListDialog.reset();
         mostCallListDialog = null;
      }
   }
   
   @Override
   public void onDialogShow(DialogInterface dialogInterface){}
   
   @Override
   public void showList(CharSequence title, List<ICall> phoneCalls){
      
      showList(title, phoneCalls, null);
   }
   
   @Override
   public void showList(CharSequence title, List<ICall> phoneCalls, ICall phoneCall){
      
      isDialogOpen = true;
      
      dialog = ShowList.builder()
                       .dialogAction(this)
                       .title(title)
                       .call(phoneCall)
                       .calls(phoneCalls)
                       .build()
                       .showOn(activityReference);
   }
   
   @SuppressLint("InflateParams")
   @Override
   public void showCall(ICall phoneCall){
      
      setDialogOpen(true);
      AlertDialog.Builder dialog = new AlertDialog.Builder(getActivityReference().get());
      
      rootDialogView = activityReference.get().getLayoutInflater().inflate(R.layout.dialog_call_list_item, null, false);
      
      final int verticalPadding = 16;
      rootDialogView.setPadding(0, verticalPadding, 0, verticalPadding);
      rootDialogView.setBackgroundResource(MainActivity.getWellRipple());
      
      
      TextView  name         = rootDialogView.findViewById(R.id.name);
      TextView  number       = rootDialogView.findViewById(R.id.number);
      TextView  duration     = rootDialogView.findViewById(R.id.callDuration);
      TextView  date         = rootDialogView.findViewById(R.id.date);
      ImageView image        = rootDialogView.findViewById(R.id.image);
      ImageView type         = rootDialogView.findViewById(R.id.type);
      ImageView durationIcon = rootDialogView.findViewById(R.id.durationIcon);
      TextView  ringingText  = rootDialogView.findViewById(R.id.ringingDuration);
      
      if(phoneCall.getRingingDuration() != 0){
         
         ringingText.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
      }
      else{
         
         ringingText.setText("--:--");
      }
      
      number.setText(CallStory.formatNumberForDisplay(phoneCall.getNumber()));
      date.setText(u.formatDate(phoneCall.getDate()));
      
      String nname  = phoneCall.getName();
      String letter = "?";
      
      if(nname == null || nname.trim().isEmpty()){
         
         nname = phoneCall.getNumber();
      }
      else{
         
         letter = nname.substring(0, 1);
      }
      
      name.setText(nname);
      
      int color = u.colorGenerator.getRandomColor();
      
      Drawable drawable = TextDrawable.builder().buildRound(letter.toUpperCase(), color);
      image.setImageDrawable(drawable);
      duration.setText(u.formatSeconds(phoneCall.getDuration()));
      
      switch(phoneCall.getType()){
         
         case Type.INCOMMING:
            type.setImageDrawable(rootDialogView.getContext().getDrawable(R.drawable.incomming_call));
            break;
         case Type.OUTGOING:
            type.setImageDrawable(rootDialogView.getContext().getDrawable(R.drawable.outgoing_call));
            break;
         case Type.MISSED:
            type.setImageDrawable(rootDialogView.getContext().getDrawable(R.drawable.missed_call));
            duration.setText(rootDialogView.getContext().getString(R.string.missed_call));
            durationIcon.setVisibility(View.GONE);
            duration.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
            break;
         case Type.REJECTED:
            type.setImageDrawable(rootDialogView.getContext().getDrawable(R.drawable.rejected_call));
            duration.setText(rootDialogView.getContext().getString(R.string.rejected_call));
            durationIcon.setVisibility(View.GONE);
            duration.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
            break;
         case Type.BLOCKED:
            type.setImageDrawable(rootDialogView.getContext().getDrawable(R.drawable.blocked_call));
            duration.setText(u.formatMilliseconds(phoneCall.getRingingDuration()));
            
            final float scaleFactor = 0.8f;
            type.setScaleX(scaleFactor);
            type.setScaleY(scaleFactor);
            duration.setText(u.formatSeconds(phoneCall.getDuration()));
            break;
         
         case Type.UNREACHED:
            
            //todo tamamla
            break;
         
         default: break;
      }
      
      dialog.setView(rootDialogView);
      dialog.setCancelable(true);
      
      this.dialog = dialog.create();
      //noinspection ConstantConditions
      this.dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      
      this.dialog.setOnDismissListener(dialogListener.getDialogListener());
      this.dialog.setOnCancelListener(dialogListener.getDialogListener());
      this.dialog.setOnShowListener(dialogListener.getDialogListener());
      this.dialog.show();
   }
   
   @Override
   public CharSequence commentateTheNote(){
      
      Spanner comment = new Spanner();
      String  line    = System.lineSeparator();
      comment.append(line).append(line);
      
      comment.append("✱");
      
      comment.append("  ")
             .append("Hatırlatıcı", Spans.bold())
             .append(" : ");
      
      
      if(this.call.getNote() == null || call.getNote().trim().isEmpty()){
         
         comment.append(" Yok");
      }
      else{
         
         comment.append(Stringx.format("%s", call.getNote()));
      }
      
      return comment;
   }
   
   /**
    * Yorumlanan aramanın değeri ile aynı olan kaç arama olduğunu verir.
    *
    * @param calls    aramalar
    * @param function hangi değere göre yapılacağını söyle. Mesela konuşma süresi olabilir.
    * @return aynı değere sahip arama sayısı
    */
   public static int getRankCount(Iterable<? extends ICall> calls, ToLongFunction<? super ICall> function, ICall call){
      
      int        count     = 0;
      final long oneSecond = 1000L;
      
      int current = (int) function.applyAsLong(call) / 1000;
      
      for(ICall temp : calls){
         
         if(current == (int) (function.applyAsLong(temp) / oneSecond)){
            
            count++;
         }
      }
      
      return count;
   }
   
   protected void setCall(ICall call){
      
      this.call       = call;
      this.history    = callStory.createHistory(call);
      callName        = Stringx.toTitle(call.getName() == null || call.getName().trim().isEmpty() ? call.getNumber() : call.getName());
      ringingDuration = this.call.getRingingDuration();
      long       sDuration = this.call.getSpeakingDuration();
      final long oneSec    = 1000L;
      
      speakingDuration = sDuration == 0 ? call.getDuration() * oneSec : sDuration;
   }
   
   @IntDef({ALL_CALLS, ALL_CALLS_OF_PERSON_IN_THE_DIRECTION, ALL_CALLS_OF_DIRECTION, ALL_CALLS_OF_PERSON})
   @Retention(RetentionPolicy.SOURCE)
   public @interface RecordType{}
   
   
}
