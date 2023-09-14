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
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
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

    @ManagedConfiguration
    BuyQtyProperties buyQtyProperties;

    private static final Long DEFAULT_IS_QTY = 0L;
    private static final Long DEFAULT_TOTAL_IS_QTY = 1L;
    private static final Integer DEFAULT_INITIAL_THRESHOLD = 1;

    public AddStoreBuyQuantityService() {

    }

    public AddStoreBuyQuantityService(ObjectMapper objectMapper,
                                      CalculateBumpPackQtyService calculateBumpPackQtyService,
                                      BuyQuantityConstraintService buyQuantityConstraintService,
                                      CalculateInitialSetQuantityService calculateInitialSetQuantityService,
                                      BuyQtyProperties buyQtyProperties) {
        this.objectMapper = objectMapper;
        this.calculateBumpPackQtyService = calculateBumpPackQtyService;
        this.buyQuantityConstraintService = buyQuantityConstraintService;
        this.calculateInitialSetQuantityService = calculateInitialSetQuantityService;
        this.buyQtyProperties = buyQtyProperties;
    }


    public void addStoreBuyQuantities(AddStoreBuyQuantity addStoreBuyQuantity, BuyQtyObj buyQtyObj, Integer initialThreshold) {
        BuyQtyStoreObj buyQtyStoreObj = Optional.ofNullable(buyQtyObj)
                .map(BuyQtyObj::getBuyQtyStoreObj)
                .orElse(new BuyQtyStoreObj());

        List<StoreQuantity> initialSetQuantities = Optional.of(buyQtyStoreObj)
                .map(BuyQtyStoreObj::getBuyQuantities)
                .orElse(new ArrayList<>());

        List<RFASizePackData> rfaSizePackDataList = addStoreBuyQuantity.getRfaSizePackDataList();
        if (Boolean.parseBoolean(buyQtyProperties.getOneUnitPerStoreFeatureFlag())) {
            List<StoreQuantity> processQuantities = new ArrayList<>();
            // First iteration to make sure each store gets atleast one unit
            rfaSizePackDataList.forEach(rfaSizePackData -> addOneUnitPerStore(addStoreBuyQuantity, buyQtyObj, processQuantities, rfaSizePackData));
            // Second iteration on the result of the first to adjust the rep with admin rule
            setInitialSetAndBumpSetQtyV2(addStoreBuyQuantity, processQuantities, buyQtyObj, initialThreshold);
            initialSetQuantities.addAll(processQuantities);
        } else {
            rfaSizePackDataList.forEach(rfaSizePackData -> calculateAndAddStoreBuyQuantities(addStoreBuyQuantity, buyQtyObj, initialSetQuantities, rfaSizePackData, initialThreshold));
        }
        initialSetQuantities.forEach(quantity -> quantity.setRfaSizePackData(null));
        buyQtyStoreObj.setBuyQuantities(initialSetQuantities);
        if(!ObjectUtils.isEmpty(buyQtyObj)) {
            buyQtyObj.setBuyQtyStoreObj(buyQtyStoreObj);
        }
    }

    /**
     * Setting default values for initial set and start the process of making sure each store gets atleast one unit
     * @param addStoreBuyQuantity
     * @param buyQtyObj
     * @param initialSetQuantities
     * @param rfaSizePackData
     * @result initialSetQuantities
     */
    private void addOneUnitPerStore(AddStoreBuyQuantity addStoreBuyQuantity, BuyQtyObj buyQtyObj, List<StoreQuantity> initialSetQuantities, RFASizePackData rfaSizePackData) {
        if (rfaSizePackData == null) {
            log.warn("rfaSizePackData is null. Not adding storeBuyQuantities for styleNbr : {} , ccId :{}  ", addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId());
            return;
        }
        // Get volume cluster
        Cluster volumeCluster = getVolumeCluster(addStoreBuyQuantity, rfaSizePackData);
        if (volumeCluster != null) {
            // Set default values for initial set
            setDefaultValueForNullInitialSet(volumeCluster);
            // calculation logic to make sure each store gets one unit
            setInitialSetWithAtleastOneUnitPerStore(addStoreBuyQuantity, initialSetQuantities, volumeCluster, buyQtyObj,rfaSizePackData);
        }
    }

    /**
     * Calculation logic to make sure each store gets one unit
     * @param addStoreBuyQuantity
     * @param initialSetQuantities
     * @param volumeCluster
     * @param buyQtyObj
     * @param rfaSizePackData
     * @result initialSetQuantities
     */
    private void setInitialSetWithAtleastOneUnitPerStore(AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> initialSetQuantities, Cluster volumeCluster, BuyQtyObj buyQtyObj, RFASizePackData rfaSizePackData) {
        List<Integer> storeList = safeReadStoreList(rfaSizePackData.getStore_list()).stream().sorted().collect(Collectors.toList());
        SizeDto sizeDto = addStoreBuyQuantity.getSizeDto();
        // calculate the InitialSetQty
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQtyV2(sizeDto, volumeCluster, rfaSizePackData); // 0
        double perStoreQty = initialSetQuantity.getPerStoreQty();
        double isQty = initialSetQuantity.getIsQty();
        // Based on the flag which we get from calculateInitialSetQty method to figure our which one is explicitly been set to 1
        if (initialSetQuantity.isOneUnitPerStore() && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()))) {
            long totalReplenishment = getTotalReplenishment(buyQtyObj);
            if (totalReplenishment > 0) {
                double totalReducedReplenishment = rfaSizePackData.getStore_cnt();
                if (totalReplenishment >= totalReducedReplenishment) {
                    buyQuantityConstraintService.getISWithMoreReplenConstraint(buyQtyObj, totalReducedReplenishment, rfaSizePackData, DEFAULT_INITIAL_THRESHOLD);
                    log.debug("| Replenishment count after adjusting with more replenishment | : {} | {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size(), getTotalReplenishment(buyQtyObj));
                } else {
                    // When the replenishment is less than the store count, reduce available replenishment count to zero
                    buyQtyObj.getReplenishments().stream()
                            .filter(rep -> rep.getAdjReplnUnits() > 0)
                            .forEach(replenishment -> replenishment.setAdjReplnUnits(0L));

                    log.debug("| Replenishment count after adjusting with less replenishment | : {} | {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size(), getTotalReplenishment(buyQtyObj));
                }
            }
        }
        StoreQuantity storeQuantity = BuyQtyCommonUtil.createStoreQuantity(rfaSizePackData, perStoreQty, storeList, isQty, volumeCluster);
        initialSetQuantities.add(storeQuantity);
    }

    // TODO: This needs to be removed once the feature flag goes away for oneUnitPerStore
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

    // TODO: This needs to be removed once the feature flag goes away for oneUnitPerStore
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
                    perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                    isQty = initialSetWithReplnsConstraint.getIsQty();
                    log.debug("| IS after IS constraints with more replenishment | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size());
                } else {
                    int storeCntWithNewQty = (int) (totalReplenishment / unitsLessThanThreshold);
                    InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithLessReplenConstraint(buyQtyObj, storeCntWithNewQty, storeList, perStoreQty, rfaSizePackData, volumeCluster, sizeDto, initialThreshold);
                    storeList = storeList.subList(0, storeCntWithNewQty);
                    initialSetQuantities.add(initialSetWithReplnsConstraint.getStoreQuantity());
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

    /**
     * Calculate the admin rule based on the minimum threshold and bump sets.
     * @param addStoreBuyQuantity
     * @param initialSetQuantities
     * @param buyQtyObj
     * @param initialThreshold
     * @result initialSetQuantities
     */
    private void setInitialSetAndBumpSetQtyV2(AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> initialSetQuantities, BuyQtyObj buyQtyObj, Integer initialThreshold) {
        List<StoreQuantity> initialSetQuantitiesWithLessRep = new ArrayList<>();
        for (int i = 0; i < initialSetQuantities.size(); i++) {
            StoreQuantity initialQuantity = initialSetQuantities.get(i);
            RFASizePackData rfaSizePackData = initialQuantity.getRfaSizePackData();
            Cluster volumeCluster = getVolumeCluster(addStoreBuyQuantity, rfaSizePackData);
            List<Integer> storeList = initialQuantity.getStoreList();
            double perStoreQty = initialQuantity.getIsUnits();
            double isQty = initialQuantity.getTotalUnits();
            if ((perStoreQty < initialThreshold && perStoreQty > 0) && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()))) {
                InitialSetWithReplenishment initialSetWithReplenishment = getUnitsFromReplenishment(initialQuantity, buyQtyObj, addStoreBuyQuantity, initialSetQuantitiesWithLessRep, volumeCluster, initialThreshold);
                isQty = initialSetWithReplenishment.getIsQty();
                perStoreQty = initialSetWithReplenishment.getPerStoreQty();
                storeList = initialSetWithReplenishment.getStoreList();
                buyQtyObj.setReplenishments(initialSetWithReplenishment.getReplenishments());
            }
            initialQuantity = BuyQtyCommonUtil.createStoreQuantity(rfaSizePackData, perStoreQty, storeList, isQty, volumeCluster);
            initialQuantity.setBumpSets(calculateBumpPackQtyService.calculateBumpPackQty(addStoreBuyQuantity.getSizeDto(), rfaSizePackData, volumeCluster, storeList.size()));
            initialSetQuantities.set(i, initialQuantity);
        }
        initialSetQuantities.addAll(initialSetQuantitiesWithLessRep);
    }

    private InitialSetWithReplenishment getUnitsFromReplenishment(StoreQuantity initialQuantity, BuyQtyObj buyQtyObj, AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> initialSetQuantities, Cluster volumeCluster, Integer initialThreshold) {
        long totalReplenishment = getTotalReplenishment(buyQtyObj);
        SizeDto sizeDto = addStoreBuyQuantity.getSizeDto();
        double perStoreQty = initialQuantity.getIsUnits();
        double isQty = initialQuantity.getTotalUnits();
        RFASizePackData rfaSizePackData = initialQuantity.getRfaSizePackData();
        List<Integer> storeList = initialQuantity.getStoreList();
        if (totalReplenishment > 0) {
            double unitsLessThanThreshold = initialThreshold - perStoreQty;
            double totalReducedReplenishment = unitsLessThanThreshold * rfaSizePackData.getStore_cnt();
            if (totalReplenishment >= totalReducedReplenishment) {
                InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithMoreReplenConstraint(buyQtyObj, totalReducedReplenishment, rfaSizePackData, initialThreshold);
                perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                isQty = initialSetWithReplnsConstraint.getIsQty();
                log.debug("| IS after IS constraints with more replenishment | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size());
            } else {
                int storeCntWithNewQty = (int) (totalReplenishment / unitsLessThanThreshold);
                InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithLessReplenConstraint(buyQtyObj, storeCntWithNewQty, storeList, perStoreQty, rfaSizePackData, volumeCluster, sizeDto, initialThreshold);
                storeList = storeList.subList(0, storeCntWithNewQty);
                initialSetQuantities.add(initialSetWithReplnsConstraint.getStoreQuantity());
                perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                isQty = initialSetWithReplnsConstraint.getIsQty();

                log.debug("| IS after IS constraints with less replenishment with new IS qty | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type())
                        , isQty, perStoreQty, storeList.size());
            }
        }
        return new InitialSetWithReplenishment(buyQtyObj.getReplenishments(), isQty, perStoreQty, storeList);
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
        }
        if (volumeCluster.getInitialSet().getInitialSetUnitsPerFix() == null) {
            log.warn("InitialSetUnitsPerFix of volumeCluster : {} is null. Setting InitialSetUnitsPerFix as zero ", volumeCluster);
            volumeCluster.getInitialSet().setInitialSetUnitsPerFix(DEFAULT_IS_QTY);
        }
        if (volumeCluster.getInitialSet().getTotalInitialSetUnits() == null) {
            log.warn("TotalInitialSetUnits of volumeCluster : {} is null. Setting TotalInitialSetUnits as one ", volumeCluster);
            volumeCluster.getInitialSet().setTotalInitialSetUnits(DEFAULT_TOTAL_IS_QTY);
        }
    }
}
