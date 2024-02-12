package com.walmart.aex.sp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.dto.replenishment.cons.ReplenishmentCons;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class BuyQtyReplenishmentMapperService {

    private final ObjectMapper objectMapper;
    private final ReplenishmentService replenishmentService;
    private final AppMessageTextService appMessageTextService;

    public BuyQtyReplenishmentMapperService(ObjectMapper objectMapper, ReplenishmentService replenishmentService, AppMessageTextService appMessageTextService) {
        this.objectMapper = objectMapper;
        this.replenishmentService = replenishmentService;
        this.appMessageTextService = appMessageTextService;
    }

    public List<MerchCatgReplPack> setAllReplenishments(StyleDto styleDto, MerchMethodsDto merchMethodsDto,
                                                        CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest,
                                                        CalculateBuyQtyResponse calculateBuyQtyResponse,
                                                        CustomerChoiceDto customerChoiceDto,
                                                        Set<CcSpMmReplPack> ccSpMmReplPacks,
                                                        ReplenishmentCons replenishmentCons,
                                                        ValidationResult ccValidationResult) {
        Set<MerchCatgReplPack> merchCatgReplPacks = new HashSet<>(calculateBuyQtyResponse.getMerchCatgReplPacks());
        // Hard coded FixtureTypeRollUpId for testing calculation
        MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId(calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getLvl0Nbr(),
                calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(),
                ChannelType.getChannelIdFromName(calculateBuyQtyParallelRequest.getChannel()), merchMethodsDto.getMerchMethodCode());
        log.info("Replenishment: Check if merch catg pack Id is existing: {}", merchCatgReplPackId);
        MerchCatgReplPack merchCatgReplPack = setMerchCatgReplPack(merchCatgReplPacks, merchCatgReplPackId);

        Set<SubCatgReplPack> subCatgReplPacks = Optional.ofNullable(merchCatgReplPack.getSubReplPack()).orElse(new HashSet<>());
        SubCatgReplPackId subCatgReplPackId = new SubCatgReplPackId(merchCatgReplPackId, calculateBuyQtyParallelRequest.getLvl4Nbr());
        log.info("Replenishment: Check if sub catg pack Id is existing: {}", subCatgReplPackId);
        SubCatgReplPack subCatgReplPack = setSubCatgReplPack(subCatgReplPacks, subCatgReplPackId);

        Set<FinelineReplPack> finelineReplPacks = Optional.ofNullable(subCatgReplPack.getFinelineReplPack()).orElse(new HashSet<>());
        FinelineReplPackId finelineReplPackId = new FinelineReplPackId(subCatgReplPackId, calculateBuyQtyParallelRequest.getFinelineNbr());
        log.info("Replenishment: Check if fineline pack Id is existing: {}", finelineReplPackId);
        FinelineReplPack finelineReplPack = setFinelineReplenishment(finelineReplPacks, finelineReplPackId);

        Set<StyleReplPack> styleReplPacks = Optional.ofNullable(finelineReplPack.getStyleReplPack()).orElse(new HashSet<>());
        StyleReplPackId styleReplPackId = new StyleReplPackId(finelineReplPack.getFinelineReplPackId(), styleDto.getStyleNbr());
        log.info("Replenishment: Check if Style Repln pack Id is existing: {}", styleReplPackId);
        StyleReplPack styleReplPack = setStyleReplPack(styleReplPacks, styleReplPackId);

        Set<CcReplPack> ccReplPacks = Optional.ofNullable(styleReplPack.getCcReplPack()).orElse(new HashSet<>());
        CcReplPackId ccReplPackId = new CcReplPackId(styleReplPack.getStyleReplPackId(), customerChoiceDto.getCcId());
        log.info("Replenishment: Check if Cc Repln pack Id is existing: {}", ccReplPackId);
        CcReplPack ccReplPack = setCcReplPack(ccReplPacks, ccReplPackId);

        Set<CcMmReplPack> ccMmReplPacks = Optional.ofNullable(ccReplPack.getCcMmReplPack()).orElse(new HashSet<>());
        CcMmReplPackId ccMmReplPackId = new CcMmReplPackId(ccReplPack.getCcReplPackId(), merchMethodsDto.getMerchMethodCode());
        log.info("Replenishment: Check if Cc MM Repln pack Id is existing: {}", ccMmReplPackId);
        CcMmReplPack ccMmReplPack = setCcMmReplnPack(ccMmReplPacks, ccMmReplPackId);
        Map<Integer, CcSpMmReplPack> ccSpMmReplPackSizeMap = replenishmentCons.getCcSpMmReplPackConsMap();
        Set<Integer> sizeValidationCodes = new HashSet<>();
        ccSpMmReplPacks.forEach(ccSpMmReplPack -> {
            setReplenishmentSizeEntity(ccMmReplPack, ccSpMmReplPack, merchMethodsDto);
            ccSpMmReplPack = replenishmentService.setVendorPackAndWhsePackCountForCCSpMm(ccSpMmReplPackSizeMap, ccSpMmReplPack, replenishmentCons.getCcMmReplPackCons());
            ValidationResult validationResult = getValidationResult(ccSpMmReplPack.getMessageObj());
            sizeValidationCodes.addAll(validationResult.getCodes());
        });
        ccMmReplPack.setVendorPackCnt(replenishmentCons.getCcMmReplPackCons().getVendorPackCount());
        ccMmReplPack.setWhsePackCnt(replenishmentCons.getCcMmReplPackCons().getWarehousePackCount());
        ccMmReplPack.setVnpkWhpkRatio(replenishmentCons.getCcMmReplPackCons().getVendorPackWareHousePackRatio());

        log.info("Calculating CC MM Repln Qty");
        //Repln Units
        ccMmReplPack.setReplUnits(ccSpMmReplPacks
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(ccSpMmReplPack -> Optional.ofNullable(ccSpMmReplPack.getReplUnits()).orElse(0))
                .sum()
        );
        //Total Buy Units
        ccMmReplPack.setFinalBuyUnits(ccSpMmReplPacks
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(ccSpMmReplPack -> Optional.ofNullable(ccSpMmReplPack.getFinalBuyUnits()).orElse(0))
                .sum()
        );
        ccMmReplPack.setReplPackCnt(ccMmReplPack.getReplUnits()/ccMmReplPack.getVendorPackCnt());

        ValidationResult ccMmValidationResult = getValidationResult(ccMmReplPack.getMessageObj());
        if (!sizeValidationCodes.isEmpty()) {
            ccMmValidationResult.getCodes().addAll(appMessageTextService.getHierarchyIds(sizeValidationCodes));
        }
        ccMmReplPack.setMessageObj(getStringValue(ccMmValidationResult));
        ccMmReplPack.setMerchMethodDesc(merchMethodsDto.getMerchMethod());

        ccMmReplPacks.add(ccMmReplPack);

        //CC
        ccReplPack.setCcMmReplPack(ccMmReplPacks);
        log.info("Calculating CC Repln Qty");
        ccReplPack.setVendorPackCnt(replenishmentCons.getCcReplPackCons().getVendorPackCount());
        ccReplPack.setWhsePackCnt(replenishmentCons.getCcReplPackCons().getWarehousePackCount());
        ccReplPack.setVnpkWhpkRatio(replenishmentCons.getCcReplPackCons().getVendorPackWareHousePackRatio());
        ccReplPack.setReplUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getReplUnits()).orElse(0))
                .sum());
        ccReplPack.setFinalBuyUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getFinalBuyUnits()).orElse(0))
                .sum());
        ccReplPack.setReplPackCnt(ccReplPack.getReplUnits()/ccReplPack.getVendorPackCnt());
        if (Objects.nonNull(ccMmValidationResult) && !ccMmValidationResult.getCodes().isEmpty()) {
            ccValidationResult.getCodes().addAll(ccMmValidationResult.getCodes());
        }
        ccReplPack.setMessageObj(getStringValue(ccValidationResult));

        ccReplPacks.add(ccReplPack);

        //Style
        styleReplPack.setCcReplPack(ccReplPacks);
        log.info("Calculating Style Repln Qty");
        styleReplPack.setVendorPackCnt(replenishmentCons.getStyleReplPackCons().getVendorPackCount());
        styleReplPack.setWhsePackCnt(replenishmentCons.getStyleReplPackCons().getWarehousePackCount());
        styleReplPack.setVnpkWhpkRatio(replenishmentCons.getStyleReplPackCons().getVendorPackWareHousePackRatio());
        styleReplPack.setReplUnits(ccReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccReplPack1 -> Optional.ofNullable(ccReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        styleReplPack.setFinalBuyUnits(ccReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccReplPack1 -> Optional.ofNullable(ccReplPack1.getFinalBuyUnits()).orElse(0))
                .sum()
        );
        styleReplPack.setReplPackCnt(styleReplPack.getReplUnits()/styleReplPack.getVendorPackCnt());

        ValidationResult styleValidationResult = getValidationResult(styleReplPack.getMessageObj());
        if (Objects.nonNull(ccValidationResult) && !ccValidationResult.getCodes().isEmpty()) {
            styleValidationResult.getCodes().addAll(appMessageTextService.getHierarchyIds(ccValidationResult.getCodes()));
        }
        styleReplPack.setMessageObj(getStringValue(styleValidationResult));

        styleReplPacks.add(styleReplPack);

        //Fineline
        finelineReplPack.setStyleReplPack(styleReplPacks);
        log.info("Calculating fineline Repln Qty");
        finelineReplPack.setVendorPackCnt(replenishmentCons.getFinelineReplPackCons().getVendorPackCount());
        finelineReplPack.setWhsePackCnt(replenishmentCons.getFinelineReplPackCons().getWarehousePackCount());
        finelineReplPack.setVnpkWhpkRatio(replenishmentCons.getFinelineReplPackCons().getVendorPackWareHousePackRatio());
        finelineReplPack.setReplUnits(styleReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(styleReplPack1 -> Optional.ofNullable(styleReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        finelineReplPack.setFinalBuyUnits(styleReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(styleReplPack1 -> Optional.ofNullable(styleReplPack1.getFinalBuyUnits()).orElse(0))
                .sum()
        );
        finelineReplPack.setReplPackCnt(finelineReplPack.getReplUnits()/finelineReplPack.getVendorPackCnt());

        finelineReplPack.setFixtureTypeRollupName(FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
        finelineReplPack.setRunStatusCode(0);
        ValidationResult finelineValidationResult = getValidationResult(finelineReplPack.getMessageObj());
        if (Objects.nonNull(styleValidationResult) && !styleValidationResult.getCodes().isEmpty()) {
            finelineValidationResult.getCodes().addAll(styleValidationResult.getCodes());
        }
        finelineReplPack.setMessageObj(getStringValue(finelineValidationResult));

        finelineReplPacks.add(finelineReplPack);

        //Sub catg
        subCatgReplPack.setFinelineReplPack(finelineReplPacks);
        log.info("Calculating Sub Catg Repln Qty");
        subCatgReplPack.setVendorPackCnt(replenishmentCons.getSubCatgReplPackCons().getVendorPackCount());
        subCatgReplPack.setWhsePackCnt(replenishmentCons.getSubCatgReplPackCons().getWarehousePackCount());
        subCatgReplPack.setVnpkWhpkRatio(replenishmentCons.getSubCatgReplPackCons().getVendorPackWareHousePackRatio());

        subCatgReplPack.setReplUnits(finelineReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(finelineReplPack1 -> Optional.ofNullable(finelineReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        subCatgReplPack.setFinalBuyUnits(finelineReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(finelineReplPack1 -> Optional.ofNullable(finelineReplPack1.getFinalBuyUnits()).orElse(0))
                .sum()
        );

        subCatgReplPack.setReplPackCnt(subCatgReplPack.getReplUnits()/subCatgReplPack.getVendorPackCnt());

        subCatgReplPack.setFixtureTypeRollupName(FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
        subCatgReplPack.setRunStatusCode(0);
        subCatgReplPacks.add(subCatgReplPack);

        //Catg
        merchCatgReplPack.setSubReplPack(subCatgReplPacks);
        log.info("Calculating Catg Repln Qty");
        merchCatgReplPack.setVendorPackCnt(replenishmentCons.getMerchCatgReplPackCons().getVendorPackCount());
        merchCatgReplPack.setWhsePackCnt(replenishmentCons.getMerchCatgReplPackCons().getWarehousePackCount());
        merchCatgReplPack.setVnpkWhpkRatio(replenishmentCons.getMerchCatgReplPackCons().getVendorPackWareHousePackRatio());
        merchCatgReplPack.setReplUnits(subCatgReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(subCatgReplPack1 -> Optional.ofNullable(subCatgReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        merchCatgReplPack.setFinalBuyUnits(subCatgReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(subCatgReplPack1 -> Optional.ofNullable(subCatgReplPack1.getFinalBuyUnits()).orElse(0))
                .sum()
        );
        merchCatgReplPack.setReplPackCnt(merchCatgReplPack.getReplUnits()/merchCatgReplPack.getVendorPackCnt());

        merchCatgReplPack.setFixtureTypeRollupName(FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
        merchCatgReplPack.setRunStatusCode(0);
        merchCatgReplPacks.add(merchCatgReplPack);

        return new ArrayList<>(merchCatgReplPacks);
    }

    private CcMmReplPack setCcMmReplnPack(Set<CcMmReplPack> ccMmReplPacks, CcMmReplPackId ccMmReplPackId) {
        CcMmReplPack ccMmReplPack = Optional.of(ccMmReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .filter(ccMmReplPack1 -> ccMmReplPack1.getCcMmReplPackId().equals(ccMmReplPackId))
                .findFirst()
                .orElse(new CcMmReplPack());
        if (ccMmReplPack.getCcMmReplPackId() == null) {
            ccMmReplPack.setCcMmReplPackId(ccMmReplPackId);
        }
        return ccMmReplPack;
    }

    private CcReplPack setCcReplPack(Set<CcReplPack> ccReplPacks, CcReplPackId ccReplPackId) {
        CcReplPack ccReplPack = Optional.of(ccReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .filter(ccReplPack1 -> ccReplPack1.getCcReplPackId().equals(ccReplPackId))
                .findFirst()
                .orElse(new CcReplPack());

        if (ccReplPack.getCcReplPackId() == null) {
            ccReplPack.setCcReplPackId(ccReplPackId);
        }
        return ccReplPack;
    }

    private StyleReplPack setStyleReplPack(Set<StyleReplPack> styleReplPacks, StyleReplPackId styleReplPackId) {
        StyleReplPack styleReplPack = Optional.of(styleReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .filter(styleReplPack1 -> styleReplPack1.getStyleReplPackId().equals(styleReplPackId))
                .findFirst()
                .orElse(new StyleReplPack());

        if (styleReplPack.getStyleReplPackId() == null) {
            styleReplPack.setStyleReplPackId(styleReplPackId);
        }
        return styleReplPack;
    }

    private MerchCatgReplPack setMerchCatgReplPack(Set<MerchCatgReplPack> merchCatgReplPacks, MerchCatgReplPackId merchCatgReplPackId) {
        MerchCatgReplPack merchCatgReplPack = Optional.of(merchCatgReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatgReplPack1 -> merchCatgReplPack1.getMerchCatgReplPackId().equals(merchCatgReplPackId))
                .findFirst()
                .orElse(new MerchCatgReplPack());
        if (merchCatgReplPack.getMerchCatgReplPackId() == null) {
            merchCatgReplPack.setMerchCatgReplPackId(merchCatgReplPackId);
        }
        return merchCatgReplPack;
    }

    private SubCatgReplPack setSubCatgReplPack(Set<SubCatgReplPack> subCatgReplPacks, SubCatgReplPackId subCatgReplPackId) {
        SubCatgReplPack subCatgReplPack = Optional.of(subCatgReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .filter(subCatgReplPack1 -> subCatgReplPack1.getSubCatgReplPackId().equals(subCatgReplPackId))
                .findFirst()
                .orElse(new SubCatgReplPack());

        if (subCatgReplPack.getSubCatgReplPackId() == null) {
            subCatgReplPack.setSubCatgReplPackId(subCatgReplPackId);
        }
        return subCatgReplPack;
    }

    private FinelineReplPack setFinelineReplenishment(Set<FinelineReplPack> finelineReplPacks, FinelineReplPackId finelineReplPackId) {
        FinelineReplPack finelineReplPack = Optional.of(finelineReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .filter(finelineReplPack1 -> finelineReplPack1.getFinelineReplPackId().equals(finelineReplPackId))
                .findFirst()
                .orElse(new FinelineReplPack());

        if (finelineReplPack.getFinelineReplPackId() == null) {
            finelineReplPack.setFinelineReplPackId(finelineReplPackId);
        }
        return finelineReplPack;
    }

    private void setReplenishmentSizeEntity(CcMmReplPack ccMmReplPack, CcSpMmReplPack ccSpMmReplPack, MerchMethodsDto merchMethodsDto) {
        Set<CcSpMmReplPack> ccSpMmReplPacks = Optional.ofNullable(ccMmReplPack.getCcSpMmReplPack()).orElse(new HashSet<>());

        CcSpMmReplPackId ccSpMmReplPackId = Optional.ofNullable(ccSpMmReplPack.getCcSpReplPackId()).orElse(new CcSpMmReplPackId());
        ccSpMmReplPackId.setCcMmReplPackId(ccMmReplPack.getCcMmReplPackId());

        ccSpMmReplPack.setMerchMethodDesc(merchMethodsDto.getMerchMethod());

        ccSpMmReplPacks.add(ccSpMmReplPack);
        ccMmReplPack.setCcSpMmReplPack(ccSpMmReplPacks);
    }

    public CcSpMmReplPack setCcMmSpReplenishment(Map.Entry<SizeDto, BuyQtyObj> entry, int totalReplenishment, int totalBuyQty) {
        CcSpMmReplPackId ccSpMmReplPackId = new CcSpMmReplPackId();
        ccSpMmReplPackId.setAhsSizeId(entry.getKey().getAhsSizeId());

        CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
        ccSpMmReplPack.setSizeDesc(entry.getKey().getSizeDesc());

        ccSpMmReplPack.setCcSpReplPackId(ccSpMmReplPackId);

        ccSpMmReplPack.setFinalBuyUnits(totalBuyQty);
        ccSpMmReplPack.setReplUnits(totalReplenishment);
        ccSpMmReplPack.setReplenObj(getStringValue(entry.getValue().getReplenishments()));
        ccSpMmReplPack.setMessageObj(getStringValue(entry.getValue().getValidationResult()));

        return ccSpMmReplPack;
    }

    private String getStringValue(Object obj) {
        try {
            return (Objects.nonNull(obj)) ? objectMapper.writeValueAsString(obj) : null;
        } catch (Exception e) {
            log.error("Failed to convert obj into string: {} | {}", obj, e.getMessage());
            throw new CustomException("Failed to convert obj into string " + e);
        }
    }

    private ValidationResult getValidationResult(String messageObj) {
        try {
            return StringUtils.isNotEmpty(messageObj) ? objectMapper.readValue(messageObj, ValidationResult.class) : ValidationResult.builder().codes(new HashSet<>()).build();
        } catch (Exception e) {
            throw new CustomException("Exception occurred while deserializing validation messages");
        }
    }
}
