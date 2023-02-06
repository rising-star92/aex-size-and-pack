package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.CustomerChoice;
import com.walmart.aex.sp.dto.bqfp.Fixture;
import com.walmart.aex.sp.dto.bqfp.InitialSet;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.bqfp.Style;
import com.walmart.aex.sp.dto.buyquantity.AddStoreBuyQuantity;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyObj;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyStoreObj;
import com.walmart.aex.sp.dto.buyquantity.InitialSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.InitialSetWithReplnsConstraint;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.dto.buyquantity.StoreQuantity;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddStoreBuyQuantityService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    @Autowired
    BuyQuantityConstraintService buyQuantityConstraintService;

    @Autowired
    CalculateInitialSetQuantityService calculateInitialSetQuantityService;

    private static final Long DEFAULT_IS_QTY = 0L;
    private static final Long DEFAULT_TOTAL_IS_QTY = 1L;

    public AddStoreBuyQuantityService() {

    }

    public AddStoreBuyQuantityService(ObjectMapper objectMapper,
                                      CalculateBumpPackQtyService calculateBumpPackQtyService,
                                      BuyQuantityConstraintService buyQuantityConstraintService,
                                      CalculateInitialSetQuantityService calculateInitialSetQuantityService) {
        this.objectMapper = objectMapper;
        this.calculateBumpPackQtyService = calculateBumpPackQtyService;
        this.buyQuantityConstraintService = buyQuantityConstraintService;
        this.calculateInitialSetQuantityService = calculateInitialSetQuantityService;
    }


    public void addStoreBuyQuantities(AddStoreBuyQuantity addStoreBuyQuantity, BuyQtyObj buyQtyObj, Integer initialThreshold) {
        BuyQtyStoreObj buyQtyStoreObj = Optional.ofNullable(buyQtyObj)
                .map(BuyQtyObj::getBuyQtyStoreObj)
                .orElse(new BuyQtyStoreObj());

        List<StoreQuantity> initialSetQuantities = Optional.of(buyQtyStoreObj)
                .map(BuyQtyStoreObj::getBuyQuantities)
                .orElse(new ArrayList<>());
        List<RFASizePackData> rfaSizePackDataList = addStoreBuyQuantity.getRfaSizePackDataList();
        rfaSizePackDataList.forEach(rfaSizePackData -> calculateAndAddStoreBuyQuantities(addStoreBuyQuantity, buyQtyObj, initialSetQuantities, rfaSizePackData, initialThreshold));
        buyQtyStoreObj.setBuyQuantities(initialSetQuantities);
        if(!ObjectUtils.isEmpty(buyQtyObj)) {
            buyQtyObj.setBuyQtyStoreObj(buyQtyStoreObj);
        }
    }

    public void calculateAndAddStoreBuyQuantities(AddStoreBuyQuantity addStoreBuyQuantity, BuyQtyObj buyQtyObj, List<StoreQuantity> initialSetQuantities, RFASizePackData rfaSizePackData, Integer initialThreshold) {
        if (rfaSizePackData == null) {
            log.warn("rfaSizePackData is null. Not adding storeBuyQuantities for styleNbr : {} , ccId :{}  ", addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId());
            return;
        }
        Cluster volumeCluster = getVolumeCluster(addStoreBuyQuantity, rfaSizePackData);
        if (volumeCluster != null) {
            setDefaultValueForNullInitialSet(volumeCluster);
            setInitialSetAndBumpSetQty(addStoreBuyQuantity, initialSetQuantities, volumeCluster, buyQtyObj, rfaSizePackData, initialThreshold);
        }
    }

    private void setInitialSetAndBumpSetQty(AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> initialSetQuantities, Cluster volumeCluster, BuyQtyObj buyQtyObj, RFASizePackData rfaSizePackData, Integer initialThreshold) {
        List<Integer> storeList = safeReadStoreList(rfaSizePackData.getStore_list()).stream().sorted().collect(Collectors.toList());
        SizeDto sizeDto = addStoreBuyQuantity.getSizeDto();
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQty(sizeDto, volumeCluster, rfaSizePackData);
        double perStoreQty = initialSetQuantity.getPerStoreQty();
        double isQty = initialSetQuantity.getIsQty();
        if ((perStoreQty < initialThreshold && perStoreQty > 0) && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()))) {
            long totalReplenishment = getTotalReplenishment(buyQtyObj);
            if (totalReplenishment > 0) {
                double unitsLessThanThreshold = initialThreshold - perStoreQty;
                double totalReducedReplenishment = unitsLessThanThreshold * rfaSizePackData.getStore_cnt();
                if (totalReplenishment >= totalReducedReplenishment) {
                    InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithMoreReplenConstraint(buyQtyObj, totalReducedReplenishment, rfaSizePackData, initialThreshold);
                    buyQtyObj.setReplenishments(initialSetWithReplnsConstraint.getReplnsWithUnits());
                    perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                    isQty = initialSetWithReplnsConstraint.getIsQty();
                    log.debug("| IS after IS constraints with more replenishment | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size());
                } else {
                    int storeCntWithNewQty = (int) (totalReplenishment / unitsLessThanThreshold);
                    InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithLessReplenConstraint(buyQtyObj, storeCntWithNewQty, storeList, perStoreQty, rfaSizePackData, volumeCluster, sizeDto, initialThreshold);
                    storeList = storeList.subList(0, storeCntWithNewQty);
                    initialSetQuantities.add(initialSetWithReplnsConstraint.getStoreQuantity());
                    buyQtyObj.setReplenishments(initialSetWithReplnsConstraint.getReplnsWithUnits());
                    perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                    isQty = initialSetWithReplnsConstraint.getIsQty();

                    log.debug("| IS after IS constraints with less replenishment with new IS qty | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type())
                            , isQty, perStoreQty, storeList.size());
                }
            }
        }
        StoreQuantity storeQuantity = BuyQtyCommonUtil.createStoreQuantity(rfaSizePackData, perStoreQty, storeList, isQty, volumeCluster);
        storeQuantity.setBumpSets(calculateBumpPackQtyService.calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster, storeList.size()));
        initialSetQuantities.add(storeQuantity);
    }

    private long getTotalReplenishment(BuyQtyObj buyQtyObj) {
        return buyQtyObj.getReplenishments()
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(Replenishment::getAdjReplnUnits)
                .sum();
    }

    private Cluster getVolumeCluster(AddStoreBuyQuantity addStoreBuyQuantity, RFASizePackData rfaSizePackData) {
        return Optional.ofNullable(addStoreBuyQuantity.getBqfpResponse().getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(addStoreBuyQuantity.getStyleDto().getStyleNbr()))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(addStoreBuyQuantity.getCustomerChoiceDto().getCcId()))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type())))
                .findFirst()
                .map(Fixture::getClusters)
                .stream()
                .flatMap(Collection::stream)
                .filter(cluster -> cluster.getAnalyticsClusterId().equals(rfaSizePackData.getVolume_group_cluster_id()))
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

    private void setDefaultValueForNullInitialSet(Cluster volumeCluster) {
        if (volumeCluster.getInitialSet() == null) {
            log.warn("InitialSet of volumeCluster : {} is null. Setting default initial Set ", volumeCluster);
            InitialSet initialSet = new InitialSet();
            initialSet.setInitialSetUnitsPerFix(DEFAULT_IS_QTY);
            initialSet.setTotalInitialSetUnits(DEFAULT_TOTAL_IS_QTY);
            volumeCluster.setInitialSet(initialSet);
        } else if (volumeCluster.getInitialSet().getInitialSetUnitsPerFix() == null) {
            log.warn("InitialSetUnitsPerFix of volumeCluster : {} is null. Setting InitialSetUnitsPerFix as zero ", volumeCluster);
            volumeCluster.getInitialSet().setInitialSetUnitsPerFix(DEFAULT_IS_QTY);
        } else if (volumeCluster.getInitialSet().getTotalInitialSetUnits() == null) {
            log.warn("TotalInitialSetUnits of volumeCluster : {} is null. Setting TotalInitialSetUnits as one ", volumeCluster);
            volumeCluster.getInitialSet().setTotalInitialSetUnits(DEFAULT_TOTAL_IS_QTY);
        }
    }
}
