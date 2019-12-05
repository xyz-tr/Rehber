package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.call.Call;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class AddedNewCalls{
   
   @Getter private final List<Call> calls;
}
