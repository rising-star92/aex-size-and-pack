package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.storedistribution.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StoreDistributionServiceTest {

    @Mock
    private BigQueryStoreDistributionService bigQueryStoreDistributionService;
    
    @InjectMocks
    private StoreDistributionService storeDistributionService;
    
    private PackInfoRequest request;
    
    private StoreDistributionData storeDistributionData;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        storeDistributionService = new StoreDistributionService(bigQueryStoreDistributionService, new StoreDistributionMapper());
        setData();
    }

    @Test
    void fetchStoreDistributionResponseWithNoDuplicatesTest() {
        when(bigQueryStoreDistributionService.getStoreDistributionData(Mockito.any())).thenReturn(storeDistributionData);
        StoreDistributionResponse response = storeDistributionService.fetchStoreDistributionResponse(request);

        StoreDistribution storeDistribution = response.getStoreDistributions().get(0);
        InitialSetPlanData initialSetPlanData = storeDistribution.getInitialSetPlanDataList().get(0);
        PackDistribution packDistribution = initialSetPlanData.getPackDistributionList().get(0);
        DistributionMetric distributionMetric = packDistribution.getDistributionMetricList().iterator().next();

        assertEquals(1, response.getStoreDistributions().size());
        assertEquals(1234, storeDistribution.getFinelineNbr());
        assertEquals("Test_Style", storeDistribution.getStyleNbr());
        assertEquals(1, storeDistribution.getInitialSetPlanDataList().size());
        assertEquals(202344L, initialSetPlanData.getInStoreWeek());
        assertEquals(1, initialSetPlanData.getPackDistributionList().size());
        assertEquals("Test_PackId", packDistribution.getPackId());
//        We get three from the response, but the mapper is removing 1 duplicate entry since it has same store and multiplier combination
        assertEquals(2, packDistribution.getDistributionMetricList().size());
        assertEquals(1, distributionMetric.getStore());
        assertEquals(1, distributionMetric.getMultiplier());
    }

    private void setData() throws IOException {
        FinelineData finelineData = new FinelineData();
        finelineData.setFinelineNbr(1234);
        finelineData.setInStoreWeek(202344L);
        finelineData.setPackId("Test_PackId");
        
        PackInfo info = new PackInfo();
        info.setPlanId(1L);
        info.setChannel("Store");
        info.setFinelineDataList(List.of(finelineData));
        
        request = new PackInfoRequest();
        request.setPackInfoList(List.of(info));

        StoreDistributionDTO storeDistributionDTO1 = new StoreDistributionDTO();
        storeDistributionDTO1.setStore(1);
        storeDistributionDTO1.setInStoreWeek(202344L);
        storeDistributionDTO1.setPackMultiplier(1);
        storeDistributionDTO1.setCc("Test_CC1");
        storeDistributionDTO1.setFinelineNbr(1234);
        storeDistributionDTO1.setStyleNbr("Test_Style");
        storeDistributionDTO1.setPackId("Test_PackId");
        storeDistributionDTO1.setClusterId(1);
        storeDistributionDTO1.setFixtureAllocation(1.0F);
        storeDistributionDTO1.setFixtureType("RACKS");
        storeDistributionDTO1.setProductFineline("1_1234");

        StoreDistributionDTO storeDistributionDTO2 = new StoreDistributionDTO();
        storeDistributionDTO2.setStore(1);
        storeDistributionDTO2.setInStoreWeek(202344L);
        storeDistributionDTO2.setPackMultiplier(1);
        storeDistributionDTO2.setCc("Test_CC2");
        storeDistributionDTO2.setFinelineNbr(1234);
        storeDistributionDTO2.setStyleNbr("Test_Style");
        storeDistributionDTO2.setPackId("Test_PackId");
        storeDistributionDTO2.setClusterId(1);
        storeDistributionDTO2.setFixtureAllocation(1.0F);
        storeDistributionDTO2.setFixtureType("RACKS");
        storeDistributionDTO2.setProductFineline("1_1234");

        StoreDistributionDTO storeDistributionDTO3 = new StoreDistributionDTO();
        storeDistributionDTO3.setStore(2);
        storeDistributionDTO3.setInStoreWeek(202344L);
        storeDistributionDTO3.setPackMultiplier(1);
        storeDistributionDTO3.setCc("Test_CC1");
        storeDistributionDTO3.setFinelineNbr(1234);
        storeDistributionDTO3.setStyleNbr("Test_Style");
        storeDistributionDTO3.setPackId("Test_PackId");
        storeDistributionDTO3.setClusterId(1);
        storeDistributionDTO3.setFixtureAllocation(1.0F);
        storeDistributionDTO3.setFixtureType("RACKS");
        storeDistributionDTO3.setProductFineline("1_1234");

        storeDistributionData = new StoreDistributionData();
        storeDistributionData.setStoreDistributionList(List.of(storeDistributionDTO1, storeDistributionDTO2, storeDistributionDTO3));
    }
}