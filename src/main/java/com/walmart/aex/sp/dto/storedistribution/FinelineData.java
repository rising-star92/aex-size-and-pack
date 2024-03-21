package com.walmart.aex.sp.dto.storedistribution;

import lombok.Data;

@Data
public class FinelineData {
	private Integer finelineNbr;
	private String packId;
	private Long inStoreWeek;
	private String groupingType;
}