package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.quote.PLMAcceptedQuoteFineline;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.PLMServiceProperties;
import com.walmart.aex.sp.util.CommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PLMQuoteService {

    public static final String ERROR = "Error getting Data from PLM service: ";

    @Autowired
    private RestTemplate restTemplate;

    @ManagedConfiguration
    public PLMServiceProperties plmServiceProperties;

    @Retryable(exclude = HttpClientErrorException.BadRequest.class, backoff = @Backoff(delay = 1000))
    public List<PLMAcceptedQuoteFineline> getApprovedQuoteFromPlm(Long planId, HttpMethod httpMethod) {
        List<PLMAcceptedQuoteFineline> responseMsg = null;
        try {
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("WM_CONSUMER.ID", plmServiceProperties.getPlmConsumerId());
            headerMap.put("WM_SVC.NAME", plmServiceProperties.getPlmServiceName());
            headerMap.put("WM_SVC.ENV", plmServiceProperties.getPlmEnv());
            HttpHeaders headers = CommonUtil.getHttpHeaders(headerMap);
            HttpEntity<String> entity = new HttpEntity<>(null, headers);
            log.info("Calling AEX_PLM_SERVICES API to retrieve approvedQuotes : {}", httpMethod.name());
            ResponseEntity<List<PLMAcceptedQuoteFineline>> responseEntity = restTemplate.exchange(
                    plmServiceProperties.getPlmApiBaseURL().concat("/approvedQuotes?planId={planId}"),
                    httpMethod,
                    entity, new ParameterizedTypeReference<>() {
                    }, planId);
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (HttpStatus.OK == statusCode) {
                responseMsg = responseEntity.getBody();
                log.info("Retrieved approvedQuote Successfully: {}", responseEntity.getBody());
            } else {
                log.info(ERROR + responseEntity.getBody());
                throw new CustomException(ERROR + planId);
            }
        } catch (Exception e) {
            log.error("Exception while getting PLM data::{} Exception: ", planId, e);
            throw new CustomException(ERROR + planId);
        }
        return responseMsg;
    }

    @Recover
    public String recover(Exception e, Long planId, HttpMethod httpMethod) {
        throw new CustomException(ERROR + planId);
    }

}
