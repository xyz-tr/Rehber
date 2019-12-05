package com.tr.hsyn.telefonrehberi.util.dialog;

import android.graphics.drawable.Drawable;

import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.SelectDialogListener;


/**
 * <h1>ISelectDialog</h1>
 * 
 * <p>
 *    Listeden eleman seçmek için gösterilecek olan görselin uyması gereken sözleşme.
 * 
 * @author hsyn 2019-12-03 13:54:43
 */
public interface ISelectDialog{
   
   void build();
   
   ISelectDialog setTitle(CharSequence title);
   
   ISelectDialog setItems(CharSequence[] items);
   
   ISelectDialog setListener(SelectDialogListener listener);
   
   ISelectDialog setCancelable(boolean cancelable);
   
   void setAnimationStyle(int animationStyle);
   
   ISelectDialog setTitleIcon(Drawable titleIcon);
}
