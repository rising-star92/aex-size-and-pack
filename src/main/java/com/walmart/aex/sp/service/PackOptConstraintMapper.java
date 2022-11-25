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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PackOptConstraintMapper {

    public List<Lvl3> mapPackOptLvl3(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response) {
        List<Lvl3> lvl3List = CollectionUtils.isEmpty(response.getLvl3List())
                ? new ArrayList<>() : new ArrayList<>(response.getLvl3List());

        lvl3List.stream()
                .filter(lvl3 -> lvl3.getLvl3Nbr() != null && fineLineMapperDto.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(getLvl4Sp(fineLineMapperDto, lvl3)),
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
        lvl3.setLvl4List(getLvl4Sp(fineLineMapperDto, lvl3));
        lvl3.setConstraints(getConstraints(fineLineMapperDto, CategoryType.MERCHANT));
        lvl3List.add(lvl3);
    }

    private List<Lvl4> getLvl4Sp(FineLineMapperDto fineLineMapperDto, Lvl3 lvl3) {
        List<Lvl4> lvl4DtoList = CollectionUtils.isEmpty(lvl3.getLvl4List())
                ? new ArrayList<>() : new ArrayList<>(lvl3.getLvl4List());

        lvl4DtoList.stream()
                .filter(lvl4 -> lvl4.getLvl4Nbr() != null && fineLineMapperDto.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(getFineLines(fineLineMapperDto, lvl4)),
                        () -> setPackoptLvl4(fineLineMapperDto, lvl4DtoList));
        return lvl4DtoList;
    }

    private void setPackoptLvl4(FineLineMapperDto fineLineMapperDto, List<Lvl4> lvl4DtoList) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(fineLineMapperDto.getLvl4Nbr());
        lvl4.setLvl4Name(fineLineMapperDto.getLvl4Desc());
        lvl4.setFinelines(getFineLines(fineLineMapperDto, lvl4));
        lvl4.setConstraints(getConstraints(fineLineMapperDto, CategoryType.SUB_CATEGORY));
        lvl4DtoList.add(lvl4);
    }

    private List<Fineline> getFineLines(FineLineMapperDto fineLineMapperDto, Lvl4 lvl4) {
        List<Fineline> fineLineDtoList = CollectionUtils.isEmpty(lvl4.getFinelines())
                ? new ArrayList<>() : new ArrayList<>(lvl4.getFinelines());

        fineLineDtoList.stream()
                .filter(fineLineDto -> fineLineDto.getFinelineNbr() != null
                        && fineLineMapperDto.getFineLineNbr().equals(fineLineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(fineLineDto -> {
                            fineLineDtoList.remove(fineLineDto);
                            setPackOptFineLine(fineLineMapperDto, fineLineDtoList);
                        },
                        () -> setPackOptFineLine(fineLineMapperDto, fineLineDtoList));
        return fineLineDtoList;
    }

    private void setPackOptFineLine(FineLineMapperDto fineLineMapperDto, List<Fineline> finelineDtoList) {
        Fineline fineline = new Fineline();
        String status = StringUtils.isEmpty(fineLineMapperDto.getRunStatusDesc())
                ? "NOT SENT" : fineLineMapperDto.getRunStatusDesc();
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setFinelineName(fineLineMapperDto.getFineLineDesc());
        fineline.setAltFinelineName(fineLineMapperDto.getAltfineLineDesc());
        fineline.setPackOptimizationStatus(status);
        fineline.setOptimizationDetails(getRunOptimizationDetails(fineLineMapperDto));
        fineline.setConstraints(getConstraints(fineLineMapperDto, CategoryType.FINE_LINE));
        setPackOptStyles(fineLineMapperDto, fineline);
        finelineDtoList.add(fineline);
    }

    private List<RunOptimization> getRunOptimizationDetails(FineLineMapperDto fineLineMapperDto) {
        RunOptimization opt = new RunOptimization();
        opt.setName(fineLineMapperDto.getFirstName());
        opt.setReturnMessage(fineLineMapperDto.getReturnMessage());
        opt.setRunStatusCode(fineLineMapperDto.getRunStatusCode());
        opt.setStartTs(fineLineMapperDto.getStartTs());
        return List.of(opt);
    }

    private void setPackOptStyles(FineLineMapperDto fineLineMapperDto, Fineline fineline) {
        List<Style> styleDtoList = CollectionUtils.isEmpty(fineline.getStyles())
                ? new ArrayList<>() : new ArrayList<>(fineline.getStyles());

        styleDtoList.stream()
                .filter(styleDto -> styleDto.getStyleNbr() != null
                        && fineLineMapperDto.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(style ->
                                setPackOptCustomerChoice(fineLineMapperDto, style),
                        () -> setPackOptStyle(fineLineMapperDto, styleDtoList));
        fineline.setStyles(styleDtoList);
    }


    private void setPackOptStyle(FineLineMapperDto fineLineMapperDto, List<Style> styleDtoList) {
        Style styleDto = new Style();
        styleDto.setStyleNbr(fineLineMapperDto.getStyleNbr());
        styleDto.setConstraints(getConstraints(fineLineMapperDto.getStyleSupplierName(),
                fineLineMapperDto.getStyleFactoryIds(), fineLineMapperDto.getStyleCountryOfOrigin(),
                fineLineMapperDto.getStylePortOfOrigin(), fineLineMapperDto.getStyleSinglePackIndicator(),
                fineLineMapperDto.getStyleColorCombination()));
        setPackOptCustomerChoice(fineLineMapperDto, styleDto);
        styleDtoList.add(styleDto);
    }

    private void setPackOptCustomerChoice(FineLineMapperDto fineLineMapperDto, Style styleDto) {
        List<CustomerChoice> customerChoiceList = CollectionUtils.isEmpty(styleDto.getCustomerChoices())
                ? new ArrayList<>() : new ArrayList<>(styleDto.getCustomerChoices());

        customerChoiceList.stream()
                .filter(customerChoiceDto -> customerChoiceDto.getCcId() != null
                        && fineLineMapperDto.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                        },
                        () -> setPackOptCc(fineLineMapperDto, customerChoiceList));
        styleDto.setCustomerChoices(customerChoiceList);
    }

    private void setPackOptCc(FineLineMapperDto fineLineMapperDto, List<CustomerChoice> customerChoiceDtoList) {

        CustomerChoice customerChoiceDto = new CustomerChoice();
        customerChoiceDto.setCcId(fineLineMapperDto.getCcId());
        customerChoiceDto.setConstraints(getConstraints(fineLineMapperDto.getCcSupplierName(), fineLineMapperDto.getCcFactoryIds(),
                fineLineMapperDto.getCcCountryOfOrigin(), fineLineMapperDto.getCcPortOfOrigin(), fineLineMapperDto.getCcSinglePackIndicator(),
                fineLineMapperDto.getCcColorCombination()));
        customerChoiceDtoList.add(customerChoiceDto);
    }

    private Constraints getConstraints(String vendorName, String factoryId, String originCountryName,
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
