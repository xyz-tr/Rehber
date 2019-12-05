package com.tr.hsyn.telefonrehberi.util.concurrent;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.annimon.stream.function.Consumer;
import com.orhanobut.logger.Logger;
import com.tr.hsyn.telefonrehberi.util.FixedSizeDeque;
import com.tr.hsyn.telefonrehberi.util.event.EventWorkFinish;
import com.tr.hsyn.telefonrehberi.util.event.EventWorkStart;
import com.tr.hsyn.telefonrehberi.util.phone.Files;
import com.tr.hsyn.telefonrehberi.xyz.main.Kickback_MainBox;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import java9.util.concurrent.CompletableFuture;
import java9.util.function.Supplier;
import lombok.Getter;
import lombok.val;
import timber.log.Timber;


/**
 * <h1>Worker</h1>
 * <p>
 * Arka plan işlemlerini yönetecek.
 *
 * @param <R> Geri dönüş değerini belirle
 */
@SuppressWarnings("UnusedReturnValue")
public class Worker<R>{
   
   private                      Thread                             _workerThread;
   private                      Consumer<R>                        workerProccessor;
   private                      WorkerHandler<R>                   workerHandler;
   private                      java9.util.function.Supplier<R>    backgroundSupplier;
   private                      Runnable                           backgroundRun;
   private                      WorkerGeneratorListener<R>         generatorListener;
   private                      WorkerBreakeListener               breakeListener;
   private                      WorkerGenerationOnCompleteListener generationOnCompleteListener;
   private                      WorkStat                           workStat;
   private                      boolean                            minPriority;
   @Getter private              long                               delay;
   private static               int                                WORKSTAT_FIXED_SIZE                              = 32;
   @Getter private final        String                             id                                               = UUID.randomUUID().toString();
   private static final         int                                THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_LOW_PRIORITY  = 16;
   private static final         int                                THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_NORM_PRIORITY = 32;
   private static final         int                                THREAD_PRIORITY_BACKGROUND                       = Thread.NORM_PRIORITY;
   private static               ScheduledThreadPoolExecutor        NORM_PRIORITY_EXECUTOR                           = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_NORM_PRIORITY, new PriorityThreadFactory(THREAD_PRIORITY_BACKGROUND));
   private static final         int                                THREAD_PRIORITY_LOWEST                           = Thread.MIN_PRIORITY;
   private static               ScheduledThreadPoolExecutor        MIN_PRIORITY_EXECUTOR                            = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_LOW_PRIORITY, new PriorityThreadFactory(THREAD_PRIORITY_LOWEST));
   private static final         Handler                            mainHandler                                      = new Handler(Looper.getMainLooper());
   private static final         Executor                           MAIN_THREAD_EXECUTOR                             = mainHandler::post;
   @Getter private static final FixedSizeDeque<WorkStat>           works                                            = new FixedSizeDeque<>(WORKSTAT_FIXED_SIZE);

   static{
      
      setWorkstatFixedSize();
   }
   
   public Worker(){}
   
   private Worker(@NonNull Supplier<R> supplier, @NonNull String reason){
      
      backgroundSupplier = supplier;
      works.addFirst(workStat = new WorkStat(id, reason));
   }
   
   private Worker(@NonNull Runnable runnable, @NonNull String reason){
      
      backgroundRun = runnable;
      works.addFirst(workStat = new WorkStat(id, reason));
   }
   
   private Worker(@NonNull Runnable runnable, @NonNull final String reason, long delay){
      
      backgroundRun = runnable;
      this.delay    = delay;
      works.addFirst(workStat = new WorkStat(id, reason));
      workStat.setWorkStartTime(System.currentTimeMillis() + delay);
   }
   
   private Worker(@NonNull Supplier<R> supplier, @NonNull final String reason, long delay){
      
      backgroundSupplier = supplier;
      this.delay         = delay;
      works.addFirst(workStat = new WorkStat(id, reason));
      workStat.setWorkStartTime(System.currentTimeMillis() + delay);
   }
   
   private Worker(@NonNull Supplier<R> supplier, @NonNull final String reason, long delay, boolean minPriority){
      
      backgroundSupplier = supplier;
      this.delay         = delay;
      this.minPriority   = minPriority;
      works.addFirst(workStat = new WorkStat(id, reason, minPriority));
      workStat.setWorkStartTime(System.currentTimeMillis() + delay);
   }
   
   private Worker(@NonNull final Supplier<R> supplier, @NonNull final String reason, boolean minPriority){
      
      backgroundSupplier = supplier;
      this.minPriority   = minPriority;
      works.addFirst(workStat = new WorkStat(id, reason, minPriority));
   }
   
   private Worker(@NonNull final Runnable runnable, @NonNull final String reason, boolean minPriority){
      
      backgroundRun    = runnable;
      this.minPriority = minPriority;
      works.addFirst(workStat = new WorkStat(id, reason, minPriority));
   }
   
   private Worker(@NonNull final Runnable runnable, @NonNull final String reason, long delay, boolean minPriority){
      
      backgroundRun    = runnable;
      this.minPriority = minPriority;
      this.delay       = delay;
      works.addFirst(workStat = new WorkStat(id, reason, minPriority));
   }
   
   /**
    * Verilen işi arkaplanda çalıştır.
    *
    * @param supplier iş
    * @param <R>      işin geri döndüreceği tür
    * @return CompletableFuture
    */
   public static <R> CompletableFuture<R> onBackground(@NonNull Supplier<R> supplier, @NonNull String reason){
      
      val worker = new Worker<>(supplier, reason);
      
      return worker.startSupply();
   }
   
   private CompletableFuture<R> startSupply(){
      
      if(backgroundSupplier == null){
         
         Logger.w("Arka plan işlemi belirtilmedi");
         return null;
      }
      
      try{
         
         Executor _executor = selectExecutor(delay, minPriority);
         
         val future = CompletableFuture.supplyAsync(backgroundSupplier, _executor)
                                       .whenCompleteAsync(this::consumer, MIN_PRIORITY_EXECUTOR);
         
         workStat.setCompletableFuture(future);
         onWorkStart(future);
         return future;
      }
      catch(java.util.concurrent.RejectedExecutionException e){
         
         createExecutors();
         Executor _executor = selectExecutor(delay, minPriority);
         val future = CompletableFuture.supplyAsync(backgroundSupplier, _executor)
                                       .whenCompleteAsync(this::consumer, MIN_PRIORITY_EXECUTOR);
         
         workStat.setCompletableFuture(future);
         onWorkStart(future);
         return future;
      }
   }
   
   private void onWorkStart(CompletableFuture completableFuture){
      
      EventBus.getDefault().post(new EventWorkStart(workStat, completableFuture));
   }
   
   private void consumer(R value, Throwable throwable){
      
      workStat.setWorkEndTime(System.currentTimeMillis());
      workStat.setDone(true);
      
      if(throwable == null){
         
         Timber.d("Arkaplan işlemi tamamlandı [%s] [%dms]", workStat.getReason(), workStat.getWorkEndTime() - workStat.getWorkStartTime());
      }
      else{
         
         Logger.d("Arkaplan işlemi hata ile tamamlandı : %s [%s] [%dms]", throwable.getMessage(), workStat.getReason(), workStat.getWorkEndTime() - workStat.getWorkStartTime());
   
         try{
      
            File logFile = Kickback_MainBox.getInstance().getLogFile();
            val  writer  = new PrintWriter(logFile);
            throwable.printStackTrace();
            throwable.printStackTrace(writer);
            writer.flush();
            val content = Files.getFileContent(logFile);
            writer.close();
      
            if(content != null){
         
               Logger.e(content);
               workStat.setError(content);
            }
            else{
         
               workStat.setError(throwable.toString());
            }
      
         }
         catch(FileNotFoundException e){
            e.printStackTrace();
         }
      }
      
      onWorkFinish();
   }
   
   private void onWorkFinish(){
      
      if(workStat.getError() == null && workStat.isDone()){
         
         workStat.setCompletableFuture(null);
      }
      
      EventBus.getDefault().post(new EventWorkFinish(workStat));
   }
   
   private static void createExecutors(){
      
      Logger.w("Executors yeniden tanımlanıyor");
      MIN_PRIORITY_EXECUTOR  = createMinPriorityExecutor();
      NORM_PRIORITY_EXECUTOR = createNormPriorityExecutor();
   }
   
   private static ScheduledThreadPoolExecutor createMinPriorityExecutor(){
      
      return new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_LOW_PRIORITY, new PriorityThreadFactory(THREAD_PRIORITY_LOWEST));
   }
   
   private static ScheduledThreadPoolExecutor createNormPriorityExecutor(){
      
      return new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_NORM_PRIORITY, new PriorityThreadFactory(THREAD_PRIORITY_BACKGROUND));
   }
   
   private static Executor selectExecutor(long delay, boolean minPriority){
      
      Executor _executor;
      Executor __executor = minPriority ? MIN_PRIORITY_EXECUTOR : NORM_PRIORITY_EXECUTOR;
      
      if(delay > 0){
         
         _executor = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS, __executor);
      }
      else{
         
         _executor = __executor;
      }
      
      return _executor;
   }
   
   public static CompletableFuture<Void> onBackground(@NonNull Runnable runnable, @NonNull String reason){
      
      return new Worker<>(runnable, reason)
            .startRun();
   }
   
   private CompletableFuture<Void> startRun(){
      
      if(backgroundRun == null){
         
         Logger.w("Çalışacak iş belirtilmedi");
         return null;
      }
      
      try{
         
         val future = createRunFuture();
         workStat.setCompletableFuture(future);
         onWorkStart(future);
         
         return future;
      }
      catch(java.util.concurrent.RejectedExecutionException e){
         
         createExecutors();
         
         val future = createRunFuture();
         
         workStat.setCompletableFuture(future);
         onWorkStart(future);
         
         return future;
      }
   }
   
   private CompletableFuture<Void> createRunFuture(){
      
      Executor _executor = selectExecutor(delay, minPriority);
      return CompletableFuture.runAsync(backgroundRun, _executor)
                              .whenComplete(this::consumer);
   }
   
   private Void consumer(Void v, Throwable throwable){
      
      workStat.setWorkEndTime(System.currentTimeMillis());
      workStat.setDone(true);
      
      if(throwable == null){
         
         Timber.d("Arkaplan işlemi tamamlandı [%s] [%dms]", workStat.getReason(), workStat.getWorkEndTime() - workStat.getWorkStartTime());
      }
      else{
         
         Logger.d("Arkaplan işlemi hata ile tamamlandı : %s [%s] [%dms]", throwable.getMessage(), workStat.getReason(), workStat.getWorkEndTime() - workStat.getWorkStartTime());
   
   
         try{
            
            File logFile = Kickback_MainBox.getInstance().getLogFile();
            val writer = new PrintWriter(logFile);
            throwable.printStackTrace();
            throwable.printStackTrace(writer);
            writer.flush();
            val content = Files.getFileContent(logFile);
            writer.close();
   
            if(content != null){
   
               Logger.e(content);
               workStat.setError(content);
            }
            else{
               
               workStat.setError(throwable.toString());
            }
            
         }
         catch(FileNotFoundException e){
            e.printStackTrace();
         }
   
         
      }
      
      onWorkFinish();
      return null;
   }
   
   public static CompletableFuture<Void> onBackground(@NonNull Runnable runnable, @NonNull String reason, long delay){
      
      return new Worker<>(runnable, reason, delay)
            .startRun();
   }
   
   public static CompletableFuture<Void> onBackground(@NonNull Runnable runnable, @NonNull String reason, long delay, boolean isMinPriority){
      
      return new Worker<>(runnable, reason, delay, isMinPriority)
            .startRun();
   }
   
   public static <R> CompletableFuture<R> onBackground(@NonNull Supplier<R> supplier, @NonNull String reason, long delay){
      
      val worker = new Worker<>(supplier, reason, delay);
      
      return worker.startSupply();
   }
   
   public static <R> CompletableFuture<R> onBackground(@NonNull Supplier<R> supplier, @NonNull String reason, long delay, boolean isMinPriority){
      
      val worker = new Worker<>(supplier, reason, delay, isMinPriority);
      
      return worker.startSupply();
   }
   
   /**
    * Düşük öncelikli arkaplan işlemini başlat.
    *
    * @param supplier arkaplandan değer döndürecek fonksiyon.
    * @param reason   arkaplan işleminin kullanılma sebebi
    * @param <R>      dönecek değerin türü
    * @return Worker
    */
   public static <R> CompletableFuture<R> onBackground(@NonNull Supplier<R> supplier, @NonNull String reason, boolean isMinPriority){
      
      val worker = new Worker<>(supplier, reason, isMinPriority);
      
      return worker.startSupply();
   }
   
   /**
    * Düşük öncelikli arkaplan işlemini başlat.
    *
    * @param runnable arkaplandan görevi.
    * @param reason   arkaplan işleminin kullanılma sebebi
    * @return Worker
    */
   public static CompletableFuture<Void> onBackground(@NonNull Runnable runnable, @NonNull String reason, boolean isMinPriority){
      
      val worker = new Worker<>(runnable, reason, isMinPriority);
      
      return worker.startRun();
   }
   
   /**
    * Servisleri sonlandır
    */
   public static void shutDown(){
      
      MIN_PRIORITY_EXECUTOR.shutdownNow();
      NORM_PRIORITY_EXECUTOR.shutdownNow();
   }
   
   /**
    * Verilen işi Ana thread üzerinde çalıştırır.
    *
    * @param runnable İş
    */
   public static CompletableFuture<Void> onMain(Runnable runnable){
      
      return CompletableFuture.runAsync(runnable, MAIN_THREAD_EXECUTOR);
   }
   
   /**
    * Verilen işi belirtilen süre sonunda Ana Thread üzerinde çalıştırır.
    *
    * @param runnable İş
    * @param delay    Beklenecek süre (milisaniye)
    */
   public static CompletableFuture<Void> onMain(Runnable runnable, long delay){
      
      Executor executor = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS, MAIN_THREAD_EXECUTOR);
      
      return CompletableFuture.runAsync(runnable, executor);
   }
   
   public static CompletableFuture<Void> onBackground(Runnable runnable){
      
      return CompletableFuture.runAsync(runnable, NORM_PRIORITY_EXECUTOR);
   }
   
   public static CompletableFuture<Void> onBackground(Runnable runnable, long delay){
      
      return CompletableFuture.runAsync(runnable, selectExecutor(delay));
   }
   
   private static Executor selectExecutor(long delay){
      
      Executor _executor;
      
      if(delay > 0){
         
         _executor = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS, NORM_PRIORITY_EXECUTOR);
      }
      else{
         
         _executor = NORM_PRIORITY_EXECUTOR;
      }
      
      return _executor;
   }
   
   public static CompletableFuture<Void> onBackground(Runnable runnable, boolean minPriority){
      
      return CompletableFuture.runAsync(runnable, minPriority ? MIN_PRIORITY_EXECUTOR : NORM_PRIORITY_EXECUTOR);
   }
   
   public static CompletableFuture<Void> onBackground(Runnable runnable, long delay, boolean minPriority){
      
      return CompletableFuture.runAsync(runnable, selectExecutor(delay, minPriority));
   }
   
   public static <R> CompletableFuture<R> onBackground(Supplier<R> supplier){
      
      return CompletableFuture.supplyAsync(supplier, NORM_PRIORITY_EXECUTOR);
   }
   
   public static <R> CompletableFuture<R> onBackground(Supplier<R> supplier, long delay){
      
      return CompletableFuture.supplyAsync(supplier, selectExecutor(delay));
   }
   
   public static <R> CompletableFuture<R> onBackground(Supplier<R> supplier, boolean minPriority){
      
      return CompletableFuture.supplyAsync(supplier, minPriority ? MIN_PRIORITY_EXECUTOR : NORM_PRIORITY_EXECUTOR);
   }
   
   public static <R> CompletableFuture<R> onBackground(Supplier<R> supplier, long delay, boolean minPriority){
      
      return CompletableFuture.supplyAsync(supplier, selectExecutor(delay, minPriority));
   }
   
   /**
    * Verilen işi en düşük öncelikle arka planda çalıştırır.
    *
    * @param runnable İş
    */
   public static void onBackgroundWithMinPriority(Runnable runnable){
      
      executeWithMinPriority(runnable);
   }
   
   /**
    * İşi en düşük öncelikle koştur.
    *
    * @param runnable İş
    */
   private static void executeWithMinPriority(Runnable runnable){
      
      try{
         
         MIN_PRIORITY_EXECUTOR.execute(runnable);
      }
      catch(java.util.concurrent.RejectedExecutionException e){
         
         Logger.w("Executor yeniden tanımlanıyor");
         
         MIN_PRIORITY_EXECUTOR = createMinPriorityExecutor();
         
         try{
            
            MIN_PRIORITY_EXECUTOR.execute(runnable);
            
         }
         catch(java.util.concurrent.RejectedExecutionException ee){
            
            Logger.w("ExecutorWithMinPriority kapatılmış");
         }
      }
   }
   
   /**
    * Arka planda çalışıp sonucu UI thread'e döndür.
    *
    * @param worker  Değer döndürecek olan nesne
    * @param handler Dönen değeri alacak olan nesne
    * @param <R>     Döndürülecek değerin türü
    */
   @Deprecated
   public static <R> Runnable onBackground(Supplier<R> worker, Consumer<R> handler){
      
      if(worker == null || handler == null) return null;
      
      Runnable runnable = () -> {
         
         R result = worker.get();
         
         mainHandler.post(() -> handler.accept(result));
      };
      
      executeWithBackgroundPriority(runnable);
      
      return runnable;
   }
   
   private static void executeWithBackgroundPriority(Runnable runnable){
      
      try{
         
         NORM_PRIORITY_EXECUTOR.execute(runnable);
      }
      catch(Exception e){
         
         Logger.w("Executor yeniden tanımlanıyor");
         
         NORM_PRIORITY_EXECUTOR = createNormExecutor();
         
         try{
            
            NORM_PRIORITY_EXECUTOR.execute(runnable);
            
         }
         catch(Exception ee){
            
            Logger.w("ExecutorWithMinPriority kapatılmış");
         }
      }
   }
   
   private static ScheduledThreadPoolExecutor createNormExecutor(){
      
      return new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE_FOR_EXECUTOR_WITH_NORM_PRIORITY, new PriorityThreadFactory(THREAD_PRIORITY_BACKGROUND));
   }
   
   /**
    * Arka planda çalışacak metodu tanımlar
    *
    * @param backgroundWorker Functional interface. Arka plan görevi
    * @return WorkerThread
    */
   @Deprecated
   public Worker<R> thisIsMyWork(Supplier<R> backgroundWorker){
      
      this.backgroundSupplier = backgroundWorker;
      return this;
   }
   
   /**
    * Aldığı metodu arka planda çalıştırmak üzere hazırlar.
    *
    * @param backgroundWorker Functional interface. Arka plan görevi
    * @return WorkerThread
    */
   @Deprecated
   public Worker<R> workOnBackground(java9.util.function.Supplier<R> backgroundWorker){
      
      if((this.backgroundSupplier = backgroundWorker) == null){ return this; }
      
      try{
         
         _workerThread = new Thread(() -> {
            
            R result = backgroundWorker.get();
            
            if(workerProccessor != null){
               workerProccessor.accept(result);
            }
            
            if(workerHandler != null){
               
               mainHandler.post(() -> workerHandler.onWorkResult(result));
            }
         });
      }
      catch(Exception e){
         
         e.printStackTrace();
      }
      
      return this;
   }
   
   /**
    * Alınan geri dönüş değerini yine arka planda işlemek için.
    * Geri dönüş değeri {@code handleOnForeground} metoduna verilmeden önce çağrılır.
    *
    * @param workerProccessor Geri dönüş değeri üzerinde arka planda çalışacak metodu tanımlar.
    * @return Worker
    */
   @Deprecated
   public Worker<R> proccessOnBackground(Consumer<R> workerProccessor){
      
      this.workerProccessor = workerProccessor;
      return this;
   }
   
   /**
    * Arka planda çalışan metodun geri döndürdüğü değeri
    * ön planda alacak olan görevi tanımlar.
    *
    * @param workerHandler Functional interface. Geri dönüş değerini alacak olan görev.
    * @return WorkerThread
    */
   @Deprecated
   public Worker<R> handleOnForeground(WorkerHandler<R> workerHandler){
      
      this.workerHandler = workerHandler;
      return this;
   }
   
   @Deprecated
   public Worker<R> generate(final int _count, int startProgress, long interval){
      
      _workerThread = new _WorkerThread(_count, startProgress, interval);
      
      return this;
   }
   
   @Deprecated
   public Worker<R> onBreak(WorkerBreakeListener breakeListener){
      
      this.breakeListener = breakeListener;
      
      return this;
   }
   
   @Deprecated
   public Worker<R> listenGeneration(WorkerGeneratorListener<R> generatorListener){
      
      this.generatorListener = generatorListener;
      return this;
   }
   
   @Deprecated
   public Worker<R> onGenerationComplete(WorkerGenerationOnCompleteListener generationOnCompleteListener){
      
      this.generationOnCompleteListener = generationOnCompleteListener;
      return this;
   }
   
   @Deprecated
   public void stop(){
      
      if(_workerThread != null){
         
         _workerThread.interrupt();
      }
   }
   
   /**
    * Arka plan görevini başlatır.
    */
   @Deprecated
   public Worker<R> start(){
      
      if(_workerThread != null){
         
         _workerThread.start();
      }
      else{
         
         Logger.w("There is no worker");
      }
      
      return this;
   }
   
   public static void sleep(long duration){
      
      try{
         Thread.sleep(duration);
      }
      catch(InterruptedException e){
         e.printStackTrace();
      }
   }
   
   public static void setWorkstatFixedSize(){
      
      int size = Paper.book().read("workStatFixedSize", WORKSTAT_FIXED_SIZE);
      
      if(size != WORKSTAT_FIXED_SIZE){
         
         WORKSTAT_FIXED_SIZE = size;
      }
   }
   
   public static ScheduledThreadPoolExecutor getNormPriorityExecutor(){
      
      return NORM_PRIORITY_EXECUTOR;
   }
   
   public static Executor getMainThreadExecutor(){
      
      return MAIN_THREAD_EXECUTOR;
   }
   
   public static int getWorkstatFixedSize(){
      
      return WORKSTAT_FIXED_SIZE;
   }
   
   public static void setWorkstatFixedSize(int size){
      
      if(size == WORKSTAT_FIXED_SIZE) return;
      
      Paper.book().write("workStatFixedSize", size);
      
      WORKSTAT_FIXED_SIZE = size;
   }
   
   public static ScheduledThreadPoolExecutor getMinPriorityExecutor(){
      
      return MIN_PRIORITY_EXECUTOR;
   }
   
   @Deprecated
   public boolean isAlive(){
      
      return _workerThread != null && _workerThread.isAlive();
   }
   
   
   private class _WorkerThread extends Thread{
      
      long interval;
      int  startProgress;
      final int _count;
      
      _WorkerThread(final int _count, int startProgress, final long interval){
         
         //this.backgroundWorker = backgroundWorker;
         this._count        = _count;
         this.interval      = interval;
         this.startProgress = startProgress;
      }
      
      
      @Override
      public void run(){
         
         boolean isBroken = false;
         
         for(int i = startProgress; i < _count; i++){
            
            R result = backgroundSupplier.get();
            
            if(workerProccessor != null){
               workerProccessor.accept(result);
            }
            
            //if (workerHandler != null) workerHandler.onWorkResult(result);
            
            int finalI = i;
            
            mainHandler.post(() -> generatorListener.onGenerate(result, finalI, _count));
            
            try{
               sleep(interval);
            }
            catch(InterruptedException e){
               
               if(breakeListener != null){
                  
                  mainHandler.post(breakeListener::onBreak);
               }
               
               isBroken = true;
               
               break;
            }
         }
         
         if(!isBroken && generationOnCompleteListener != null){
            
            mainHandler.post(generationOnCompleteListener::onGenerationComplete);
            return;
         }
         
         if(isBroken && breakeListener != null){
            
            mainHandler.post(breakeListener::onBreak);
         }
      }
      
      
   }
   
}
