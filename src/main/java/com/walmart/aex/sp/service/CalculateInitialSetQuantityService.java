package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.InitialSet;
import com.walmart.aex.sp.dto.buyquantity.CalculateInitialSet;
import com.walmart.aex.sp.dto.buyquantity.CalculateQuantityByUnit;
import com.walmart.aex.sp.dto.buyquantity.InitialSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CalculateInitialSetQuantityService {

    private static final double FOUR_PRECISION = 10000;
    private static final Long DEFAULT_IS_QTY = 0L;
    private static final Long DEFAULT_TOTAL_IS_QTY = 1L;

    public InitialSetQuantity calculateInitialSetQty(SizeDto sizeDto, Cluster volumeCluster, RFASizePackData rfaSizePackData) {
        // TODO: remove this version when cleaning up
        InitialSetQuantity initialSetQuantity = new InitialSetQuantity();
        float isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
        double isQty = (isCalculatedBq * BuyQtyCommonUtil.getSizePct(sizeDto)) / 100;
        double perStoreQty = Math.round(isQty / rfaSizePackData.getStore_cnt());
        isQty = perStoreQty * rfaSizePackData.getStore_cnt();
        initialSetQuantity.setIsQty(isQty);
        initialSetQuantity.setPerStoreQty(perStoreQty);
        log.debug("| IS before constraints | : {} | {} | {} ", sizeDto.getSizeDesc(), isQty, perStoreQty);
        return initialSetQuantity;
    }

    /** Calculate IS Buy Quantity **/
    public InitialSetQuantity calculateInitialSetQtyV2(SizeDto sizeDto, Cluster volumeCluster, RFASizePackData rfaSizePackData) {
        InitialSetQuantity initialSetQuantity = new InitialSetQuantity();
        float isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
        // SizePct = precedence value is Merchant Override (AdjSizeProfilePct) then Data Science (SizeProfilePct)
        double sizePct = BuyQtyCommonUtil.getSizePct(sizeDto);
        double isQty = 0.0;
        double perStoreQty = 0.0;
        // if sizePct is > 0 and initialSetUnitsPerFix > 0, then perform the logic to promote perStoreQty to 1 incase if the calculation gives us 0
        if (sizePct > 0.0 && volumeCluster.getInitialSet().getInitialSetUnitsPerFix() != null && volumeCluster.getInitialSet().getInitialSetUnitsPerFix() > 0) {
            isQty = (isCalculatedBq * sizePct) / 100;
            perStoreQty = Math.round(isQty / rfaSizePackData.getStore_cnt());
            if (perStoreQty == 0.0) {
                // explicitly setting perStoreQty as 1
                 perStoreQty = 1.0;
                // flag to identify this particular InitialSet is set to 1
                initialSetQuantity.setOneUnitPerStore(true); // rep
            }
            isQty = perStoreQty * rfaSizePackData.getStore_cnt();
        }
        initialSetQuantity.setIsQty(isQty);
        initialSetQuantity.setPerStoreQty(perStoreQty);
        log.debug("| IS before constraints and isOneUnitPerStore | : {} | {} | {} | {}  ", sizeDto.getSizeDesc(), isQty, perStoreQty, initialSetQuantity.isOneUnitPerStore());
        return initialSetQuantity;
    }

    /**
     * Initiate Calculate of Initial Set
     */
    public void calculateInitialSet(CalculateInitialSet calculateInitialSet, SizeDto sizeDto, Cluster volumeCluster, RFASizePackData rfaSizePackData) {
        log.info("Calculating IS Quantity: {}", sizeDto.getSizeDesc());
        InitialSetQuantity initialSetQuantity = calculateInitialSetQtyV3(sizeDto, volumeCluster, rfaSizePackData);
        calculateInitialSet.setTotalUnits(calculateInitialSet.getTotalUnits() + initialSetQuantity.getRoundedPerStoreQty());
        calculateInitialSet.getInitialSetQuantities().add(initialSetQuantity);
    }

    /**
     * Calculate IS V3 - this version captures more properties in the resulting object
     */
    private InitialSetQuantity calculateInitialSetQtyV3(SizeDto sizeDto, Cluster volumeCluster, RFASizePackData rfaSizePackData) {
        InitialSetQuantity initialSetQuantity = new InitialSetQuantity();
        float isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
        // SizePct = precedence value is Merchant Override (AdjSizeProfilePct) then Data Science (SizeProfilePct)
        double sizePct = BuyQtyCommonUtil.getSizePct(sizeDto);
        double isQty = 0.0;
        double perStoreQty = 0.0;
        // if sizePct is > 0 and initialSetUnitsPerFix > 0, then perform the logic to promote perStoreQty to 1 incase if the calculation gives us 0
        if (sizePct > 0.0 && volumeCluster.getInitialSet().getInitialSetUnitsPerFix() != null && volumeCluster.getInitialSet().getInitialSetUnitsPerFix() > 0) {
            isQty = (isCalculatedBq * sizePct) / 100;
            perStoreQty = ((isQty / rfaSizePackData.getStore_cnt()) * FOUR_PRECISION) / FOUR_PRECISION;
            if (perStoreQty < 0.5) {
                // flag to identify this particular InitialSet is set to 1
                initialSetQuantity.setOneUnitPerStore(true); // rep
            }
            isQty = (Math.round(perStoreQty) * rfaSizePackData.getStore_cnt());
        }
        initialSetQuantity.setCalculatedISQty(Math.round(isQty));
        initialSetQuantity.setRoundedPerStoreQty(Math.round(perStoreQty));
        initialSetQuantity.setOriginalPerStoreQty(perStoreQty);
        initialSetQuantity.setSizeDesc(sizeDto.getSizeDesc());
        initialSetQuantity.setSizePct(sizePct);
        initialSetQuantity.setRfaSizePackData(rfaSizePackData);
        initialSetQuantity.setVolumeCluster(volumeCluster);
        log.debug("| IS before adjusting units, constraints and isOneUnitPerStore | : {} | {} | {} | {}  ", sizeDto.getSizeDesc(), isQty, perStoreQty, initialSetQuantity.isOneUnitPerStore());
        return initialSetQuantity;
    }

    /**
     * Adjusting IS Units by comparing it with BQ Total Units
     */
    public void adjustInitialSetUnits(CalculateQuantityByUnit calculateQuantityByUnit) {
        CalculateInitialSet calculateInitialSet = calculateQuantityByUnit.getCalculateInitialSet();
        if (!calculateQuantityByUnit.getTotalUnitsFromBQ().equals(calculateInitialSet.getTotalUnits())) {
            if (calculateQuantityByUnit.getTotalUnitsFromBQ() < calculateInitialSet.getTotalUnits()) {
                reduceISUnits(calculateInitialSet.getTotalUnits() - calculateQuantityByUnit.getTotalUnitsFromBQ(), calculateInitialSet.getInitialSetQuantities(), calculateQuantityByUnit.getStoreCount());
            } else if (calculateQuantityByUnit.getTotalUnitsFromBQ() > calculateInitialSet.getTotalUnits()) {
                distributeISUnits(calculateQuantityByUnit.getTotalUnitsFromBQ() - calculateInitialSet.getTotalUnits(), calculateInitialSet.getInitialSetQuantities(), calculateQuantityByUnit.getStoreCount());
            }
            calculateInitialSet.setTotalUnits(calculateInitialSet.getInitialSetQuantities().stream().mapToLong(InitialSetQuantity::getRoundedPerStoreQty).sum());
        }
    }

    /**
     * if total Units from BQ is less than what calculatedISQty gave then reduce IS units
     */
    private void reduceISUnits(Long unitsToReduce, List<InitialSetQuantity> initialSetQuantities, Integer storeCount) {
        List<InitialSetQuantity> sortedInitialSetQuantities = initialSetQuantities.stream()
                .filter(isQty -> getDecimalDifference(isQty) >= 0.5 && isQty.getRoundedPerStoreQty() > 1)
                .sorted(Comparator.comparing(CalculateInitialSetQuantityService::getDecimalDifference))
                .collect(Collectors.toList());
//        If all the IS are having 1 unit
        if (sortedInitialSetQuantities.isEmpty()) {
            sortedInitialSetQuantities = initialSetQuantities.stream()
                    .filter(isQty -> getDecimalDifference(isQty) >= 0.5)
                    .sorted(Comparator.comparing(CalculateInitialSetQuantityService::getDecimalDifference))
                    .collect(Collectors.toList());
        }
        if (!sortedInitialSetQuantities.isEmpty() &&  sortedInitialSetQuantities.size() > 1 &&
                getDecimalDifference(sortedInitialSetQuantities.get(0)) == getDecimalDifference(sortedInitialSetQuantities.get(1))) {
            sortedInitialSetQuantities = sortedInitialSetQuantities.stream().sorted(Comparator.comparing(InitialSetQuantity::getSizePct).reversed()).collect(Collectors.toList());
        }
        adjustUnits(unitsToReduce, sortedInitialSetQuantities, storeCount, true);
    }

    /**
     * get decimal difference value
     */
    private static double getDecimalDifference(InitialSetQuantity isQty) {
        return isQty.getOriginalPerStoreQty() - (int) isQty.getOriginalPerStoreQty();
    }

    /**
     * if total Units from BQ is greater than what calculatedISQty gave then increase IS units
     */
    private void distributeISUnits(Long unitsToIncrease, List<InitialSetQuantity> initialSetQuantities, Integer storeCount) {
        List<InitialSetQuantity> isQuantitiesWithOneUnitRule = initialSetQuantities.stream()
                .filter(InitialSetQuantity::isOneUnitPerStore).collect(Collectors.toList());
        List<InitialSetQuantity> sortedInitialSetQuantities = initialSetQuantities.stream()
                .filter(isQty -> getDecimalDifference(isQty) < 0.5)
                .sorted(Comparator.comparing(CalculateInitialSetQuantityService::getDecimalDifference).reversed())
                .collect(Collectors.toList());
        if (!sortedInitialSetQuantities.isEmpty() &&  sortedInitialSetQuantities.size() > 1 &&
                getDecimalDifference(sortedInitialSetQuantities.get(0)) == getDecimalDifference(sortedInitialSetQuantities.get(1))) {
            sortedInitialSetQuantities = sortedInitialSetQuantities.stream().sorted(Comparator.comparing(InitialSetQuantity::getSizePct)).collect(Collectors.toList());
        }
        unitsToIncrease = adjustUnits(unitsToIncrease, isQuantitiesWithOneUnitRule, storeCount, false);
        adjustUnits(unitsToIncrease, sortedInitialSetQuantities, storeCount, false);
    }

    /**
     * Adjust units either by reducing or increasing until unitsToAdjust comes down to 0 and recalculated ISQty
     */
    private static Long adjustUnits(Long unitsToAdjust, List<InitialSetQuantity> initialSetQuantities, Integer storeCount, boolean reduceUnits) {
        for (InitialSetQuantity initialSetQuantity : initialSetQuantities) {
            if(unitsToAdjust == 0)
                break;
            if (reduceUnits) {
                initialSetQuantity.setRoundedPerStoreQty(initialSetQuantity.getRoundedPerStoreQty() - 1);
                if (initialSetQuantity.getRoundedPerStoreQty() == 0)
                    initialSetQuantity.setOneUnitPerStore(true);
            }
            else {
                initialSetQuantity.setRoundedPerStoreQty(initialSetQuantity.getRoundedPerStoreQty() + 1);
                if (initialSetQuantity.isOneUnitPerStore())
                    initialSetQuantity.setOneUnitPerStore(false);
            }
            initialSetQuantity.setCalculatedISQty(initialSetQuantity.getRoundedPerStoreQty() * storeCount);
            unitsToAdjust--;
        }
        return unitsToAdjust;
    }

    /**
     * Setting default values for IS before initiating the calculation
     */
    public void setDefaultValueForNullInitialSet(Cluster volumeCluster) {
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
