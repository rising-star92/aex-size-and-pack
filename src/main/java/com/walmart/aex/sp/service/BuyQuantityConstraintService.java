package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.Replenishment;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.enums.AppMessageText;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.walmart.aex.sp.util.CommonUtil.getValidationCodesFromBuyQtyObj;

@Service
@Slf4j
public class BuyQuantityConstraintService {

    @Autowired
    CalculateBumpPackQtyService calculateBumpPackQtyService;

    public BuyQuantityConstraintService() {
    }

    public BuyQuantityConstraintService(CalculateBumpPackQtyService calculateBumpPackQtyService) {
        this.calculateBumpPackQtyService = calculateBumpPackQtyService;
    }


    public InitialSetWithReplnsConstraint getISWithMoreReplenConstraint(BuyQtyObj buyQtyObj, double totalReducedReplenishment, RFASizePackData rfaSizePackData, Integer initialThreshold) {
        List<Replenishment> replnsWithUnits = getReplnsWithUnits(buyQtyObj);
        long replenishmentSize = replnsWithUnits.size();
        double perReplenishmentReduced = (totalReducedReplenishment / replenishmentSize);
        double perReplenishmentReducedRemainder = (totalReducedReplenishment % replenishmentSize);
        double remainingWeekUnits = 0.0;
        for (Replenishment replenishment: replnsWithUnits) {
            long result = replenishment.getAdjReplnUnits() - (int) perReplenishmentReduced;
            // if the perReplenishmentReduced is greater than the current week, capture the remainder and reduce it from future week
            if (result < 0)
                remainingWeekUnits = remainingWeekUnits + Math.abs(result);
            replenishment.setAdjReplnUnits(getAdjustedDifference(result));
        }
        if (remainingWeekUnits > 0) {
            replnsWithUnits = getReplnsWithUnits(buyQtyObj);
            reduceUnits(replnsWithUnits, remainingWeekUnits);
        }
        replnsWithUnits.get(0).setAdjReplnUnits(getAdjustedDifference(getRoundedDifference(replnsWithUnits.get(0).getAdjReplnUnits(), perReplenishmentReducedRemainder)));
        double isQty = (double)initialThreshold * rfaSizePackData.getStore_cnt();
        InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = new InitialSetWithReplnsConstraint();
        initialSetWithReplnsConstraint.setIsQty(isQty);
        initialSetWithReplnsConstraint.setPerStoreQty(initialThreshold);
        return initialSetWithReplnsConstraint;
    }

    private static long getRoundedDifference(long replenishmentUnits, double perReplenishmentReduced) {
        return Math.round(replenishmentUnits - perReplenishmentReduced);
    }

    private void reduceUnits(List<Replenishment> replenishments, double remainingUnits) {
        for (Replenishment replenishment : replenishments) {
            if (replenishment.getAdjReplnUnits() > 0) {
                if (remainingUnits == 0)
                    break;
                long result = getRoundedDifference(replenishment.getAdjReplnUnits(), remainingUnits);
                replenishment.setAdjReplnUnits(getAdjustedDifference(result));
                if (result < 0)
                    remainingUnits = Math.abs(result);
                else
                    remainingUnits = 0;
            }
        }
    }

    private long getAdjustedDifference(long value) {
        return Math.max(value, 0);
    }

    public InitialSetWithReplnsConstraint getISWithLessReplenConstraint(BuyQtyObj buyQtyObj, int storeCntWithNewQty, List<Integer> storeList, double perStoreQty, RFASizePackData rfaSizePackData, Cluster volumeCluster, SizeDto sizeDto, Integer initialThreshold) {
        List<Replenishment> replnsWithUnits = getReplnsWithUnits(buyQtyObj);
        List<Integer> storeListWithOldQty = storeList.subList(storeCntWithNewQty, storeList.size());
        StoreQuantity storeQtyCopy = BuyQtyCommonUtil.createStoreQuantity(rfaSizePackData, perStoreQty, storeListWithOldQty, perStoreQty * storeListWithOldQty.size(), volumeCluster);
        if (sizeDto != null) {
            storeQtyCopy.setBumpSets(calculateBumpPackQtyService.calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster, storeListWithOldQty.size()));
        }
        replnsWithUnits.forEach(replenishment -> replenishment.setAdjReplnUnits(0L));
        storeList = storeList.subList(0, storeCntWithNewQty);
        perStoreQty = initialThreshold;
        double isQty = perStoreQty * storeList.size();
        InitialSetWithReplnsConstraint initialSetWithReplnsConstraint = new InitialSetWithReplnsConstraint();
        initialSetWithReplnsConstraint.setStoreQuantity(storeQtyCopy);
        initialSetWithReplnsConstraint.setIsQty(isQty);
        initialSetWithReplnsConstraint.setPerStoreQty(perStoreQty);
        return initialSetWithReplnsConstraint;
    }

    public InitialSetWithReplnsConstraint getISWithLessReplenConstraint(BuyQtyObj buyQtyObj, int storeCntWithNewQty, List<Integer> storeList, double perStoreQty, RFASizePackData rfaSizePackData, Cluster volumeCluster, Integer initialThreshold) {
        return getISWithLessReplenConstraint(buyQtyObj, storeCntWithNewQty, storeList, perStoreQty, rfaSizePackData, volumeCluster, null, initialThreshold);
    }

    public void processReplenishmentConstraints(Map.Entry<SizeDto, BuyQtyObj> entry, long totalReplenishment, Integer replenishmentThreshold) {
        Set<Integer> codes = getValidationCodesFromBuyQtyObj(entry.getValue());
        if (totalReplenishment < replenishmentThreshold && totalReplenishment > 0) {
            codes.add(AppMessageText.REPLN_UNITS_MOVED_TO_INITIAL_SET.getId());
            entry.getValue().setValidationResult(ValidationResult.builder().codes(codes).build());
            while (entry.getValue().getTotalReplenishment() > 0)
                updateReplnToInitialSet(entry);
        }
    }

    private void updateReplnToInitialSet(Map.Entry<SizeDto, BuyQtyObj> entry) {
        List<StoreQuantity> splitStoreQtys = new ArrayList<>();
        Comparator<StoreQuantity> sqc = Comparator.comparing(StoreQuantity::getVolumeCluster);
        Comparator<StoreQuantity> sqc2 = Comparator.comparing(StoreQuantity::getSizeCluster);
        /* If some store clusters have initial set qtys, then only add replenishment to those store clusters.
           If no store clusters have initial set qty, then populate store clusters until replenishment is depleted */
        List<StoreQuantity> sortedStoreQty = entry.getValue().getBuyQtyStoreObj().getBuyQuantities().stream()
                .sorted(sqc.thenComparing(sqc2))
                .collect(Collectors.toList());
        List<StoreQuantity> replenishmentQuantities = sortedStoreQty.stream()
                .filter(sq -> BuyQtyCommonUtil.isReplenishmentEligible(sq.getFlowStrategyCode()))
                .collect(Collectors.toList());
        List<StoreQuantity> populatedStoreQtys = replenishmentQuantities.stream().filter(storeQty -> storeQty.getIsUnits() > 0).collect(Collectors.toList());
        List<StoreQuantity> storeQtysToUpdate = populatedStoreQtys.isEmpty() ? replenishmentQuantities : populatedStoreQtys;
        for (StoreQuantity storeQuantity : storeQtysToUpdate) {
            if (entry.getValue().getTotalReplenishment() >= storeQuantity.getStoreList().size()) {
                updateStoreQuantity(entry, storeQuantity);
                log.debug("| IS after Replenishment constraints with more replenishment with new IS qty | : {} | {} | {} | {} | {} | {}", storeQuantity.getIsUnits(), storeQuantity.getTotalUnits(),
                        storeQuantity.getStoreList().size(), storeQuantity.getVolumeCluster(), storeQuantity.getSizeCluster(), entry.getValue().getTotalReplenishment());
            } else if (entry.getValue().getTotalReplenishment() > 0) {
                log.debug("Splitting store list for new qtys: {}", storeQuantity.getStoreList());
                StoreQuantity storeQuantity1 = new StoreQuantity();
                storeQuantity1.setIsUnits(storeQuantity.getIsUnits());
                storeQuantity1.setVolumeCluster(storeQuantity.getVolumeCluster());
                storeQuantity1.setSizeCluster(storeQuantity.getSizeCluster());
                storeQuantity1.setStoreList(storeQuantity.getStoreList().subList((int) entry.getValue().getTotalReplenishment(), storeQuantity.getStoreList().size()));
                storeQuantity1.setTotalUnits(storeQuantity1.getIsUnits() * storeQuantity1.getStoreList().size());
                storeQuantity1.setBumpSets(storeQuantity.getBumpSets());
                storeQuantity1.setFlowStrategyCode(storeQuantity.getFlowStrategyCode());

                storeQuantity1.getBumpSets().forEach(bumpSetQuantity -> bumpSetQuantity.setTotalUnits(bumpSetQuantity.getBsUnits() * storeQuantity1.getStoreList().size()));

                log.debug("| IS after Replenishment constraints with less replenishment with old IS qty and split store list | : {} | {} | {} | {} | {} | {}", storeQuantity1.getIsUnits(), storeQuantity1.getTotalUnits(),
                        storeQuantity1.getStoreList().size(), storeQuantity1.getVolumeCluster(), storeQuantity1.getSizeCluster(), entry.getValue().getTotalReplenishment());
                splitStoreQtys.add(storeQuantity1);
                storeQuantity.setStoreList(storeQuantity.getStoreList().subList(0, (int) entry.getValue().getTotalReplenishment()));
                updateStoreQuantity(entry, storeQuantity);
                log.debug("| IS after Replenishment constraints with less replenishment with new IS qty and split store list | : {} | {} | {} | {} | {} | {}", storeQuantity.getIsUnits(), storeQuantity.getTotalUnits(),
                        storeQuantity.getStoreList().size(), storeQuantity.getVolumeCluster(), storeQuantity.getSizeCluster(), entry.getValue().getTotalReplenishment());
            } else break;
        }
        sortedStoreQty.addAll(splitStoreQtys);
        entry.getValue().getBuyQtyStoreObj().setBuyQuantities(sortedStoreQty);
    }

    private void updateStoreQuantity(Map.Entry<SizeDto, BuyQtyObj> entry, StoreQuantity storeQuantity) {
        storeQuantity.setIsUnits(storeQuantity.getIsUnits() + 1);
        storeQuantity.setTotalUnits(storeQuantity.getIsUnits() * storeQuantity.getStoreList().size());
        storeQuantity.getBumpSets().forEach(bumpSetQuantity -> bumpSetQuantity.setTotalUnits(bumpSetQuantity.getBsUnits() * storeQuantity.getStoreList().size()));
        entry.getValue().setTotalReplenishment(entry.getValue().getTotalReplenishment() - storeQuantity.getStoreList().size());
    }

    public long getTotalReplenishment(List<Replenishment> replenishments) {
        return replenishments
                .stream()
                .filter(Objects::nonNull)
                .mapToLong(Replenishment::getAdjReplnUnits)
                .sum();
    }

    private List<Replenishment> getReplnsWithUnits(BuyQtyObj buyQtyObj) {
        return buyQtyObj.getReplenishments().stream()
                .filter(repln -> repln.getAdjReplnUnits() > 0).collect(Collectors.toList());
    }

}
