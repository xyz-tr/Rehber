package com.tr.hsyn.telefonrehberi.util.event;

import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReloadCall{
   
   @Getter private final ICall phoneCall;
}
