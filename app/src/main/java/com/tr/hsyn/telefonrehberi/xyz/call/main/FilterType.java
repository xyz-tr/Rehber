package com.tr.hsyn.telefonrehberi.xyz.call.main;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Retention(RetentionPolicy.SOURCE)
@IntDef({Filter.ALL_CALLS,
      Filter.INCOMMING,
      Filter.OUTGOING,
      Filter.MISSED,
      Filter.REJECTED,
      Filter.BLOCKED,
      Filter.DELETED,
      Filter.MOST_OUTGOING_CALLS,
      Filter.MOST_INCOMMING_CALLS,
      Filter.MOST_MISSED_CALLS,
      Filter.MOST_REJECTED_CALLS,
      Filter.MOST_SPEAKING,
      Filter.MOST_TALKING,
      Filter.MOST_DURATION_INCOMMING_CALLS_TOTAL,
      Filter.MOST_DURATION_OUTGOING_CALLS_TOTAL,
      Filter.QUICKEST_MISSED_CALLS,
      Filter.LONGEST_MISSED_CALLS,
      Filter.QUICKEST_ANSWERED_CALLS,
      Filter.LONGEST_ANSWERED_CALLS,
      Filter.LONGEST_MAKE_ANSWERED_CALLS,
      Filter.QUICKEST_MAKE_ANSWERED_CALLS,
      Filter.QUICKEST_REJECTED_CALLS,
      Filter.LONGEST_REJECTED_CALLS,
      Filter.MOST_BLOCKED_CALLS
})
public @interface FilterType{}
