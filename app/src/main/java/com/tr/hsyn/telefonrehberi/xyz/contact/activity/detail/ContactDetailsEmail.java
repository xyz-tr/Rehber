package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.Email;
import com.tr.hsyn.telefonrehberi.xyz.main.MainActivity;

import lombok.val;
import timber.log.Timber;


public abstract class ContactDetailsEmail extends ContactDetailsRingtone{
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setupEmails();
   }
   
   private void setupEmails(){
      
      Worker.onBackground(() -> {
         
         val emails = Contacts.getEmails(this, contact.getContactId());
         
         if(emails == null){
            
            return;
         }
         
         contact.setEmails(emails);
         
         Worker.onMain(() -> {
            
            ViewGroup emailsLayout = findViewById(R.id.emails);
            
            emailsLayout.setVisibility(View.VISIBLE);
            
            int i = 1;
            
            for(val email : emails){
               
               View emailItem = getLayoutInflater().inflate(R.layout.email_item, emailsLayout, false);
               
               TextView  emailText      = emailItem.findViewById(R.id.email);
               TextView  emailType      = emailItem.findViewById(R.id.email_type);
               ImageView icon           = emailItem.findViewById(R.id.email_icon);
               View      emailContainer = emailItem.findViewById(R.id.email_container);
               
               emailItem.setTag(email.getEmail());
               emailItem.setOnClickListener(this::onClickEmail);
               
               if(i > 1){
                  
                  RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) emailContainer.getLayoutParams();
                  icon.setVisibility(View.GONE);
                  layoutParams.leftMargin = u.dpToPx(this, 64);
                  emailContainer.setLayoutParams(layoutParams);
                  
               }
               else{
                  
                  u.setTintDrawable(icon.getDrawable(), u.lighter(getPrimaryColor(), .3F));
                  
               }
               
               emailItem.setBackgroundResource(MainActivity.getWellRipple());
               emailText.setText(email.getEmail());
               emailType.setText(Email.getTypeString(email.getType()));
               
               emailsLayout.addView(emailItem);
               
               i++;
            }
         });
      }, "ContactDetailsBase:Kişiye ait mail adreslerini alma - " + (contact.getName() != null ? contact.getName() : contact.getNumber()));
   }
   
   private void onClickEmail(View view){
      
      String email = (String) view.getTag();
      
      Timber.d("Email clicked : %s", email);
      
      sendEmailIntent(email);
      
   }
   
   private void sendEmailIntent(String email){
      
      val    uri = Uri.parse("mailto:" + email).buildUpon().build();
      Intent i   = new Intent(Intent.ACTION_SENDTO, uri);
      startActivity(Intent.createChooser(i, ""));
   }
}
