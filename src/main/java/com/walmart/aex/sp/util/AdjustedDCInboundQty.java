package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AdjustedDCInboundQty {

    private AdjustedDCInboundQty() {
    }

    public static List<Replenishment> updatedAdjustedDcInboundQty(List<Replenishment> replenishments, Double vnpkWhpkRatio) {

        if (replenishments != null && vnpkWhpkRatio > 0) {
            replenishments.forEach(replobj -> {
                int noOfVendorPacks = (int) Math.ceil(replobj.getAdjReplnUnits() / vnpkWhpkRatio);
                Long updatedAdjustedDcInboundQty = (long) (noOfVendorPacks * vnpkWhpkRatio);
                replobj.setDcInboundAdjUnits(updatedAdjustedDcInboundQty);
            });
        }
        return replenishments;
    }
}