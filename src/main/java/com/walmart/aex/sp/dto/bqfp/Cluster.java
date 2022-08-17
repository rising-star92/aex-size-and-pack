package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

import java.util.List;

@Data
public class Cluster {
   private String volClusterDesc;
   private String volClusterLevel;
   private Integer analyticsClusterId;
   private Integer strategyId;
   private Integer flowStrategy;
   private Metrics metrics;
   private InitialSet initialSet;
   private List<BumpSet> bumpList;
   private Reconciliation recon;
}
