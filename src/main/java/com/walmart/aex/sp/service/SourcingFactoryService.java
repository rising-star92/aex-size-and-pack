package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsDTO;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.SourcingFactoryServiceProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
@Service
public class SourcingFactoryService {

    @Autowired
    private RestTemplate restTemplate;

    @ManagedConfiguration
    private SourcingFactoryServiceProperties sourcingFactoryServiceProperties;

    @Retryable(backoff = @Backoff(delay = 3000))
    public FactoryDetailsResponse callSourcingFactoryForFactoryDetails(String factoryId) {
        FactoryDetailsResponse factoryDetailsResponse = new FactoryDetailsResponse();
        try {

            HttpHeaders headers = getHeaders();
            String url =  sourcingFactoryServiceProperties.getUrl() + factoryId;
            ResponseEntity<FactoryDetailsDTO> responseEntity = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), FactoryDetailsDTO.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                FactoryDetailsDTO factoryDetailsDTO = responseEntity.getBody();
                if (factoryDetailsDTO != null) {

                    log.info("Fetching factory details for factory ID : {}", factoryId);
                    factoryDetailsResponse.setFactoryName(factoryDetailsDTO.getFactoryName());
                    factoryDetailsResponse.setCountry(factoryDetailsDTO.getAddress().getCountry());
                    factoryDetailsResponse.setCountryCode(factoryDetailsDTO.getAddress().getCountryCode());
                    return factoryDetailsResponse;
                }
            }

        }  catch (RestClientException rce) {
            log.error("Error connecting with Sourcing Factory service: {}", rce.getMessage());
            throw new CustomException("Unable to reach Sourcing Factory service");
        }
        return null;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        List<MediaType> mediaTypeList = new ArrayList<>();
        mediaTypeList.add(MediaType.APPLICATION_JSON);
        headers.setAccept(mediaTypeList);
        headers.add(WM_SVC_ENV, sourcingFactoryServiceProperties.getEnv());
        headers.add(WM_SVC_NAME, sourcingFactoryServiceProperties.getServiceName());
        headers.add(API_TOKEN_KEY, sourcingFactoryServiceProperties.getApiTokenKey());
        headers.add(WM_CONSUMER_ID, sourcingFactoryServiceProperties.getConsumerId());

        return headers;
    }

    @Recover
    public FactoryDetailsResponse recover(Exception e, String factoryId) {
        FactoryDetailsResponse response = createDefaultResponse();
        log.error("Sourcing Factory service call failed after 3 retries for url : " + factoryId, e);
        return response;
    }

    private FactoryDetailsResponse createDefaultResponse() {
        return new FactoryDetailsResponse();
    }
}
