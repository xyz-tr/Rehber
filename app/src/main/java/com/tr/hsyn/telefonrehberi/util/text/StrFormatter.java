package com.tr.hsyn.telefonrehberi.util.text;

import java.util.HashMap;
import java.util.Map;


/**
 * <h1>StrFormatter</h1>
 * Format nesnesi.
 * Bir string'i verilen değişkenlerle formatlamak için.
 * 
 * 
 * <pre>val value = Stringx.from("%d. name='$name', number='%s', type='$type', date='%s'%n")
 *                                   .arg(1, i)
 *                                   .key("name", name)
 *                                   .arg(2, noted.getNumber())
 *                                   .key("type", Type.getTypeStr(noted.getType()))
 *                                   .arg(3, Time.getDate(noted.getDate()))
 *                                   .format();
 * </pre>
 * 
 *
 * @author hsyn 2019-11-27 12:30:43
 */
class StrFormatter implements IFormatter{
   
   private       String               str;
   private final Map<Integer, Object> args = new HashMap<>();
   
   /**
    * Kurucu.
    * 
    * @param str formatlanacak string
    */
   StrFormatter(String str){
      
      this.str = str;
   }
   
   @Override
   public CharSequence format(){
      
      return Stringx.format(str, getArgs());
   }
   
   @Override
   public IFormatter arg(int order, Object obj){
      
      args.putIfAbsent(order, obj);
      
      return this;
   }
   
   @Override
   public IFormatter key(String key, Object obj){
      
      str = str.replaceAll("\\$" + key, obj.toString());
      
      return this;
   }
   
   private Object[] getArgs(){
      
      final int      size    = args.size();
      final Object[] objects = new Object[size];
      
      for(int i = 0; i < size; i++){ objects[i] = args.get(i + 1); }
      
      return objects;
   }
   
   
}
