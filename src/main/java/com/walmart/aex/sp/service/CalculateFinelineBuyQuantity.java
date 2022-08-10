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

        spFineLineChannelFixture.setInitialSetQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getInitialSetQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setBumpPackQty(spStyleChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getBumpPackQty()).orElse(0))
                .sum()
        );
        spFineLineChannelFixture.setSpStyleChannelFixtures(spStyleChannelFixtures);
    }

    private void getCustomerChoices(StyleDto styleDto, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse,
                                    SpStyleChannelFixture spStyleChannelFixture, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) {

        Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtures = Optional.ofNullable(spStyleChannelFixture.getSpCustomerChoiceChannelFixture()).orElse(new HashSet<>());
        styleDto.getCustomerChoices().forEach(customerChoiceDto -> {

            SpCustomerChoiceChannelFixtureId spCustomerChoiceChannelFixtureId = new SpCustomerChoiceChannelFixtureId(spStyleChannelFixture.getSpStyleChannelFixtureId(), customerChoiceDto.getCcId());
            SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture = Optional.of(spCustomerChoiceChannelFixtures)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(spCustomerChoiceChannelFixture1 -> spCustomerChoiceChannelFixture1.getSpCustomerChoiceChannelFixtureId().equals(spCustomerChoiceChannelFixtureId))
                    .findFirst()
                    .orElse(new SpCustomerChoiceChannelFixture());

            if (spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureId() == null) {
                spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureId(spCustomerChoiceChannelFixtureId);
            }
            //Replenishment
            List<Replenishment> replenishments = getReplenishments(merchMethodsDto, bqfpResponse);
            if (!CollectionUtils.isEmpty(replenishments)) {
                //Replenishment
                setAllReplenishments(styleDto, merchMethodsDto, spStyleChannelFixture, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, replenishments);
            }

            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getCcClusters(styleDto, customerChoiceDto, merchMethodsDto, apResponse, bqfpResponse, spCustomerChoiceChannelFixture);
            }
            spCustomerChoiceChannelFixtures.add(spCustomerChoiceChannelFixture);
        });

        spStyleChannelFixture.setInitialSetQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getInitialSetQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setBumpPackQty(spCustomerChoiceChannelFixtures.stream()
                .filter(Objects::nonNull)
                .mapToInt(spCustomerChoiceChannelFixtureSize -> Optional.ofNullable(spCustomerChoiceChannelFixtureSize.getBumpPackQty()).orElse(0))
                .sum()
        );
        spStyleChannelFixture.setSpCustomerChoiceChannelFixture(spCustomerChoiceChannelFixtures);
    }

    private void setAllReplenishments(StyleDto styleDto, MerchMethodsDto merchMethodsDto, SpStyleChannelFixture spStyleChannelFixture, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse, CustomerChoiceDto customerChoiceDto, List<Replenishment> replenishments) {
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
        CcMmReplPackId ccMmReplPackId = new CcMmReplPackId(ccReplPack.getCcReplPackId(), spStyleChannelFixture.getMerchMethodCode());
        log.info("Replenishment: Check if Cc MM Repln pack Id is existing: {}", ccMmReplPackId);
        CcMmReplPack ccMmReplPack = setCcMmReplnPack(ccMmReplPacks, ccMmReplPackId);

        Optional.ofNullable(customerChoiceDto.getClusters())
                .stream()
                .flatMap(Collection::stream)
                .filter(clustersDto1 -> clustersDto1.getClusterID().equals(0))
                .findFirst().ifPresent(clustersDto -> setReplenishmentSizes(ccMmReplPack, clustersDto, replenishments));
        ccMmReplPack.setReplUnits(ccMmReplPack.getCcSpMmReplPack()
                .stream()
                .filter(Objects::nonNull)
                .mapToInt(ccSpMmReplPack -> Optional.ofNullable(ccSpMmReplPack.getReplUnits()).orElse(0))
                .sum()
        );
        ccMmReplPacks.add(ccMmReplPack);

        //CC
        ccReplPack.setCcMmReplPack(ccMmReplPacks);
        ccReplPack.setReplUnits(ccMmReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccMmReplPack1 -> Optional.ofNullable(ccMmReplPack1.getReplUnits()).orElse(0))
                .sum());
        ccReplPacks.add(ccReplPack);

        //Style
        styleReplPack.setCcReplPack(ccReplPacks);
        styleReplPack.setReplUnits(ccReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(ccReplPack1 -> Optional.ofNullable(ccReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        styleReplPacks.add(styleReplPack);

        //Fineline
        finelineReplPack.setStyleReplPack(styleReplPacks);
        finelineReplPack.setReplUnits(styleReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(styleReplPack1 -> Optional.ofNullable(styleReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        finelineReplPacks.add(finelineReplPack);

        //Sub catg
        subCatgReplPack.setFinelineReplPack(finelineReplPacks);
        subCatgReplPack.setReplUnits(finelineReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(finelineReplPack1 -> Optional.ofNullable(finelineReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        subCatgReplPacks.add(subCatgReplPack);

        //Catg
        merchCatgReplPack.setSubReplPack(subCatgReplPacks);
        merchCatgReplPack.setReplUnits(subCatgReplPacks.stream()
                .filter(Objects::nonNull)
                .mapToInt(subCatgReplPack1 -> Optional.ofNullable(subCatgReplPack1.getReplUnits()).orElse(0))
                .sum()
        );
        merchCatgReplPacks.add(merchCatgReplPack);

        calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
    }

    private void getCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse, SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture) {
        Map<Integer, BuyQtyStoreObj> storeBuyQtyBySizeId = new HashMap<>();
        customerChoiceDto.getClusters().forEach(clustersDto -> {
            if (!CollectionUtils.isEmpty(clustersDto.getSizes())) {
                getClusterSizes(styleDto, customerChoiceDto, clustersDto, merchMethodsDto, apResponse, bqfpResponse, storeBuyQtyBySizeId);
            }
        });
        log.info("Store Map: {}", storeBuyQtyBySizeId);
        Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes = Optional.ofNullable(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureSize()).orElse(new HashSet<>());
        for (Map.Entry<Integer, BuyQtyStoreObj> entry : storeBuyQtyBySizeId.entrySet()) {
            SpCustomerChoiceChannelFixtureSizeId spCustomerChoiceChannelFixtureSizeId = new SpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureId(), entry.getKey());
            SpCustomerChoiceChannelFixtureSize spCustomerChoiceChannelFixtureSize = Optional.of(spCustomerChoiceChannelFixtureSizes)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(spCustomerChoiceChannelFixtureSize1 -> spCustomerChoiceChannelFixtureSize1.getSpCustomerChoiceChannelFixtureSizeId().equals(spCustomerChoiceChannelFixtureSizeId))
                    .findFirst()
                    .orElse(new SpCustomerChoiceChannelFixtureSize());
            if (spCustomerChoiceChannelFixtureSize.getSpCustomerChoiceChannelFixtureSizeId() == null) {
                spCustomerChoiceChannelFixtureSize.setSpCustomerChoiceChannelFixtureSizeId(spCustomerChoiceChannelFixtureSizeId);
            }
            final Integer ZERO = 0;
            spCustomerChoiceChannelFixtureSize.setInitialSetQty(entry.getValue().getBuyQuantities()
                    .stream()
                    .filter(Objects::nonNull)
                    .mapToInt(storeQuantity -> Optional.ofNullable(storeQuantity.getTotalUnits()).orElse(ZERO))
                    .sum()
            );

            spCustomerChoiceChannelFixtureSize.setBumpPackQty(entry.getValue().getBuyQuantities()
                    .stream()
                    .map(StoreQuantity::getBumpSets)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .mapToInt(bumpSetQuantity -> Optional.ofNullable(bumpSetQuantity.getTotalUnits()).orElse(ZERO))
                    .sum()
            );

            try {
                log.info("Store Obj: {}", objectMapper.writeValueAsString(entry.getValue()));
                spCustomerChoiceChannelFixtureSize.setStoreObj(objectMapper.writeValueAsString(entry.getValue()));
            } catch (Exception e) {
                log.error("Error parsing Json: ", e);
                throw new CustomException("Error parsing Json: " + e);
            }
            spCustomerChoiceChannelFixtureSizes.add(spCustomerChoiceChannelFixtureSize);
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
    }

    private void setReplenishmentSizes(CcMmReplPack ccMmReplPack, ClustersDto clustersDto, List<Replenishment> replenishments) {
        Set<CcSpMmReplPack> ccSpMmReplPacks = Optional.ofNullable(ccMmReplPack.getCcSpMmReplPack()).orElse(new HashSet<>());
        clustersDto.getSizes().forEach(sizeDto -> {
            List<Replenishment> replObj = new ArrayList<>();
            CcSpMmReplPackId ccSpMmReplPackId = new CcSpMmReplPackId(ccMmReplPack.getCcMmReplPackId(), sizeDto.getAhsSizeId());
            CcSpMmReplPack ccSpMmReplPack = Optional.of(ccSpMmReplPacks)
                    .stream()
                    .flatMap(Collection::stream)
                    .filter(ccSpMmReplPack1 -> ccSpMmReplPack1.getCcSpReplPackId().equals(ccSpMmReplPackId))
                    .findFirst()
                    .orElse(new CcSpMmReplPack());
            if (ccSpMmReplPack.getCcSpReplPackId() == null) {
                ccSpMmReplPack.setCcSpReplPackId(ccSpMmReplPackId);
            }

            ccSpMmReplPack.setSizeDesc(sizeDto.getSizeDesc());

            replenishments.forEach(replenishment -> {
                Replenishment replenishment1 = new Replenishment();
                replenishment1.setReplnUnits((long) (replenishment.getReplnUnits() * getAvgSizePct(sizeDto)));
                replObj.add(replenishment1);
            });

            ccSpMmReplPack.setReplUnits((int) replenishments.stream()
                    .filter(Objects::nonNull)
                    .mapToLong(replenishment -> Optional.ofNullable(replenishment.getReplnUnits()).orElse(0L))
                    .sum()
            );

            try {
                ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(replObj));
            } catch (Exception e) {
                log.error("Failed to create replenishment Obj for size: {}", sizeDto, e);
                throw new CustomException("Failed to create replenishment Obj for size " + e);
            }

            ccSpMmReplPacks.add(ccSpMmReplPack);
        });
        ccMmReplPack.setCcSpMmReplPack(ccSpMmReplPacks);
    }

    private List<Replenishment> getReplenishments(MerchMethodsDto merchMethodsDto, BQFPResponse bqfpResponse) {
        return Optional.ofNullable(bqfpResponse.getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(style.getStyleId()))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(customerChoice.getCcId()))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(merchMethodsDto.getFixtureTypeRollupId()))
                .findFirst()
                .map(Fixture::getReplenishments)
                .orElse(new ArrayList<>());
    }

    private void getClusterSizes(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, ClustersDto clustersDto, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse, Map<Integer, BuyQtyStoreObj> storeBuyQtyBySizeId) {

        //TODO: Round Off Logic
        clustersDto.getSizes().forEach(sizeDto -> {
            BuyQtyStoreObj buyQtyStoreObj;

            if (storeBuyQtyBySizeId.containsKey(sizeDto.getAhsSizeId())) {
                buyQtyStoreObj = storeBuyQtyBySizeId.get(sizeDto.getAhsSizeId());
            } else {
                storeBuyQtyBySizeId.put(sizeDto.getAhsSizeId(), new BuyQtyStoreObj());
                buyQtyStoreObj = storeBuyQtyBySizeId.get(sizeDto.getAhsSizeId());
            }
            List<StoreQuantity> initialSetQuantities = Optional.ofNullable(buyQtyStoreObj.getBuyQuantities()).orElse(new ArrayList<>());
            log.info("Size Cluster: {}", clustersDto.getClusterID());
            log.info("Style Nbr: {} : {}", styleDto.getStyleNbr(), customerChoiceDto.getCcId());
            List<RFASizePackData> rfaSizePackDataList = getSizeVolumeClustersFromRfa(apResponse, clustersDto.getClusterID(), styleDto.getStyleNbr(), customerChoiceDto.getCcId(),
                    FixtureTypeRollup.getFixtureTypeFromId(merchMethodsDto.getFixtureTypeRollupId()));
            log.info("RFA Size PackData: {}", rfaSizePackDataList);
            rfaSizePackDataList.forEach(rfaSizePackData -> {
                StoreQuantity storeQuantity = new StoreQuantity();
                Cluster volumeCluster = getVolumeCluster(bqfpResponse, styleDto.getStyleNbr(), customerChoiceDto.getCcId(),
                        merchMethodsDto.getFixtureTypeRollupId(), rfaSizePackData.getVolume_group_cluster_id());

                //Calculate IS Buy Quantity
                double isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
                double isQty = isCalculatedBq * getSizePct(sizeDto);
                double perStoreQty = isQty / rfaSizePackData.getStore_cnt();

                storeQuantity.setTotalUnits((int) isQty);
                storeQuantity.setIsUnits((int) perStoreQty);
                storeQuantity.setVolumeCluster(rfaSizePackData.getVolume_group_cluster_id());
                storeQuantity.setSizeCluster(rfaSizePackData.getSize_cluster_id());
                List<Integer> storeList = safeReadStoreList(rfaSizePackData.getStore_list());
                storeQuantity.setStoreList(storeList);

                //Calculate Bump Qty
                storeQuantity.setBumpSets(calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster));
                initialSetQuantities.add(storeQuantity);

                //TODO: Handle Initial set and Replenishment Constraints
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
        bqfpRequest.setChannel(calculateBuyQtyRequest.getChannel());
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
