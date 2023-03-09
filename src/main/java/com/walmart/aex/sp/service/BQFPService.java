package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BQFPServiceProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Service
public class BQFPService {

   @Autowired
   private RestTemplate restTemplate;

   @ManagedConfiguration
   private BQFPServiceProperties bqfpServiceProperties;

   public BQFPService() {
   }

   public BQFPService(RestTemplate restTemplate, BQFPServiceProperties bqfpProperties) {
      this.restTemplate = restTemplate;
      this.bqfpServiceProperties = bqfpProperties;
   }

   public BQFPService(RestTemplate restTemplate) {
      this.restTemplate = restTemplate;
   }

   @Retryable(backoff = @Backoff(delay = 3000))
   public BQFPResponse getBuyQuantityUnits(BQFPRequest request) {
      try {
         final URI uri = createURIWithParams(bqfpServiceProperties.getUrl(), request);
         log.info("BQFP Request: {}", uri);
         final HttpHeaders headers = getHeaders();
         final ResponseEntity<BQFPResponse> respEntity = restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(null, headers), BQFPResponse.class);
         if (respEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Received successful response from BQFP!");
            return respEntity.getBody();
         }

      } catch (URISyntaxException|NullPointerException e) {
         log.error("Error constructing BQFP service url: {}", e.getMessage());
         throw new CustomException("Unable to construct BQFP Service endpoint");
      } catch (RestClientException rce) {
         log.error("Error consuming BQFP service: {}", rce.getMessage());
         throw new CustomException("Unable to reach BQFP Service");
      }
      return null;
   }

   private HttpHeaders getHeaders() {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("wm_consumer.id", bqfpServiceProperties.getConsumerId());
      headers.set("wm_svc.name", bqfpServiceProperties.getSvcName());
      headers.set("wm_svc.env", bqfpServiceProperties.getEnv());
      return headers;
   }

   private static URI createURIWithParams(String url, BQFPRequest request) throws URISyntaxException {
      final String PLAN_ID = "planId";
      final String CHANNEL = "channelId";
      final String FINELINE_NBR = "fineLineNbr";
      return new URIBuilder(url)
              .addParameter(PLAN_ID, String.valueOf(request.getPlanId()))
              .addParameter(CHANNEL, request.getChannel())
              .addParameter(FINELINE_NBR, String.valueOf(request.getFinelineNbr()))
              .build();
   }

   @Recover
   public BQFPResponse recover(Exception e, BQFPRequest request) {
      BQFPResponse response = createDefaultResponse();
      log.error("BQFP service call failed after 3 retries for request : " + request, e);
      return response;
   }

   private BQFPResponse createDefaultResponse() {
      return new BQFPResponse();
   }
}