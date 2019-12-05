package com.tr.hsyn.telefonrehberi.util;

public class Rank implements RankInfo{
   
   private int rank;
   private int rankCount;
   private int mostRank;
   
   public Rank(int rank, int rankCount, int mostRank){
      
      this.rank      = rank;
      this.rankCount = rankCount;
      this.mostRank  = mostRank;
   }
   
   public Rank(int rank, int rankCount){
      
      this.rank      = rank;
      this.rankCount = rankCount;
   }
   
   @Override
   public int getRank(){
      
      return rank;
   }
   
   @Override
   public int getRankCount(){
      
      return rankCount;
   }
   
   @Override
   public int getMostRank(){
      
      return mostRank;
   }
   
}
