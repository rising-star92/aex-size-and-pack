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
import com.walmart.aex.sp.repository.MerchCatgReplPackRepository;
import com.walmart.aex.sp.repository.SpFineLineChannelFixtureRepository;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
    private final SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository;
    private final MerchCatgReplPackRepository merchCatgReplPackRepository;
    private final AddStoreBuyQuantitiesService addStoreBuyQuantitiesService;
    private final BuyQuantityConstraintService buyQuantityConstraintService;

   public CalculateFinelineBuyQuantity(BQFPService bqfpService,
                                        ObjectMapper objectMapper,
                                        BuyQtyReplenishmentMapperService buyQtyReplenishmentMapperService,
                                        CalculateOnlineFinelineBuyQuantity calculateOnlineFinelineBuyQuantity,
                                        StrategyFetchService strategyFetchService,
                                        SpFineLineChannelFixtureRepository spFineLineChannelFixtureRepository,
                                        MerchCatgReplPackRepository merchCatgReplPackRepository,
                                        AddStoreBuyQuantitiesService addStoreBuyQuantitiesService,
                                        BuyQuantityConstraintService buyQuantityConstraintService) {
        this.bqfpService = bqfpService;
        this.objectMapper = objectMapper;
        this.strategyFetchService = strategyFetchService;
        this.buyQtyReplenishmentMapperService = buyQtyReplenishmentMapperService;
        this.calculateOnlineFinelineBuyQuantity = calculateOnlineFinelineBuyQuantity;
        this.spFineLineChannelFixtureRepository = spFineLineChannelFixtureRepository;
        this.merchCatgReplPackRepository = merchCatgReplPackRepository;
        this.addStoreBuyQuantitiesService = addStoreBuyQuantitiesService;
        this.buyQuantityConstraintService = buyQuantityConstraintService;
    }

    public CalculateBuyQtyResponse calculateFinelineBuyQty(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) throws CustomException {
        CompletableFuture<BuyQtyResponse> buyQtyResponseCompletableFuture = getBuyQtyResponseCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        CompletableFuture<BQFPResponse> bqfpResponseCompletableFuture = getBqfpResponseCompletableFuture(calculateBuyQtyRequest, calculateBuyQtyParallelRequest);
        //wrapper future completes when all futures have completed
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(buyQtyResponseCompletableFuture, bqfpResponseCompletableFuture);
          try {
              combinedFuture.join();
              final BuyQtyResponse buyQtyResponse = buyQtyResponseCompletableFuture.get();
              final BQFPResponse bqfpResponse = bqfpResponseCompletableFuture.get();
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
                  deleteExistingFinelineIsBsBuyQty(calculateBuyQtyParallelRequest, calculateBuyQtyResponse);
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

    private CompletableFuture<BQFPResponse> getBqfpResponseCompletableFuture(CalculateBuyQtyRequest calculateBuyQtyRequest, CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        return CompletableFuture.supplyAsync(() -> getBqfpResponse(calculateBuyQtyRequest, calculateBuyQtyParallelRequest.getFinelineNbr()));
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

    private void deleteExistingFinelineIsBsBuyQty(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, CalculateBuyQtyResponse calculateBuyQtyResponse) {
        List<SpFineLineChannelFixture> spFineLineChannelFixtures = calculateBuyQtyResponse.getSpFineLineChannelFixtures();
        if (!CollectionUtils.isEmpty(spFineLineChannelFixtures)) {
            log.info("Deleteing IS BS Buy Qty data for Fineline: {}", calculateBuyQtyParallelRequest.getFinelineNbr());
            spFineLineChannelFixtures.removeIf(spFineLineChannelFixture -> spFineLineChannelFixture.getSpFineLineChannelFixtureId().getFineLineNbr().equals(calculateBuyQtyParallelRequest.getFinelineNbr()));
            spFineLineChannelFixtureRepository.flush();
        }

        Set<SubCatgReplPack> subCatgReplPacks = Optional.ofNullable(calculateBuyQtyResponse.getMerchCatgReplPacks())
                .stream()
                .flatMap(Collection::stream)
                .filter(merchCatgReplPack -> merchCatgReplPack.getMerchCatgReplPackId().getRepTLvl3().equals(calculateBuyQtyParallelRequest.getLvl3Nbr()))
                .map(MerchCatgReplPack::getSubReplPack)
                .flatMap(Collection::stream)
                .filter(subCatgReplPack -> subCatgReplPack.getSubCatgReplPackId().getRepTLvl4().equals(calculateBuyQtyParallelRequest.getLvl4Nbr()))
                .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(subCatgReplPacks)) {
            log.info("Delete replenishment buy qty for fineline: {}", calculateBuyQtyParallelRequest.getFinelineNbr());
            subCatgReplPacks.forEach(subCatgReplPack -> {
                if (!CollectionUtils.isEmpty(subCatgReplPack.getFinelineReplPack())) {
                    subCatgReplPack.getFinelineReplPack().removeIf(finelineReplPack -> finelineReplPack.getFinelineReplPackId().getFinelineNbr().equals(calculateBuyQtyParallelRequest.getFinelineNbr()));
                }
            });
        }
        merchCatgReplPackRepository.flush();
    }

    private void getMerchMethod(CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest, FinelineDto finelineDto, APResponse apResponse, BQFPResponse bqfpResponse,
                                CalculateBuyQtyResponse calculateBuyQtyResponse, CalculateBuyQtyRequest calculateBuyQtyRequest) {
        List<SpFineLineChannelFixture> spFineLineChannelFixtures = calculateBuyQtyResponse.getSpFineLineChannelFixtures();
        finelineDto.getMerchMethods().forEach(merchMethodsDto -> {
            if (merchMethodsDto.getMerchMethodCode() != null) {
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
            }

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
        setFinelineChanFixtures(spFineLineChannelFixture, spStyleChannelFixtures);
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
            if (!CollectionUtils.isEmpty(customerChoiceDto.getClusters())) {
                getCcClusters(styleDto, customerChoiceDto, merchMethodsDto, apResponse, bqfpResponse, spCustomerChoiceChannelFixture, calculateBuyQtyResponse, calculateBuyQtyParallelRequest);
            }
            spCustomerChoiceChannelFixtures.add(spCustomerChoiceChannelFixture);
        });
        log.info("calculating Style IS and BS Qty");
        setStyleChanFixtures(spStyleChannelFixture, spCustomerChoiceChannelFixtures);
        spStyleChannelFixture.setSpCustomerChoiceChannelFixture(spCustomerChoiceChannelFixtures);
    }

    private void getCcClusters(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, MerchMethodsDto merchMethodsDto, APResponse apResponse,
                               BQFPResponse bqfpResponse, SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture, CalculateBuyQtyResponse calculateBuyQtyResponse,
                               CalculateBuyQtyParallelRequest calculateBuyQtyParallelRequest) {
        Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId = new HashMap<>();
        //Replenishment
        List<Replenishment> replenishments = BuyQtyCommonUtil.getReplenishments(merchMethodsDto, bqfpResponse, styleDto, customerChoiceDto);
        log.info("Get All Replenishments if exists for customerchoice: {} and fixtureType: {}", customerChoiceDto.getCcId(), merchMethodsDto.getFixtureTypeRollupId());
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
                //Set Initial Set and Bump Set for Size Map
                getClusterSizes(styleDto, customerChoiceDto, clustersDto, merchMethodsDto, bqfpResponse, storeBuyQtyBySizeId, rfaSizePackDataList);
            }
        });
        Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes = Optional.ofNullable(spCustomerChoiceChannelFixture.getSpCustomerChoiceChannelFixtureSize()).orElse(new HashSet<>());
        Set<CcSpMmReplPack> ccSpMmReplPacks = new HashSet<>();
        for (Map.Entry<SizeDto, BuyQtyObj> entry : storeBuyQtyBySizeId.entrySet()) {
            setSizeChanFixtureBuyQty(merchMethodsDto, spCustomerChoiceChannelFixture, replenishments, spCustomerChoiceChannelFixtureSizes, ccSpMmReplPacks, entry);
        }

        if (!CollectionUtils.isEmpty(ccSpMmReplPacks)) {
            //Replenishment
            List<MerchCatgReplPack> merchCatgReplPacks = buyQtyReplenishmentMapperService.setAllReplenishments(styleDto, merchMethodsDto, calculateBuyQtyParallelRequest, calculateBuyQtyResponse, customerChoiceDto, ccSpMmReplPacks);
            calculateBuyQtyResponse.setMerchCatgReplPacks(merchCatgReplPacks);
        }

        spCustomerChoiceChannelFixture.setSpCustomerChoiceChannelFixtureSize(spCustomerChoiceChannelFixtureSizes);
        setCcChanFixtures(spCustomerChoiceChannelFixture, spCustomerChoiceChannelFixtureSizes);
    }

    private void getClusterSizes(StyleDto styleDto, CustomerChoiceDto customerChoiceDto, ClustersDto clustersDto, MerchMethodsDto merchMethodsDto,
                                 BQFPResponse bqfpResponse, Map<SizeDto, BuyQtyObj> storeBuyQtyBySizeId, List<RFASizePackData> rfaSizePackDataList) {
        clustersDto.getSizes().forEach(sizeDto -> {

            BuyQtyObj buyQtyObj;
            if (storeBuyQtyBySizeId.containsKey(sizeDto)) {
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            } else {
                storeBuyQtyBySizeId.put(sizeDto, new BuyQtyObj());
                buyQtyObj = storeBuyQtyBySizeId.get(sizeDto);
            }
            AddStoreBuyQuantities addStoreBuyQuantities = new AddStoreBuyQuantities();
            addStoreBuyQuantities.setRfaSizePackDataList(rfaSizePackDataList);
            addStoreBuyQuantities.setStyleDto(styleDto);
            addStoreBuyQuantities.setSizeDto(sizeDto);
            addStoreBuyQuantities.setMerchMethodsDto(merchMethodsDto);
            addStoreBuyQuantities.setBqfpResponse(bqfpResponse);
            addStoreBuyQuantities.setCustomerChoiceDto(customerChoiceDto);
            addStoreBuyQuantitiesService.addStoreBuyQuantities(addStoreBuyQuantities, buyQtyObj);
            storeBuyQtyBySizeId.put(sizeDto, buyQtyObj);
        });
    }

    private void setSizeChanFixtureBuyQty(MerchMethodsDto merchMethodsDto, SpCustomerChoiceChannelFixture spCustomerChoiceChannelFixture,
                                          List<Replenishment> replenishments, Set<SpCustomerChoiceChannelFixtureSize> spCustomerChoiceChannelFixtureSizes,
                                          Set<CcSpMmReplPack> ccSpMmReplPacks, Map.Entry<SizeDto, BuyQtyObj> entry) {
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
        buyQuantityConstraintService.processReplenishmentConstraints(entry);
        double bsBuyQty = getBsQty(entry);
        double isBuyQty = getIsQty(entry);
        double totalBuyQty = isBuyQty + bsBuyQty + entry.getValue().getTotalReplenishment();
        spCustomerChoiceChannelFixtureSize.setInitialSetQty((int) Math.round(isBuyQty));
        spCustomerChoiceChannelFixtureSize.setBumpPackQty((int) Math.round(bsBuyQty));
        spCustomerChoiceChannelFixtureSize.setMerchMethodCode(merchMethodsDto.getMerchMethodCode());
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
        if(null != bqfpResponse.getVolumeDeviationStrategyLevelSelection()){
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
                Replenishment replenishment1 = new Replenishment();
                replenishment1.setReplnWeek(replenishment.getReplnWeek());
                replenishment1.setReplnWeekDesc(replenishment.getReplnWeekDesc());
                //Updating to use the dcInboundAdjUnits if set or dcInboundUnits from BQFP service
                Long units = getReplenishmentUnits(replenishment);
                replenishment1.setAdjReplnUnits((long) (units * getAvgSizePct(sizeDto)) / 100);
                replObj.add(replenishment1);
            });
            buyQtyObj.setReplenishments(replObj);
        });
    }

    private Long getReplenishmentUnits(Replenishment replenishment) {
        if (replenishment.getDcInboundAdjUnits() != null && replenishment.getDcInboundAdjUnits() != 0) {
            return replenishment.getDcInboundAdjUnits();
        } else if (replenishment.getDcInboundUnits() != null) {
            return replenishment.getDcInboundUnits();
        } else return 0L;
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