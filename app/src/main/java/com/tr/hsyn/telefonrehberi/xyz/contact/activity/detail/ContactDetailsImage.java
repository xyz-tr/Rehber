package com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.tr.hsyn.telefonrehberi.xyz.contact.Contacts;
import com.tr.hsyn.telefonrehberi.xyz.contact.adapter.ContactAdapter;

import lombok.val;


public abstract class ContactDetailsImage extends ContactDetailsNumbers{
   
   
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      setupImage();
   }
   
   /**
    * Resmi ayarla
    */
   private void setupImage(){
      
      image.setOnClickListener(this::onImageClick);
      val imageUri = Contacts.getImageUri(this, contact.getContactId());
      
      if(imageUri == null){
         
         Drawable drawable;
         
         drawable = TextDrawable.builder()
                                .buildRect(ContactAdapter.getLetter(contact.getName()), getPrimaryColor());
         
         image.setImageDrawable(drawable);
      }
      else{
         
         image.setImageURI(imageUri);
         collapsingToolbarLayout.setContentScrimColor(getPrimaryColor());
      }
   }
   
   /**
    * Resme tıklandığında.
    *
    * @param view Resim
    */
   private void onImageClick(@SuppressWarnings("unused") View view){
        
        
        /*Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 9);
        */
        
        /*if (image.getHeight() == height) {
            
            image.getLayoutParams().height = u.dpToPx(this, 450);
        }
        else {
            
            image.getLayoutParams().height = u.dpToPx(this, 350);
        }
        
        TransitionManager.beginDelayedTransition(collapsingToolbarLayout);
        
        view.requestLayout();*/
   }
}
