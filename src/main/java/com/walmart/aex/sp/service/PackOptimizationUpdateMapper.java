package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.PackOptConstraintUpdateRequestDTO;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PackOptimizationUpdateMapper {

    private final MerchPackOptimizationRepository merchPackOptimizationRepository;
    private final SubCatgPackOptimizationRepository subCatgPackOptimizationRepository;
    private final CcPackOptimizationRepository ccPackOptimizationRepository;
    private final StylePackOptimizationRepository stylePackOptimizationRepository;

    private final FinelinePackOptRepository finelinePackOptRepository;

    public PackOptimizationUpdateMapper(MerchPackOptimizationRepository merchPackOptimizationRepository, SubCatgPackOptimizationRepository subCatgPackOptimizationRepository, StylePackOptimizationRepository stylePackOptimizationRepository,
                                        CcPackOptimizationRepository ccPackOptimizationRepository, FinelinePackOptRepository finelinePackOptRepository) {
        this.merchPackOptimizationRepository = merchPackOptimizationRepository;
        this.subCatgPackOptimizationRepository = subCatgPackOptimizationRepository;
        this.ccPackOptimizationRepository = ccPackOptimizationRepository;
        this.stylePackOptimizationRepository = stylePackOptimizationRepository;
        this.finelinePackOptRepository = finelinePackOptRepository;
    }

    public void updateCategoryPackOptCons(Integer channelId, PackOptConstraintUpdateRequestDTO request, List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Check if a MerchCatPackOptimization for merchantPackOptimizationID : {} already exists or not", request.getPlanId().toString());

        if (request.getLvl4Nbr() == null) {
            merchantPackOptimizationList.forEach(merchantPackOpt -> {
                updateMerchCatgPackOptConstFields(request, merchantPackOpt);
            });
            merchPackOptimizationRepository.saveAll(merchantPackOptimizationList);
        }
        List<SubCatgPackOptimization> subCatgPkOptPkConsList = merchantPackOptimizationList
                .stream()
                .flatMap(catgPackOptCons -> catgPackOptCons.getSubCatgPackOptimization().stream()).filter(subCatgPkOpt -> Objects.equals(subCatgPkOpt.getChannelText().getChannelId(), channelId)).collect(Collectors.toList());
        updateSubCategoryPackOptCons(request, subCatgPkOptPkConsList, channelId);
    }

    private void updateSubCategoryPackOptCons(PackOptConstraintUpdateRequestDTO request, List<SubCatgPackOptimization> subCatgReplnPkConsList, Integer channelId) {

        if (request.getFinelineNbr() == null) {
            for (SubCatgPackOptimization lvl4PackOptCons : subCatgReplnPkConsList) {
                updateSubCatgPackOptConstFields(request, lvl4PackOptCons);
            }
            subCatgPackOptimizationRepository.saveAll(subCatgReplnPkConsList);
        }
        List<fineLinePackOptimization> fineLinePkOptPkConsList = subCatgReplnPkConsList
                .stream()
                .flatMap(subCatgPackOptCons -> subCatgPackOptCons.getFinelinepackOptimization().stream()).filter(fineLinePkOpt -> Objects.equals(fineLinePkOpt.getChannelText().getChannelId(), channelId)).collect(Collectors.toList());
        updateFinelinePackOptCons(request, fineLinePkOptPkConsList, channelId);
    }

    private void updateFinelinePackOptCons(PackOptConstraintUpdateRequestDTO request, List<fineLinePackOptimization> fineLinePackOptimizationList, Integer channelId) {
        if (request.getStyleNbr() == null) {
            for (fineLinePackOptimization fl : fineLinePackOptimizationList) {
                updateFlPackOptConstWithNewInputFields(request, fl);
            }
            finelinePackOptRepository.saveAll(fineLinePackOptimizationList);
        }
        List<StylePackOptimization> stylePackOptimizationList = fineLinePackOptimizationList
                .stream()
                .flatMap(fineLinePackOptCons -> fineLinePackOptCons.getStylePackOptimization().stream()).filter(stylePkOpt -> Objects.equals(stylePkOpt.getChannelText().getChannelId(), channelId)).collect(Collectors.toList());
        updateStylePackOptCons(request, stylePackOptimizationList, channelId);

    }

    private void updateStylePackOptCons(PackOptConstraintUpdateRequestDTO request, List<StylePackOptimization> stylePackOptimizationList, Integer channelId) {
        if (request.getCcId() == null) {
            for (StylePackOptimization st : stylePackOptimizationList) {
                updateStPackOptConstWithNewInputFields(request, st);
            }
            stylePackOptimizationRepository.saveAll(stylePackOptimizationList);
        }
        List<CcPackOptimization> ccPackOptimizationList = stylePackOptimizationList
                .stream()
                .flatMap(stylePackOptCons -> stylePackOptCons.getCcPackOptimization().stream()).filter(ccPkOpt -> Objects.equals(ccPkOpt.getChannelText().getChannelId(), channelId)).collect(Collectors.toList());
        updateCcPackOptCons(request, ccPackOptimizationList);
    }


    private void updateCcPackOptCons(PackOptConstraintUpdateRequestDTO request, List<CcPackOptimization> ccPackOptimizationList) {
        for (CcPackOptimization cc : ccPackOptimizationList) {
            updateCcPackOptConstWithNewInputFields(request, cc);
        }
    }

    private void updateMerchCatgPackOptConstFields(PackOptConstraintUpdateRequestDTO request, MerchantPackOptimization merchantPackOpt) {
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

    private void updateSubCatgPackOptConstFields(PackOptConstraintUpdateRequestDTO request, SubCatgPackOptimization subCatgPackOpt) {
        if (request.getColorCombination() != null && !request.getColorCombination().isEmpty())
            subCatgPackOpt.setColorCombination(request.getColorCombination());
        else if (request.getMaxNbrOfPacks() != null)
            subCatgPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks());
        else if (request.getMaxUnitsPerPack() != null)
            subCatgPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack());
        else if (request.getPortOfOriginId() != null)
            subCatgPackOpt.setPortOfOriginId(request.getPortOfOriginId());
        else if (request.getPortOfOriginName() != null && request.getPortOfOriginName().isEmpty())
            subCatgPackOpt.setPortOfOriginName(request.getPortOfOriginName());
        else if (request.getSinglePackInd() != null)
            subCatgPackOpt.setSinglePackInd(request.getSinglePackInd());
        else if (request.getFactoryName() != null && !request.getFactoryName().isEmpty())
            subCatgPackOpt.setFactoryName(request.getFactoryName());
        else if (request.getFactoryId() != null)
            subCatgPackOpt.setFactoryId(request.getFactoryId());
        else if (request.getOriginCountryCode() != null && !request.getOriginCountryCode().isEmpty())
            subCatgPackOpt.setOriginCountryCode(request.getOriginCountryCode());
        else if (request.getOriginCountryName() != null && !request.getOriginCountryName().isEmpty())
            subCatgPackOpt.setOriginCountryName(request.getOriginCountryName());
        else if (request.getVendorName() != null && !request.getVendorName().isEmpty())
            subCatgPackOpt.setVendorName(request.getVendorName());
        else if (request.getVendorNbr6() != null)
            subCatgPackOpt.setVendorNbr6(request.getVendorNbr6());
        else if (request.getVendorNbr9() != null)
            subCatgPackOpt.setVendorNbr9(request.getVendorNbr9());

    }

    private void updateFlPackOptConstWithNewInputFields(PackOptConstraintUpdateRequestDTO request, fineLinePackOptimization fineLinePackOpt) {
        if (request.getColorCombination() != null && !request.getColorCombination().isEmpty())
            fineLinePackOpt.setColorCombination(request.getColorCombination());
        else if (request.getMaxNbrOfPacks() != null)
            fineLinePackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks());
        else if (request.getMaxUnitsPerPack() != null)
            fineLinePackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack());
        else if (request.getPortOfOriginId() != null)
            fineLinePackOpt.setPortOfOriginId(request.getPortOfOriginId());
        else if (request.getPortOfOriginName() != null && request.getPortOfOriginName().isEmpty())
            fineLinePackOpt.setPortOfOriginName(request.getPortOfOriginName());
        else if (request.getSinglePackInd() != null)
            fineLinePackOpt.setSinglePackInd(request.getSinglePackInd());
        else if (request.getFactoryName() != null && !request.getFactoryName().isEmpty())
            fineLinePackOpt.setFactoryName(request.getFactoryName());
        else if (request.getFactoryId() != null)
            fineLinePackOpt.setFactoryId(request.getFactoryId());
        else if (request.getOriginCountryCode() != null && !request.getOriginCountryCode().isEmpty())
            fineLinePackOpt.setOriginCountryCode(request.getOriginCountryCode());
        else if (request.getOriginCountryName() != null && !request.getOriginCountryName().isEmpty())
            fineLinePackOpt.setOriginCountryName(request.getOriginCountryName());
        else if (request.getVendorName() != null && !request.getVendorName().isEmpty())
            fineLinePackOpt.setVendorName(request.getVendorName());
        else if (request.getVendorNbr6() != null)
            fineLinePackOpt.setVendorNbr6(request.getVendorNbr6());
        else if (request.getVendorNbr9() != null)
            fineLinePackOpt.setVendorNbr9(request.getVendorNbr9());
    }

    private void updateStPackOptConstWithNewInputFields(PackOptConstraintUpdateRequestDTO request, StylePackOptimization stylePackOpt) {
        if (request.getColorCombination() != null && !request.getColorCombination().isEmpty())
            stylePackOpt.setColorCombination(request.getColorCombination());
        else if (request.getMaxNbrOfPacks() != null)
            stylePackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks());
        else if (request.getMaxUnitsPerPack() != null)
            stylePackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack());
        else if (request.getPortOfOriginId() != null)
            stylePackOpt.setPortOfOriginId(request.getPortOfOriginId());
        else if (request.getPortOfOriginName() != null && request.getPortOfOriginName().isEmpty())
            stylePackOpt.setPortOfOriginName(request.getPortOfOriginName());
        else if (request.getSinglePackInd() != null)
            stylePackOpt.setSinglePackInd(request.getSinglePackInd());
        else if (request.getFactoryName() != null && !request.getFactoryName().isEmpty())
            stylePackOpt.setFactoryName(request.getFactoryName());
        else if (request.getFactoryId() != null)
            stylePackOpt.setFactoryId(request.getFactoryId());
        else if (request.getOriginCountryCode() != null && !request.getOriginCountryCode().isEmpty())
            stylePackOpt.setOriginCountryCode(request.getOriginCountryCode());
        else if (request.getOriginCountryName() != null && !request.getOriginCountryName().isEmpty())
            stylePackOpt.setOriginCountryName(request.getOriginCountryName());
        else if (request.getVendorName() != null && !request.getVendorName().isEmpty())
            stylePackOpt.setVendorName(request.getVendorName());
        else if (request.getVendorNbr6() != null)
            stylePackOpt.setVendorNbr6(request.getVendorNbr6());
        else if (request.getVendorNbr9() != null)
            stylePackOpt.setVendorNbr9(request.getVendorNbr9());
    }

    private void updateCcPackOptConstWithNewInputFields(PackOptConstraintUpdateRequestDTO request, CcPackOptimization ccPackOpt) {
        if (request.getColorCombination() != null && !request.getColorCombination().isEmpty())
            ccPackOpt.setColorCombination(request.getColorCombination());
        else if (request.getMaxNbrOfPacks() != null)
            ccPackOpt.setMaxNbrOfPacks(request.getMaxNbrOfPacks());
        else if (request.getMaxUnitsPerPack() != null)
            ccPackOpt.setMaxUnitsPerPack(request.getMaxUnitsPerPack());
        else if (request.getPortOfOriginId() != null)
            ccPackOpt.setPortOfOriginId(request.getPortOfOriginId());
        else if (request.getPortOfOriginName() != null && request.getPortOfOriginName().isEmpty())
            ccPackOpt.setPortOfOriginName(request.getPortOfOriginName());
        else if (request.getSinglePackInd() != null)
            ccPackOpt.setSinglePackInd(request.getSinglePackInd());
        else if (request.getFactoryName() != null && !request.getFactoryName().isEmpty())
            ccPackOpt.setFactoryName(request.getFactoryName());
        else if (request.getFactoryId() != null)
            ccPackOpt.setFactoryId(request.getFactoryId());
        else if (request.getOriginCountryCode() != null && !request.getOriginCountryCode().isEmpty())
            ccPackOpt.setOriginCountryCode(request.getOriginCountryCode());
        else if (request.getOriginCountryName() != null && !request.getOriginCountryName().isEmpty())
            ccPackOpt.setOriginCountryName(request.getOriginCountryName());
        else if (request.getVendorName() != null && !request.getVendorName().isEmpty())
            ccPackOpt.setVendorName(request.getVendorName());
        else if (request.getVendorNbr6() != null)
            ccPackOpt.setVendorNbr6(request.getVendorNbr6());
        else if (request.getVendorNbr9() != null)
            ccPackOpt.setVendorNbr9(request.getVendorNbr9());
    }

}