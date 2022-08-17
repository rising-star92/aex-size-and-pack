package com.walmart.aex.sp.dto.historicalmetrics;

import lombok.Data;

@Data
public class HistoricalMetricsRequest {
   private Integer planId;
   private Integer lvl3Nbr;
   private Integer lvl4Nbr;
   private Integer finelineNbr;
   private String styleNbr;
   private String customerChoice;
   private String channel;
   private Integer lyCompWeekStart;
   private Integer lyCompWeekEnd;
}
