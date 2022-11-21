package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.bqfp.SPInitialSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculateInitialSetQuantityService {

    public SPInitialSetQuantity calculateInitialSetQty(SizeDto sizeDto, Cluster volumeCluster, RFASizePackData rfaSizePackData) {
        /*** Calculate IS Buy Quantity ***/
        SPInitialSetQuantity spInitialSetQuantity = new SPInitialSetQuantity();
        Float isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
        double isQty = (isCalculatedBq * BuyQtyCommonUtil.getSizePct(sizeDto)) / 100;
        double perStoreQty = Math.round(isQty / rfaSizePackData.getStore_cnt());
        isQty = perStoreQty * rfaSizePackData.getStore_cnt();
        spInitialSetQuantity.setIsQty(isQty);
        spInitialSetQuantity.setPerStoreQty(perStoreQty);
        log.debug("| IS before constraints | : {} | {} | {}  ", sizeDto.getSizeDesc(), isQty, perStoreQty);
        return spInitialSetQuantity;
    }
}
