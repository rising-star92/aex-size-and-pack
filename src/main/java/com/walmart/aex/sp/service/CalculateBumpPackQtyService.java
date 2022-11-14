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
            bsQty = bsPerStoreQty * storeCnt;
            bumpSetQuantity.setTotalUnits(bsQty);
            bumpSetQuantity.setBsUnits(bsPerStoreQty);
            bumpPackQuantities.add(bumpSetQuantity);
        });
        return bumpPackQuantities;
    }
}
