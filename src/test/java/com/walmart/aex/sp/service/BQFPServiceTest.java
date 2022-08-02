package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BQFPServiceProperties;
import com.walmart.aex.sp.service.BQFPService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

public class BQFPServiceTest {

   @Mock
   private RestTemplate restTemplate;

   @Mock
   private BQFPServiceProperties properties;

   private BQFPService bqfpService;

   @Before
   public void init() {
      MockitoAnnotations.openMocks(this);
      bqfpService = new BQFPService(restTemplate, properties);
   }

   @Test
   public void successResponseReturnsBQFPResponse() {
      ResponseEntity<BQFPResponse> response = ResponseEntity.status(HttpStatus.OK).body(successResponse());
      when(properties.getUrl()).thenReturn("https://bqfp.dev/flow-plan/v1/getBuyQuantityFromFlowPlan");
      when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class))).thenReturn(response);
      BQFPResponse result = bqfpService.getBuyQuantityUnits(new BQFPRequest(485, "1", 572));
      verify(restTemplate, times(1)).exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class));
      assertNotNull(result);
   }

   @Test(expected = CustomException.class)
   public void exceptionThrownWithInvalidURI() {
      bqfpService.getBuyQuantityUnits(new BQFPRequest(485, "1", 572));
   }

   @Test(expected = CustomException.class)
   public void exceptionThrownWhenNonSuccessResponseFromBQFP() {
      when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class))).thenThrow(new RestClientException("Bad Request"));
      bqfpService.getBuyQuantityUnits(new BQFPRequest(485, "1", 572));
   }

   private BQFPResponse successResponse() {
      BQFPResponse response = new BQFPResponse();
      response.setPlanId(485L);
      response.setFinelineNbr(572);
      return response;
   }

}
