package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AddStoreBuyQuantitiesService {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    @Autowired
    CalculateInitialSetQuantityService calculateInitialSetQuantityService;

    @ManagedConfiguration
    private BuyQtyProperties buyQtyProperties;

    public BuyQtyObj addStoreBuyQuantities(AddStoreBuyQuantities addStoreBuyQuantities, BuyQtyObj buyQtyObj) {
        BuyQtyStoreObj buyQtyStoreObj = Optional.ofNullable(buyQtyObj)
                .map(BuyQtyObj::getBuyQtyStoreObj)
                .orElse(new BuyQtyStoreObj());

        List<StoreQuantity> initialSetQuantities = Optional.of(buyQtyStoreObj)
                .map(BuyQtyStoreObj::getBuyQuantities)
                .orElse(new ArrayList<>());
        List<RFASizePackData> rfaSizePackDataList = addStoreBuyQuantities.getRfaSizePackDataList();
        rfaSizePackDataList.forEach(rfaSizePackData -> calculateAndAddStoreBuyQuantities(addStoreBuyQuantities, buyQtyObj, initialSetQuantities, rfaSizePackData));
        buyQtyStoreObj.setBuyQuantities(initialSetQuantities);
        buyQtyObj.setBuyQtyStoreObj(buyQtyStoreObj);
        return buyQtyObj;
    }

    public void calculateAndAddStoreBuyQuantities(AddStoreBuyQuantities addStoreBuyQuantities, BuyQtyObj buyQtyObj, List<StoreQuantity> initialSetQuantities, RFASizePackData rfaSizePackData) {
        Cluster volumeCluster = getVolumeCluster(addStoreBuyQuantities, rfaSizePackData);
        if (volumeCluster == null) {
            /***If there is no volume cluster then no calculation required *****/
            return;
        }
        calculateReplenishmentLogic(addStoreBuyQuantities, initialSetQuantities, volumeCluster, buyQtyObj, rfaSizePackData);
    }


    private void calculateReplenishmentLogic(AddStoreBuyQuantities addStoreBuyQuantities, List<StoreQuantity> initialSetQuantities, Cluster volumeCluster, BuyQtyObj buyQtyObj, RFASizePackData rfaSizePackData) {
        List<Integer> storeList = safeReadStoreList(rfaSizePackData.getStore_list()).stream().sorted().collect(Collectors.toList());
        SizeDto sizeDto = addStoreBuyQuantities.getSizeDto();
        SPInitialSetQuantities spInitialSetQuantities = calculateInitialSetQuantityService.calculateInitialSetQty(sizeDto, volumeCluster, rfaSizePackData);
        double perStoreQty = spInitialSetQuantities.getPerStoreQty();
        double isQty = spInitialSetQuantities.getIsQty();
        if ((perStoreQty < buyQtyProperties.getInitialThreshold() && perStoreQty > 0) && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()))) {
            long totalReplenishment = getTotalReplenishment(buyQtyObj);
            if (totalReplenishment > 0) {
                double unitsLessThanThreshold = buyQtyProperties.getInitialThreshold() - perStoreQty;
                double totalReducedReplenishment = unitsLessThanThreshold * rfaSizePackData.getStore_cnt();
                List<Replenishment> replnsWithUnits = getReplnsWithUnits(buyQtyObj);
                List<Replenishment> replnsWithNoUnits = getReplnsWithNoUnits(buyQtyObj);
                if (totalReplenishment >= totalReducedReplenishment) {
                    long replenishmentSize = replnsWithUnits.size();
                    double perReplenishmentReduced = (totalReducedReplenishment / replenishmentSize);
                    double perReplenishmentReducedRemainder = (totalReducedReplenishment % replenishmentSize);
                    replnsWithUnits.forEach(replenishment -> replenishment.setAdjReplnUnits(Math.round(replenishment.getAdjReplnUnits() - perReplenishmentReduced)));
                    replnsWithUnits.get(0).setAdjReplnUnits(Math.round(replnsWithUnits.get(0).getAdjReplnUnits() - perReplenishmentReducedRemainder));
                    replnsWithUnits.addAll(replnsWithNoUnits);
                    perStoreQty = buyQtyProperties.getInitialThreshold();
                    isQty = perStoreQty * rfaSizePackData.getStore_cnt();
                    log.debug("| IS after IS constraints with more replenishment | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantities.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), addStoreBuyQuantities.getMerchMethodsDto().getFixtureTypeRollupId(), isQty, perStoreQty, storeList.size());
                } else {
                    int storeCntWithNewQty = (int) (totalReplenishment / unitsLessThanThreshold);
                    List<Integer> storeListWithOldQty = storeList.subList(storeCntWithNewQty, storeList.size());
                    StoreQuantity storeQtyCopy = createStoreQuantity(rfaSizePackData, perStoreQty, storeListWithOldQty, perStoreQty * storeListWithOldQty.size(), volumeCluster);
                    storeQtyCopy.setBumpSets(calculateBumpPackQtyService.calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster, storeListWithOldQty.size()));
                    initialSetQuantities.add(storeQtyCopy);
                    log.debug("| IS after IS constraints with less replenishment with old IS qty | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantities.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), addStoreBuyQuantities.getMerchMethodsDto().getFixtureTypeRollupId(),
                            perStoreQty * storeListWithOldQty.size(), perStoreQty, storeListWithOldQty.size());
                    replnsWithUnits.forEach(replenishment -> replenishment.setAdjReplnUnits(0L));
                    replnsWithUnits.addAll(replnsWithNoUnits);
                    storeList = storeList.subList(0, storeCntWithNewQty);
                    perStoreQty = buyQtyProperties.getInitialThreshold();
                    isQty = perStoreQty * storeList.size();

                    log.debug("| IS after IS constraints with less replenishment with new IS qty | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantities.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), addStoreBuyQuantities.getMerchMethodsDto().getFixtureTypeRollupId()
                            , isQty, perStoreQty, storeList.size());
                }
                buyQtyObj.setReplenishments(replnsWithUnits);
            }
        }
        StoreQuantity storeQuantity = createStoreQuantity(rfaSizePackData, perStoreQty, storeList, isQty, volumeCluster);
        storeQuantity.setBumpSets(calculateBumpPackQtyService.calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster, storeList.size()));
        initialSetQuantities.add(storeQuantity);
    }

    private List<Replenishment> getReplnsWithUnits(BuyQtyObj buyQtyObj) {
        return buyQtyObj.getReplenishments().stream()
                .filter(repln -> repln.getAdjReplnUnits() > 0).collect(Collectors.toList());
    }

    private List<Replenishment> getReplnsWithNoUnits(BuyQtyObj buyQtyObj) {
        return buyQtyObj.getReplenishments().stream().filter(repln -> repln.getAdjReplnUnits() == null || repln.getAdjReplnUnits() == 0).collect(Collectors.toList());
    }

    private long getTotalReplenishment(BuyQtyObj buyQtyObj) {
        return buyQtyObj.getReplenishments()
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(Replenishment::getAdjReplnUnits)
                .sum();
    }

    private Cluster getVolumeCluster(AddStoreBuyQuantities addStoreBuyQuantities, RFASizePackData rfaSizePackData) {
        return Optional.ofNullable(addStoreBuyQuantities.getBqfpResponse().getStyles())
                .stream()
                .flatMap(Collection::stream)
                .filter(style -> style.getStyleId().equalsIgnoreCase(addStoreBuyQuantities.getStyleDto().getStyleNbr()))
                .findFirst()
                .map(Style::getCustomerChoices)
                .stream()
                .flatMap(Collection::stream)
                .filter(customerChoice -> customerChoice.getCcId().equalsIgnoreCase(addStoreBuyQuantities.getCustomerChoiceDto().getCcId()))
                .findFirst()
                .map(CustomerChoice::getFixtures)
                .stream()
                .flatMap(Collection::stream)
                .filter(fixture -> fixture.getFixtureTypeRollupId().equals(addStoreBuyQuantities.getMerchMethodsDto().getFixtureTypeRollupId()))
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

    private StoreQuantity createStoreQuantity(RFASizePackData rfaSizePackData, double perStoreQty, List<Integer> storeListWithOldQty, double totalUnits, Cluster volumeCluster) {
        StoreQuantity storeQuantity = new StoreQuantity();
        storeQuantity.setTotalUnits(totalUnits);
        storeQuantity.setIsUnits(perStoreQty);
        storeQuantity.setVolumeCluster(rfaSizePackData.getVolume_group_cluster_id());
        storeQuantity.setSizeCluster(rfaSizePackData.getSize_cluster_id());
        storeQuantity.setStoreList(storeListWithOldQty);
        if (volumeCluster.getFlowStrategy() != null)
            storeQuantity.setFlowStrategyCode(volumeCluster.getFlowStrategy());
        return storeQuantity;
    }
}
