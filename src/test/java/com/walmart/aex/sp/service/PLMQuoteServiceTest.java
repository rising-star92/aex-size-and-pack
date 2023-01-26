package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.quote.PLMAcceptedQuoteFineline;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.PLMServiceProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PLMQuoteServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PLMServiceProperties plmServiceProperties;

    @InjectMocks
    private PLMQuoteService plmQuoteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(plmServiceProperties.getPlmApiBaseURL()).thenReturn("https://walmart.com/");
        when(plmServiceProperties.getPlmEnv()).thenReturn("stage");
        when(plmServiceProperties.getPlmConsumerId()).thenReturn("121123123");
        when(plmServiceProperties.getPlmServiceName()).thenReturn("test");
    }

    @Test
    void successResponseReturnsFromPLMService() {
        ResponseEntity<List<PLMAcceptedQuoteFineline>> response = ResponseEntity.status(HttpStatus.OK).body(getPLMAcceptedQuoteFinelines());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class), anyLong())).thenReturn(response);
        List<PLMAcceptedQuoteFineline> result = plmQuoteService.getApprovedQuoteFromPlm(12L, HttpMethod.GET);
        assertNotNull(result);
    }

    @Test
    void failResponseReturnsFromPLMService() {
        ResponseEntity<List<PLMAcceptedQuoteFineline>> response = ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), any(ParameterizedTypeReference.class), anyLong())).thenReturn(response);
        Exception exception = assertThrows(CustomException.class, () -> {
            plmQuoteService.getApprovedQuoteFromPlm(12L, HttpMethod.GET);
        });
        String expectedMessage = "Error getting Data from PLM service";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    private List<PLMAcceptedQuoteFineline> getPLMAcceptedQuoteFinelines() {
        List<PLMAcceptedQuoteFineline> plmAcceptedQuoteFinelines = new ArrayList<>();
        String plmQuoteJson = "{\"planId\":12,\"lvl0Nbr\":50000,\"lvl1Nbr\":34,\"lvl2Nbr\":6419,\"lvl3Nbr\":12231,\"lvl4Nbr\":31513,\"finelineNbr\":5151,\"plmAcceptedQuoteStyles\":[{\"styleNbr\":\"34_5151_3_22_6\",\"plmAcceptedQuoteCcs\":[{\"customerChoice\":\"34_5151_3_22_6_BATIK PATCHWORK_PEACH GLAZE\",\"plmAcceptedQuotes\":[{\"quoteId\":1754513,\"sizes\":[\"S\",\"M\",\"L\",\"XL\"],\"firstCost\":5.58,\"landedCost\":null,\"vsn\":\"TS23100122626\",\"factoryId\":28029595,\"supplierNbr\":13040,\"supplier8Nbr\":28001704,\"supplier9Nbr\":13040342,\"supplierName\":\"G-III LEATHER FASHIONS, INC.\",\"supplierType\":null,\"countryOfOrigin\":\"INDIA\",\"portOfOrigin\":null},{\"quoteId\":1754514,\"sizes\":[\"XL\",\"XXL\"],\"firstCost\":6.09,\"landedCost\":null,\"vsn\":\"TS23100122626A\",\"factoryId\":100037308,\"supplierNbr\":13040,\"supplier8Nbr\":28001704,\"supplier9Nbr\":13040342,\"supplierName\":\"G-III LEATHER FASHIONS, INC.\",\"supplierType\":null,\"countryOfOrigin\":\"INDIA\",\"portOfOrigin\":null}]}]}]}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            PLMAcceptedQuoteFineline plmAcceptedQuoteFineline = mapper.readValue(plmQuoteJson, PLMAcceptedQuoteFineline.class);
            plmAcceptedQuoteFinelines.add(plmAcceptedQuoteFineline);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return plmAcceptedQuoteFinelines;
    }
}
