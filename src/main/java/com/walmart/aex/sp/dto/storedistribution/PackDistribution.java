package com.walmart.aex.sp.dto.storedistribution;

import java.util.Set;

import lombok.Data;

@Data
public class PackDistribution {
	private String packId;
	private Set<DistributionMetric> distributionMetricList;
}
