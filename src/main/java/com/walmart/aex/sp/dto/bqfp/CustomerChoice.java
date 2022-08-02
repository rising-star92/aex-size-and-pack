package com.walmart.aex.sp.dto.bqfp;

import com.walmart.aex.sp.enums.ChannelType;
import lombok.Data;

import java.util.List;

@Data
public class CustomerChoice {
   private String ccId;
   private String ccName;
   private Long planId;
   private Integer lvl0Nbr;
   private Integer lvl1Nbr;
   private Integer lvl2Nbr;
   private Integer lvl3Nbr;
   private Integer lvl4Nbr;
   private ChannelType channelType;
   private Integer finelineId;
   private String styleId;
   private Metrics metrics;
   private List<Fixture> fixtures;

   private InitialSet initialSet;
   private List<BumpSet> bumpList;
   private Reconciliation recon;
   private FlowStrategy flowStrategy;
   private List<Replenishment> replenishment;
}
