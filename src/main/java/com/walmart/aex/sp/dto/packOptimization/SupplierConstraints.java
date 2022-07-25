package com.walmart.aex.sp.dto.packOptimization;

import lombok.Data;

@Data
public class SupplierConstraints {
	
	private String supplierName;
	
	private Integer maxUnitsPerPack;
	
	private Integer maxPacks;

}
