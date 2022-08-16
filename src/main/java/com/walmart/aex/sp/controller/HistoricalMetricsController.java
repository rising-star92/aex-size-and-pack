package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import com.walmart.aex.sp.service.HistoricalMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HistoricalMetricsController {

   @Autowired
   HistoricalMetricsService historicalMetricsService;

   @QueryMapping
   public HistoricalMetricsResponse fetchHistoricalMetricsFineline(@Argument HistoricalMetricsRequest request) {
      return historicalMetricsService.fetchHistoricalMetricsFineline(request);
   }

   @QueryMapping
   public HistoricalMetricsResponse fetchHistoricalMetricsCC(@Argument HistoricalMetricsRequest request) {
      return historicalMetricsService.fetchHistoricalMetricsCC(request);
   }
}
