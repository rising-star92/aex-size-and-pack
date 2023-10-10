package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.buyquantity.BumpSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CalculateBumpPackQtyService {

    public List<BumpSetQuantity> calculateBumpPackQty(SizeDto sizeDto, RFASizePackData rfaSizePackData, Cluster volumeCluster, int storeCnt) {
        List<BumpSetQuantity> bumpPackQuantities = new ArrayList<>();
        volumeCluster.getBumpList().forEach(bumpSet -> {
            BumpSetQuantity bumpSetQuantity = new BumpSetQuantity();
            //Calculate BS Buy Quantity
            double bumpQtyPerFixture = (bumpSet.getUnits() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix()) / volumeCluster.getInitialSet().getTotalInitialSetUnits().doubleValue();
            double bsCalculatedBq = storeCnt * bumpQtyPerFixture * rfaSizePackData.getFixture_group();
            double bsQty = (bsCalculatedBq * BuyQtyCommonUtil.getSizePct(sizeDto)) / 100;
            double bsPerStoreQty = Math.round(bsQty / storeCnt);
            double roundedBSQty = bsPerStoreQty * storeCnt;
            bumpSetQuantity.setTotalUnits(roundedBSQty);
            bumpSetQuantity.setBsUnits(bsPerStoreQty);
            bumpSetQuantity.setSetNbr(bumpSet.getBumpPackNbr());
            bumpSetQuantity.setWeekDesc(bumpSet.getWeekDesc());
            bumpSetQuantity.setWmYearWeek(String.valueOf(bumpSet.getWmYearWeek()));
            bumpSetQuantity.setSizeDesc(sizeDto.getSizeDesc());
            bumpPackQuantities.add(bumpSetQuantity);
        });
        return bumpPackQuantities;
    }

    public List<BumpSetQuantity> calculateBumpPackQtyV2(SizeDto sizeDto, RFASizePackData rfaSizePackData, Cluster volumeCluster) {
        return calculateBumpPackQty(sizeDto, rfaSizePackData, volumeCluster, rfaSizePackData.getStore_cnt());
    }

    public List<BumpSetQuantity> adjustBumpSet(List<BumpSetQuantity> bumpSetQuantities, int storeCount) {
        List<BumpSetQuantity> bumpSetQuantityList = new ArrayList<>();
        for (BumpSetQuantity bumpSetQuantity: bumpSetQuantities) {
            double newTotalUnits = bumpSetQuantity.getBsUnits() * storeCount;
            BumpSetQuantity bumpSetQuantityNew = new BumpSetQuantity();
            bumpSetQuantityNew.setSetNbr(bumpSetQuantity.getSetNbr());
            bumpSetQuantityNew.setBsUnits(bumpSetQuantity.getBsUnits());
            bumpSetQuantityNew.setWeekDesc(bumpSetQuantity.getWeekDesc());
            bumpSetQuantityNew.setSizeDesc(bumpSetQuantity.getSizeDesc());
            bumpSetQuantityNew.setWmYearWeek(bumpSetQuantity.getWmYearWeek());
            bumpSetQuantityNew.setTotalUnits(newTotalUnits);
            bumpSetQuantityList.add(bumpSetQuantityNew);

            bumpSetQuantity.setTotalUnits(Math.abs(bumpSetQuantity.getTotalUnits() - newTotalUnits));
        }
        return bumpSetQuantityList;
    }
}
