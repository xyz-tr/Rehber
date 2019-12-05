package com.tr.hsyn.telefonrehberi.xyz.ptt.deleters;

import com.google.api.services.gmail.Gmail;

import java.util.List;


public interface ISentboxDeleter {
   
   void deleteAllSentMessages(Gmail gmail, List<String> labels, String query);
}
