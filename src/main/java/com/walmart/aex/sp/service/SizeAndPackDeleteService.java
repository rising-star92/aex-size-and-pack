package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Slf4j
public class SizeAndPackDeleteService {
    private final FineLineReplenishmentRepository finelineReplenishmentRepository;
    private final StyleReplnPkConsRepository styleReplnPkConsRepository;
    private final SpCustomerChoiceReplenishmentRepository spCustomerChoiceReplenishmentRepository;
    private final SpStyleChannelFixtureRepository spStyleChannelFixtureRepository;
    private final SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository;
    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final StylePackOptimizationRepository stylePackOptimizationRepository;
    private final FinelinePackOptConsRepository finelinePackOptConsRepository;

    public SizeAndPackDeleteService(FineLineReplenishmentRepository finelineReplenishmentRepository, StyleReplnPkConsRepository styleReplnPkConsRepository, SpCustomerChoiceReplenishmentRepository spCustomerChoiceReplenishmentRepository, SpStyleChannelFixtureRepository spStyleChannelFixtureRepository, SpCustomerChoiceChannelFixtureRepository spCustomerChoiceChannelFixtureRepository, SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository, CcPackOptimizationRepository ccPackOptimizationRepository, StylePackOptimizationRepository stylePackOptimizationRepository, FinelinePackOptConsRepository finelinePackOptConsRepository) {
        this.finelineReplenishmentRepository = finelineReplenishmentRepository;
        this.styleReplnPkConsRepository = styleReplnPkConsRepository;
        this.spCustomerChoiceReplenishmentRepository = spCustomerChoiceReplenishmentRepository;
        this.spStyleChannelFixtureRepository = spStyleChannelFixtureRepository;
        this.spCustomerChoiceChannelFixtureRepository = spCustomerChoiceChannelFixtureRepository;
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.stylePackOptimizationRepository = stylePackOptimizationRepository;
        this.finelinePackOptConsRepository = finelinePackOptConsRepository;
    }

    void deleteSizeAndPackDataAtFl(Long planId, Integer lvl3Nbr,
                                   Integer lvl4Nbr, Integer finelineNbr) {
        log.info("Deleting fineline replenishment info for finelineNbr: {}, and planId: {}", finelineNbr, planId);
        finelineReplenishmentRepository.deleteByFinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_planIdAndFinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_repTLvl3AndFinelineReplPackId_SubCatgReplPackId_repTLvl4AndFinelineReplPackId_finelineNbr(planId, lvl3Nbr, lvl4Nbr, finelineNbr);
        log.info("Deleting fineline buy qty info for finelineNbr: {}, and planId: {}", finelineNbr, planId);
        spFineLineChannelFixtureRepository.deleteBySpFineLineChannelFixtureId_planIdAndSpFineLineChannelFixtureId_lvl3NbrAndSpFineLineChannelFixtureId_lvl4NbrAndSpFineLineChannelFixtureId_fineLineNbr(planId, lvl3Nbr, lvl4Nbr, finelineNbr);
        log.info("Deleting fineline pack optimization info for finelineNbr: {}, and planId: {}", finelineNbr, planId);
        finelinePackOptConsRepository.deleteByFinelinePackOptId_SubCatgPackOptimizationID_MerchantPackOptimizationID_planIdAndFinelinePackOptId_SubCatgPackOptimizationID_MerchantPackOptimizationID_repTLvl3AndFinelinePackOptId_SubCatgPackOptimizationID_repTLvl4AndFinelinePackOptId_finelineNbr(planId, lvl3Nbr, lvl4Nbr, finelineNbr);
    }

    void deleteSizeAndPackDataAtStyleOrCC(List<Style> styles, Long planId, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) {
        log.info("Deleting Style or CC info for finelineNbr: {}, and planId: {}", finelineNbr, planId);
        for (Style style : styles) {
            if (style.getStyleNbr() != null && CollectionUtils.isEmpty(style.getCustomerChoices())) {
                log.info("Deleting Style Replenishment for styleNbr: {}, and planId: {}", style.getStyleNbr(), planId);
                styleReplnPkConsRepository.deleteByStyleReplPackId_FinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_planIdAndStyleReplPackId_FinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_repTLvl3AndStyleReplPackId_FinelineReplPackId_SubCatgReplPackId_repTLvl4AndStyleReplPackId_FinelineReplPackId_finelineNbrAndStyleReplPackId_styleNbr(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
                log.info("Deleting Style Buy Qty for styleNbr: {}, and planId: {}", style.getStyleNbr(), planId);
                spStyleChannelFixtureRepository.deleteBySpStyleChannelFixtureId_SpFineLineChannelFixtureId_planIdAndSpStyleChannelFixtureId_SpFineLineChannelFixtureId_lvl3NbrAndSpStyleChannelFixtureId_SpFineLineChannelFixtureId_lvl4NbrAndSpStyleChannelFixtureId_SpFineLineChannelFixtureId_fineLineNbrAndSpStyleChannelFixtureId_styleNbr(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
                log.info("Deleting Style Pack Optimization for styleNbr: {}, and planId: {}", style.getStyleNbr(), planId);
                stylePackOptimizationRepository.deleteByStylePackoptimizationId_FinelinePackOptimizationID_SubCatgPackOptimizationID_MerchantPackOptimizationID_planIdAndStylePackoptimizationId_FinelinePackOptimizationID_SubCatgPackOptimizationID_MerchantPackOptimizationID_repTLvl3AndStylePackoptimizationId_FinelinePackOptimizationID_SubCatgPackOptimizationID_repTLvl4AndStylePackoptimizationId_FinelinePackOptimizationID_finelineNbrAndStylePackoptimizationId_styleNbr(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
            } else if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                deleteSizeAndPackDataAtCc(style.getCustomerChoices(), planId,
                        lvl3Nbr, lvl4Nbr, finelineNbr, style.getStyleNbr());
            }
        }

    }

    void deleteSizeAndPackDataAtCc(List<CustomerChoice> customerChoices,
                                   Long planId, Integer lvl3Nbr,
                                   Integer lvl4Nbr, Integer finelineNbr, String styleNbr) {
        for (CustomerChoice cc : customerChoices) {
            if (cc.getCcId() != null) {
                log.info("Deleting ccId Replenishment for ccId: {}, and planId: {}", cc.getCcId(), planId);
                spCustomerChoiceReplenishmentRepository.deleteByCcReplPackId_StyleReplPackId_FinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_planIdAndCcReplPackId_StyleReplPackId_FinelineReplPackId_SubCatgReplPackId_MerchCatgReplPackId_repTLvl3AndCcReplPackId_StyleReplPackId_FinelineReplPackId_SubCatgReplPackId_repTLvl4AndCcReplPackId_StyleReplPackId_FinelineReplPackId_finelineNbrAndCcReplPackId_StyleReplPackId_styleNbrAndCcReplPackId_customerChoice(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
                log.info("Deleting ccId Buy Qty for ccId: {}, and planId: {}", cc.getCcId(), planId);
                spCustomerChoiceChannelFixtureRepository.deleteBySpCustomerChoiceChannelFixtureId_SpStyleChannelFixtureId_SpFineLineChannelFixtureId_planIdAndSpCustomerChoiceChannelFixtureId_SpStyleChannelFixtureId_SpFineLineChannelFixtureId_lvl3NbrAndSpCustomerChoiceChannelFixtureId_SpStyleChannelFixtureId_SpFineLineChannelFixtureId_lvl4NbrAndSpCustomerChoiceChannelFixtureId_SpStyleChannelFixtureId_SpFineLineChannelFixtureId_fineLineNbrAndSpCustomerChoiceChannelFixtureId_SpStyleChannelFixtureId_styleNbrAndSpCustomerChoiceChannelFixtureId_customerChoice(
                        planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
                log.info("Deleting ccId Pack Optimization for ccId: {}, and planId: {}", cc.getCcId(), planId);
                ccPackOptimizationRepository.deleteByCcPackOptimizationId_StylePackOptimizationID_FinelinePackOptimizationID_SubCatgPackOptimizationID_MerchantPackOptimizationID_planIdAndCcPackOptimizationId_StylePackOptimizationID_FinelinePackOptimizationID_SubCatgPackOptimizationID_MerchantPackOptimizationID_repTLvl3AndCcPackOptimizationId_StylePackOptimizationID_FinelinePackOptimizationID_SubCatgPackOptimizationID_repTLvl4AndCcPackOptimizationId_StylePackOptimizationID_FinelinePackOptimizationID_finelineNbrAndCcPackOptimizationId_StylePackOptimizationID_styleNbrAndCcPackOptimizationId_customerChoice(planId, lvl3Nbr, lvl4Nbr, finelineNbr, styleNbr, cc.getCcId());
            }
        }
    }

}
