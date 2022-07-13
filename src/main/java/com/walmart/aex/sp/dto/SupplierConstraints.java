package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class SupplierConstraints {
	
	private String supplierName;
	
	private Integer maxUnitsPerPack;
	
	private Integer maxPacks;

}
