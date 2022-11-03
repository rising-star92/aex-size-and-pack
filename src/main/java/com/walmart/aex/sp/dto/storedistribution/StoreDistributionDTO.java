package com.walmart.aex.sp.dto.storedistribution;

import lombok.Data;

@Data
public class StoreDistributionDTO {
	private Integer finelineNbr;
	private String styleNbr;
	private Long inStoreWeek;
	private String packId;
	private Integer store;
	private Integer initialPackMultiplier;
}