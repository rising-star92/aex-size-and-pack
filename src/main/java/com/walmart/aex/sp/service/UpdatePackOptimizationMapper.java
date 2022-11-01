package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.UpdatePackOptConstraintRequestDTO;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.repository.*;
import com.walmart.aex.sp.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.CommonUtil.setIfNotNull;

@Service
@Slf4j
public class UpdatePackOptimizationMapper {

    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private final SubCatgPackOptimizationRepository subCatgPackOptimizationRepository;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final StylePackOptimizationRepository stylePackOptimizationRepository;
    private final FinelinePackOptConsRepository finelinePackOptConsRepository;

    public UpdatePackOptimizationMapper(MerchPackOptimizationRepository merchPackOptimizationRepository, SubCatgPackOptimizationRepository subCatgPackOptimizationRepository, StylePackOptimizationRepository stylePackOptimizationRepository,
                                        CcPackOptimizationRepository ccPackOptimizationRepository, FinelinePackOptConsRepository finelinePackOptConsRepository) {
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.subCatgPackOptimizationRepository = subCatgPackOptimizationRepository;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.stylePackOptimizationRepository = stylePackOptimizationRepository;
        this.finelinePackOptConsRepository = finelinePackOptConsRepository;
    }

    public void updateCategoryPackOptCons(UpdatePackOptConstraintRequestDTO request, List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Updating Category pack optimization constraint for lvl3Nbr {} ", request.getLvl3Nbr().toString());
        if (request.getLvl3Nbr()!=null && request.getLvl4Nbr() == null) {
            for (MerchantPackOptimization merchantPackOpt : merchantPackOptimizationList) {
                updateMerchCatgPackOptConstFields(request, merchantPackOpt);
            };
            merchPackOptimizationRepository.saveAll(merchantPackOptimizationList);
        }
        List<SubCatgPackOptimization> subCatgPkOptPkConsList = merchantPackOptimizationList
                .stream()
                .flatMap(catgPackOptCons -> catgPackOptCons.getSubCatgPackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(subCatgPkOptPkConsList)){
            updateSubCategoryPackOptCons(request, subCatgPkOptPkConsList);
        }
    }

    private void updateSubCategoryPackOptCons(UpdatePackOptConstraintRequestDTO request, List<SubCatgPackOptimization> subCatgReplnPkConsList) {
        log.info("Updating Sub Category pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getLvl4Nbr()!=null){
            subCatgReplnPkConsList = subCatgReplnPkConsList.stream().filter(subCatg-> subCatg.getSubCatgPackOptimizationID().getRepTLvl4().equals(request.getLvl4Nbr())).collect(Collectors.toList());
        }
        if (request.getFinelineNbr() == null) {
            for (SubCatgPackOptimization lvl4PackOptCons : subCatgReplnPkConsList) {
                updateSubCatgPackOptConstFields(request, lvl4PackOptCons);
            }
            subCatgPackOptimizationRepository.saveAll(subCatgReplnPkConsList);
        }
        List<FineLinePackOptimization> fineLinePkOptPkConsList = subCatgReplnPkConsList
                .stream()
                .flatMap(subCatgPackOptCons -> subCatgPackOptCons.getFinelinepackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(fineLinePkOptPkConsList)){
            updateFinelinePackOptCons(request, fineLinePkOptPkConsList);
        }
    }

    private void updateFinelinePackOptCons(UpdatePackOptConstraintRequestDTO request, List<FineLinePackOptimization> fineLinePackOptimizationList) {
        log.info("Updating Fine Line pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getFinelineNbr()!=null){
            fineLinePackOptimizationList = fineLinePackOptimizationList.stream().filter(flPkOpt-> flPkOpt.getFinelinePackOptId().getFinelineNbr().equals(request.getFinelineNbr())).collect(Collectors.toList());
        }
        if (request.getStyleNbr() == null) {
            for (FineLinePackOptimization fl : fineLinePackOptimizationList) {
                updateFlPackOptConstFields(request, fl);
            }
            finelinePackOptConsRepository.saveAll(fineLinePackOptimizationList);
        }
        List<StylePackOptimization> stylePackOptimizationList = fineLinePackOptimizationList
                .stream()
                .flatMap(fineLinePackOptCons -> fineLinePackOptCons.getStylePackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(stylePackOptimizationList)){
            updateStylePackOptCons(request, stylePackOptimizationList);
        }
    }

    private void updateStylePackOptCons(UpdatePackOptConstraintRequestDTO request, List<StylePackOptimization> stylePackOptimizationList) {
        log.info("Updating Style pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getStyleNbr()!=null && !request.getStyleNbr().isEmpty()){
            stylePackOptimizationList=stylePackOptimizationList.stream().filter(stPkOpt-> stPkOpt.getStylePackoptimizationId().getStyleNbr().trim().equalsIgnoreCase(request.getStyleNbr().trim())).collect(Collectors.toList());
        }
        if (request.getCcId() == null) {
            for (StylePackOptimization st : stylePackOptimizationList) {
                updateStPackOptConstFields(request, st);
            }
            stylePackOptimizationRepository.saveAll(stylePackOptimizationList);
        }
        List<CcPackOptimization> ccPackOptimizationList = stylePackOptimizationList
                .stream()
                .flatMap(stylePackOptCons -> stylePackOptCons.getCcPackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(ccPackOptimizationList)){
            updateCcPackOptCons(request, ccPackOptimizationList);
        }
    }

    private void updateCcPackOptCons(UpdatePackOptConstraintRequestDTO request, List<CcPackOptimization> ccPackOptimizationList) {
        log.info("Updating Customer choice pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getCcId()!=null && !request.getCcId().isEmpty()){
            ccPackOptimizationList=ccPackOptimizationList.stream().filter(stPkOpt-> stPkOpt.getCcPackOptimizationId().getCustomerChoice().trim().equalsIgnoreCase(request.getCcId().trim())).collect(Collectors.toList());
        }
        for (CcPackOptimization cc : ccPackOptimizationList) {
            updateCcPackOptConstFields(request, cc);
        }
        ccPackOptimizationRepository.saveAll(ccPackOptimizationList);
    }

    private void updateMerchCatgPackOptConstFields(UpdatePackOptConstraintRequestDTO request, MerchantPackOptimization merchantPackOpt) {
        setIfNotNull(request.getColorCombination(), () -> merchantPackOpt.setColorCombination(request.getColorCombination()));
        setIfNotNull(request.getMaxNbrOfPacks(), () -> merchantPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks()));
        setIfNotNull(request.getMaxUnitsPerPack(), () -> merchantPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack()));
        setIfNotNull(request.getPortOfOriginId(), () -> merchantPackOpt.setPortOfOriginId(request.getPortOfOriginId()));
        setIfNotNull(request.getPortOfOriginName(), () -> merchantPackOpt.setPortOfOriginName(request.getPortOfOriginName()));
        setIfNotNull(request.getSinglePackInd(), () -> merchantPackOpt.setSinglePackInd(request.getSinglePackInd()));
        setIfNotNull(request.getFactoryId(), () -> merchantPackOpt.setFactoryId(request.getFactoryId()));
        setIfNotNull(request.getFactoryName(), () -> merchantPackOpt.setFactoryName(request.getFactoryName()));
        setIfNotNull(request.getOriginCountryCode(), () -> merchantPackOpt.setOriginCountryCode(request.getOriginCountryCode()));
        setIfNotNull(request.getOriginCountryName(), () -> merchantPackOpt.setOriginCountryName(request.getOriginCountryName()));
        setIfNotNull(request.getVendorName(), () -> merchantPackOpt.setVendorName(request.getVendorName()));
        setIfNotNull(request.getVendorNbr6(), () -> merchantPackOpt.setVendorNbr6(request.getVendorNbr6()));
        setIfNotNull(request.getVendorNbr9(), () -> merchantPackOpt.setVendorNbr9(request.getVendorNbr9()));
    }

    private void updateSubCatgPackOptConstFields(UpdatePackOptConstraintRequestDTO request, SubCatgPackOptimization subCatgPackOpt) {
        setIfNotNull(request.getColorCombination(), () -> subCatgPackOpt.setColorCombination(request.getColorCombination()));
        setIfNotNull(request.getMaxNbrOfPacks(), () -> subCatgPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks()));
        setIfNotNull(request.getMaxUnitsPerPack(), () -> subCatgPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack()));
        setIfNotNull(request.getPortOfOriginId(), () -> subCatgPackOpt.setPortOfOriginId(request.getPortOfOriginId()));
        setIfNotNull(request.getPortOfOriginName(), () -> subCatgPackOpt.setPortOfOriginName(request.getPortOfOriginName()));
        setIfNotNull(request.getSinglePackInd(), () -> subCatgPackOpt.setSinglePackInd(request.getSinglePackInd()));
        setIfNotNull(request.getFactoryId(), () -> subCatgPackOpt.setFactoryId(request.getFactoryId()));
        setIfNotNull(request.getFactoryName(), () -> subCatgPackOpt.setFactoryName(request.getFactoryName()));
        setIfNotNull(request.getOriginCountryCode(), () -> subCatgPackOpt.setOriginCountryCode(request.getOriginCountryCode()));
        setIfNotNull(request.getOriginCountryName(), () -> subCatgPackOpt.setOriginCountryName(request.getOriginCountryName()));
        setIfNotNull(request.getVendorName(), () -> subCatgPackOpt.setVendorName(request.getVendorName()));
        setIfNotNull(request.getVendorNbr6(), () -> subCatgPackOpt.setVendorNbr6(request.getVendorNbr6()));
        setIfNotNull(request.getVendorNbr9(), () -> subCatgPackOpt.setVendorNbr9(request.getVendorNbr9()));

    }

    private void updateFlPackOptConstFields(UpdatePackOptConstraintRequestDTO request, FineLinePackOptimization fineLinePackOpt) {
        setIfNotNull(request.getColorCombination(), () -> fineLinePackOpt.setColorCombination(request.getColorCombination()));
        setIfNotNull(request.getMaxNbrOfPacks(), () -> fineLinePackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks()));
        setIfNotNull(request.getMaxUnitsPerPack(), () -> fineLinePackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack()));
        setIfNotNull(request.getPortOfOriginId(), () -> fineLinePackOpt.setPortOfOriginId(request.getPortOfOriginId()));
        setIfNotNull(request.getPortOfOriginName(), () -> fineLinePackOpt.setPortOfOriginName(request.getPortOfOriginName()));
        setIfNotNull(request.getSinglePackInd(), () -> fineLinePackOpt.setSinglePackInd(request.getSinglePackInd()));
        setIfNotNull(request.getFactoryId(), () -> fineLinePackOpt.setFactoryId(request.getFactoryId()));
        setIfNotNull(request.getFactoryName(), () -> fineLinePackOpt.setFactoryName(request.getFactoryName()));
        setIfNotNull(request.getOriginCountryCode(), () -> fineLinePackOpt.setOriginCountryCode(request.getOriginCountryCode()));
        setIfNotNull(request.getOriginCountryName(), () -> fineLinePackOpt.setOriginCountryName(request.getOriginCountryName()));
        setIfNotNull(request.getVendorName(), () -> fineLinePackOpt.setVendorName(request.getVendorName()));
        setIfNotNull(request.getVendorNbr6(), () -> fineLinePackOpt.setVendorNbr6(request.getVendorNbr6()));
        setIfNotNull(request.getVendorNbr9(), () -> fineLinePackOpt.setVendorNbr9(request.getVendorNbr9()));
    }

    private void updateStPackOptConstFields(UpdatePackOptConstraintRequestDTO request, StylePackOptimization stylePackOpt) {
        setIfNotNull(request.getColorCombination(), () -> stylePackOpt.setColorCombination(request.getColorCombination()));
        setIfNotNull(request.getMaxNbrOfPacks(), () -> stylePackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks()));
        setIfNotNull(request.getMaxUnitsPerPack(), () -> stylePackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack()));
        setIfNotNull(request.getPortOfOriginId(), () -> stylePackOpt.setPortOfOriginId(request.getPortOfOriginId()));
        setIfNotNull(request.getPortOfOriginName(), () -> stylePackOpt.setPortOfOriginName(request.getPortOfOriginName()));
        setIfNotNull(request.getSinglePackInd(), () -> stylePackOpt.setSinglePackInd(request.getSinglePackInd()));
        setIfNotNull(request.getFactoryId(), () -> stylePackOpt.setFactoryId(request.getFactoryId()));
        setIfNotNull(request.getFactoryName(), () -> stylePackOpt.setFactoryName(request.getFactoryName()));
        setIfNotNull(request.getOriginCountryCode(), () -> stylePackOpt.setOriginCountryCode(request.getOriginCountryCode()));
        setIfNotNull(request.getOriginCountryName(), () -> stylePackOpt.setOriginCountryName(request.getOriginCountryName()));
        setIfNotNull(request.getVendorName(), () -> stylePackOpt.setVendorName(request.getVendorName()));
        setIfNotNull(request.getVendorNbr6(), () -> stylePackOpt.setVendorNbr6(request.getVendorNbr6()));
        setIfNotNull(request.getVendorNbr9(), () -> stylePackOpt.setVendorNbr9(request.getVendorNbr9()));
    }

    private void updateCcPackOptConstFields(UpdatePackOptConstraintRequestDTO request, CcPackOptimization ccPackOpt) {
        setIfNotNull(request.getColorCombination(), () -> ccPackOpt.setColorCombination(request.getColorCombination()));
        setIfNotNull(request.getMaxNbrOfPacks(), () -> ccPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks()));
        setIfNotNull(request.getMaxUnitsPerPack(), () -> ccPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack()));
        setIfNotNull(request.getPortOfOriginId(), () -> ccPackOpt.setPortOfOriginId(request.getPortOfOriginId()));
        setIfNotNull(request.getPortOfOriginName(), () -> ccPackOpt.setPortOfOriginName(request.getPortOfOriginName()));
        setIfNotNull(request.getSinglePackInd(), () -> ccPackOpt.setSinglePackInd(request.getSinglePackInd()));
        setIfNotNull(request.getFactoryId(), () -> ccPackOpt.setFactoryId(request.getFactoryId()));
        setIfNotNull(request.getFactoryName(), () -> ccPackOpt.setFactoryName(request.getFactoryName()));
        setIfNotNull(request.getOriginCountryCode(), () -> ccPackOpt.setOriginCountryCode(request.getOriginCountryCode()));
        setIfNotNull(request.getOriginCountryName(), () -> ccPackOpt.setOriginCountryName(request.getOriginCountryName()));
        setIfNotNull(request.getVendorName(), () -> ccPackOpt.setVendorName(request.getVendorName()));
        setIfNotNull(request.getVendorNbr6(), () -> ccPackOpt.setVendorNbr6(request.getVendorNbr6()));
        setIfNotNull(request.getVendorNbr9(), () -> ccPackOpt.setVendorNbr9(request.getVendorNbr9()));
    }


}