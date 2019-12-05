package com.tr.hsyn.telefonrehberi.xyz.ptt.command;


import androidx.annotation.Nullable;


public interface ICommandEditor {
   
   void setCommandExecuted(String id, @Nullable String comment);
   void setCommandExecuting(boolean isExecuting, String id, @Nullable String comment);
}
