package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.bqfp.Cluster;
import com.walmart.aex.sp.dto.buyquantity.InitialSetQuantity;
import com.walmart.aex.sp.dto.buyquantity.SizeDto;
import com.walmart.aex.sp.util.BuyQtyCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CalculateInitialSetQuantityService {

    public InitialSetQuantity calculateInitialSetQty(SizeDto sizeDto, Cluster volumeCluster, RFASizePackData rfaSizePackData) {
        /*** Calculate IS Buy Quantity ***/
        InitialSetQuantity initialSetQuantity = new InitialSetQuantity();
        Float isCalculatedBq = rfaSizePackData.getStore_cnt() * volumeCluster.getInitialSet().getInitialSetUnitsPerFix() * rfaSizePackData.getFixture_group();
        // SizePct = precedence value is Merchant Override (AdjSizeProfilePct) then Data Science (SizeProfilePct)
        double sizePct = BuyQtyCommonUtil.getSizePct(sizeDto);
        double isQty = 0.0;
        double perStoreQty = 0.0;
        // if sizePct is > 0 then perform the logic to promote perStoreQty to 1 incase if the calculation gives us 0
        if (sizePct > 0.0) {
            isQty = (isCalculatedBq * sizePct) / 100;
            perStoreQty = Math.round(isQty / rfaSizePackData.getStore_cnt());
            if (perStoreQty == 0.0) {
                // explicitly setting perStoreQty as 1
                 perStoreQty = 1.0;
                // flag to identify this particular InitialSet is set to 1
                initialSetQuantity.setZeroPerStore(true); // rep
            }
            isQty = perStoreQty * rfaSizePackData.getStore_cnt();
        }
        initialSetQuantity.setIsQty(isQty);
        initialSetQuantity.setPerStoreQty(perStoreQty);
        log.debug("| IS before constraints | : {} | {} | {}  ", sizeDto.getSizeDesc(), isQty, perStoreQty);
        return initialSetQuantity;
    }
}
