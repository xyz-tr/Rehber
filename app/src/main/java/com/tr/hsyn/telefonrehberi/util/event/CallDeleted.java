package com.tr.hsyn.telefonrehberi.util.event;


import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class CallDeleted{
   
   @Getter private final ICall deletedCall;
}
