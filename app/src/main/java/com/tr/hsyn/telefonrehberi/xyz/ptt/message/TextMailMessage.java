package com.tr.hsyn.telefonrehberi.xyz.ptt.message;

import org.simpleframework.xml.Root;

import java.util.List;

import lombok.ToString;


@ToString(callSuper = true)
@Root(name = "text_mail_message")
public class TextMailMessage extends ITextMailMessage{
   
   TextMailMessage(){}
   
   TextMailMessage(String text, List<String> recipients){
      
      this.text       = text;
      this.recipients = recipients;
      messageType     = MessageType.TEXT;
      subject         = messageType.toString();
   }
   
   TextMailMessage(ITextMailMessage mailMessage){
      
      this.text       = mailMessage.text;
      this.recipients = mailMessage.recipients;
      messageType     = mailMessage.messageType;
      subject         = mailMessage.subject;
   }
   
   
   
}
