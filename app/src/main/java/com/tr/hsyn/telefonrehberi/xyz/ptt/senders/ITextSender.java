package com.tr.hsyn.telefonrehberi.xyz.ptt.senders;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import java.util.List;


public interface ITextSender {
    
    Message sendText(Gmail gmail, String sender, List<String> recievers, String subject, String body);
}
