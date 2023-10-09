package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.PageImpl;
import com.google.cloud.bigquery.*;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.isVolume.FinelineVolume;
import com.walmart.aex.sp.dto.isVolume.InitialSetVolumeResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class BigQueryInitialSetPlanServiceTest {
    @Mock
    private BigQuery bigQuery;
    private TableResult isResult;
    private BQFPResponse bqfpResponse;
    @Mock
    private StrategyFetchService strategyFetchService;
    @InjectMocks
    private BigQueryInitialSetPlanService bigQueryInitialSetPlanService;
    @Mock
    private BigQueryConnectionProperties bigQueryConnectionProperties;
    @Mock
    private BQFPService bqfpService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        bigQueryInitialSetPlanService = new BigQueryInitialSetPlanService(new ObjectMapper(),bqfpService,strategyFetchService,bigQuery);
        ReflectionTestUtils.setField(bigQueryInitialSetPlanService, "bigQueryConnectionProperties", bigQueryConnectionProperties);
        setData();
        setProperties();
    }
    @Test
    void getInitialAndBumpSetDetailsByVolumeClusterTest() throws SizeAndPackException {
        Long planId = 73l;
        FinelineVolume request = getFinelineVolume();
        StrategyVolumeDeviationResponse volumeDeviationResponse = getVolumeDeviationStrategyResponse();
        when(strategyFetchService.getStrategyVolumeDeviation(planId, request.getFinelineNbr())).thenReturn(volumeDeviationResponse);
        try {
            when(bigQuery.query(any(QueryJobConfiguration.class))).thenReturn(isResult);
            when(bqfpService.getBqfpResponse(anyInt(), anyInt())).thenReturn(bqfpResponse);
            List<InitialSetVolumeResponse> response = bigQueryInitialSetPlanService.getInitialAndBumpSetDetailsByVolumeCluster(planId,request);
            assertEquals(1, response.size());
            assertEquals(2, response.get(0).getCustomerChoices().size());
            assertEquals(1, response.get(0).getCustomerChoices().get(0).getIsPlans().get(0).getMetrics().get(0).getStores().size());
            verify(bigQuery, times(2)).query(any(QueryJobConfiguration.class));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getInitialAndBumpSetDetailsByVolumeClusterTestWhenVolumeDeviationIsPassed() throws SizeAndPackException {
        Long planId = 73l;
        FinelineVolume request = getFinelineVolumeWithVolDeviation();
        try {
            when(bigQuery.query(any(QueryJobConfiguration.class))).thenReturn(isResult);
            when(bqfpService.getBqfpResponse(anyInt(), anyInt())).thenReturn(bqfpResponse);
            List<InitialSetVolumeResponse> response = bigQueryInitialSetPlanService.getInitialAndBumpSetDetailsByVolumeCluster(planId,request);
            assertEquals(1, response.size());
            assertEquals(2, response.get(0).getCustomerChoices().size());
            assertEquals(1, response.get(0).getCustomerChoices().get(0).getIsPlans().get(0).getMetrics().get(0).getStores().size());
            verify(bigQuery, times(2)).query(any(QueryJobConfiguration.class));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getInitialAndBumpSetDetailsByVolumeClusterTestWhenStrategyAPIReturnsNull() throws SizeAndPackException {
        Long planId = 73l;
        FinelineVolume request = getFinelineVolume();
        when(strategyFetchService.getStrategyVolumeDeviation(planId, request.getFinelineNbr())).thenReturn(null);
        try {
            List<InitialSetVolumeResponse> response = bigQueryInitialSetPlanService.getInitialAndBumpSetDetailsByVolumeCluster(planId, request);
            assertEquals(0, response.size());
            verify(bigQuery, times(2)).query(any(QueryJobConfiguration.class));
            fail("Error Occurred while fetching Strategy Volume Deviation Response for plan ID 73");
        }catch (SizeAndPackException e) {
            assertEquals("Error Occurred while fetching Strategy Volume Deviation Response for plan ID 73", e.getMessage());
            }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private FinelineVolume getFinelineVolume() {
        FinelineVolume request = new FinelineVolume();
        request.setFinelineNbr(3483);
        request.setInterval("S3");
        request.setLvl3Nbr(12228);
        request.setLvl4Nbr(31507);
        request.setFiscalYear(2024);
        return request;
    }

    private FinelineVolume getFinelineVolumeWithVolDeviation() {
        FinelineVolume request = new FinelineVolume();
        request.setFinelineNbr(3483);
        request.setInterval("S3");
        request.setLvl3Nbr(12228);
        request.setLvl4Nbr(31507);
        request.setFiscalYear(2024);
        request.setVolumeDeviationLevel("Sub_Category");
        return request;
    }
    private void setData() throws IOException {
        FieldValue isFieldValue1 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, "{\"productFineline\":\"73_3483\",\"cc\":\"34_3483_4_19_8_COFFEE CAKE\",\"style_nbr\":\"34_3483_4_19_8\",\"is_quantity\":202344,\"bs_quantity\":1,\"store\":35,\"clusterId\":1,\"in_store_week\":202432,\"fixtureAllocation\":0.25,\"fixtureType\":\"HANGING\"}");
        FieldValue isFieldValue2 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, "{\"productFineline\":\"73_3483\",\"cc\":\"34_3483_4_19_8_MUSTARD\",\"style_nbr\":\"34_3483_4_19_8\",\"is_quantity\":202344,\"bs_quantity\":2,\"store\":37,\"clusterId\":1,\"in_store_week\":202432,\"fixtureAllocation\":0.25,\"fixtureType\":\"HANGING\"}");
        FieldValueList isFieldValueList = FieldValueList.of(List.of(isFieldValue1, isFieldValue2));
        isResult = new TableResult(null, 2L, new PageImpl<>(null, null, Collections.singleton(isFieldValueList)) );
        bqfpResponse = BuyQtyResponseInputs.bQFPResponseFromJson("/bqfpServiceResponse");
    }

    private void setProperties() {
        lenient().when(bigQueryConnectionProperties.getMLProjectId()).thenReturn("wmt-mtech-assortment-ml-prod");
        lenient().when(bigQueryConnectionProperties.getMLDataSetName()).thenReturn("aex_pack_opt_non_prod");
        lenient().when(bigQueryConnectionProperties.getRFASPPackOptTableName()).thenReturn("output_stage");

        lenient().when(bigQueryConnectionProperties.getRFAProjectId()).thenReturn("wmt-e12743607538928");
        lenient().when(bigQueryConnectionProperties.getRFADataSetName()).thenReturn("commitment_report_rfa_output_stg");
        lenient().when(bigQueryConnectionProperties.getRFACCStageTable()).thenReturn("rfa_cc_out_parquet");
    }

    private StrategyVolumeDeviationResponse getVolumeDeviationStrategyResponse() {
        StrategyVolumeDeviationResponse volumeDeviationResponse = new StrategyVolumeDeviationResponse();
        FinelineVolumeDeviationDto finelineVolumeDeviationDto = new FinelineVolumeDeviationDto();
        finelineVolumeDeviationDto.setFinelineId(3483);
        finelineVolumeDeviationDto.setLvl0Nbr(50000);
        finelineVolumeDeviationDto.setLvl1Nbr(34);
        finelineVolumeDeviationDto.setLvl2Nbr(1488);
        finelineVolumeDeviationDto.setLvl3Nbr(12228);
        finelineVolumeDeviationDto.setLvl4Nbr(31507);
        finelineVolumeDeviationDto.setVolumeDeviationLevel("Sub_Category");
        List<FinelineVolumeDeviationDto> finelines = new ArrayList<>();
        finelines.add(finelineVolumeDeviationDto);
        volumeDeviationResponse.setFinelines(finelines);
        return volumeDeviationResponse;
    }
}
