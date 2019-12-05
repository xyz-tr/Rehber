package com.tr.hsyn.telefonrehberi.xyz.call.detail.cloud;


import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import java.util.List;


@KickbackBox(name = "CallDetailBox")
public class CallDetailBox{
   
   @KickbackElement(name = "calls")
   List<ICall> calls;
}
