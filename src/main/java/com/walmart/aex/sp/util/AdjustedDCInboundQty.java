package com.walmart.aex.sp.util;

import com.walmart.aex.sp.dto.bqfp.Replenishment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AdjustedDCInboundQty {

	public static List<Replenishment> updatedAdjustedDcInboundQty(List<Replenishment> replenishments,Integer vnpkQty) {

		if(replenishments!=null && vnpkQty!= null && vnpkQty>0) {
			replenishments.forEach(replobj -> {
				if(replobj.getAdjReplnUnits()!=null){
					double noOfVendorPacks = Math.ceil((double)(replobj.getAdjReplnUnits()) / vnpkQty); // Math.ceil will return a double value and double has higher range than int, therefore using double
					Long updatedAdjustedDcInboundQty = (long)(noOfVendorPacks * vnpkQty);
					replobj.setAdjReplnUnits(updatedAdjustedDcInboundQty);
				}
			});
		}
		return replenishments;
		}

}