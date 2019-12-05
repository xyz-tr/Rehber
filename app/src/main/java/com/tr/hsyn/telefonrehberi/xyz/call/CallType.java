package com.tr.hsyn.telefonrehberi.xyz.call;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.tr.hsyn.telefonrehberi.xyz.call.Type.BLOCKED;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.GETREJECTED;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.INCOMMING;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.INCOMMING_WIFI;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.MISSED;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.OUTGOING;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.OUTGOING_WIFI;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.REJECTED;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.UNKNOWN;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.UNREACHED;
import static com.tr.hsyn.telefonrehberi.xyz.call.Type.UNRECIEVED;


@IntDef({INCOMMING, OUTGOING, MISSED, REJECTED, BLOCKED, INCOMMING_WIFI, OUTGOING_WIFI, UNREACHED, UNRECIEVED, GETREJECTED, UNKNOWN})
@Retention(RetentionPolicy.SOURCE)
public @interface CallType{}
