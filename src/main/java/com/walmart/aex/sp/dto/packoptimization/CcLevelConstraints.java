package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class CcLevelConstraints {

	private Integer maxUnitsPerPack;

	private Integer maxPacks;

	private Integer singlePackIndicator;

	private String colorCombination;
}
