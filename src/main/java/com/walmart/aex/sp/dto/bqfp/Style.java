package com.walmart.aex.sp.dto.bqfp;

import com.walmart.aex.sp.enums.ChannelType;
import lombok.Data;

import java.util.List;
@Data
public class Style {

   private String styleId;
   private Object styleName;
   private ChannelType channelType;
   private Metrics metrics;
   private InitialSet initialSet;
   private List<BumpSet> bumpList;
   private Reconciliation recon;
   private String flowStrategy;
   private List<CustomerChoice> customerChoices;
   private List<Replenishment> replenishment;
}
