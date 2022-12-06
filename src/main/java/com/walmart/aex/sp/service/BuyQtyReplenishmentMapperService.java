package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
@Service
public class BuyQtyReplenishmentMapperService {
    public List<MerchCatgReplPack> setAllReplenishments(StyleDto styleDto, MerchMethodsDto merchMethodsDto, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse, CustomerChoiceDto customerChoiceDto, Set<CcSpMmReplPack> ccSpMmReplPacks) {
        List<MerchCatgReplPack> merchCatgReplPacks = calculateBuyQtyResponse.getMerchCatgReplPacks();
        // Hard coded FixtureTypeRollUpId for testing calculation
        MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId(calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getLvl0Nbr(),
                calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(),
                ChannelType.getChannelIdFromName(calculateBuyQtyParallelRequest.getChannel()), 1);
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

        ccSpMmReplPacks.forEach( ccSpMmReplPack -> {
            ccSpMmReplPack.setReplPackCnt(ccSpMmReplPack.getReplUnits()/VP_DEFAULT);
            ccSpMmReplPack.setVendorPackCnt(VP_DEFAULT);
            ccSpMmReplPack.setWhsePackCnt(WP_DEFAULT);
            ccSpMmReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
        });

        ccSpMmReplPacks.forEach(ccSpMmReplPack -> setReplenishmentSizeEntity(ccMmReplPack, ccSpMmReplPack, merchMethodsDto));

        ccMmReplPack.setVendorPackCnt(VP_DEFAULT);
        ccMmReplPack.setWhsePackCnt(WP_DEFAULT);
        ccMmReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);

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
        ccMmReplPack.setReplPackCnt(ccMmReplPack.getReplUnits()/VP_DEFAULT);

        ccMmReplPack.setMerchMethodDesc(merchMethodsDto.getMerchMethod());
        ccMmReplPacks.add(ccMmReplPack);

        //CC
        ccReplPack.setCcMmReplPack(ccMmReplPacks);
        log.info("Calculating CC Repln Qty");
        ccReplPack.setVendorPackCnt(VP_DEFAULT);
        ccReplPack.setWhsePackCnt(WP_DEFAULT);
        ccReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
        ccReplPack.setReplUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getReplUnits()).orElse(0))
                .sum());
        ccReplPack.setFinalBuyUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getFinalBuyUnits()).orElse(0))
                .sum());
        ccReplPack.setReplPackCnt(ccReplPack.getReplUnits()/VP_DEFAULT);

        ccReplPacks.add(ccReplPack);

        //Style
        styleReplPack.setCcReplPack(ccReplPacks);
        log.info("Calculating Style Repln Qty");
        styleReplPack.setVendorPackCnt(VP_DEFAULT);
        styleReplPack.setWhsePackCnt(WP_DEFAULT);
        styleReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
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
        styleReplPack.setReplPackCnt(styleReplPack.getReplUnits()/VP_DEFAULT);

        styleReplPacks.add(styleReplPack);

        //Fineline
        finelineReplPack.setStyleReplPack(styleReplPacks);
        log.info("Calculating fineline Repln Qty");
        finelineReplPack.setVendorPackCnt(VP_DEFAULT);
        finelineReplPack.setWhsePackCnt(WP_DEFAULT);
        finelineReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
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
        finelineReplPack.setReplPackCnt(finelineReplPack.getReplUnits()/VP_DEFAULT);

        finelineReplPack.setFixtureTypeRollupName(FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
        finelineReplPack.setRunStatusCode(0);
        finelineReplPacks.add(finelineReplPack);

        //Sub catg
        subCatgReplPack.setFinelineReplPack(finelineReplPacks);
        log.info("Calculating Sub Catg Repln Qty");
        subCatgReplPack.setVendorPackCnt(VP_DEFAULT);
        subCatgReplPack.setWhsePackCnt(WP_DEFAULT);
        subCatgReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
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

        subCatgReplPack.setReplPackCnt(subCatgReplPack.getReplUnits()/VP_DEFAULT);

        subCatgReplPack.setFixtureTypeRollupName(FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
        subCatgReplPack.setRunStatusCode(0);
        subCatgReplPacks.add(subCatgReplPack);

        //Catg
        merchCatgReplPack.setSubReplPack(subCatgReplPacks);
        log.info("Calculating Catg Repln Qty");
        merchCatgReplPack.setVendorPackCnt(VP_DEFAULT);
        merchCatgReplPack.setWhsePackCnt(WP_DEFAULT);
        merchCatgReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
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
        merchCatgReplPack.setReplPackCnt(merchCatgReplPack.getReplUnits()/VP_DEFAULT);

        merchCatgReplPack.setFixtureTypeRollupName(FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
        merchCatgReplPack.setRunStatusCode(0);
        merchCatgReplPacks.add(merchCatgReplPack);

        return merchCatgReplPacks;
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

    private MerchCatgReplPack setMerchCatgReplPack(List<MerchCatgReplPack> merchCatgReplPacks, MerchCatgReplPackId merchCatgReplPackId) {
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
}
