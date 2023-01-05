package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.CustomerChoices;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Fixtures;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.Size;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.MerchMethod;
import com.walmart.aex.sp.repository.CcMmReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcReplnPkConsRepository;
import com.walmart.aex.sp.repository.CcSpReplnPkConsRepository;
import com.walmart.aex.sp.repository.FinelineReplnPkConsRepository;
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.StyleReplnPkConsRepository;
import com.walmart.aex.sp.repository.SubCatgReplnPkConsRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

        Map<Integer, Integer> rollUpDifferenceByMerchMethod = updateRCMerchFineline(planId, finelineNbr, isAndBPQtyDTO);
        final int totalRollupDiff = rollUpDifferenceByMerchMethod.values().stream().mapToInt(Integer::intValue).sum();
        if (totalRollupDiff != 0) {
            updateRCMerchCatg(planId, finelineNbr, rollUpDifferenceByMerchMethod);
            updateRCMerchSubCatg(planId, finelineNbr, rollUpDifferenceByMerchMethod);
            updateRCStyleAndCustomerChoice(planId, finelineNbr, isAndBPQtyDTO);
            updateRCMerchMethodCCFixture(planId, finelineNbr, isAndBPQtyDTO);
            updateRCMerchMethodCCFixtureSize(planId, finelineNbr, isAndBPQtyDTO);
        }

    }

    public void updateRCMerchMethodCCFixtureSize(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {

        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> cc.getFixtures().forEach(fixture -> fixture.getSizes().forEach(size -> {
            List<CcSpMmReplPack> ccSpMmReplPacks = ccSpReplnPkConsRepository.findCcSpMmReplnPkConsData(planId, finelineNbr, cc.getCcId(), size.getSizeDesc()).orElse(Collections.emptyList());
            ccSpMmReplPacks.stream().filter(ccSpMmReplPack -> getMerchMethodCode(ccSpMmReplPack.getCcSpReplPackId())
                  .equals(MerchMethod.getMerchMethodIdFromDescription(fixture.getMerchMethod()))).forEach(ccSpMmReplPack -> {

               Integer updatedReplnQty = ccSpMmReplPack.getFinalBuyUnits() - size.getOptFinalBuyQty();
               ccSpMmReplPack.setReplUnits(updatedReplnQty);
               ccSpMmReplPack.setReplPackCnt(UpdateReplnConfigMapper.getReplenishmentPackCount(ccSpMmReplPack.getReplUnits(), ccSpMmReplPack.getVendorPackCnt()));
               String replnWeeksObj = ccSpMmReplPack.getReplenObj();
               if (StringUtils.isNotEmpty(replnWeeksObj)) {

                   try {
                       List<Replenishment> replObj = objectMapper.readValue(replnWeeksObj, new TypeReference<>() {});
                       Long totalReplnUnits = replObj.stream().mapToLong(Replenishment::getAdjReplnUnits).sum();
                       replObj.forEach(replWeekObj -> replWeekObj.setAdjReplnUnits((updatedReplnQty*((replWeekObj.getAdjReplnUnits()*100)/totalReplnUnits)/100)));
                       List<Replenishment> updatedReplenishmentsPack = replenishmentsOptimizationServices.getUpdatedReplenishmentsPack(replObj,ccSpMmReplPack.getVendorPackCnt());
                       ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(updatedReplenishmentsPack));
                   } catch (JsonProcessingException jpe) {
                       log.error("Could not convert Replenishment Object Json for week disaggregation ", jpe);
                   }
               }
            });
            ccSpReplnPkConsRepository.saveAll(ccSpMmReplPacks);
        })));
    }

    private void updateRCMerchMethodCCFixture(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {

        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> {

            List<String> merchMethods = cc.getFixtures().stream().map(Fixtures::getMerchMethod).distinct().collect(Collectors.toList());
            merchMethods.forEach(merchMethod -> {
                List<CcMmReplPack> ccMmReplPacks = ccMmReplnPkConsRepository.findCcMmReplnPkConsData(planId, finelineNbr, cc.getCcId()).orElse(Collections.emptyList());

                ccMmReplPacks.forEach(ccMmReplPack -> {
                    int updatedTotal = cc.getFixtures().stream().filter(ccFix -> ccFix.getMerchMethod().equalsIgnoreCase(merchMethod))
                          .map(Fixtures::getSizes)
                          .flatMap(Collection::stream)
                          .mapToInt(Size::getOptFinalBuyQty)
                          .sum();

                    Integer updatedTotalReplnQty = ccMmReplPack.getFinalBuyUnits() - updatedTotal;
                    ccMmReplPack.setReplUnits(updatedTotalReplnQty);
                    ccMmReplPack.setReplPackCnt(UpdateReplnConfigMapper.getReplenishmentPackCount(ccMmReplPack.getReplUnits(), ccMmReplPack.getVendorPackCnt()));
                    ccMmReplnPkConsRepository.save(ccMmReplPack);
                });
            });
        });
    }

    public void updateRCStyleAndCustomerChoice(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {

        isAndBPQtyDTO.getCustomerChoices().forEach(cc -> {
            List<StyleReplPack> styleReplPacks = styleReplnPkConsRepository.findByPlanIdAndCCId(planId, finelineNbr, cc.getCcId()).orElse(Collections.emptyList());
            List<CcReplPack> ccReplPacks = ccReplnPkConsRepository.findByPlanIdAndCCId(planId, finelineNbr, cc.getCcId()).orElse(Collections.emptyList());
            ccReplPacks.stream().filter(ccRpk -> ccRpk.getCcReplPackId().getCustomerChoice().equalsIgnoreCase(cc.getCcId())).forEach(ccReplPack -> {
                Integer merchMethodCode = getMerchMethodCode(ccReplPack.getCcReplPackId());
                Integer totalUpdatedFinalBuyQty = cc.getFixtures().stream()
                      .filter(ccFix -> MerchMethod.getMerchMethodIdFromDescription(ccFix.getMerchMethod()).equals(merchMethodCode))
                      .map(Fixtures::getSizes)
                      .flatMap(Collection::stream)
                      .mapToInt(Size::getOptFinalBuyQty)
                      .sum();

                Integer rollupDifference = ccReplPack.getFinalBuyUnits() - totalUpdatedFinalBuyQty;
                Integer updatedReplnQty = ccReplPack.getReplUnits() + rollupDifference;
                ccReplPack.setReplUnits(updatedReplnQty);
                //ccReplnPkConsRepository.save(ccReplPack);

                styleReplPacks.stream()
                      .filter(styleReplPack -> getMerchMethodCode(styleReplPack.getStyleReplPackId()).equals(merchMethodCode))
                      .forEach(styleReplPack -> {
                          styleReplPack.setReplUnits(styleReplPack.getReplUnits() + rollupDifference);
                          styleReplPack.setReplPackCnt(UpdateReplnConfigMapper.getReplenishmentPackCount(styleReplPack.getReplUnits(), styleReplPack.getVendorPackCnt()));
                          //styleReplnPkConsRepository.save(styleReplPack);
                      });
            });
            ccReplnPkConsRepository.saveAll(ccReplPacks);
            styleReplnPkConsRepository.saveAll(styleReplPacks);
        });
    }


    public void updateRCMerchSubCatg(Long planId, Integer finelineNbr, Map<Integer, Integer> rollupDiffByMerchMethod) {
        List<SubCatgReplPack> subCatgReplPacks = subCatgReplnPkConsRepository.findByPlanIdAndFinelineNbr(planId, finelineNbr).orElse(Collections.emptyList());

        subCatgReplPacks.forEach(subCatgReplPack -> {
            Integer merchMethodCode = getMerchMethodCode(subCatgReplPack.getSubCatgReplPackId());
            subCatgReplPack.setReplUnits(subCatgReplPack.getReplUnits() + rollupDiffByMerchMethod.getOrDefault(merchMethodCode, 0));
            subCatgReplPack.setReplPackCnt(UpdateReplnConfigMapper.getReplenishmentPackCount(subCatgReplPack.getReplUnits(), subCatgReplPack.getVendorPackCnt()));
        });

        subCatgReplnPkConsRepository.saveAll(subCatgReplPacks);
    }

    public Map<Integer, Integer> updateRCMerchFineline(Long planId, Integer finelineNbr, ISAndBPQtyDTO isAndBPQtyDTO) {
        Map<Integer, Integer> replnDifferenceByMerchMethod = new HashMap<>();
        List<FinelineReplPack> finelines = finelineReplnPkConsRepository.findByPlanIdAndFinelineNbr(planId, finelineNbr).orElse(Collections.emptyList());

        finelines.forEach(finelineMerchMethod -> {
            final Integer merchMethodCode = getMerchMethodCode(finelineMerchMethod.getFinelineReplPackId());

            if (!replnDifferenceByMerchMethod.containsKey(merchMethodCode))
                replnDifferenceByMerchMethod.put(merchMethodCode, 0);

            int updatedTotalBuyQty = isAndBPQtyDTO.getCustomerChoices().stream()
                  .map(CustomerChoices::getFixtures)
                  .flatMap(Collection::stream)
                  .filter(merchMethod -> MerchMethod.getMerchMethodIdFromDescription(merchMethod.getMerchMethod()).equals(merchMethodCode))
                  .map(Fixtures::getSizes)
                  .flatMap(Collection::stream)
                  .mapToInt(Size::getOptFinalBuyQty)
                  .sum();

            int updatedReplnQty = finelineMerchMethod.getFinalBuyUnits() - updatedTotalBuyQty;
            int replnDifference = (updatedReplnQty - Optional.ofNullable(finelineMerchMethod.getReplUnits()).orElse(0));
            replnDifferenceByMerchMethod.put(merchMethodCode, replnDifference);
            finelineMerchMethod.setReplUnits(updatedReplnQty);
        });

        finelineReplnPkConsRepository.saveAll(finelines);
        return replnDifferenceByMerchMethod;
    }

    public void updateRCMerchCatg(Long planId, Integer finelineNbr, Map<Integer, Integer> rollupDiffByMerchMethod) {
        List<MerchCatgReplPack> merchCatgReplPacks = merchCatgReplPackRepository.findByPlanIdAndFinelineNbr(planId, finelineNbr).orElse(Collections.emptyList());

        merchCatgReplPacks.forEach(merchCatgReplPack -> {
            Integer merchMethodCode = merchCatgReplPack.getMerchCatgReplPackId().getFixtureTypeRollupId();
            merchCatgReplPack.setReplUnits(merchCatgReplPack.getReplUnits() + rollupDiffByMerchMethod.getOrDefault(merchMethodCode, 0));
            merchCatgReplPack.setReplPackCnt(UpdateReplnConfigMapper.getReplenishmentPackCount(merchCatgReplPack.getReplUnits(), merchCatgReplPack.getVendorPackCnt()));
        });
        merchCatgReplPackRepository.saveAll(merchCatgReplPacks);
    }

    private Integer getMerchMethodCode(SubCatgReplPackId subCatgReplPackId) {
        return subCatgReplPackId.getMerchCatgReplPackId().getFixtureTypeRollupId();
    }
    private Integer getMerchMethodCode(FinelineReplPackId finelineReplPackId) {
        return getMerchMethodCode(finelineReplPackId.getSubCatgReplPackId());
    }
    private Integer getMerchMethodCode(StyleReplPackId styleReplPackId) {
        return getMerchMethodCode(styleReplPackId.getFinelineReplPackId());
    }
    private Integer getMerchMethodCode(CcReplPackId ccReplPackId) {
        return getMerchMethodCode(ccReplPackId.getStyleReplPackId());
    }
    private Integer getMerchMethodCode(CcMmReplPackId ccMmReplPackId) {
        return getMerchMethodCode(ccMmReplPackId.getCcReplPackId());
    }
    private Integer getMerchMethodCode(CcSpMmReplPackId ccSpMmReplPackId) {
        return getMerchMethodCode(ccSpMmReplPackId.getCcMmReplPackId());
    }

}
