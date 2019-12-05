package com.tr.hsyn.telefonrehberi.util.concurrent;

import androidx.annotation.NonNull;

import java9.util.concurrent.CompletableFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@ToString
public class WorkStat{
   
   @Getter private final   String            id;
   @Getter private final   String            reason;
   @Getter @Setter private long              workStartTime = System.currentTimeMillis();
   @Getter @Setter private long              workEndTime;
   @Getter @Setter private String            error;
   @Getter @Setter private String            errorCause;
   @Getter @Setter private boolean           isDone;
   @Getter @Setter private CompletableFuture completableFuture;
   @Getter @Setter private boolean           minPriority;
   
   
   WorkStat(String id, String reason){
      
      this.id     = id;
      this.reason = reason;
   }
   
   WorkStat(String id, String reason, boolean minPriority){
      
      this.id     = id;
      this.reason = reason;
      this.minPriority = minPriority;
   }
   
   @Override
   public int hashCode(){
      
      return id.hashCode();
   }
   
   @Override
   public boolean equals(Object o){
      
      return o instanceof WorkStat && id.equals(((WorkStat) o).id);
   }
   
   public boolean equals(@NonNull WorkStat workStat){
      
      return id.equals(workStat.id);
   }
}
