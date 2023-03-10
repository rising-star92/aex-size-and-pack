package com.walmart.aex.sp.dto.storedistribution;

import lombok.Data;

@Data
public class StoreDistributionDTO {
	private String productFineline;
	private Integer finelineNbr;
	private String styleNbr;
	private Long inStoreWeek;
	private String packId;
	private Integer store;
	private Integer packMultiplier;
	private Integer clusterId;
	private String cc;
	private Float fixtureAllocation;
	private String fixtureType;
}
