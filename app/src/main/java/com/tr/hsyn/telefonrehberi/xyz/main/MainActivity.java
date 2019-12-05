package com.tr.hsyn.telefonrehberi.xyz.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.CallNumberRequest;
import com.tr.hsyn.telefonrehberi.util.event.ColorChanged;
import com.tr.hsyn.telefonrehberi.util.event.ContactAdded;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.util.ui.htext.base.HTextView;
import com.tr.hsyn.telefonrehberi.util.ui.snack.SnackBarListener;
import com.tr.hsyn.telefonrehberi.xyz.call.main.fragment.CallLog;
import com.tr.hsyn.telefonrehberi.xyz.contact.fragment.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ColorController;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.GlobalMessageController;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ObjectStarter;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.RegisterController;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.ScreenSizeController;
import com.tr.hsyn.telefonrehberi.xyz.main.controller.TitleController;
import com.tr.hsyn.telefonrehberi.xyz.main.handler.ActionAndScrollHandler;
import com.tr.hsyn.telefonrehberi.xyz.main.page.PageAdapter;
import com.tr.hsyn.telefonrehberi.xyz.main.page.PageChangeListener;
import com.tr.hsyn.telefonrehberi.xyz.main.page.ShowMe;
import com.tr.hsyn.telefonrehberi.xyz.main.page.ShowTimer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.function.IntConsumer;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.val;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * <h1>MainActivity</h1>
 * <p>
 * Uygulamanın başlangıç noktası.
 * Activity'de sadece iki sayfadan oluşan bir viewPager var.
 * Sayfanın biri Rehber, diğeri arama kayıtları.
 */
public class MainActivity extends AppCompatActivity implements SnackBarListener{
   
   
   //region Fields...
   
   
   @Getter @BindView(R.id.floatingActionButton) FloatingActionButton actionButton;
   @BindView(R.id.viewPager)                    ViewPager            viewPager;
   @BindView(R.id.toolbar)                      Toolbar              toolbar;
   @BindView(R.id.appbar)                       AppBarLayout         appbar;
   @BindView(R.id.title)                        HTextView            title;
   @BindView(R.id.sub_title)                    HTextView            sub_title;
   
   private       Contacts                contactsFragment;
   private       CallLog                 callLogFragment;
   private       GlobalMessageController globalMessageController;
   private       ColorController         colorController;
   private       ScreenSizeController    screenSizeController;
   private       RegisterController      registerController;
   private       ActionAndScrollHandler  actionAndScrollHandler;
   private       String                  numberToCall;
   private final IntConsumer             animationConsumer = i -> {
      
      appbar.setBackgroundColor(i);
      toolbar.setBackgroundColor(i);
      actionButton.setSupportBackgroundTintList(ColorStateList.valueOf(i));
   };
   /**
    * 'Bağlantılar' menüsü için id numarası
    */
   private static final int                     MENU_RELATIONS    = 88;
   private static final int                     RC_CALL           = 55;
   //endregion fields
   
   @SuppressLint({"CheckResult"})
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      
      ButterKnife.bind(this);
      
      Kickback_MainBox.getInstance().setLogFile(new File(getFilesDir(), "log_file.txt"));
      Logger.w("Debug mode started : %s", Time.whatTimeIsIt());
      
      colorController         = new ColorController(this);
      screenSizeController    = new ScreenSizeController(this);
      registerController      = new RegisterController(this);
      globalMessageController = new GlobalMessageController(this);
      
      EventBus.getDefault().register(this);
      setupViews(savedInstanceState);
      
      onCreated();
   }
   
   private void onCreated(){
      
      globalMessageController.setSnackBarListener(actionAndScrollHandler);
      globalMessageController.checkMessages();
      
      Worker.onBackground(registerController::isRegistered, "MainActivity:Giriş kontrolü yapma", true);
      
      test();
   }
   
   /**
    * Deneme amaçlı kullanılan kodlar buraya
    */
   private void test(){
      
   
      
      
  /*    Worker.onMain(() -> {
         
         val recipients = IRecipients.create().getRecipients(true);
   
         if(recipients == null || recipients.size() == 0){
      
            u.log.d("Alıcı yok");
            return;
         }
         
         for(int i = 0; i < 10; i++){
            
            IPostMan.callSigleton(this).postText("mailMessage - " + i, recipients);
         }
         
      },3000);*/
  
      /*startActivity(new Intent(this, PttActivity.class));
      Bungee.card(this);*/
   }
   
   @Override
   protected void onDestroy(){
      
      EventBus.getDefault().unregister(this);
      Worker.shutDown();
      
      setSupportActionBar(null);
      toolbar = null;
      super.onDestroy();
      EventBus.clearCaches();
      Logger.w("Hikaye burada bitiyor");
   }
   
   @Override
   protected void onSaveInstanceState(@NonNull Bundle outState){
      
      super.onSaveInstanceState(outState);
      
      getSupportFragmentManager().putFragment(outState, "callLogFragment", callLogFragment);
      getSupportFragmentManager().putFragment(outState, "contactsFragment", contactsFragment);
   }
   
   private void setupViews(@Nullable Bundle savedInstanceState){
      
      final float factor = .9f;
      getWindow().setStatusBarColor(u.darken(u.getPrimaryColor(this), factor));
      setSupportActionBar(toolbar);
      actionButton.setOnClickListener(this::onActionClick);
      
      assert null != getSupportActionBar();
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      
      TabLayout tabLayout = findViewById(R.id.tabs);
      viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
      tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
      
      int color = ColorController.getPrimaryColor().getValue();
      
      appbar.setBackgroundColor(color);
      toolbar.setBackgroundColor(color);
      actionButton.setSupportBackgroundTintList(ColorStateList.valueOf(color));
      
      //*** hello
      //******************************************************************************************
      
      
      if(savedInstanceState == null){
         
         contactsFragment = new Contacts();
         callLogFragment  = new CallLog();
      }
      else{
         
         contactsFragment = (Contacts) getSupportFragmentManager().getFragment(savedInstanceState, "contactsFragment");
         callLogFragment  = (CallLog) getSupportFragmentManager().getFragment(savedInstanceState, "callLogFragment");
      }
      
      
      final Fragment[] fragments   = {contactsFragment, callLogFragment};
      ShowTimer        showTimer   = new ShowTimer(new ShowMe[]{contactsFragment, callLogFragment});
      PageAdapter      pageAdapter = new PageAdapter(getSupportFragmentManager(), fragments);
      viewPager.setAdapter(pageAdapter);
      
      actionAndScrollHandler = new ActionAndScrollHandler(actionButton, new PageChangeListener[]{showTimer::showTime}, viewPager);
      
      TitleController titleController = new TitleController(title, sub_title);
      
      contactsFragment.addFastScrollListener(actionAndScrollHandler);
      callLogFragment.setTitleController(titleController);
      contactsFragment.setTitleController(titleController);
      
      
   }
   
   private void onActionClick(final View view){
      
      Throwable throwable = com.tr.hsyn.telefonrehberi.xyz.contact.Contacts.openNewContactActivity(this, MainConsts.REQUEST_CODE_ADD_CONTACT);
      
      if(null != throwable){
         
         Logger.w(throwable.toString());
      }
      
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      Bungee.slideUp(this);
   }
   
   @Override
   public void onWindowFocusChanged(boolean hasFocus){
      
      super.onWindowFocusChanged(hasFocus);
      
      if(null != screenSizeController){
         
         screenSizeController.checkScreenSize();
      }
   }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu){
      
      // menu.clear();
      getMenuInflater().inflate(R.menu.main_activity_menu, menu);
      return true;
   }
   
   @Override
   public boolean onPrepareOptionsMenu(Menu menu){
      
      if(registerController.isSign()){
         
         if(null == menu.findItem(MENU_RELATIONS)){
            
            menu.add(Menu.NONE, MENU_RELATIONS, Menu.CATEGORY_SECONDARY, "Bağlantılar");
         }
         
         val item = menu.findItem(R.id.main_activity_sign);
         
         if(null != item){
            
            menu.removeItem(item.getItemId());
         }
      }
      
      return super.onPrepareOptionsMenu(menu);
   }
   
   @Override
   public boolean onOptionsItemSelected(@NonNull MenuItem item){
      
      switch(item.getItemId()){
         
         case R.id.menu_change_theme:
            
            colorController.openChangeColorDialog(this);
            return true;
         
         
         case R.id.main_activity_sign:
            
            ObjectStarter.startSignActivity(this);
            return true;
         
         
         case R.id.activity_main_background_works:
            
            ObjectStarter.startBackgroundWorksActivity(this);
            return true;
         
         case MENU_RELATIONS:
            
            ObjectStarter.startRelationsActivity(this);
            return true;
      }
      
      return false;
   }
   
   @Override
   public void onSnackBarStarted(Object object){
      
      actionAndScrollHandler.onSnackBarStarted(object);
   }
   
   @Override
   public void onSnackBarFinished(Object object, boolean actionPressed){
      
      actionAndScrollHandler.onSnackBarFinished(object, actionPressed);
   }
   
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      if(null == data) return;
      
      if(MainConsts.REQUEST_CODE_ADD_CONTACT == requestCode){
         
         if(Activity.RESULT_OK == resultCode){
            
            if(null != data.getData())
               EventBus.getDefault().post(new ContactAdded(data.getData()));
            
         }
      }
      
   }
   
   @Override
   protected void onResume(){
      
      super.onResume();
      
      if(null != actionAndScrollHandler){
         actionAndScrollHandler.showActionButton();
      }
      
      if(null != globalMessageController) globalMessageController.checkMessages();
   }
   
   @Override
   public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
   }
   
   @Subscribe
   public void callRequest(CallNumberRequest event){
      
      if(EasyPermissions.hasPermissions(this, Manifest.permission.CALL_PHONE)){
         
         Phone.makeCall(this, event.getNumber());
      }
      else{
         
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            
            numberToCall = event.getNumber();
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, RC_CALL);
         }
      }
   }
   
   @Subscribe
   public void onChangeColor(ColorChanged event){
      
      ColorController.runColorAnimation(animationConsumer);
      ColorController.changeStatusBarColor(this);
   }
   
   @AfterPermissionGranted(RC_CALL)
   private void call(){
      
      String number = numberToCall;
      
      numberToCall = null;
      
      if(number != null){
         
         Phone.makeCall(this, number);
      }
   }
   
   public static int getWellRipple(){
      
      return ColorController.getWellRipple();
   }
   
   
}
