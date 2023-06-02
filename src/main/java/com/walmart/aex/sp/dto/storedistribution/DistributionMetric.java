package com.walmart.aex.sp.dto.storedistribution;

import lombok.Data;

@Data
public class DistributionMetric implements Comparable<DistributionMetric> {
	private Integer store;
	private Integer multiplier;

	@Override
	public int compareTo(DistributionMetric metric) {
		return this.store.compareTo(metric.getStore());
	}
}
