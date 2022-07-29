package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class SizeAndPackObjectMapper {

    public Set<MerchCatPlan> setMerchCatPlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, String channel) {
        Set<MerchCatPlan> merchCatPlanSet = new HashSet<>();
        Integer channelId = ChannelType.getChannelIdFromName(channel);
        List<Integer> channelList = getChannelListFromChannelId(channelId);

        if (!CollectionUtils.isEmpty(channelList)) {
            channelList.forEach(chan -> {
                MerchCatPlanId merchCatPlanId = new MerchCatPlanId(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), chan);
                MerchCatPlan merchCatPlan = Optional.of(merchCatPlanSet)
                        .stream()
                        .flatMap(Collection::stream).filter(merchCatPlan1 -> merchCatPlan1.getMerchCatPlanId().equals(merchCatPlanId))
                        .findFirst()
                        .orElse(new MerchCatPlan());
                if (merchCatPlan.getMerchCatPlanId() == null) {
                    merchCatPlan.setMerchCatPlanId(merchCatPlanId);
                }
                merchCatPlan.setLvl0Desc(request.getLvl0Name());
                merchCatPlan.setLvl1Desc(lvl1.getLvl1Name());
                merchCatPlan.setLvl2Desc(lvl2.getLvl2Name());
                merchCatPlan.setLvl3Desc(lvl3.getLvl3Name());
                if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
                    merchCatPlan.setSubCatPlans(setSubCatPlans(merchCatPlan, request, lvl1, lvl2, lvl3, lvl3.getLvl4List()));
                }
                merchCatPlanSet.add(merchCatPlan);
            });
        }
        return merchCatPlanSet;
    }

    public Set<SubCatPlan> setSubCatPlans(MerchCatPlan merchCatPlan, PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, List<Lvl4> lvl4s) {
        Set<SubCatPlan> subCatPlanSet = Optional.ofNullable(merchCatPlan.getSubCatPlans())
                .orElse(new HashSet<>());
        for (Lvl4 lvl4 : lvl4s) {
            SubCatPlanId subCatPlanId = new SubCatPlanId(merchCatPlan.getMerchCatPlanId(), lvl4.getLvl4Nbr());
            SubCatPlan subCatPlan = Optional.of(subCatPlanSet)
                    .stream()
                    .flatMap(Collection::stream).filter(subCatPlan1 -> subCatPlan1.getSubCatPlanId().equals(subCatPlanId))
                    .findFirst()
                    .orElse(new SubCatPlan());
            if (subCatPlan.getSubCatPlanId() == null) {
                subCatPlan.setSubCatPlanId(subCatPlanId);
            }
            subCatPlan.setLvl0Desc(request.getLvl0Name());
            subCatPlan.setLvl1Desc(lvl1.getLvl1Name());
            subCatPlan.setLvl2Desc(lvl2.getLvl2Name());
            subCatPlan.setLvl3Desc(lvl3.getLvl3Name());
            subCatPlan.setLvl4Desc(lvl4.getLvl4Name());
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                subCatPlan.setFinelinePlans(setFinelinePlans(subCatPlan, lvl4.getFinelines()));
            }
            subCatPlanSet.add(subCatPlan);
        }
        return subCatPlanSet;
    }

    public Set<FinelinePlan> setFinelinePlans(SubCatPlan subCatPlan, List<Fineline> finelines) {
        Set<FinelinePlan> finelinePlanSet = Optional.ofNullable(subCatPlan.getFinelinePlans())
                .orElse(new HashSet<>());
        for (Fineline fineline : finelines) {
            FinelinePlanId finelinePlanId = new FinelinePlanId(subCatPlan.getSubCatPlanId(), fineline.getFinelineNbr());
            FinelinePlan finelinePlan = Optional.of(finelinePlanSet)
                    .stream()
                    .flatMap(Collection::stream).filter(finelinePlan1 -> finelinePlan1.getFinelinePlanId().equals(finelinePlanId))
                    .findFirst()
                    .orElse(new FinelinePlan());
            if (finelinePlan.getFinelinePlanId() == null) {
                finelinePlan.setFinelinePlanId(finelinePlanId);
            }
            finelinePlan.setFinelineDesc(fineline.getFinelineName());
            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                finelinePlan.setStylePlans(setStylesPlans(finelinePlan, fineline.getStyles()));
            }
            finelinePlanSet.add(finelinePlan);
        }
        return finelinePlanSet;
    }

    public Set<StylePlan> setStylesPlans(FinelinePlan finelinePlan, List<Style> styles) {
        Set<StylePlan> stylePlanSet = Optional.ofNullable(finelinePlan.getStylePlans())
                .orElse(new HashSet<>());
        for (Style style : styles) {
            if (finelinePlan.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getChannelId().equals(ChannelType.getChannelIdFromName(style.getChannel()))
                    || ChannelType.getChannelIdFromName(style.getChannel()).equals(3)) {
                StylePlanId stylePlanId = new StylePlanId(finelinePlan.getFinelinePlanId(), style.getStyleNbr());
                StylePlan stylePlan = Optional.of(stylePlanSet)
                        .stream()
                        .flatMap(Collection::stream).filter(stylePlan1 -> stylePlan1.getStylePlanId().equals(stylePlanId))
                        .findFirst()
                        .orElse(new StylePlan());
                if (stylePlan.getStylePlanId() == null) {
                    stylePlan.setStylePlanId(stylePlanId);
                }
                if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                    stylePlan.setCustChoicePlans(setCustChoicePlans(stylePlan, style.getCustomerChoices()));
                }
                stylePlanSet.add(stylePlan);
            }
        }
        return stylePlanSet;
    }

    public Set<CustChoicePlan> setCustChoicePlans(StylePlan stylePlan, List<CustomerChoice> customerChoices) {
        Set<CustChoicePlan> custChoicePlanSet = Optional.ofNullable(stylePlan.getCustChoicePlans())
                .orElse(new HashSet<>());
        for (CustomerChoice customerChoice : customerChoices) {
            if (stylePlan.getStylePlanId().getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getChannelId().equals(ChannelType.getChannelIdFromName(customerChoice.getChannel()))
                    || ChannelType.getChannelIdFromName(customerChoice.getChannel()).equals(3)) {
                CustChoicePlanId custChoicePlanId = new CustChoicePlanId(stylePlan.getStylePlanId(), customerChoice.getCcId());
                CustChoicePlan custChoicePlan = Optional.of(custChoicePlanSet)
                        .stream()
                        .flatMap(Collection::stream).filter(custChoicePlan1  -> custChoicePlan1.getCustChoicePlanId().equals(custChoicePlanId))
                        .findFirst()
                        .orElse(new CustChoicePlan());
                if (custChoicePlan.getCustChoicePlanId() == null) {
                    custChoicePlan.setCustChoicePlanId(custChoicePlanId);
                }
                custChoicePlan.setColorName(customerChoice.getColorName());
                custChoicePlanSet.add(custChoicePlan);
            }
        }
        return custChoicePlanSet;
    }

    //When channel is Omni, two entries should be made. One for store and another for online
    private List<Integer> getChannelListFromChannelId(Integer channelId) {
        List<Integer> channelList = new ArrayList<>();
        if (channelId.equals(3)) {
            channelList.add(1);
            channelList.add(2);
        }
        else {
            channelList.add(channelId);
        }
        return channelList;
    }
}
