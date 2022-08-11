package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BQFPResponse {
   private Long planId;
   private Integer lvl0Nbr;
   private Integer lvl1Nbr;
   private Integer lvl2Nbr;
   private Integer lvl3Nbr;
   private Integer lvl4Nbr;
   private Integer finelineNbr;
   private BigDecimal volumeDeviationStrategyLevelSelection;
   private List<Style> styles;
}
