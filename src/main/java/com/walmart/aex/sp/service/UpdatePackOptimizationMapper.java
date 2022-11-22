package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.UpdatePackOptConstraintRequestDTO;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.SinglePackIndicator;
import com.walmart.aex.sp.repository.common.PackOptimizationCommonRepository;
import com.walmart.aex.sp.service.helper.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UpdatePackOptimizationMapper {
    @Autowired
    private final PackOptimizationCommonRepository packOptimizationCommonRepository;

    public UpdatePackOptimizationMapper(PackOptimizationCommonRepository packOptimizationCommonRepository) {
        this.packOptimizationCommonRepository = packOptimizationCommonRepository;
    }

    public void updateCategoryPackOptCons(UpdatePackOptConstraintRequestDTO request, List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Updating Category pack optimization constraint for lvl3Nbr {} ", request.getLvl3Nbr().toString());
        if (request.getLvl3Nbr()!=null && request.getLvl4Nbr() == null) {
            for (MerchantPackOptimization merchantPackOpt : merchantPackOptimizationList) {
                updateMerchCatgPackOptConstFields(request, merchantPackOpt);
            }
            packOptimizationCommonRepository.getMerchPackOptimizationRepository().saveAll(merchantPackOptimizationList);
        }
        List<SubCatgPackOptimization> subCatgPkOptPkConsList = merchantPackOptimizationList
                .stream()
                .flatMap(catgPackOptCons -> catgPackOptCons.getSubCatgPackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(subCatgPkOptPkConsList)){
            updateSubCategoryPackOptCons(request, subCatgPkOptPkConsList,merchantPackOptimizationList);
        }

    }

    private void updateSubCategoryPackOptCons(UpdatePackOptConstraintRequestDTO request, List<SubCatgPackOptimization> subCatgReplnPkConsList,List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Updating Sub Category pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getLvl4Nbr()!=null){
            subCatgReplnPkConsList = subCatgReplnPkConsList.stream().filter(subCatg-> subCatg.getSubCatgPackOptimizationID().getRepTLvl4().equals(request.getLvl4Nbr())).collect(Collectors.toList());
        }
        if (request.getFinelineNbr() == null) {
            for (SubCatgPackOptimization lvl4PackOptCons : subCatgReplnPkConsList) {
                updateSubCatgPackOptConstFields(request, lvl4PackOptCons);
            }
            rollupCatgFields(merchantPackOptimizationList,request.getLvl3Nbr());
        }
        packOptimizationCommonRepository.getSubCatgPackOptimizationRepository().saveAll(subCatgReplnPkConsList);
        List<FineLinePackOptimization> fineLinePkOptPkConsList = subCatgReplnPkConsList
                .stream()
                .flatMap(subCatgPackOptCons -> subCatgPackOptCons.getFinelinepackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(fineLinePkOptPkConsList)){
            updateFinelinePackOptCons(request, fineLinePkOptPkConsList,merchantPackOptimizationList);
        }
    }

    private void updateFinelinePackOptCons(UpdatePackOptConstraintRequestDTO request, List<FineLinePackOptimization> fineLinePackOptimizationList,List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Updating Fine Line pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getFinelineNbr()!=null){
            fineLinePackOptimizationList = fineLinePackOptimizationList.stream().filter(flPkOpt-> flPkOpt.getFinelinePackOptId().getFinelineNbr().equals(request.getFinelineNbr())).collect(Collectors.toList());
        }
        if (request.getStyleNbr() == null) {
            for (FineLinePackOptimization fl : fineLinePackOptimizationList) {
                updateFlPackOptConstFields(request, fl);
            }
            rollupSubCatgFields(merchantPackOptimizationList,request.getLvl3Nbr(),request.getLvl4Nbr());
        }
        packOptimizationCommonRepository.getFinelinePackOptConsRepository().saveAll(fineLinePackOptimizationList);
        List<StylePackOptimization> stylePackOptimizationList = fineLinePackOptimizationList
                .stream()
                .flatMap(fineLinePackOptCons -> fineLinePackOptCons.getStylePackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(stylePackOptimizationList)){
            updateStylePackOptCons(request, stylePackOptimizationList,merchantPackOptimizationList);
        }
    }

    private void updateStylePackOptCons(UpdatePackOptConstraintRequestDTO request, List<StylePackOptimization> stylePackOptimizationList,List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Updating Style pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getStyleNbr()!=null && !request.getStyleNbr().isEmpty()){
            stylePackOptimizationList=stylePackOptimizationList.stream().filter(stPkOpt-> stPkOpt.getStylePackoptimizationId().getStyleNbr().trim().equalsIgnoreCase(request.getStyleNbr().trim())).collect(Collectors.toList());
        }
        if (request.getCcId() == null) {
            for (StylePackOptimization st : stylePackOptimizationList) {
                updateStPackOptConstFields(request, st);
            }
            stylePackOptimizationList.forEach(StylePackOptimization::getCcPackOptimization);
            rollupFinelineFields(merchantPackOptimizationList,request.getLvl3Nbr(), request.getLvl4Nbr(),request.getFinelineNbr());
        }
        packOptimizationCommonRepository.getStylePackOptimizationRepository().saveAll(stylePackOptimizationList);
        List<CcPackOptimization> ccPackOptimizationList = stylePackOptimizationList
                .stream()
                .flatMap(stylePackOptCons -> stylePackOptCons.getCcPackOptimization().stream()).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(ccPackOptimizationList)){
            updateCcPackOptCons(request, ccPackOptimizationList,merchantPackOptimizationList);
        }
    }

    private void updateCcPackOptCons(UpdatePackOptConstraintRequestDTO request, List<CcPackOptimization> ccPackOptimizationList,List<MerchantPackOptimization> merchantPackOptimizationList) {
        log.info("Updating Customer choice pack optimization constraint for planId {} ", request.getPlanId().toString());
        if(request.getCcId()!=null && !request.getCcId().isEmpty()){
            ccPackOptimizationList=ccPackOptimizationList.stream().filter(stPkOpt-> stPkOpt.getCcPackOptimizationId().getCustomerChoice().trim().equalsIgnoreCase(request.getCcId().trim())).collect(Collectors.toList());
        }
        for (CcPackOptimization cc : ccPackOptimizationList) {
            updateCcPackOptConstFields(request, cc);
        }
        rollupStyleFields(merchantPackOptimizationList,request.getLvl3Nbr(), request.getLvl4Nbr(), request.getFinelineNbr(), request.getStyleNbr());
        packOptimizationCommonRepository.getCcPackOptimizationRepository().saveAll(ccPackOptimizationList);
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

    public static void setIfNotNull(Object value, Action action) {
        if (value != null)
            action.execute();
    }

    private void rollupCatgFields(List<MerchantPackOptimization> merchantPackOptimizationList,Integer lvl3Nbr)  {
        List<MerchantPackOptimization> merchantPackOptimizations = fetchMerchantPackOptimization(merchantPackOptimizationList, lvl3Nbr);
        merchantPackOptimizations.forEach(merchantPackOptimization -> {
            merchantPackOptimization.setSinglePackInd(getSinglePackIndicatorFlag(merchantPackOptimization.getSubCatgPackOptimization().stream().map(SubCatgPackOptimization::getSinglePackInd).collect(Collectors.toList())));
        });

    }

    private void rollupSubCatgFields(List<MerchantPackOptimization> merchantPackOptimizationList, Integer lvl3Nbr, Integer lvl4Nbr){
        List<MerchantPackOptimization> merchantPackOptimizations = fetchMerchantPackOptimization(merchantPackOptimizationList, lvl3Nbr);
        merchantPackOptimizations.forEach(merchantPackOptimization -> {
            fetchStrategySubCatgSpClus(merchantPackOptimization.getSubCatgPackOptimization(),lvl4Nbr).stream().forEach(
                    subCatgPackOptimization -> {
                        subCatgPackOptimization.setSinglePackInd(getSinglePackIndicatorFlag(subCatgPackOptimization.getFinelinepackOptimization().stream().map(FineLinePackOptimization::getSinglePackInd).collect(Collectors.toList())));
                    }
            );
        });
        rollupCatgFields(merchantPackOptimizationList,lvl3Nbr);

    }

    private void rollupFinelineFields(List<MerchantPackOptimization> merchantPackOptimizationList, Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr){
        List<MerchantPackOptimization> merchantPackOptimizations = fetchMerchantPackOptimization(merchantPackOptimizationList, lvl3Nbr);
        merchantPackOptimizations.forEach(merchantPackOptimization -> {
            fetchStrategySubCatgSpClus(merchantPackOptimization.getSubCatgPackOptimization(),lvl4Nbr).stream().forEach(
                   subCatgPackOptimization -> fetchStrategyFinelineSpClus(subCatgPackOptimization.getFinelinepackOptimization(),finelineNbr).forEach(fineLinePackOptimization -> {
                       fineLinePackOptimization.setSinglePackInd(getSinglePackIndicatorFlag(fineLinePackOptimization.getStylePackOptimization().stream().map(StylePackOptimization::getSinglePackInd).collect(Collectors.toList())));
                   })
            );
        });
        rollupSubCatgFields(merchantPackOptimizationList,lvl3Nbr,lvl4Nbr);
    }

    private void rollupStyleFields(List<MerchantPackOptimization> merchantPackOptimizationList,Integer lvl3Nbr, Integer lvl4Nbr, Integer finelineNbr, String styleNbr){
        List<MerchantPackOptimization> merchantPackOptimizations = fetchMerchantPackOptimization(merchantPackOptimizationList, lvl3Nbr);
        merchantPackOptimizations.forEach(merchantPackOptimization -> {
            fetchStrategySubCatgSpClus(merchantPackOptimization.getSubCatgPackOptimization(),lvl4Nbr).stream().forEach(
                    subCatgPackOptimization -> fetchStrategyFinelineSpClus(subCatgPackOptimization.getFinelinepackOptimization(),finelineNbr).forEach(fineLinePackOptimization -> {
                        fetchStylePackOptimization(fineLinePackOptimization.getStylePackOptimization(),styleNbr)
                                .forEach(stylePackOptimization -> {
                                    stylePackOptimization.setSinglePackInd(getSinglePackIndicatorFlag(stylePackOptimization.getCcPackOptimization().stream().map(CcPackOptimization::getSinglePackInd).collect(Collectors.toList())));
                                });
                    })
            );
        });
        rollupFinelineFields(merchantPackOptimizationList,lvl3Nbr,lvl4Nbr,finelineNbr);
    }

  public Integer getSinglePackIndicatorFlag(List<Integer> integers){
      boolean allChecked = integers.stream().allMatch(integer -> integer.equals(1));
      boolean allUnChecked = integers.stream().allMatch(integer -> integer.equals(0));
      if(allChecked){
          return SinglePackIndicator.SELECTED.getId();
      }else if(allUnChecked){
          return SinglePackIndicator.UNSELECTED.getId();
      }  else {
          return SinglePackIndicator.PARTIAL.getId();
      }
    }

    private List<MerchantPackOptimization> fetchMerchantPackOptimization(List<MerchantPackOptimization> merchantPackOptimizationList, Integer lvl3Nbr) {
        return Optional.ofNullable(merchantPackOptimizationList)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchantPackOptimization -> merchantPackOptimization.getMerchantPackOptimizationID().getRepTLvl3().equals(lvl3Nbr)).collect(Collectors.toList());

    }

    private List<SubCatgPackOptimization> fetchStrategySubCatgSpClus(Set<SubCatgPackOptimization> subCatgPackOptimizations,Integer lvl4Nbr) {
        return Optional.ofNullable(subCatgPackOptimizations)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatgPackOptimization -> subCatgPackOptimization.getSubCatgPackOptimizationID().getRepTLvl4().equals(lvl4Nbr)).collect(Collectors.toList());


    }

    private List<FineLinePackOptimization> fetchStrategyFinelineSpClus(Set<FineLinePackOptimization> fineLinePackOptimizations,Integer finelineNbr) {
        return Optional.ofNullable(fineLinePackOptimizations)
                .stream()
                .flatMap(Collection::stream)
                .filter(fineLinePackOptimization -> fineLinePackOptimization.getFinelinePackOptId().getFinelineNbr().equals(finelineNbr)).collect(Collectors.toList());
    }
    private List<StylePackOptimization> fetchStylePackOptimization(Set<StylePackOptimization> stylePackOptimizations,String styleNum) {
        return Optional.ofNullable(stylePackOptimizations)
                .stream()
                .flatMap(Collection::stream)
                .filter(stylePackOptimization -> stylePackOptimization.getStylePackoptimizationId().getStyleNbr().trim().equalsIgnoreCase(styleNum.trim())).collect(Collectors.toList());
    }

}

