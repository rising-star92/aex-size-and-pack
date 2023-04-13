package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@Slf4j
@Service
public class BuyQtyReplenishmentMapperService {

    private final MerchCatgReplPackRepository merchCatgReplPackRepository;

    private final SubCatgReplnPkConsRepository subCatgReplnPkConsRepository;

    private final FinelineReplnPkConsRepository finelineReplnPkConsRepository;

    private final StyleReplnPkConsRepository styleReplnPkConsRepository;

    private final CcReplnPkConsRepository ccReplnPkConsRepository;

    private final CcMmReplnPkConsRepository ccMmReplnPkConsRepository;

    private final CcSpReplnPkConsRepository ccSpReplnPkConsRepository;

    public BuyQtyReplenishmentMapperService(MerchCatgReplPackRepository merchCatgReplPackRepository, SubCatgReplnPkConsRepository subCatgReplnPkConsRepository,
                                            FinelineReplnPkConsRepository finelineReplnPkConsRepository, StyleReplnPkConsRepository styleReplnPkConsRepository,
                                            CcReplnPkConsRepository ccReplnPkConsRepository, CcMmReplnPkConsRepository ccMmReplnPkConsRepository, CcSpReplnPkConsRepository ccSpReplnPkConsRepository) {
        this.merchCatgReplPackRepository = merchCatgReplPackRepository;
        this.subCatgReplnPkConsRepository = subCatgReplnPkConsRepository;
        this.finelineReplnPkConsRepository = finelineReplnPkConsRepository;
        this.styleReplnPkConsRepository = styleReplnPkConsRepository;
        this.ccReplnPkConsRepository = ccReplnPkConsRepository;
        this.ccMmReplnPkConsRepository = ccMmReplnPkConsRepository;
        this.ccSpReplnPkConsRepository = ccSpReplnPkConsRepository;
    }

    public List<MerchCatgReplPack> setAllReplenishments(StyleDto styleDto, MerchMethodsDto merchMethodsDto, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse, CustomerChoiceDto customerChoiceDto, Set<CcSpMmReplPack> ccSpMmReplPacks) {
        List<MerchCatgReplPack> merchCatgReplPacks = calculateBuyQtyResponse.getMerchCatgReplPacks();
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

        ccSpMmReplPacks.forEach(ccSpMmReplPack -> setReplenishmentSizeEntity(ccMmReplPack, ccSpMmReplPack, merchMethodsDto));

        Map<Integer, CcSpMmReplPack> ccSpMmReplPackSizeMap = getCcSpMmReplPackSieMap(ccMmReplPack);
        ccSpMmReplPacks.forEach( ccSpMmReplPack -> {
            setVendorPackAndWhsePackCountForCCSpMm(ccSpMmReplPackSizeMap, ccSpMmReplPack);
        });
        setVendorPackAndWhsePackCountForCCmm(ccMmReplPack);

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

        ccMmReplPack.setMerchMethodDesc(merchMethodsDto.getMerchMethod());
        ccMmReplPacks.add(ccMmReplPack);

        //CC
        ccReplPack.setCcMmReplPack(ccMmReplPacks);
        log.info("Calculating CC Repln Qty");
        setVendorPackAndWhsePackCountForCCRepln(ccReplPack);
        ccReplPack.setReplUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getReplUnits()).orElse(0))
                .sum());
        ccReplPack.setFinalBuyUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getFinalBuyUnits()).orElse(0))
                .sum());
        ccReplPack.setReplPackCnt(ccReplPack.getReplUnits()/ccReplPack.getVendorPackCnt());

        ccReplPacks.add(ccReplPack);

        //Style
        styleReplPack.setCcReplPack(ccReplPacks);
        log.info("Calculating Style Repln Qty");
        setVendorPackAndWhsePackCountForStyle(styleReplPack);
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

        styleReplPacks.add(styleReplPack);

        //Fineline
        finelineReplPack.setStyleReplPack(styleReplPacks);
        log.info("Calculating fineline Repln Qty");
        setVendorPackAndWhsePackCountForFineline(finelineReplPack);
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
        finelineReplPacks.add(finelineReplPack);

        //Sub catg
        subCatgReplPack.setFinelineReplPack(finelineReplPacks);
        log.info("Calculating Sub Catg Repln Qty");
        setVendorPackAndWhsePackCountForSubCat(subCatgReplPack);
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
        setVendorPackAndWhsePackCountForMerchCatg(merchCatgReplPack);
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

    private void setVendorPackAndWhsePackCountForMerchCatg(MerchCatgReplPack merchCatgReplPack) {
        if (merchCatgReplPack.getVendorPackCnt() == null && merchCatgReplPack.getWhsePackCnt() == null) {
            Optional<MerchCatgReplPack> merchCatgReplPackResult = merchCatgReplPackRepository.findById(merchCatgReplPack.getMerchCatgReplPackId());
            if (merchCatgReplPackResult.isPresent()) {
                MerchCatgReplPack merchCatgReplPackFromDb = merchCatgReplPackResult.get();
                merchCatgReplPack.setVendorPackCnt(merchCatgReplPackFromDb.getVendorPackCnt());
                merchCatgReplPack.setWhsePackCnt(merchCatgReplPackFromDb.getWhsePackCnt());
                merchCatgReplPack.setVnpkWhpkRatio(merchCatgReplPackFromDb.getVnpkWhpkRatio());
            } else {
                merchCatgReplPack.setVendorPackCnt(VP_DEFAULT);
                merchCatgReplPack.setWhsePackCnt(WP_DEFAULT);
                merchCatgReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
            }
        }
    }

    private void setVendorPackAndWhsePackCountForSubCat(SubCatgReplPack subCatgReplPack) {
        if (subCatgReplPack.getVendorPackCnt() == null && subCatgReplPack.getWhsePackCnt() == null) {
            Optional<SubCatgReplPack> subCatgReplPackResult = subCatgReplnPkConsRepository.findById(subCatgReplPack.getSubCatgReplPackId());
            if (subCatgReplPackResult.isPresent()) {
                SubCatgReplPack subCatgReplPackFromDb = subCatgReplPackResult.get();
                subCatgReplPack.setVendorPackCnt(subCatgReplPackFromDb.getVendorPackCnt());
                subCatgReplPack.setWhsePackCnt(subCatgReplPackFromDb.getWhsePackCnt());
                subCatgReplPack.setVnpkWhpkRatio(subCatgReplPackFromDb.getVnpkWhpkRatio());
            } else {
                subCatgReplPack.setVendorPackCnt(VP_DEFAULT);
                subCatgReplPack.setWhsePackCnt(WP_DEFAULT);
                subCatgReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
            }
        }
    }

    private void setVendorPackAndWhsePackCountForFineline(FinelineReplPack finelineReplPack) {
        if (finelineReplPack.getVendorPackCnt() == null && finelineReplPack.getWhsePackCnt() == null) {
            Optional<FinelineReplPack> finelineReplPackResult = finelineReplnPkConsRepository.findById(finelineReplPack.getFinelineReplPackId());
            if (finelineReplPackResult.isPresent()) {
                FinelineReplPack finelineReplPackFromDB = finelineReplPackResult.get();
                finelineReplPack.setVendorPackCnt(finelineReplPackFromDB.getVendorPackCnt());
                finelineReplPack.setWhsePackCnt(finelineReplPackFromDB.getWhsePackCnt());
                finelineReplPack.setVnpkWhpkRatio(finelineReplPackFromDB.getVnpkWhpkRatio());
            } else {
                finelineReplPack.setVendorPackCnt(VP_DEFAULT);
                finelineReplPack.setWhsePackCnt(WP_DEFAULT);
                finelineReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
            }
        }
    }

    private void setVendorPackAndWhsePackCountForStyle(StyleReplPack styleReplPack) {
        if (styleReplPack.getVendorPackCnt() == null && styleReplPack.getWhsePackCnt() == null) {
            Optional<StyleReplPack> styleReplPackResult = styleReplnPkConsRepository.findById(styleReplPack.getStyleReplPackId());
            if (styleReplPackResult.isPresent()) {
                StyleReplPack styleReplPackFromDB = styleReplPackResult.get();
                styleReplPack.setVendorPackCnt(styleReplPackFromDB.getVendorPackCnt());
                styleReplPack.setWhsePackCnt(styleReplPackFromDB.getWhsePackCnt());
                styleReplPack.setVnpkWhpkRatio(styleReplPackFromDB.getVnpkWhpkRatio());
            } else {
                styleReplPack.setVendorPackCnt(VP_DEFAULT);
                styleReplPack.setWhsePackCnt(WP_DEFAULT);
                styleReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
            }
        }
    }

    private void setVendorPackAndWhsePackCountForCCRepln(CcReplPack ccReplPack) {
        if (ccReplPack.getVendorPackCnt() == null && ccReplPack.getWhsePackCnt() == null) {
            Optional<CcReplPack> ccReplPackResult = ccReplnPkConsRepository.findById(ccReplPack.getCcReplPackId());
            if (ccReplPackResult.isPresent()) {
                CcReplPack ccReplPackFromDb = ccReplPackResult.get();
                ccReplPack.setVendorPackCnt(ccReplPackFromDb.getVendorPackCnt());
                ccReplPack.setWhsePackCnt(ccReplPackFromDb.getWhsePackCnt());
                ccReplPack.setVnpkWhpkRatio(ccReplPackFromDb.getVnpkWhpkRatio());
            } else {
                ccReplPack.setVendorPackCnt(VP_DEFAULT);
                ccReplPack.setWhsePackCnt(WP_DEFAULT);
                ccReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
            }
        }
    }

    private void setVendorPackAndWhsePackCountForCCmm(CcMmReplPack ccMmReplPack) {
        if (ccMmReplPack.getVendorPackCnt() == null && ccMmReplPack.getWhsePackCnt() == null) {
            Optional<CcMmReplPack> ccMmReplPackResult = ccMmReplnPkConsRepository.findById(ccMmReplPack.getCcMmReplPackId());
            if (ccMmReplPackResult.isPresent()) {
                CcMmReplPack ccMmReplPackFromDb = ccMmReplPackResult.get();
                ccMmReplPack.setVendorPackCnt(ccMmReplPackFromDb.getVendorPackCnt());
                ccMmReplPack.setWhsePackCnt(ccMmReplPackFromDb.getWhsePackCnt());
                ccMmReplPack.setVnpkWhpkRatio(ccMmReplPackFromDb.getVnpkWhpkRatio());
            } else {
                ccMmReplPack.setVendorPackCnt(VP_DEFAULT);
                ccMmReplPack.setWhsePackCnt(WP_DEFAULT);
                ccMmReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
            }
        }
    }

    private Map<Integer, CcSpMmReplPack> getCcSpMmReplPackSieMap(CcMmReplPack ccMmReplPack) {
        List<CcSpMmReplPack> ccSpMmReplPacks = new ArrayList<>();
        if (ccMmReplPack != null && ccMmReplPack.getCcMmReplPackId() != null) {
            CcReplPackId ccReplPackId = ccMmReplPack.getCcMmReplPackId().getCcReplPackId();
            StyleReplPackId styleReplPackId = ccReplPackId.getStyleReplPackId();
            FinelineReplPackId finelineReplPackId = styleReplPackId.getFinelineReplPackId();
            SubCatgReplPackId subCatgReplPackId = finelineReplPackId.getSubCatgReplPackId();
            MerchCatgReplPackId merchCatgReplPackId = subCatgReplPackId.getMerchCatgReplPackId();
            ccSpMmReplPacks = ccSpReplnPkConsRepository.getCcSpMmReplnPkVendorPackAndWhsePackCount(merchCatgReplPackId.getPlanId(), merchCatgReplPackId.getChannelId(), merchCatgReplPackId.getRepTLvl3(), subCatgReplPackId.getRepTLvl4(), finelineReplPackId.getFinelineNbr(), styleReplPackId.getStyleNbr(), ccReplPackId.getCustomerChoice(), ccMmReplPack.getCcMmReplPackId().getMerchMethodCode());
        }
        return Optional.ofNullable(ccSpMmReplPacks)
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(ccSpMmReplPack -> ccSpMmReplPack.getCcSpReplPackId().getAhsSizeId(), ccSpMmReplPack -> ccSpMmReplPack));
    }

    private void setVendorPackAndWhsePackCountForCCSpMm(Map<Integer, CcSpMmReplPack> ccSpMmReplPackSizeMap, CcSpMmReplPack ccSpMmReplPack) {
        if (ccSpMmReplPackSizeMap != null && ccSpMmReplPackSizeMap.containsKey(ccSpMmReplPack.getCcSpReplPackId().getAhsSizeId())) {
            CcSpMmReplPack ccSpMmReplPackFromDb = ccSpMmReplPackSizeMap.get(ccSpMmReplPack.getCcSpReplPackId().getAhsSizeId());
            Integer vendorPackCnt = ccSpMmReplPackFromDb.getVendorPackCnt();
            ccSpMmReplPack.setReplPackCnt(ccSpMmReplPack.getReplUnits() / vendorPackCnt);
            ccSpMmReplPack.setVendorPackCnt(vendorPackCnt);
            ccSpMmReplPack.setWhsePackCnt(ccSpMmReplPackFromDb.getWhsePackCnt());
            ccSpMmReplPack.setVnpkWhpkRatio(ccSpMmReplPackFromDb.getVnpkWhpkRatio());
        } else {
            ccSpMmReplPack.setReplPackCnt(ccSpMmReplPack.getReplUnits() / VP_DEFAULT);
            ccSpMmReplPack.setVendorPackCnt(VP_DEFAULT);
            ccSpMmReplPack.setWhsePackCnt(WP_DEFAULT);
            ccSpMmReplPack.setVnpkWhpkRatio(VP_WP_RATIO_DEFAULT);
        }
    }
}
