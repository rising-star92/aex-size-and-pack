package com.walmart.aex.sp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class PackOptimizationUtil {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private CommonUtil commonUtil;

    public Set<AnalyticsMlSend> createAnalyticsMlSendEntry(RunPackOptRequest request, IntegrationHubRequestDTO integrationHubRequestDTO, String analysticsJobId, String startDateStr) {
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
                        Date startDate = commonUtil.getStartDate(startDateStr);
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
}
