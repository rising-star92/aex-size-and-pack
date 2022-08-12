package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Service
public class HistoricalMetricsService {

   @Autowired
   private RestTemplate restTemplate;

   @Autowired
   private ObjectMapper objectMapper;

   public HistoricalMetricsResponse fetchHistoricalMetricsFineline(HistoricalMetricsRequest request) {
      return new HistoricalMetricsResponse(new ArrayList<>());
   }

   public HistoricalMetricsResponse fetchHistoricalMetricsCC(HistoricalMetricsRequest request) {
      return new HistoricalMetricsResponse(new ArrayList<>());
   }
}
