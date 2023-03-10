package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
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

   public BQFPResponse(Long planId, Integer lvl0Nbr, Integer lvl1Nbr, Integer lvl2Nbr,
                             Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, List<Style> styles) {
      this.planId=planId;
      this.lvl0Nbr=lvl0Nbr;
      this.lvl1Nbr=lvl1Nbr;
      this.lvl2Nbr=lvl2Nbr;
      this.lvl3Nbr=lvl3Nbr;
      this.lvl4Nbr=lvl4Nbr;
      this.finelineNbr=finelineNbr;
      this.styles=styles;
   }
}
