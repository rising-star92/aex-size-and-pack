package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APRequest;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.exception.SizeAndPackException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculateFinelineBuyQuantity {

    private final BQFPService bqfpService;
    private final ObjectMapper objectMapper;
    private final SizeAndPackService sizeAndPackService;

    public CalculateFinelineBuyQuantity(BQFPService bqfpService,
                                        ObjectMapper objectMapper, SizeAndPackService sizeAndPackService) {
        this.bqfpService = bqfpService;
        this.objectMapper = objectMapper;
        this.sizeAndPackService = sizeAndPackService;
    }

    public CalculateBuyQtyResponse calculateFinelineBuyQty(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) throws SizeAndPackException {
        BuyQtyResponse buyQtyResponse = getSizeProfiles(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        log.info("Size Profiles: {}", buyQtyResponse);
        BQFPResponse bqfpResponse = getBqfpResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest.getFinelineNbr());
        log.info("BQ FP Response: {}", bqfpResponse);
        APResponse apResponse = getRfaSpResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest.getFinelineNbr(), bqfpResponse);
        log.info("RFA Response: {}", apResponse);

        FinelineDto finelineDto = getFineline(buyQtyResponse);
        if (finelineDto != null) {
            if (!CollectionUtils.isEmpty(finelineDto.getMerchMethods())) {
                getMerchMethod(calculateBuyQtyParallelRequest, finelineDto, apResponse, bqfpResponse, calculateBuyQtyResponse, calculateBuyQtyRequest);
            }
        } else log.info("Size Profile Fineline is null: {}", buyQtyResponse);
        return calculateBuyQtyResponse;
    }

    private BuyQtyResponse getSizeProfiles(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) throws SizeAndPackException {
        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        buyQtyRequest.setChannel(calculateBuyQtyRequest.getChannel());
        buyQtyRequest.setLvl3Nbr(calculateBuyQtyParallelRequest.getLvl3Nbr());
        buyQtyRequest.setLvl4Nbr(calculateBuyQtyParallelRequest.getLvl4Nbr());
        buyQtyRequest.setFinelineNbr(calculateBuyQtyParallelRequest.getFinelineNbr());
        return sizeAndPackService.getAllCcSizeProfiles(buyQtyRequest);
    }

    private FinelineDto getFineline(BuyQtyResponse buyQtyResponse) {
        return Optional.ofNullable(buyQtyResponse.getLvl3List())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl3Dto::getLvl4List)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(Lvl4Dto::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);
    }

    private void getMerchMethod(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, FinelineDto finelineDto, APResponse apResponse, BQFPResponse bqfpResponse,
                                CalculateBuyQtyResponse calculateBuyQtyResponse, CalculateBuyQtyRequest calculateBuyQtyRequest) {
        List<SpFineLineChannelFixture> spFineLineChannelFixtures = calculateBuyQtyResponse.getSpFineLineChannelFixtures();
        finelineDto.getMerchMethods().forEach(merchMethodsDto -> {
            FixtureTypeRollUpId fixtureTypeRollUpId = new FixtureTypeRollUpId(merchMethodsDto.getFixtureTypeRollupId());
            SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId(fixtureTypeRollUpId, calculateBuyQtyRequest.getPlanId(), calculateBuyQtyRequest.getLvl0Nbr(),
                    calculateBuyQtyRequest.getLvl1Nbr(), calculateBuyQtyRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(), calculateBuyQtyParallelRequest.getLvl4Nbr(), finelineDto.getFinelineNbr(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()));
            log.info("Checking if Fineline Chan Fixture Id is existing: {}", spFineLineChannelFixtureId);
            SpFineLineChannelFixture spFineLineChannelFixture = Optional.of(spFineLineChannelFixtures)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(spFineLineChannelFixture1 -> spFineLineChannelFixture1.getSpFineLineChannelFixtureId().equals(spFineLineChannelFixtureId))
                    .findFirst()
                    .orElse(new SpFineLineChannelFixture());

            if (spFineLineChannelFixture.getSpFineLineChannelFixtureId() == null) {
                spFineLineChannelFixture.setSpFineLineChannelFixtureId(spFineLineChannelFixtureId);
            }

            if (!CollectionUtils.isEmpty(finelineDto.getStyles())) {
                getStyles(finelineDto.getStyles(), merchMethodsDto, apResponse, bqfpResponse, spFineLineChannelFixture, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
            } else log.info("Styles Size Profiles are empty to calculate buy Qty: {}", finelineDto);
            spFineLineChannelFixtures.add(spFineLineChannelFixture);

        });
        calculateBuyQtyResponse.setSpFineLineChannelFixtures(spFineLineChannelFixtures);
    }

    private void getStyles(List<StyleDto> styles, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse, SpFineLineChannelFixture spFineLineChannelFixture,
                           CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) {

        Set<SpStyleChannelFixture> spStyleChannelFixtures = Optional.ofNullable(spFineLineChannelFixture.getSpStyleChannelFixtures()).orElse(new HashSet<>());
        styles.forEach(styleDto -> {

            SpStyleChannelFixtureId spStyleChannelFixtureId = new SpStyleChannelFixtureId(spFineLineChannelFixture.getSpFineLineChannelFixtureId(), styleDto.getStyleNbr());
            log.info("Checking if Style Chan Fixture Id is existing: {}", spStyleChannelFixtureId);
            SpStyleChannelFixture spStyleChannelFixture = Optional.of(spStyleChannelFixtures)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(spStyleChannelFixture1 -> spStyleChannelFixture1.getSpStyleChannelFixtureId().equals(spStyleChannelFixtureId))
                    .findFirst()
                    .orElse(new SpStyleChannelFixture());

            if (spStyleChannelFixture.getSpStyleChannelFixtureId() == null) {
                spStyleChannelFixture.setSpStyleChannelFixtureId(spStyleChannelFixtureId);
            }
            if (!CollectionUtils.isEmpty(styleDto.getCustomerChoices())) {
                getCustomerChoices(styleDto, merchMethodsDto, apResponse, bqfpResponse, spStyleChannelFixture, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
            }
            spStyleChannelFixtures.add(spStyleChannelFixture);
        });
        log.info("calculating fineline IS and BS Qty");
        spFineLineChannelFixture.setInitialSetQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getInitialSetQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setBumpPackQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getBumpPackQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setBuyQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getBuyQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setReplnQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spStyleChannelFixture -> Optional.ofNullable(spStyleChannelFixture.getReplnQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setSpStyleChannelFixtures(spStyleChannelFixtures);
    }

    private void getCustomerChoices(StyleDto styleDto, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse,
                                    SpStyleChannelFixture spStyleChannelFixture, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) {

        Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtures = Optional.ofNullable(spStyleChannelFixture.getSpCustomerChoiceChannelFixture()).orElse(new HashSet<>());
        styleDto.getCustomerChoices().forEach(customerChoiceDto -> {
            SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId = new SpCustomerChoiceChannelFixtureId(spStyleChannelFixture.getSpStyleChannelFixtureId(), customerChoiceDto.getCcId());
            log.info("Checking if Cc Chan Fixture Id is existing: {}", spCustomerChoiceChannelFixtureId);
            SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture = Optional.of(spCustomerChoiceChannelFixtures)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(spCustomerChoiceChannelFixture1 -> spCustomerChoiceChannelFixture1.getSpCustomerChoiceChannelFixtureId().equals(spCustomerChoiceChannelFixtureId))
                    .findFirst()
                    .orElse(new SpCustomerChoiceChannelFixture());

            if (spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureId() == null) {
                spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureId(spCustomerChoiceChannelFixtureId);
            }

            //TODO: Delete replenishment if replenishment is deleted after set
            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getCcClusters(styleDto, customerChoiceDto, merchMethodsDto, apResponse, bqfpResponse, spCustomerChoiceChannelFixture, calculateBuyQtyResponse, calculateBuyQtyParallelRequest);
            }
            spCustomerChoiceChannelFixtures.add(spCustomerChoiceChannelFixture);
        });
        log.info("calculating Style IS and BS Qty");
        spStyleChannelFixture.setInitialSetQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getInitialSetQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setBumpPackQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getBumpPackQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setBuyQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getBuyQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setReplnQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixture -> Optional.ofNullable(spCustomerChoiceChannelFixture.getReplnQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setSpCustomerChoiceChannelFixture(spCustomerChoiceChannelFixtures);
    }

    private void getCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, MerchMethodsDto merchMethodsDto, APResponse apResponse,
                               BQFPResponse bqfpResponse, SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture, CalculateBuyQtyResponse calculateBuyQtyResponse,
                               CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId = new HashMap<>();
        //Replenishment
        List<Replenishment> replenishments = getReplenishments(merchMethodsDto, bqfpResponse, styleDto, customerChoiceDto);
        log.info("Get All Replenishments if exists for customerchoice: {} and merch method: {}", customerChoiceDto.getCcId(), merchMethodsDto.getFixtureTypeRollupId());
        if (!CollectionUtils.isEmpty(replenishments)) {
            //Set Replenishment for Size Map
            Optional.ofNullable(customerChoiceDto.getClusters())
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(clustersDto1 -> clustersDto1.getClusterID().equals(0))
                    .findFirst().ifPresent(clustersDto -> setReplenishmentSizes(clustersDto, replenishments, storeBuyQtyBySizeId));
        }
        customerChoiceDto.getClusters().forEach(clustersDto -> {
            if (!CollectionUtils.isEmpty(clustersDto.getSizes()) && !clustersDto.getClusterID().equals(0)) {

                List<RFASizePackData> rfaSizePackDataList = getSizeVolumeClustersFromRfa(apResponse, clustersDto.getClusterID(), styleDto.getStyleNbr(), customerChoiceDto.getCcId(),
                        FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
                log.info("RFA Size PackData: {}", rfaSizePackDataList);
                //Set Initial Set and Bump Set for Size Map
                getClusterSizes(styleDto, customerChoiceDto, clustersDto, merchMethodsDto, bqfpResponse, storeBuyQtyBySizeId, rfaSizePackDataList);
            }
        });
        log.info("Store Map: {}", storeBuyQtyBySizeId);
        Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes = Optional.ofNullable(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureSize()).orElse(new HashSet<>());
        Set<CcSpMmReplPack> ccSpMmReplPacks = new HashSet<>();
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            final Integer ZERO = 0;
            SpCustomerChoiceChannelFixtureSizeId spCustomerChoiceChannelFixtureSizeId = new SpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureId(), entry.getKey().getAhsSizeId());
            SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSize = Optional.of(spCustomerChoiceChannelFixtureSizes)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(spCustomerChoiceChannelFixtureSize1 -> spCustomerChoiceChannelFixtureSize1.getSpCustomerChoiceChannelFixtureSizeId().equals(spCustomerChoiceChannelFixtureSizeId))
                    .findFirst()
                    .orElse(new SpCustomerChoiceChannelFixtureSize());
            if (spCustomerChoiceChannelFixtureSize.getSpCustomerChoiceChannelFixtureSizeId() == null) {
                spCustomerChoiceChannelFixtureSize.setSpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixtureSizeId);
            }

            int isBuyQty = entry.getValue().getBuyQtyStoreObj().getBuyQuantities()
                    .stream()
                    .filter(Objects::nonNull)
                    .mapToInt(storeQuantity -> Optional.ofNullable(storeQuantity.getTotalUnits()).orElse(ZERO))
                    .sum();

            int bsBuyQty = entry.getValue().getBuyQtyStoreObj().getBuyQuantities()
                    .stream()
                    .map(StoreQuantity::getBumpSets)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .mapToInt(bumpSetQuantity -> Optional.ofNullable(bumpSetQuantity.getTotalUnits()).orElse(ZERO))
                    .sum();

            //TODO: move threshold to CCM
            double replenishmentThreshold = 500.0;
            long totalReplenishment = 0L;
            if ((!CollectionUtils.isEmpty(entry.getValue().getReplenishments()))) {
                totalReplenishment = entry.getValue().getReplenishments()
                        .stream()
                        .filter(Objects::nonNull)
                        .mapToLong(replenishment -> Optional.ofNullable(replenishment.getReplnUnits()).orElse(0L))
                        .sum();
                if (totalReplenishment < 500) {
                    isBuyQty = (int) (isBuyQty + totalReplenishment);
                    totalReplenishment = 0L;
                    //TODO: Adjust IS BS Store Obj

                }

            }

            int totalBuyQty = isBuyQty + bsBuyQty + (int) totalReplenishment;
            spCustomerChoiceChannelFixtureSize.setInitialSetQty(isBuyQty);
            spCustomerChoiceChannelFixtureSize.setBumpPackQty(bsBuyQty);
            spCustomerChoiceChannelFixtureSize.setMerchMethodCode(merchMethodsDto.getMerchMethodCode());
            spCustomerChoiceChannelFixtureSize.setAhsSizeDesc(entry.getKey().getSizeDesc());
            spCustomerChoiceChannelFixtureSize.setReplnQty((int) totalReplenishment);
            spCustomerChoiceChannelFixtureSize.setBuyQty(totalBuyQty);

            try {
                log.info("Store Obj: {}", objectMapper.writeValueAsString(entry.getValue()));
                spCustomerChoiceChannelFixtureSize.setStoreObj(objectMapper.writeValueAsString(entry.getValue()));
            } catch (Exception e) {
                log.error("Error parsing Json: ", e);
                throw new CustomException("Error parsing Json: " + e);
            }
            spCustomerChoiceChannelFixtureSizes.add(spCustomerChoiceChannelFixtureSize);

            //Replenishment
            if (!CollectionUtils.isEmpty(replenishments) && totalReplenishment > 0) {
                CcSpMmReplPackId ccSpMmReplPackId = new CcSpMmReplPackId();
                ccSpMmReplPackId.setAhsSizeId(entry.getKey().getAhsSizeId());

                CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();

                ccSpMmReplPack.setFinalBuyUnits(totalBuyQty);
                ccSpMmReplPack.setReplUnits((int) totalReplenishment);
                try {
                    ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(entry.getValue().getReplenishments()));
                } catch (Exception e) {
                    log.error("Failed to create replenishment Obj for size: {}", entry.getKey(), e);
                    throw new CustomException("Failed to create replenishment Obj for size " + e);
                }

                ccSpMmReplPacks.add(ccSpMmReplPack);
            }
        }

        if (!CollectionUtils.isEmpty(ccSpMmReplPacks)) {
            //Replenishment
            setAllReplenishments(styleDto, merchMethodsDto, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks);
        }

        spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureSize(spCustomerChoiceChannelFixtureSizes);
        spCustomerChoiceChannelFixture.setInitialSetQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getInitialSetQty()).orElse(0))
                .sum()
        );
        spCustomerChoiceChannelFixture.setBumpPackQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getBumpPackQty()).orElse(0))
                .sum()
        );
        spCustomerChoiceChannelFixture.setBuyQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getBuyQty()).orElse(0))
                .sum()
        );
        spCustomerChoiceChannelFixture.setReplnQty(spCustomerChoiceChannelFixtureSizes.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getReplnQty()).orElse(0))
                .sum()
        );
    }

    private void setAllReplenishments(StyleDto styleDto, MerchMethodsDto merchMethodsDto, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse, CustomerChoiceDto customerChoiceDto, Set<CcSpMmReplPack> ccSpMmReplPacks) {
        List<MerchCatgReplPack> merchCatgReplPacks = calculateBuyQtyResponse.getMerchCatgReplPacks();
        MerchCatgReplPackId merchCatgReplPackId = new MerchCatgReplPackId(calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getLvl0Nbr(),
                calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(),
                ChannelType.getChannelIdFromName(calculateBuyQtyParallelRequest.getChannel()), merchMethodsDto.getFixtureTypeRollupId());
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

        ccSpMmReplPacks.forEach(ccSpMmReplPack -> setReplenishmentSizeEntity(ccMmReplPack, ccSpMmReplPack));

        log.info("Calculating CC MM Repln Qty");
        //Repln Units
        ccMmReplPack.setReplUnits(ccMmReplPack.getCcSpMmReplPack()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(ccSpMmReplPack -> Optional.ofNullable(ccSpMmReplPack.getReplUnits()).orElse(0))
                .sum()
        );
        //Total Buy Units
        ccMmReplPack.setFinalBuyUnits(ccMmReplPack.getCcSpMmReplPack()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(ccSpMmReplPack -> Optional.ofNullable(ccSpMmReplPack.getFinalBuyUnits()).orElse(0))
                .sum()
        );
        ccMmReplPacks.add(ccMmReplPack);

        //CC
        ccReplPack.setCcMmReplPack(ccMmReplPacks);
        log.info("Calculating CC Repln Qty");
        ccReplPack.setReplUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getReplUnits()).orElse(0))
                .sum());
        ccReplPack.setFinalBuyUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getFinalBuyUnits()).orElse(0))
                .sum());
        ccReplPacks.add(ccReplPack);

        //Style
        styleReplPack.setCcReplPack(ccReplPacks);
        log.info("Calculating Style Repln Qty");
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
        styleReplPacks.add(styleReplPack);

        //Fineline
        finelineReplPack.setStyleReplPack(styleReplPacks);
        log.info("Calculating fineline Repln Qty");
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
        finelineReplPacks.add(finelineReplPack);

        //Sub catg
        subCatgReplPack.setFinelineReplPack(finelineReplPacks);
        log.info("Calculating Sub Catg Repln Qty");
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
        subCatgReplPacks.add(subCatgReplPack);

        //Catg
        merchCatgReplPack.setSubReplPack(subCatgReplPacks);
        log.info("Calculating Catg Repln Qty");
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
        merchCatgReplPacks.add(merchCatgReplPack);

        calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
    }

    private void setReplenishmentSizeEntity(CcMmReplPack ccMmReplPack, CcSpMmReplPack ccSpMmReplPack) {
        Set<CcSpMmReplPack> ccSpMmReplPacks = Optional.ofNullable(ccMmReplPack.getCcSpMmReplPack()).orElse(new HashSet<>());

        ccSpMmReplPack.getCcSpReplPackId().setCcMmReplPackId(ccMmReplPack.getCcMmReplPackId());
        ccSpMmReplPacks.add(ccSpMmReplPack);
        ccMmReplPack.setCcSpMmReplPack(ccSpMmReplPacks);
    }

    private void setReplenishmentSizes(ClustersDto clustersDto, List<Replenishment> replenishments, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId) {
        clustersDto.getSizes().forEach(sizeDto -> {

            BuyQtyObj buyQtyObj;

            if (storeBuyQtyBySizeId.containsKey(sizeDto)) {
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            } else {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            }

            List<Replenishment> replObj = new ArrayList<>();

            replenishments.forEach(replenishment -> {
                Replenishment replenishment1 = new Replenishment();
                replenishment1.setReplnUnits((long) (replenishment.getReplnUnits() * getAvgSizePct(sizeDto)));
                replObj.add(replenishment1);
            });
            buyQtyObj.setReplenishments(replObj);
        });
    }

    private List<Replenishment> getReplenishments(MerchMethodsDto merchMethodsDto, BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto) {
        return Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(styleDto.getStyleNbr()))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(customerChoiceDto.getCcId()))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(merchMethodsDto.getFixtureTypeRollupId()))
                .findFirst()
                .map(Fixture::getReplenishments)
                .orElse(new ArrayList<>());
    }

    private void getClusterSizes(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, ClustersDto clustersDto, MerchMethodsDto merchMethodsDto,
                                 BQFPResponse bqfpResponse, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId, List<RFASizePackData> rfaSizePackDataList) {
        //TODO: Round Off Logic
        clustersDto.getSizes().forEach(sizeDto -> {

            BuyQtyObj buyQtyObj;
            if (storeBuyQtyBySizeId.containsKey(sizeDto)) {
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            } else {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            }
            BuyQtyStoreObj buyQtyStoreObj = buyQtyObj.getBuyQtyStoreObj();

            List<StoreQuantity> initialSetQuantities = Optional.ofNullable(buyQtyStoreObj.getBuyQuantities()).orElse(new ArrayList<>());
            log.info("Size Cluster: {}", clustersDto.getClusterID());
            log.info("Style Nbr: {} : {}", styleDto.getStyleNbr(), customerChoiceDto.getCcId());
            rfaSizePackDataList.forEach(rfaSizePackData -> {
                StoreQuantity storeQuantity = new StoreQuantity();
                Cluster volumeCluster = getVolumeCluster(bqfpResponse, styleDto.getStyleNbr(), customerChoiceDto.getCcId(),
                        merchMethodsDto.getFixtureTypeRollupId(), rfaSizePackData.getVolume_group_cluster_id());

                //Calculate IS Buy Quantity
                double isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
                double isQty = (isCalculatedBq * getSizePct(sizeDto)) / 100;
                double perStoreQty = isQty / rfaSizePackData.getStore_cnt();

                //TODO: move threshold to CCM
                double initialSetThreshold = 2.0;
                if ((perStoreQty < initialSetThreshold) && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()))) {
                    double unitsLessThanThreshold = initialSetThreshold - perStoreQty;
                    double totalReducedReplenishment = unitsLessThanThreshold * rfaSizePackData.getStore_cnt();

                    int replenishmentSize = buyQtyObj.getReplenishments().size();

                    int perReplenishmentReduced = (int) (totalReducedReplenishment / replenishmentSize);
                    int perReplenishmentReducedRemainder = (int) (totalReducedReplenishment % replenishmentSize);

                    buyQtyObj.getReplenishments().forEach(replenishment -> replenishment.setReplnUnits((long) (replenishment.getReplnUnits() - perReplenishmentReduced)));
                    buyQtyObj.getReplenishments().get(0).setReplnUnits(buyQtyObj.getReplenishments().get(0).getReplnUnits() - perReplenishmentReducedRemainder);

                    perStoreQty = initialSetThreshold;
                }

                storeQuantity.setTotalUnits((int) isQty);
                storeQuantity.setIsUnits((int) perStoreQty);
                storeQuantity.setVolumeCluster(rfaSizePackData.getVolume_group_cluster_id());
                storeQuantity.setSizeCluster(rfaSizePackData.getSize_cluster_id());
                List<Integer> storeList = safeReadStoreList(rfaSizePackData.getStore_list());
                storeQuantity.setStoreList(storeList);

                //Calculate Bump Qty
                storeQuantity.setBumpSets(calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster));
                initialSetQuantities.add(storeQuantity);
            });
            buyQtyStoreObj.setBuyQuantities(initialSetQuantities);
        });
    }

    private Double getSizePct(SizeDto sizeDto) {
        final Double ZERO = 0.0;
        return sizeDto.getMetrics() != null
                ? Optional.ofNullable(sizeDto.getMetrics().getAdjSizeProfilePct())
                .orElse(Optional.ofNullable(sizeDto.getMetrics().getSizeProfilePct()).orElse(ZERO))
                : ZERO;
    }

    private Double getAvgSizePct(SizeDto sizeDto) {
        final Double ZERO = 0.0;
        return sizeDto.getMetrics() != null
                ? Optional.ofNullable(sizeDto.getMetrics().getAdjAvgSizeProfilePct())
                .orElse(Optional.ofNullable(sizeDto.getMetrics().getAvgSizeProfilePct()).orElse(ZERO))
                : ZERO;
    }

    private List<BumpSetQuantity> calculateBumpPackQty(SizeDto sizeDto, RFASizePackData rfaSizePackData, Cluster volumeCluster) {
        List<BumpSetQuantity> bumpPackQuantities = new ArrayList<>();
        volumeCluster.getBumpList().forEach(bumpSet -> {
            BumpSetQuantity bumpSetQuantity = new BumpSetQuantity();
            //Calculate BS Buy Quantity
            double bumpQtyPerFixture = (bumpSet.getUnits() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix()) / volumeCluster.getInitialSet().getTotalInitialSetUnits().doubleValue();
            double bsCalculatedBq = rfaSizePackData.getStore_cnt() * bumpQtyPerFixture * rfaSizePackData.getFixture_group();
            double bsQty = bsCalculatedBq * getSizePct(sizeDto);
            double bsPerStoreQty = bsQty / rfaSizePackData.getStore_cnt();
            bumpSetQuantity.setTotalUnits((int) bsQty);
            bumpSetQuantity.setBsUnits((int) bsPerStoreQty);
            bumpPackQuantities.add(bumpSetQuantity);
        });
        return bumpPackQuantities;
    }

    private APResponse getRfaSpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, Integer finelineNbr, BQFPResponse bqfpResponse) {
        APRequest apRequest = new APRequest();
        apRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        apRequest.setFinelineNbr(finelineNbr);
        apRequest.setVolumeDeviationLevel(VdLevelCode.getVdLevelCodeFromId(bqfpResponse.getVolumeDeviationStrategyLevelSelection().intValue()));

        return sizeAndPackService.fetchRunFixtureAllocationOutput(apRequest);
    }

    private BQFPResponse getBqfpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, Integer finelineNbr) {
        BQFPRequest bqfpRequest = new BQFPRequest();
        bqfpRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        bqfpRequest.setChannel(ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()).toString());
        bqfpRequest.setFinelineNbr(finelineNbr);

        return bqfpService.getBuyQuantityUnits(bqfpRequest);
    }

    private List<RFASizePackData> getSizeVolumeClustersFromRfa(APResponse apResponse, Integer sizeCluster, String styleNbr, String ccId, String fixtureType) {
        return Optional.ofNullable(apResponse)
                .map(APResponse::getRfaSizePackData)
                .stream()
                .flatMap(Collection::stream)
                .filter(rfaSizePackData -> rfaSizePackData.getSize_cluster_id().equals(sizeCluster)
                        && rfaSizePackData.getStyle_nbr().equalsIgnoreCase(styleNbr)
                        && rfaSizePackData.getCustomer_choice().equalsIgnoreCase(ccId)
                        && rfaSizePackData.getFixture_type().equalsIgnoreCase(fixtureType)
                )
                .collect(Collectors.toList());
    }

    private Cluster getVolumeCluster(BQFPResponse bqfpResponse, String styleNbr, String ccId, Integer fixtureTypeRollupId, Integer volumeClusterId) {

        return Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(styleNbr))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(ccId))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(fixtureTypeRollupId))
                .findFirst()
                .map(Fixture::getClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(cluster -> cluster.getAnalyticsClusterId().equals(volumeClusterId))
                .findFirst()
                .orElse(null);

    }

    private List<Integer> safeReadStoreList(String storeList) {
        try {
            return Arrays.asList(objectMapper.readValue(storeList, Integer[].class));
        } catch (JsonProcessingException e) {
            log.error("Error deserializing size object: {}", storeList);
            throw new CustomException("Error deserializing size object");
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Size object provided was null");
            return new ArrayList<>();
        }
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
}
