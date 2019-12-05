package com.tr.hsyn.telefonrehberi.xyz.ptt.message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.orhanobut.logger.Logger;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.io.File;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;


/**
 * <h1>IMailMessage</h1>
 * <p>
 * Mail yoluyla gönderilecek her mesajın temel sınıfı.
 * 
 *
 * @author hsyn 2019-09-09 11:54:37
 */
@ToString
@Root(name = "mail_message")
public abstract class IMailMessage{
   
   /**
    * Mesajın id'si
    */
   @Getter @Element(name = "id")
   protected String id;
   
   /**
    * Bu mesajın kayıtlı olduğu dosyanın tam yolu
    */
   @Element(name = "file")
   protected String file;
   
   /**
    * Konu.
    * Buraya mesajın türü yazılacak
    */
   @Getter @Setter @Element(name = "subject")
   protected String subject;
   
   /**
    * Mail içeriği.
    * Burası genelde boş geçilecek çünkü herşey zaten dosyada
    */
   @Getter @Setter @Element(name = "body", required = false)
   protected String body;
   
   /**
    * Gönderen
    */
   @Getter @Setter @Element(name = "sender")
   protected String sender;
   
   /**
    * Mesajın alıcıları
    */
   @Getter @Setter @ElementList(name = "recipients")
   protected List<String> recipients;
   
   /**
    * Mesajın ilk oluşturulma zamanı
    */
   @Getter @Setter @Element(name = "create_time")
   protected long createTime;
   
   /**
    * Mesajın başarılı bir şekilde gönderildiği tarih
    */
   @Getter @Setter @Element(name = "sent_time", required = false)
   protected long sentTime;
   
   /**
    * Mesajın alındığı zaman
    */
   @Getter @Setter @Element(name = "recieve_time", required = false)
   protected long recieveTime;
   
   /**
    * Mail'in gönderilmesi ile gerçek mail id'si buraya yazılacak. Gmail mail'leri bu id ile takip ediyor.
    */
   @Getter @Setter @Element(name = "mail_id", required = false)
   protected String mailId;
   
   /**
    * Gönderim kaç kez başarısız olmuş ve tekrar denenmiş
    */
   @Getter @Setter @Element(name = "try_count", required = false)
   protected int tryCount;
   
   /**
    * Mesajın türü.
    * Mail gönderilirken mail'in subject alanına yazılacak ve
    * bu şekilde her mail'in türü, alındığı anda bilinecek.
    */
   @Getter @Setter @Element(name = "message_type")
   protected MessageType messageType;
   
   /**
    * Mesajı dosyaya kaydet.
    * 
    * @param message kaydedilecek mesaj
    * @param file kayıt dosyası
    * @return kayıt başarılı ise {@code true}
    */
   public static boolean saveToXml(IMailMessage message, File file){
      
      return message.saveToXml(file);
   }
   
   /**
    * Tek tür mesaj olmayacağı için bu sınıfı genişleten sınıfların her biri dosyaya kaydın nasıl yapılacağını bu metedla tanımlayacak.
    * 
    * @param file nesnenin kaydedileceği dosya
    * @return kayıt başarılı ise {@code true}
    */
   abstract public boolean saveToXml(File file);
   
   /**
    * Dosyayı mesajı döndür.
    *
    * @param file mesaja döndürülecek dosya
    * @return dönüştürme başarısız olursa {@code null}.
    */
   @Nullable
   public static IMailMessage createFromXml(File file){
      
      if(file == null) return null;
      
      val type = getMessageType(file);
      
      if(type == null){
   
         Logger.w("Dosyadan mesaj türü alınamadı");
         return null;
      }
      
      return createFromXml(type, file);
   }
   
   /**
    * Dosya ismindeki mesaj türünü ver.
    *
    * @param file dosya
    * @return mesaj türü
    */
   @Nullable
   private static MessageType getMessageType(File file){
      
      if(file == null) return null;
      
      val name = file.getName().substring(0, file.getName().lastIndexOf('.'));
      
      try{
         
         return MessageType.valueOf(name.split("_")[1]);
      }
      catch(IllegalArgumentException e){
         
         e.printStackTrace();
         Logger.w("Mesaj türü alınamadı");
      }
      
      return null;
   }
   
   /**
    * Mesaj türüne göre dosyadaki nesneyi oluştur.
    *
    * @param messageType mesaj türü
    * @param file        dosya
    * @return mesaj nesnesi
    */
   @Nullable
   private static IMailMessage createFromXml(@NonNull MessageType messageType, @NonNull File file){
      
      switch(messageType){
         
         case TEXT: return ITextMailMessage.createFromFile(file);
         
         case CALL:
         
         default: return null;
      }
   }
   
   public abstract IMailMessage create(IMailMessage mailMessage);
   
   
   /**
    * Bu metod xml dosyasını verecek.
    * Bu xml dosyası mesajın tüm bilgilerini ve içeriğini taşıyan dosya.
    * Mail gönderilirken dosya buraya koyulacak ve {@code subject} alanına mesajın türü yazılacak.
    * Mail alınırken de önce {@code subject} alanına bakılacak ve
    * türüne göre mesaj buradan alınacak.
    * Aslında mesaj türü dosya ismine ekleneceği için dosya isminden de mesajın türü öğrenilebilir.
    * Ancak ikisine de bakarak bir karşılaştırma yapıp türün sağlaması yapılabilir.
    * Dosya ismindeki tür ile {@code subject} alanındaki tür aynı değil ise bir yanlışlık olduğunu düşünebiliriz.
    *
    * @return mesaj dosyası
    */
   public File getFile(){
      
      return new File(this.file);
   }
   
   public void setFile(File file){
      
      this.file = file.getAbsolutePath();
   }
   
   {
      createTime = System.currentTimeMillis();
      id         = UUID.randomUUID().toString();
   }
}
