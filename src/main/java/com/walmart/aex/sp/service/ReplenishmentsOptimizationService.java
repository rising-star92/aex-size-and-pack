package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@NoArgsConstructor
public class ReplenishmentsOptimizationService {

    public static final Integer MINIMUM_REPLENISHMENT_QUANTITY = 500;

    public List<Replenishment> getUpdatedReplenishmentsPack(List<Replenishment> replenishments) {

        //get non-zero weeks which are supposed to be adjusted.
        List<Replenishment> nonZeroReplenishmentList = replenishments.stream().filter(replenishment -> replenishment.getAdjReplnUnits() > 0).collect(Collectors.toList());

        //sum of all adjReplnUnits including starting index
        long futureWeekAdjReplnUnitsSum = nonZeroReplenishmentList.stream().map(replenishment -> replenishment.getAdjReplnUnits()).mapToLong(Long::longValue).sum();


        //if only 1 week or empty , no operation can be performed.
        if (nonZeroReplenishmentList == null || nonZeroReplenishmentList.isEmpty()|| nonZeroReplenishmentList.size()==1) {
            return replenishments;
        }

        //adjust the adjReplnUnits
        for (int i = 0; i < nonZeroReplenishmentList.size(); i++) {
            //if current adjReplnUnits is less than Minimum_Replenishment_Quantity only then it need adjustment and if nonZeroReplenishmentList is last and is less Minimum_Replenishment_Quantity
            if (nonZeroReplenishmentList.get(i).getAdjReplnUnits() < MINIMUM_REPLENISHMENT_QUANTITY) {

                //get available future adjReplnUnits exclude current
                futureWeekAdjReplnUnitsSum = Math.abs(futureWeekAdjReplnUnitsSum - nonZeroReplenishmentList.get(i).getAdjReplnUnits());

                long required = Math.abs(MINIMUM_REPLENISHMENT_QUANTITY - nonZeroReplenishmentList.get(i).getAdjReplnUnits());

                // if future weeks sum is less than Minimum_Replenishment_Quantity then add to current week and make rest to 0;
                if (futureWeekAdjReplnUnitsSum < MINIMUM_REPLENISHMENT_QUANTITY) {

                    //add all of them and make future weeks to 0
                    nonZeroReplenishmentList.get(i).setAdjReplnUnits(nonZeroReplenishmentList.get(i).getAdjReplnUnits() + futureWeekAdjReplnUnitsSum);

                    // make rest of the weeks to 0
                    for (int j = i + 1; j < nonZeroReplenishmentList.size(); j++) {
                        nonZeroReplenishmentList.get(j).setAdjReplnUnits(0L);
                    }
                    // break out of loop and return

                    if(i>0 && nonZeroReplenishmentList.get(i).getAdjReplnUnits()< MINIMUM_REPLENISHMENT_QUANTITY){
                        int k = i-1;
                        nonZeroReplenishmentList.get(k).setAdjReplnUnits(nonZeroReplenishmentList.get(k).getAdjReplnUnits()+nonZeroReplenishmentList.get(i).getAdjReplnUnits());
                        nonZeroReplenishmentList.get(i).setAdjReplnUnits(0L);
                    }

                    break;
                }

                //for every week [i], we need to iterate future weeks [j] to get required, if we get required then break or else loop exits itself
                for (int j = i + 1; j < nonZeroReplenishmentList.size(); j++) {
                    if (required == 0 || nonZeroReplenishmentList.get(j).getAdjReplnUnits() == 0 ) {
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

            } else {
                futureWeekAdjReplnUnitsSum = Math.abs(futureWeekAdjReplnUnitsSum - nonZeroReplenishmentList.get(i).getAdjReplnUnits());
            }
        }
        return replenishments;
    }
}
