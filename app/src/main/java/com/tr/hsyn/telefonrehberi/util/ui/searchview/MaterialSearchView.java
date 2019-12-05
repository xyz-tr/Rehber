package com.tr.hsyn.telefonrehberi.util.ui.searchview;

import android.content.Context;
import android.util.AttributeSet;

import com.tr.hsyn.telefonrehberi.R;


public class MaterialSearchView extends BaseMaterialSearchView{
   
   public MaterialSearchView(Context context){
      
      super(context);
   }
   
   public MaterialSearchView(Context context, AttributeSet attrs){
      
      super(context, attrs);
   }
   
   public MaterialSearchView(Context context, AttributeSet attrs, int defStyleAttr){
      
      super(context, attrs, defStyleAttr);
   }
   
   @Override
   public void showSearch(boolean animate){
      
      super.showSearch(animate);
      
      this.showKeyboard(mETSearchText);
   }
   
   @Override
   protected int getLayoutId(){
      
      return R.layout.msv_simple;
   }
}
