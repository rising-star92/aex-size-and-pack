package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import lombok.Data;

@Data
public class MetricsPackDto {
	
	private Integer clusterId;
	private List<Integer> storeList;
	private Integer initialSet;
	private Integer bumpSet;

}
