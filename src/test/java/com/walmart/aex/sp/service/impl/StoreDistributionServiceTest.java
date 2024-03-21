package com.walmart.aex.sp.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.StoreClusterMap;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.service.StoreClusterService;
import com.walmart.aex.sp.dto.storedistribution.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.service.BigQueryStoreDistributionService;
import com.walmart.aex.sp.service.StoreDistributionMapper;
import com.walmart.aex.sp.service.StoreDistributionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class StoreDistributionServiceTest {

    @Mock
    private BigQueryStoreDistributionService bigQueryStoreDistributionService;

    @Mock
    private StoreDistributionMapper storeDistributionMapper;

    @Mock
    private StoreClusterService storeClusterService;

    @InjectMocks
    private StoreDistributionService storeDistributionService;

    private ObjectMapper objectMapper;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void testFetchStoreDistributionResponse() throws SizeAndPackException, IOException {
        PackInfoRequest request = new PackInfoRequest();
        FinelineData finelineData = new FinelineData();
        finelineData.setFinelineNbr(1234);
        finelineData.setInStoreWeek(202344L);
        finelineData.setPackId("Test_PackId");
        finelineData.setGroupingType("onshore");

        PackInfo info = new PackInfo();
        info.setPlanId(1L);
        info.setChannel("Store");
        info.setSeason("S1");
        info.setFiscalYear("2025");
        info.setFinelineDataList(List.of(finelineData));

        request = new PackInfoRequest();
        request.setPackInfoList(List.of(info));

        StoreDistributionDTO storeDistributionDTO = new StoreDistributionDTO();
        List<StoreDistributionDTO> storeDistributionDTOS = new ArrayList<>();
        storeDistributionDTOS.add(storeDistributionDTO);

        File storeClusterInfoResponseFile = new File(Objects.requireNonNull(this.getClass()
                .getResource("/data/storeClusterServiceResponse.json")).getFile());
        StoreClusterMap storeClusterResponse = objectMapper.readValue(storeClusterInfoResponseFile, StoreClusterMap.class);

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

        StoreDistributionData storeDistributionData = new StoreDistributionData();
        storeDistributionData.setStoreDistributionList(List.of(storeDistributionDTO1, storeDistributionDTO2, storeDistributionDTO3));

        when(bigQueryStoreDistributionService.getStoreDistributionData(any())).thenReturn(storeDistributionData);
        when(storeClusterService.fetchPOStoreClusterGrouping(any(), any())).thenReturn(storeClusterResponse);

        StoreDistributionResponse result = storeDistributionService.fetchStoreDistributionResponse(request);

        assertNotNull(result);
        verify(bigQueryStoreDistributionService, times(1)).getStoreDistributionData(any());
        verify(storeDistributionMapper, times(3)).mapStoreDistributionResponse(any(), any());
    }

    @Test
    public void testFetchStoreDistributionResponse_Exception() {
        PackInfoRequest request = new PackInfoRequest();
        List<PackInfo> packInfoList = new ArrayList<>();
        PackInfo packInfo = new PackInfo();
        packInfoList.add(packInfo);
        request.setPackInfoList(packInfoList);

        when(bigQueryStoreDistributionService.getStoreDistributionData(any())).thenThrow(RuntimeException.class);

        StoreDistributionResponse result = storeDistributionService.fetchStoreDistributionResponse(request);

        assertNotNull(result);
    }

    @Test
    public void testFetchStoreDistributionResponse_NullRequest() {
        PackInfoRequest request = null;

        StoreDistributionResponse result = storeDistributionService.fetchStoreDistributionResponse(request);

        assertNotNull(result);
    }


}