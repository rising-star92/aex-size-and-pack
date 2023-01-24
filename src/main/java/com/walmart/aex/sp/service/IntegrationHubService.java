package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class IntegrationHubService {

    @Autowired
    private RestTemplate restTemplate;

    @ManagedConfiguration
    private IntegrationHubServiceProperties integrationHubServiceProperties;

    public IntegrationHubService() {

    }

    public IntegrationHubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(backoff = @Backoff(delay = 3000))
    public ResponseEntity<IntegrationHubResponseDTO> callIntegrationHubForPackOpt(IntegrationHubRequestDTO integrationHubRequestDTO) {
        try {
            final HttpHeaders headers = getHeaders();
            String url = integrationHubServiceProperties.getUrl();
            final ResponseEntity<IntegrationHubResponseDTO> respEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(integrationHubRequestDTO, headers), IntegrationHubResponseDTO.class);
            if (respEntity.getStatusCode().is2xxSuccessful()) {
                return respEntity;
            }
        }  catch (RestClientException rce) {
            log.error("Error connecting with Integration Hub service: {}", rce.getMessage());
            throw new CustomException("Unable to reach Integration Hub service");
        }
        return null;
    }



    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);
        return headers;
    }

    @Recover
    public RunPackOptResponse recover(Exception e, RunPackOptRequest request) {
        RunPackOptResponse response = createDefaultResponse();
        log.error("IntegrationHub service call failed after 3 retries for url : " + request, e);
        return response;
    }

    private RunPackOptResponse createDefaultResponse() {
        return new RunPackOptResponse();
    }


}
