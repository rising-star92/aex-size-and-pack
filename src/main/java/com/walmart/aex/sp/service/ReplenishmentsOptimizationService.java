package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReplenishmentsOptimizationService {

    public static final Integer MINIMUM_REPLENISHMENT_QUANTITY = 500;

    /**
     * Update Replenishments packs count from the list
     * @param replenishments
     * @param vnpkQty
     * @return
     */
    public List<Replenishment> getUpdatedReplenishmentsPack(List<Replenishment> replenishments, Integer vnpkQty, Integer channelId) {

        if (CollectionUtils.isEmpty(replenishments)) {
            log.info("Replenishment list is null/empty");
            return Collections.emptyList();
        }
        /** Adjust the replenishment unit only for Store */
        if(1 == channelId) {
            //get non-zero weeks which are supposed to be adjusted.
            List<Replenishment> nonZeroReplenishmentList = replenishments.stream().filter(replenishment -> replenishment.getAdjReplnUnits() != null && replenishment.getAdjReplnUnits() > 0).collect(Collectors.toList());

            //sum of all adjReplnUnits including starting index
            long futureWeekAdjReplnUnitsSum = nonZeroReplenishmentList.stream().map(Replenishment::getAdjReplnUnits).mapToLong(Long::longValue).sum();


            //adjust the adjReplnUnits
            for (int i = 0; i < nonZeroReplenishmentList.size(); i++) {
                //if current adjReplnUnits is less than Minimum_Replenishment_Quantity only then it need adjustment and if nonZeroReplenishmentList is last and is less Minimum_Replenishment_Quantity
                if (nonZeroReplenishmentList.get(i).getAdjReplnUnits() < MINIMUM_REPLENISHMENT_QUANTITY) {
                    //get available future adjReplnUnits exclude current
                    futureWeekAdjReplnUnitsSum = Math.abs(futureWeekAdjReplnUnitsSum - nonZeroReplenishmentList.get(i).getAdjReplnUnits());

                    // if future weeks sum is less than Minimum_Replenishment_Quantity then add to current week and make rest to 0
                    if (futureWeekAdjReplnUnitsSum < MINIMUM_REPLENISHMENT_QUANTITY) {
                        //add all of them and make future weeks to 0
                        setDefaultFutureWeek(nonZeroReplenishmentList, futureWeekAdjReplnUnitsSum, i);
                        break;
                    }
                    //for every week [i], we need to iterate future weeks [j] to get required, if we get required then break or else loop exits itself
                    futureWeekAdjReplnUnitsSum = getFutureWeekAdjReplnUnitsSum(nonZeroReplenishmentList, futureWeekAdjReplnUnitsSum, i);

                } else {
                    futureWeekAdjReplnUnitsSum = Math.abs(futureWeekAdjReplnUnitsSum - nonZeroReplenishmentList.get(i).getAdjReplnUnits());
                }
            }
        }
        updatedAdjustedDcInboundQty(replenishments,vnpkQty);
        return replenishments;
    }

    private void updatedAdjustedDcInboundQty(List<Replenishment> replenishments,Integer vnpkQty) {

        if(replenishments!=null && vnpkQty!= null && vnpkQty>0) {
            replenishments.forEach(replobj -> {
                if(replobj.getAdjReplnUnits()!=null){
                    double noOfVendorPacks = Math.ceil((double)(replobj.getAdjReplnUnits()) / vnpkQty); // Math.ceil will return a double value and double has higher range than int, therefore using double
                    Long updatedAdjReplnUnits = (long)(noOfVendorPacks * vnpkQty);
                    replobj.setAdjReplnUnits(updatedAdjReplnUnits);
                }
            });
        }
    }

    /**
     * Set 0 as default for all future weeks and add the current week with futureWeekAdjReplnUnitsSum
     * @param nonZeroReplenishmentList
     * @param futureWeekAdjReplnUnitsSum
     * @param i
     */
    private static void setDefaultFutureWeek(List<Replenishment> nonZeroReplenishmentList, long futureWeekAdjReplnUnitsSum, int i) {
        nonZeroReplenishmentList.get(i).setAdjReplnUnits(nonZeroReplenishmentList.get(i).getAdjReplnUnits() + futureWeekAdjReplnUnitsSum);

        // make rest of the weeks to 0
        for (int j = i + 1; j < nonZeroReplenishmentList.size(); j++) {
            nonZeroReplenishmentList.get(j).setAdjReplnUnits(0L);
        }
        // break out of loop and return

        if (i > 0 && nonZeroReplenishmentList.get(i).getAdjReplnUnits() < MINIMUM_REPLENISHMENT_QUANTITY) {
            int k = i - 1;
            nonZeroReplenishmentList.get(k).setAdjReplnUnits(nonZeroReplenishmentList.get(k).getAdjReplnUnits() + nonZeroReplenishmentList.get(i).getAdjReplnUnits());
            nonZeroReplenishmentList.get(i).setAdjReplnUnits(0L);
        }
    }


    /**
     * Get futureWeekAdjReplnUnitsSum by removing the Adjusted Replenish Units
     * @param nonZeroReplenishmentList
     * @param futureWeekAdjReplnUnitsSum
     * @param i
     * @return
     */
    private static long getFutureWeekAdjReplnUnitsSum(List<Replenishment> nonZeroReplenishmentList, long futureWeekAdjReplnUnitsSum, int i) {
        long required = Math.abs(MINIMUM_REPLENISHMENT_QUANTITY - nonZeroReplenishmentList.get(i).getAdjReplnUnits());
        for (int j = i + 1; j < nonZeroReplenishmentList.size(); j++) {
            if (required == 0 || nonZeroReplenishmentList.get(j).getAdjReplnUnits() == 0) {
                break;
            }

            if (required >= nonZeroReplenishmentList.get(j).getAdjReplnUnits()) {
                long temp = nonZeroReplenishmentList.get(j).getAdjReplnUnits();
                nonZeroReplenishmentList.get(j).setAdjReplnUnits(required - temp);
                nonZeroReplenishmentList.get(i).setAdjReplnUnits(nonZeroReplenishmentList.get(i).getAdjReplnUnits() + temp);
                required = required - temp;
                futureWeekAdjReplnUnitsSum = futureWeekAdjReplnUnitsSum - temp;
            }

            if (required < nonZeroReplenishmentList.get(j).getAdjReplnUnits()) {
                nonZeroReplenishmentList.get(j).setAdjReplnUnits(nonZeroReplenishmentList.get(j).getAdjReplnUnits() - required);
                nonZeroReplenishmentList.get(i).setAdjReplnUnits(nonZeroReplenishmentList.get(i).getAdjReplnUnits() + required);
                futureWeekAdjReplnUnitsSum = futureWeekAdjReplnUnitsSum - required;
                required = 0;
            }
        }
        return futureWeekAdjReplnUnitsSum;
    }
}
