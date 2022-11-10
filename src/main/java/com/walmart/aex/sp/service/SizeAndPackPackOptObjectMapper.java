package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SizeAndPackPackOptObjectMapper {

    private final MerchPackOptimizationRepository merchPackOptimizationRepository;

    private final SizeAndPackObjectMapper sizeAndPackObjectMapper;

    public SizeAndPackPackOptObjectMapper(MerchPackOptimizationRepository merchPackOptimizationRepository, SizeAndPackObjectMapper sizeAndPackObjectMapper) {
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.sizeAndPackObjectMapper = sizeAndPackObjectMapper;
    }

    public Set<MerchantPackOptimization> setMerchCatPackOpt(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3, MerchPackOptimizationRepository merchPackOptimizationRepository) {
        Set<MerchantPackOptimization> merchCatPackOptSet = new HashSet<>();
        Integer requestChannel = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(lvl3));
        List<Integer> channelList = sizeAndPackObjectMapper.getChannelListFromChannelId(requestChannel);

        if (!CollectionUtils.isEmpty(channelList)) {
            channelList.forEach(channel -> {
                MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(),channel);
                MerchantPackOptimization merchantPackOptimization = merchPackOptimizationRepository.findById(merchantPackOptimizationID).orElse(new MerchantPackOptimization());
                if (merchantPackOptimization.getMerchantPackOptimizationID() == null) {
                    merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);

                }
                //Other values has to be updated later after defining proper contract
                merchantPackOptimization.setMaxNbrOfPacks(50);
                merchantPackOptimization.setMaxUnitsPerPack(36);
                merchantPackOptimization.setSinglePackInd(1);

                if (!CollectionUtils.isEmpty(lvl3.getLvl4List())) {
                    merchantPackOptimization.setSubCatgPackOptimization(setSubCatPackOpt(merchantPackOptimization, lvl3.getLvl4List()));
                }
                merchCatPackOptSet.add(merchantPackOptimization);
            });
        }
        return merchCatPackOptSet;
    }

    public Set<SubCatgPackOptimization> setSubCatPackOpt(MerchantPackOptimization merchantPackOptimization, List<Lvl4> lvl4s) {
        Set<SubCatgPackOptimization> subCatgPackOptimizationSet = Optional.ofNullable(merchantPackOptimization.getSubCatgPackOptimization())
                .orElse(new HashSet<>());
        for (Lvl4 lvl4 : lvl4s) {
            SubCatgPackOptimizationID subCatgPackOptimizationID = new SubCatgPackOptimizationID(merchantPackOptimization.getMerchantPackOptimizationID(), lvl4.getLvl4Nbr());
            SubCatgPackOptimization subCatgPackOptimization = Optional.of(subCatgPackOptimizationSet)
                    .stream()
                    .flatMap(Collection::stream).filter(subCatgPackOptimization1 -> subCatgPackOptimization1.getSubCatgPackOptimizationID().equals(subCatgPackOptimizationID))
                    .findFirst()
                    .orElse(new SubCatgPackOptimization());
            if (subCatgPackOptimization.getSubCatgPackOptimizationID() == null) {
                subCatgPackOptimization.setSubCatgPackOptimizationID(subCatgPackOptimizationID);
            }

            //Other values has to be updated later after defining proper contract
            subCatgPackOptimization.setMaxNbrOfPacks(50);
            subCatgPackOptimization.setMaxUnitsPerPack(36);
            subCatgPackOptimization.setSinglePackInd(1);


            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                subCatgPackOptimization.setFinelinepackOptimization(setFinelinePackOpt(subCatgPackOptimization, lvl4.getFinelines()));
            }
            subCatgPackOptimizationSet.add(subCatgPackOptimization);
        }
        return subCatgPackOptimizationSet;
    }

    public Set<FineLinePackOptimization> setFinelinePackOpt(SubCatgPackOptimization subCatgPackOptimization, List<Fineline> finelines) {
        Set<FineLinePackOptimization> fineLinePackOptimizationSet = Optional.ofNullable(subCatgPackOptimization.getFinelinepackOptimization())
                .orElse(new HashSet<>());
        for (Fineline fineline : finelines) {
            FineLinePackOptimizationID fineLinePackOptimizationID = new FineLinePackOptimizationID(subCatgPackOptimization.getSubCatgPackOptimizationID(), fineline.getFinelineNbr());
            FineLinePackOptimization fineLinePackOptimization = Optional.of(fineLinePackOptimizationSet)
                    .stream()
                    .flatMap(Collection::stream).filter(fineLinePackOptimization1 -> fineLinePackOptimization1.getFinelinePackOptId().equals(fineLinePackOptimizationID))
                    .findFirst()
                    .orElse(new FineLinePackOptimization());
            if (fineLinePackOptimization.getFinelinePackOptId() == null) {
                fineLinePackOptimization.setFinelinePackOptId(fineLinePackOptimizationID);
            }

            //Other values has to be updated later after defining proper contract
            fineLinePackOptimization.setMaxNbrOfPacks(50);
            fineLinePackOptimization.setMaxUnitsPerPack(36);
            fineLinePackOptimization.setSinglePackInd(1);


            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                fineLinePackOptimization.setStylePackOptimization(setStylesPackOpt(fineLinePackOptimization, fineline.getStyles()));
            }
            fineLinePackOptimizationSet.add(fineLinePackOptimization);
        }
        return fineLinePackOptimizationSet;
    }

    public Set<StylePackOptimization> setStylesPackOpt(FineLinePackOptimization fineLinePackOptimization, List<Style> styles) {
        Set<StylePackOptimization> stylePackOptimizationSet = Optional.ofNullable(fineLinePackOptimization.getStylePackOptimization())
                .orElse(new HashSet<>());
        for (Style style : styles) {

            StylePackOptimizationID stylePackOptimizationID = new StylePackOptimizationID(fineLinePackOptimization.getFinelinePackOptId(), style.getStyleNbr());
            StylePackOptimization stylePackOptimization = Optional.of(stylePackOptimizationSet)
                    .stream()
                    .flatMap(Collection::stream).filter(stylePackOptimization1 -> stylePackOptimization1.getStylePackoptimizationId().equals(stylePackOptimizationID))
                    .findFirst()
                    .orElse(new StylePackOptimization());
            if (stylePackOptimization.getStylePackoptimizationId() == null) {
                stylePackOptimization.setStylePackoptimizationId(stylePackOptimizationID);
            }

            //Other values has to be updated later after defining proper contract
            stylePackOptimization.setMaxNbrOfPacks(50);
            stylePackOptimization.setMaxUnitsPerPack(36);
            stylePackOptimization.setSinglePackInd(1);

            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                stylePackOptimization.setCcPackOptimization(setCustChoicePackOpt(stylePackOptimization, style.getCustomerChoices()));
            }
            stylePackOptimizationSet.add(stylePackOptimization);

        }
        return stylePackOptimizationSet;
    }

    public Set<CcPackOptimization> setCustChoicePackOpt(StylePackOptimization stylePackOptimization, List<CustomerChoice> customerChoices) {
        Set<CcPackOptimization> ccPackOptimizationSet = Optional.ofNullable(stylePackOptimization.getCcPackOptimization())
                .orElse(new HashSet<>());
        for (CustomerChoice customerChoice : customerChoices) {

            CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID(stylePackOptimization.getStylePackoptimizationId(), customerChoice.getCcId());
            CcPackOptimization ccPackOptimization = Optional.of(ccPackOptimizationSet)
                    .stream()
                    .flatMap(Collection::stream).filter(ccPackOptimization1  -> ccPackOptimization1.getCcPackOptimizationId().equals(ccPackOptimizationID))
                    .findFirst()
                    .orElse(new CcPackOptimization());
            if (ccPackOptimization.getCcPackOptimizationId() == null) {
                ccPackOptimization.setCcPackOptimizationId(ccPackOptimizationID);
            }

            //Other values has to be updated later after defining proper contract
            ccPackOptimization.setMaxNbrOfPacks(50);
            ccPackOptimization.setMaxUnitsPerPack(36);
            ccPackOptimization.setSinglePackInd(1);

            ccPackOptimizationSet.add(ccPackOptimization);

        }
        return ccPackOptimizationSet;
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
        return setMerchCatPackOpt(request,lvl1, lvl2, lvl3,merchPackOptimizationRepository);
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
                            !stylePackOptimization.getStylePackoptimizationId().getFinelinePackOptimizationID().getSubCatgPackOptimizationID().getMerchantPackOptimizationID().getChannelId().equals(ChannelType.getChannelIdFromName(style.getChannel())));
                }
            }
        });
    }

    private void deleteCCPackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Lvl3 lvl3, Lvl4 lvl4, Fineline fineline, Style style) {
        style.getCustomerChoices().forEach(customerChoice -> {
            if (!ChannelType.getChannelIdFromName(customerChoice.getChannel()).equals(3)) {
                StylePackOptimization stylePackOptimization = fetchStylePackOpt(merchantPackOptimizationSet, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fineline.getFinelineNbr(), style.getStyleNbr(), ChannelType.getChannelIdFromName(customerChoice.getChannel()));
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
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchantPackOptimization -> merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3Nbr) &&
                        !merchantPackOptimization.getMerchantPackOptimizationID().getChannelId().equals(channelId))
                .findFirst()
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatgPackOptimization -> subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4Nbr))
                .findFirst()
                .orElse(null);
    }

    private FineLinePackOptimization fetchFinelinePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, Integer channelId) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchantPackOptimization -> merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3Nbr) &&
                        !merchantPackOptimization.getMerchantPackOptimizationID().getChannelId().equals(channelId))
                .findFirst()
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatgPackOptimization -> subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4Nbr))
                .findFirst()
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .stream()
                .flatMap(Collection::stream)
                .filter(fineLinePackOptimization -> fineLinePackOptimization.getFinelinePackOptId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .orElse(null);
    }

    private StylePackOptimization fetchStylePackOpt(Set<MerchantPackOptimization> merchantPackOptimizationSet, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNum, Integer channelId) {
        return Optional.ofNullable(merchantPackOptimizationSet)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchantPackOptimization -> merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3Nbr) &&
                        !merchantPackOptimization.getMerchantPackOptimizationID().getChannelId().equals(channelId))
                .findFirst()
                .map(MerchantPackOptimization::getSubCatgPackOptimization)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatgPackOptimization -> subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4Nbr))
                .findFirst()
                .map(SubCatgPackOptimization::getFinelinepackOptimization)
                .stream()
                .flatMap(Collection::stream)
                .filter(fineLinePackOptimization -> fineLinePackOptimization.getFinelinePackOptId().getFinelineNbr().equals(finelineNbr))
                .findFirst()
                .map(FineLinePackOptimization::getStylePackOptimization)
                .stream()
                .flatMap(Collection::stream)
                .filter(stylePackOptimization -> stylePackOptimization.getStylePackoptimizationId().getStyleNbr().equals(styleNum))
                .findFirst()
                .orElse(null);
    }
}
