package com.walmart.aex.sp.util;

import java.util.List;

import com.walmart.aex.sp.service.BuyQuantityMapper;
import org.springframework.stereotype.Component;
import com.walmart.aex.sp.dto.bqfp.Replenishment;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdjustedDCInboundQty {

	public static List<Replenishment> updatedAdjustedDcInboundQty(List<Replenishment> replenishments,Integer vnpkQty) {

		if(replenishments!=null && vnpkQty>0) {
			replenishments.forEach(replobj -> {
				long noOfVendorPacks = (replobj.getAdjReplnUnits() / vnpkQty);
				Long updatedAdjustedDcInboundQty = noOfVendorPacks * vnpkQty;
				replobj.setAdjReplnUnits(updatedAdjustedDcInboundQty);
			});
		}
		return replenishments;
		}
}