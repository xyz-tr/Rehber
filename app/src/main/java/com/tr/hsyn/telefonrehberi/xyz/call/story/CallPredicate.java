package com.tr.hsyn.telefonrehberi.xyz.call.story;

import com.annimon.stream.function.BiFunction;
import com.annimon.stream.function.Predicate;
import com.annimon.stream.function.ToIntFunction;
import com.annimon.stream.function.ToLongFunction;
import com.tr.hsyn.telefonrehberi.xyz.call.ICall;


public enum CallPredicate{
   ;
   
   public static final Predicate<ICall>                    PREDICATE_SPEAKING = cx -> cx.getDuration() != 0;
   public static final Predicate<ICall>                    PREDICATE_RINGING  = cx -> cx.getRingingDuration() != 0;
   public static final Predicate<ICall>                    PREDICATE_RESPONSE = com.annimon.stream.function.Predicate.Util.and(PREDICATE_SPEAKING, PREDICATE_RINGING);
   public static final BiFunction<ICall, Integer, Boolean> PREDICATE_TYPE     = (call, type) -> call.getType() == type;
   public static final ToIntFunction<ICall>                FUNCTION_SPEAKING_AVERAGE = ICall::getDuration;
   public static final ToLongFunction<ICall>               FUNCTION_RINGING_AVERAGE  = ICall::getRingingDuration;
   
}
