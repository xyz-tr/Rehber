package com.tr.hsyn.telefonrehberi.xyz.call.main.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotedCall{
   
   @Getter private String number;
   @Getter private long date;
   @Getter private int type;
}
