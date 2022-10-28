package com.walmart.aex.sp.dto.storedistribution;

import java.util.List;

import lombok.Data;

@Data
public class PackDistribution {
	private String packId;
	private List<DistributionMetric> distributionMetricList;
}
