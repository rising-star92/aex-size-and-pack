package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StoreClusterMap;
import com.walmart.aex.sp.dto.storedistribution.*;
import com.walmart.aex.sp.enums.ChannelType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;

public class StoreDistributionServiceTest {

    @Mock
    private BigQueryStoreDistributionService bigQueryStoreDistributionService;

    @Mock
    private StoreDistributionMapper storeDistributionMapper;

    @Mock
    private StoreClusterService storeClusterService;

    @InjectMocks
    private StoreDistributionService storeDistributionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFetchStoreDistributionResponse() {
        PackInfoRequest request = new PackInfoRequest();
        List<PackInfo> packInfoList = new ArrayList<>();
        PackInfo packInfo = new PackInfo();
        packInfo.setChannel(ChannelType.STORE.name());
        packInfoList.add(packInfo);
        request.setPackInfoList(packInfoList);

        StoreDistributionDTO storeDistributionDTO = new StoreDistributionDTO();
        List<StoreDistributionDTO> storeDistributionDTOS = new ArrayList<>();
        storeDistributionDTOS.add(storeDistributionDTO);

        when(bigQueryStoreDistributionService.getStoreDistributionData(any())).thenReturn(new StoreDistributionData());
        when(storeClusterService.fetchPOStoreClusterGrouping(any(), any())).thenReturn(new StoreClusterMap());

        StoreDistributionResponse result = storeDistributionService.fetchStoreDistributionResponse(request);

        assertNotNull(result);
        verify(bigQueryStoreDistributionService, times(1)).getStoreDistributionData(any());
        verify(storeDistributionMapper, times(1)).mapStoreDistributionResponse(any(), any());
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