package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostPackOptimizationService {




    private final MerchCatgReplPackRepository merchCatgReplPackRepository;

    private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    private final StyleReplnPkConsRepository styleReplnPkConsRepository;

    private final CcReplnPkConsRepository ccReplnPkConsRepository;

    private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;

    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    private final ObjectMapper objectMapper;

    private final ReplenishmentsOptimizationService replenishmentsOptimizationServices;


    public PostPackOptimizationService(MerchCatgReplPackRepository merchCatgReplPackRepository,
                                       FinelineReplnPkConsRepository finelineReplnPkConsRepository,
                                       SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
                                       StyleReplnPkConsRepository styleReplnPkConsRepository,
                                       CcReplnPkConsRepository ccReplnPkConsRepository,
                                       CcMmReplnPkConsRepository ccMmReplnPkConsRepository,
                                       CcSpReplnPkConsRepository ccSpReplnPkConsRepository,
                                       ObjectMapper objectMapper, ReplenishmentsOptimizationService replenishmentsOptimizationServices) {
        this.merchCatgReplPackRepository = merchCatgReplPackRepository;
        this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
        this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
        this.styleReplnPkConsRepository = styleReplnPkConsRepository;
        this.ccReplnPkConsRepository = ccReplnPkConsRepository;
        this.ccMmReplnPkConsRepository = ccMmReplnPkConsRepository;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
        this.objectMapper = objectMapper;
        this.replenishmentsOptimizationServices = replenishmentsOptimizationServices;
    }



    @Transactional
    public void updateInitialSetAndBumpPackAty(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        log.info("Update Replenishment qty {}", isAndBPQtyDTO);
        Integer rollUpDifference = updateRCMerchFineline(planId, finelineNbr, isAndBPQtyDTO);
        if (rollUpDifference != 0) {
            updateRCMerchCatg(planId, finelineNbr, rollUpDifference);
            updateRCMerchSubCatg(planId, finelineNbr, rollUpDifference);
            updateRCStyleAndCustomerChoice(planId, finelineNbr, isAndBPQtyDTO);
            updateRCMerchMethodCCFixture(planId, finelineNbr, isAndBPQtyDTO);
            updateRCMerchMethodCCFixtureSize(planId, finelineNbr, isAndBPQtyDTO);
        }

    }

    private void updateRCMerchMethodCCFixtureSize(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> cc.getFixtures().forEach(fixtures -> fixtures.getSizes().forEach(size -> {
            ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(planId, finelineNbr, cc.getCcId(), CommonUtil.getMerchMethod(fixtures.getMerchMethod()), CommonUtil.getFixtureRollUpId(fixtures.getFixtureType()), size.getSizeDesc()).ifPresent( ccSpMmReplPack ->{
                Integer updatedReplenishmentQty = ccSpMmReplPack.getFinalBuyUnits() - size.getOptFinalBuyQty();
                ccSpMmReplPack.setReplUnits(updatedReplenishmentQty);
                String replObjJson = ccSpMmReplPack.getReplenObj();
                if(replObjJson!= null && !replObjJson.isEmpty()){
                    try {
                        List<Replenishment> replObj = objectMapper.readValue(replObjJson, new TypeReference<>() {});
                        Long total = replObj.stream().mapToLong(ru->ru.getAdjReplnUnits()).sum();
                        List<Replenishment> updateReplObj = replObj.stream()
                                .peek(replenishment -> replenishment.setAdjReplnUnits((updatedReplenishmentQty*(((replenishment.getAdjReplnUnits()*100)/total))/100)))
                                .collect(Collectors.toList());
                        List<Replenishment> updatedReplenishmentsPack = replenishmentsOptimizationServices.getUpdatedReplenishmentsPack(updateReplObj);
                        ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(updateReplObj));
                    } catch (JsonProcessingException e) {
                       log.error("Could not convert Replenishment Object Json for week disaggregation ",e );
                    }
                }
                ccSpReplnPkConsRepository.save(ccSpMmReplPack);
            } );
        })));
    }

    private void updateRCMerchMethodCCFixture(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> cc.getFixtures().forEach(fixtures -> ccMmReplnPkConsRepository.findCcMmReplnPkConsData(planId, finelineNbr, cc.getCcId(), CommonUtil.getMerchMethod(fixtures.getMerchMethod()), CommonUtil.getFixtureRollUpId(fixtures.getFixtureType())).ifPresent(ccMmReplPack -> {
            Integer total = fixtures.getSizes().stream()
                    .mapToInt(Size::getOptFinalBuyQty).sum();

            Integer updatedReplenishmentQty = ccMmReplPack.getFinalBuyUnits() - total;
            ccMmReplPack.setReplUnits(updatedReplenishmentQty);
            ccMmReplnPkConsRepository.save(ccMmReplPack);
        })));
    }

    private void updateRCStyleAndCustomerChoice(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> ccReplnPkConsRepository.findByPlanIdAndCCId(planId, finelineNbr, cc.getCcId()).ifPresent(ccReplPack -> {
            Integer total = cc.getFixtures().stream()
                    .mapToInt(fixtures -> fixtures.getSizes().stream()
                            .mapToInt(Size::getOptFinalBuyQty).sum()).sum();


            Integer updatedReplenishmentQty = ccReplPack.getFinalBuyUnits() - total;
            Integer rollUpDifference = updatedReplenishmentQty - ccReplPack.getReplUnits();

            ccReplPack.setReplUnits(updatedReplenishmentQty);
            ccReplnPkConsRepository.save(ccReplPack);
            styleReplnPkConsRepository.findByPlanIdAndCCId(planId, finelineNbr, cc.getCcId()).ifPresent(styleReplPack -> {
                styleReplPack.setReplUnits(styleReplPack.getReplUnits() + rollUpDifference);
                styleReplnPkConsRepository.save(styleReplPack);
            });
        }));
    }


    private void updateRCMerchSubCatg(Long planId, Integer finelineNbr, Integer rollUpDifference) {
        subCatgReplnPkConsRepository.findByPlanIdAndFinelineNbr(planId, finelineNbr).ifPresent(subCatgReplPack -> {
            subCatgReplPack.setReplUnits(subCatgReplPack.getReplUnits() + rollUpDifference);
            subCatgReplnPkConsRepository.save(subCatgReplPack);
        });
    }

    private Integer updateRCMerchFineline(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        AtomicReference<Integer> updateValue = new AtomicReference<>(0);
        finelineReplnPkConsRepository.findByPlanIdAndFinelineNbr(planId, finelineNbr).ifPresent(finelineReplPack -> {
            Integer total = isAndBPQtyDTO.getCustomerChoices().stream()
                    .mapToInt(cc -> cc.getFixtures().stream()
                            .mapToInt(fixtures -> fixtures.getSizes().stream()
                                    .mapToInt(Size::getOptFinalBuyQty).sum()).sum()).sum();


            Integer updatedReplenishmentQty = finelineReplPack.getFinalBuyUnits() - total;
            updateValue.set(updatedReplenishmentQty - finelineReplPack.getReplUnits());
            finelineReplPack.setReplUnits(updatedReplenishmentQty);
            finelineReplnPkConsRepository.save(finelineReplPack);
        });
        return updateValue.get();
    }

    private void updateRCMerchCatg(Long planId, Integer finelineNbr, Integer rollUpDifference) {
        merchCatgReplPackRepository.findByPlanIdAndFinelineNbr(planId, finelineNbr).ifPresent(merchCatgReplPack -> {
            merchCatgReplPack.setReplUnits(merchCatgReplPack.getReplUnits() + rollUpDifference);
            merchCatgReplPackRepository.save(merchCatgReplPack);
        });
    }
}
