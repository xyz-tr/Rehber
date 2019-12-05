package com.tr.hsyn.telefonrehberi.util;



import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.xyz.contact.activity.detail.ContactDetailsSocial;


public class SocialAccountInfo{
   
   ContactDetailsSocial.SocialType socialType;
   private String                    number;
   private String                    chatDataId;
   private String                    voiceDataId;
   private String                    videoDataId;
   
   
   public String getNumber(){
      
      return number;
   }
   
   public void setNumber(String number){
      
      this.number = number;
   }
   
   public String getChatDataId(){
      
      return chatDataId;
   }
   
   public void setChatDataId(String chatDataId){
      
      this.chatDataId = chatDataId;
   }
   
   public String getVoiceDataId(){
      
      return voiceDataId;
   }
   
   public void setVoiceDataId(String voiceDataId){
      
      this.voiceDataId = voiceDataId;
   }
   
   public String getVideoDataId(){
      
      return videoDataId;
   }
   
   public void setVideoDataId(String videoDataId){
      
      this.videoDataId = videoDataId;
   }
   
   @NonNull
   @Override
   public String toString(){
      
      return u.format(
            
            "number  : %s%n" +
            "chat   : %s%n" +
            "voice  : %s%n" +
            "video  : %s",
            
            number, chatDataId, voiceDataId, videoDataId
      
      );
   }
   
   public ContactDetailsSocial.SocialType getSocialType(){
      
      return socialType;
   }
   
   public void setSocialType(ContactDetailsSocial.SocialType socialType){
      
      this.socialType = socialType;
   }
}
