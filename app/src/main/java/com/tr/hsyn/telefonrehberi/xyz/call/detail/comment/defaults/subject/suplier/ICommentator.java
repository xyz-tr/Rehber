package com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.subject.suplier;

import android.app.Activity;

import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.comment.defaults.commentary.IComment;
import com.tr.hsyn.telefonrehberi.xyz.call.history.Historicall;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.MostCallListDialog;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;

import java.lang.ref.WeakReference;
import java.util.List;

//@off
/**
 * <h1>ICommentator</h1>
 * 
 * <p>
 *    Yorumcu sözleşmesi.
 * 
 * @author hsyn 2019-12-02 14:56:08
 */
//@on
public interface ICommentator{
   
   /**
    * Arama kayıtlarını dialog ile göster.
    *
    * @param title      dialog başlığı
    * @param phoneCalls gösterilecek arama kayıtları
    */
   void showList(CharSequence title, List<ICall> phoneCalls);
   
   /**
    * Arama kayıtlarını göster.
    *
    * @param title      başlık
    * @param phoneCalls gösterilecek arama kayıtları
    * @param phoneCall  yorumlamakta olan arama kaydı, vurgu yapmak için
    */
   void showList(CharSequence title, List<ICall> phoneCalls, ICall phoneCall);
   
   /**
    * Tek bir arama kaydını dialog ile göster.
    *
    * @param phoneCall gösterilecek kayıt
    */
   void showCall(ICall phoneCall);
   
   long getRingingDuration();
   
   long getSpeakingDuration();
   
   Historicall<ICall> getHistory();
   
   ICall getCall();
   
   WeakReference<Activity> getActivityReference();
   
   boolean isDialogOpen();
   
   void setDialogOpen(boolean isOpen);
   
   IComment getCommentStore();
   
   CallStory getCallStory();
   
   String getCallName();
   
   MostCallListDialog getMostCallListDialog();
   
   void setMostCallListDialog(MostCallListDialog dialog);
   
   DialogListener getDialogListener();
}
