package com.walmart.aex.sp.dto.historicalmetrics;

import lombok.Data;

@Data
public class HistoricalMetric {
   private String sizeDesc;
   private Integer lyActualSalesUnits;
   private Double lyActualSalesUnitsPct;
   private Integer lyActualReceiptUnits;
   private Double lyActualReceiptUnitsPct;
}
