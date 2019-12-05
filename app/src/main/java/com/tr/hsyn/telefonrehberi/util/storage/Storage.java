package com.tr.hsyn.telefonrehberi.util.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.tr.hsyn.telefonrehberi.util.storage.helpers.ImmutablePair;
import com.tr.hsyn.telefonrehberi.util.storage.helpers.OrderType;
import com.tr.hsyn.telefonrehberi.util.storage.helpers.SizeUnit;
import com.tr.hsyn.telefonrehberi.util.storage.security.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.crypto.Cipher;


/**
 * Common class for internal and external storage implementations
 *
 * @author Roman Kushnarenko - sromku (sromku@gmail.com)
 */
public class Storage{
   
   private static final String TAG = "Storage";
   
   private final Context              mContext;
   private       EncryptConfiguration mConfiguration;
   
   public Storage(Context context){
      
      mContext = context;
   }
   
   public String getExternalStorageDirectory(String publicDirectory){
      
      return Environment.getExternalStoragePublicDirectory(publicDirectory).getAbsolutePath();
   }
   
   public boolean createDirectory(String path){
      
      File directory = new File(path);
      if(directory.exists()){
         Log.w(TAG, "Directory '" + path + "' already exists");
         return false;
      }
      return directory.mkdirs();
   }
   
   public boolean createDirectory(String path, boolean override){
      
      // Check if directory exists. If yes, then delete all directory
      if(override && isDirectoryExists(path)){
         deleteDirectory(path);
      }
      
      // Create new directory
      return createDirectory(path);
   }
   
   public boolean isDirectoryExists(String path){
      
      return new File(path).exists();
   }
   
   public boolean createFile(String path, String content){
      
      return createFile(path, content.getBytes());
   }
   
   public boolean createFile(String path, byte[] content){
      
      try{
         OutputStream stream = new FileOutputStream(new File(path));
         
         // encrypt if needed
         if(mConfiguration != null && mConfiguration.isEncrypted()){
            content = encrypt(content, Cipher.ENCRYPT_MODE);
         }
         
         stream.write(content);
         stream.flush();
         stream.close();
      }
      catch(IOException e){
         Log.e(TAG, "Failed create file", e);
         return false;
      }
      return true;
   }
   
   /**
    * Encrypt or Descrypt the content. <br>
    *
    * @param content        The content to encrypt or descrypt.
    * @param encryptionMode Use: {@link Cipher#ENCRYPT_MODE} or
    *                       {@link Cipher#DECRYPT_MODE}
    * @return
    */
   private synchronized byte[] encrypt(byte[] content, int encryptionMode){
      
      final byte[] secretKey = mConfiguration.getSecretKey();
      final byte[] ivx       = mConfiguration.getIvParameter();
      return SecurityUtil.encrypt(content, encryptionMode, secretKey, ivx);
   }
   
   public boolean createFile(String path, Storable storable){
      
      return createFile(path, storable.getBytes());
   }
   
   public boolean createFile(String path, Bitmap bitmap){
      
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
      byte[] byteArray = stream.toByteArray();
      return createFile(path, byteArray);
   }
   
   public boolean deleteFile(String path){
      
      File file = new File(path);
      return file.delete();
   }
   
   public String readTextFile(String path){
      
      byte[] bytes = readFile(path);
      return new String(bytes);
   }
   
   public byte[] readFile(String path){
      
      final FileInputStream stream;
      
      try{
         
         stream = new FileInputStream(new File(path));
         return readFile(stream);
      }
      catch(FileNotFoundException e){
         
         Log.e(TAG, "Failed to read file to input stream", e);
         return null;
      }
   }
   
   protected byte[] readFile(final FileInputStream stream){
      
      class Reader extends Thread{
         
         byte[] array = null;
      }
      
      Reader reader = new Reader(){
         
         @Override public void run(){
            
            LinkedList<ImmutablePair<byte[], Integer>> chunks = new LinkedList<ImmutablePair<byte[], Integer>>();
            
            // read the file and build chunks
            int size       = 0;
            int globalSize = 0;
            do{
               try{
                  int chunkSize = mConfiguration != null ? mConfiguration.getChuckSize() : 8192;
                  // read chunk
                  byte[] buffer = new byte[chunkSize];
                  size = stream.read(buffer, 0, chunkSize);
                  if(size > 0){
                     globalSize += size;
                     
                     // add chunk to list
                     chunks.add(new ImmutablePair<byte[], Integer>(buffer, size));
                  }
               }
               catch(Exception e){
                  // very bad
               }
            } while(size > 0);
            
            try{
               stream.close();
            }
            catch(Exception e){
               // very bad
            }
            
            array = new byte[globalSize];
            
            // append all chunks to one array
            int offset = 0;
            for(ImmutablePair<byte[], Integer> chunk : chunks){
               // flush chunk to array
               System.arraycopy(chunk.element1, 0, array, offset, chunk.element2);
               offset += chunk.element2;
            }
         }
         
         ;
      };
      
      reader.start();
      try{
         reader.join();
      }
      catch(InterruptedException e){
         Log.e(TAG, "Failed on reading file from storage while the locking Thread", e);
         return null;
      }
      
      if(mConfiguration != null && mConfiguration.isEncrypted()){
         return encrypt(reader.array, Cipher.DECRYPT_MODE);
      }
      else{
         return reader.array;
      }
   }
   
   public void appendFile(String path, String content){
      
      appendFile(path, content.getBytes());
   }
   
   public void appendFile(String path, byte[] bytes){
      
      if(!isFileExist(path)){
         Log.w(TAG, "Impossible to append content, because such file doesn't exist");
         return;
      }
      
      try{
         FileOutputStream stream = new FileOutputStream(new File(path), true);
         stream.write(bytes);
         stream.write(System.getProperty("line.separator").getBytes());
         stream.flush();
         stream.close();
      }
      catch(IOException e){
         Log.e(TAG, "Failed to append content to file", e);
      }
   }
   
   public boolean isFileExist(String path){
      
      return new File(path).exists();
   }
   
   public List<File> getNestedFiles(String path){
      
      File       file = new File(path);
      List<File> out  = new ArrayList<File>();
      getDirectoryFiles(file, out);
      return out;
   }
   
   public List<File> getFiles(File dir){
      
      return getFiles(dir, OrderType.DATE);
   }
   
   public List<File> getFiles(@NonNull final File dir, OrderType orderType){
      
      File[] files = dir.listFiles();
      
      if(files == null) return null;
      
      List<File> fileList = Arrays.asList(files);
      
      if(files.length < 2) return fileList;
      
      return Stream.of(fileList).sorted(orderType.getComparator()).toList();
   }
   
   public List<File> getFiles(File dir, final String matchRegex){
      
      File[] files;
      
      if(matchRegex != null){
         
         FilenameFilter filter = (dir1, fileName) -> fileName.matches(matchRegex);
         
         files = dir.listFiles(filter);
      }
      else{
         
         files = dir.listFiles();
      }
      return files != null ? Arrays.asList(files) : null;
   }
   
   public boolean rename(String fromPath, String toPath){
      
      File file    = getFile(fromPath);
      File newFile = new File(toPath);
      return file.renameTo(newFile);
   }
   
   public File getFile(String path){
      
      return new File(path);
   }
   
   public double getSize(File file, SizeUnit unit){
      
      long length = file.length();
      return (double) length / (double) unit.inBytes();
   }
   
   public String getReadableSize(File file){
      
      long length = file.length();
      return SizeUnit.readableSizeUnit(length);
   }
   
   public long getFreeSpace(String dir, SizeUnit sizeUnit){
      
      StatFs statFs = new StatFs(dir);
      long   availableBlocks;
      long   blockSize;
      availableBlocks = statFs.getAvailableBlocksLong();
      blockSize       = statFs.getBlockSizeLong();
      long freeBytes = availableBlocks * blockSize;
      return freeBytes / sizeUnit.inBytes();
   }
   
   public long getUsedSpace(String dir, SizeUnit sizeUnit){
      
      StatFs statFs = new StatFs(dir);
      long   availableBlocks;
      long   blockSize;
      long   totalBlocks;
      availableBlocks = statFs.getAvailableBlocksLong();
      blockSize       = statFs.getBlockSizeLong();
      totalBlocks     = statFs.getBlockCountLong();
      long usedBytes = totalBlocks * blockSize - availableBlocks * blockSize;
      return usedBytes / sizeUnit.inBytes();
   }
   
   public boolean move(String fromPath, String toPath){
      
      if(copy(fromPath, toPath)){
         return getFile(fromPath).delete();
      }
      return false;
   }
   
   public boolean copy(String fromPath, String toPath){
      
      File file = getFile(fromPath);
      
      if(!file.isFile()){
         
         return false;
      }
      
      FileInputStream  inStream  = null;
      FileOutputStream outStream = null;
      
      try{
         
         inStream  = new FileInputStream(file);
         outStream = new FileOutputStream(new File(toPath));
         FileChannel inChannel  = inStream.getChannel();
         FileChannel outChannel = outStream.getChannel();
         inChannel.transferTo(0, inChannel.size(), outChannel);
      }
      catch(Exception e){
         
         Log.e(TAG, "Failed copy", e);
         return false;
      }
      finally{
         
         closeSilently(inStream);
         closeSilently(outStream);
      }
      
      return true;
   }
   
   private static void closeSilently(Closeable closeable){
      
      if(closeable != null){
         
         try{ closeable.close(); }
         catch(IOException ignored){ }
      }
   }
   
   public static boolean copy(File from, File to, boolean owerwrite){
      
      if(!from.exists() || !from.isFile()){
         
         return false;
      }
      
      if(to.exists()){
         
         if(!owerwrite) return false;
         
         if(!to.delete()) return false;
      }
      
      FileInputStream  inStream  = null;
      FileOutputStream outStream = null;
      
      try{
         
         inStream  = new FileInputStream(from);
         outStream = new FileOutputStream(to);
         FileChannel inChannel  = inStream.getChannel();
         FileChannel outChannel = outStream.getChannel();
         inChannel.transferTo(0, inChannel.size(), outChannel);
      }
      catch(Exception e){
         
         Log.e(TAG, "Failed copy", e);
         return false;
      }
      finally{
         
         closeSilently(inStream);
         closeSilently(outStream);
      }
      return true;
   }
   
   /**
    * Delete the directory and all sub content.
    *
    * @param path The absolute directory path. For example:
    *             <i>mnt/sdcard/NewFolder/</i>.
    * @return <code>True</code> if the directory was deleted, otherwise return
    * <code>False</code>
    */
   @SuppressWarnings("ResultOfMethodCallIgnored")
   private boolean deleteDirectory(String path){
      
      File directory = new File(path);
      
      // If the directory exists then delete
      if(directory.exists()){
         
         File[] files = directory.listFiles();
         
         if(files == null){
            
            return true;
         }
         // Run on all sub files and folders and delete them
         for(File file : files){
            
            if(file.isDirectory()){
               
               deleteDirectory(file.getAbsolutePath());
            }
            else{
               
               file.delete();
            }
         }
      }
      return directory.delete();
   }
   
   /**
    * Klasördeki ve alt klasörlerdeki tüm dosyaları ver.
    *
    * @param directory klasör
    * @param out       dosyaların verileceği liste
    */
   private void getDirectoryFiles(File directory, List<File> out){
      
      if(directory.exists()){
         
         File[] files = directory.listFiles();
         
         if(files != null){
            
            for(File file : files){
               
               if(file.isDirectory()){
                  
                  getDirectoryFiles(file, out);
               }
               else{
                  
                  out.add(file);
               }
            }
         }
      }
   }
   
   public String getExternalStorageDirectory(){
      
      return Environment.getExternalStorageDirectory().getAbsolutePath();
   }
   
   public String getInternalRootDirectory(){
      
      return Environment.getRootDirectory().getAbsolutePath();
   }
   
   public String getInternalFilesDirectory(){
      
      return mContext.getFilesDir().getAbsolutePath();
   }
   
   public String getInternalCacheDirectory(){
      
      return mContext.getCacheDir().getAbsolutePath();
   }
   
   public static boolean isExternalWritable(){
      
      String state = Environment.getExternalStorageState();
      return Environment.MEDIA_MOUNTED.equals(state);
   }
   
   public void setEncryptConfiguration(EncryptConfiguration configuration){
      
      mConfiguration = configuration;
   }
}
