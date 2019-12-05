package com.tr.hsyn.telefonrehberi.xyz.ptt.dispatch;


import androidx.annotation.NonNull;

import com.tr.hsyn.telefonrehberi.xyz.ptt.command.ICommand;
import com.tr.hsyn.telefonrehberi.xyz.ptt.command.ICommandExecutor;

import java.util.List;


public interface IDispatch {
    
    void publishMessages(List<ICommand> messages) ;
    
    void registerReciever(@NonNull ICommandExecutor reciever);
    void unregisterReciever(@NonNull ICommandExecutor reciever);

}
