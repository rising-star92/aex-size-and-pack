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
import com.walmart.aex.sp.dto.packoptimization.packDescription.PackDescCustChoiceDTO;
import com.walmart.aex.sp.entity.AnalyticsMlChildSend;
import com.walmart.aex.sp.entity.AnalyticsMlSend;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.*;

import static com.walmart.aex.sp.util.CommonUtil.getDateFromString;
import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

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

        if (bumpCount > 0) {
            for (int bumpNumber = 1; bumpNumber <= bumpCount; bumpNumber++) {
                setAnalyticsMlChildSend(flWithIHResMap, analyticsMlSend, flWithIHReqMap, analyticsMlChildSendSet, bumpNumber);
            }
        } else {
            setAnalyticsMlChildSend(flWithIHResMap, analyticsMlSend, flWithIHReqMap, analyticsMlChildSendSet, 0);
        }

        return analyticsMlChildSendSet;
    }

    private static void setAnalyticsMlChildSend(Map<String, IntegrationHubResponseDTO> flWithIHResMap, AnalyticsMlSend analyticsMlSend, Map<String, IntegrationHubRequestDTO> flWithIHReqMap, Set<AnalyticsMlChildSend> analyticsMlChildSendSet, int bumpNumber) {
        String fineLineNbr = analyticsMlSend.getFinelineNbr().toString() + (bumpNumber > 1 ? MULTI_BUMP_PACK_SUFFIX + bumpNumber : "");
        IntegrationHubResponseDTO integrationHubResponseDTO = flWithIHResMap.get(fineLineNbr);
        IntegrationHubRequestDTO integrationHubRequestDTO = flWithIHReqMap.get(fineLineNbr);
        AnalyticsMlChildSend analyticsMlChildSend = getAnalyticsMlChildSend(analyticsMlSend, integrationHubRequestDTO, integrationHubResponseDTO, Math.max(bumpNumber, 1));
        analyticsMlChildSendSet.add(analyticsMlChildSend);
    }

    private static AnalyticsMlChildSend getAnalyticsMlChildSend(AnalyticsMlSend analyticsMlSend,
               IntegrationHubRequestDTO integrationHubRequestDTO, IntegrationHubResponseDTO integrationHubResponseDTO, int bumpNumber) {
        AnalyticsMlChildSend analyticsMlChildSend = new AnalyticsMlChildSend();
        analyticsMlChildSend.setAnalyticsMlSend(analyticsMlSend);
        analyticsMlChildSend.setRunStatusCode(analyticsMlSend.getRunStatusCode());
        analyticsMlChildSend.setAnalyticsSendDesc("Sent");
        analyticsMlChildSend.setRetryCnt(analyticsMlSend.getRetryCnt());
        analyticsMlChildSend.setStartTs(analyticsMlSend.getStartTs());
        analyticsMlChildSend.setEndTs(analyticsMlSend.getEndTs());
        if (integrationHubResponseDTO.getStatus().equalsIgnoreCase("FAILED")) {
            analyticsMlChildSend.setRunStatusCode(RunStatusCodeType.INTEGRATION_HUB_TECHNICAL_ERROR.getId());
            analyticsMlChildSend.setEndTs(new Date());
        }
        String reqPayload = null;
        try {
            reqPayload = objectMapper.writeValueAsString(integrationHubRequestDTO);
        } catch (JsonProcessingException exp) {
            log.error("Couldn't parse the payload sent to Integration Hub. Error: {}", exp.toString());
        }
        analyticsMlChildSend.setPayloadObj(reqPayload);
        analyticsMlChildSend.setReturnMessage(analyticsMlSend.getReturnMessage());
        analyticsMlChildSend.setAnalyticsJobId(integrationHubResponseDTO.getWf_running_id());
        analyticsMlChildSend.setBumpPackNbr(bumpNumber);
        return analyticsMlChildSend;
    }

    /**
     * Generates Pack Description in this format - FinelineDesc_ColorName_MerchMethod_BumpPackNumber_SequenceNumber
     * Ex BumpSet - 3463 - GV EK CHASE CAPRIS_WHITE_HANGING_BP1_0
     * Ex InitialSet - 3463 - GV EK CHASE CAPRIS_WHITE_HANGING_IS_0
     */
    public static String createPackDescription(String packId, String merchMethod, Integer bumpPackNumber, List<String> colors, String altFinelineDesc) {
        return new StringBuilder().append(altFinelineDesc.trim()).append(UNDERSCORE)
                .append(colors.size() == 1 ? colors.get(0) + UNDERSCORE : EMPTY_STRING)
                .append(merchMethod).append(UNDERSCORE)
                .append(null == bumpPackNumber ? INITIAL_SET_IDENTIFIER : BUMP_PACK + bumpPackNumber).append(UNDERSCORE)
                .append(null != packId ? packId.substring(packId.lastIndexOf(UNDERSCORE) + 1) : null)
                .toString();
    }

    public static String getFinelineDescription(List<PackDescCustChoiceDTO> packDescCustChoiceDTOList, Integer finelineNbr) {
        if (!packDescCustChoiceDTOList.isEmpty()) {
            String finelineDesc = packDescCustChoiceDTOList.get(0).getAltFinelineDesc();
            return StringUtils.isNotEmpty(finelineDesc) ? finelineDesc : String.valueOf(finelineNbr);
        }
        else
            return String.valueOf(finelineNbr);
    }

}
