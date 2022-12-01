package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.Constraints;
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

@Service
@Slf4j
public class PackOptAddDataMapper {


    private final SizeAndPackObjectMapper sizeAndPackObjectMapper;

    public PackOptAddDataMapper(SizeAndPackObjectMapper sizeAndPackObjectMapper) {
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

            if (customerChoice.getConstraints() != null) {
                Constraints constraints = customerChoice.getConstraints();
                if (constraints.getColorCombinationConstraints() != null) {
                    ccPackOptimization.setOriginCountryName(constraints.getColorCombinationConstraints().getCountryOfOrigin());
                    ccPackOptimization.setVendorName(constraints.getColorCombinationConstraints().getSupplier().getSupplierName());
                    ccPackOptimization.setVendorNbr6(constraints.getColorCombinationConstraints().getSupplier().getSupplierNumber());
                    ccPackOptimization.setVendorNbr9(constraints.getColorCombinationConstraints().getSupplier().getSupplierId());
                    ccPackOptimization.setGsmSupplierId(constraints.getColorCombinationConstraints().getSupplier().getSupplier8Number());
                }
                if (constraints.getFinelineLevelConstraints() != null) {
                    ccPackOptimization.setMaxNbrOfPacks(constraints.getFinelineLevelConstraints().getMaxPacks());
                    ccPackOptimization.setMaxUnitsPerPack(constraints.getFinelineLevelConstraints().getMaxUnitsPerPack());
                }
            }

            ccPackOptimizationSet.add(ccPackOptimization);

        }
        return ccPackOptimizationSet;
    }
}
