package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.BuyQntyResponseDTO;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.gql.GraphQLResponse;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestContextDTO;
import com.walmart.aex.sp.dto.packoptimization.Execution;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.IntegrationHubServiceProperties;
import com.walmart.aex.sp.repository.AnalyticsMlSendRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
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

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;

    public IntegrationHubService() {

    }

    public IntegrationHubService(RestTemplate restTemplate,
                                 IntegrationHubServiceProperties integrationHubServiceProperties,
                                 AnalyticsMlSendRepository analyticsMlSendRepository) {
        this.restTemplate = restTemplate;
        this.integrationHubServiceProperties = integrationHubServiceProperties;
        this.analyticsMlSendRepository = analyticsMlSendRepository;
    }

    public List<RunPackOptResponse> callIntegrationHubForPackOptByFineline(RunPackOptRequest request) {
        List<RunPackOptResponse> runPackOptResponseList = new ArrayList<>();
        try {
            InputRequest inputRequest = request.getInputRequest();
            List<Integer> finelinesList = new ArrayList<>();
            List<String> finelineIsBsList = new ArrayList<>();
            if (inputRequest != null) {
                for (Lvl3Dto lvl3 : inputRequest.getLvl3List()) {
                    for (Lvl4Dto lv4 : lvl3.getLvl4List()) {
                        for (FinelineDto finelines : lv4.getFinelines()) {
                            finelinesList.add(finelines.getFinelineNbr());
                        }
                    }
                }
                setFinelineIsBSList(request, finelinesList, finelineIsBsList);
            }

            finelineIsBsList.forEach(finelineNbr -> {
                IntegrationHubRequestDTO integrationHubRequestDTO = getIntegrationHubRequest(request.getPlanId(), finelineNbr);
                runPackOptResponseList.add(callIntegrationHubForPackOpt(request, integrationHubRequestDTO));
            });
            return runPackOptResponseList;
        }
        catch (RestClientException rce) {
            log.error("Error connecting with Integration Hub service: {}", rce.getMessage());
            return runPackOptResponseList;
        }
    }

    @Retryable(backoff = @Backoff(delay = 3000))
    public RunPackOptResponse callIntegrationHubForPackOpt(RunPackOptRequest request, IntegrationHubRequestDTO integrationHubRequestDTO) {
        RunPackOptResponse runPackOptResponse = null;
        try {
            final HttpHeaders headers = getHeaders();
            String url = integrationHubServiceProperties.getUrl();
            final ResponseEntity<IntegrationHubResponseDTO> respEntity = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(integrationHubRequestDTO, headers), IntegrationHubResponseDTO.class);
            if (respEntity.getStatusCode().is2xxSuccessful()) {
                IntegrationHubResponseDTO integrationHubResponseDTO = respEntity.getBody();
                if (integrationHubResponseDTO != null) {
                    Set<AnalyticsMlSend> analyticsMlSendSet = createAnalyticsMlSendEntry(request, integrationHubRequestDTO, integrationHubResponseDTO.getWf_running_id(), integrationHubResponseDTO.getStarted_time());
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

    private IntegrationHubRequestDTO getIntegrationHubRequest(Long planId, String finelineNbr) {
        List<String> finelineIsBsList = new ArrayList<>();
        finelineIsBsList.add(finelineNbr);
        IntegrationHubRequestDTO integrationHubRequestDTO = new IntegrationHubRequestDTO();
        IntegrationHubRequestContextDTO integrationHubRequestContextDTO = new IntegrationHubRequestContextDTO();
        final String packOptFinelineDetailsSuffix = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}";
        final String packOptFinelineStatusSuffix = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}";
        String sizeAndPackSvcUrl = integrationHubServiceProperties.getSizeAndPackUrl();
        if (finelineNbr.contains("-BP")) {
            integrationHubRequestContextDTO.setGetPackOptFinelineDetails(sizeAndPackSvcUrl + packOptFinelineDetailsSuffix + "/bumppack/{bumpPackNbr}");
        }
        else {
            integrationHubRequestContextDTO.setGetPackOptFinelineDetails(sizeAndPackSvcUrl + packOptFinelineDetailsSuffix);
        }
        integrationHubRequestContextDTO.setUpdatePackOptFinelineStatus(sizeAndPackSvcUrl  + packOptFinelineStatusSuffix);
        integrationHubRequestContextDTO.setPlanId(planId);
        integrationHubRequestContextDTO.setFinelineNbrs(finelineIsBsList);
        integrationHubRequestContextDTO.setEnv(integrationHubServiceProperties.getEnv());
        integrationHubRequestDTO.setContext(integrationHubRequestContextDTO);
        return integrationHubRequestDTO;
    }

    private void setFinelineIsBSList(RunPackOptRequest request, List<Integer> finelinesList, List<String> finelineIsBsList) {
        List<BuyQntyResponseDTO> bumpPackCntByFinelines = spFineLineChannelFixtureRepository.getBumpPackCntByFinelines(request.getPlanId(), finelinesList);
        bumpPackCntByFinelines.forEach(bumpPackCntByFineline -> {
            if (bumpPackCntByFineline.getBumpPackCnt() > 1) {
                int bumpPackCntFlag = 1;
                while (bumpPackCntFlag <= bumpPackCntByFineline.getBumpPackCnt()) {
                    finelineIsBsList.add(bumpPackCntByFineline.getFinelineNbr().toString() + "-BP" + bumpPackCntByFineline.getBumpPackCnt().toString());
                    bumpPackCntFlag++;
                }
            }
            else finelineIsBsList.add(bumpPackCntByFineline.getFinelineNbr().toString());
        });

    }

    private Set<AnalyticsMlSend> createAnalyticsMlSendEntry(RunPackOptRequest request, IntegrationHubRequestDTO integrationHubRequestDTO, String analysticsJobId, String startDateStr) {
        Set<AnalyticsMlSend> analyticsMlSendSet = new HashSet<>();
        InputRequest inputRequest = request.getInputRequest();
        if (inputRequest != null) {
            for (Lvl3Dto lvl3 : inputRequest.getLvl3List()) {
                for (Lvl4Dto lv4 : lvl3.getLvl4List()) {
                    for (FinelineDto finelines : lv4.getFinelines()) {
                        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
                        analyticsMlSend.setPlanId(request.getPlanId());
                        analyticsMlSend.setStrategyId(null);
                        analyticsMlSend.setAnalyticsClusterId(null);
                        analyticsMlSend.setLvl0Nbr(inputRequest.getLvl0Nbr());
                        analyticsMlSend.setLvl1Nbr(inputRequest.getLvl1Nbr());
                        analyticsMlSend.setLvl2Nbr(inputRequest.getLvl2Nbr());
                        analyticsMlSend.setLvl3Nbr(lvl3.getLvl3Nbr());
                        analyticsMlSend.setLvl4Nbr(lv4.getLvl4Nbr());
                        analyticsMlSend.setFinelineNbr(finelines.getFinelineNbr());
                        analyticsMlSend.setFirstName(request.getRunUser());
                        analyticsMlSend.setLastName(request.getRunUser());
                        //Setting the run status as 3, which is Sent to Analytics
                        analyticsMlSend.setRunStatusCode(RunStatusCodeType.SENT_TO_ANALYTICS.getId());
                        //todo - hard coding values as its non null property
                        analyticsMlSend.setAnalyticsSendDesc("analytics Desc");
                        Date startDate = getStartDate(startDateStr);
                        analyticsMlSend.setStartTs(startDate);
                        analyticsMlSend.setEndTs(null);
                        analyticsMlSend.setRetryCnt(0);
                        String reqPayload = null;
                        try {
                            reqPayload = objectMapper.writeValueAsString(integrationHubRequestDTO);
                            log.info("Request payload sent to Integration Hub for planId: {} and finelineNbr is : {}", request.getPlanId(), finelines.getFinelineNbr(), reqPayload);
                        } catch (JsonProcessingException exp) {
                            log.error("Couldn't parse the payload sent to Integration Hub. Error: {}", exp.toString());
                        }

                        analyticsMlSend.setPayloadObj(reqPayload);
                        analyticsMlSend.setReturnMessage(null);
                        analyticsMlSend.setAnalyticsJobId(analysticsJobId);
                        analyticsMlSendSet.add(analyticsMlSend);
                    }
                }
            }
        }

        return analyticsMlSendSet;
    }

    private Date getStartDate(String startDateStr) {
        Date date = null;
        if (startDateStr != null && !startDateStr.isEmpty()) {
            try {
                Instant startDateInstant = Instant.parse(startDateStr);
                if (startDateInstant != null) {
                    date = Date.from(startDateInstant);
                }
            } catch (Exception ex) {
                log.info("Error converting the start Date string: {}", ex.getMessage());
            }
        }
        return date;
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
