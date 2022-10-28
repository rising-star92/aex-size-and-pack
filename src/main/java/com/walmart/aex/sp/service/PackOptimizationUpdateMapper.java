package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintUpdateRequestDTO;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.entity.SubCatgPackOptimizationID;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PackOptimizationUpdateMapper {

    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private final SubCatgPackOptimizationRepository subCatgPackOptimizationRepository;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final StylePackOptimizationRepository stylePackOptimizationRepository;

    public PackOptimizationUpdateMapper(MerchPackOptimizationRepository merchPackOptimizationRepository, SubCatgPackOptimizationRepository subCatgPackOptimizationRepository, StylePackOptimizationRepository stylePackOptimizationRepository,
                                        CcPackOptimizationRepository ccPackOptimizationRepository) {
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.subCatgPackOptimizationRepository = subCatgPackOptimizationRepository;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.stylePackOptimizationRepository = stylePackOptimizationRepository;
    }

    public List<MerchantPackOptimization> updateCategoryPackOptCons(Integer channelId, PackOptConstraintUpdateRequestDTO request) {
        List<MerchantPackOptimization> merchantPackOptimizationList = merchPackOptimizationRepository.findMerchantPackOptimizationByMerchantPackOptimizationID_planIdAndMerchantPackOptimizationID_repTLvl3(request.getPlanId(), request.getLvl3Nbr())
                .orElseThrow(() -> new CustomException(String.format("Size Cluster doesn't exists for the PlanId :%s, StrategyId: %s  & lvl3Nbr : %s provided",
                        request.getPlanId(), request.getLvl3Nbr())));

        Optional.ofNullable(merchantPackOptimizationList).ifPresent(
                merchantPackOpts -> {
                    merchantPackOpts.forEach(mc -> {
                        if (request.getLvl4Nbr() == null) {
                            try {
                                updateMerchCatgPackOptConstWithNewInput(request, mc);
                            } catch (Exception e) {
                                throw new CustomException("Failed while updating category size profile");
                            }
                        }
                        updateSubCategoryPackOptCons(request, mc, channelId);
                    });
                }
        );

        return merchantPackOptimizationList;
    }

    private void updateSubCategoryPackOptCons(PackOptConstraintUpdateRequestDTO request, MerchantPackOptimization merchantPackOpts, Integer channelId) {
        for (SubCatgPackOptimization lvl4 : merchantPackOpts.getSubCatgPackOptimization()) {
            Optional.ofNullable(lvl4.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategySubCategorySPCluster strategySubCategorySPCluster = fetchStrategySubCatgSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr());
                            updateSubCatFields(clusterSpUpdatedFields, strategySubCategorySPCluster);
                            rollupCatgFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3.getLvl3Nbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Sub category size profile");
                        }
                    }
            );
            if (!CollectionUtils.isEmpty(lvl4.getFinelines())) {
                updateFinelineSizes(lvl3, strategyMerchCategorySPClusters, lvl4);
            }
        }
    }


    private Set<SubCatgPackOptimization> updateSubCatgPackOptCons(PackOptConstraintUpdateRequestDTO request, Optional<MerchantPackOptimization> merchantPackOptimization) {
        Set<SubCatgPackOptimization> subCatgPackOptimizationSet = merchantPackOptimization.map(MerchantPackOptimization::getSubCatgPackOptimization)
                .orElse(new HashSet<>());
        if(request.getFinelineNbr()==null && request.getStyleNbr()==null && request.getCcId()==null){
''
            subCatgPackOptimizationSet.forEach(x->{
                updateSubCatgPackOptConstWithNewInput(request, x);

            });

        }else{

        }


        return subCatgPackOptimizationSet;
    }
    private void updateMerchCatgPackOptConstWithNewInput(PackOptConstraintUpdateRequestDTO request, MerchantPackOptimization merchantPackOpt) {
            if (request.getColorCombination() != null && !request.getColorCombination().isEmpty())
                merchantPackOpt.setColorCombination(request.getColorCombination());
            else if (request.getMaxNbrOfPacks() != null)
                merchantPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks());
            else if (request.getMaxUnitsPerPack() != null)
                merchantPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack());
            else if (request.getPortOfOriginId() != null)
                merchantPackOpt.setPortOfOriginId(request.getPortOfOriginId());
            else if (request.getPortOfOriginName() != null && request.getPortOfOriginName().isEmpty())
                merchantPackOpt.setPortOfOriginName(request.getPortOfOriginName());
            else if (request.getSinglePackInd() != null)
                merchantPackOpt.setSinglePackInd(request.getSinglePackInd());
            else if (request.getFactoryName() != null && !request.getFactoryName().isEmpty())
                merchantPackOpt.setFactoryName(request.getFactoryName());
            else if (request.getFactoryId() != null)
                merchantPackOpt.setFactoryId(request.getFactoryId());
            else if (request.getOriginCountryCode() != null && !request.getOriginCountryCode().isEmpty())
                merchantPackOpt.setOriginCountryCode(request.getOriginCountryCode());
            else if (request.getOriginCountryName() != null && !request.getOriginCountryName().isEmpty())
                merchantPackOpt.setOriginCountryName(request.getOriginCountryName());
            else if (request.getVendorName() != null && !request.getVendorName().isEmpty())
                merchantPackOpt.setVendorName(request.getVendorName());
            else if (request.getVendorNbr6() != null)
                merchantPackOpt.setVendorNbr6(request.getVendorNbr6());
            else if (request.getVendorNbr9() != null)
                merchantPackOpt.setVendorNbr9(request.getVendorNbr9());


    }

    private void updateSubCatgPackOptConstWithNewInput(PackOptConstraintUpdateRequestDTO request,Optional<SubCatgPackOptimization> merchantPackOptimization) {
        merchantPackOptimization.ifPresent(merchantPackOpt -> {
            if (request.getColorCombination() != null && !request.getColorCombination().isEmpty())
                merchantPackOpt.setColorCombination(request.getColorCombination());
            else if (request.getMaxNbrOfPacks() != null)
                merchantPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks());
            else if (request.getMaxUnitsPerPack() != null)
                merchantPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack());
            else if (request.getPortOfOriginId() != null)
                merchantPackOpt.setPortOfOriginId(request.getPortOfOriginId());
            else if (request.getPortOfOriginName() != null && request.getPortOfOriginName().isEmpty())
                merchantPackOpt.setPortOfOriginName(request.getPortOfOriginName());
            else if (request.getSinglePackInd() != null)
                merchantPackOpt.setSinglePackInd(request.getSinglePackInd());
            else if (request.getFactoryName() != null && !request.getFactoryName().isEmpty())
                merchantPackOpt.setFactoryName(request.getFactoryName());
            else if (request.getFactoryId() != null)
                merchantPackOpt.setFactoryId(request.getFactoryId());
            else if (request.getOriginCountryCode() != null && !request.getOriginCountryCode().isEmpty())
                merchantPackOpt.setOriginCountryCode(request.getOriginCountryCode());
            else if (request.getOriginCountryName() != null && !request.getOriginCountryName().isEmpty())
                merchantPackOpt.setOriginCountryName(request.getOriginCountryName());
            else if (request.getVendorName() != null && !request.getVendorName().isEmpty())
                merchantPackOpt.setVendorName(request.getVendorName());
            else if (request.getVendorNbr6() != null)
                merchantPackOpt.setVendorNbr6(request.getVendorNbr6());
            else if (request.getVendorNbr9() != null)
                merchantPackOpt.setVendorNbr9(request.getVendorNbr9());

        });
    }





    private void updateFinelineSizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl4ListSP lvl4) {
        for (FineLineSP fl : lvl4.getFinelines()) {
            Optional.ofNullable(fl.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategyFineLineSPCluster strategyFineLineSPCluster = fetchStrategyFinelineSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr());
                            updateFlFields(clusterSpUpdatedFields, strategyFineLineSPCluster);
                            rollupSubCatgFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3.getLvl3Nbr(),lvl4.getLvl4Nbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Fineline size profile");
                        }
                    }
            );
            if (!CollectionUtils.isEmpty(fl.getStyles())) {
                updateStyleSizes(lvl3, strategyMerchCategorySPClusters, lvl4, fl);
            }
        }
    }

    private void updateStyleSizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl4ListSP lvl4, FineLineSP fl) {
        for (StyleSP st : fl.getStyles()) {
            Optional.ofNullable(st.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategyStyleSPCluster strategyStyleSPCluster = fetchStrategyStyleSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr(), st.getStyleNbr());
                            updateStFields(clusterSpUpdatedFields, strategyStyleSPCluster);
                            rollupFinelineFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(),fl.getFinelineNbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Style size profile");
                        }
                    }
            );
            if (!CollectionUtils.isEmpty(st.getCustomerChoices())) {
                updateCcSizes(lvl3, strategyMerchCategorySPClusters, lvl4, fl, st);
            }
        }
    }

    private void updateCcSizes(Lvl3ListSP lvl3, List<StrategyMerchCategorySPCluster> strategyMerchCategorySPClusters, Lvl4ListSP lvl4, FineLineSP fl, StyleSP st) {
        for (CustomerChoiceSP cc : st.getCustomerChoices()) {
            Optional.ofNullable(cc.getUpdatedSizes()).map(UpdatedSizesSP::getSizes).map(CommonUtil::getUpdatedFieldsMap).ifPresent(
                    clusterSpUpdatedFields -> {
                        try {
                            StrategyCcSPCluster strategyCcSPCluster = fetchStrategyCcSpClus(strategyMerchCategorySPClusters, lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr(), st.getStyleNbr(), cc.getCcId());
                            updateCcFields(clusterSpUpdatedFields, strategyCcSPCluster);
                            rollupStyleFields(strategyMerchCategorySPClusters,clusterSpUpdatedFields,lvl3.getLvl3Nbr(), lvl4.getLvl4Nbr(), fl.getFinelineNbr(), st.getStyleNbr());
                        } catch (JsonProcessingException e) {
                            throw new CustomException("Failed while updating Customer Choice size profile");
                        }
                    }
            );
        }
    }
    }