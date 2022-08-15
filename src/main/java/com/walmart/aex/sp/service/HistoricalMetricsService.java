package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class HistoricalMetricsService {
   private static final String WEEK_START = "01";

   @Autowired
   MidasServiceCall midasServiceCall;

   public HistoricalMetricsResponse fetchHistoricalMetricsFineline(HistoricalMetricsRequest request) {
      request.setLyCompWeekStart(Integer.valueOf(String.valueOf(getLastYear()).concat(WEEK_START)));
      request.setLyCompWeekEnd(Integer.valueOf(String.valueOf(getCurrentYear()).concat(WEEK_START)));
      return midasServiceCall.fetchHistoricalMetrics(request);
   }

   public HistoricalMetricsResponse fetchHistoricalMetricsCC(HistoricalMetricsRequest request) {
      return new HistoricalMetricsResponse(new ArrayList<>());
   }

   private Integer getLastYear() {
      return LocalDate.now().getYear() - 1;
   }

   private Integer getCurrentYear() {
      return LocalDate.now().getYear();
   }


}
