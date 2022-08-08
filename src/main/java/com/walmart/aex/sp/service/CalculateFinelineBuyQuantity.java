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

    public List<SpFineLineChannelFixture> calculateFinelineBuyQty(CalculateBuyQtyRequest calculateBuyQtyRequest, Lvl3Dto lvl3Dto, Lvl4Dto lvl4Dto, FinelineDto fineline, List<SpFineLineChannelFixture> spFineLineChannelFixtures) throws SizeAndPackException {

        BuyQtyResponse buyQtyResponse = getSizeProfiles(calculateBuyQtyRequest, lvl3Dto, lvl4Dto, fineline);
        log.info("Size Profiles: {}", buyQtyResponse);
        BQFPResponse bqfpResponse = getBqfpResponse(calculateBuyQtyRequest, fineline);
        log.info("BQ FP Response: {}", bqfpResponse);
        APResponse apResponse = getRfaSpResponse(calculateBuyQtyRequest, fineline, bqfpResponse);
        log.info("RFA Response: {}", apResponse);

        FinelineDto finelineDto = getFineline(buyQtyResponse);
        if (finelineDto != null) {
            if (!CollectionUtils.isEmpty(finelineDto.getMerchMethods())) {
                getMerchMethod(lvl3Dto, lvl4Dto, finelineDto, apResponse, bqfpResponse, spFineLineChannelFixtures, calculateBuyQtyRequest);
            }
        } else log.info("Size Profile Fineline is null: {}", buyQtyResponse);
        return spFineLineChannelFixtures;
    }

    private BuyQtyResponse getSizeProfiles(CalculateBuyQtyRequest calculateBuyQtyRequest, Lvl3Dto lvl3Dto, Lvl4Dto lvl4Dto, FinelineDto fineline) throws SizeAndPackException {
        BuyQtyRequest buyQtyRequest = new BuyQtyRequest();
        buyQtyRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        buyQtyRequest.setChannel(calculateBuyQtyRequest.getChannel());
        buyQtyRequest.setLvl3Nbr(lvl3Dto.getLvl3Nbr());
        buyQtyRequest.setLvl4Nbr(lvl4Dto.getLvl4Nbr());
        buyQtyRequest.setFinelineNbr(fineline.getFinelineNbr());
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

    private void getMerchMethod(Lvl3Dto lvl3Dto, Lvl4Dto lvl4Dto, FinelineDto finelineDto, APResponse apResponse, BQFPResponse bqfpResponse, List<SpFineLineChannelFixture> spFineLineChannelFixtures, CalculateBuyQtyRequest calculateBuyQtyRequest) {
        finelineDto.getMerchMethods().forEach(merchMethodsDto -> {
            FixtureTypeRollUpId fixtureTypeRollUpId = new FixtureTypeRollUpId(merchMethodsDto.getFixtureTypeRollupId());
            SpFineLineChannelFixtureId spFineLineChannelFixtureId = new SpFineLineChannelFixtureId(fixtureTypeRollUpId, calculateBuyQtyRequest.getPlanId(), calculateBuyQtyRequest.getLvl0Nbr(),
                    calculateBuyQtyRequest.getLvl1Nbr(), calculateBuyQtyRequest.getLvl2Nbr(), lvl3Dto.getLvl3Nbr(), lvl4Dto.getLvl4Nbr(), finelineDto.getFinelineNbr(), ChannelType.getChannelIdFromName(calculateBuyQtyRequest.getChannel()));

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
                getStyles(finelineDto.getStyles(), merchMethodsDto, apResponse, bqfpResponse, spFineLineChannelFixture);
            } else log.info("Styles Size Profiles are empty to calculate buy Qty: {}", finelineDto);
            spFineLineChannelFixtures.add(spFineLineChannelFixture);
        });
    }

    private void getStyles(List<StyleDto> styles, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse, SpFineLineChannelFixture spFineLineChannelFixture) {

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
                getCustomerChoices(styleDto, merchMethodsDto, apResponse, bqfpResponse, spStyleChannelFixture);
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

    private void getCustomerChoices(StyleDto styleDto, MerchMethodsDto merchMethodsDto, APResponse apResponse, BQFPResponse bqfpResponse, SpStyleChannelFixture spStyleChannelFixture) {

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

    private List<BumpSetQuantity> calculateBumpPackQty(SizeDto sizeDto, RFASizePackData rfaSizePackData, Cluster volumeCluster) {
        List<BumpSetQuantity> bumpPackQuantities = new ArrayList<>();
        volumeCluster.getBumpList().forEach(bumpSet -> {
            BumpSetQuantity bumpSetQuantity = new BumpSetQuantity();
            //Calculate BS Buy Quantity
            double bumpQtyPerFixture = (bumpSet.getUnits() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix())/volumeCluster.getInitialSet().getTotalInitialSetUnits().doubleValue();
            double bsCalculatedBq = rfaSizePackData.getStore_cnt() * bumpQtyPerFixture * rfaSizePackData.getFixture_group();
            double bsQty = bsCalculatedBq * getSizePct(sizeDto);
            double bsPerStoreQty = bsQty / rfaSizePackData.getStore_cnt();
            bumpSetQuantity.setTotalUnits((int) bsQty);
            bumpSetQuantity.setBsUnits((int) bsPerStoreQty);
            bumpPackQuantities.add(bumpSetQuantity);
        });
        return bumpPackQuantities;
    }

    private APResponse getRfaSpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, FinelineDto fineline, BQFPResponse bqfpResponse) {
        APRequest apRequest = new APRequest();
        apRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        apRequest.setFinelineNbr(fineline.getFinelineNbr());
        apRequest.setVolumeDeviationLevel(VdLevelCode.getVdLevelCodeFromId(bqfpResponse.getVolumeDeviationStrategyLevelSelection().intValue()));

        return sizeAndPackService.fetchRunFixtureAllocationOutput(apRequest);
    }

    private BQFPResponse getBqfpResponse(CalculateBuyQtyRequest calculateBuyQtyRequest, FinelineDto fineline) {
        BQFPRequest bqfpRequest = new BQFPRequest();
        bqfpRequest.setPlanId(calculateBuyQtyRequest.getPlanId());
        bqfpRequest.setChannel(calculateBuyQtyRequest.getChannel());
        bqfpRequest.setFinelineNbr(fineline.getFinelineNbr());

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
}
