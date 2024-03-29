/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tr.hsyn.telefonrehberi.util.crypto.secure.vault;

import android.os.ParcelFileDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Utils{
   
   private Utils(){
      //utility class
   }
   
   public static void closeQuietly(Closeable closable){
      
      if(closable != null){
         try{
            closable.close();
         }
         catch(IOException ignored){
         }
      }
   }
   
   public static void closeWithErrorQuietly(ParcelFileDescriptor pfd, String msg){
      
      if(pfd != null){
         try{
            pfd.closeWithError(msg);
         }
         catch(IOException ignored){
         }
      }
   }
   
   public static void writeFully(File file, byte[] data) throws IOException{
      
      try(OutputStream out = new FileOutputStream(file)){
         out.write(data);
      }
   }
   
   public static byte[] readFully(File file) throws IOException{
      
      try(InputStream in = new FileInputStream(file)){
          
         ByteArrayOutputStream bytes  = new ByteArrayOutputStream();
         byte[]                buffer = new byte[1024];
         int                   count;
         while((count = in.read(buffer)) != -1){
            bytes.write(buffer, 0, count);
         }
         return bytes.toByteArray();
      }
   }
}