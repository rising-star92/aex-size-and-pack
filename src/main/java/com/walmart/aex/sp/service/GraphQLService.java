package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.gql.GraphQLRequest;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsRequest;
import com.walmart.aex.sp.dto.historicalmetrics.HistoricalMetricsResponse;
import com.walmart.aex.sp.exception.SizeAndPackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
/**
 * Generic service which can call and retrieve a response from a GraphQL endpoint
 */
public class GraphQLService {

    @Autowired
    RestTemplate restTemplate;

    @Retryable(backoff = @Backoff(delay = 3000))
    public GraphQLResponse post(String url, String query, Map<String, String> headers, Map<String, Object> data) throws SizeAndPackException {
        log.debug("Request: url: {}, query: {}, data: {}", url, query, data);
        long startTime = System.currentTimeMillis();
        HttpHeaders httpHeaders = getHttpHeaders(headers);
        ResponseEntity<GraphQLResponse> response;
        try {
            GraphQLRequest graphQLReq = new GraphQLRequest(query, data);
            HttpEntity<GraphQLRequest> request = new HttpEntity<>(graphQLReq, httpHeaders);
            response = restTemplate.exchange(url, HttpMethod.POST, request, GraphQLResponse.class);
            log.debug("Response: url: {} status: {} time: {}ms, body: {}",
                  url, response.getStatusCode(), (System.currentTimeMillis() - startTime), response.getBody());

        } catch (Exception e) {
            log.error("Request failed.  url: {}, query: {}, data: {}, error: {}", url, query, data, e);
            throw new SizeAndPackException("Unable to call api " + url);
        }
        return response.getBody();
    }

    private HttpHeaders getHttpHeaders(Map<String, String> headers) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
        httpHeaders.setCacheControl(CacheControl.noCache());
        if (headers != null && !headers.isEmpty()) {
            headers.forEach(httpHeaders::add);
        }
        return httpHeaders;
    }

    @Recover
    public GraphQLResponse recover(Exception e, String url, String query, Map<String, String> headers, Map<String, Object> data) {
        GraphQLResponse response = createDefaultResponse();
        log.error("GraphQL service call failed after 3 retries for url : " + url, e);
        return response;
    }

    private GraphQLResponse createDefaultResponse() {
        return new GraphQLResponse();
    }
}
