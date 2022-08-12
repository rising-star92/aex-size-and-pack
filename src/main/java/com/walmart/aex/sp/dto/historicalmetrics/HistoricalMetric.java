package com.walmart.aex.sp.dto.historicalmetrics;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricalMetric {
   private String sizeDesc;
   private Integer lyActualSalesUnits;
   private Double lyActualSalesUnitsPct;
   private Integer lyActualReceiptUnits;
   private Double lyActualReceiptUnitsPct;
}
