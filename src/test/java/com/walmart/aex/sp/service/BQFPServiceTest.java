package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BQFPServiceProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BQFPServiceTest {

   @Mock
   private RestTemplate restTemplate;

   @Mock
   private BQFPServiceProperties properties;

   @InjectMocks
   private BQFPService bqfpService;

   @BeforeEach
   public void init() {
      MockitoAnnotations.openMocks(this);
      bqfpService = new BQFPService(restTemplate, properties);
   }

   @Test
   public void successResponseReturnsBQFPResponse() {
      ResponseEntity<BQFPResponse> response = ResponseEntity.status(HttpStatus.OK).body(successResponse());
      when(properties.getUrl()).thenReturn("https://bqfp.dev/flow-plan/v1/getBuyQuantityFromFlowPlan");
      when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class))).thenReturn(response);
      BQFPResponse result = bqfpService.getBuyQuantityUnits(new BQFPRequest(485L, "1", 572));
      verify(restTemplate, times(1)).exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class));
      assertNotNull(result);
   }

   @Test
   public void exceptionThrownWithInvalidURI() {

      Exception exception = assertThrows(CustomException.class, () -> {
         bqfpService.getBuyQuantityUnits(new BQFPRequest(485L, "1", 572));
      });
      String expectedMessage = "Unable to construct BQFP Service endpoint";
      String actualMessage = exception.getMessage();

      assertTrue(actualMessage.contains(expectedMessage));
   }

   @Test
   public void exceptionThrownWhenNonSuccessResponseFromBQFP() {
      when(properties.getUrl()).thenReturn("https://bqfp.dev/flow-plan/v1/getBuyQuantityFromFlowPlan");
      when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class))).thenThrow(new RestClientException("Bad Request"));
      Exception exception = assertThrows(CustomException.class, () -> {
         bqfpService.getBuyQuantityUnits(new BQFPRequest(485L, "1", 572));
      });
      String expectedMessage = "Unable to reach BQFP Service";
      String actualMessage = exception.getMessage();

      assertTrue(actualMessage.contains(expectedMessage));
   }

   @Test
   public void createDefaultConstructorTest() {
      bqfpService = new BQFPService();
      assertNotNull(bqfpService);
   }

   @Test
   public void createBQFPServiceWithRestTemplateConstructor() {
      bqfpService = new BQFPService(restTemplate);
      assertNotNull(bqfpService);
   }

   @Test
   public void handleReturnNullTest() {
      ResponseEntity<BQFPResponse> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
      when(properties.getUrl()).thenReturn("https://bqfp.dev/flow-plan/v1/getBuyQuantityFromFlowPlan");
      when(restTemplate.exchange(any(URI.class), eq(HttpMethod.GET), any(), eq(BQFPResponse.class))).thenReturn(response);
      BQFPResponse result = bqfpService.getBuyQuantityUnits(new BQFPRequest(485L, "1", 572));
      assertNull(result);
   }



   private BQFPResponse successResponse() {
      BQFPResponse response = new BQFPResponse();
      response.setPlanId(485L);
      response.setFinelineNbr(572);
      return response;
   }

}
