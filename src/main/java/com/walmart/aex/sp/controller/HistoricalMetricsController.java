package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoricalMetricsController {

   @QueryMapping
   public HistoricalMetricsResponse fetchHistoricalMetricsFineline(HistoricalMetricsRequest request) {
      return null;
   }

   @QueryMapping
   public HistoricalMetricsResponse fetchHistoricalMetricsCC(HistoricalMetricsRequest request) {
      return null;
   }
}
