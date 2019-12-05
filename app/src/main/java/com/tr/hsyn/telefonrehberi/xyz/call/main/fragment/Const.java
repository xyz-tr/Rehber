package com.tr.hsyn.telefonrehberi.xyz.call.main.fragment;

import com.skydoves.kickback.KickbackBox;
import com.skydoves.kickback.KickbackElement;
import com.tr.hsyn.telefonrehberi.xyz.call.story.CallStory;


@KickbackBox(name = "CallsConst")
public abstract class Const{
   
   @KickbackElement(name = "callStory")
   CallStory callStory;
   
}
