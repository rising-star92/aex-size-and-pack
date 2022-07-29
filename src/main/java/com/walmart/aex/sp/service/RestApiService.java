package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.exception.SizeAndPackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class RestApiService {

    private static final String RESPONSE_URL = "Received response URL {}  Status {} Time taken {} ms";

    @Autowired
    RestTemplate restTemplate;

    public GraphQLResponse getSizeProfiles(String url, String query, Map<String, String> headers, Map<String, Object> data) throws SizeAndPackException {
        log.info("Calling GET URL {} data {}", url, data);
        long startTime = System.currentTimeMillis();
        HttpHeaders httpHeaders = getHttpHeaders(headers);
        ResponseEntity<GraphQLResponse> response;
        try {
            GraphQLRequest graphQLReq = new GraphQLRequest(query, data);
            HttpEntity<GraphQLRequest> request = new HttpEntity<>(graphQLReq, httpHeaders);
            response = restTemplate.exchange(url, HttpMethod.POST, request, GraphQLResponse.class);
            log.info(RESPONSE_URL, url, response.getStatusCode(), (System.currentTimeMillis() - startTime));

            log.info("Response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Exception occurred while sending request to {} and exception {}", url, e.getMessage());
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
}
