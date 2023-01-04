package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.*;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.repository.MerchPackOptimizationRepository;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Slf4j
public class PackOptAddDataMapper {


    private final SizeAndPackObjectMapper sizeAndPackObjectMapper;
    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private static final Integer DEFAULT_SINGLE_PACK_INDICATOR = 0;

    public PackOptAddDataMapper(SizeAndPackObjectMapper sizeAndPackObjectMapper, MerchPackOptimizationRepository merchPackOptimizationRepository) {
        this.sizeAndPackObjectMapper = sizeAndPackObjectMapper;
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
    }

    public Set<MerchantPackOptimization> setMerchCatPackOpt(PlanSizeAndPackDTO request, Lvl1 lvl1, Lvl2 lvl2, Lvl3 lvl3) {
        Set<MerchantPackOptimization> merchCatPackOptSet = new HashSet<>();
        Integer requestChannel = ChannelType.getChannelIdFromName(CommonUtil.getRequestedFlChannel(lvl3));
        List<Integer> channelList = sizeAndPackObjectMapper.getChannelListFromChannelId(requestChannel);

        if (!CollectionUtils.isEmpty(channelList)) {
            channelList.forEach(channel -> {
                MerchantPackOptimizationID merchantPackOptimizationID = new MerchantPackOptimizationID(request.getPlanId(), request.getLvl0Nbr(), lvl1.getLvl1Nbr(), lvl2.getLvl2Nbr(), lvl3.getLvl3Nbr(), channel);
                MerchantPackOptimization merchantPackOptimization = merchPackOptimizationRepository.findById(merchantPackOptimizationID).orElse(new MerchantPackOptimization());
                if (merchantPackOptimization.getMerchantPackOptimizationID() == null) {
                    merchantPackOptimization.setMerchantPackOptimizationID(merchantPackOptimizationID);

                }
                //Other values has to be updated later after defining proper contract
                merchantPackOptimization.setMaxNbrOfPacks(50);
                merchantPackOptimization.setMaxUnitsPerPack(36);
                merchantPackOptimization.setSinglePackInd(DEFAULT_SINGLE_PACK_INDICATOR);

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
            subCatgPackOptimization.setSinglePackInd(DEFAULT_SINGLE_PACK_INDICATOR);


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
            fineLinePackOptimization.setSinglePackInd(DEFAULT_SINGLE_PACK_INDICATOR);


            if (!CollectionUtils.isEmpty(fineline.getStyles())) {
                Set<String> colorCombinationSets = new HashSet<>();
                fineLinePackOptimization.setStylePackOptimization(setStylesPackOpt(fineLinePackOptimization, fineline.getStyles(), colorCombinationSets));
                /**Reset the color combination if there is change in Country of Origin or Supplier Name */
                resetColorCombination(colorCombinationSets, fineLinePackOptimization);
            }
            fineLinePackOptimizationSet.add(fineLinePackOptimization);
        }
        return fineLinePackOptimizationSet;
    }

    public Set<StylePackOptimization> setStylesPackOpt(FineLinePackOptimization fineLinePackOptimization, List<Style> styles, Set<String> colorCombinationSets) {
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
            stylePackOptimization.setSinglePackInd(DEFAULT_SINGLE_PACK_INDICATOR);

            if (!CollectionUtils.isEmpty(style.getCustomerChoices())) {
                stylePackOptimization.setCcPackOptimization(setCustChoicePackOpt(stylePackOptimization, style.getCustomerChoices(), colorCombinationSets));
            }
            stylePackOptimizationSet.add(stylePackOptimization);

        }
        return stylePackOptimizationSet;
    }

    public Set<CcPackOptimization> setCustChoicePackOpt(StylePackOptimization stylePackOptimization, List<CustomerChoice> customerChoices, Set<String> colorCombinationSets) {
        Set<CcPackOptimization> ccPackOptimizationSet = Optional.ofNullable(stylePackOptimization.getCcPackOptimization())
                .orElse(new HashSet<>());
        for (CustomerChoice customerChoice : customerChoices) {

            CcPackOptimizationID ccPackOptimizationID = new CcPackOptimizationID(stylePackOptimization.getStylePackoptimizationId(), customerChoice.getCcId());
            CcPackOptimization ccPackOptimization = Optional.of(ccPackOptimizationSet)
                    .stream()
                    .flatMap(Collection::stream).filter(ccPackOptimization1 -> ccPackOptimization1.getCcPackOptimizationId().equals(ccPackOptimizationID))
                    .findFirst()
                    .orElse(new CcPackOptimization());
            if (ccPackOptimization.getCcPackOptimizationId() == null) {
                ccPackOptimization.setCcPackOptimizationId(ccPackOptimizationID);
            }

            //Other values has to be updated later after defining proper contract
            ccPackOptimization.setMaxNbrOfPacks(50);
            ccPackOptimization.setMaxUnitsPerPack(36);
            ccPackOptimization.setSinglePackInd(DEFAULT_SINGLE_PACK_INDICATOR);

            if (customerChoice.getConstraints() != null) {
                Constraints constraints = customerChoice.getConstraints();
                updateCcColorCombinationConstraint(ccPackOptimization, constraints, colorCombinationSets);
            }
            ccPackOptimizationSet.add(ccPackOptimization);
        }
        return ccPackOptimizationSet;
    }

    private void updateCcColorCombinationConstraint(CcPackOptimization ccPackOptimization, Constraints constraints, Set<String> colorCombinationSets) {
        ColorCombinationConstraints colorCombinationConstraints = constraints.getColorCombinationConstraints();
        if (colorCombinationConstraints != null) {
            String countryOfOrigin = colorCombinationConstraints.getCountryOfOrigin();
            String countryOfOriginFromDB = ccPackOptimization.getOriginCountryName();
            ccPackOptimization.setOriginCountryName(countryOfOrigin);
            /**Comparing Country of origin. If there is a change in country of origin then reset all color combination constraints (Supplier, Factory, Color Combination and Port of Origin)*/
            if (!StringUtils.isEmpty(countryOfOrigin) && !StringUtils.isEmpty(countryOfOriginFromDB) && !countryOfOrigin.equalsIgnoreCase(countryOfOriginFromDB)) {
                if (!StringUtils.isEmpty(ccPackOptimization.getColorCombination())) {
                    colorCombinationSets.add(ccPackOptimization.getColorCombination());
                }
            } else {
                setCcSupplierConstraints(colorCombinationConstraints, ccPackOptimization, colorCombinationSets);
            }
        }
        if (constraints.getFinelineLevelConstraints() != null) {
            ccPackOptimization.setMaxNbrOfPacks(constraints.getFinelineLevelConstraints().getMaxPacks());
            ccPackOptimization.setMaxUnitsPerPack(constraints.getFinelineLevelConstraints().getMaxUnitsPerPack());
        }
    }

    private void setCcSupplierConstraints(ColorCombinationConstraints colorCombinationConstraints, CcPackOptimization ccPackOptimization, Set<String> colorCombinationSets) {
        List<Supplier> suppliers = colorCombinationConstraints.getSuppliers();
        if (!CollectionUtils.isEmpty(suppliers)) {
            String supplierName = StringEscapeUtils.unescapeHtml(suppliers.get(0).getSupplierName());
            /**Comparing Supplier Name. If there is a change in supplier name then reset all color combination constraints (Country of origin, Factory, Color Combination and Port of Origin)*/
            if (!StringUtils.isEmpty(supplierName) && !StringUtils.isEmpty(ccPackOptimization.getVendorName()) && !supplierName.equalsIgnoreCase(ccPackOptimization.getVendorName())) {
                ccPackOptimization.setOriginCountryName(null);
                ccPackOptimization.setFactoryId(null);
                ccPackOptimization.setFactoryName(null);
                ccPackOptimization.setPortOfOriginName(null);
                if (!StringUtils.isEmpty(ccPackOptimization.getColorCombination())) {
                    colorCombinationSets.add(ccPackOptimization.getColorCombination());
                }
            }
            ccPackOptimization.setVendorName(supplierName);
            ccPackOptimization.setVendorNbr6(suppliers.get(0).getSupplierId());
            ccPackOptimization.setVendorNbr9(suppliers.get(0).getSupplierNumber());
            ccPackOptimization.setGsmSupplierId(suppliers.get(0).getSupplier8Number());
        }
    }

    private void resetColorCombination(Set<String> colorCombinationSets, FineLinePackOptimization fineLinePackOptimization) {
        if (!CollectionUtils.isEmpty(colorCombinationSets)) {
            for (StylePackOptimization stylePackOptimization : fineLinePackOptimization.getStylePackOptimization()) {
                for (CcPackOptimization ccPackOptimization : stylePackOptimization.getCcPackOptimization()) {
                    if (colorCombinationSets.contains(ccPackOptimization.getColorCombination())) {
                        ccPackOptimization.setColorCombination(null);
                    }
                }
            }
        }
    }
}
