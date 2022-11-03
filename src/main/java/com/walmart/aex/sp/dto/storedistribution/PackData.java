package com.walmart.aex.sp.dto.storedistribution;

import lombok.Data;

@Data
public class PackData {
	private Long planId;
	private Integer finelineNbr;
	private String packId;
	private Long inStoreWeek;
}
