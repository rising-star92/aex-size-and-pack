package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.packoptimization.InputRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.sourcingFactory.FactoryDetailsResponse;
import com.walmart.aex.sp.dto.quote.*;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.StylePackOptimization;
import com.walmart.aex.sp.repository.CcPackOptimizationRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.FAILED_STATUS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SUCCESS_STATUS;

@Service
@Slf4j
public class UpdateFromQuoteService {

    private final PLMQuoteService plmQuoteService;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final SourcingFactoryService sourcingFactoryService;
    private Map<String, String> factoryMap = new HashMap<>();

    public UpdateFromQuoteService(PLMQuoteService plmQuoteService, CcPackOptimizationRepository ccPackOptimizationRepository, SourcingFactoryService sourcingFactoryService) {
        this.plmQuoteService = plmQuoteService;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.sourcingFactoryService = sourcingFactoryService;
    }

    public StatusResponse updateFactoryFromApproveQuotes(RunPackOptRequest request) {
        StatusResponse response = new StatusResponse();
        log.info("Received UpdateFromQuote request to update the factoryId and factoryName : {} ", request);
        Long planId = request.getPlanId();
        InputRequest inputRequest = request.getInputRequest();
        if (planId == null || inputRequest == null) {
            log.error("Invalid planId: {} or inputRequest: {} .Failed to update the factory from approved quotes", planId, inputRequest);
            response.setStatus(FAILED_STATUS);
            return response;
        }
        List<PLMAcceptedQuoteFineline> plmAcceptedQuoteFineLines = plmQuoteService.getApprovedQuoteFromPlm(planId, HttpMethod.GET);
        if (CollectionUtils.isEmpty(plmAcceptedQuoteFineLines)) {
            log.warn("No accepted quotes found for planId : {} there is no update to factory Id ", planId);
            response.setStatus(FAILED_STATUS);
            return response;
        }
        try {
            processInputRequestForFactoryUpdate(planId, inputRequest, plmAcceptedQuoteFineLines);
            response.setStatus(SUCCESS_STATUS);
        }catch (Exception e){
            log.error("UpdateFromQuote failed: ",e);
            response.setStatus(FAILED_STATUS);
        }
        return response;
    }

    private void processInputRequestForFactoryUpdate(Long planId, InputRequest inputRequest, List<PLMAcceptedQuoteFineline> plmAcceptedQuoteFineLines) {
        for (Lvl3Dto lvl3 : inputRequest.getLvl3List()) {
            for (Lvl4Dto lv4 : lvl3.getLvl4List()) {
                for (FinelineDto fineLine : lv4.getFinelines()) {
                    PLMAcceptedQuoteFineline plmAcceptedQuoteFineline = getFineLineFromApprovedQuote(fineLine.getFinelineNbr(), lv4.getLvl4Nbr(), lvl3.getLvl3Nbr(), plmAcceptedQuoteFineLines);
                    /** Update Factory details if CC is mapped with quotes **/
                    if (plmAcceptedQuoteFineline != null && !CollectionUtils.isEmpty(plmAcceptedQuoteFineline.getPlmAcceptedQuoteStyles())) {
                        List<CcPackOptimization> ccPackOptimizations = ccPackOptimizationRepository.findCCPackOptimizationByFineLineNbr(planId, inputRequest.getLvl0Nbr(), inputRequest.getLvl1Nbr(), inputRequest.getLvl2Nbr(), lvl3.getLvl3Nbr(), lv4.getLvl4Nbr(), fineLine.getFinelineNbr());
                        List<StylePackOptimization> stylePackOptimizationList = getStylePackOptimization(ccPackOptimizations);
                        List<PLMAcceptedQuoteStyle> plmAcceptedQuoteStyles = getQuoteMappedPLMStyles(stylePackOptimizationList, plmAcceptedQuoteFineline);
                        updateFactoryFromMappedPLMQuote(plmAcceptedQuoteStyles, ccPackOptimizations);
                        ccPackOptimizationRepository.saveAll(ccPackOptimizations);
                    }
                }
            }
        }

    }

    private void updateFactoryFromMappedPLMQuote(List<PLMAcceptedQuoteStyle> plmAcceptedQuoteStyles, List<CcPackOptimization> ccPackOptimizations) {
        Map<String, Factory> ccFactoryMap = new HashMap<>();
        for (PLMAcceptedQuoteStyle plmAcceptedQuoteStyle : plmAcceptedQuoteStyles) {
            getPLMCCFactoryMap(plmAcceptedQuoteStyle.getPlmAcceptedQuoteCcs(), ccPackOptimizations, ccFactoryMap);
        }
        if (ccFactoryMap.size() > 0) {
            for (CcPackOptimization ccPackOptimization : ccPackOptimizations) {
                String customerChoice = ccPackOptimization.getCcPackOptimizationId().getCustomerChoice();
                if (ccFactoryMap.containsKey(customerChoice)) {
                    Factory plmFactory = ccFactoryMap.get(customerChoice);
                    ccPackOptimization.setFactoryId(plmFactory.getFactoryId());
                    ccPackOptimization.setFactoryName(plmFactory.getFactoryName());
                    ccPackOptimization.setOverrideFactoryId(null);
                    ccPackOptimization.setOverrideFactoryName(null);
                }
            }
        }
    }

    private void getPLMCCFactoryMap(List<PLMAcceptedQuoteCc> plmAcceptedQuoteCcs, List<CcPackOptimization> ccPackOptimizations, Map<String, Factory> ccFactoryMap) {
        List<PLMAcceptedQuoteCc> matchingPLMCCs = getQuoteMappedPLMCCs(plmAcceptedQuoteCcs, ccPackOptimizations);
        for (PLMAcceptedQuoteCc plmAcceptedQuoteCc : matchingPLMCCs) {
            String customerChoice = plmAcceptedQuoteCc.getCustomerChoice();
            List<PLMAcceptedQuote> plmAcceptedQuotes = plmAcceptedQuoteCc.getPlmAcceptedQuotes();
            if (!CollectionUtils.isEmpty(plmAcceptedQuotes)) {
                PLMAcceptedQuote plmAcceptedQuote = plmAcceptedQuotes.get(0);
                getPLMFactoryMap(plmAcceptedQuote, customerChoice, ccFactoryMap);
            }
        }
    }

    private void getPLMFactoryMap(PLMAcceptedQuote plmAcceptedQuote, String customerChoice, Map<String, Factory> ccFactoryMap) {
        BigInteger plmFactoryId = plmAcceptedQuote.getFactoryId();
        if (plmFactoryId != null) {
            String factoryId = String.valueOf(plmFactoryId);
            factoryMap.computeIfAbsent(factoryId, k -> getFactoryName(k));
            Factory factory = new Factory();
            factory.setFactoryId(factoryId);
            if (factoryMap.containsKey(factoryId)) {
                factory.setFactoryName(factoryMap.get(factoryId));
            }
            ccFactoryMap.put(customerChoice, factory);
        }
    }

    private String getFactoryName(String factoryId) {
        FactoryDetailsResponse factoryDetailsResponse = sourcingFactoryService.callSourcingFactoryForFactoryDetails(factoryId);
        return factoryDetailsResponse.getFactoryName();
    }

    private List<StylePackOptimization> getStylePackOptimization(List<CcPackOptimization> ccPackOptimizations) {
        return Optional.ofNullable(ccPackOptimizations)
                .stream()
                .flatMap(Collection::stream)
                .map(CcPackOptimization::getStylePackOptimization)
                .collect(Collectors.toList());
    }

    private List<PLMAcceptedQuoteCc> getQuoteMappedPLMCCs(List<PLMAcceptedQuoteCc> plmAcceptedQuoteCcs, List<CcPackOptimization> ccPackOptimizations) {
        return Optional.ofNullable(plmAcceptedQuoteCcs)
                .stream()
                .flatMap(Collection::stream)
                .filter(plmAcceptedQuoteCc -> ccPackOptimizations.stream().anyMatch(ccPackOptimization ->
                        (ccPackOptimization.getCcPackOptimizationId() != null) && (StringUtils.isNotEmpty(ccPackOptimization.getCcPackOptimizationId().getCustomerChoice())) &&
                                plmAcceptedQuoteCc.getCustomerChoice().equalsIgnoreCase(ccPackOptimization.getCcPackOptimizationId().getCustomerChoice().trim()))
                ).collect(Collectors.toList());
    }

    private List<PLMAcceptedQuoteStyle> getQuoteMappedPLMStyles(List<StylePackOptimization> stylePackOptimizationList, PLMAcceptedQuoteFineline plmAcceptedQuoteFineline) {
        return Optional.ofNullable(plmAcceptedQuoteFineline.getPlmAcceptedQuoteStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(plmAcceptedQuoteStyle -> stylePackOptimizationList.stream().anyMatch(stylePackOptimization ->
                        (stylePackOptimization.getStylePackoptimizationId() != null) && (StringUtils.isNotEmpty(stylePackOptimization.getStylePackoptimizationId().getStyleNbr())) &&
                                plmAcceptedQuoteStyle.getStyleNbr().equalsIgnoreCase(stylePackOptimization.getStylePackoptimizationId().getStyleNbr().trim()))
                ).collect(Collectors.toList());
    }

    private PLMAcceptedQuoteFineline getFineLineFromApprovedQuote(Integer fineLinenbr, Integer lv4nbr, Integer lv3Nbr, List<PLMAcceptedQuoteFineline> plmAcceptedQuoteFineLines) {
        return Optional.ofNullable(plmAcceptedQuoteFineLines)
                .stream()
                .flatMap(Collection::stream)
                .filter(plmFl -> (plmFl.getFinelineNbr().equals(fineLinenbr) && plmFl.getLvl4Nbr().equals(lv4nbr) && plmFl.getLvl3Nbr().equals(lv3Nbr)))
                .findFirst().orElse(null);
    }

}
