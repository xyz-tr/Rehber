package com.tr.hsyn.telefonrehberi.util;



import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.tr.hsyn.telefonrehberi.util.Listx.indexOf;


public class Adapterx{
   
   public static <T> boolean remove(RecyclerView.Adapter adapter, List<T> adapterData, T removeItem){
      
      int index = indexOf(adapterData, removeItem);
      
      if(index == -1){ return false; }
      
      adapterData.remove(index);
      adapter.notifyItemRemoved(index);
      return true;
   }
}
