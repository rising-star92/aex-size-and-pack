package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.Lvl1;
import com.walmart.aex.sp.dto.planhierarchy.Lvl2;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.entity.CustChoicePlan;
import com.walmart.aex.sp.entity.CustChoicePlanId;
import com.walmart.aex.sp.entity.FinelinePlan;
import com.walmart.aex.sp.entity.FinelinePlanId;
import com.walmart.aex.sp.entity.MerchCatPlan;
import com.walmart.aex.sp.entity.MerchCatPlanId;
import com.walmart.aex.sp.entity.StylePlan;
import com.walmart.aex.sp.entity.StylePlanId;
import com.walmart.aex.sp.entity.SubCatPlan;
import com.walmart.aex.sp.entity.SubCatPlanId;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.MerchCatPlanRepository;
import com.walmart.aex.sp.util.CommonUtil;
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

    private final MerchCatPlanRepository merchCatPlanRepository;

    public SizeAndPackObjectMapper(MerchCatPlanRepository merchCatPlanRepository) {
        this.merchCatPlanRepository = merchCatPlanRepository;
    }

    public Set<MerchCatPlan> setMerchCatPlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3) {
        Set<MerchCatPlan> merchCatPlanSet = new HashSet<>();
        Integer finelineChannel = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(lvl3));
        List<Integer> channelList = getChannelListFromChannelId(finelineChannel);

        if (!CollectionUtils.isEmpty(channelList)) {
            channelList.forEach(chan -> {
                MerchCatPlanId merchCatPlanId = new MerchCatPlanId(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), chan);
                MerchCatPlan merchCatPlan = merchCatPlanRepository.findById(merchCatPlanId).orElse(new MerchCatPlan());
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
            finelinePlan.setAltFinelineName(fineline.getAltFinelineName());
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
                stylePlan.setAltStyleDesc(style.getAltStyleDesc());
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
                custChoicePlan.setColorFamilyDesc(customerChoice.getColorFamily());
                custChoicePlan.setAltCcDesc(customerChoice.getAltCcDesc());
                custChoicePlanSet.add(custChoicePlan);
            }
        }
        return custChoicePlanSet;
    }

    //When channel is Omni, two entries should be made. One for store and another for online
     List<Integer> getChannelListFromChannelId(Integer channelId) {
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

    public Set<MerchCatPlan> updateMerchCatPlan(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3) {
        Integer finelineChannel = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(lvl3));
        List<Integer> channelList = getChannelListFromChannelId(finelineChannel);

        List<MerchCatPlan> merchCatPlans = merchCatPlanRepository.findMerchCatPlanByMerchCatPlanId_planIdAndMerchCatPlanId_lvl0NbrAndMerchCatPlanId_lvl1NbrAndMerchCatPlanId_lvl2NbrAndMerchCatPlanId_lvl3Nbr(request.getPlanId(),
                request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr());

        Set<MerchCatPlan> merchCatPlanSet = new HashSet<>(merchCatPlans);
        if (!CollectionUtils.isEmpty(channelList)) {
            channelList.forEach(channelId -> {
                if (!CollectionUtils.isEmpty(merchCatPlanSet)) {
                   deleteMerchCatPlan(merchCatPlanSet, lvl3, channelId);
                }
            });
        }
        return setMerchCatPlan(request,lvl1, lvl2, lvl3);
    }

    private void deleteMerchCatPlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Integer channelId) {
        deleteMerchSubCatPlan(merchCatPlanSet, lvl3, channelId);
        if (!channelId.equals(3)) {
            MerchCatPlan merchCatPlan = fetchMerchCatPlan(merchCatPlanSet, lvl3.getLvl3Nbr(), channelId);
            if(merchCatPlan != null && CollectionUtils.isEmpty(merchCatPlan.getSubCatPlans())) {
                //Only removing from set is not deleting the entry in the DB. Hence deleting by the ID
                merchCatPlanRepository.deleteById(merchCatPlan.getMerchCatPlanId());
            }
            merchCatPlanSet.removeIf(merchCatPlan1 -> CollectionUtils.isEmpty(merchCatPlan1.getSubCatPlans()) && merchCatPlan1.getMerchCatPlanId().getLvl3Nbr().equals(lvl3.getLvl3Nbr()) &&
                    !merchCatPlan1.getMerchCatPlanId().getChannelId().equals(channelId));
        }
    }

    private void deleteMerchSubCatPlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Integer channelId) {
        lvl3.getLvl4List().forEach(lvl4 -> {
            deleteFinelinePlan(merchCatPlanSet, lvl3, lvl4);
            if (!channelId.equals(3)) {
                MerchCatPlan merchCatPlan = fetchMerchCatPlan(merchCatPlanSet, lvl3.getLvl3Nbr(), channelId);
                if (merchCatPlan != null) {
                    merchCatPlan.getSubCatPlans().removeIf(subCatPlan -> CollectionUtils.isEmpty(subCatPlan.getFinelinePlans()) && subCatPlan.getSubCatPlanId().getLvl4Nbr().equals(lvl4.getLvl4Nbr()) &&
                            !subCatPlan.getSubCatPlanId().getMerchCatPlanId().getChannelId().equals(channelId));
                }
            }
        });
    }

    private void deleteFinelinePlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Lvl4 lvl4) {
        lvl4.getFinelines().forEach(fineline -> {
            deleteStylePlan(merchCatPlanSet, lvl3, lvl4, fineline);
            if (!ChannelType.getChannelIdFromName(fineline.getChannel()).equals(3)) {
                SubCatPlan subCatPlan = fetchMerchSubCatPlan(merchCatPlanSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), ChannelType.getChannelIdFromName(fineline.getChannel()));
                if (subCatPlan != null) {
                    subCatPlan.getFinelinePlans().removeIf(finelinePlan -> CollectionUtils.isEmpty(finelinePlan.getStylePlans()) && finelinePlan.getFinelinePlanId().getFinelineNbr().equals(fineline.getFinelineNbr()) &&
                            !finelinePlan.getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getChannelId().equals(ChannelType.getChannelIdFromName(fineline.getChannel())));
                }
            }
        });
    }

    private void deleteStylePlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline) {
        fineline.getStyles().forEach(style -> {
            deleteCCPlan(merchCatPlanSet, lvl3, lvl4, fineline, style);
            if (!ChannelType.getChannelIdFromName(style.getChannel()).equals(3)) {
                FinelinePlan finelinePlan = fetchFinelinePlan(merchCatPlanSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), ChannelType.getChannelIdFromName(style.getChannel()));
                if (finelinePlan != null) {
                    finelinePlan.getStylePlans().removeIf(stylePlan -> CollectionUtils.isEmpty(stylePlan.getCustChoicePlans()) && stylePlan.getStylePlanId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr()) &&
                            !stylePlan.getStylePlanId().getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId().getChannelId().equals(ChannelType.getChannelIdFromName(style.getChannel())));
                }
            }
        });
    }

    private void deleteCCPlan(Set<MerchCatPlan> merchCatPlanSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
        style.getCustomerChoices().forEach(customerChoice -> {
            if (!ChannelType.getChannelIdFromName(customerChoice.getChannel()).equals(3)) {
                StylePlan stylePlan = fetchStylePlan(merchCatPlanSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr(), ChannelType.getChannelIdFromName(customerChoice.getChannel()));
                if (stylePlan != null) {
                    stylePlan.getCustChoicePlans().removeIf(custChoicePlan -> custChoicePlan.getCustChoicePlanId().getCcId().equalsIgnoreCase(customerChoice.getCcId()) &&
                            !custChoicePlan.getCustChoicePlanId().getStylePlanId().getFinelinePlanId().getSubCatPlanId().getMerchCatPlanId()
                                    .getChannelId().equals(ChannelType.getChannelIdFromName(customerChoice.getChannel())));
                }
            }
        });
    }

    private MerchCatPlan fetchMerchCatPlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer channelId) {
        return Optional.ofNullable(merchCatPlanSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatPlan -> merchCatPlan.getMerchCatPlanId().getLvl3Nbr().equals(lvl3Nbr) &&
                        !merchCatPlan.getMerchCatPlanId().getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    private SubCatPlan fetchMerchSubCatPlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer channelId) {
        return Optional.ofNullable(merchCatPlanSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatPlan -> merchCatPlan.getMerchCatPlanId().getLvl3Nbr().equals(lvl3Nbr) &&
                        !merchCatPlan.getMerchCatPlanId().getChannelId().equals(channelId))
                .findFirst()
                .map(MerchCatPlan::getSubCatPlans)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchSubCatPlan -> merchSubCatPlan.getSubCatPlanId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .orElse(null);
    }

    private FinelinePlan fetchFinelinePlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, Integer channelId) {
        return Optional.ofNullable(merchCatPlanSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatPlan -> merchCatPlan.getMerchCatPlanId().getLvl3Nbr().equals(lvl3Nbr) &&
                        !merchCatPlan.getMerchCatPlanId().getChannelId().equals(channelId))
                .findFirst()
                .map(MerchCatPlan::getSubCatPlans)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatPlan -> subCatPlan.getSubCatPlanId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(SubCatPlan::getFinelinePlans)
                .stream()
                .flatMap(Collection::stream)
                .filter(finelinePlan -> finelinePlan.getFinelinePlanId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .orElse(null);
    }

    private StylePlan fetchStylePlan(Set<MerchCatPlan> merchCatPlanSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum, Integer channelId) {
        return Optional.ofNullable(merchCatPlanSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatPlan -> merchCatPlan.getMerchCatPlanId().getLvl3Nbr().equals(lvl3Nbr) &&
                        !merchCatPlan.getMerchCatPlanId().getChannelId().equals(channelId))
                .findFirst()
                .map(MerchCatPlan::getSubCatPlans)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatPlan -> subCatPlan.getSubCatPlanId().getLvl4Nbr().equals(lvl4Nbr))
                .findFirst()
                .map(SubCatPlan::getFinelinePlans)
                .stream()
                .flatMap(Collection::stream)
                .filter(finelinePlan -> finelinePlan.getFinelinePlanId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .map(FinelinePlan::getStylePlans)
                .stream()
                .flatMap(Collection::stream)
                .filter(stylePlan -> stylePlan.getStylePlanId().getStyleNbr().equals(styleNum))
                .findFirst()
                .orElse(null);
    }



}
