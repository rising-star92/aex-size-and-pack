package com.walmart.aex.sp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.walmart.aex.sp.util.CommonUtil.getDateFromString;

@Component
@Slf4j
public class PackOptimizationUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Set<AnalyticsMlSend> createAnalyticsMlSendEntry(RunPackOptRequest request,
                       Map<String, IntegrationHubRequestDTO> flWithIHReqMap,
                       Map<String, IntegrationHubResponseDTO> flWithIHResMap) {
        Set<AnalyticsMlSend> analyticsMlSendSet = new HashSet<>();
        InputRequest inputRequest = request.getInputRequest();
        if (inputRequest != null) {
            for (Lvl3Dto lvl3 : inputRequest.getLvl3List()) {
                for (Lvl4Dto lv4 : lvl3.getLvl4List()) {
                    for (FinelineDto fineline : lv4.getFinelines()) {
                        AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
                        analyticsMlSend.setPlanId(request.getPlanId());
                        analyticsMlSend.setStrategyId(null);
                        analyticsMlSend.setAnalyticsClusterId(null);
                        analyticsMlSend.setLvl0Nbr(inputRequest.getLvl0Nbr());
                        analyticsMlSend.setLvl1Nbr(inputRequest.getLvl1Nbr());
                        analyticsMlSend.setLvl2Nbr(inputRequest.getLvl2Nbr());
                        analyticsMlSend.setLvl3Nbr(lvl3.getLvl3Nbr());
                        analyticsMlSend.setLvl4Nbr(lv4.getLvl4Nbr());
                        analyticsMlSend.setFinelineNbr(fineline.getFinelineNbr());
                        analyticsMlSend.setFirstName(request.getRunUser());
                        analyticsMlSend.setLastName(request.getRunUser());
                        //Setting the run status as 3, which is Sent to Analytics
                        analyticsMlSend.setRunStatusCode(RunStatusCodeType.SENT_TO_ANALYTICS.getId());
                        //todo - hard coding values as its non null property
                        analyticsMlSend.setAnalyticsSendDesc("analytics Desc");
                        IntegrationHubResponseDTO integrationHubResponseDTO = flWithIHResMap.getOrDefault(fineline.getFinelineNbr().toString(), null);
                        Date startDate = null;
                        if(!ObjectUtils.isEmpty(integrationHubResponseDTO) &&
                                StringUtils.isEmpty(integrationHubResponseDTO.getStarted_time())) {
                            startDate = getDateFromString(integrationHubResponseDTO.getStarted_time());
                        }
                        analyticsMlSend.setStartTs(startDate);
                        analyticsMlSend.setEndTs(null);
                        analyticsMlSend.setRetryCnt(0);
                        String reqPayload = null;
                        try {
                            reqPayload = objectMapper.writeValueAsString(
                                    flWithIHReqMap.getOrDefault(fineline.getFinelineNbr().toString(), null));
                            log.info("Request payload sent to Integration Hub for planId: {} and finelineNbr is : {}", request.getPlanId(), fineline.getFinelineNbr(), reqPayload);
                        } catch (JsonProcessingException exp) {
                            log.error("Couldn't parse the payload sent to Integration Hub. Error: {}", exp.toString());
                        }

                        analyticsMlSend.setPayloadObj(reqPayload);
                        analyticsMlSend.setReturnMessage(null);
                        analyticsMlSendSet.add(analyticsMlSend);
                    }
                }
            }
        }

        return analyticsMlSendSet;
    }


    public static Set<AnalyticsMlChildSend> setAnalyticsChildDataToAnalyticsMlSend(Map<String, IntegrationHubResponseDTO> flWithIHResMap, Map<Integer, Integer> fineLineWithBumpCntMap, AnalyticsMlSend analyticsMlSend) {
        IntegrationHubResponseDTO integrationHubResponseDTO = flWithIHResMap.getOrDefault(analyticsMlSend.getFinelineNbr().toString(), null);
        String analyticsJobId = integrationHubResponseDTO != null ? integrationHubResponseDTO.getWf_running_id() : null;
        Set<AnalyticsMlChildSend> analyticsMlChildSendSet = new HashSet<>();
        Integer bumpCount = fineLineWithBumpCntMap.get(analyticsMlSend.getFinelineNbr());
        for (int index = 1; index <= bumpCount; index++) {
            AnalyticsMlChildSend analyticsMlChildSend = new AnalyticsMlChildSend();
            analyticsMlChildSend.setRunStatusCode(analyticsMlSend.getRunStatusCode());
            analyticsMlChildSend.setAnalyticsSendDesc(analyticsMlSend.getAnalyticsSendDesc());
            analyticsMlChildSend.setStartTs(analyticsMlSend.getStartTs());
            analyticsMlChildSend.setEndTs(analyticsMlSend.getEndTs());
            analyticsMlChildSend.setRetryCnt(analyticsMlSend.getRetryCnt());
            analyticsMlChildSend.setPayloadObj(analyticsMlSend.getPayloadObj());
            analyticsMlChildSend.setReturnMessage(analyticsMlSend.getReturnMessage());
            analyticsMlChildSend.setAnalyticsJobId(analyticsJobId);
            analyticsMlChildSend.setBumpPackNbr(index);
            analyticsMlChildSendSet.add(analyticsMlChildSend);
        }
        return analyticsMlChildSendSet;
    }

}
