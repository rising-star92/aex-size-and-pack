package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.paging.Pages;
import com.google.cloud.PageImpl;
import com.google.cloud.bigquery.*;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.dto.plandefinition.PlanIdResponse;
import com.walmart.aex.sp.dto.storedistribution.PackData;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionDTO;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionData;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;
import com.walmart.aex.sp.properties.GraphQLProperties;
import com.walmart.aex.sp.util.BuyQtyResponseInputs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BigQueryStoreDistributionServiceTest {

    @Mock
    private BigQueryConnectionProperties bigQueryConnectionProperties;
    @Mock
    private BigQuery bigQuery;
    @Mock
    private BQFPService bqfpService;
    @Mock
    private StrategyFetchService strategyFetchService;
    @Mock
    private GraphQLService graphQLService;
    @Mock
    private GraphQLProperties graphQLProperties;
    @Mock
    private LinePlanService linePlanService;
    @InjectMocks
    private BigQueryStoreDistributionService bigQueryStoreDistributionService;
    private PackData packData;
    private TableResult isResult;
    private TableResult bsResult;
    private BQFPResponse bqfpResponse;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        bigQueryStoreDistributionService = new BigQueryStoreDistributionService(new ObjectMapper(), bqfpService, strategyFetchService, graphQLService, linePlanService, bigQuery);
        ReflectionTestUtils.setField(bigQueryStoreDistributionService, "bigQueryConnectionProperties", bigQueryConnectionProperties);
        ReflectionTestUtils.setField(bigQueryStoreDistributionService, "graphQLProperties", graphQLProperties);
        setData();
        setProperties();
    }

    @Test
    void getStoreDistributionDataISTest() {
        packData.setPackId("SP_is73_3483_0_34_3483_4_19_8_CHGYHT_HANGING_1");
        try (MockedStatic<BigQueryOptions> mockBigQuery = mockStatic(BigQueryOptions.class)) {
            when(bigQuery.query(any(QueryJobConfiguration.class))).thenReturn(isResult);
            StoreDistributionData response = bigQueryStoreDistributionService.getStoreDistributionData(packData);
            StoreDistributionDTO storeDistributionDTO = response.getStoreDistributionList().stream()
                    .filter(s -> s.getPackId().equals(packData.getPackId()))
                    .findFirst().orElse(new StoreDistributionDTO());

            assertEquals(2, response.getStoreDistributionList().size());
            assertEquals(202344L, storeDistributionDTO.getInStoreWeek());
            verify(bigQuery, times(1)).query(any(QueryJobConfiguration.class));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getStoreDistributionDataBSCatTest() {
        TableResult result = new TableResult(null, 0L, Pages.empty() );

        FinelineVolumeDeviationDto finelineVolumeDeviationDto = new FinelineVolumeDeviationDto();
        finelineVolumeDeviationDto.setPlanId(73L);
        finelineVolumeDeviationDto.setFinelineId(3483);
        finelineVolumeDeviationDto.setLvl3Nbr(12231);
        finelineVolumeDeviationDto.setVolumeDeviationLevel("Category");

        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = new StrategyVolumeDeviationResponse();
        strategyVolumeDeviationResponse.setFinelines(List.of(finelineVolumeDeviationDto));

        PlanIdResponse planIdResponse = new PlanIdResponse(73, "S3 - FYE 2024");

        Payload payload = new Payload();
        payload.setGetPlanById(planIdResponse);

        GraphQLResponse graphQLResponse = new GraphQLResponse();
        graphQLResponse.setData(payload);
        graphQLResponse.setErrors(new ArrayList<>());

        try {
            when(bigQuery.query(Mockito.any(QueryJobConfiguration.class))).thenReturn(result).thenReturn(bsResult);
            when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
            when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);
            when(bqfpService.getBqfpResponse(anyInt(), anyInt())).thenReturn(bqfpResponse);

            StoreDistributionData response = bigQueryStoreDistributionService.getStoreDistributionData(packData);
            StoreDistributionDTO storeDistributionDTO = response.getStoreDistributionList().stream()
                    .filter(s -> s.getPackId().equals(packData.getPackId()))
                    .findFirst().orElse(new StoreDistributionDTO());

            assertEquals(2, response.getStoreDistributionList().size());
            assertEquals(202348L, storeDistributionDTO.getInStoreWeek());
            verify(bigQuery, times(2)).query(any(QueryJobConfiguration.class));
            verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
            verify(bqfpService, times(1)).getBqfpResponse(anyInt(), anyInt());
            verify(strategyFetchService, times(1)).getStrategyVolumeDeviation(anyLong(), anyInt());
        } catch (InterruptedException | SizeAndPackException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getStoreDistributionDataBSSubCatTest() {
        TableResult result = new TableResult(null, 0L, Pages.empty() );

        FinelineVolumeDeviationDto finelineVolumeDeviationDto = new FinelineVolumeDeviationDto();
        finelineVolumeDeviationDto.setPlanId(73L);
        finelineVolumeDeviationDto.setFinelineId(3483);
        finelineVolumeDeviationDto.setLvl3Nbr(12234);
        finelineVolumeDeviationDto.setLvl4Nbr(31520);
        finelineVolumeDeviationDto.setVolumeDeviationLevel("Sub_Category");

        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = new StrategyVolumeDeviationResponse();
        strategyVolumeDeviationResponse.setFinelines(List.of(finelineVolumeDeviationDto));

        PlanIdResponse planIdResponse = new PlanIdResponse(73, "S3 - FYE 2024");

        Payload payload = new Payload();
        payload.setGetPlanById(planIdResponse);

        GraphQLResponse graphQLResponse = new GraphQLResponse();
        graphQLResponse.setData(payload);
        graphQLResponse.setErrors(new ArrayList<>());

        try {
            when(bigQuery.query(Mockito.any(QueryJobConfiguration.class))).thenReturn(result).thenReturn(bsResult);
            when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
            when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);
            when(bqfpService.getBqfpResponse(anyInt(), anyInt())).thenReturn(bqfpResponse);

            StoreDistributionData response = bigQueryStoreDistributionService.getStoreDistributionData(packData);
            StoreDistributionDTO storeDistributionDTO = response.getStoreDistributionList().stream()
                    .filter(s -> s.getPackId().equals(packData.getPackId()))
                    .findFirst().orElse(new StoreDistributionDTO());

            assertEquals(2, response.getStoreDistributionList().size());
            assertEquals(202348L, storeDistributionDTO.getInStoreWeek());
            verify(bigQuery, times(2)).query(any(QueryJobConfiguration.class));
            verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
            verify(bqfpService, times(1)).getBqfpResponse(anyInt(), anyInt());
            verify(strategyFetchService, times(1)).getStrategyVolumeDeviation(anyLong(), anyInt());


        } catch (InterruptedException | SizeAndPackException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getStoreDistributionDataFinelineTest() {
        TableResult result = new TableResult(null, 0L, Pages.empty() );

        FinelineVolumeDeviationDto finelineVolumeDeviationDto = new FinelineVolumeDeviationDto();
        finelineVolumeDeviationDto.setPlanId(73L);
        finelineVolumeDeviationDto.setFinelineId(3483);
        finelineVolumeDeviationDto.setLvl3Nbr(12234);
        finelineVolumeDeviationDto.setLvl4Nbr(31520);
        finelineVolumeDeviationDto.setVolumeDeviationLevel("Fineline");

        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = new StrategyVolumeDeviationResponse();
        strategyVolumeDeviationResponse.setFinelines(List.of(finelineVolumeDeviationDto));

        PlanIdResponse planIdResponse = new PlanIdResponse(73, "S3 - FYE 2024");

        Payload payload = new Payload();
        payload.setGetPlanById(planIdResponse);

        GraphQLResponse graphQLResponse = new GraphQLResponse();
        graphQLResponse.setData(payload);
        graphQLResponse.setErrors(new ArrayList<>());

        try {
            when(bigQuery.query(Mockito.any(QueryJobConfiguration.class))).thenReturn(result).thenReturn(bsResult);
            when(strategyFetchService.getStrategyVolumeDeviation(anyLong(), anyInt())).thenReturn(strategyVolumeDeviationResponse);
            when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);
            when(bqfpService.getBqfpResponse(anyInt(), anyInt())).thenReturn(bqfpResponse);

            StoreDistributionData response = bigQueryStoreDistributionService.getStoreDistributionData(packData);
            StoreDistributionDTO storeDistributionDTO = response.getStoreDistributionList().stream()
                    .filter(s -> s.getPackId().equals(packData.getPackId()))
                    .findFirst().orElse(new StoreDistributionDTO());

            assertEquals(2, response.getStoreDistributionList().size());
            assertEquals(202348L, storeDistributionDTO.getInStoreWeek());
            verify(bigQuery, times(2)).query(any(QueryJobConfiguration.class));
            verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
            verify(bqfpService, times(1)).getBqfpResponse(anyInt(), anyInt());
            verify(strategyFetchService, times(1)).getStrategyVolumeDeviation(anyLong(), anyInt());
        } catch (InterruptedException | SizeAndPackException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("When the PO Distribution override list has a planId that's matched from the request, it will use the override query (one call)")
    void poDistributionOverrideRoutesToCorrectQuery() {
        final String poOverridePlanIdsJson = "[123]";
        packData.setPlanId(123L);
        when(bigQueryConnectionProperties.getPODistributionOverridePlanIds()).thenReturn(poOverridePlanIdsJson);

        try {
            bigQueryStoreDistributionService.getStoreDistributionData(packData);
            verify(bigQuery, times(1)).query(any(QueryJobConfiguration.class));
        } catch (InterruptedException e) {
            fail(e);
        }
    }

    private void setData() throws IOException {
        packData = new PackData();
        packData.setPlanId(73L);
        packData.setFinelineNbr(3483);
        packData.setPackId("SP_bs73_3483_0_34_3483_4_19_8_BLCOVE_FOLDED_0");
        packData.setInStoreWeek(202348L);

        FieldValue isFieldValue1 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, "{\"productFineline\":\"73_3483\",\"finelineNbr\":3483,\"styleNbr\":\"34_3483_4_19_8\",\"inStoreWeek\":202344,\"packId\":\"SP_is73_3483_0_34_3483_4_19_8_CHGYHT_HANGING_1\",\"store\":35,\"packMultiplier\":1}");
        FieldValue isFieldValue2 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, "{\"productFineline\":\"73_3483\",\"finelineNbr\":3483,\"styleNbr\":\"34_3483_4_19_8\",\"inStoreWeek\":202344,\"packId\":\"SP_is73_3483_0_34_3483_4_19_8_CHGYHT_HANGING_1\",\"store\":37,\"packMultiplier\":1}");

        FieldValueList isFieldValueList = FieldValueList.of(List.of(isFieldValue1, isFieldValue2));

        isResult = new TableResult(null, 2L, new PageImpl<>(null, null, Collections.singleton(isFieldValueList)) );

        FieldValue bsFieldValue1 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, "{\"productFineline\":\"73_3483\",\"finelineNbr\":3483,\"styleNbr\":\"34_3483_4_19_8\",\"inStoreWeek\":202344,\"packId\":\"SP_bs73_3483_0_34_3483_4_19_8_BLCOVE_FOLDED_0\",\"store\":5440,\"packMultiplier\":1,\"clusterId\":1,\"cc\":\"34_3483_4_19_8_BLCOVE\",\"fixtureAllocation\":0.5,\"fixtureType\":\"RACKS\"}");
        FieldValue bsFieldValue2 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, "{\"productFineline\":\"73_3483\",\"finelineNbr\":3483,\"styleNbr\":\"34_3483_4_19_8\",\"inStoreWeek\":202344,\"packId\":\"SP_bs73_3483_0_34_3483_4_19_8_BLCOVE_FOLDED_0\",\"store\":5440,\"packMultiplier\":1,\"clusterId\":1,\"cc\":\"34_3483_4_19_8_BLCOVE_0\",\"fixtureAllocation\":0.5,\"fixtureType\":\"RACKS\"}");

        FieldValueList bsFieldValueList = FieldValueList.of(List.of(bsFieldValue1, bsFieldValue2));

        bsResult = new TableResult(null, 2L, new PageImpl<>(null, null, Collections.singleton(bsFieldValueList)) );

        bqfpResponse = BuyQtyResponseInputs.bQFPResponseFromJson("/bqfpServiceResponse");
    }

    private void setProperties() {
        lenient().when(bigQueryConnectionProperties.getMLProjectId()).thenReturn("ml-prod");
        lenient().when(bigQueryConnectionProperties.getMLDataSetName()).thenReturn("non-prod");
        lenient().when(bigQueryConnectionProperties.getRFASPPackOptTableName()).thenReturn("output");
        lenient().when(bigQueryConnectionProperties.getRFAProjectId()).thenReturn("wmt-prod");
        lenient().when(bigQueryConnectionProperties.getRFADataSetName()).thenReturn("rfa_output");
        lenient().when(bigQueryConnectionProperties.getRFACCStageTable()).thenReturn("out_parquet");

        lenient().when(graphQLProperties.getPlanDefinitionConsumerId()).thenReturn("planDefConsumerId");
        lenient().when(graphQLProperties.getPlanDefinitionConsumerName()).thenReturn("planDefConsumerName");
        lenient().when(graphQLProperties.getPlanDefinitionConsumerEnv()).thenReturn("planDefConsumerEnv");
        lenient().when(graphQLProperties.getPlanDefinitionUrl()).thenReturn("planDefUrl");
        lenient().when(graphQLProperties.getPlanDefinitionQuery()).thenReturn("planDefQuery");
    }
}