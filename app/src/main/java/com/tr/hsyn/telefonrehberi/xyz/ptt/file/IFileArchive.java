package com.tr.hsyn.telefonrehberi.xyz.ptt.file;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tr.hsyn.telefonrehberi.util.Time;
import com.tr.hsyn.telefonrehberi.util.storage.Storage;
import com.tr.hsyn.telefonrehberi.util.storage.helpers.OrderType;
import com.tr.hsyn.telefonrehberi.xyz.ptt.message.IMailMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * <h1>IFileArchive</h1>
 * <p>
 * Dosya yöneticisi
 *
 * @author hsyn 2019-09-12 17:58:20
 * @version 1.0.0
 */
public abstract class IFileArchive{
   
   @NonNull private final File    rootFolder;
   private final          Context context;
   private final          Storage storage;
   
   public IFileArchive(Context context, File rootFolder){
      
      this.context    = context;
      this.rootFolder = rootFolder;
      this.storage    = new Storage(context);
      
      if(!rootFolder.exists()){
         
         if(!rootFolder.mkdir()){
            
            throw new RuntimeException("Dosya sistemine ulaşım yok");
         }
      }
   }
   
   @Nullable
   public File getThisMounthFolder(boolean createIfNotExist){
      
      File file = getFolder(System.currentTimeMillis(), createIfNotExist);
      
      if(file == null) return null;
      
      return file.getParentFile();
   }
   
   /**
    * Verilen zamana göre yıl ay gün olarak iç içe klasör döndürür.
    * <p>
    * Mesela bugün için <br><br>
    * <p>
    * {@code getFolder(System.currentTimeMillis(), true)}<br><br>
    * <p>
    * 2019<br>
    * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;|<br>
    * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--- 09<br>
    * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; |<br>
    * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;--- 12<br><br>
    *
    * <br>
    * <p>
    * sonucunu {@code gün} klasörü (12) için File nesnesi olarak dönecek.
    *
    * @param date             zaman
    * @param createIfNotExist eğer kalsör mevcut değilse ve oluşturulması isteniyorsa {@code true}
    * @return klasör
    */
   @Nullable
   public File getFolder(long date, boolean createIfNotExist){
      
      Time time = new Time(date);
      
      String year   = time.getYear();
      String mounth = time.getMounthValue();
      String day    = time.getDayValue();
      
      File yearFolder = new File(rootFolder, year);
      
      if(!yearFolder.exists()){
         
         if(!createIfNotExist) return null;
         if(!yearFolder.mkdir()) return null;
      }
      
      File mounthFolder = new File(yearFolder, mounth);
      
      if(!mounthFolder.exists()){
         
         if(!createIfNotExist) return null;
         if(!mounthFolder.mkdir()) return null;
      }
      
      File dayFolder = new File(mounthFolder, day);
      
      if(!dayFolder.exists()){
         
         if(!createIfNotExist) return null;
         if(!dayFolder.mkdir()) return null;
      }
      
      return dayFolder;
   }
   
   public static IFileArchive create(Context context, File rootFolder){
      
      return new FileArshive(context, rootFolder);
   }
   
   /**
    * Var olan bir dosyayı bugünün klasörüne kopyala.
    *
    * @param file      eklenecek dosya
    * @param owerwrite dosya zaten varsa ve üzerine yazılacaksa {@code true}
    * @return dosya yoksa veya kopyalama başarısız olursa {@code false}
    */
   public boolean add(File file, boolean owerwrite){
      
      return add(file, owerwrite, false);
   }
   
   /**
    * Var olan bir dosyayı bugünün klasörüne kopyala.
    *
    * @param file             eklenecek dosya
    * @param owerwrite        kopyalanacak yerde aynı isimde bir dosya olaması durumunda üzerine yazılsın mı?
    * @param deleteSourceFile kaynak dosya silinsin mi
    * @return işlem başarılı ise {@code true}
    */
   public boolean add(File file, boolean owerwrite, boolean deleteSourceFile){
      
      if(!file.exists()) return false;
      
      File folder = getTodayFolder();
      
      if(folder == null) return false;
      
      File newFile = new File(folder, file.getName());
      
      if(deleteSourceFile){
         
         return Storage.copy(file, newFile, owerwrite) && file.delete();
      }
      
      return Storage.copy(file, newFile, owerwrite);
   }
   
   @Nullable
   public File getTodayFolder(){
      
      return getFolder(System.currentTimeMillis(), true);
   }
   
   /**
    * Var olan bir dosyayı bugünün klasörüne kopyala.
    *
    * @param file             eklenecek dosya
    * @param owerwrite        kopyalanacak yerde aynı isimde bir dosya olaması durumunda üzerine yazılsın mı?
    * @param deleteSourceFile kaynak dosya silinsin mi
    * @param subDirs          dosya hangi alt klasörlerin içinde olsun? Verilen listedeki her eleman bir klasör ismi olacak
    *                         ve bugünün klasörü altında oluşturulacak. Eklenecek dosya da bu alt klasörlerin her birine kopyalanacak.
    *                         Belirtilen alt klasörler yoksa oluşturulacak.
    *                         
    * @return işlem başarılı ise {@code true}
    */
   public boolean add(File file, boolean owerwrite, boolean deleteSourceFile, List<String> subDirs){
      
      if(!file.exists()) return false;
      
      File todayFolder = getTodayFolder();
      
      if(todayFolder == null) return false;
      
      for(String dir : subDirs){
         
         File _dir = new File(todayFolder, dir);
         
         if(!_dir.exists()){
            
            if(!_dir.mkdir()){
               
               return false;
            }
         }
         
         File newFile = new File(_dir, file.getName());
         Storage.copy(file, newFile, owerwrite);
      }
      
      if(deleteSourceFile){
         
         return file.delete();
      }
      
      return true;
   }
   
   public boolean add(IMailMessage message){
   
      File todayFolder = getTodayFolder();
   
      if(todayFolder == null) return false;
   
      List<String> subDirs = message.getRecipients();
      
      for(String dir : subDirs){
      
         File _dir = new File(todayFolder, dir.substring(0, dir.indexOf('@')));
      
         if(!_dir.exists()){
         
            if(!_dir.mkdir()){
            
               return false;
            }
         }
   
         //noinspection ResultOfMethodCallIgnored
         message.getFile().delete();
   
         File newFile = new File(_dir, message.getFile().getName());
         
         if(!message.saveToXml(newFile)) return false;
      }
      
      return true;
   }
   
   public boolean containsInToday(String fileName){
      
      List<File> files = getTodayFiles();
      
      for(File file : files){
         
         if(fileName.equals(file.getName())) return true;
      }
      
      return false;
   }
   
   @NonNull
   public List<File> getTodayFiles(){
      
      return getFiles(System.currentTimeMillis());
   }
   
   /**
    * Belirtilen zamanın gününe ait klasördeki dosyaları döndür.
    *
    * @param date zaman
    * @return o güne ait dosyalar. Yoksa boş liste.
    */
   @NonNull
   public List<File> getFiles(long date){
      
      File timeFolder = getFolder(date, false);
      
      if(timeFolder == null) return new ArrayList<>(0);
      
      List<File> files = storage.getFiles(timeFolder, OrderType.DATE);
      
      if(files == null) return new ArrayList<>(0);
      
      return files;
   }
   
   /**
    * Bugünün klasörü içinde olacak şekilde verilen isimde yeni bir dosya döndür.
    *
    * @param fileName yeni oluşturulacak dosyanın ismi
    * @return dosya
    */
   @Nullable
   public File createNewFileInToday(String fileName){
      
      return new File(getTodayFolder(true), fileName);
   }
   
   @Nullable
   public File getTodayFolder(boolean createIfNotExist){
      
      return getFolder(System.currentTimeMillis(), createIfNotExist);
   }
   
   @Nullable
   public File getThisYearFolder(){
      
      File file = getFolder(System.currentTimeMillis(), true);
      
      if(file == null) return null;
      
      file = file.getParentFile();
      
      if(file == null) return null;
      
      return file.getParentFile();
   }
   
   @Nullable
   public File getThisMounthFolder(){
      
      File file = getFolder(System.currentTimeMillis(), true);
      
      if(file == null) return null;
      
      return file.getParentFile();
   }
   
}
