package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.enums.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PackOptConstraintMapper {

    public void mapPackOptLvl2(PackOptConstraintResponseDTO packOptConstraintResponseDTO, PackOptimizationResponse response, Integer finelineNbr) {
        if (response.getPlanId() == null) {
            response.setPlanId(packOptConstraintResponseDTO.getPlanId());
        }
        if (response.getLvl0Nbr() == null) {
            response.setLvl0Nbr(packOptConstraintResponseDTO.getLvl0Nbr());
            response.setLvl0Desc(packOptConstraintResponseDTO.getLvl0Desc());
        }
        if (response.getLvl1Nbr() == null) {
            response.setLvl1Nbr(packOptConstraintResponseDTO.getLvl1Nbr());
            response.setLvl1Desc(packOptConstraintResponseDTO.getLvl1Desc());
        }
        if (response.getLvl2Nbr() == null) {
            response.setLvl2Nbr(packOptConstraintResponseDTO.getLvl2Nbr());
            response.setLvl2Desc(packOptConstraintResponseDTO.getLvl2Desc());
        }
        if (response.getChannel() == null) {
            response.setChannel(ChannelType.getChannelNameFromId(packOptConstraintResponseDTO.getChannelId()));
        }
        response.setLvl3List(mapPackOptLvl3(packOptConstraintResponseDTO, response, finelineNbr));
    }

    private List<Lvl3> mapPackOptLvl3(PackOptConstraintResponseDTO packOptConstraintResponseDTO, PackOptimizationResponse response, Integer finelineNbr) {
        List<Lvl3> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> packOptConstraintResponseDTO.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(mapPackOptLvl4Sp(packOptConstraintResponseDTO, lvl3, finelineNbr)),
                        () -> setPackOptLvl3(packOptConstraintResponseDTO, lvl3List, finelineNbr));
        return lvl3List;
    }

    private void setPackOptLvl3(PackOptConstraintResponseDTO packOptConstraintResponseDTO, List<Lvl3> lvl3List, Integer finelineNbr) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl3Nbr(packOptConstraintResponseDTO.getLvl3Nbr());
        lvl3.setLvl3Name(packOptConstraintResponseDTO.getLvl3Desc());
        lvl3.setLvl4List(mapPackOptLvl4Sp(packOptConstraintResponseDTO, lvl3, finelineNbr));
        lvl3List.add(lvl3);
    }

    private List<Lvl4> mapPackOptLvl4Sp(PackOptConstraintResponseDTO packOptConstraintResponseDTO, Lvl3 lvl3, Integer finelineNbr) {
        List<Lvl4> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> packOptConstraintResponseDTO.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(mapPackOptFinelines(packOptConstraintResponseDTO, lvl4, finelineNbr)),
                        () -> setPackoptLvl4(packOptConstraintResponseDTO, lvl4DtoList, finelineNbr));
        return lvl4DtoList;
    }

    private void setPackoptLvl4(PackOptConstraintResponseDTO packOptConstraintResponseDTO, List<Lvl4> lvl4DtoList, Integer finelineNbr) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(packOptConstraintResponseDTO.getLvl4Nbr());
        lvl4.setLvl4Name(packOptConstraintResponseDTO.getLvl4Desc());
        lvl4.setFinelines(mapPackOptFinelines(packOptConstraintResponseDTO, lvl4, finelineNbr));
        lvl4DtoList.add(lvl4);
    }

    private List<Fineline> mapPackOptFinelines(PackOptConstraintResponseDTO packOptConstraintResponseDTO, Lvl4 lvl4, Integer finelineNbr) {
        List<Fineline> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> packOptConstraintResponseDTO.getFinelineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> {
                            if (finelineNbr != null) {
                                finelineDto.setStyles(mapPackOptStyles(packOptConstraintResponseDTO, finelineDto));
                            }
                        },
                        () -> setPackOptFineline(packOptConstraintResponseDTO, finelineDtoList, finelineNbr));
        return finelineDtoList;
    }

    private void setPackOptFineline(PackOptConstraintResponseDTO packOptConstraintResponseDTO, List<Fineline> finelineDtoList, Integer finelineNbr) {
        Fineline fineline = new Fineline();
        fineline.setFinelineNbr(packOptConstraintResponseDTO.getFinelineNbr());
        fineline.setFinelineName(packOptConstraintResponseDTO.getFinelineDesc());
        fineline.setAltFinelineName(packOptConstraintResponseDTO.getAltfinelineDesc());
        if (finelineNbr != null) {

            fineline.setStyles(mapPackOptStyles(packOptConstraintResponseDTO, fineline));
        }
        finelineDtoList.add(fineline);
    }

    private List<Style> mapPackOptStyles(PackOptConstraintResponseDTO packOptConstraintResponseDTO, Fineline fineline) {
        List<Style> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

        styleDtoList.stream()
                .filter(styleDto -> packOptConstraintResponseDTO.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(style ->
                            style.setCustomerChoices(mapPackOptCc(packOptConstraintResponseDTO, style)),
                        () -> setPackOptStyle(packOptConstraintResponseDTO, styleDtoList));
        return styleDtoList;
    }


    private void setPackOptStyle(PackOptConstraintResponseDTO packOptConstraintResponseDTO, List<Style> styleDtoList) {

        Style styleDto = new Style();
        styleDto.setStyleNbr(packOptConstraintResponseDTO.getStyleNbr());
        styleDto.setConstraints(setConstraints(packOptConstraintResponseDTO.getStyleSupplierName(),
                packOptConstraintResponseDTO.getStyleFactoryIds(), packOptConstraintResponseDTO.getStyleCountryOfOrigin(),
                packOptConstraintResponseDTO.getStylePortOfOrigin(), packOptConstraintResponseDTO.getStyleSinglePackIndicator(),
                packOptConstraintResponseDTO.getStyleColorCombination()));
        styleDto.setCustomerChoices(mapPackOptCc(packOptConstraintResponseDTO, styleDto));
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoice> mapPackOptCc(PackOptConstraintResponseDTO packOptConstraintResponseDTO, Style styleDto) {
        List<CustomerChoice> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());

        customerChoiceList.stream()
                .filter(customerChoiceDto -> packOptConstraintResponseDTO.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                        },
                        () -> setPackOptCc(packOptConstraintResponseDTO, customerChoiceList));
        return customerChoiceList;

    }

    private void setPackOptCc(PackOptConstraintResponseDTO packOptConstraintResponseDTO, List<CustomerChoice> customerChoiceDtoList) {

        CustomerChoice customerChoiceDto = new CustomerChoice();
        customerChoiceDto.setCcId(packOptConstraintResponseDTO.getCcId());
        customerChoiceDto.setConstraints(setConstraints(packOptConstraintResponseDTO.getCcSupplierName(), packOptConstraintResponseDTO.getCcFactoryIds(),
                packOptConstraintResponseDTO.getCcCountryOfOrigin(), packOptConstraintResponseDTO.getCcPortOfOrigin(), packOptConstraintResponseDTO.getCcSinglePackIndicator(),
                packOptConstraintResponseDTO.getCcColorCombination()));
        customerChoiceDtoList.add(customerChoiceDto);
    }

    private Constraints setConstraints(String vendorName, String factoryId, String originCountryName,
                                       String portOfOriginName, Integer singlePackInd, String colorCombination) {
        Constraints constraints = new Constraints();
        constraints.setColorCombinationConstraints(new ColorCombinationConstraints(vendorName,factoryId,
                originCountryName,portOfOriginName,getBoolenValueByInt(singlePackInd),colorCombination));
        return constraints;
    }
    Boolean getBoolenValueByInt(Integer number)
    {
        if(number>0)
        {
            return true;
        }
        return false;
    }
}
