package com.walmart.aex.sp.dto;

import java.util.List;

import lombok.Data;

@Data
public class Constraints {
	
	private SupplierConstraints supplierConstraints;
	
	private List<CcLevelConstraints> ccLevelConstraints;
	
	

}
