package com.tr.hsyn.telefonrehberi.xyz.ptt.mail;

import android.content.Context;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.util.Arrays;


/**
 * <h1>GmailService</h1>
 * <p>
 * GmailService is a account
 * <p>
 * Created by hsyn on 2017-06-07 10:43:52
 *
 * @version 1.0.0
 */
public class GmailService extends Account{
   
   private static Gmail mService;
   
   private GmailService(Context context){
      
      super(context);
      setupService();
   }
   
   private void setupService(){
      
      String[] SCOPES = {GmailScopes.MAIL_GOOGLE_COM};
      
      GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(context.getApplicationContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());
      mCredential.setSelectedAccountName(getAccount());
      
      HttpTransport transport = new NetHttpTransport();
      //HttpTransport transport   = AndroidHttp.newCompatibleTransport();
      JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      
      mService = new Gmail.Builder(transport, jsonFactory, mCredential).setApplicationName("Gmail").build();
      
   }
   
   public static Gmail getGmailService(Context context){
      
      if(mService == null){
         
         synchronized(GmailService.class){
            
            if(mService == null){
               
               new GmailService(context);
            }
         }
      }
      
      return mService;
   }
   
}
