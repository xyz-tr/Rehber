package com.tr.hsyn.telefonrehberi.xyz.call.main.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.transition.TransitionManager;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.BuildConfig;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.save.Save;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.snack.Show;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.Type;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.CallLogFiltered;
import com.tr.hsyn.telefonrehberi.xyz.call.main.random.listener.GeneratorOperationListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.service.RandomCallsService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import hari.bounceview.BounceView;
import timber.log.Timber;

import static com.tr.hsyn.telefonrehberi.util.text.Stringx.isPhoneNumber;


public class RandomCallsActivity extends AppCompatActivity{
   
   
   private       Button                     startButton;
   private       Button                     startDateButton;
   private       Button                     endDateButton;
   private       EditText                   editText;
   private       CheckBox                   saveSystemCheckBox;
   private       CheckBox                   isShare;
   private       boolean                    isRingtone;
   private       boolean                    isSharable;
   private       View                       progressIcon;
   private       TextView                   progressCounter;
   private       TextView                   progressText;
   private       ViewGroup                  callTypes;
   private       ViewGroup                  progressView;
   private       ProgressBar                progressBar;
   //private ViewGroup      counterView;
   private       List<Integer>              types;
   private       int                        count;
   private final ActivityGenerationListener activityGenerationListener = new ActivityGenerationListener();
   private       RandomCallsService         randomCallsService;
   private       boolean                    isSaveSystem;
   private       Calendar                   startDate                  = Calendar.getInstance();
   private       Calendar                   endDate                    = Calendar.getInstance();
   private       long                       now                        = Time.getTime();
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.random_calls);
      setupToolbar();
      
      setupViews();
      
   }
   
   /**
    * <code>random_calls.xml</code> layout dosyasında tanımlanmış ilgili <code>view</code>
    * nesnelerini tanımlamak ve olayları ayarlamak için.
    * Tipik bir başlama metodu.
    * Çok tipik.
    */
   private void setupViews(){
      
      startButton        = findViewById(R.id.progressStart);
      editText           = findViewById(R.id.edittext);
      saveSystemCheckBox = findViewById(R.id.saveSystem);
      progressIcon       = findViewById(R.id.progressIcon);
      progressCounter    = findViewById(R.id.progressCounter);
      progressText       = findViewById(R.id.progressText);
      callTypes          = findViewById(R.id.callTypes);
      progressView       = findViewById(R.id.progressTextView);
      startDateButton    = findViewById(R.id.startDateButton);
      endDateButton      = findViewById(R.id.endDateButton);
      startDateButton.setOnClickListener((v) -> onClickStartDate());
      endDateButton.setOnClickListener((v) -> onClickEndDate());
      isShare = findViewById(R.id.isSharable);
      //counterView = findViewById(R.id.progressCounterView);
      
      progressBar = findViewById(R.id.progress);
      BounceView.addAnimTo(startButton);
      
      editText.addTextChangedListener(new TextWatcher(){
         
         @Override
         public void beforeTextChanged(CharSequence s, int start, int count, int after){
            
         }
         
         @Override
         public void onTextChanged(CharSequence s, int start, int before, int count){
            
            
            if(s.toString().isEmpty()){
               
               startButton.setEnabled(false);
               return;
            }
            
            if(isPhoneNumber(s.toString())){
               
               if(!startButton.isEnabled()){
                  
                  startButton.setEnabled(true);
                  editText.setTextColor(Color.BLACK);
               }
            }
            else{
               
               if(startButton.isEnabled()){
                  
                  startButton.setEnabled(false);
                  editText.setTextColor(Color.RED);
               }
            }
         }
         
         @Override
         public void afterTextChanged(Editable s){
            
         }
      });
      editText.setOnEditorActionListener((v, actionId, event) -> {
         
         if(actionId == EditorInfo.IME_ACTION_DONE){
            
            startButton.performClick();
            return true;
         }
         return false;
      });
      startButton.setOnClickListener(this::onClickStartButton);
      
      restoreStates();
      
      setDateButtonText(startDateButton, startDate);
      setDateButtonText(endDateButton, endDate);
   }
   
   /**
    * Başlangıç ve bitiş tarihleri butonların ismi olacak.
    *
    * @param button   İsmi ayarlanacak olan buton
    * @param calendar Ayarlanacak tarih
    */
   private void setDateButtonText(Button button, Calendar calendar){
      
      button.setText(
            u.format("%02d.%02d.%d",
                     calendar.get(Calendar.DAY_OF_MONTH),
                     calendar.get(Calendar.MONTH) + 1,
                     calendar.get(Calendar.YEAR)));
   }
   
   /**
    * Üretimin başlama tarihini ayarlayacak.
    */
   private void onClickStartDate(){
      
      DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
         
         startDate.set(year, month, dayOfMonth);
         
         if(startDate.getTimeInMillis() > endDate.getTimeInMillis()){
            
            startDate.setTimeInMillis(endDate.getTimeInMillis());
            
            Show.globalMessage(this, "Başlangıç bitişten büyük olamaz. Düzeltildi");
         }
         
         
         setDateButtonText(startDateButton, startDate);
         
      }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
      
      
      datePickerDialog.getDatePicker().setMaxDate(endDate.getTimeInMillis());
      
      datePickerDialog.setCancelable(true);
      
      datePickerDialog.show();
   }
   
   /**
    * Üretimin bitiş tarihini ayarlayacak.
    */
   private void onClickEndDate(){
      
      DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
         
         endDate.set(year, month, dayOfMonth);
         
         if(endDate.getTimeInMillis() > now){
            
            endDate.setTimeInMillis(now);
            
            Show.globalMessage(RandomCallsActivity.this, "Bitiş tarihi günümüzden büyük olamaz. Düzeltildi");
         }
         
         
         setDateButtonText(endDateButton, endDate);
         
      }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
      
      
      datePickerDialog.getDatePicker().setMaxDate(now);
      datePickerDialog.getDatePicker().setMinDate(startDate.getTimeInMillis());
      datePickerDialog.setCancelable(true);
      
      datePickerDialog.show();
   }
   
   @Override
   protected void onResume(){
      
      super.onResume();
      
      ifAlive();
   }
   
   /**
    * Activity açıldığında devam eden bir üretim varsa üretim servisine bağlanmak için hazırlanacak
    */
   private void ifAlive(){
      
      if(RandomCallsService.isRunning()){
         
         count = RandomCallsService.getCount();
         startButton.setText("Durdur");
         
         setGenerationListener();
         randomCallsService = RandomCallsService.getService();
         randomCallsService.stopLonlyMode();
      }
      
   }
   
   private void setupToolbar(){
      
      Toolbar toolbar = findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
      
      int color = u.getPrimaryColor(this);
      
      toolbar.setBackgroundColor(color);
      getWindow().setStatusBarColor(color);
   }
   
   private void onClickStartButton(View view){
      
      saveStates();
      setMode();
   }
   
   private void saveStates(){
      
      Save save = new Save(this, "RandomService");
      
      save.saveBoolean("saveSystem", saveSystemCheckBox.isChecked());
      save.saveBoolean("incomming", ((CheckBox) findViewById(R.id.incomming)).isChecked());
      save.saveBoolean("outgoing", ((CheckBox) findViewById(R.id.outgoing)).isChecked());
      save.saveBoolean("missed", ((CheckBox) findViewById(R.id.missed)).isChecked());
      save.saveBoolean("rejected", ((CheckBox) findViewById(R.id.rejected)).isChecked());
      save.saveBoolean("blocked", ((CheckBox) findViewById(R.id.blocked)).isChecked());
      save.saveLong("startDate", startDate.getTimeInMillis());
      save.saveLong("endDate", endDate.getTimeInMillis());
      
      if(BuildConfig.DEBUG){
         
         save.saveBoolean("isShare", isShare.isChecked());
         save.saveBoolean("unreached", ((CheckBox) findViewById(R.id.unreached)).isChecked());
         save.saveBoolean("unrecieved", ((CheckBox) findViewById(R.id.unrecieved)).isChecked());
         save.saveBoolean("getrejected", ((CheckBox) findViewById(R.id.getrejected)).isChecked());
      }
      
      
      String count = editText.getText().toString();
      int    c;
      
      if(count.trim().isEmpty()){
         
         count = "0";
      }
      
      try{
         
         c = Integer.valueOf(count);
         save.saveInt("count", c);
      }
      catch(NumberFormatException e){
   
         Timber.w("Sayı dönüşümü hatalı");
         e.printStackTrace();
      }
   }
   
   private void restoreStates(){
      
      Save save = new Save(this, "RandomService");
      
      saveSystemCheckBox.setChecked(save.getBoolean("saveSystem", true));
      
      ((CheckBox) findViewById(R.id.incomming)).setChecked(save.getBoolean("incomming", true));
      ((CheckBox) findViewById(R.id.outgoing)).setChecked(save.getBoolean("outgoing", true));
      ((CheckBox) findViewById(R.id.missed)).setChecked(save.getBoolean("missed", true));
      ((CheckBox) findViewById(R.id.rejected)).setChecked(save.getBoolean("rejected", true));
      ((CheckBox) findViewById(R.id.blocked)).setChecked(save.getBoolean("blocked", false));
      
      if(BuildConfig.DEBUG){
         
         ((CheckBox) findViewById(R.id.unreached)).setChecked(save.getBoolean("unreached", false));
         ((CheckBox) findViewById(R.id.unrecieved)).setChecked(save.getBoolean("unrecieved", false));
         ((CheckBox) findViewById(R.id.getrejected)).setChecked(save.getBoolean("getrejected", false));
      }
      else{
         
         findViewById(R.id.unreached).setEnabled(false);
         findViewById(R.id.unrecieved).setEnabled(false);
         findViewById(R.id.getrejected).setEnabled(false);
      }
      
      startDate.setTimeInMillis(save.getLong("startDate", startDate.getTimeInMillis() - TimeUnit.DAYS.toMillis(180)));
      
      long end = save.getLong("endDate", 0L);
      
      if(end == 0L){
         
         endDate.set(Calendar.DAY_OF_MONTH, endDate.get(Calendar.DAY_OF_MONTH) - 1);
      }
      else{
         
         endDate.setTimeInMillis(end);
      }
      
      if(BuildConfig.DEBUG){
         
         isShare.setChecked(save.getBoolean("isShare", true));
      }
      
      int count = save.getInt("count", 0);
   
      if(count == 0){ return; }
      
      editText.setText(String.valueOf(count));
   }
   
   /**
    * Üretim varsa durduracak, yoksa başlama için hazırlayacak.
    */
   private void setMode(){
      
      if(startButton.isEnabled()){
         
         startButton.setEnabled(false);
   
         Worker.onMain(() -> {
            
            TransitionManager.beginDelayedTransition(progressView);
            startButton.setEnabled(true);
         }, 2000);
      }
      
      
      if(RandomCallsService.isRunning()){
         
         stopMode();
      }
      else{
         
         if(runningMode()){
            
            startService();
         }
      }
   }
   
   private boolean isNumberEntered(){
      
      String c = editText.getText().toString().trim();
      
      return !c.isEmpty();
   }
   
   private void notEnteredNumberMessage(){
      
      u.toast(RandomCallsActivity.this, "Sayı belirtilmedi");
   }
   
   private void enteredNumberInvalidMessage(){
      
      String message = "Sayı hatalı. En yüksek değer ayarlandı";
      
      u.toast(this, message);
      Show.globalMessage(this, message, 6000L);
   }
   
   private boolean isNumberValid(){
      
      String c = editText.getText().toString().trim();
      
      if(c.isEmpty()){
         
         return false;
      }
      
      try{
         
         Integer.valueOf(c);
         return true;
      }
      catch(Exception ignore){}
      
      return false;
   }
   
   private int getEnteredNumber(){
      
      return Integer.parseInt(editText.getText().toString().trim());
   }
   
   /**
    * Üretim için gerekli bilgileri toplayacak ve başlamaya hazır hale getirecek.
    *
    * @return Eğer gerekli bilgiler sağlanmışsa <code>true</code>, aksi halde <code>false</code>.
    */
   private boolean runningMode(){
      
      if(!isNumberEntered()){
         
         notEnteredNumberMessage();
         return false;
      }
      
      if(isNumberValid()){
         
         count = getEnteredNumber();
      }
      else{
   
         count = Integer.MAX_VALUE;
         editText.setText(String.valueOf(count));
         enteredNumberInvalidMessage();
      }
      
      isSaveSystem = saveSystemCheckBox.isChecked();
      
      types = Stream.range(0, callTypes.getChildCount())
            .map(i -> {
               
               if(((CheckBox) callTypes.getChildAt(i)).isChecked()){
                  
                  return getCallType(i);
               }
               
               return 0;
            })
            .filter(i -> i != 0).toList();
      
      
      if(types.size() == 0){
         
         String message = "En az 1 arama türü seçmelisin";
         u.toast(this, message);
         Show.globalMessage(this, message, 6000);
         
         return false;
      }
      
      isRingtone = isShare.isChecked();
      editText.setEnabled(false);
      
      if(BuildConfig.DEBUG){
         
         isSharable = isShare.isChecked();
      }
      else{
         
         isShare.setChecked(false);
         isShare.setEnabled(false);
      }
      
      
      setGenerationListener();
      startButton.setText("Durdur");
      return true;
   }
   
   /**
    * Üretimi durduracak.
    */
   private void stopMode(){
   
      if(randomCallsService != null){ randomCallsService.stopService(); }
      
      startButton.setText("Üret");
      editText.setEnabled(true);
   }
   
   private void setProgress(){
      
      progressBar.setIndeterminate(false);
      progressBar.setMax(count);
      
      int progress = RandomCallsService.getProgress();
      
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
         progressBar.setProgress(progress, true);
      }
      else{
         progressBar.setProgress(progress);
      }
   }
   
   private void setEditText(){
   
      Worker.onMain(() -> {
         
         editText.setText(String.valueOf(count));
         editText.setEnabled(false);
      }, 1000);
   }
   
   /**
    * Üretim servisini başlatacak
    */
   private void startService(){
      
      startDate.set(Calendar.HOUR_OF_DAY, 0);
      startDate.set(Calendar.MINUTE, 0);
      startDate.set(Calendar.SECOND, 0);
      endDate.set(Calendar.HOUR_OF_DAY, 23);
      endDate.set(Calendar.MINUTE, 59);
      endDate.set(Calendar.SECOND, 59);
      
      Intent i = new Intent(this, RandomCallsService.class);
      i.putExtra(RandomCallsService.COMMAND, RandomCallsService.START);
      i.putExtra(RandomCallsService.EXTRA_COUNT, count);
      i.putIntegerArrayListExtra(RandomCallsService.EXTRA_TYPES, (ArrayList<Integer>) types);
      i.putExtra(RandomCallsService.EXTRA_SAVE_SYSTEM, isSaveSystem);
      i.putExtra(RandomCallsService.EXTRA_START_DATE, startDate.getTimeInMillis());
      i.putExtra(RandomCallsService.EXTRA_END_DATE, endDate.getTimeInMillis());
      i.putExtra(RandomCallsService.EXTRA_RINGTONE_DURATION, isRingtone);
      i.putExtra(RandomCallsService.EXTRA_SHARABLE, isSharable);
      
      startService(i);
   }
   
   private void setProgressText(String text){
      
      //TransitionManager.beginDelayedTransition(progressView);
      progressText.setText(text);
   }
   
   private void counter(int i){
      
      progressCounter.setText(u.format("%9d / %d", i, count));
   }
   
   private int getCallType(int index){
      
      switch(index){
         case 0 : return Type.INCOMMING;
         case 1 : return Type.OUTGOING;
         case 2 : return Type.MISSED;
         case 3 : return Type.REJECTED;
         case 4 : return Type.BLOCKED;
         case 5 : return Type.UNREACHED;
         case 6 : return Type.UNRECIEVED;
         case 7 : return Type.GETREJECTED;
         default: return Type.UNKNOWN;
      }
   }
   
   private void setGenerationListener(){
      
      RandomCallsService.setActivityGenerationListener(activityGenerationListener);
      RandomCallsService.setBinaryMode(true);
   }
   
   private void showProgressIcon(){
   
      Worker.onMain(() -> {
         
         progressIcon.setVisibility(View.VISIBLE);
         
         progressIcon.setScaleX(0);
         progressIcon.setScaleY(0);
         progressIcon.setAlpha(1);
         
         ViewCompat.animate(progressIcon).scaleX(1).scaleY(1).alpha(1).setDuration(300).start();
      }, 400);
      
      
   }
   
   @Override
   protected void onPause(){
      
      super.onPause();
      
      saveStates();
      
      if(randomCallsService != null && RandomCallsService.isRunning()){
         
         randomCallsService.stopBinaryMode();
      }
   }
   
   @Override
   public boolean onSupportNavigateUp(){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public boolean navigateUpTo(Intent upIntent){
      
      onBackPressed();
      return true;
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideUp(this);
      
   }
   
   
   public class ActivityGenerationListener implements GeneratorOperationListener<Call>{
      
      @Override
      public void onBreak(){
         
         Runnable endAction = () -> {
            
            progressIcon.setVisibility(View.GONE);
            editText.setEnabled(true);
            TransitionManager.beginDelayedTransition(progressView);
            progressText.setText("Durduruldu");
         };
         
         
         ViewCompat.animate(progressIcon).scaleX(0).scaleY(0).alpha(0).withEndAction(endAction).setDuration(400).start();
         
      }
      
      @Override
      public void onGenerationComplete(){
         
         progressCounter.setText(u.format("%9d / %d", count, count));
         
         Runnable endAction = () -> {
            
            progressIcon.setVisibility(View.GONE);
            TransitionManager.beginDelayedTransition(progressView);
            progressText.setText("Tamamlandı");
            progressBar.setProgress(count);
         };
         
         ViewCompat.animate(progressIcon).scaleX(0).scaleY(0).alpha(0).withEndAction(endAction).setDuration(300).start();
         
         startButton.setText("Üret");
         editText.setEnabled(true);
         
         randomCallsService.stopService();
         
         CallLogFiltered.setNeedRefresh(true);
      }
      
      @Override
      public void onGenerate(Call result, int progress, int total){
         
         counter(progress);
         setProgressText(String.format("%s%n%s", result.getName() == null ? result.getNumber() : result.getName(), Type.getTypeStr(RandomCallsActivity.this, result.getType())));
         
         progressBar.setProgress(progress);
         randomCallsService.requestValue();
      }
      
      @Override
      public void onConnect(){
   
         Timber.d("Servis bağlantısı sağlandı");
         
         setProgress();
         
         randomCallsService = RandomCallsService.getService();
         
         if(randomCallsService == null){
   
            Timber.d("RandomCallsService alınamadı");
            return;
         }
         
         showProgressIcon();
         setEditText();
         
         randomCallsService.stopForeground(true);
         Worker.onMain(() -> randomCallsService.requestValue(), 1000);
      }
   }
   
}
