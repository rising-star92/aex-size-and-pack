package com.walmart.aex.sp.dto.storedistribution;

import java.util.List;

import lombok.Data;

@Data
public class InitialSetPlanData {
	private Long inStoreWeek;
	private List<PackDistribution> packDistributionList;
}
