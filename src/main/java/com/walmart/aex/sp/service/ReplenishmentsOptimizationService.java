package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor
public class ReplenishmentsOptimizationService {

    public List<Replenishment> getUpdatedReplenishmentsPack(List<Replenishment> replenishments) {

        //get non-zero weeks which are supposed to be adjusted.
        List<Replenishment> nonZeroReplenishmentList = replenishments.stream().filter(replenishment -> replenishment.getAdjReplnUnits() > 0).collect(Collectors.toList());

        //sum of all adjReplnUnits
        long futureWeekAdjReplnUnitsSum = nonZeroReplenishmentList.stream().map(replenishment -> replenishment.getAdjReplnUnits()).mapToLong(Long::longValue).sum();


        //if only 1 week or empty , no operation can be performed.
        if (nonZeroReplenishmentList == null || nonZeroReplenishmentList.isEmpty()|| nonZeroReplenishmentList.size()==1) {
            return replenishments;
        }

        //adjust the adjReplnUnits using.
        for (int i = 0; i < nonZeroReplenishmentList.size(); i++) {
            //if current adjReplnUnits is less than 500 only then it need adjustment and if nonZeroReplenishmentList is last and is less 500
            if (nonZeroReplenishmentList.get(i).getAdjReplnUnits() < 500) {

                //get available future adjReplnUnits exclude current
                futureWeekAdjReplnUnitsSum = Math.abs(futureWeekAdjReplnUnitsSum - nonZeroReplenishmentList.get(i).getAdjReplnUnits());

                long required = Math.abs(500 - nonZeroReplenishmentList.get(i).getAdjReplnUnits());

                if (required > futureWeekAdjReplnUnitsSum) {

                    int k = i - 1;

                    //add all future week and myself to previous.
                    nonZeroReplenishmentList.get(k).setAdjReplnUnits(nonZeroReplenishmentList.get(k).getAdjReplnUnits() + nonZeroReplenishmentList.get(i).getAdjReplnUnits() + futureWeekAdjReplnUnitsSum);
                    futureWeekAdjReplnUnitsSum = 0;

                    //set current to 0
                    nonZeroReplenishmentList.get(i).setAdjReplnUnits(0L);

                    //set future week to 0
                    required = 0;
                }

                //for every week [i], we need to iterate future weeks [j] to get required, if we get required then break or else loop exits itself
                for (int j = i + 1; j < nonZeroReplenishmentList.size(); j++) {
                    if (required == 0) {
                        nonZeroReplenishmentList.get(j).setAdjReplnUnits(0L);
                    }

                    if (required > nonZeroReplenishmentList.get(j).getAdjReplnUnits()) {
                        long temp = nonZeroReplenishmentList.get(j).getAdjReplnUnits();
                        nonZeroReplenishmentList.get(j).setAdjReplnUnits(0L);
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
        return nonZeroReplenishmentList;
    }
}
