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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.walmart.aex.sp.util.CommonUtil.getDateFromString;
import static com.walmart.aex.sp.util.SizeAndPackConstants.MULTI_BUMP_PACK_SUFFIX;

@Component
@Slf4j
public class PackOptimizationUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Set<AnalyticsMlSend> createAnalyticsMlSendEntry(RunPackOptRequest request,
                       Map<String, IntegrationHubResponseDTO> flWithIHResMap) {
        Set<AnalyticsMlSend> analyticsMlSendSet = new HashSet<>();
        InputRequest inputRequest = request.getInputRequest();
        if (!ObjectUtils.isEmpty(request)) {
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
                        analyticsMlSend.setAnalyticsSendDesc("Sent");
                        //Setting the run status as 3, which is Sent to Analytics
                        analyticsMlSend.setRunStatusCode(RunStatusCodeType.SENT_TO_ANALYTICS.getId());
                        //todo - hard coding values as its non null property
                        IntegrationHubResponseDTO integrationHubResponseDTO = flWithIHResMap.getOrDefault(fineline.getFinelineNbr().toString(), null);
                        Date startDate = null;
                        if(!ObjectUtils.isEmpty(integrationHubResponseDTO) &&
                                StringUtils.isNotEmpty(integrationHubResponseDTO.getStarted_time())) {
                            startDate = getDateFromString(integrationHubResponseDTO.getStarted_time());
                        }
                        analyticsMlSend.setStartTs(startDate);
                        analyticsMlSend.setEndTs(null);
                        analyticsMlSend.setRetryCnt(0);
                        analyticsMlSend.setReturnMessage(null);
                        analyticsMlSendSet.add(analyticsMlSend);
                    }
                }
            }
        }

        return analyticsMlSendSet;
    }


    public static Set<AnalyticsMlChildSend> setAnalyticsChildDataToAnalyticsMlSend(
            Map<String, IntegrationHubResponseDTO> flWithIHResMap,
            Map<Integer, Integer> fineLineWithBumpCntMap, AnalyticsMlSend analyticsMlSend,
            Map<String, IntegrationHubRequestDTO> flWithIHReqMap) {

        Set<AnalyticsMlChildSend> analyticsMlChildSendSet = new HashSet<>();
        Integer bumpCount = fineLineWithBumpCntMap.get(analyticsMlSend.getFinelineNbr());
        AnalyticsMlChildSend analyticsMlChildSend = getAnalyticsMlChildSend(flWithIHResMap, analyticsMlSend, flWithIHReqMap, 0);
        analyticsMlChildSendSet.add(analyticsMlChildSend);
        for (int index = 1; index < bumpCount; index++) {
            analyticsMlChildSend = getAnalyticsMlChildSend(flWithIHResMap, analyticsMlSend, flWithIHReqMap, index);
            analyticsMlChildSendSet.add(analyticsMlChildSend);
        }
        return analyticsMlChildSendSet;
    }

    private static AnalyticsMlChildSend getAnalyticsMlChildSend(Map<String, IntegrationHubResponseDTO> flWithIHResMap, AnalyticsMlSend analyticsMlSend, Map<String, IntegrationHubRequestDTO> flWithIHReqMap, int index) {
        Integer bumpNumber = index + 1;
        String fineLineNbr = index == 0 ? analyticsMlSend.getFinelineNbr().toString() : analyticsMlSend.getFinelineNbr().toString() + MULTI_BUMP_PACK_SUFFIX + bumpNumber;
        IntegrationHubResponseDTO integrationHubResponseDTO = flWithIHResMap.getOrDefault(fineLineNbr, null);
        String analyticsJobId = integrationHubResponseDTO != null ? integrationHubResponseDTO.getWf_running_id() : null;
        AnalyticsMlChildSend analyticsMlChildSend = new AnalyticsMlChildSend();
        analyticsMlChildSend.setAnalyticsMlSend(analyticsMlSend);
        analyticsMlChildSend.setRunStatusCode(analyticsMlSend.getRunStatusCode());
        analyticsMlChildSend.setAnalyticsSendDesc("Sent");
        analyticsMlChildSend.setStartTs(analyticsMlSend.getStartTs());
        analyticsMlChildSend.setEndTs(analyticsMlSend.getEndTs());
        analyticsMlChildSend.setRetryCnt(analyticsMlSend.getRetryCnt());
        String reqPayload = null;
        try {
            reqPayload = objectMapper.writeValueAsString(
                    flWithIHReqMap.getOrDefault(fineLineNbr, null));
        } catch (JsonProcessingException exp) {
            log.error("Couldn't parse the payload sent to Integration Hub. Error: {}", exp.toString());
        }
        analyticsMlChildSend.setPayloadObj(reqPayload);
        analyticsMlChildSend.setReturnMessage(analyticsMlSend.getReturnMessage());
        analyticsMlChildSend.setAnalyticsJobId(analyticsJobId);
        analyticsMlChildSend.setBumpPackNbr(bumpNumber);
        return analyticsMlChildSend;
    }

}
