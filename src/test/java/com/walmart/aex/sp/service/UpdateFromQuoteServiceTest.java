package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.dto.quote.PLMAcceptedQuoteFineline;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.CcPackOptimizationID;
import com.walmart.aex.sp.entity.StylePackOptimization;
import com.walmart.aex.sp.entity.StylePackOptimizationID;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class UpdateFromQuoteServiceTest {

    @Mock
    PLMQuoteService plmQuoteService;

    @Mock
    CcPackOptimizationRepository ccPackOptimizationRepository;

    @Mock
    SourcingFactoryService sourcingFactoryService;

    @InjectMocks
    UpdateFromQuoteService updateFromQuoteService;

    @Spy
    ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
        updateFromQuoteService = new UpdateFromQuoteService(plmQuoteService, ccPackOptimizationRepository, sourcingFactoryService);
    }

    @Test
    void updateFactoryFromApproveQuotesSuccessTest() {
        List<PLMAcceptedQuoteFineline> plmAcceptedQuoteFinelines = getPLMAcceptedQuoteFinelines();
        List<CcPackOptimization> ccPackOptimizations = getCcPackOptimization();
        Mockito.when(sourcingFactoryService.callSourcingFactoryForFactoryDetails(Mockito.anyString())).thenReturn(getFactorObject());
        Mockito.when(plmQuoteService.getApprovedQuoteFromPlm(Mockito.anyLong(), Mockito.any())).thenReturn(plmAcceptedQuoteFinelines);
        Mockito.when(ccPackOptimizationRepository.findCCPackOptimizationByFineLineNbr(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(ccPackOptimizations);
        StatusResponse response = updateFromQuoteService.updateFactoryFromApproveQuotes(getRunPackOptRequest());
        Assert.assertEquals("Success", response.getStatus());
    }

    @Test
    void updateFactoryFromApproveQuotesFailureTest(){
        StatusResponse response = updateFromQuoteService.updateFactoryFromApproveQuotes(new RunPackOptRequest());
        Assert.assertEquals("Failed", response.getStatus());
    }

    private RunPackOptRequest getRunPackOptRequest() {
        RunPackOptRequest runPackOptRequest = new RunPackOptRequest();
        runPackOptRequest.setPlanId(12L);
        String inputJson = "{\"lvl0Nbr\":50000,\"lvl1Nbr\":34,\"lvl2Nbr\":6419,\"lvl3List\":[{\"lvl3Nbr\":12231,\"lvl4List\":[{\"lvl4Nbr\":31513,\"finelines\":[{\"finelineNbr\":5151}]}]}]}";
        try {
            InputRequest inputRequest = mapper.readValue(inputJson, InputRequest.class);
            runPackOptRequest.setInputRequest(inputRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return runPackOptRequest;
    }

    private List<PLMAcceptedQuoteFineline> getPLMAcceptedQuoteFinelines() {
        List<PLMAcceptedQuoteFineline> plmAcceptedQuoteFinelines = new ArrayList<>();
        String plmQuoteJson = "{\"planId\":12,\"lvl0Nbr\":50000,\"lvl1Nbr\":34,\"lvl2Nbr\":6419,\"lvl3Nbr\":12231,\"lvl4Nbr\":31513,\"finelineNbr\":5151,\"plmAcceptedQuoteStyles\":[{\"styleNbr\":\"34_5151_3_22_6\",\"plmAcceptedQuoteCcs\":[{\"customerChoice\":\"34_5151_3_22_6_BATIK PATCHWORK_PEACH GLAZE\",\"plmAcceptedQuotes\":[{\"quoteId\":1754513,\"sizes\":[\"S\",\"M\",\"L\",\"XL\"],\"firstCost\":5.58,\"landedCost\":null,\"vsn\":\"TS23100122626\",\"factoryId\":28029595,\"supplierNbr\":13040,\"supplier8Nbr\":28001704,\"supplier9Nbr\":13040342,\"supplierName\":\"G-III LEATHER FASHIONS, INC.\",\"supplierType\":null,\"countryOfOrigin\":\"INDIA\",\"portOfOrigin\":null},{\"quoteId\":1754514,\"sizes\":[\"XL\",\"XXL\"],\"firstCost\":6.09,\"landedCost\":null,\"vsn\":\"TS23100122626A\",\"factoryId\":100037308,\"supplierNbr\":13040,\"supplier8Nbr\":28001704,\"supplier9Nbr\":13040342,\"supplierName\":\"G-III LEATHER FASHIONS, INC.\",\"supplierType\":null,\"countryOfOrigin\":\"INDIA\",\"portOfOrigin\":null}]}]}]}";
        try {
            PLMAcceptedQuoteFineline plmAcceptedQuoteFineline = mapper.readValue(plmQuoteJson, PLMAcceptedQuoteFineline.class);
            plmAcceptedQuoteFinelines.add(plmAcceptedQuoteFineline);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return plmAcceptedQuoteFinelines;
    }

    private List<CcPackOptimization> getCcPackOptimization() {
        List<CcPackOptimization> ccPackOptimizations = new ArrayList<>();
        CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID();
        StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID();
        stylePackOptimizationID.setStyleNbr("34_5151_3_22_6");
        ccPackOptimizationID.setStylePackOptimizationID(stylePackOptimizationID);
        ccPackOptimizationID.setCustomerChoice("34_5151_3_22_6_BATIK PATCHWORK_PEACH GLAZE");
        CcPackOptimization ccPackOptimization = new CcPackOptimization();
        ccPackOptimization.setCcPackOptimizationId(ccPackOptimizationID);
        StylePackOptimization stylePackOptimization = new StylePackOptimization();
        stylePackOptimization.setStylePackoptimizationId(stylePackOptimizationID);
        ccPackOptimization.setStylePackOptimization(stylePackOptimization);
        ccPackOptimizations.add(ccPackOptimization);
        return ccPackOptimizations;
    }

    private FactoryDetailsResponse getFactorObject() {
        FactoryDetailsResponse factoryDetails = new FactoryDetailsResponse();
        factoryDetails.setFactoryName("Test");
        return factoryDetails;
    }
}
