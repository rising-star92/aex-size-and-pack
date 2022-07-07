package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class CcLevelConstraints {

	private String factoryIds;
	
	private String countryOfOrigin;
	
	private String portOfOrigin;
	
	private Integer singlePackIndicator;
	
	private String colorCombination;
}
