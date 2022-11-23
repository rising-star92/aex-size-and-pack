package com.walmart.aex.sp.service;

import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackOptUpdateDataMapper {

    private final SizeAndPackObjectMapper sizeAndPackObjectMapper;
    private final PackOptAddDataMapper packOptAddDataMapper;

    public PackOptUpdateDataMapper(SizeAndPackObjectMapper sizeAndPackObjectMapper, PackOptAddDataMapper packOptAddDataMapper) {
        this.sizeAndPackObjectMapper = sizeAndPackObjectMapper;
        this.packOptAddDataMapper = packOptAddDataMapper;
    }

    public Set<MerchantPackOptimization> updateMerchCatPackOpt(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, MerchPackOptimizationRepository merchPackOptimizationRepository) {
        Integer requestChannel = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(lvl3));
        List<Integer> channelList = sizeAndPackObjectMapper.getChannelListFromChannelId(requestChannel);

        List<MerchantPackOptimization> merchantPackOptimizationList = merchPackOptimizationRepository.findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl0AndMerchantPackOptimizationID_repTLvl1AndMerchantPackOptimizationID_repTLvl2AndMerchantPackOptimizationID_repTLvl3(request.getPlanId(),
                request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr());

        Set<MerchantPackOptimization> merchantPackOptimizationSet = merchantPackOptimizationList.stream().collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(channelList)) {
            channelList.forEach(channelId -> {
                if (!CollectionUtils.isEmpty(merchantPackOptimizationSet)) {
                    deleteMerchCatPackOpt(merchantPackOptimizationSet, lvl3, channelId, merchPackOptimizationRepository);
                }
            });
        }
        return packOptAddDataMapper.setMerchCatPackOpt(request,lvl1, lvl2, lvl3,merchPackOptimizationRepository);
    }

    private Set<MerchantPackOptimization> deleteMerchCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Integer channelId, MerchPackOptimizationRepository merchPackOptimizationRepository) {
        deleteMerchSubCatPackOpt(merchantPackOptimizationSet, lvl3, channelId);
        if (!channelId.equals(3)) {
            MerchantPackOptimization merchantPackOptimization = fetchMerchCatPackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), channelId);
            if(merchantPackOptimization != null) {
                if (CollectionUtils.isEmpty(merchantPackOptimization.getSubCatgPackOptimization())) {
                    //Only removing from set is not deleting the entry in the DB. Hence deleting by the ID
                    merchPackOptimizationRepository.deleteById(merchantPackOptimization.getMerchantPackOptimizationID());
                }
            }
            merchantPackOptimizationSet.removeIf(merchantPackOptimization1 -> CollectionUtils.isEmpty(merchantPackOptimization1.getSubCatgPackOptimization()) && merchantPackOptimization1.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3.getLvl3Nbr()) &&
                    !merchantPackOptimization.getMerchantPackOptimizationID().getChannelId().equals(channelId));
        }
        return merchantPackOptimizationSet;
    }

    private void deleteMerchSubCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Integer channelId) {
        lvl3.getLvl4List().forEach(lvl4 -> {
            deleteFinelinePackOpt(merchantPackOptimizationSet, lvl3, lvl4);
            if (!channelId.equals(3)) {
                MerchantPackOptimization merchantPackOptimization = fetchMerchCatPackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), channelId);
                if (merchantPackOptimization != null) {
                    merchantPackOptimization.getSubCatgPackOptimization().removeIf(subCatgPackOptimization -> CollectionUtils.isEmpty(subCatgPackOptimization.getFinelinepackOptimization()) && subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4.getLvl4Nbr()) &&
                            !subCatgPackOptimization.getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(channelId));
                }
            }
        });
    }

    private void deleteFinelinePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Lvl4 lvl4) {
        lvl4.getFinelines().forEach(fineline -> {
            deleteStylePackOpt(merchantPackOptimizationSet, lvl3, lvl4, fineline);
            if (!ChannelType.getChannelIdFromName(fineline.getChannel()).equals(3)) {
                SubCatgPackOptimization subCatgPackOptimization = fetchMerchSubCatPackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), ChannelType.getChannelIdFromName(fineline.getChannel()));
                if (subCatgPackOptimization != null) {
                    subCatgPackOptimization.getFinelinepackOptimization().removeIf(fineLinePackOptimization -> CollectionUtils.isEmpty(fineLinePackOptimization.getStylePackOptimization()) && fineLinePackOptimization.getFinelinePackOptId().getFinelineNbr().equals(fineline.getFinelineNbr()) &&
                            !fineLinePackOptimization.getFinelinePackOptId().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(ChannelType.getChannelIdFromName(fineline.getChannel())));
                }
            }
        });
    }

    private void deleteStylePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline) {
        fineline.getStyles().forEach(style -> {
            deleteCCPackOpt(merchantPackOptimizationSet, lvl3, lvl4, fineline, style);
            if (!ChannelType.getChannelIdFromName(style.getChannel()).equals(3)) {
                FineLinePackOptimization fineLinePackOptimization = fetchFinelinePackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), ChannelType.getChannelIdFromName(style.getChannel()));
                if (fineLinePackOptimization != null) {
                    fineLinePackOptimization.getStylePackOptimization().removeIf(stylePackOptimization -> CollectionUtils.isEmpty(stylePackOptimization.getCcPackOptimization()) && stylePackOptimization.getStylePackoptimizationId().getStyleNbr().equalsIgnoreCase(style.getStyleNbr()) &&
                            !stylePackOptimization.getStylePackoptimizationId().getFinelinePackOptimizationID().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(ChannelType.getChannelIdFromName(fineline.getChannel())));
                }
            }
        });
    }

    private void deleteCCPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
        style.getCustomerChoices().forEach(customerChoice -> {
            if (!ChannelType.getChannelIdFromName(customerChoice.getChannel()).equals(3)) {
                StylePackOptimization stylePackOptimization = fetchStylePackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr(), ChannelType.getChannelIdFromName(fineline.getChannel()));
                if (stylePackOptimization != null) {
                    stylePackOptimization.getCcPackOptimization().removeIf(ccPackOptimization -> ccPackOptimization.getCcPackOptimizationId().getCustomerChoice().equalsIgnoreCase(customerChoice.getCcId()) &&
                            !ccPackOptimization.getCcPackOptimizationId().getStylePackOptimizationID().getFinelinePackOptimizationID().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(ChannelType.getChannelIdFromName(customerChoice.getChannel())));
                }
            }
        });
    }

    private MerchantPackOptimization fetchMerchCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer channelId) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchantPackOptimization -> merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3Nbr) &&
                        !merchantPackOptimization.getMerchantPackOptimizationID().getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    private SubCatgPackOptimization fetchMerchSubCatPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer channelId) {
        return Optional.ofNullable(fetchMerchCatPackOpt(merchantPackOptimizationSet,lvl3Nbr,channelId))
                .stream()
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .flatMap(Collection::stream)
                .filter(subCatgPackOptimization -> subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4Nbr))
                .findFirst()
                .orElse(null);
    }

    private FineLinePackOptimization fetchFinelinePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, Integer channelId) {
        return Optional.ofNullable(fetchMerchSubCatPackOpt(merchantPackOptimizationSet,lvl3Nbr,lvl4Nbr,channelId))
                .stream()
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .flatMap(Collection::stream)
                .filter(fineLinePackOptimization -> fineLinePackOptimization.getFinelinePackOptId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .orElse(null);
    }

    private StylePackOptimization fetchStylePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum, Integer channelId) {
        return Optional.ofNullable(fetchFinelinePackOpt(merchantPackOptimizationSet,lvl3Nbr,lvl4Nbr,finelineNbr,channelId))
                .stream()
                .map(FineLinePackOptimization::getStylePackOptimization)
                .flatMap(Collection::stream)
                .filter(stylePackOptimization -> stylePackOptimization.getStylePackoptimizationId().getStyleNbr().equals(styleNum))
                .findFirst()
                .orElse(null);
    }

}

