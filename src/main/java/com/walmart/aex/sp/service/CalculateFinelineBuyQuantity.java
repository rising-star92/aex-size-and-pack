package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.*;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.currentlineplan.LikeAssociation;
import com.walmart.aex.sp.dto.replenishment.MerchMethodsDto;
import com.walmart.aex.sp.dto.replenishment.cons.ReplenishmentCons;
import com.walmart.aex.sp.entity.*;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.enums.VdLevelCode;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.exception.SizeAndPackException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.util.AuthUtils;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.SizeAndPackConstants.DEFAULT_COLOR_FAMILY;
import static com.walmart.aex.sp.util.SizeAndPackConstants.VP_DEFAULT;

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
    private final ReplenishmentService replenishmentService;
    private final ReplenishmentsOptimizationService replenishmentsOptimizationServices;
    private final MidasServiceCall midasServiceCall;
    private final LinePlanService linePlanService;
    private final BigQueryClusterService bigQueryClusterService;
    private final CalculateInitialSetQuantityService calculateInitialSetQuantityService;
    private final CalculateBumpPackQtyService calculateBumpPackQtyService;
    private final ValidationService validationService;
    private final CalculateFinelineBuyQuantityMapper calculateFinelineBuyQuantityMapper;
    @ManagedConfiguration
    BuyQtyProperties buyQtyProperties;

    public CalculateFinelineBuyQuantity(BQFPService bqfpService,
                                        ObjectMapper objectMapper,
                                        BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService,
                                        CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity,
                                        StrategyFetchService strategyFetchService,
                                        AddStoreBuyQuantityService addStoreBuyQuantityService,
                                        BuyQuantityConstraintService buyQuantityConstraintService,
                                        DeptAdminRuleService deptAdminRuleService,
                                        ReplenishmentService replenishmentService,
                                        ReplenishmentsOptimizationService replenishmentsOptimizationServices,
                                        MidasServiceCall midasServiceCall,
                                        LinePlanService linePlanService,
                                        BigQueryClusterService bigQueryClusterService,
                                        CalculateInitialSetQuantityService calculateInitialSetQuantityService,
                                        CalculateBumpPackQtyService calculateBumpPackQtyService,
                                        ValidationService validationService,
                                        CalculateFinelineBuyQuantityMapper calculateFinelineBuyQuantityMapper) {
        this.bqfpService = bqfpService;
        this.objectMapper = objectMapper;
        this.strategyFetchService = strategyFetchService;
        this.buyQtyReplenishmentMapperService = buyQtyReplenishmentMapperService;
        this.calculateOnlineFinelineBuyQuantity = calculateOnlineFinelineBuyQuantity;
        this.addStoreBuyQuantityService = addStoreBuyQuantityService;
        this.buyQuantityConstraintService = buyQuantityConstraintService;
        this.deptAdminRuleService = deptAdminRuleService;
        this.replenishmentService = replenishmentService;
        this.replenishmentsOptimizationServices = replenishmentsOptimizationServices;
        this.midasServiceCall = midasServiceCall;
        this.linePlanService = linePlanService;
        this.bigQueryClusterService = bigQueryClusterService;
        this.calculateInitialSetQuantityService = calculateInitialSetQuantityService;
        this.calculateBumpPackQtyService = calculateBumpPackQtyService;
        this.validationService = validationService;
        this.calculateFinelineBuyQuantityMapper = calculateFinelineBuyQuantityMapper;
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
            String volumeDeviation = getStrategyVolumeDeviation(strategyVolumeDeviationResponse);
            if (null != volumeDeviation) {
                bqfpResponse.setVolumeDeviationStrategyLevelSelection(BigDecimal.valueOf(VdLevelCode.getVdLevelCodeIdFromName(volumeDeviation)));
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
                    getMerchMethod(calculateBuyQtyParallelRequest, finelineDto, apResponse, bqfpResponseCompletableFuture.get(), calculateBuyQtyResponse);
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

    public CalculateBuyQtyResponse calculateFinelineBuyQtyV2(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) throws CustomException {
        log.info("Calculating buy quantity for planId: {}, finelineNbr: {}, channel: {}", calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr(), calculateBuyQtyParallelRequest.getChannel());
        CompletableFuture<BuyQtyResponse> buyQtyResponseCompletableFuture = getBuyQtyResponseCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        CompletableFuture<BQFPResponse> bqfpResponseCompletableFuture = getBqfpResponseCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        //Set Volume Deviation from Strategy
        CompletableFuture<StrategyVolumeDeviationResponse> strategyVolumeDeviationResponseCompletableFuture = getStrategyVolumeDeviationCompletableFuture(calculateBuyQtyRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr());

        // Like Fineline details
        CompletableFuture<LikeAssociation> likeFinelineDetailsCompletableFuture = getLikeAssociationCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        // Color Families for original fineline
        CompletableFuture<List<String>> colorFamiliesCompletableFuture = getColorFamiliesCompleteFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);

        //wrapper future completes when all futures have completed
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(buyQtyResponseCompletableFuture, bqfpResponseCompletableFuture, strategyVolumeDeviationResponseCompletableFuture, likeFinelineDetailsCompletableFuture, colorFamiliesCompletableFuture);
        try {
            combinedFuture.join();
            log.info("All futures completed");
            final BuyQtyResponse buyQtyResponse = buyQtyResponseCompletableFuture.get();
            final BQFPResponse bqfpResponse = bqfpResponseCompletableFuture.get();
            final StrategyVolumeDeviationResponse strategyVolumeDeviationResponse = strategyVolumeDeviationResponseCompletableFuture.get();
            final LikeAssociation likeFinelineResponse = likeFinelineDetailsCompletableFuture.get();
            final List<ColorDefinition> colorDefinitions = getColorDefinitions(calculateBuyQtyRequest, colorFamiliesCompletableFuture, buyQtyResponse, likeFinelineResponse);
            String volumeDeviation = getStrategyVolumeDeviation(strategyVolumeDeviationResponse);
            if (null != volumeDeviation) {
                bqfpResponse.setVolumeDeviationStrategyLevelSelection(BigDecimal.valueOf(VdLevelCode.getVdLevelCodeIdFromName(volumeDeviation)));
            }
            APResponse apResponse = getRFAResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest, likeFinelineResponse, colorDefinitions, volumeDeviation);

            if (log.isDebugEnabled()) {
                logExtResponse("Size Profiles", buyQtyResponse);
                logExtResponse("BQFP", bqfpResponse);
                logExtResponse("RFA", apResponse);
            }
            FinelineDto finelineDto = getFineline(buyQtyResponse);
            if (finelineDto != null) {
                if (!CollectionUtils.isEmpty(finelineDto.getMerchMethods()) && ChannelType.STORE.getDescription().equalsIgnoreCase(calculateBuyQtyParallelRequest.getChannel())) {
                    getMerchMethod(calculateBuyQtyParallelRequest, finelineDto, apResponse, bqfpResponseCompletableFuture.get(), calculateBuyQtyResponse);
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

    private List<ColorDefinition> getColorDefinitions(CalculateBuyQtyRequest calculateBuyQtyRequest, CompletableFuture<List<String>> colorFamiliesCompletableFuture, BuyQtyResponse buyQtyResponse, LikeAssociation likeFinelineResponse) throws InterruptedException, ExecutionException, SizeAndPackException {
        List<String> colorFamiliesFromMidas;
        if (null == likeFinelineResponse) {
            colorFamiliesFromMidas = colorFamiliesCompletableFuture.get();
        } else {
            colorFamiliesFromMidas = getColorFamilies(calculateBuyQtyRequest.getSeasonCode(), calculateBuyQtyRequest.getFiscalYear(), likeFinelineResponse.getLvl1Nbr(), Integer.parseInt(likeFinelineResponse.getId()));
        }
        // Match the colors between DS and Strategy, if not found then use DEFAULT
        return getAssociatedColorFamilies(colorFamiliesFromMidas, buyQtyResponse);
    }

    private RFASizePackRequest createRFASizePackRequest(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, LikeAssociation likeFinelineResponse, List<ColorDefinition> colorDefinitions) {
        return RFASizePackRequest.builder()
                .plan_id(Math.toIntExact(calculateBuyQtyRequest.getPlanId()))
                .fiscal_year(calculateBuyQtyRequest.getFiscalYear())
                .seasonCode(calculateBuyQtyRequest.getSeasonCode())
                .rpt_lvl_0_nbr(calculateBuyQtyParallelRequest.getLvl0Nbr())
                .rpt_lvl_1_nbr(calculateBuyQtyParallelRequest.getLvl1Nbr())
                .rpt_lvl_2_nbr(calculateBuyQtyParallelRequest.getLvl2Nbr())
                .rpt_lvl_3_nbr(calculateBuyQtyParallelRequest.getLvl3Nbr())
                .rpt_lvl_4_nbr(calculateBuyQtyParallelRequest.getLvl4Nbr())
                .fineline_nbr(calculateBuyQtyParallelRequest.getFinelineNbr())
                .like_lvl1_nbr(null != likeFinelineResponse ? likeFinelineResponse.getLvl1Nbr() : null)
                .like_lvl3_nbr(null != likeFinelineResponse ? likeFinelineResponse.getLvl3Nbr() : null)
                .like_lvl4_nbr(null != likeFinelineResponse ? likeFinelineResponse.getLvl4Nbr() : null)
                .like_fineline_nbr(null != likeFinelineResponse ? Integer.parseInt(likeFinelineResponse.getId()) : null)
                .colors(colorDefinitions)
                .build();
    }

    private List<ColorDefinition> getAssociatedColorFamilies(List<String> colorFamiliesFromMidas, BuyQtyResponse buyQtyResponse) {
        List<ColorDefinition> colorDefinitions = new ArrayList<>();
        buyQtyResponse.getLvl3List().stream()
                .map(Lvl3Dto::getLvl4List)
                .flatMap(Collection::stream)
                .map(Lvl4Dto::getFinelines)
                .flatMap(Collection::stream)
                .map(FinelineDto::getStyles)
                .flatMap(Collection::stream)
                .map(StyleDto::getCustomerChoices)
                .flatMap(Collection::stream)
                .forEach(cc -> {
                    if (cc.getColorFamily() != null && colorFamiliesFromMidas.contains(cc.getColorFamily().toUpperCase())) {
                        colorDefinitions.add(ColorDefinition.builder().cc(cc.getCcId()).color_family_desc(cc.getColorFamily().toUpperCase()).build());
                    } else {
                        colorDefinitions.add(ColorDefinition.builder().cc(cc.getCcId()).color_family_desc(DEFAULT_COLOR_FAMILY).build());
                    }
                });

        return colorDefinitions;
    }

    private String getStrategyVolumeDeviation(StrategyVolumeDeviationResponse strategyVolumeDeviationResponse) {
        return Optional.ofNullable(strategyVolumeDeviationResponse)
                .map(StrategyVolumeDeviationResponse::getFinelines)
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .map(FinelineVolumeDeviationDto::getVolumeDeviationLevel).orElse(null);
    }

    private CompletableFuture<BQFPResponse> getBqfpResponseCompletableFuture(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        return CompletableFuture.supplyAsync(() -> getBqfpResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest.getFinelineNbr()));
    }

    private CompletableFuture<StrategyVolumeDeviationResponse> getStrategyVolumeDeviationCompletableFuture(Long planId, Integer finelineNbr) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return strategyFetchService.getStrategyVolumeDeviation(planId, finelineNbr);
            } catch (SizeAndPackException e) {
                throw new CustomException("Failed to fetch Strategy Volume Deviation");
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

    private CompletableFuture<LikeAssociation> getLikeAssociationCompletableFuture(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return linePlanService.getLikeAssociation(calculateBuyQtyRequest.getPlanId(), calculateBuyQtyParallelRequest.getFinelineNbr());
            } catch (SizeAndPackException e) {
                throw new CustomException("Failed to fetch Like Association");
            }
        });
    }

    private CompletableFuture<List<String>> getColorFamiliesCompleteFuture(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return getColorFamilies(calculateBuyQtyRequest.getSeasonCode(), calculateBuyQtyRequest.getFiscalYear(), calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getFinelineNbr());
            } catch (SizeAndPackException e) {
                throw new CustomException("Failed to fetch Color Families");
            }
        });
    }

    private void getMerchMethod(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, FinelineDto finelineDto, APResponse apResponse, BQFPResponse bqfpResponse,
                                CalculateBuyQtyResponse calculateBuyQtyResponse) {
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
            SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId(fixtureTypeRollUpId, calculateBuyQtyParallelRequest.getPlanId(), calculateBuyQtyParallelRequest.getLvl0Nbr(),
                    calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getLvl2Nbr(), calculateBuyQtyParallelRequest.getLvl3Nbr(), calculateBuyQtyParallelRequest.getLvl4Nbr(), finelineDto.getFinelineNbr(), ChannelType.getChannelIdFromName(calculateBuyQtyParallelRequest.getChannel()));
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
            updateFinelineMetadata(spFineLineChannelFixture,calculateBuyQtyParallelRequest.getUserId());
            spFineLineChannelFixtures.add(spFineLineChannelFixture);
        });
        calculateBuyQtyResponse.setSpFineLineChannelFixtures(spFineLineChannelFixtures);
    }

    private void updateFinelineMetadata(SpFineLineChannelFixture spFineLineChannelFixture,String userId ) {
        spFineLineChannelFixture.setCreateUserId(userId);
        spFineLineChannelFixture.setLastModifiedUserId(userId);
        spFineLineChannelFixture.setCreateTs(new Date());
        spFineLineChannelFixture.setLastModifiedTs(new Date());
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
        ReplenishmentCons replenishmentCons = replenishmentService.fetchHierarchyReplnCons(calculateBuyQtyParallelRequest, merchMethodsDtos.get(0));
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
                if (BuyQtyCommonUtil.isStyleHasBQFP(bqfpResponse, styleDto.getStyleNbr())) {
                    replenishmentService.setStyleReplenishmentCons(replenishmentCons, styleDto);
                }
                getCustomerChoices(styleDto, merchMethodsDtos, apResponse, bqfpResponse, spStyleChannelFixture, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, replenishmentCons);
            }
            spStyleChannelFixtures.add(spStyleChannelFixture);
        });
        log.info("calculating fineline IS and BS Qty");
        calculateFinelineBuyQuantityMapper.setFinelineChanFixtures(spFineLineChannelFixture, spStyleChannelFixtures);
        spFineLineChannelFixture.setSpStyleChannelFixtures(spStyleChannelFixtures);
    }

    private void getCustomerChoices(StyleDto styleDto, List<MerchMethodsDto> merchMethodsDtos, APResponse apResponse, BQFPResponse bqfpResponse,
                                    SpStyleChannelFixture spStyleChannelFixture, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse, ReplenishmentCons replenishmentCons) {

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
            ValidationResult ccValidationResult = validationService.validateCalculateBuyQuantityInputData(merchMethodsDtos, apResponse, bqfpResponse, styleDto, customerChoiceDto);
            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getCcClusters(styleDto, customerChoiceDto, merchMethodsDtos, apResponse, bqfpResponse, spCustomerChoiceChannelFixture, calculateBuyQtyResponse, calculateBuyQtyParallelRequest, replenishmentCons, ccValidationResult);
            }
            spCustomerChoiceChannelFixture.setBumpPackCnt(getCcMaxBumpPackCnt(bqfpResponse,styleDto,customerChoiceDto));
            spCustomerChoiceChannelFixtures.add(spCustomerChoiceChannelFixture);
        });
        log.info("calculating Style IS and BS Qty");
        calculateFinelineBuyQuantityMapper.setStyleChanFixtures(spStyleChannelFixture, spCustomerChoiceChannelFixtures);
        spStyleChannelFixture.setSpCustomerChoiceChannelFixture(spCustomerChoiceChannelFixtures);
    }

    protected Integer getCcMaxBumpPackCnt(BQFPResponse bqfpResponse, StyleDto styleDto, CustomerChoiceDto customerChoiceDto ) {
        Set<CustomerChoice> customerChoices = bqfpResponse.getStyles().stream().filter(style -> style.getStyleId().equalsIgnoreCase(styleDto.getStyleNbr()))
                .map(Style::getCustomerChoices)
                .flatMap(Collection::stream)
                .filter(cc -> cc.getCcId().equalsIgnoreCase(customerChoiceDto.getCcId())).collect(Collectors.toSet());
        return getMaxBumpCountVal(customerChoices);
    }

    private void getCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, List<MerchMethodsDto> merchMethodsDtos,
                               APResponse apResponse, BQFPResponse bqfpResponse, SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture,
                               CalculateBuyQtyResponse calculateBuyQtyResponse, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest,
                               ReplenishmentCons replenishmentCons, ValidationResult ccValidationResult) {
        Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId = new HashMap<>();
        Integer initialThreshold = deptAdminRuleService.getInitialThreshold(bqfpResponse.getPlanId(), bqfpResponse.getLvl1Nbr());
        //Replenishment
        List<Replenishment> replenishments = BuyQtyCommonUtil.getReplenishments(merchMethodsDtos, bqfpResponse, styleDto, customerChoiceDto);
        log.info("Get All Replenishments if exists for customerchoice: {} and fixtureType: {}", customerChoiceDto.getCcId(), spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureId().getSpStyleChannelFixtureId().getSpFineLineChannelFixtureId().getFixtureTypeRollUpId().getFixtureTypeRollupId());
        if (!CollectionUtils.isEmpty(replenishments)) {
            // Query the Replenishment constraint if Replenishment unit exist
            if(hasDcInboundUnits(replenishments)){
                replenishmentService.setCcsReplenishmentCons(replenishmentCons, calculateBuyQtyParallelRequest, merchMethodsDtos.get(0), styleDto, customerChoiceDto);
            }
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
                if (Boolean.parseBoolean(buyQtyProperties.getDeviationFlag())) {
                    getClusterSizesV2(styleDto, customerChoiceDto, clustersDto, bqfpResponse, storeBuyQtyBySizeId, rfaSizePackDataList, initialThreshold);
                } else {
                    getClusterSizes(styleDto, customerChoiceDto, clustersDto, bqfpResponse, storeBuyQtyBySizeId, rfaSizePackDataList, initialThreshold);
                }
            }
        });
        Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes = Optional.ofNullable(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureSize()).orElse(new HashSet<>());
        Set<CcSpMmReplPack> ccSpMmReplPacks = new HashSet<>();
        Integer replenishmentThreshold = deptAdminRuleService.getReplenishmentThreshold(bqfpResponse.getPlanId(), bqfpResponse.getLvl1Nbr());
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            Integer vendorPackQty = null != replenishmentCons.getCcMmReplPackCons() ? getVendorPackQty(replenishmentCons, entry) : null;
            setSizeChanFixtureBuyQty(spCustomerChoiceChannelFixture, replenishments, spCustomerChoiceChannelFixtureSizes, ccSpMmReplPacks, entry, replenishmentThreshold, vendorPackQty, calculateBuyQtyParallelRequest.getLvl1Nbr(), calculateBuyQtyParallelRequest.getPlanId());
        }

        if (!CollectionUtils.isEmpty(ccSpMmReplPacks)) {
            //Replenishment
            List<MerchCatgReplPack> merchCatgReplPacks = buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDtos.get(0), calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks, replenishmentCons, ccValidationResult);
            calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
        }

        spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureSize(spCustomerChoiceChannelFixtureSizes);
        calculateFinelineBuyQuantityMapper.setCcChanFixtures(spCustomerChoiceChannelFixture, spCustomerChoiceChannelFixtureSizes, ccValidationResult);
    }

    private static Integer getVendorPackQty(ReplenishmentCons replenishmentCons, Map.Entry<SizeDto, BuyQtyObj> entry) {
        Integer vendorPackQty = replenishmentCons.getCcMmReplPackCons().getVendorPackCount();
        Map<Integer, CcSpMmReplPack> cCSpMmReplPackSizeMap = replenishmentCons.getCcSpMmReplPackConsMap();
        if (!CollectionUtils.isEmpty(cCSpMmReplPackSizeMap) && cCSpMmReplPackSizeMap.containsKey(entry.getKey().getAhsSizeId())) {
            vendorPackQty = cCSpMmReplPackSizeMap.get(entry.getKey().getAhsSizeId()).getVendorPackCnt();
        }
        return vendorPackQty;
    }

    private void getClusterSizes(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, ClustersDto clustersDto,
                                 BQFPResponse bqfpResponse, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId, List<RFASizePackData> rfaSizePackDataList, Integer initialThreshold) {
        clustersDto.getSizes().forEach(sizeDto -> {
            if (!storeBuyQtyBySizeId.containsKey(sizeDto)) {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
            }
            BuyQtyObj buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);

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

    /**
     *  Calculate Buy Qty V2 to reduce deviation
     */
    private void getClusterSizesV2(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, ClustersDto clustersDto, BQFPResponse bqfpResponse, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId, List<RFASizePackData> rfaSizePackDataList, Integer initialThreshold) {
        List<CalculateQuantityByUnit> calculateQuantityByUnits = new ArrayList<>();
        // Calculate IS and BS
        for (SizeDto sizeDto: clustersDto.getSizes()) {
            if (!storeBuyQtyBySizeId.containsKey(sizeDto)) {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
            }
            rfaSizePackDataList.forEach(rfaSizePackData -> {
                Cluster volumeCluster = BuyQtyCommonUtil.getVolumeCluster(styleDto.getStyleNbr(), customerChoiceDto.getCcId(), bqfpResponse, rfaSizePackData);
                if (volumeCluster != null) {
                    calculateInitialSetQuantityService.setDefaultValueForNullInitialSet(volumeCluster);
                    CalculateQuantityByUnit calculateQuantityByUnit = getCalculateQuantityByUnit(calculateQuantityByUnits, rfaSizePackData, volumeCluster.getInitialSet().getInitialSetUnitsPerFix());
                    // Initial Set
                    calculateInitialSetQuantityService.calculateInitialSet(calculateQuantityByUnit.getCalculateInitialSet(), sizeDto, volumeCluster, rfaSizePackData);
                    // Bump Set
                    calculateQuantityByUnit.getCalculateBumpSet().getBumpSetQuantities().addAll(calculateBumpPackQtyService.calculateBumpPackQtyV2(sizeDto, rfaSizePackData, volumeCluster));
                }
            });
        }
        // Adjust IS units if they don't match with total BQ units and split them into sizes
        for (CalculateQuantityByUnit calculateQuantityByUnit : calculateQuantityByUnits) {
            calculateInitialSetQuantityService.adjustInitialSetUnits(calculateQuantityByUnit);
            // TODO: add bumpset adjustment
            // Segregate IS and BS by size to be used in adjusting the constraints
            setISAndBSBySize(calculateQuantityByUnit, storeBuyQtyBySizeId);
        }
        // Adjust constraints
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            BuyQtyObj buyQtyObj = entry.getValue();
            BuyQtyStoreObj buyQtyStoreObj = Optional.ofNullable(buyQtyObj.getBuyQtyStoreObj())
                    .orElse(new BuyQtyStoreObj());
            buyQtyObj.setValidationResult(ValidationResult.builder().codes(new HashSet<>()).build());

            List<StoreQuantity> storeQuantities = Optional.ofNullable(buyQtyStoreObj.getBuyQuantities())
                    .orElse(new ArrayList<>());

            List<StoreQuantity> processStoreQuantities = new ArrayList<>();
            if (null != buyQtyObj.getCalculateQuantityBySizes() && !buyQtyObj.getCalculateQuantityBySizes().isEmpty()) {
                addStoreBuyQuantityService.adjustISForOneUnitPerStoreV2(buyQtyObj, processStoreQuantities);
                addStoreBuyQuantityService.adjustISWithConstraint(processStoreQuantities, buyQtyObj, initialThreshold, entry.getKey().getSizeDesc());
                addStoreBuyQuantityService.adjustBSWithConstraint(buyQtyObj.getCalculateQuantityBySizes(), processStoreQuantities);
            }
            processStoreQuantities.forEach(quantity -> {
                quantity.setRfaSizePackData(null);
                quantity.setCluster(null);
            });
            buyQtyObj.setCalculateQuantityBySizes(null);
            storeQuantities.addAll(processStoreQuantities);
            buyQtyStoreObj.setBuyQuantities(storeQuantities);
            if(!ObjectUtils.isEmpty(buyQtyObj)) {
                buyQtyObj.setBuyQtyStoreObj(buyQtyStoreObj);
            }
        }
    }

    /**
     * Get unique CalculateQuantity group by units - volumeCluster, fixtureType, fixtureGroup and totalUnits from BQ
     */
    private static CalculateQuantityByUnit getCalculateQuantityByUnit(List<CalculateQuantityByUnit> calculateQuantityByUnits, RFASizePackData rfaSizePackData, long initialSetUnitsPerFix) {
        long totalSizeUnits = Math.round(rfaSizePackData.getFixture_group() * initialSetUnitsPerFix);
        Optional<CalculateQuantityByUnit> optionalCalculateQuantityByUnit = calculateQuantityByUnits.stream()
                .filter(cq -> cq.getVolumeGroupClusterId().equals(rfaSizePackData.getVolume_group_cluster_id()) &&
                        cq.getFixtureType().equals(rfaSizePackData.getFixture_type()) &&
                        cq.getFixtureGroup().equals(rfaSizePackData.getFixture_group()) &&
                        cq.getTotalUnitsFromBQ().equals(totalSizeUnits))
                .findFirst();
        if (optionalCalculateQuantityByUnit.isEmpty()) {
            CalculateQuantityByUnit calculateQuantityByUnit = CalculateQuantityByUnit.builder()
                    .volumeGroupClusterId(rfaSizePackData.getVolume_group_cluster_id())
                    .fixtureType(rfaSizePackData.getFixture_type())
                    .fixtureGroup(rfaSizePackData.getFixture_group())
                    .totalUnitsFromBQ(totalSizeUnits)
                    .storeCount(rfaSizePackData.getStore_cnt())
                    .calculateInitialSet(CalculateInitialSet.builder().totalUnits(0L).initialSetQuantities(new ArrayList<>()).build())
                    .calculateBumpSet(CalculateBumpSet.builder().totalUnits(0L).bumpSetQuantities(new ArrayList<>()).build())
                    .build();
            calculateQuantityByUnits.add(calculateQuantityByUnit);
            return calculateQuantityByUnit;
        } else {
            return optionalCalculateQuantityByUnit.get();
        }
    }

    /**
     * Get unique CalculateQuantity group by size
     */
    private static CalculateQuantityBySize getCalculateQuantityBySize(List<CalculateQuantityBySize> calculateQuantityBySizeList, CalculateQuantityByUnit calculateQuantityByUnit, String sizeDesc) {
        Optional<CalculateQuantityBySize> optionalCalculateQuantityBySize = calculateQuantityBySizeList.stream()
                .filter(cq -> cq.getVolumeGroupClusterId().equals(calculateQuantityByUnit.getVolumeGroupClusterId()) &&
                        cq.getFixtureType().equals(calculateQuantityByUnit.getFixtureType()) &&
                        cq.getFixtureGroup().equals(calculateQuantityByUnit.getFixtureGroup()) &&
                        cq.getSizeDesc().equals(sizeDesc))
                .findFirst();
        if (optionalCalculateQuantityBySize.isEmpty()) {
            CalculateQuantityBySize calculateQuantityBySize = CalculateQuantityBySize.builder()
                    .volumeGroupClusterId(calculateQuantityByUnit.getVolumeGroupClusterId())
                    .fixtureType(calculateQuantityByUnit.getFixtureType())
                    .fixtureGroup(calculateQuantityByUnit.getFixtureGroup())
                    .sizeDesc(sizeDesc)
                    .bumpSetQuantities((new ArrayList<>()))
                    .build();
            calculateQuantityBySizeList.add(calculateQuantityBySize);
            return calculateQuantityBySize;
        } else {
            return optionalCalculateQuantityBySize.get();
        }
    }

    /**
     * Split calculated IS and BS by size and store it in buyQtyObj for further processing
     */
    private void setISAndBSBySize(CalculateQuantityByUnit calculateQuantityByUnit, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId) {
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            SizeDto sizeDto = entry.getKey();
            BuyQtyObj buyQtyObj = entry.getValue();
            List<CalculateQuantityBySize> calculateQuantityBySizes = Optional.ofNullable(buyQtyObj.getCalculateQuantityBySizes()).orElse(new ArrayList<>());
            CalculateQuantityBySize calculateQuantityBySize = getCalculateQuantityBySize(calculateQuantityBySizes, calculateQuantityByUnit, sizeDto.getSizeDesc());
            List<BumpSetQuantity> bumpSetQuantitiesBySize = calculateQuantityBySize.getBumpSetQuantities();

            List<InitialSetQuantity> initialSetQuantities = calculateQuantityByUnit.getCalculateInitialSet().getInitialSetQuantities();
            initialSetQuantities.stream()
                    .filter(isQty -> isQty.getSizeDesc().equals(sizeDto.getSizeDesc()))
                    .findFirst().ifPresent(calculateQuantityBySize::setInitialSetQuantity);

            // BS Split By Size
            List<BumpSetQuantity> bumpSetQuantityList = calculateQuantityByUnit.getCalculateBumpSet().getBumpSetQuantities();
            List<BumpSetQuantity> bumpSetQuantities = bumpSetQuantityList.stream()
                    .filter(bsQty -> bsQty.getSizeDesc().equals(sizeDto.getSizeDesc()))
                    .collect(Collectors.toList());
            bumpSetQuantitiesBySize.addAll(bumpSetQuantities);
            buyQtyObj.setCalculateQuantityBySizes(calculateQuantityBySizes);
        }
    }

    private void setSizeChanFixtureBuyQty(SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture,
                                          List<Replenishment> replenishments, Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes,
                                          Set<CcSpMmReplPack> ccSpMmReplPacks,
                                          Map.Entry<SizeDto, BuyQtyObj> entry,
                                          Integer replenishmentThreshold,
                                          Integer vendorPackQty,
                                          Integer lvl1Nbr, Long planId) {
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
        double isBuyQty = getIsQty(entry);
        //Update Store Qty
        final BuyQtyObj allStoresBuyQty = entry.getValue();
        if (!CollectionUtils.isEmpty(allStoresBuyQty.getReplenishments()) && !CollectionUtils.isEmpty(allStoresBuyQty.getBuyQtyStoreObj().getBuyQuantities())) {
            allStoresBuyQty.setTotalReplenishment(buyQuantityConstraintService.getTotalReplenishment(allStoresBuyQty.getReplenishments()));
            // IF calculated IS is greater than 0, then process the replenishment to be moved into InitialSet if it falls under minimum replenishment condition
            if (isBuyQty > 0) {
                buyQuantityConstraintService.processReplenishmentConstraints(entry, allStoresBuyQty.getTotalReplenishment(), replenishmentThreshold);
            } else {
                // Run DC Inbound Optimization when calculated IS is equal to 0 to consider the replenishment count for non initialSets
                // Getting 0 IS from BQFP or User explicitly added SizePct as 0 where they dont need any IS
                entry.getValue().setReplenishments(replenishmentsOptimizationServices.getUpdatedReplenishmentsPack(entry.getValue().getReplenishments(), vendorPackQty, SizeAndPackConstants.STORE_CHANNEL_ID, lvl1Nbr, planId));
                entry.getValue().setTotalReplenishment(buyQuantityConstraintService.getTotalReplenishment(entry.getValue().getReplenishments()));
            }
        }

        isBuyQty = getIsQty(entry);
        double bsBuyQty = getBsQty(entry);
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
            // Add validation codes to CustomerChoiceChannelFixtureSize
            spCustomerChoiceChannelFixtureSize.setMessageObj(objectMapper.writeValueAsString(entry.getValue().getValidationResult()));
        } catch (Exception e) {
            log.error("Error parsing Json: ", e);
            throw new CustomException("Error parsing Json: " + e);
        }
        spCustomerChoiceChannelFixtureSizes.add(spCustomerChoiceChannelFixtureSize);

        //Replenishment
        if (!CollectionUtils.isEmpty(replenishments) && entry.getValue().getTotalReplenishment() > 0) {
            ccSpMmReplPacks.add(buyQtyReplenishmentMapperService.setCcMmSpReplenishment(entry, (int) entry.getValue().getTotalReplenishment(), (int) Math.round(totalBuyQty)));
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

    private APResponse getRFAResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, LikeAssociation likeFinelineResponse, List<ColorDefinition> colorDefinitions, String volumeDeviation) throws InterruptedException, JsonProcessingException {
        APResponse apResponse = new APResponse();
        if (ChannelType.STORE.getDescription().equalsIgnoreCase(calculateBuyQtyParallelRequest.getChannel())) {
            RFASizePackRequest rfaSizePackRequest = createRFASizePackRequest(calculateBuyQtyRequest, calculateBuyQtyParallelRequest, likeFinelineResponse, colorDefinitions);
            log.info("Invoking BQ query to get RFA Data for fineline: {}", rfaSizePackRequest.getFineline_nbr());
            List<RFASizePackData> rfaSizePackDataList = bigQueryClusterService.fetchRFASizePackData(rfaSizePackRequest, volumeDeviation);
            log.info("RFA Data from BQ: {}", Arrays.toString(rfaSizePackDataList.toArray()));
            apResponse.setRfaSizePackData(rfaSizePackDataList);
        }
        return apResponse;
    }

    private BQFPResponse getBqfpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, Integer finelineNbr) {
        BQFPRequest bqfpRequest = new BQFPRequest();
        bqfpRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        bqfpRequest.setChannel(ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()).toString());
        bqfpRequest.setFinelineNbr(finelineNbr);
        log.info("BQFPRequest payload for planId {} : {}", bqfpRequest, calculateBuyQtyRequest.getPlanId());
        return bqfpService.getBuyQuantityUnits(bqfpRequest);
    }

    private List<String> getColorFamilies(String season, Integer fiscalYear, Integer deptNbr, Integer finelineNbr) throws SizeAndPackException {
        try {
            return midasServiceCall.fetchColorFamilies(season, fiscalYear, deptNbr, finelineNbr);
        } catch (CustomException e) {
            log.error("An exception occurred while fetching color families from midas: {}", e.getMessage());
            throw new SizeAndPackException("An exception occurred while fetching color families from midas");
        }
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

    private void setReplenishmentSizes(ClustersDto clustersDto, List<Replenishment> replenishments, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId) {
        clustersDto.getSizes().forEach(sizeDto -> {
            if (!storeBuyQtyBySizeId.containsKey(sizeDto)) {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
            }
            BuyQtyObj buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);

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

    private boolean hasDcInboundUnits(List<Replenishment> replenishments) {
        return Optional.ofNullable(replenishments)
                .stream()
                .flatMap(Collection::stream)
                .anyMatch(replenishment -> (replenishment.getDcInboundUnits() != null && replenishment.getDcInboundUnits() > 0));
    }

}