package com.tr.hsyn.telefonrehberi.xyz.call.detail.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.tr.hsyn.telefonrehberi.R;
import com.tr.hsyn.telefonrehberi.util.spanner.Spanner;
import com.tr.hsyn.telefonrehberi.util.u;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;
import com.tr.hsyn.telefonrehberi.xyz.call.detail.actor.NoteEditor;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogAction;
import com.tr.hsyn.telefonrehberi.xyz.call.main.dialog.listener.DialogListener;

import java.lang.ref.WeakReference;

import lombok.Setter;
import timber.log.Timber;


@SuppressWarnings("ConstantConditions")
public class NoteDialog implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener, DialogAction{
   
   private         EditText                editText;
   private         TextView                deepNoteText;
   private         Button                  write;
   private         ViewGroup               content;
   private         boolean                 textChanged;
   private         WeakReference<Activity> activityWeakReference;
   private         Spanner                 description;
   private         Spanner                 deepNote;
   private         String                  note;
   private         boolean                 isEdit;
   private         ICall                   phoneCall;
   private         TextView                descriptionText;
   private         String                  currentNote;
   private         AlertDialog             dialog;
   private         Runnable                runnable;
   private         NoteEditor              noteEditor;
   private         View                    view;
   private         DialogListener          _dialogCloseListener;
   @Setter private DialogAction            dialogAction;
   
   public NoteDialog(WeakReference<Activity> activityWeakReference, Spanner description, boolean isEdit, Spanner deepNote, ICall phoneCall, NoteEditor noteEditor){
      
      this.activityWeakReference = activityWeakReference;
      this.description           = description;
      this.isEdit                = isEdit;
      this.deepNote              = deepNote;
      this.phoneCall             = phoneCall;
      note                       = phoneCall.getNote();
      this.noteEditor            = noteEditor;
      _dialogCloseListener       = new DialogListener(this);
      
      
   }
   
   @Override
   public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
      
      if(actionId != EditorInfo.IME_ACTION_DONE){ return false; }
      
      String text = editText.getText().toString().trim();
      
      if(text.isEmpty()){
         
         if(note == null){
            
            return true;
         }
         
         Timber.d("Tam burada notu sil");
         //phoneCall.setNote(null);
         setNote();
         return true;
      }
      
      if(note != null && note.equals(text)){
         
         return true;
      }
      
      Timber.d("Tam burada notu kaydet");
      
      //phoneCall.setNote(text);
      setNote();
      return true;
   }
   
   private void setNote(){
      
      if(!textChanged){
         
         Timber.d("Not değişmediği için kayıt işlemi yapılmayacak");
      }
      else{
         
         if(currentNote.trim().isEmpty()){
            
            phoneCall.setNote(null);
         }
         else{
            
            phoneCall.setNote(currentNote);
         }
         
         
         noteEditor.setNote(currentNote);
         
         setTextChanged(false);
         
         onComplete();
      }
      
      dialog.dismiss();
   }
   
   private void setTextChanged(boolean isChange){
      
      write.setEnabled(textChanged = isChange);
      
   }
   
   private void onComplete(){
      
      if(runnable != null){ runnable.run(); }
   }
   
   @Override
   public void onDialogClose(DialogInterface dialogInterface){
      
      if(dialogAction != null) dialogAction.onDialogClose(dialogInterface);
      reset();
   }
   
   private void reset(){
      
      if(dialog != null){
         
         dialog.setView(view = null);
         dialog = null;
      }
      
      if(editText != null){
         
         editText.removeTextChangedListener(this);
         editText.setOnEditorActionListener(null);
         editText = null;
      }
      
      deepNote        = null;
      content         = null;
      runnable        = null;
      noteEditor      = null;
      descriptionText = null;
      deepNoteText    = null;
   }
   
   @Override
   public void onDialogShow(DialogInterface dialogInterface){
      
   }
   
   @Override
   public void beforeTextChanged(CharSequence s, int start, int count, int after){}
   
   @Override
   public void onTextChanged(CharSequence s, int start, int before, int count){
      
      currentNote = editText.getText().toString().trim();
      
      if(note == null){
         
         if(currentNote.isEmpty()){
            
            Timber.d("Not boş");
         }
         else{
            
            setTextChanged(true);
         }
      }
      else{
         
         if(!(note.equals(currentNote))){
            
            setTextChanged(true);
         }
         else{
            
            setTextChanged(false);
         }
      }
   }
   
   @Override
   public void afterTextChanged(Editable s){}
   
   @Override
   public void onClick(View v){
      
      if(write.getText().toString().equals("Kaydet")){
         
         setNote();
      }
      else{
         
         setEdit(true);
      }
   }
   
   @SuppressLint("InflateParams")
   public void show(){
      
      dialog  = new AlertDialog.Builder(activityWeakReference.get()).create();
      view    = activityWeakReference.get().getLayoutInflater().inflate(R.layout.note, null, false);
      content = view.findViewById(R.id.content);
      
      view.findViewById(R.id.header).setBackgroundColor(u.getPrimaryColor(activityWeakReference.get()));
      
      descriptionText = view.findViewById(R.id.descriptionText);
      descriptionText.setText(description);
      Button cancel = view.findViewById(R.id.cancel);
      write        = view.findViewById(R.id.writeNote);
      editText     = view.findViewById(R.id.editText);
      deepNoteText = view.findViewById(R.id.note);
      deepNoteText.setText(deepNote);
      write.setOnClickListener(this);
      
      cancel.setOnClickListener((v) -> dialog.dismiss());
      setEdit(isEdit);
      dialog.setView(view);
      dialog.setCancelable(true);
      setDialogListener(_dialogCloseListener);
      
      dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationBounce;
      dialog.show();
   }
   
   private void setDialogListener(DialogListener dialogCloseListener){
      
      dialog.setOnShowListener(dialogCloseListener.getDialogListener());
      dialog.setOnCancelListener(dialogCloseListener.getDialogListener());
      dialog.setOnDismissListener(dialogCloseListener.getDialogListener());
   }
   
   @SuppressLint("SetTextI18n")
   private void setEdit(boolean isEdit){
      
      if(isEdit){
         
         TransitionManager.beginDelayedTransition(content);
         descriptionText.setVisibility(View.GONE);
         editText.setVisibility(View.VISIBLE);
         deepNoteText.setVisibility(View.VISIBLE);
         //cancel.setVisibility(View.GONE);
         write.setText("Kaydet");
         write.setEnabled(false);
         
         editText.addTextChangedListener(this);
         editText.setOnEditorActionListener(this);
         
         if(note != null){
            
            editText.setText(note);
            editText.selectAll();
         }
         
         editText.requestFocus();
      }
   }
   
   public NoteDialog setOnCompleteListener(Runnable runnable){
      
      this.runnable = runnable;
      return this;
   }
   
   
}
