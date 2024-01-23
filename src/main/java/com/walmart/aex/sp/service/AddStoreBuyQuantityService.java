package com.walmart.aex.sp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.*;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.enums.FixtureTypeRollup;
import com.walmart.aex.sp.exception.CustomException;
import com.walmart.aex.sp.properties.BuyQtyProperties;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.*;

import java.util.*;
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
        initialSetQuantities.forEach(quantity -> {
            quantity.setRfaSizePackData(null);
            quantity.setCluster(null);
        });
        buyQtyStoreObj.setBuyQuantities(initialSetQuantities);
        if(!ObjectUtils.isEmpty(buyQtyObj)) {
            buyQtyObj.setBuyQtyStoreObj(buyQtyStoreObj);
        }
    }

    /**
     * Setting default values for initial set and start the process of making sure each store gets atleast one unit
     */
    private void addOneUnitPerStore(AddStoreBuyQuantity addStoreBuyQuantity, BuyQtyObj buyQtyObj, List<StoreQuantity> initialSetQuantities, RFASizePackData rfaSizePackData) {
        if (rfaSizePackData == null) {
            log.warn("rfaSizePackData is null. Not adding storeBuyQuantities for styleNbr : {} , ccId :{}  ", addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId());
            return;
        }
        // Get volume cluster
        Cluster volumeCluster = BuyQtyCommonUtil.getVolumeCluster(addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), addStoreBuyQuantity.getBqfpResponse(), rfaSizePackData);
        if (volumeCluster != null) {
            calculateInitialSetQuantityService.setDefaultValueForNullInitialSet(volumeCluster);
            // calculation logic to make sure each store gets one unit
            adjustISForOneUnitPerStore(addStoreBuyQuantity, initialSetQuantities, volumeCluster, buyQtyObj,rfaSizePackData);
        }
    }

    /**
     * Calculation logic to make sure each store gets one unit
     */
    private void adjustISForOneUnitPerStore(AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> initialSetQuantities, Cluster volumeCluster, BuyQtyObj buyQtyObj, RFASizePackData rfaSizePackData) {
        List<Integer> storeList = safeReadStoreList(rfaSizePackData.getStore_list()).stream().sorted().collect(Collectors.toList());
        SizeDto sizeDto = addStoreBuyQuantity.getSizeDto();
        // calculate the InitialSetQty
        InitialSetQuantity initialSetQuantity = calculateInitialSetQuantityService.calculateInitialSetQtyV2(sizeDto, volumeCluster, rfaSizePackData);
        double perStoreQty = initialSetQuantity.getPerStoreQty();
        double isQty = initialSetQuantity.getIsQty();
        // Based on the flag which we get from calculateInitialSetQty method to figure our which one is explicitly been set to 1
        if (initialSetQuantity.isOneUnitPerStore() && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()))) {
            adjustReplenishmentsForOneUnitPerStore(buyQtyObj, rfaSizePackData, addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), isQty, perStoreQty, storeList);
        }
        StoreQuantity storeQuantity = BuyQtyCommonUtil.createStoreQuantity(rfaSizePackData, perStoreQty, storeList, isQty, volumeCluster);
        initialSetQuantities.add(storeQuantity);
    }

    private void adjustReplenishmentsForOneUnitPerStore(BuyQtyObj buyQtyObj, RFASizePackData rfaSizePackData, String ccId, String sizeDesc, double isQty, double perStoreQty, List<Integer> storeList) {
        long totalReplenishment = getTotalReplenishment(buyQtyObj);
        if (totalReplenishment > 0) {
            double totalReducedReplenishment = rfaSizePackData.getStore_cnt();
            if (totalReplenishment >= totalReducedReplenishment) {
                buyQuantityConstraintService.getISWithMoreReplenConstraint(buyQtyObj, totalReducedReplenishment, rfaSizePackData, DEFAULT_INITIAL_THRESHOLD);
                log.debug("| Replenishment count after adjusting with more replenishment | : {} | {} | {} | {} | {} | {} | {}", ccId, sizeDesc, FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size(), getTotalReplenishment(buyQtyObj));
            } else {
                // When the replenishment is less than the store count, reduce available replenishment count to zero
                buyQtyObj.getReplenishments().stream()
                        .filter(rep -> rep.getAdjReplnUnits() > 0)
                        .forEach(replenishment -> replenishment.setAdjReplnUnits(0L));

                log.debug("| Replenishment count after adjusting with less replenishment | : {} | {} | {} | {} | {} | {} | {}", ccId, sizeDesc, FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size(), getTotalReplenishment(buyQtyObj));
            }
        }
    }

    public void adjustISForOneUnitPerStoreV2(BuyQtyObj buyQtyObj, List<StoreQuantity> storeQuantities) {
        List<Integer> warningCodes = new ArrayList<>();
        for (CalculateQuantityBySize calculateQuantityBySize: buyQtyObj.getCalculateQuantityBySizes()) {
            InitialSetQuantity initialSetQuantity = calculateQuantityBySize.getInitialSetQuantity();
            List<Integer> storeList = safeReadStoreList(initialSetQuantity.getRfaSizePackData().getStore_list()).stream().sorted().collect(Collectors.toList());
            long perStoreQty = initialSetQuantity.getRoundedPerStoreQty();
            long isQty = initialSetQuantity.getCalculatedISQty();
            boolean oneUnitRuleToIncreaseTBQ = false;
            boolean oneUnitRuleToRemoveRepl = false;
            // Based on the flag which we get from calculateInitialSetQty method to figure out which one needs to explicitly set to 1
            if (initialSetQuantity.isOneUnitPerStore()) {
                perStoreQty = perStoreQty + DEFAULT_INITIAL_THRESHOLD;
                isQty = (long) DEFAULT_INITIAL_THRESHOLD * initialSetQuantity.getRfaSizePackData().getStore_cnt();
                oneUnitRuleToIncreaseTBQ = true;
                if (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments()) && BuyQtyCommonUtil.isReplenishmentEligible(initialSetQuantity.getVolumeCluster().getFlowStrategy())) {
                    oneUnitRuleToRemoveRepl = true;
                    adjustReplenishmentsForOneUnitPerStore(buyQtyObj, initialSetQuantity.getRfaSizePackData(), initialSetQuantity.getRfaSizePackData().getCustomer_choice(), initialSetQuantity.getSizeDesc(), isQty, perStoreQty, storeList);
                }
            }
            StoreQuantity storeQuantity = BuyQtyCommonUtil.createStoreQuantity(initialSetQuantity.getRfaSizePackData(), perStoreQty, storeList, isQty, initialSetQuantity.getVolumeCluster());
            if(oneUnitRuleToIncreaseTBQ) {
                warningCodes.add(203);
            }
            if(oneUnitRuleToRemoveRepl) {
                warningCodes.add(204);
            }
            storeQuantities.add(storeQuantity);
        }
        buyQtyObj.setValidationCode(ValidationCode.builder().messages(warningCodes).build());
    }

    // TODO: This needs to be removed once the feature flag goes away for oneUnitPerStore
    public void calculateAndAddStoreBuyQuantities(AddStoreBuyQuantity addStoreBuyQuantity, BuyQtyObj buyQtyObj, List<StoreQuantity> initialSetQuantities, RFASizePackData rfaSizePackData, Integer initialThreshold) {
        if (rfaSizePackData == null) {
            log.warn("rfaSizePackData is null. Not adding storeBuyQuantities for styleNbr : {} , ccId :{}  ", addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId());
            return;
        }
        Cluster volumeCluster = BuyQtyCommonUtil.getVolumeCluster(addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), addStoreBuyQuantity.getBqfpResponse(), rfaSizePackData);
        if (volumeCluster != null) {
            calculateInitialSetQuantityService.setDefaultValueForNullInitialSet(volumeCluster);
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
     */
    private void setInitialSetAndBumpSetQtyV2(AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> initialSetQuantities, BuyQtyObj buyQtyObj, Integer initialThreshold) {
        List<StoreQuantity> initialSetQuantitiesWithLessRep = new ArrayList<>();
        for (int i = 0; i < initialSetQuantities.size(); i++) {
            StoreQuantity initialQuantity = initialSetQuantities.get(i);
            RFASizePackData rfaSizePackData = initialQuantity.getRfaSizePackData();
            Cluster volumeCluster = BuyQtyCommonUtil.getVolumeCluster(addStoreBuyQuantity.getStyleDto().getStyleNbr(), addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), addStoreBuyQuantity.getBqfpResponse(), rfaSizePackData);
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

    public void adjustISWithConstraint(List<StoreQuantity> storeQuantities, BuyQtyObj buyQtyObj, Integer initialThreshold, String sizeDesc) {
        List<StoreQuantity> storeQuantitiesWithLessRep = new ArrayList<>();
        for (int i = 0; i < storeQuantities.size(); i++) {
            StoreQuantity storeQuantity = storeQuantities.get(i);
            RFASizePackData rfaSizePackData = storeQuantity.getRfaSizePackData();
            Cluster volumeCluster = storeQuantity.getCluster();
            List<Integer> storeList = storeQuantity.getStoreList();
            double perStoreQty = storeQuantity.getIsUnits();
            double isQty = storeQuantity.getTotalUnits();
            if ((perStoreQty < initialThreshold && perStoreQty > 0) && (!CollectionUtils.isEmpty(buyQtyObj.getReplenishments())) && BuyQtyCommonUtil.isReplenishmentEligible(storeQuantity.getFlowStrategyCode())) {
                InitialSetWithReplenishment initialSetWithReplenishment = getUnitsFromReplenishment(storeQuantity, buyQtyObj, storeQuantitiesWithLessRep, volumeCluster, initialThreshold, rfaSizePackData.getCustomer_choice(), sizeDesc);
                isQty = initialSetWithReplenishment.getIsQty();
                perStoreQty = initialSetWithReplenishment.getPerStoreQty();
                storeList = initialSetWithReplenishment.getStoreList();
                buyQtyObj.setReplenishments(initialSetWithReplenishment.getReplenishments());
            }
            storeQuantity = BuyQtyCommonUtil.createStoreQuantity(rfaSizePackData, perStoreQty, storeList, isQty, volumeCluster);
            storeQuantities.set(i, storeQuantity);
        }
        storeQuantities.addAll(storeQuantitiesWithLessRep);
    }

    private InitialSetWithReplenishment getUnitsFromReplenishment(StoreQuantity storeQuantity, BuyQtyObj buyQtyObj, AddStoreBuyQuantity addStoreBuyQuantity, List<StoreQuantity> storeQuantities, Cluster volumeCluster, Integer initialThreshold) {
        long totalReplenishment = getTotalReplenishment(buyQtyObj);
        SizeDto sizeDto = addStoreBuyQuantity.getSizeDto();
        double perStoreQty = storeQuantity.getIsUnits();
        double isQty = storeQuantity.getTotalUnits();
        RFASizePackData rfaSizePackData = storeQuantity.getRfaSizePackData();
        List<Integer> storeList = storeQuantity.getStoreList();
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
                storeQuantities.add(initialSetWithReplnsConstraint.getStoreQuantity());
                perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                isQty = initialSetWithReplnsConstraint.getIsQty();

                log.debug("| IS after IS constraints with less replenishment with new IS qty | : {} | {} | {} | {} | {} | {}", addStoreBuyQuantity.getCustomerChoiceDto().getCcId(), sizeDto.getSizeDesc(), FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type())
                        , isQty, perStoreQty, storeList.size());
            }
        }
        return new InitialSetWithReplenishment(buyQtyObj.getReplenishments(), isQty, perStoreQty, storeList);
    }

    private InitialSetWithReplenishment getUnitsFromReplenishment(StoreQuantity storeQuantity, BuyQtyObj buyQtyObj, List<StoreQuantity> storeQuantities, Cluster volumeCluster, Integer initialThreshold, String ccId, String sizeDesc) {
        long totalReplenishment = getTotalReplenishment(buyQtyObj);
        double perStoreQty = storeQuantity.getIsUnits();
        double isQty = storeQuantity.getTotalUnits();
        RFASizePackData rfaSizePackData = storeQuantity.getRfaSizePackData();
        List<Integer> storeList = storeQuantity.getStoreList();
        if (totalReplenishment > 0) {
            double unitsLessThanThreshold = initialThreshold - perStoreQty;
            double totalReducedReplenishment = unitsLessThanThreshold * rfaSizePackData.getStore_cnt();
            if (totalReplenishment >= totalReducedReplenishment) {
                InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithMoreReplenConstraint(buyQtyObj, totalReducedReplenishment, rfaSizePackData, initialThreshold);
                perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                isQty = initialSetWithReplnsConstraint.getIsQty();
                log.debug("| IS after IS constraints with more replenishment | : {} | {} | {} | {} | {} | {}", ccId, sizeDesc, FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type()), isQty, perStoreQty, storeList.size());
            } else {
                int storeCntWithNewQty = (int) (totalReplenishment / unitsLessThanThreshold);
                InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = buyQuantityConstraintService.getISWithLessReplenConstraint(buyQtyObj, storeCntWithNewQty, storeList, perStoreQty, rfaSizePackData, volumeCluster, initialThreshold);
                storeList = storeList.subList(0, storeCntWithNewQty);
                storeQuantities.add(initialSetWithReplnsConstraint.getStoreQuantity());
                perStoreQty = initialSetWithReplnsConstraint.getPerStoreQty();
                isQty = initialSetWithReplnsConstraint.getIsQty();

                log.debug("| IS after IS constraints with less replenishment with new IS qty | : {} | {} | {} | {} | {} | {}", ccId, sizeDesc, FixtureTypeRollup.getFixtureIdFromName(rfaSizePackData.getFixture_type())
                        , isQty, perStoreQty, storeList.size());
            }
        }
        return new InitialSetWithReplenishment(buyQtyObj.getReplenishments(), isQty, perStoreQty, storeList);
    }

    public void adjustBSWithConstraint(List<CalculateQuantityBySize> calculateQuantityBySizeList, List<StoreQuantity> storeQuantities) {
        for (StoreQuantity storeQuantity : storeQuantities) {
            List<BumpSetQuantity> bumpSetQuantities = calculateQuantityBySizeList.stream()
                    .filter(cQty -> cQty.getVolumeGroupClusterId().equals(storeQuantity.getVolumeCluster()) &&
                            cQty.getFixtureType().equals(storeQuantity.getRfaSizePackData().getFixture_type()) &&
                            cQty.getFixtureGroup().equals(storeQuantity.getRfaSizePackData().getFixture_group()))
                    .map(CalculateQuantityBySize::getBumpSetQuantities)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            if (!bumpSetQuantities.isEmpty()) {
                long storeCount = Math.round(bumpSetQuantities.get(0).getTotalUnits() / bumpSetQuantities.get(0).getBsUnits());
                if (storeCount == storeQuantity.getStoreList().size()) {
                    storeQuantity.setBumpSets(bumpSetQuantities);
                } else {
                    storeQuantity.setBumpSets(calculateBumpPackQtyService.adjustBumpSet(bumpSetQuantities, storeQuantity.getStoreList().size()));
                }
            }
        }
    }

    private long getTotalReplenishment(BuyQtyObj buyQtyObj) {
        return buyQtyObj.getReplenishments()
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(Replenishment::getAdjReplnUnits)
                .sum();
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
