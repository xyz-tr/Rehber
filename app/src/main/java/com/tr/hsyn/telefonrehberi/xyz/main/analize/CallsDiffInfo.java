package com.tr.hsyn.telefonrehberi.xyz.main.analize;

import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import java.util.Collection;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class CallsDiffInfo{
   
   private Collection<? extends ICall> newCalls;
   private Collection<? extends ICall>  removedCalls;
   private StringBuilder               report = new StringBuilder();
   
}
