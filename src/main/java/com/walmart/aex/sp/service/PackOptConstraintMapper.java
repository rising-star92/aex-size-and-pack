package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.ColorCombinationConstraints;
import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.packoptimization.FinelineLevelConstraints;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.RunOptimization;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.enums.CategoryType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PackOptConstraintMapper {

    public List<Lvl3> mapPackOptLvl3(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response) {
        List<Lvl3> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> fineLineMapperDto.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapPackOptLvl4Sp(fineLineMapperDto, lvl3)),
                        () -> setPackOptLvl3(fineLineMapperDto, lvl3List));
        return lvl3List;
    }

    private void setPackOptLvl3(FineLineMapperDto fineLineMapperDto, List<Lvl3> lvl3List) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl0Nbr(fineLineMapperDto.getLvl0Nbr());
        lvl3.setLvl1Nbr(fineLineMapperDto.getLvl1Nbr());
        lvl3.setLvl2Nbr(fineLineMapperDto.getLvl2Nbr());
        lvl3.setLvl3Nbr(fineLineMapperDto.getLvl3Nbr());
        lvl3.setLvl3Name(fineLineMapperDto.getLvl3Desc());
        lvl3.setLvl4List(mapPackOptLvl4Sp(fineLineMapperDto, lvl3));
        lvl3.setConstraints(getConstraints(fineLineMapperDto, CategoryType.MERCHANT));
        lvl3List.add(lvl3);
    }

    private List<Lvl4> mapPackOptLvl4Sp(FineLineMapperDto fineLineMapperDto, Lvl3 lvl3) {
        List<Lvl4> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> fineLineMapperDto.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapPackOptFinelines(fineLineMapperDto, lvl4)),
                        () -> setPackoptLvl4(fineLineMapperDto, lvl4DtoList));
        return lvl4DtoList;
    }

    private void setPackoptLvl4(FineLineMapperDto fineLineMapperDto, List<Lvl4> lvl4DtoList) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(fineLineMapperDto.getLvl4Nbr());
        lvl4.setLvl4Name(fineLineMapperDto.getLvl4Desc());
        lvl4.setFinelines(mapPackOptFinelines(fineLineMapperDto, lvl4));
        lvl4.setConstraints(getConstraints(fineLineMapperDto, CategoryType.SUB_CATEGORY));
        lvl4DtoList.add(lvl4);
    }

    private List<Fineline> mapPackOptFinelines(FineLineMapperDto fineLineMapperDto, Lvl4 lvl4) {
        List<Fineline> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> fineLineMapperDto.getFineLineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> {
                            if (finelineDto.getOptimizationDetails() != null &&
                                    !finelineDto.getOptimizationDetails().isEmpty()
                                    && finelineDto.getOptimizationDetails().get(0).getStartTs() != null
                                    && finelineDto.getOptimizationDetails().get(0).getStartTs()
                                    .compareTo(fineLineMapperDto.getStartTs()) < 0) {
                                finelineDtoList.remove(finelineDto);
                                setPackOptFineline(fineLineMapperDto, finelineDtoList);
                            }
                            if (fineLineMapperDto.getFineLineNbr() != null) {
                                finelineDto.setStyles(mapPackOptStyles(fineLineMapperDto, finelineDto));
                            }
                        },
                        () -> setPackOptFineline(fineLineMapperDto, finelineDtoList));
        return finelineDtoList;
    }

    private void setPackOptFineline(FineLineMapperDto fineLineMapperDto, List<Fineline> finelineDtoList) {
        Fineline fineline = new Fineline();
        String status = Optional.ofNullable(fineLineMapperDto.getRunStatusDesc()).orElse("NOT SENT");
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setFinelineName(fineLineMapperDto.getFineLineDesc());
        fineline.setAltFinelineName(fineLineMapperDto.getAltfineLineDesc());
        fineline.setPackOptimizationStatus(status);
        fineline.setOptimizationDetails(setOptimizationDetails(fineLineMapperDto));
        fineline.setConstraints(getConstraints(fineLineMapperDto, CategoryType.FINE_LINE));
        if (fineLineMapperDto.getFineLineNbr() != null) {
            fineline.setStyles(mapPackOptStyles(fineLineMapperDto, fineline));
        }
        finelineDtoList.add(fineline);
    }

    private List<RunOptimization> setOptimizationDetails(FineLineMapperDto fineLineMapperDto) {
        RunOptimization opt = new RunOptimization();
        opt.setName(fineLineMapperDto.getFirstName());
        opt.setReturnMessage(fineLineMapperDto.getReturnMessage());
        opt.setRunStatusCode(fineLineMapperDto.getRunStatusCode());
        opt.setStartTs(fineLineMapperDto.getStartTs());
        return List.of(opt);
    }

    private List<Style> mapPackOptStyles(FineLineMapperDto fineLineMapperDto, Fineline fineline) {
        List<Style> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

        styleDtoList.stream()
                .filter(styleDto -> fineLineMapperDto.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(style ->
                                style.setCustomerChoices(mapPackOptCc(fineLineMapperDto, style)),
                        () -> setPackOptStyle(fineLineMapperDto, styleDtoList));
        return styleDtoList;
    }


    private void setPackOptStyle(FineLineMapperDto fineLineMapperDto, List<Style> styleDtoList) {

        Style styleDto = new Style();
        styleDto.setStyleNbr(fineLineMapperDto.getStyleNbr());
        styleDto.setConstraints(setConstraints(fineLineMapperDto.getStyleSupplierName(),
                fineLineMapperDto.getStyleFactoryIds(), fineLineMapperDto.getStyleCountryOfOrigin(),
                fineLineMapperDto.getStylePortOfOrigin(), fineLineMapperDto.getStyleSinglePackIndicator(),
                fineLineMapperDto.getStyleColorCombination()));
        styleDto.setCustomerChoices(mapPackOptCc(fineLineMapperDto, styleDto));
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoice> mapPackOptCc(FineLineMapperDto fineLineMapperDto, Style styleDto) {
        List<CustomerChoice> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());

        customerChoiceList.stream()
                .filter(customerChoiceDto -> fineLineMapperDto.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                        },
                        () -> setPackOptCc(fineLineMapperDto, customerChoiceList));
        return customerChoiceList;

    }

    private void setPackOptCc(FineLineMapperDto fineLineMapperDto, List<CustomerChoice> customerChoiceDtoList) {

        CustomerChoice customerChoiceDto = new CustomerChoice();
        customerChoiceDto.setCcId(fineLineMapperDto.getCcId());
        customerChoiceDto.setConstraints(setConstraints(fineLineMapperDto.getCcSupplierName(), fineLineMapperDto.getCcFactoryIds(),
                fineLineMapperDto.getCcCountryOfOrigin(), fineLineMapperDto.getCcPortOfOrigin(), fineLineMapperDto.getCcSinglePackIndicator(),
                fineLineMapperDto.getCcColorCombination()));
        customerChoiceDtoList.add(customerChoiceDto);
    }

    private Constraints setConstraints(String vendorName, String factoryId, String originCountryName,
                                       String portOfOriginName, Integer singlePackInd, String colorCombination) {
        Constraints constraints = new Constraints();
        constraints.setColorCombinationConstraints(new ColorCombinationConstraints(vendorName, factoryId,
                originCountryName, portOfOriginName, singlePackInd, colorCombination));
        return constraints;
    }

    private Constraints getConstraints(FineLineMapperDto fineLineMapperDto, CategoryType type) {

        Constraints constraints = new Constraints();
        switch (type) {
            case MERCHANT:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getMerchMaxNbrOfPacks(),fineLineMapperDto.getMerchMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(fineLineMapperDto.getMerchSupplierName(), fineLineMapperDto.getMerchFactoryId(),
                        fineLineMapperDto.getMerchOriginCountryName(), fineLineMapperDto.getMerchPortOfOriginName(),
                        fineLineMapperDto.getMerchSinglePackInd(), fineLineMapperDto.getMerchColorCombination()));
                break;
            case SUB_CATEGORY:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getSubCatMaxNbrOfPacks(),fineLineMapperDto.getSubCatMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(fineLineMapperDto.getSubCatSupplierName(), fineLineMapperDto.getSubCatFactoryId(),
                        fineLineMapperDto.getSubCatOriginCountryName(), fineLineMapperDto.getSubCatPortOfOriginName(),
                        fineLineMapperDto.getSubCatSinglePackInd(), fineLineMapperDto.getSubCatColorCombination()));
                break;
            default:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getFineLineMaxNbrOfPacks(),fineLineMapperDto.getFineLineMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(fineLineMapperDto.getFineLineSupplierName(), fineLineMapperDto.getFineLineFactoryId(),
                        fineLineMapperDto.getFineLineOriginCountryName(), fineLineMapperDto.getFineLinePortOfOriginName(),
                        fineLineMapperDto.getFineLineSinglePackInd(), fineLineMapperDto.getFineLineColorCombination()));
                break;
        }
        return constraints;

    }
}
