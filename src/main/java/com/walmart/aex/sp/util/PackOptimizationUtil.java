package com.walmart.aex.sp.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubRequestDTO;
import com.walmart.aex.sp.dto.integrationhub.IntegrationHubResponseDTO;
import com.walmart.aex.sp.dto.packoptimization.LevelDto;
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

    public static Set<AnalyticsMlSend> createAnalyticsMlSendEntry(RunPackOptRequest request, Map<Integer, LevelDto> finelineLevelMap,
                                                                  Map<String, IntegrationHubResponseDTO> flWithIHResMap) {
        Set<AnalyticsMlSend> analyticsMlSendSet = new HashSet<>();
        if (!ObjectUtils.isEmpty(request) && !finelineLevelMap.isEmpty()) {
            for(Map.Entry<Integer, LevelDto> finelineEle : finelineLevelMap.entrySet()) {
                AnalyticsMlSend analyticsMlSend = new AnalyticsMlSend();
                analyticsMlSend.setPlanId(request.getPlanId());
                analyticsMlSend.setStrategyId(null);
                analyticsMlSend.setAnalyticsClusterId(null);
                analyticsMlSend.setLvl0Nbr(finelineEle.getValue().getLvl0Nbr());
                analyticsMlSend.setLvl1Nbr(finelineEle.getValue().getLvl1Nbr());
                analyticsMlSend.setLvl2Nbr(finelineEle.getValue().getLvl2Nbr());
                analyticsMlSend.setLvl3Nbr(finelineEle.getValue().getLvl3Nbr());
                analyticsMlSend.setLvl4Nbr(finelineEle.getValue().getLvl4Nbr());
                analyticsMlSend.setFinelineNbr(finelineEle.getKey());
                analyticsMlSend.setFirstName(request.getRunUser());
                analyticsMlSend.setLastName(request.getRunUser());
                analyticsMlSend.setAnalyticsSendDesc("Sent");
                //Setting the run status as 3, which is Sent to Analytics
                analyticsMlSend.setRunStatusCode(RunStatusCodeType.SENT_TO_ANALYTICS.getId());
                //todo - hard coding values as its non null property
                IntegrationHubResponseDTO integrationHubResponseDTO = flWithIHResMap.getOrDefault(finelineEle.getKey().toString(), null);
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

        return analyticsMlSendSet;
    }


    public static Set<AnalyticsMlChildSend> setAnalyticsChildDataToAnalyticsMlSend(
            Map<String, IntegrationHubResponseDTO> flWithIHResMap,
            Map<Integer, Integer> fineLineWithBumpCntMap, AnalyticsMlSend analyticsMlSend,
            Map<String, IntegrationHubRequestDTO> flWithIHReqMap) {

        Set<AnalyticsMlChildSend> analyticsMlChildSendSet = new HashSet<>();
        Integer bumpCount = fineLineWithBumpCntMap.get(analyticsMlSend.getFinelineNbr());
        String fineLineNbr = analyticsMlSend.getFinelineNbr().toString();
        String analyticsJobId = flWithIHResMap.get(fineLineNbr).getWf_running_id();
        IntegrationHubRequestDTO integrationHubRequestDTO = flWithIHReqMap.get(fineLineNbr);
        AnalyticsMlChildSend analyticsMlChildSend = getAnalyticsMlChildSend(analyticsJobId, analyticsMlSend, integrationHubRequestDTO, 1);
        analyticsMlChildSendSet.add(analyticsMlChildSend);

        for (int bumpNumber = 2; bumpNumber <= bumpCount; bumpNumber++) {
            fineLineNbr = analyticsMlSend.getFinelineNbr().toString() + MULTI_BUMP_PACK_SUFFIX + bumpNumber;
            analyticsJobId = flWithIHResMap.get(fineLineNbr).getWf_running_id();
            integrationHubRequestDTO = flWithIHReqMap.get(fineLineNbr);
            analyticsMlChildSend = getAnalyticsMlChildSend(analyticsJobId, analyticsMlSend, integrationHubRequestDTO, bumpNumber);
            analyticsMlChildSendSet.add(analyticsMlChildSend);
        }

        return analyticsMlChildSendSet;
    }

    private static AnalyticsMlChildSend getAnalyticsMlChildSend(String analyticsJobId, AnalyticsMlSend analyticsMlSend,
               IntegrationHubRequestDTO integrationHubRequestDTO, int bumpNumber) {
        AnalyticsMlChildSend analyticsMlChildSend = new AnalyticsMlChildSend();
        analyticsMlChildSend.setAnalyticsMlSend(analyticsMlSend);
        analyticsMlChildSend.setRunStatusCode(analyticsMlSend.getRunStatusCode());
        analyticsMlChildSend.setAnalyticsSendDesc("Sent");
        analyticsMlChildSend.setStartTs(analyticsMlSend.getStartTs());
        analyticsMlChildSend.setEndTs(analyticsMlSend.getEndTs());
        analyticsMlChildSend.setRetryCnt(analyticsMlSend.getRetryCnt());
        String reqPayload = null;
        try {
            reqPayload = objectMapper.writeValueAsString(integrationHubRequestDTO);
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
