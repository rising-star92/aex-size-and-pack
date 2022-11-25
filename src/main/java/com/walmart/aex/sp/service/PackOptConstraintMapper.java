package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.enums.CategoryType;
import com.walmart.aex.sp.enums.ChannelType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PackOptConstraintMapper {

    public PackOptimizationResponse packOptDetails(
            List<FineLineMapperDto> fineLineMapperDtos) {

        PackOptimizationResponse packOptResp = new PackOptimizationResponse();
        fineLineMapperDtos = updateSupplierNames(fineLineMapperDtos);
        Optional.of(fineLineMapperDtos)
                .stream()
                .flatMap(Collection::stream)
                .forEach(fineLineMapperDto -> mapPackOptLvl2(fineLineMapperDto, packOptResp));

        return packOptResp;
    }

    private List<FineLineMapperDto> updateSupplierNames(List<FineLineMapperDto> finePlanPackOptimizationList) {
        List<FineLineMapperDto> fineLineMapperDtoList;
        fineLineMapperDtoList = updateFineLineLevelSuppliers(finePlanPackOptimizationList);
        fineLineMapperDtoList = updateStyleLevelSuppliers(fineLineMapperDtoList);
        return fineLineMapperDtoList;
    }

    private List<FineLineMapperDto> updateFineLineLevelSuppliers(List<FineLineMapperDto> finePlanPackOptimizationList) {
        Map<Integer, Set<String>> fineLineSupplierMap = new LinkedHashMap<>();
        List<FineLineMapperDto> fineLineMapperDtoList = new ArrayList<>();

        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            fineLineSupplierMap.putIfAbsent(fineLineMapperDto.getFineLineNbr(), new LinkedHashSet<>());
            fineLineSupplierMap.computeIfPresent(fineLineMapperDto.getFineLineNbr(), (k, v) -> {
                if(StringUtils.isNotEmpty(fineLineMapperDto.getCcSupplierName())) {
                    v.add(fineLineMapperDto.getCcSupplierName());
                }
                return v;
            });
        });

        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            if(fineLineSupplierMap.containsKey(fineLineMapperDto.getFineLineNbr())) {
                StringBuilder sb = new StringBuilder();
                fineLineSupplierMap.get(fineLineMapperDto.getFineLineNbr()).forEach(val -> sb.append(val).append(", "));
                fineLineMapperDto.setFineLineSupplierName(sb.toString().isEmpty() ? "" : sb.substring(0, sb.toString().length() - 2));
                fineLineMapperDtoList.add(fineLineMapperDto);
            }
        });
        return fineLineMapperDtoList;
    }

    private List<FineLineMapperDto> updateStyleLevelSuppliers(List<FineLineMapperDto> finePlanPackOptimizationList) {
        Map<String, Set<String>> styleSupplierMap = new LinkedHashMap<>();
        List<FineLineMapperDto> fineLineMapperDtoList = new ArrayList<>();

        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            styleSupplierMap.putIfAbsent(fineLineMapperDto.getStyleNbr(), new LinkedHashSet<>());
            styleSupplierMap.computeIfPresent(fineLineMapperDto.getStyleNbr(), (k, v) -> {
                if(StringUtils.isNotEmpty(fineLineMapperDto.getCcSupplierName())) {
                    v.add(fineLineMapperDto.getCcSupplierName());
                }
                return v;
            });
        });
        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            if(styleSupplierMap.containsKey(fineLineMapperDto.getStyleNbr())) {
                StringBuilder sb = new StringBuilder();
                styleSupplierMap.get(fineLineMapperDto.getStyleNbr()).forEach(val -> sb.append(val).append(", "));
                fineLineMapperDto.setStyleSupplierName(sb.toString().isEmpty() ? "" : sb.substring(0, sb.toString().length() - 2));
                fineLineMapperDtoList.add(fineLineMapperDto);
            }
        });
        return fineLineMapperDtoList;
    }

    private void mapPackOptLvl2(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response) {
        if (response.getPlanId() == null) {
            response.setPlanId(fineLineMapperDto.getPlanId());
        }
        if (response.getLvl0Nbr() == null) {
            response.setLvl0Nbr(fineLineMapperDto.getLvl0Nbr());
            response.setLvl0Desc(fineLineMapperDto.getLvl0Desc());
        }
        if (response.getLvl1Nbr() == null) {
            response.setLvl1Nbr(fineLineMapperDto.getLvl1Nbr());
            response.setLvl1Desc(fineLineMapperDto.getLvl1Desc());
        }
        if (response.getLvl2Nbr() == null) {
            response.setLvl2Nbr(fineLineMapperDto.getLvl2Nbr());
            response.setLvl2Desc(fineLineMapperDto.getLvl2Desc());
        }
        if (response.getChannel() == null) {
            response.setChannel(ChannelType.getChannelNameFromId(fineLineMapperDto.getChannelId()));
        }
        response.setLvl3List(mapPackOptLvl3(fineLineMapperDto, response));
    }

    private List<Lvl3> mapPackOptLvl3(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response) {
        List<Lvl3> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

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
        List<Lvl4> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

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
        List<Fineline> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> finelineDto.getFinelineNbr() != null && fineLineMapperDto.getFineLineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> {
                            if (finelineDto.getOptimizationDetails() != null &&
                                    !finelineDto.getOptimizationDetails().isEmpty()
                                    && finelineDto.getOptimizationDetails().get(0).getStartTs() != null
                                    && finelineDto.getOptimizationDetails().get(0).getStartTs()
                                    .compareTo(fineLineMapperDto.getStartTs()) < 0) {
                                finelineDtoList.remove(finelineDto);
                                setPackOptFineLine(fineLineMapperDto, finelineDtoList);
                            }
                            if (fineLineMapperDto.getFineLineNbr() != null) {
                                finelineDto.setStyles(getPackOptStyles(fineLineMapperDto, finelineDto));
                            }
                        },
                        () -> setPackOptFineLine(fineLineMapperDto, finelineDtoList));
        return finelineDtoList;
    }

    private void setPackOptFineLine(FineLineMapperDto fineLineMapperDto, List<Fineline> finelineDtoList) {
        Fineline fineline = new Fineline();
        String status = Optional.ofNullable(fineLineMapperDto.getRunStatusDesc()).orElse("NOT SENT");
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setFinelineName(fineLineMapperDto.getFineLineDesc());
        fineline.setAltFinelineName(fineLineMapperDto.getAltfineLineDesc());
        fineline.setPackOptimizationStatus(status);
        fineline.setOptimizationDetails(getRunOptimizationDetails(fineLineMapperDto));
        fineline.setConstraints(getConstraints(fineLineMapperDto, CategoryType.FINE_LINE));
        if (fineLineMapperDto.getFineLineNbr() != null) {
            fineline.setStyles(getPackOptStyles(fineLineMapperDto, fineline));
        }
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

    private List<Style> getPackOptStyles(FineLineMapperDto fineLineMapperDto, Fineline fineline) {
        List<Style> styleDtoList = Optional.ofNullable(fineline.getStyles()).orElse(new ArrayList<>());

        styleDtoList.stream()
                .filter(styleDto -> styleDto.getStyleNbr() != null && fineLineMapperDto.getStyleNbr().equals(styleDto.getStyleNbr())).findFirst()
                .ifPresentOrElse(style ->
                                style.setCustomerChoices(getPackOptCustomerChoice(fineLineMapperDto, style)),
                        () -> setPackOptStyle(fineLineMapperDto, styleDtoList));
        return styleDtoList;
    }


    private void setPackOptStyle(FineLineMapperDto fineLineMapperDto, List<Style> styleDtoList) {

        Style styleDto = new Style();
        styleDto.setStyleNbr(fineLineMapperDto.getStyleNbr());
        styleDto.setConstraints(getConstraints(fineLineMapperDto, CategoryType.STYLE));
        styleDto.setCustomerChoices(getPackOptCustomerChoice(fineLineMapperDto, styleDto));
        styleDtoList.add(styleDto);
    }

    private List<CustomerChoice> getPackOptCustomerChoice(FineLineMapperDto fineLineMapperDto, Style styleDto) {
        List<CustomerChoice> customerChoiceList = Optional.ofNullable(styleDto.getCustomerChoices()).orElse(new ArrayList<>());

        customerChoiceList.stream()
                .filter(customerChoiceDto -> customerChoiceDto.getCcId() != null && fineLineMapperDto.getCcId().equals(customerChoiceDto.getCcId())).findFirst()
                .ifPresentOrElse(customerChoiceDto -> {
                        },
                        () -> setPackOptCc(fineLineMapperDto, customerChoiceList));
        return customerChoiceList;

    }

    private void setPackOptCc(FineLineMapperDto fineLineMapperDto, List<CustomerChoice> customerChoiceDtoList) {

        CustomerChoice customerChoiceDto = new CustomerChoice();
        customerChoiceDto.setCcId(fineLineMapperDto.getCcId());
        customerChoiceDto.setConstraints(getConstraints(fineLineMapperDto, CategoryType.CUSTOMER_CHOICE));
        customerChoiceDtoList.add(customerChoiceDto);
    }

    private Constraints getConstraints(FineLineMapperDto fineLineMapperDto, CategoryType type) {

        Constraints constraints = new Constraints();
        Supplier supplier = new Supplier();
        switch (type) {
            case MERCHANT:
                supplier.setSupplierName(fineLineMapperDto.getMerchSupplierName());
                supplier.setSupplierId(fineLineMapperDto.getMerchSupplierNumber9());
                supplier.setSupplierNumber(fineLineMapperDto.getMerchSupplierNumber6());
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getMerchMaxNbrOfPacks(),fineLineMapperDto.getMerchMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(supplier, fineLineMapperDto.getMerchFactoryId(),
                        fineLineMapperDto.getMerchOriginCountryName(), fineLineMapperDto.getMerchPortOfOriginName(),
                        fineLineMapperDto.getMerchSinglePackInd(), fineLineMapperDto.getMerchColorCombination()));
                break;
            case SUB_CATEGORY:
                supplier.setSupplierName(fineLineMapperDto.getSubCatSupplierName());
                supplier.setSupplierId(fineLineMapperDto.getSubCatSupplierNumber9());
                supplier.setSupplierNumber(fineLineMapperDto.getSubCatSupplierNumber6());
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getSubCatMaxNbrOfPacks(),fineLineMapperDto.getSubCatMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(supplier, fineLineMapperDto.getSubCatFactoryId(),
                        fineLineMapperDto.getSubCatOriginCountryName(), fineLineMapperDto.getSubCatPortOfOriginName(),
                        fineLineMapperDto.getSubCatSinglePackInd(), fineLineMapperDto.getSubCatColorCombination()));
                break;
            case FINE_LINE:
                supplier.setSupplierName(fineLineMapperDto.getFineLineSupplierName());
                supplier.setSupplierId(fineLineMapperDto.getFineLineSupplierNumber9());
                supplier.setSupplierNumber(fineLineMapperDto.getFineLineSupplierNumber6());
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getFineLineMaxNbrOfPacks(),fineLineMapperDto.getFineLineMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(supplier, fineLineMapperDto.getFineLineFactoryId(),
                        fineLineMapperDto.getFineLineOriginCountryName(), fineLineMapperDto.getFineLinePortOfOriginName(),
                        fineLineMapperDto.getFineLineSinglePackInd(), fineLineMapperDto.getFineLineColorCombination()));
                break;
            case STYLE:
                supplier.setSupplierName(fineLineMapperDto.getStyleSupplierName());
                supplier.setSupplierId(fineLineMapperDto.getStyleSupplierNumber9());
                supplier.setSupplierNumber(fineLineMapperDto.getStyleSupplierNumber6());
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getStyleMaxPacks(),fineLineMapperDto.getStyleMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(supplier, fineLineMapperDto.getStyleFactoryIds(),
                        fineLineMapperDto.getStyleCountryOfOrigin(), fineLineMapperDto.getStylePortOfOrigin(),
                        fineLineMapperDto.getStyleSinglePackIndicator(), fineLineMapperDto.getStyleColorCombination()));
                break;
            default:
                supplier.setSupplierName(fineLineMapperDto.getCcSupplierName());
                supplier.setSupplierId(fineLineMapperDto.getCcSupplierNumber9());
                supplier.setSupplierNumber(fineLineMapperDto.getCcSupplierNumber6());
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getCcMaxPacks(),fineLineMapperDto.getCcMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(supplier, fineLineMapperDto.getCcFactoryIds(),
                        fineLineMapperDto.getCcCountryOfOrigin(), fineLineMapperDto.getCcPortOfOrigin(),
                        fineLineMapperDto.getCcSinglePackIndicator(), fineLineMapperDto.getCcColorCombination()));
                break;
        }
        return constraints;

    }
}
