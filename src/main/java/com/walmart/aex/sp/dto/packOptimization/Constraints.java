package com.walmart.aex.sp.dto.packOptimization;

import java.util.List;

import lombok.Data;

@Data
public class Constraints {
	
	private SupplierConstraints supplierConstraints;
	
	private List<CcLevelConstraints> ccLevelConstraints;
	
	

}
