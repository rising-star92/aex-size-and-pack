package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.APRequest;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.gql.Error;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.gql.Payload;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.GraphQLProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StrategyFetchServiceTest {

    @Mock
    GraphQLService graphQLService;
    @Mock
    GraphQLProperties graphQLProperties;

    @InjectMocks
    StrategyFetchService strategyFetchService;

    GraphQLResponse graphQLResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
//        strategyFetchService = new StrategyFetchService(graphQLService, graphQLProperties);
        setGraphQlResponse();
        setGraphQLProperties();
    }

    @Test
    void getBuyQtyResponseSizeProfileTest() throws SizeAndPackException {
        BuyQtyRequest request = getBuyQtyRequest();
        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        BuyQtyResponse response = strategyFetchService.getBuyQtyResponseSizeProfile(request);
        assertEquals(1L, response.getPlanId());
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    @Test
    void getBuyQtyResponseSizeProfileNullTest() throws SizeAndPackException {
        BuyQtyRequest request = getBuyQtyRequest();
        graphQLResponse.getErrors().add(0, new Error());
        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        BuyQtyResponse response = strategyFetchService.getBuyQtyResponseSizeProfile(request);
        assertNull(response);
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    @Test
    void getBuyQtyDetailsForFinelinesTest() throws SizeAndPackException {
        BuyQtyRequest request = getBuyQtyRequest();
        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        BuyQtyResponse response = strategyFetchService.getBuyQtyDetailsForFinelines(request);
        assertEquals(1L, response.getPlanId());
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    @Test
    void getBuyQtyDetailsForStylesCcTest() throws SizeAndPackException {
        BuyQtyRequest request = getBuyQtyRequest();
        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        BuyQtyResponse response = strategyFetchService.getBuyQtyDetailsForStylesCc(request, 123);
        assertEquals(1L, response.getPlanId());
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    @Test
    void getAPRunFixtureAllocationOutputTest() throws SizeAndPackException {
        APRequest request = new APRequest();
        request.setPlanId(1L);
        request.setFinelineNbr(1234);
        request.setVolumeDeviationLevel("Fineline");

        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        APResponse response = strategyFetchService.getAPRunFixtureAllocationOutput(request);
        assertEquals(1L, response.getRfaSizePackData().get(0).getPlan_id_partition());
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    @Test
    void getAllCcSizeProfilesTest() throws SizeAndPackException {
        BuyQtyRequest request = getBuyQtyRequest();

        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        BuyQtyResponse response = strategyFetchService.getAllCcSizeProfiles(request);
        assertEquals(1L, response.getPlanId());
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    @Test
    void getStrategyVolumeDeviationTest() throws SizeAndPackException {

        when(graphQLService.post(anyString(), anyString(), anyMap(), anyMap())).thenReturn(graphQLResponse);

        StrategyVolumeDeviationResponse response = strategyFetchService.getStrategyVolumeDeviation(1L, 1234);
        assertEquals(1L, response.getFinelines().get(0).getPlanId());
        assertEquals("Category", response.getFinelines().get(0).getVolumeDeviationLevel());
        verify(graphQLService, times(1)).post(anyString(), anyString(), anyMap(), anyMap());
    }

    private BuyQtyRequest getBuyQtyRequest() {
        BuyQtyRequest request = new BuyQtyRequest();
        request.setPlanId(1L);
        request.setChannel("store");
        request.setPlanDesc("S3");
        request.setLvl3Nbr(12);
        request.setLvl4Nbr(123);
        request.setFinelineNbr(1234);
        request.setStyleNbr("123_1234_4321");
        request.setCcId("123_1234_4321 Blue Soot");

        return request;
    }

    private void setGraphQLProperties() {
        lenient().when(graphQLProperties.getSizeProfileUrl()).thenReturn("sizeProfileUrl");
        lenient().when(graphQLProperties.getSizeProfileQuery()).thenReturn("sizeProfileQuery");
        lenient().when(graphQLProperties.getBuyQtyFinelinesSizeQuery()).thenReturn("finelineSizeQuery");
        lenient().when(graphQLProperties.getBuyQtyStyleCcSizeQuery()).thenReturn("styleCcSizeQuery");
        lenient().when(graphQLProperties.getAllCcSizeProfileQuery()).thenReturn("ccSizeProfileQuery");
        lenient().when(graphQLProperties.getStrategyVolumeDeviationLevel()).thenReturn("strategyVolumeQuery");
        lenient().when(graphQLProperties.getSizeProfileConsumerId()).thenReturn("548547323");
        lenient().when(graphQLProperties.getSizeProfileConsumerName()).thenReturn("AEX_SIZE_AND_PACK");
        lenient().when(graphQLProperties.getSizeProfileConsumerEnv()).thenReturn("test");

        lenient().when(graphQLProperties.getAssortProductUrl()).thenReturn("assortProductUrl");
        lenient().when(graphQLProperties.getAssortProductRFAQuery()).thenReturn("assortProductQuery");
        lenient().when(graphQLProperties.getAssortProductConsumerId()).thenReturn("548547323");
        lenient().when(graphQLProperties.getAssortProductConsumerName()).thenReturn("AEX_ASSORT_PRODUCT");
        lenient().when(graphQLProperties.getAssortProductConsumerEnv()).thenReturn("test");
    }

    private void setGraphQlResponse() {
        BuyQtyResponse buyQtyResponse = new BuyQtyResponse();
        buyQtyResponse.setPlanId(1L);
        buyQtyResponse.setPlanDesc("S3");

        RFASizePackData rfaSizePackData = new RFASizePackData();
        rfaSizePackData.setPlan_id_partition(1L);
        rfaSizePackData.setFineline_nbr(1234);
        rfaSizePackData.setFixture_type("WALLS");
        rfaSizePackData.setSize_cluster_id(1);
        rfaSizePackData.setFixture_group(1.0F);

        APResponse apResponse = new APResponse();
        apResponse.setRfaSizePackData(List.of(rfaSizePackData));

        FinelineVolumeDeviationDto finelineVolumeDeviationDto = new FinelineVolumeDeviationDto();
        finelineVolumeDeviationDto.setPlanId(1L);
        finelineVolumeDeviationDto.setFinelineId(1234);
        finelineVolumeDeviationDto.setVolumeDeviationLevel("Category");

        StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = new StrategyVolumeDeviationResponse();
        strategyVolumeDeviationResponse.setFinelines(List.of(finelineVolumeDeviationDto));

        Payload payload = new Payload();
        payload.setGetCcSizeClus(buyQtyResponse);
        payload.setGetFinelinesWithSizeAssociation(buyQtyResponse);
        payload.setGetStylesCCsWithSizeAssociation(buyQtyResponse);
        payload.setGetRFADataFromSizePack(apResponse);
        payload.setGetAllCcSizeClus(buyQtyResponse);
        payload.setGetVolumeDeviationStrategySelection(strategyVolumeDeviationResponse);

        graphQLResponse = new GraphQLResponse();
        graphQLResponse.setData(payload);
        graphQLResponse.setErrors(new ArrayList<>());
    }
}