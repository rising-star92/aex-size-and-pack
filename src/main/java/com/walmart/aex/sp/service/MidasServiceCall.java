package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import com.walmart.aex.sp.dto.midas.MidasResponse;
import com.walmart.aex.sp.dto.midas.Payload;
import com.walmart.aex.sp.dto.midas.Result;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.MidasApiProperties;
import com.walmart.aex.sp.properties.SecretsProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

@Service
@Slf4j
public class MidasServiceCall {

   private static final String DEFAULT_HISTORICAL_METRICS_QUERY = "{\"query\":{\"select\":[{\"field\":\"*\"}],\"from\":\"get_historical_size_metrics_fineline_cc\",\"params\":{\"finelineNbr\":%d,\"lyCompWeekStart\":%d,\"lyCompWeekEnd\":%d,\"lvl0Nbr\":%d,\"lvl1Nbr\":%d,\"lvl2Nbr\":%d,\"lvl3Nbr\":%d,\"lvl4Nbr\":%d,\"channel\":\"%s\"}}}";

   @Autowired
   private RestTemplate restTemplate;

   @ManagedConfiguration
   private MidasApiProperties midasProperties;

   @Autowired
   SecretsProperties secretsProperties;

   @Retryable(backoff = @Backoff(delay = 1000))
   public HistoricalMetricsResponse fetchHistoricalMetrics(HistoricalMetricsRequest request) {
      HistoricalMetricsResponse response = createDefaultResponse();
      try {
         final String query = midasProperties.getMidasHistoricalMetricsQuery() == null
               ? DEFAULT_HISTORICAL_METRICS_QUERY : midasProperties.getMidasHistoricalMetricsQuery();
         final String midasRequest = formatMidasQuery(query, request);
         String url = midasProperties.getMidasApiBaseURL();
         log.info("Invoking Midas API for Create event with URL : {} and query : {}", url, midasRequest);

         ResponseEntity<MidasResponse> result =
               restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(midasRequest, getHeadersForMidas()), MidasResponse.class);

         if (!result.getStatusCode().is2xxSuccessful())
            return response;

         if (result.getBody() != null && !CollectionUtils.isEmpty(result.getBody().getErrors())) {
            log.error("Error retrieving historical size metrics: {}", result.getBody().getErrors());
         } else {
            response.setMetrics(Optional.ofNullable(result.getBody()).stream()
                  .map(MidasResponse::getPayload)
                  .map(Payload::getResult)
                  .map(Result::getResponse)
                  .findFirst().orElse(new ArrayList<>()));
         }
      } catch (Exception e) {
         throw new CustomException("Exception in fetching historical metrics: " + e);
      }
      return response;
   }

   @Recover
   public HistoricalMetricsResponse recover(Exception e, HistoricalMetricsRequest request) {
      HistoricalMetricsResponse response = createDefaultResponse();
      log.error("Failed midas call after 3 retries for historical metrics : ", e);
      return response;
   }

   private HttpHeaders getHeadersForMidas() throws IOException {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
      headers.set("consumer", midasProperties.getMidasHeaderConsumer());
      headers.set("signature_key_version", midasProperties.getMidasHeaderSignatureKeyVersion());
      headers.set("signature_ts", midasProperties.getMidasHeaderSignatureTS());
      headers.set("signature_auth_flag", midasProperties.getMidasHeaderSignatureAuthFlag());
      headers.set("request_ts", String.valueOf(Instant.now().getEpochSecond()));
      headers.set("tenant", midasProperties.getMidasHeaderTenant());
      headers.set("Authorization", secretsProperties.fetchMidasAPIAuthorization());
      return headers;
   }

   private String formatMidasQuery(String query, HistoricalMetricsRequest request) {
      Integer finelineNbr = request.getFinelineNbr();
      Integer lyCompWeekStart = request.getLyCompWeekStart();
      Integer lyCompWeekEnd = request.getLyCompWeekEnd();
      Integer lvl0Nbr = request.getLvl0Nbr();
      Integer lvl1Nbr = request.getLvl1Nbr();
      Integer lvl2Nbr = request.getLvl2Nbr();
      Integer lvl3Nbr = request.getLvl3Nbr();
      Integer lvl4Nbr = request.getLvl4Nbr();
      String channel = wrapTicks(request.getChannel());
      return String.format(query, finelineNbr, lyCompWeekStart, lyCompWeekEnd, lvl0Nbr, lvl1Nbr, lvl2Nbr, lvl3Nbr, lvl4Nbr, channel);
   }

   private HistoricalMetricsResponse createDefaultResponse() {
      return new HistoricalMetricsResponse(new ArrayList<>());
   }

   private String wrapTicks(String val) {
      return String.format("'%s'", val);
   }
}
