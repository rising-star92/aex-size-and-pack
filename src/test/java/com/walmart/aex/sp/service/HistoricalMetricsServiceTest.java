package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.WeeksDTO;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import com.walmart.aex.sp.dto.historicalmetrics.WeeksResponse;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.FinelinePlanRepository;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class HistoricalMetricsServiceTest {

    @InjectMocks
    HistoricalMetricsService historicalMetricsService;

    @Mock
    WeeksService weeksService;

    @Mock
    FinelinePlanRepository finelinePlanRepository;
    @Mock
    MidasServiceCall midasServiceCall;

    @Test
    void testFetchHistoricalMetricsFineline(){
        HistoricalMetricsRequest request = new HistoricalMetricsRequest();
        request.setChannel("store");
        WeeksResponse weeksResponse = new WeeksResponse();
        WeeksDTO endWeek = new WeeksDTO();
        endWeek.setWmYearWkLly(2024);
        endWeek.setWmYearWk(2052);
        endWeek.setFiscalWeekDesc("FYE2024WK52");
        weeksResponse.setEndWeek(endWeek);
        WeeksDTO startWeek = new WeeksDTO();
        startWeek.setWmYearWkLly(2024);
        startWeek.setWmYearWk(2052);
        startWeek.setFiscalWeekDesc("FYE2024WK52");
        weeksResponse.setStartWeek(startWeek);
        FinelinePlan fineline = new FinelinePlan();
        FinelinePlanId finelinePlanId = new FinelinePlanId();
        finelinePlanId.setFinelineNbr(2057);
        fineline.setFinelinePlanId(finelinePlanId);
        SubCatPlanId subCatPlanId = new SubCatPlanId();
        MerchCatPlanId merchCatPlanId = new MerchCatPlanId(73l,50000,34,12228,6148,1);
        subCatPlanId.setMerchCatPlanId(merchCatPlanId);
        finelinePlanId.setSubCatPlanId(subCatPlanId);
        Mockito.when(weeksService.getWeeks(any(),any(),any(),any(),any())).thenReturn(weeksResponse);
        Mockito.when(finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(any(),any(),any())).thenReturn(Optional.of(fineline));
        Mockito.when(midasServiceCall.fetchHistoricalMetrics(request)).thenReturn(new HistoricalMetricsResponse());
        HistoricalMetricsResponse historicalMetricsResponse =historicalMetricsService.fetchHistoricalMetricsFineline(request);
        Assert.assertNotNull(historicalMetricsResponse);
    }

    @Test
    void testFetchHistoricalMetricsFinelineReturnNullResponse(){
        HistoricalMetricsRequest request = new HistoricalMetricsRequest();
        request.setChannel("store");
        WeeksResponse weeksResponse = new WeeksResponse();
        WeeksDTO endWeek = new WeeksDTO();
        endWeek.setWmYearWkLly(2024);
        endWeek.setWmYearWk(2052);
        endWeek.setFiscalWeekDesc("FYE2024WK52");
        weeksResponse.setEndWeek(endWeek);
        WeeksDTO startWeek = new WeeksDTO();
        startWeek.setWmYearWkLly(2024);
        startWeek.setWmYearWk(2052);
        startWeek.setFiscalWeekDesc("FYE2024WK52");
        FinelinePlan fineline = new FinelinePlan();
        FinelinePlanId finelinePlanId = new FinelinePlanId();
        finelinePlanId.setFinelineNbr(2057);
        fineline.setFinelinePlanId(finelinePlanId);
        Mockito.when(finelinePlanRepository.findByFinelinePlanId_SubCatPlanId_MerchCatPlanId_PlanIdAndFinelinePlanId_FinelineNbrAndFinelinePlanId_SubCatPlanId_MerchCatPlanId_ChannelId(any(),any(),any())).thenReturn(Optional.of(fineline));
        HistoricalMetricsResponse historicalMetricsResponse =historicalMetricsService.fetchHistoricalMetricsFineline(request);
        Assert.assertNotNull(historicalMetricsResponse);
    }

}
