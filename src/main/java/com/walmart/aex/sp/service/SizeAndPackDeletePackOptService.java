package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.STORE_CHANNEL_ID;

@Service
@Slf4j
public class SizeAndPackDeletePackOptService {

    public Set<MerchantPackOptimization> updateMerchantPackOpt(List<MerchantPackOptimization> merchantPackOpts, Lvl3 lvl3, Fineline strongKeyFineline) {
        Set<MerchantPackOptimization> merchantPackOptimizationSet = new HashSet<>(merchantPackOpts);
        if (!CollectionUtils.isEmpty(merchantPackOptimizationSet)) {
            deleteMerchantPackOpt(merchantPackOptimizationSet, lvl3, strongKeyFineline);
        }
        return merchantPackOptimizationSet;
    }


    private Set<MerchantPackOptimization> deleteMerchantPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Fineline strongKeyFineline) {
        deleteSubCatPackOpt(merchantPackOptimizationSet, lvl3, strongKeyFineline);
        merchantPackOptimizationSet.removeIf(merchantPackOptimization -> CollectionUtils.isEmpty(merchantPackOptimization.getSubCatgPackOptimization()) && merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3.getLvl3Nbr()) && (merchantPackOptimization.getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID)));
        return merchantPackOptimizationSet;
    }

    private void deleteSubCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Fineline strongKeyFineline) {
        lvl3.getLvl4List().forEach(lvl4 -> {
            deleteFinelinePackOpt(merchantPackOptimizationSet, strongKeyFineline, lvl3, lvl4);
            Set<MerchantPackOptimization> merchantPackOptimizationSet1 = fetchMerchCatPackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr());
            if (!CollectionUtils.isEmpty(merchantPackOptimizationSet1)) {
                merchantPackOptimizationSet1.forEach(merchantPackOptimization -> merchantPackOptimization.getSubCatgPackOptimization().removeIf(subCatgPlan -> CollectionUtils.isEmpty(subCatgPlan.getFinelinepackOptimization()) && subCatgPlan.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4.getLvl4Nbr()) && subCatgPlan.getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID)));
            }
        });
    }

    private void deleteFinelinePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Fineline strongKeyFineline, Lvl3 lvl3, Lvl4 lvl4) {
        lvl4.getFinelines().forEach(fineline -> {
            if (strongKeyFineline.getStyles() != null) {
                deleteStylePackOpt(merchantPackOptimizationSet, lvl3, lvl4, strongKeyFineline);
            }
            Set<SubCatgPackOptimization> subCatgPackOptimizationSet = fetchSubCatPackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr());
            if (!CollectionUtils.isEmpty(subCatgPackOptimizationSet)) {
                subCatgPackOptimizationSet.forEach(subCatgPackOptimization -> {
                    subCatgPackOptimization.getFinelinepackOptimization().removeIf(fineLinePlan -> CollectionUtils.isEmpty(fineLinePlan.getStylePackOptimization()) && fineLinePlan.getFinelinePackOptId().getFinelineNbr().equals(fineline.getFinelineNbr()) && fineLinePlan.getFinelinePackOptId().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID));
                });
                if (strongKeyFineline.getStyles() == null) {
                    subCatgPackOptimizationSet.forEach(subCatgPackOptimization -> {
                        subCatgPackOptimization.getFinelinepackOptimization().removeIf(fineLinePlan -> fineLinePlan.getFinelinePackOptId().getFinelineNbr().equals(fineline.getFinelineNbr()) && fineLinePlan.getFinelinePackOptId().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID));
                    });
                }
            }
        });
    }


    private void deleteStylePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Lvl4 lvl4, Fineline strongKeyFineline) {
        strongKeyFineline.getStyles().forEach(style -> {
            if (!style.getCustomerChoices().isEmpty()) {
                deleteCCPackOpt(merchantPackOptimizationSet, lvl3, lvl4, strongKeyFineline, style);
            }
            Set<FineLinePackOptimization> finelinePackOptimizationSet = fetchFinelinePackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), strongKeyFineline.getFinelineNbr());
            if (!CollectionUtils.isEmpty(finelinePackOptimizationSet)) {
                finelinePackOptimizationSet.forEach(fineLinePackOptimization -> fineLinePackOptimization.getStylePackOptimization().removeIf(stylePackOptimization -> CollectionUtils.isEmpty(stylePackOptimization.getCcPackOptimization()) && stylePackOptimization.getStylePackoptimizationId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr()) && stylePackOptimization.getStylePackoptimizationId().getFinelinePackOptimizationID().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID)
                ));
                if (style.getCustomerChoices().isEmpty()) {
                    finelinePackOptimizationSet.forEach(fineLinePackOptimization -> fineLinePackOptimization.getStylePackOptimization().removeIf(stylePackOptimization -> stylePackOptimization.getStylePackoptimizationId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr()) && stylePackOptimization.getStylePackoptimizationId().getFinelinePackOptimizationID().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID)
                    ));
                }
            }
        });
    }

    private void deleteCCPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
        style.getCustomerChoices().forEach(customerChoice -> {
            Set<StylePackOptimization> stylePackOptimizationSet = fetchStylePackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr());
            if (!CollectionUtils.isEmpty(stylePackOptimizationSet)) {
                stylePackOptimizationSet.forEach(stylePlan -> stylePlan.getCcPackOptimization().removeIf(customerChoicePlan -> customerChoicePlan.getCcPackOptimizationId().getCustomerChoice().equalsIgnoreCase(customerChoice.getCcId()) && customerChoicePlan.getCcPackOptimizationId().getStylePackOptimizationID().getFinelinePackOptimizationID().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(STORE_CHANNEL_ID)));
            }
        });
    }

    private Set<MerchantPackOptimization> fetchMerchCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchantPackOptimization -> merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3Nbr))
                .collect(Collectors.toSet());
    }

    private Set<SubCatgPackOptimization> fetchSubCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr) {
        return fetchMerchCatPackOpt(merchantPackOptimizationSet, lvl3Nbr)
                .stream()
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .flatMap(Collection::stream)
                .filter(subCatgPackOptimization -> subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4Nbr))
                .collect(Collectors.toSet());
    }

    private Set<FineLinePackOptimization> fetchFinelinePackOpt(Set<MerchantPackOptimization> merchPackOptimization, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) {
        return fetchSubCatPackOpt(merchPackOptimization, lvl3Nbr, lvl4Nbr)
                .stream()
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .flatMap(Collection::stream)
                .filter(finelinePackOpt -> finelinePackOpt.getFinelinePackOptId().getFinelineNbr().equals(finelineNbr))
                .collect(Collectors.toSet());
    }

    private Set<StylePackOptimization> fetchStylePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum) {
        return fetchFinelinePackOpt(merchantPackOptimizationSet, lvl3Nbr, lvl4Nbr, finelineNbr)
                .stream()
                .map(FineLinePackOptimization::getStylePackOptimization)
                .flatMap(Collection::stream)
                .filter(stylePackOpt -> stylePackOpt.getStylePackoptimizationId().getStyleNbr().equals(styleNum))
                .collect(Collectors.toSet());
    }

}
