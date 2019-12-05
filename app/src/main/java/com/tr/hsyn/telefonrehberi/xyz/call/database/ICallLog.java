package com.tr.hsyn.telefonrehberi.xyz.call.database;

import com.tr.hsyn.telefonrehberi.xyz.call.ICall;

import java.util.List;

//@off
/**
 * <h1>LogDatabaseOperator</h1>
 * 
 * <p>
 *    Arama kayıtları için database yöneticisi.
 * 
 * @author hsyn 2019-12-03 14:21:53
 */
//@on
public interface ICallLog{
   
   void add(Iterable<? extends ICall> calls);
   
   boolean add(ICall call);
   
   void update(Iterable<? extends ICall> calls);
   
   boolean update(ICall call);
   
   ICall get(long date);
   
   List<ICall> getAll();
   
   void close();
   
}
