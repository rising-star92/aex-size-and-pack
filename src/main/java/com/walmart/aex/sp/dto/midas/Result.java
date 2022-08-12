package com.walmart.aex.sp.dto.midas;

import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetric;
import lombok.Data;

import java.util.List;

@Data
public class Result {
   private List<HistoricalMetric> response;
}
