package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.APRequest;
import com.walmart.aex.sp.dto.assortproduct.APResponse;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.BQFPRequest;
import com.walmart.aex.sp.dto.bqfp.BQFPResponse;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Fixture;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.bqfp.Style;
import com.walmart.aex.sp.dto.buyquantity.AddStoreBuyQuantity;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyObj;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyParallelRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.ClustersDto;
import com.walmart.aex.sp.dto.buyquantity.CustomerChoiceDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineDto;
import com.walmart.aex.sp.dto.buyquantity.FinelineVolumeDeviationDto;
import com.walmart.aex.sp.dto.buyquantity.Lvl3Dto;
import com.walmart.aex.sp.dto.buyquantity.Lvl4Dto;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StoreQuantity;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationRequest;
import com.walmart.aex.sp.dto.buyquantity.StrategyVolumeDeviationResponse;
import com.walmart.aex.sp.dto.buyquantity.StyleDto;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.entity.CcSpMmReplPack;
import com.walmart.aex.sp.entity.CcSpMmReplPackId;
import com.walmart.aex.sp.entity.FixtureTypeRollUpId;
import com.walmart.aex.sp.entity.MerchCatgReplPack;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixture;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureId;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSize;
import com.walmart.aex.sp.entity.SpCustomerChoiceChannelFixtureSizeId;
import com.walmart.aex.sp.entity.SpFineLineChannelFixture;
import com.walmart.aex.sp.entity.SpFineLineChannelFixtureId;
import com.walmart.aex.sp.entity.SpStyleChannelFixture;
import com.walmart.aex.sp.entity.SpStyleChannelFixtureId;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculateFinelineBuyQuantity {


    private final BQFPService bqfpService;
    private final ObjectMapper objectMapper;
    private final StrategyFetchService strategyFetchService;
    private final BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService;
    private final CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity;
    private final AddStoreBuyQuantityService addStoreBuyQuantityService;
    private final BuyQuantityConstraintService buyQuantityConstraintService;
    private final DeptAdminRuleService deptAdminRuleService;

    public CalculateFinelineBuyQuantity(BQFPService bqfpService,
                                        ObjectMapper objectMapper,
                                        BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService,
                                        CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity,
                                        StrategyFetchService strategyFetchService,
                                        AddStoreBuyQuantityService addStoreBuyQuantityService,
                                        BuyQuantityConstraintService buyQuantityConstraintService, DeptAdminRuleService deptAdminRuleService) {
        this.bqfpService = bqfpService;
        this.objectMapper = objectMapper;
        this.strategyFetchService = strategyFetchService;
        this.buyQtyReplenishmentMapperService = buyQtyReplenishmentMapperService;
        this.calculateOnlineFinelineBuyQuantity = calculateOnlineFinelineBuyQuantity;
        this.addStoreBuyQuantityService = addStoreBuyQuantityService;
        this.buyQuantityConstraintService = buyQuantityConstraintService;
        this.deptAdminRuleService = deptAdminRuleService;
    }

    public CalculateBuyQtyResponse calculateFinelineBuyQty(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) throws CustomException {
        log.info("Calculating buy quantity for planId: {}, finelineNbr: {}, channel: {}", calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr(), calculateBuyQtyParallelRequest.getChannel());
        CompletableFuture<BuyQtyResponse> buyQtyResponseCompletableFuture = getBuyQtyResponseCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        CompletableFuture<BQFPResponse> bqfpResponseCompletableFuture = getBqfpResponseCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);

        //Set Volume Deviation from Strategy
        CompletableFuture<StrategyVolumeDeviationResponse> strategyVolumeDeviationResponseCompletableFuture = getStrategyVolumeDeviationCompletableFuture(calculateBuyQtyRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr());

        //wrapper future completes when all futures have completed
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(buyQtyResponseCompletableFuture, bqfpResponseCompletableFuture, strategyVolumeDeviationResponseCompletableFuture);
        try {
            combinedFuture.join();
            final BuyQtyResponse buyQtyResponse = buyQtyResponseCompletableFuture.get();
            final BQFPResponse bqfpResponse = bqfpResponseCompletableFuture.get();
            final StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseCompletableFuture.get();
            if (null != strategyVolumeDeviationResponse) {
                setStrategyVolumeDeviation(bqfpResponse, strategyVolumeDeviationResponse);
            }
            APResponse apResponse = null;
            if (ChannelType.STORE.getDescription().equalsIgnoreCase(calculateBuyQtyParallelRequest.getChannel()))
                apResponse = getRfaSpResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest.getFinelineNbr(), bqfpResponse);

            if (log.isDebugEnabled()) {
                logExtResponse("Size Profiles", buyQtyResponse);
                logExtResponse("BQFP", bqfpResponse);
                logExtResponse("RFA", apResponse);
            }
            FinelineDto finelineDto = getFineline(buyQtyResponse);
            if (finelineDto != null) {
                if (!CollectionUtils.isEmpty(finelineDto.getMerchMethods()) && ChannelType.STORE.getDescription().equalsIgnoreCase(calculateBuyQtyParallelRequest.getChannel())) {
                    getMerchMethod(calculateBuyQtyParallelRequest, finelineDto, apResponse, bqfpResponseCompletableFuture.get(), calculateBuyQtyResponse, calculateBuyQtyRequest);
                } else if (ChannelType.ONLINE.getDescription().equalsIgnoreCase(calculateBuyQtyParallelRequest.getChannel())) {
                    calculateBuyQtyResponse = calculateOnlineFinelineBuyQuantity.calculateOnlineBuyQty(calculateBuyQtyParallelRequest, finelineDto, bqfpResponseCompletableFuture.get(), calculateBuyQtyResponse);
                } else log.info("Merchmethods or channel is empty: {}", buyQtyResponseCompletableFuture);
            } else log.info("Size Profile Fineline is null: {}", bqfpResponseCompletableFuture);
            return calculateBuyQtyResponse;
        } catch (InterruptedException ie) {
            log.error("CalculateBuyQty failed due to interruption. plan: {}, finelineNbr: {}",
                    calculateBuyQtyRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr());
            Thread.currentThread().interrupt();
            return calculateBuyQtyResponse;
        } catch (ExecutionException e) {
            log.error("CalculateBuyQty failed due to external dependency failure.  plan: {}, finelineNbr: {}",
                    calculateBuyQtyRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr(), e.getCause());
            return calculateBuyQtyResponse;
        } catch (Exception e) {
            log.error("CalculateBuyQty failed.  plan: {}, finelineNbr: {}",
                    calculateBuyQtyRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr(), e);
            throw new CustomException("CalculateBuyQty failed");
        }
    }

    private void setStrategyVolumeDeviation(BQFPResponse bqfpResponse, StrategyVolumeDeviationResponse strategyVolumeDeviationResponse) {
        Optional.ofNullable(strategyVolumeDeviationResponse)
                .map(StrategyVolumeDeviationResponse::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(FinelineVolumeDeviationDto::getVolumeDeviationLevel).ifPresent(volumeDeviationLevel -> {
                    log.info("Strategy Volume Deviation for plan: {} : fineline: {} is : {}", bqfpResponse.getPlanId(),bqfpResponse.getFinelineNbr(),volumeDeviationLevel);
                    bqfpResponse.setVolumeDeviationStrategyLevelSelection(BigDecimal.valueOf(VdLevelCode.getVdLevelCodeIdFromName(volumeDeviationLevel)));
        });
    }

    private CompletableFuture<BQFPResponse> getBqfpResponseCompletableFuture(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        return CompletableFuture.supplyAsync(() -> getBqfpResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest.getFinelineNbr()));
    }

    private CompletableFuture<StrategyVolumeDeviationResponse> getStrategyVolumeDeviationCompletableFuture(Long planId, Integer finelineNbr) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return strategyFetchService.getStrategyVolumeDeviation(planId, finelineNbr);
            } catch (SizeAndPackException e) {
                throw new CustomException("Failed to fetch buyQtyResponse");
            }
        });
    }

    private CompletableFuture<BuyQtyResponse> getBuyQtyResponseCompletableFuture(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getSizeProfiles(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
            } catch (SizeAndPackException e) {
                throw new CustomException("Failed to fetch buyQtyResponse");
            }
        });
    }

    private void getMerchMethod(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, FinelineDto finelineDto, APResponse apResponse, BQFPResponse bqfpResponse,
                                CalculateBuyQtyResponse calculateBuyQtyResponse, CalculateBuyQtyRequest calculateBuyQtyRequest) {
        List<SpFineLineChannelFixture> spFineLineChannelFixtures = new ArrayList<>();
        Map<Integer, List<MerchMethodsDto>> merchCodeMap = new HashMap<>();
        Set<CustomerChoice> customerChoices = bqfpResponse.getStyles().stream().map(Style::getCustomerChoices)
                .flatMap(Collection::stream).collect(Collectors.toSet());
        Integer maxBumpCount = getMaxBumpCountVal(customerChoices);
        finelineDto.getMerchMethods().stream().filter(mm -> mm != null && mm.getMerchMethodCode() != null).forEach(merch -> {
            if (!merchCodeMap.containsKey(merch.getMerchMethodCode()))
                merchCodeMap.put(merch.getMerchMethodCode(), new ArrayList<>());
            merchCodeMap.get(merch.getMerchMethodCode()).add(merch);
        });

        merchCodeMap.forEach((merchMethodCode, merchMethodsDtos) -> {
            // Hard coded for temporary testing of calculating InitialSet, BumpSet and Replenishment
            FixtureTypeRollUpId fixtureTypeRollUpId = new FixtureTypeRollUpId(merchMethodCode);
            SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId(fixtureTypeRollUpId, calculateBuyQtyRequest.getPlanId(), calculateBuyQtyRequest.getLvl0Nbr(),
                    calculateBuyQtyRequest.getLvl1Nbr(), calculateBuyQtyRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(), calculateBuyQtyParallelRequest.getLvl4Nbr(), finelineDto.getFinelineNbr(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()));
            log.info("Checking if Fineline Chan Fixture Id is existing: {}", spFineLineChannelFixtureId);

            SpFineLineChannelFixture spFineLineChannelFixture = new SpFineLineChannelFixture();

            if (spFineLineChannelFixture.getSpFineLineChannelFixtureId() == null) {
                spFineLineChannelFixture.setSpFineLineChannelFixtureId(spFineLineChannelFixtureId);
            }
            spFineLineChannelFixture.setMerchMethodCode(merchMethodCode);

            if (!CollectionUtils.isEmpty(finelineDto.getStyles())) {
                getStyles(finelineDto.getStyles(), merchMethodsDtos, apResponse, bqfpResponse, spFineLineChannelFixture, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
            } else log.info("Styles Size Profiles are empty to calculate buy Qty: {}", finelineDto);
            spFineLineChannelFixture.setBumpPackCnt(maxBumpCount);
            spFineLineChannelFixtures.add(spFineLineChannelFixture);
        });
        calculateBuyQtyResponse.setSpFineLineChannelFixtures(spFineLineChannelFixtures);
    }

    private Integer getMaxBumpCountVal(Set<CustomerChoice> customerChoices) {
        int max = 0;
        Optional<Cluster> res = customerChoices.stream()
                .map(CustomerChoice::getFixtures)
                .flatMap(Collection::stream)
                .map(Fixture::getClusters)
                .flatMap(Collection::stream)
                .filter(cluster -> cluster != null && cluster.getBumpList() != null)
                .max(Comparator.comparing(cluster -> cluster.getBumpList().size()))
                .stream().findFirst();

        if(res.isPresent()) {
            max = res.get().getBumpList().size();
        }
        return max;
    }

    private void getStyles(List<StyleDto> styles, List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, SpFineLineChannelFixture spFineLineChannelFixture,
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
            spStyleChannelFixture.setMerchMethodCode(spFineLineChannelFixture.getMerchMethodCode());
            if (!CollectionUtils.isEmpty(styleDto.getCustomerChoices())) {
                getCustomerChoices(styleDto, merchMethodsDtos, apResponse, bqfpResponse, spStyleChannelFixture, calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
            }
            spStyleChannelFixtures.add(spStyleChannelFixture);
        });
        log.info("calculating fineline IS and BS Qty");
        setFinelineChanFixtures(spFineLineChannelFixture, spStyleChannelFixtures);
        spFineLineChannelFixture.setSpStyleChannelFixtures(spStyleChannelFixtures);
    }

    private void getCustomerChoices(StyleDto styleDto, List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse,
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
            spCustomerChoiceChannelFixture.setMerchMethodCode(spStyleChannelFixture.getMerchMethodCode());
            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getCcClusters(styleDto, customerChoiceDto, merchMethodsDtos, apResponse, bqfpResponse, spCustomerChoiceChannelFixture, calculateBuyQtyResponse, calculateBuyQtyParallelRequest);
            }
            spCustomerChoiceChannelFixture.setBumpPackCnt(getCcMaxBumpPackCnt(bqfpResponse,styleDto,customerChoiceDto));
            spCustomerChoiceChannelFixtures.add(spCustomerChoiceChannelFixture);
        });
        log.info("calculating Style IS and BS Qty");
        setStyleChanFixtures(spStyleChannelFixture, spCustomerChoiceChannelFixtures);
        spStyleChannelFixture.setSpCustomerChoiceChannelFixture(spCustomerChoiceChannelFixtures);
    }

    protected Integer getCcMaxBumpPackCnt(BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto ) {
        Set<CustomerChoice> customerChoices = bqfpResponse.getStyles().stream().filter(style -> style.getStyleId().equalsIgnoreCase(styleDto.getStyleNbr()))
                .map(Style::getCustomerChoices)
                .flatMap(Collection::stream)
                .filter(cc -> cc.getCcId().equalsIgnoreCase(customerChoiceDto.getCcId())).collect(Collectors.toSet());
        return getMaxBumpCountVal(customerChoices);
    }

    private void getCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse, SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture, CalculateBuyQtyResponse calculateBuyQtyResponse, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId = new HashMap<>();
        Integer initialThreshold = deptAdminRuleService.getInitialThreshold(bqfpResponse.getPlanId(), bqfpResponse.getLvl1Nbr());
        //Replenishment
        List<Replenishment> replenishments = BuyQtyCommonUtil.getReplenishments(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);
        log.info("Get All Replenishments if exists for customerchoice: {} and fixtureType: {}", customerChoiceDto.getCcId(), spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId());
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
                        merchMethodsDtos.stream().map(MerchMethodsDto::getFixtureTypeRollupId).distinct().collect(Collectors.toList()));
                //Set Initial Set and Bump Set for Size Map
                getClusterSizes(styleDto, customerChoiceDto, clustersDto, bqfpResponse, storeBuyQtyBySizeId, rfaSizePackDataList, initialThreshold);
            }
        });
        Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes = Optional.ofNullable(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureSize()).orElse(new HashSet<>());
        Set<CcSpMmReplPack> ccSpMmReplPacks = new HashSet<>();
        Integer replenishmentThreshold = deptAdminRuleService.getReplenishmentThreshold(bqfpResponse.getPlanId(), bqfpResponse.getLvl1Nbr());
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            setSizeChanFixtureBuyQty(spCustomerChoiceChannelFixture, replenishments, spCustomerChoiceChannelFixtureSizes, ccSpMmReplPacks, entry, replenishmentThreshold);
        }

        if (!CollectionUtils.isEmpty(ccSpMmReplPacks)) {
            //Replenishment
            List<MerchCatgReplPack> merchCatgReplPacks = buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDtos.get(0), calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks);
            calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
        }

        spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureSize(spCustomerChoiceChannelFixtureSizes);
        setCcChanFixtures(spCustomerChoiceChannelFixture, spCustomerChoiceChannelFixtureSizes);
    }

    private void getClusterSizes(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, ClustersDto clustersDto,
                                 BQFPResponse bqfpResponse, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId, List<RFASizePackData> rfaSizePackDataList, Integer initialThreshold) {
        clustersDto.getSizes().forEach(sizeDto -> {

            BuyQtyObj buyQtyObj;
            if (storeBuyQtyBySizeId.containsKey(sizeDto)) {
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            } else {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            }
            AddStoreBuyQuantity addStoreBuyQuantity = new AddStoreBuyQuantity();
            addStoreBuyQuantity.setRfaSizePackDataList(rfaSizePackDataList);
            addStoreBuyQuantity.setStyleDto(styleDto);
            addStoreBuyQuantity.setSizeDto(sizeDto);
            addStoreBuyQuantity.setBqfpResponse(bqfpResponse);
            addStoreBuyQuantity.setCustomerChoiceDto(customerChoiceDto);
            addStoreBuyQuantityService.addStoreBuyQuantities(addStoreBuyQuantity, buyQtyObj, initialThreshold);
            storeBuyQtyBySizeId.put(sizeDto, buyQtyObj);
        });
    }

    private void setSizeChanFixtureBuyQty(SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture,
                                          List<Replenishment> replenishments, Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes,
                                          Set<CcSpMmReplPack> ccSpMmReplPacks,
                                          Map.Entry<SizeDto, BuyQtyObj> entry,
                                          Integer replenishmentThreshold) {
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

        entry.getValue().setTotalReplenishment(0L);
        //Update Store Qty
        final BuyQtyObj allStoresBuyQty = entry.getValue();
        if (!CollectionUtils.isEmpty(allStoresBuyQty.getReplenishments()) && !CollectionUtils.isEmpty(allStoresBuyQty.getBuyQtyStoreObj().getBuyQuantities())) {
            allStoresBuyQty.setTotalReplenishment(buyQuantityConstraintService.getTotalReplenishment(allStoresBuyQty.getReplenishments()));
            buyQuantityConstraintService.processReplenishmentConstraints(entry, allStoresBuyQty.getTotalReplenishment(), replenishmentThreshold);
        }

        double bsBuyQty = getBsQty(entry);
        double isBuyQty = getIsQty(entry);
        double totalBuyQty = isBuyQty + bsBuyQty + entry.getValue().getTotalReplenishment();
        spCustomerChoiceChannelFixtureSize.setInitialSetQty((int) Math.round(isBuyQty));
        spCustomerChoiceChannelFixtureSize.setBumpPackQty((int) Math.round(bsBuyQty));
        spCustomerChoiceChannelFixtureSize.setMerchMethodCode(spCustomerChoiceChannelFixture.getMerchMethodCode());
        spCustomerChoiceChannelFixtureSize.setAhsSizeDesc(entry.getKey().getSizeDesc());
        spCustomerChoiceChannelFixtureSize.setReplnQty((int) entry.getValue().getTotalReplenishment());
        spCustomerChoiceChannelFixtureSize.setBuyQty((int) Math.round(totalBuyQty));

        //TODO: Adjust Flow Strategy
        try {
            spCustomerChoiceChannelFixtureSize.setStoreObj(objectMapper.writeValueAsString(entry.getValue().getBuyQtyStoreObj()));
        } catch (Exception e) {
            log.error("Error parsing Json: ", e);
            throw new CustomException("Error parsing Json: " + e);
        }
        spCustomerChoiceChannelFixtureSizes.add(spCustomerChoiceChannelFixtureSize);

        //Replenishment
        if (!CollectionUtils.isEmpty(replenishments) && entry.getValue().getTotalReplenishment() > 0) {
            setCcMmSpReplenishment(ccSpMmReplPacks, entry, (int) entry.getValue().getTotalReplenishment(), (int) Math.round(totalBuyQty));
        }
    }

    private double getIsQty(Map.Entry<SizeDto, BuyQtyObj> entry) {
        return entry.getValue().getBuyQtyStoreObj()
                .getBuyQuantities()
                .stream()
                .filter(Objects::nonNull)
                .mapToDouble(storeQuantity -> Optional.ofNullable(storeQuantity.getTotalUnits()).orElse((double) 0))
                .sum();
    }

    private double getBsQty(Map.Entry<SizeDto, BuyQtyObj> entry) {
        return entry.getValue().getBuyQtyStoreObj().getBuyQuantities()
                .stream()
                .map(StoreQuantity::getBumpSets)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .mapToDouble(bumpSetQuantity -> Optional.ofNullable(bumpSetQuantity.getTotalUnits()).orElse((double) 0))
                .sum();
    }

    private Double getAvgSizePct(SizeDto sizeDto) {
        final Double ZERO = 0.0;
        return sizeDto.getMetrics() != null
                ? Optional.ofNullable(sizeDto.getMetrics().getAdjAvgSizeProfilePct())
                .orElse(Optional.ofNullable(sizeDto.getMetrics().getAvgSizeProfilePct()).orElse(ZERO))
                : ZERO;
    }

    private BuyQtyResponse getSizeProfiles(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) throws SizeAndPackException {
        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        buyQtyRequest.setChannel(calculateBuyQtyRequest.getChannel());
        buyQtyRequest.setLvl3Nbr(calculateBuyQtyParallelRequest.getLvl3Nbr());
        buyQtyRequest.setLvl4Nbr(calculateBuyQtyParallelRequest.getLvl4Nbr());
        buyQtyRequest.setFinelineNbr(calculateBuyQtyParallelRequest.getFinelineNbr());
        return strategyFetchService.getAllCcSizeProfiles(buyQtyRequest);
    }

    private APResponse getRfaSpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, Integer finelineNbr, BQFPResponse bqfpResponse) {
        APRequest apRequest = new APRequest();
        apRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        apRequest.setFinelineNbr(finelineNbr);
        if (null != bqfpResponse.getVolumeDeviationStrategyLevelSelection()) {
            apRequest.setVolumeDeviationLevel(VdLevelCode.getVdLevelCodeFromId(bqfpResponse.getVolumeDeviationStrategyLevelSelection().intValue()));
        }

        try {
            return strategyFetchService.getAPRunFixtureAllocationOutput(apRequest);
        } catch (Exception e) {
            log.error("Exception While fetching RFA output:", e);
            throw new CustomException("Failed to fetch RFA output: " + e);
        }
    }

    private BQFPResponse getBqfpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, Integer finelineNbr) {
        BQFPRequest bqfpRequest = new BQFPRequest();
        bqfpRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        bqfpRequest.setChannel(ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()).toString());
        bqfpRequest.setFinelineNbr(finelineNbr);
        log.info("BQFPRequest payload for planId {} : {}", bqfpRequest, calculateBuyQtyRequest.getPlanId());
        return bqfpService.getBuyQuantityUnits(bqfpRequest);
    }

    private List<RFASizePackData> getSizeVolumeClustersFromRfa(APResponse apResponse, Integer sizeCluster, String styleNbr, String ccId, List<Integer> fixtureTypeRollUpIds) {
        return Optional.ofNullable(apResponse)
                .map(APResponse::getRfaSizePackData)
                .stream()
                .flatMap(Collection::stream)
                .filter(rfaSizePackData -> rfaSizePackData.getSize_cluster_id().equals(sizeCluster)
                        && rfaSizePackData.getStyle_nbr().equalsIgnoreCase(styleNbr)
                        && rfaSizePackData.getCustomer_choice().equalsIgnoreCase(ccId)
                        && fixtureTypeRollUpIds.contains(FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()))
                )
                .collect(Collectors.toList());
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

    private void setFinelineChanFixtures(SpFineLineChannelFixture spFineLineChannelFixture, Set<SpStyleChannelFixture> spStyleChannelFixtures) {
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
    }

    private void setStyleChanFixtures(SpStyleChannelFixture spStyleChannelFixture, Set<SpCustomerChoiceChannelFixture> spCustomerChoiceChannelFixtures) {
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
    }

    private void setCcChanFixtures(SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture, Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes) {
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

    private void setCcMmSpReplenishment(Set<CcSpMmReplPack> ccSpMmReplPacks, Map.Entry<SizeDto, BuyQtyObj> entry, int totalReplenishment, int totalBuyQty) {
        CcSpMmReplPackId ccSpMmReplPackId = new CcSpMmReplPackId();
        ccSpMmReplPackId.setAhsSizeId(entry.getKey().getAhsSizeId());

        CcSpMmReplPack ccSpMmReplPack = new CcSpMmReplPack();
        ccSpMmReplPack.setSizeDesc(entry.getKey().getSizeDesc());

        ccSpMmReplPack.setCcSpReplPackId(ccSpMmReplPackId);

        ccSpMmReplPack.setFinalBuyUnits(totalBuyQty);
        ccSpMmReplPack.setReplUnits(totalReplenishment);
        try {
            ccSpMmReplPack.setReplenObj(objectMapper.writeValueAsString(entry.getValue().getReplenishments()));
        } catch (Exception e) {
            log.error("Failed to create replenishment Obj for size: {}", entry.getKey(), e);
            throw new CustomException("Failed to create replenishment Obj for size " + e);
        }

        ccSpMmReplPacks.add(ccSpMmReplPack);
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
                Replenishment replenishment1 = new Replenishment(replenishment.getReplnWeek(), replenishment.getReplnWeekDesc());

                Long units = Optional.ofNullable(replenishment.getDcInboundUnits()).orElse(0L);
                replenishment1.setAdjReplnUnits(Math.round((units * getAvgSizePct(sizeDto)) / 100));
                replObj.add(replenishment1);
            });
            buyQtyObj.setReplenishments(replObj);
        });
    }

    private void logExtResponse(String title, Object response) {
        final String key = title == null ? "Response" : title;
        try {
            Marker tag = new BasicMarkerFactory().getMarker(title);
            log.debug(tag, objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            log.error("Unable to serialize response: {}", key, e);
        }
    }
}