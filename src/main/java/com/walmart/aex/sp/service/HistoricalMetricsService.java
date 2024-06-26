package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import com.walmart.aex.sp.dto.historicalmetrics.WeeksResponse;
import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.entity.MerchCatPlanId;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.FinelinePlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
@Slf4j
public class HistoricalMetricsService {

   @Autowired
   MidasServiceCall midasServiceCall;

   @Autowired
   FinelinePlanRepository finelinePlanRepository;

   @Autowired
   WeeksService weeksService;

   public HistoricalMetricsResponse fetchHistoricalMetricsFineline(HistoricalMetricsRequest request) {
      try {
         Integer channelId = ChannelType.getChannelIdFromName(request.getChannel());
         WeeksResponse weeksResponse = weeksService.getWeeks(request.getChannel(), request.getFinelineNbr(), request.getPlanId(), request.getLvl3Nbr(), request.getLvl4Nbr());

         if(Objects.nonNull(weeksResponse)){
            request.setLyCompWeekStart(weeksResponse.getStartWeek().getWmYearWk() - 100);
            request.setLyCompWeekEnd(weeksResponse.getEndWeek().getWmYearWk() - 100);
         }
         FinelinePlan fineline = finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(request.getPlanId(),
                         request.getFinelineNbr(),
                         channelId)
               .orElse(null);

         if (fineline == null) {
            log.warn("No matching record found for fineline for planId: {}, fineline: {}", request.getPlanId(), request.getFinelineNbr());
            return createDefaultResponse();
         } else {
            MerchCatPlanId merchCatPlanId = fineline.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId();
            request.setLvl0Nbr(merchCatPlanId.getLvl0Nbr());
            request.setLvl1Nbr(merchCatPlanId.getLvl1Nbr());
            request.setLvl2Nbr(merchCatPlanId.getLvl2Nbr());
            return midasServiceCall.fetchHistoricalMetrics(request);
         }
      } catch (Exception e) {
         log.error("Unable to retrieve historical size metrics for plan: {}, fineline: {}, error: {}",
               request.getPlanId(), request.getFinelineNbr(), e);
         return createDefaultResponse();
      }
   }

   public HistoricalMetricsResponse fetchHistoricalMetricsCC(HistoricalMetricsRequest request) {
      return new HistoricalMetricsResponse(new ArrayList<>());
   }

   private HistoricalMetricsResponse createDefaultResponse() {
      return new HistoricalMetricsResponse(new ArrayList<>());
   }



}
