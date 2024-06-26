package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.mapper.FineLineMapperDto;
import com.walmart.aex.sp.dto.packoptimization.ColorCombinationConstraints;
import com.walmart.aex.sp.dto.packoptimization.Constraints;
import com.walmart.aex.sp.dto.packoptimization.CustomerChoice;
import com.walmart.aex.sp.dto.packoptimization.Fineline;
import com.walmart.aex.sp.dto.packoptimization.FinelineLevelConstraints;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.RunOptimization;
import com.walmart.aex.sp.dto.packoptimization.Supplier;
import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import com.walmart.aex.sp.dto.planhierarchy.Lvl4;
import com.walmart.aex.sp.dto.planhierarchy.Style;
import com.walmart.aex.sp.enums.CategoryType;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.service.helper.PackOptConstraintMapperHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class PackOptConstraintMapper {

    Map<String, Set<Supplier>> supplierMap;

    @Autowired
    PackOptConstraintMapperHelper packOptimizationUtil;

    public PackOptimizationResponse packOptDetails(
            List<FineLineMapperDto> fineLineMapperDtos) {

        PackOptimizationResponse packOptResp = new PackOptimizationResponse();
        fineLineMapperDtos = updateSupplierNames(fineLineMapperDtos);
        Map<Integer, Map<Integer, String>> finelineBumpStatusMap = new HashMap<>();
        Optional.of(fineLineMapperDtos)
                .stream()
                .flatMap(Collection::stream)
                .forEach(fineLineMapperDto -> mapPackOptLvl2(fineLineMapperDto, packOptResp, finelineBumpStatusMap));

        return packOptResp;
    }

    private List<FineLineMapperDto> updateSupplierNames(List<FineLineMapperDto> finePlanPackOptimizationList) {
        supplierMap = new LinkedHashMap<>();
        List<FineLineMapperDto> fineLineMapperDtoList;
        fineLineMapperDtoList = updateFineLineLevelSuppliers(finePlanPackOptimizationList);
        if (fineLineMapperDtoList.stream().anyMatch(fineLineMapperDto -> fineLineMapperDto.getStyleNbr() != null))
            fineLineMapperDtoList = updateStyleLevelSuppliers(fineLineMapperDtoList);
        return fineLineMapperDtoList;
    }

    private List<FineLineMapperDto> updateFineLineLevelSuppliers(List<FineLineMapperDto> finePlanPackOptimizationList) {
        Map<Integer, Set<String>> fineLinePortOfOrigin = new LinkedHashMap<>();
        Map<Integer, Set<String>> fineLineFactoryId = new LinkedHashMap<>();
        List<FineLineMapperDto> fineLineMapperDtoList = new ArrayList<>();

        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            supplierMap.putIfAbsent(fineLineMapperDto.getFineLineNbr().toString(), new LinkedHashSet<>());
            fineLinePortOfOrigin.putIfAbsent(fineLineMapperDto.getFineLineNbr(), new LinkedHashSet<>());
            fineLineFactoryId.putIfAbsent(fineLineMapperDto.getFineLineNbr(), new LinkedHashSet<>());

            if(StringUtils.isNotEmpty(fineLineMapperDto.getCcSupplierName())) {
                addSuppliers(fineLineMapperDto.getFineLineNbr().toString(), fineLineMapperDto);
            }
            if(StringUtils.isNotEmpty(fineLineMapperDto.getCcPortOfOrigin())) {
                addValue(fineLinePortOfOrigin, fineLineMapperDto.getFineLineNbr(), fineLineMapperDto.getCcPortOfOrigin());
            }
            if(StringUtils.isNotEmpty(fineLineMapperDto.getCcFactoryId())) {
                String newFactoryId = getFactoryId(fineLineMapperDto);
                addValue(fineLineFactoryId, fineLineMapperDto.getFineLineNbr(), newFactoryId);
            }

        });

        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            fineLineMapperDto.setFineLinePortOfOriginName(prepareConcatString(fineLinePortOfOrigin, fineLineMapperDto.getFineLineNbr()));
            fineLineMapperDto.setFineLineFactoryId(prepareConcatString(fineLineFactoryId, fineLineMapperDto.getFineLineNbr()));
            fineLineMapperDtoList.add(fineLineMapperDto);
        });
        return fineLineMapperDtoList;
    }

    private String getFactoryId(FineLineMapperDto fineLineMapperDto) {
        StringBuilder sb = new StringBuilder();
        if(StringUtils.isNotEmpty(fineLineMapperDto.getCcFactoryId()) && StringUtils.isNotEmpty(fineLineMapperDto.getCcFactoryName())) {
            sb.append(fineLineMapperDto.getCcFactoryId()).append(" - ").append(fineLineMapperDto.getCcFactoryName());
        } else if(StringUtils.isNotEmpty(fineLineMapperDto.getCcFactoryId())) {
            sb.append(fineLineMapperDto.getCcFactoryId());
        }
        return sb.toString();
    }

    private static String prepareConcatString(Map<Integer, Set<String>> map, Integer fineLineNbr) {
        StringBuilder sb = new StringBuilder();
        map.get(fineLineNbr).forEach(val -> sb.append(val).append(", "));
        return sb.toString().isEmpty() ? "" : sb.substring(0, sb.toString().length() - 2);
    }

    private static String prepareConcatString(Map<String, Set<String>> map, String styleNumber) {
        StringBuilder sb = new StringBuilder();
        map.get(styleNumber).forEach(val -> sb.append(val).append(", "));
        return sb.toString().isEmpty() ? "" : sb.substring(0, sb.toString().length() - 2);
    }

    private static void addValue(Map<Integer, Set<String>> map, Integer key, String value) {
        map.computeIfPresent(key, (k, v) -> {
            v.add(value);
            return v;
        });
    }

    private static void addValue(Map<String, Set<String>> map, String key, String value) {
        map.computeIfPresent(key, (k, v) -> {
            v.add(value);
            return v;
        });
    }

    private void addSuppliers(String key, FineLineMapperDto fineLineMapperDto) {
        supplierMap.computeIfPresent(key, (k, v) -> {
            v.add(new Supplier(fineLineMapperDto.getCcVendorNumber6(),  fineLineMapperDto.getCcGsmSupplierNumber(), fineLineMapperDto.getCcSupplierName(), null, fineLineMapperDto.getCcVendorNumber9()));
            return v;
        });
    }

    private List<FineLineMapperDto> updateStyleLevelSuppliers(List<FineLineMapperDto> finePlanPackOptimizationList) {
        Map<String, Set<String>> stylePortOfOrigin = new LinkedHashMap<>();
        Map<String, Set<String>> styleFactoryId  = new LinkedHashMap<>();

        List<FineLineMapperDto> fineLineMapperDtoList = new ArrayList<>();

        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            supplierMap.putIfAbsent(fineLineMapperDto.getStyleNbr(), new LinkedHashSet<>());
            stylePortOfOrigin.putIfAbsent(fineLineMapperDto.getStyleNbr(), new LinkedHashSet<>());
            styleFactoryId.putIfAbsent(fineLineMapperDto.getStyleNbr(), new LinkedHashSet<>());

            if(StringUtils.isNotEmpty(fineLineMapperDto.getCcSupplierName())) {
                addSuppliers(fineLineMapperDto.getStyleNbr(), fineLineMapperDto);
            }
            if(StringUtils.isNotEmpty(fineLineMapperDto.getCcPortOfOrigin())) {
                addValue(stylePortOfOrigin, fineLineMapperDto.getStyleNbr(), fineLineMapperDto.getCcPortOfOrigin());
            }
            if(StringUtils.isNotEmpty(fineLineMapperDto.getCcFactoryId())) {
                String newFactoryId = getFactoryId(fineLineMapperDto);
                addValue(styleFactoryId, fineLineMapperDto.getStyleNbr(), newFactoryId);
            }

        });
        finePlanPackOptimizationList.forEach(fineLineMapperDto -> {
            fineLineMapperDto.setStylePortOfOrigin(prepareConcatString(stylePortOfOrigin, fineLineMapperDto.getStyleNbr()));
            fineLineMapperDto.setStyleFactoryIds(prepareConcatString(styleFactoryId, fineLineMapperDto.getStyleNbr()));
            fineLineMapperDtoList.add(fineLineMapperDto);
        });
        return fineLineMapperDtoList;
    }

    private void mapPackOptLvl2(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
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
        response.setLvl3List(mapPackOptLvl3(fineLineMapperDto, response, finelineBumpStatusMap));
    }

    private List<Lvl3> mapPackOptLvl3(FineLineMapperDto fineLineMapperDto, PackOptimizationResponse response, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        List<Lvl3> lvl3List = Optional.ofNullable(response.getLvl3List()).orElse(new ArrayList<>());

        lvl3List.stream()
                .filter(lvl3 -> lvl3.getLvl3Nbr() != null && fineLineMapperDto.getLvl3Nbr().equals(lvl3.getLvl3Nbr())).findFirst()
                .ifPresentOrElse(lvl3 -> lvl3.setLvl4List(getLvl4Sp(fineLineMapperDto, lvl3, finelineBumpStatusMap)),
                        () -> setPackOptLvl3(fineLineMapperDto, lvl3List, finelineBumpStatusMap));
        return lvl3List;
    }

    private void setPackOptLvl3(FineLineMapperDto fineLineMapperDto, List<Lvl3> lvl3List, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        Lvl3 lvl3 = new Lvl3();
        lvl3.setLvl0Nbr(fineLineMapperDto.getLvl0Nbr());
        lvl3.setLvl1Nbr(fineLineMapperDto.getLvl1Nbr());
        lvl3.setLvl2Nbr(fineLineMapperDto.getLvl2Nbr());
        lvl3.setLvl3Nbr(fineLineMapperDto.getLvl3Nbr());
        lvl3.setLvl3Name(fineLineMapperDto.getLvl3Desc());
        lvl3.setLvl4List(getLvl4Sp(fineLineMapperDto, lvl3, finelineBumpStatusMap));
        lvl3.setConstraints(getConstraints(fineLineMapperDto, CategoryType.MERCHANT));
        lvl3List.add(lvl3);
    }

    private List<Lvl4> getLvl4Sp(FineLineMapperDto fineLineMapperDto, Lvl3 lvl3, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        List<Lvl4> lvl4DtoList = Optional.ofNullable(lvl3.getLvl4List()).orElse(new ArrayList<>());

        lvl4DtoList.stream()
                .filter(lvl4 -> lvl4.getLvl4Nbr() != null && fineLineMapperDto.getLvl4Nbr().equals(lvl4.getLvl4Nbr())).findFirst()
                .ifPresentOrElse(lvl4 -> lvl4.setFinelines(getFineLines(fineLineMapperDto, lvl4, finelineBumpStatusMap)),
                        () -> setPackoptLvl4(fineLineMapperDto, lvl4DtoList, finelineBumpStatusMap));
        return lvl4DtoList;
    }

    private void setPackoptLvl4(FineLineMapperDto fineLineMapperDto, List<Lvl4> lvl4DtoList, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        Lvl4 lvl4 = new Lvl4();
        lvl4.setLvl4Nbr(fineLineMapperDto.getLvl4Nbr());
        lvl4.setLvl4Name(fineLineMapperDto.getLvl4Desc());
        lvl4.setFinelines(getFineLines(fineLineMapperDto, lvl4, finelineBumpStatusMap));
        lvl4.setConstraints(getConstraints(fineLineMapperDto, CategoryType.SUB_CATEGORY));
        lvl4DtoList.add(lvl4);
    }

    private List<Fineline> getFineLines(FineLineMapperDto fineLineMapperDto, Lvl4 lvl4, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        List<Fineline> finelineDtoList = Optional.ofNullable(lvl4.getFinelines()).orElse(new ArrayList<>());

        finelineDtoList.stream()
                .filter(finelineDto -> finelineDto.getFinelineNbr() != null && fineLineMapperDto.getFineLineNbr().equals(finelineDto.getFinelineNbr())).findFirst()
                .ifPresentOrElse(finelineDto -> updatePackOptFineLine(fineLineMapperDto, finelineDto, finelineBumpStatusMap),
                        () -> setPackOptFineLine(fineLineMapperDto, finelineDtoList, finelineBumpStatusMap));
        return finelineDtoList;
    }

    private void setPackOptFineLine(FineLineMapperDto fineLineMapperDto, List<Fineline> finelineDtoList, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        Fineline fineline = new Fineline();
        String status = Optional.ofNullable(fineLineMapperDto.getRunStatusDesc()).orElse("NOT SENT");
        fineline.setFinelineNbr(fineLineMapperDto.getFineLineNbr());
        fineline.setFinelineName(fineLineMapperDto.getFineLineDesc());
        fineline.setAltFinelineName(fineLineMapperDto.getAltfineLineDesc());
        fineline.setPackOptimizationStatus(status);
        fineline.setOptimizationDetails(getRunOptimizationDetails(fineLineMapperDto, finelineBumpStatusMap));
        fineline.setConstraints(getConstraints(fineLineMapperDto, CategoryType.FINE_LINE));
        if (fineLineMapperDto.getFineLineNbr() != null) {
            fineline.setStyles(getPackOptStyles(fineLineMapperDto, fineline));
        }
        finelineDtoList.add(fineline);
    }

    private void updatePackOptFineLine(FineLineMapperDto fineLineMapperDto, Fineline fineline, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        List<String> runStatusLongDescriptions = packOptimizationUtil.getRunStatusLongDescriptions(fineLineMapperDto, finelineBumpStatusMap);
        fineline.getOptimizationDetails().setRunStatusLongDesc(runStatusLongDescriptions);
        if (fineLineMapperDto.getFineLineNbr() != null) {
            fineline.setStyles(getPackOptStyles(fineLineMapperDto, fineline));
        }
    }

    private RunOptimization getRunOptimizationDetails(FineLineMapperDto fineLineMapperDto, Map<Integer, Map<Integer, String>> finelineBumpStatusMap) {
        List<String> runStatusLongDescriptions = packOptimizationUtil.getRunStatusLongDescriptions(fineLineMapperDto, finelineBumpStatusMap);
        RunOptimization opt = new RunOptimization();
        opt.setName(fineLineMapperDto.getFirstName());
        opt.setRunStatusLongDesc(runStatusLongDescriptions);
        opt.setRunStatusCode(fineLineMapperDto.getRunStatusCode());
        opt.setStartTs(fineLineMapperDto.getStartTs());
        return opt;
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
        styleDto.setAltStyleDesc(fineLineMapperDto.getAltStyleDesc());
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
        customerChoiceDto.setAltCcDesc(fineLineMapperDto.getAltCcDesc());
        customerChoiceDto.setColorName(fineLineMapperDto.getColorName());
        customerChoiceDto.setColorFamily(fineLineMapperDto.getColorFamilyDesc());
        customerChoiceDto.setConstraints(getConstraints(fineLineMapperDto, CategoryType.CUSTOMER_CHOICE));
        customerChoiceDtoList.add(customerChoiceDto);
    }

    private Constraints getConstraints(FineLineMapperDto fineLineMapperDto, CategoryType type) {

        Constraints constraints = new Constraints();
        List<Supplier> suppliers = new ArrayList<>();
        switch (type) {
            case MERCHANT:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getMerchMaxNbrOfPacks(),fineLineMapperDto.getMerchMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(suppliers, fineLineMapperDto.getMerchFactoryId(), fineLineMapperDto.getMerchPortOfOriginName(),
                        fineLineMapperDto.getMerchSinglePackInd(), fineLineMapperDto.getMerchColorCombination()));
                break;
            case SUB_CATEGORY:
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getSubCatMaxNbrOfPacks(),fineLineMapperDto.getSubCatMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(suppliers, fineLineMapperDto.getSubCatFactoryId(), fineLineMapperDto.getSubCatPortOfOriginName(),
                        fineLineMapperDto.getSubCatSinglePackInd(), fineLineMapperDto.getSubCatColorCombination()));
                break;
            case FINE_LINE:
                suppliers.addAll(supplierMap.get(fineLineMapperDto.getFineLineNbr().toString()));
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getFineLineMaxNbrOfPacks(),fineLineMapperDto.getFineLineMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(suppliers, fineLineMapperDto.getFineLineFactoryId(), fineLineMapperDto.getFineLinePortOfOriginName(),
                        fineLineMapperDto.getFineLineSinglePackInd(), fineLineMapperDto.getFineLineColorCombination()));
                break;
            case STYLE:
                if (supplierMap.containsKey(fineLineMapperDto.getStyleNbr()))
                    suppliers.addAll(supplierMap.get(fineLineMapperDto.getStyleNbr()));
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getStyleMaxPacks(),fineLineMapperDto.getStyleMaxUnitsPerPack()));
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(suppliers, fineLineMapperDto.getStyleFactoryIds(), fineLineMapperDto.getStylePortOfOrigin(),
                        fineLineMapperDto.getStyleSinglePackIndicator(), fineLineMapperDto.getStyleColorCombination()));
                break;
            default:
                suppliers.add(new Supplier(fineLineMapperDto.getCcVendorNumber6(), fineLineMapperDto.getCcGsmSupplierNumber(), fineLineMapperDto.getCcSupplierName(), null, fineLineMapperDto.getCcVendorNumber9()));
                constraints.setFinelineLevelConstraints(new FinelineLevelConstraints(fineLineMapperDto.getCcMaxPacks(),fineLineMapperDto.getCcMaxUnitsPerPack()));
                String newFactoryId = getFactoryId(fineLineMapperDto);
                constraints.setColorCombinationConstraints(new ColorCombinationConstraints(suppliers, newFactoryId, fineLineMapperDto.getCcPortOfOrigin(),
                        fineLineMapperDto.getCcSinglePackIndicator(), fineLineMapperDto.getCcColorCombination()));
                break;
        }
        return constraints;

    }
}
