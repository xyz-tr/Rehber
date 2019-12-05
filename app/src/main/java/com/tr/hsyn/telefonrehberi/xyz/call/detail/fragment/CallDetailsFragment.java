package com.tr.hsyn.telefonrehberi.xyz.call.detail.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.concurrent.Worker;
import com.tr.hsyn.telefonrehberi.util.event.EventReCommentCall;
import com.tr.hsyn.telefonrehberi.xyz.call.Call;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.actor.ISpeaker;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.actor.Speaker;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.listener.CommentListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

import timber.log.Timber;


/**
 * <h1>CallDetailsFragment</h1>
 * <p>
 * Arama kaydının detayları iki parçadan oluşuyor.
 * Biri, arama kaydının bilgileri,
 * diğeri arama kaydının yorumları.
 * Bu fragment yorum kısmını kapsıyor.
 *
 * @author hsyn
 * @date 01-04-2019
 */
@SuppressLint("ParcelCreator")
public class CallDetailsFragment extends Fragment implements CommentListener{
   
   public         boolean         isComment;
   private        Call            call;
   private        TextView        comment;
   private        CommentListener crossCommentListener;
   private        ViewGroup       container;
   private        LayoutInflater  inflater;
   private static ISpeaker        speaker = new Speaker();
   
   @Override
   public final void onCreate(@Nullable Bundle savedInstanceState){
      
      super.onCreate(savedInstanceState);
      
      if(getArguments() == null){ return; }
      
      call                 = getArguments().getParcelable("call");
      crossCommentListener = getArguments().getParcelable("commentListener");
      
      if(crossCommentListener == null){
         
         Timber.w("crossCommentListener null");
         return;
      }
      
      if(call == null){
         
         Timber.w("Argüman alınamadı : call");
      }
      
      EventBus.getDefault().register(this);
   }
   
   @Override
   public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
      
      this.inflater  = inflater;
      this.container = container;
      comment        = (TextView) inflater.inflate(R.layout.call_details_details, container, false);
      
      comment.setMovementMethod(LinkMovementMethod.getInstance());
   
      if(call != null){
      
         writeComment();
      }
      
      return comment;
   }
   
   private void writeComment(){
      
      Worker.onBackground(this::commentateCall, "CallDetailsFragment:Yorum yapma")
            .whenCompleteAsync(this::onWorkResult, Worker.getMainThreadExecutor());
   }
   
   private CharSequence commentateCall(){
   
      return speaker.commentateCall(call, new WeakReference<>(getActivity()));
   }
   
   private void onWorkResult(CharSequence result, Throwable throwable){
      
      if(comment == null) comment = getCommentTextView();
      
      comment.setText(result);
      
      isComment = true;
      crossCommentListener.onComment(call.getDate());
   }
   
   private TextView getCommentTextView(){
      
      return (TextView) inflater.inflate(R.layout.call_details_details, container, false);
   }
   
   @Override
   public void onStop(){
      
      EventBus.getDefault().unregister(this);
      //container.removeView(comment);
      comment = null;
      super.onStop();
      isComment = false;
   }
   
   @Override
   public boolean onComment(long id){
      
      if(call == null) return false;
      
      if(call.getDate() == id){
         
         return isComment;
      }
      
      return false;
   }
   
   @Override
   public int describeContents(){
      
      return 0;
   }
   
   @Override
   public void writeToParcel(Parcel dest, int flags){
      
   }
   
   public static CallDetailsFragment getInstance(@NonNull final Parcelable phoneCall, Parcelable commentListener){
      
      CallDetailsFragment fragment = new CallDetailsFragment();
      
      Bundle bundle = new Bundle();
      bundle.putParcelable("call", phoneCall);
      bundle.putParcelable("commentListener", commentListener);
      
      fragment.setArguments(bundle);
      
      return fragment;
   }
   
   @Subscribe
   public void onRecomment(EventReCommentCall event){
      
      writeComment();
   }
   
   
}
