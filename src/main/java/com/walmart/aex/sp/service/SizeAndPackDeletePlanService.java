package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SizeAndPackDeletePlanService {

    public Set<MerchCatPlan> updateMerchCatPlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, Fineline strongKeyFineline, MerchCatPlanRepository merchCatPlanRepository) {

        List<MerchCatPlan> merchCatPlans = merchCatPlanRepository.findMerchCatPlanByMerchCatPlanId_planIdAndMerchCatPlanId_lvl0NbrAndMerchCatPlanId_lvl1NbrAndMerchCatPlanId_lvl2NbrAndMerchCatPlanId_lvl3Nbr(request.getPlanId(),
                request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr());
        Set<MerchCatPlan> merchCatPlanSet = merchCatPlans.stream().collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(merchCatPlanSet)) {
            deleteMerchCatPlan(merchCatPlanSet, lvl3, strongKeyFineline, merchCatPlanRepository);
        }
        return merchCatPlanSet;
    }


    private Set<MerchCatPlan> deleteMerchCatPlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Fineline strongKeyFineline, MerchCatPlanRepository merchCatPlanRepository) {
        deleteMerchSubCatPlan(merchCatPlanSet, lvl3, strongKeyFineline);
        merchCatPlanSet.removeIf(merchCatPlan1 -> CollectionUtils.isEmpty(merchCatPlan1.getSubCatPlans()) && merchCatPlan1.getMerchCatPlanId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()));
        return merchCatPlanSet;
    }

    private void deleteMerchSubCatPlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Fineline strongKeyFineline) {
        lvl3.getLvl4List().forEach(lvl4 -> {
            deleteFinelinePlan(merchCatPlanSet, strongKeyFineline, lvl3, lvl4);
            Set<MerchCatPlan> merchCatPlanSet1 = fetchMerchCatPlan(merchCatPlanSet, lvl3.getLvl3Nbr());
            if (!CollectionUtils.isEmpty(merchCatPlanSet1)) {
                merchCatPlanSet1.forEach(merchCatPlan -> merchCatPlan.getSubCatPlans().removeIf(subCatgPlan -> CollectionUtils.isEmpty(subCatgPlan.getFinelinePlans()) && subCatgPlan.getSubCatPlanId().getLvl4Nbr().equals(lvl4.getLvl4Nbr())));
            }
        });

    }

    private void deleteFinelinePlan(Set<MerchCatPlan> merchCatPlanSet, Fineline strongKeyFineline, Lvl3 lvl3, Lvl4 lvl4) {
        lvl4.getFinelines().forEach(fineline -> {
            if (strongKeyFineline.getStyles() != null) {
                deleteStylePlan(merchCatPlanSet, lvl3, lvl4, strongKeyFineline);
            }
            Set<SubCatPlan> subCatPlanSet = fetchMerchSubCatPlan(merchCatPlanSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr());
            if (!CollectionUtils.isEmpty(subCatPlanSet)) {
                subCatPlanSet.forEach(subCatPlanPlan -> {
                    subCatPlanPlan.getFinelinePlans().removeIf(fineLinePlan -> CollectionUtils.isEmpty(fineLinePlan.getStylePlans()) && fineLinePlan.getFinelinePlanId().getFinelineNbr().equals(fineline.getFinelineNbr()));
                });
                if (strongKeyFineline.getStyles() == null) {
                    subCatPlanSet.forEach(subCatPlanPlan -> {
                        subCatPlanPlan.getFinelinePlans().removeIf(fineLinePlan -> fineLinePlan.getFinelinePlanId().getFinelineNbr().equals(fineline.getFinelineNbr()));
                    });
                }
            }
        });
    }

    private void deleteStylePlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Lvl4 lvl4, Fineline strongKeyFineline) {
        strongKeyFineline.getStyles().forEach(style -> {
            if (style.getCustomerChoices().size() > 0) {
                deleteCCPlan(merchCatPlanSet, lvl3, lvl4, strongKeyFineline, style);
            }
            Set<FinelinePlan> finelinePlanSet = fetchFinelinePlan(merchCatPlanSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), strongKeyFineline.getFinelineNbr());
            if (!CollectionUtils.isEmpty(finelinePlanSet)) {
                finelinePlanSet.forEach(finelinePlan -> finelinePlan.getStylePlans().removeIf(stylePlan -> CollectionUtils.isEmpty(stylePlan.getCustChoicePlans()) && stylePlan.getStylePlanId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr())
                ));
                if (style.getCustomerChoices().size() == 0) {
                    finelinePlanSet.forEach(finelinePlan -> finelinePlan.getStylePlans().removeIf(stylePlan -> stylePlan.getStylePlanId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr())
                    ));
                }
            }
        });
    }

    private void deleteCCPlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
        style.getCustomerChoices().forEach(customerChoice -> {
            Set<StylePlan> stylePlanSet = fetchStylePlan(merchCatPlanSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr());
            if (!CollectionUtils.isEmpty(stylePlanSet)) {
                stylePlanSet.forEach(stylePlan -> stylePlan.getCustChoicePlans().removeIf(customerChoicePlan -> customerChoicePlan.getCustChoicePlanId().getCcId().equalsIgnoreCase(customerChoice.getCcId())));
            }
        });
    }

    private Set<MerchCatPlan> fetchMerchCatPlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr) {
        return Optional.ofNullable(merchCatPlanSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatPlan -> merchCatPlan.getMerchCatPlanId().getLvl3Nbr().equals(lvl3Nbr))
                .collect(Collectors.toSet());
    }

    private Set<SubCatPlan> fetchMerchSubCatPlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer lvl4Nbr) {
        return fetchMerchCatPlan(merchCatPlanSet,lvl3Nbr)
                .stream()
                .map(MerchCatPlan::getSubCatPlans)
                .flatMap(Collection::stream)
                .filter(merchSubCatPlan -> merchSubCatPlan.getSubCatPlanId().getLvl4Nbr().equals(lvl4Nbr))
                .collect(Collectors.toSet());
    }

    private Set<FinelinePlan> fetchFinelinePlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr) {
        return fetchMerchSubCatPlan(merchCatPlanSet,lvl3Nbr,lvl4Nbr)
                .stream()
                .map(SubCatPlan::getFinelinePlans)
                .flatMap(Collection::stream)
                .filter(finelinePlan -> finelinePlan.getFinelinePlanId().getFinelineNbr().equals(finelineNbr))
                .collect(Collectors.toSet());
    }

    private Set<StylePlan> fetchStylePlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum) {
        return fetchFinelinePlan(merchCatPlanSet,lvl3Nbr,lvl4Nbr,finelineNbr)
                .stream()
                .map(FinelinePlan::getStylePlans)
                .flatMap(Collection::stream)
                .filter(stylePlan -> stylePlan.getStylePlanId().getStyleNbr().equals(styleNum))
                .collect(Collectors.toSet());
    }

}
