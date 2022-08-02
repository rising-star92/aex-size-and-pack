package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

import java.util.List;

@Data
public class Fixture {
   private String fixtureType;
   private Integer fixtureTypeRollupId;
   private Metrics metrics;
   private InitialSet initialSet;
   private List<BumpSet> bumpList;
   private Reconciliation recon;
   private List<Cluster>cluster;
   private FlowStrategy flowStrategy;
}
