package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class Constraints {
	private SupplierConstraints supplierConstraints;
	private CcLevelConstraints ccLevelConstraints;
	private ColorCombinationConstraints colorCombinationConstraints;
	private FinelineLevelConstraints finelineLevelConstraints;
}
