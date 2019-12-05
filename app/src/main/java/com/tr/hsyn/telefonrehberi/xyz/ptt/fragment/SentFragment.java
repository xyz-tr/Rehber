package com.tr.hsyn.telefonrehberi.xyz.ptt.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.tr.hsyn.telefonrehberi.R;


public class SentFragment extends Fragment{
   
   public SentFragment(){
      // Required empty public constructor
   }
   
   
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState){
      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.fragment_sent, container, false);
   }
}
