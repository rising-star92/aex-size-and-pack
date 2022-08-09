package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestContextDTO;
import com.walmart.aex.sp.dto.packoptimization.Execution;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.entity.RunStatusText;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IntegrationHubService {

    @Autowired
    private RestTemplate restTemplate;

    @ManagedConfiguration
    private IntegrationHubServiceProperties integrationHubServiceProperties;

    final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AnalyticsMlSendRepository analyticsMlSendRepository;

    public IntegrationHubService() {

    }

    public IntegrationHubService(RestTemplate restTemplate,
                                 IntegrationHubServiceProperties integrationHubServiceProperties,
                                 AnalyticsMlSendRepository analyticsMlSendRepository) {
        this.restTemplate = restTemplate;
        this.integrationHubServiceProperties = integrationHubServiceProperties;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
    }

    public RunPackOptResponse callIntegrationHubForPackOpt(RunPackOptRequest request) {
        RunPackOptResponse runPackOptResponse = null;
        try {
            List<InputRequest> inputRequests = request.getInputRequest();
            IntegrationHubRequestDTO integrationHubRequestDTO = getIntegrationHubRequest(request, inputRequests);
            final HttpHeaders headers = getHeaders();
            String url = integrationHubServiceProperties.getUrl();
            final ResponseEntity<IntegrationHubResponseDTO> respEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(integrationHubRequestDTO, headers), IntegrationHubResponseDTO.class);
            if (respEntity.getStatusCode().is2xxSuccessful()) {
                IntegrationHubResponseDTO integrationHubResponseDTO = respEntity.getBody();
                if (integrationHubResponseDTO != null) {
                    Set<AnalyticsMlSend> analyticsMlSendSet = createAnalyticsMlSendEntry(request, integrationHubRequestDTO, integrationHubResponseDTO.getWf_running_id());
                    analyticsMlSendRepository.saveAll(analyticsMlSendSet);
                    log.info("Done creating the entries in analytics_ml_send for plan_id : {}", request.getPlanId());
                    //todo - for now, sending the Execution id as 1 in the response
                    BigInteger bigInteger = BigInteger.ONE;
                    runPackOptResponse = new RunPackOptResponse(new Execution(bigInteger, respEntity.getStatusCode().value(), respEntity.getStatusCode().toString(), null));
                    return runPackOptResponse;
                }
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

    private IntegrationHubRequestDTO getIntegrationHubRequest(RunPackOptRequest request, List<InputRequest> inputRequests) {
        List<Integer> finelinesList = Optional.ofNullable(inputRequests.stream().map(InputRequest :: getFinelineNbr)
                        .collect(Collectors.toList())).orElse(new ArrayList<>());
        IntegrationHubRequestDTO integrationHubRequestDTO = new IntegrationHubRequestDTO();
        IntegrationHubRequestContextDTO integrationHubRequestContextDTO = new IntegrationHubRequestContextDTO();
        final String packOptFinelineDetailsSuffix = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}";
        final String packOptFinelineStatusSuffix = "/api/packOptimization/fineline/status";
        String sizeAndPackSvcUrl = integrationHubServiceProperties.getSizeAndPackUrl();
        integrationHubRequestContextDTO.setGetPackOptFinelineDetails(sizeAndPackSvcUrl + packOptFinelineDetailsSuffix);
        integrationHubRequestContextDTO.setUpdatePackOptFinelineStatus(sizeAndPackSvcUrl  + packOptFinelineStatusSuffix);
        integrationHubRequestContextDTO.setPlanId(request.getPlanId());
        integrationHubRequestContextDTO.setFinelineNbrs(finelinesList);
        integrationHubRequestDTO.setContext(integrationHubRequestContextDTO);
        return integrationHubRequestDTO;
    }

    private Set<AnalyticsMlSend> createAnalyticsMlSendEntry(RunPackOptRequest request, IntegrationHubRequestDTO integrationHubRequestDTO, String analysticsJobId) {
        Set<AnalyticsMlSend> analyticsMlSendSet = new HashSet<>();
        if (!CollectionUtils.isEmpty(request.getInputRequest())) {
            request.getInputRequest().forEach(inputRequest -> {
                AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
                analyticsMlSend.setPlanId(request.getPlanId());
                analyticsMlSend.setStrategyId(null);
                analyticsMlSend.setAnalyticsClusterId(null);
                analyticsMlSend.setLvl0Nbr(inputRequest.getLvl0Nbr());
                analyticsMlSend.setLvl1Nbr(inputRequest.getLvl1Nbr());
                analyticsMlSend.setLvl2Nbr(inputRequest.getLvl2Nbr());
                analyticsMlSend.setLvl3Nbr(inputRequest.getLvl3Nbr());
                analyticsMlSend.setLvl4Nbr(inputRequest.getLvl4Nbr());
                analyticsMlSend.setFinelineNbr(inputRequest.getFinelineNbr());
                analyticsMlSend.setFirstName(getFirstName(request.getRunUser()));
                analyticsMlSend.setLastName(getLastName(request.getRunUser()));
                analyticsMlSend.setRunStatusCode(3);
                //todo - hard coding values as its non null property
                analyticsMlSend.setAnalyticsSendDesc("analytics Desc");
                analyticsMlSend.setStartTs(null);
                analyticsMlSend.setEndTs(null);
                analyticsMlSend.setRetryCnt(0);
                String reqPayload = null;
                try {
                    reqPayload = objectMapper.writeValueAsString(integrationHubRequestDTO);
                    log.info("Request payload sent to Integration Hub for planId: {} and finelineNbr is : {}", request.getPlanId(), inputRequest.getFinelineNbr(), reqPayload);
                } catch (JsonProcessingException exp) {
                    log.error("Couldn't parse the payload sent to Integration Hub. Error: {}", exp.toString());
                }

                analyticsMlSend.setPayloadObj(reqPayload);
                analyticsMlSend.setReturnMessage(null);
                analyticsMlSend.setAnalyticsJobId(analysticsJobId);
                analyticsMlSendSet.add(analyticsMlSend);
            });
        }

        return analyticsMlSendSet;
    }

    private  String getFirstName(String fullName) {
        if (fullName != null) {
            int index = fullName.lastIndexOf(" ");
            if (index > -1) {
                return fullName.substring(0, index);
            }
        }
        return fullName;
    }

    private  String getLastName(String fullName) {
        if (fullName != null) {
            int index = fullName.lastIndexOf(" ");
            if (index > -1) {
                return fullName.substring(index + 1, fullName.length());
            }
        }
        return fullName;
    }
}
