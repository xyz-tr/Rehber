package com.tr.hsyn.telefonrehberi.util.event;

import android.net.Uri;

import androidx.annotation.NonNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class ContactAdded{
   
   @Getter @NonNull private final Uri uri;
}
