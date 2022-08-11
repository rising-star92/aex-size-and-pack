package com.walmart.aex.sp.dto.bqfp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fixture {
   private String fixtureType;
   private Integer fixtureTypeRollupId;
   private Metrics metrics;
   private InitialSet initialSet;
   private List<BumpSet> bumpList;
   private Reconciliation recon;
   private List<Cluster>clusters;
   private String flowStrategy;
   private List<Replenishment> replenishments;
   private Long remainingUnits;
}
