package com.walmart.aex.sp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.google.cloud.PageImpl;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FieldValue;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.cr.storepacks.PackDetailsVolumeResponse;
import com.walmart.aex.sp.dto.cr.storepacks.StoreMetrics;
import com.walmart.aex.sp.dto.cr.storepacks.StylePackVolume;
import com.walmart.aex.sp.dto.cr.storepacks.VolumeFixtureMetrics;
import com.walmart.aex.sp.dto.isVolume.FinelineVolume;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BigQueryConnectionProperties;

@ExtendWith(MockitoExtension.class)
class BigQueryPackStoresServiceTest 
{
    @Mock
    private BigQuery bigQuery;
    
    @Mock
    private BigQueryOptions bigQueryOptions;
    private TableResult isResult;
    
    @Mock
    private StrategyFetchService strategyFetchService;
    
    @InjectMocks
    private BigQueryInitialSetPlanService bigQueryInitialSetPlanService;
    
    @InjectMocks
    private BigQueryPackStoresService bigQueryPackStoresService;
    
    @Mock
    private BigQueryConnectionProperties bigQueryConnectionProperties;
    
    @Mock
    private BQFPService bqfpService;

    @BeforeEach
    void setUp() throws IOException 
    {
        MockitoAnnotations.openMocks(this);
        bigQueryInitialSetPlanService = new BigQueryInitialSetPlanService(bqfpService,
        		strategyFetchService,bigQuery);
        bigQueryPackStoresService = new BigQueryPackStoresService(new ObjectMapper(), bigQuery, strategyFetchService);
        ReflectionTestUtils.setField(bigQueryInitialSetPlanService, "bigQueryConnectionProperties", 
        		bigQueryConnectionProperties);
        ReflectionTestUtils.setField(bigQueryPackStoresService, "bigQueryConnectionProperties", 
        		bigQueryConnectionProperties);
        setData();
        setProperties();
    }
    
    @Test
    void getPackStoreDetailsByVolumeClusterTest() throws SizeAndPackException
    {
    	  Long planId = 73l;
          FinelineVolume request = getFinelineVolume();
          StrategyVolumeDeviationResponse volumeDeviationResponse = getVolumeDeviationStrategyResponse();
          when(strategyFetchService.getStrategyVolumeDeviation(planId, request.getFinelineNbr())).
          thenReturn(volumeDeviationResponse);
          try(MockedStatic<BigQueryOptions> mockBigQuery = mockStatic(BigQueryOptions.class)) 
          {
              when(bigQuery.query(any(QueryJobConfiguration.class))).thenReturn(isResult);
              PackDetailsVolumeResponse packDetailsVolumeResponse = bigQueryPackStoresService
            		  .getPackStoreDetailsByVolumeCluster(planId, request);
              assertNotNull(packDetailsVolumeResponse);
              List<StylePackVolume> stylePackVolumes = 
            		  packDetailsVolumeResponse.getStylePackVolumes();
              assertEquals(2, stylePackVolumes.size());
              for(StylePackVolume stylePackVolume : stylePackVolumes)
              {
            	  assertEquals("34_2840_1_21_2",stylePackVolume.getStyleId());
            	  String packId = stylePackVolume.getPackId();
            	  List<VolumeFixtureMetrics> volumeFixtureMetrics = stylePackVolume.getMetrics();
            	  
            	  if(packId == null)
            	  {
            		  VolumeFixtureMetrics metrics1 = volumeFixtureMetrics.get(0);
            		  assertEquals("34_2840_1_21_2_007", metrics1.getCcId());
            		  assertEquals("RACKS", metrics1.getFixtureType());
            		  assertEquals(BigDecimal.valueOf(0.25), metrics1.getFixtureAllocation());
            		  assertEquals(3, metrics1.getVolumeClusterId());
            		  List<StoreMetrics> storeMetrics1 = metrics1.getStores();
            		  assertEquals(1, storeMetrics1.size());
            		  for(StoreMetrics storeMetric : storeMetrics1)
            		  {
            			  assertEquals(4359, storeMetric.getStore());
            			  assertEquals(8, storeMetric.getQty());
            			  assertEquals(1, storeMetric.getMultiplier());
            		  }
            	  }
            	  else
            	  {
            		  VolumeFixtureMetrics metrics2 = volumeFixtureMetrics.get(0);
            		  assertEquals("34_2840_1_21_2_003", metrics2.getCcId());
            		  assertEquals("WALLS", metrics2.getFixtureType());
            		  assertEquals(BigDecimal.valueOf(0.5), metrics2.getFixtureAllocation());
            		  assertEquals(1, metrics2.getVolumeClusterId());
            		  List<StoreMetrics> storeMetrics2 = metrics2.getStores();
            		  assertEquals(2, storeMetrics2.size());
            		  StoreMetrics sm1 = storeMetrics2.get(0);
            		  assertEquals(1619, sm1.getStore());
            		  assertEquals(1, sm1.getMultiplier());
            		  assertEquals(8, sm1.getQty());
            		  StoreMetrics sm2 = storeMetrics2.get(1);
            		  assertEquals(2631, sm2.getStore());
            		  assertEquals(2, sm2.getMultiplier());
            		  assertEquals(4, sm2.getQty());
            	  }
            	  
              }
              verify(bigQuery, times(1)).query(any(QueryJobConfiguration.class));
          } 
          catch (InterruptedException e) 
          {
              throw new RuntimeException(e);
          }

    }
    
    private FinelineVolume getFinelineVolume() 
    {
        FinelineVolume request = new FinelineVolume();
        request.setFinelineNbr(2840);
        request.setInterval("S3");
        request.setLvl3Nbr(12228);
        request.setLvl4Nbr(31507);
        request.setFiscalYear(2024);
        return request;
    }

    private void setData() throws IOException 
    {
        FieldValue isFieldValue1 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, 
        		"{\"productFineline\":\"73_2840\",\"fineline\":\"2840\", "
        		+ "\"cc\":\"34_2840_1_21_2_003\","
        		+ "\"styleNbr\":\"34_2840_1_21_2\",\"isQuantity\":8,"
        		+ "\"store\":1619,\"clusterId\":1,\"fixtureAllocation\":0.5,"
        		+ "\"fixtureType\":\"WALLS\",\"packId\":\"SP_is68_2840_0_34_2840_1_21_2_003_HANGING_2\","
        		+ "\"initialSetPackMultiplier\":\"1\"}");
        
        FieldValue isFieldValue2 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, 
        		"{\"productFineline\":\"73_2840\",\"fineline\":\"2840\", "
        		+ "\"cc\":\"34_2840_1_21_2_003\","
        		+ "\"styleNbr\":\"34_2840_1_21_2\",\"isQuantity\":4,"
        		+ "\"store\":2631,\"clusterId\":1,\"fixtureAllocation\":0.5,"
        		+ "\"fixtureType\":\"WALLS\",\"packId\":\"SP_is68_2840_0_34_2840_1_21_2_003_HANGING_2\","
        		+ "\"initialSetPackMultiplier\":\"2\"}");
        
        FieldValue isFieldValue3 = FieldValue.of(FieldValue.Attribute.PRIMITIVE, 
        		"{\"productFineline\":\"73_2840\",\"fineline\":\"2840\", "
        		+ "\"cc\":\"34_2840_1_21_2_007\","
        		+ "\"styleNbr\":\"34_2840_1_21_2\",\"isQuantity\":8,"
        		+ "\"store\":4359,\"clusterId\":3,\"fixtureAllocation\":0.25,"
        		+ "\"fixtureType\":\"RACKS\",\"packId\":null,"
        		+ "\"initialSetPackMultiplier\":\"1\"}");
        
        FieldValueList isFieldValueList = FieldValueList.of(List.of(isFieldValue1, isFieldValue2, isFieldValue3));
        isResult = new TableResult(null, 2L, new PageImpl<>(null, null, 
        		Collections.singleton(isFieldValueList)) );
    }

    private void setProperties() 
    {
        lenient().when(bigQueryConnectionProperties.getMLProjectId()).thenReturn("wmt-mtech-assortment-ml-prod");
        lenient().when(bigQueryConnectionProperties.getMLDataSetName()).thenReturn("aex_pack_opt_non_prod");
        lenient().when(bigQueryConnectionProperties.getRFASPPackOptTableName()).thenReturn("output_stage");

        lenient().when(bigQueryConnectionProperties.getRFAProjectId()).thenReturn("wmt-e12743607538928");
        lenient().when(bigQueryConnectionProperties.getRFADataSetName()).thenReturn("commitment_report_rfa_output_stg");
        lenient().when(bigQueryConnectionProperties.getRFACCStageTable()).thenReturn("rfa_cc_out_parquet");
    }

    private StrategyVolumeDeviationResponse getVolumeDeviationStrategyResponse() 
    {
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
