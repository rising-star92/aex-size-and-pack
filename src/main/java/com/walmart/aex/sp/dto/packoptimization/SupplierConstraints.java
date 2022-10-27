package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class SupplierConstraints {

	private String factoryIds;

	private String countryOfOrigin;

	private String portOfOrigin;

	private String supplierName;
	
	private Integer maxUnitsPerPack;
	
	private Integer maxPacks;

}
