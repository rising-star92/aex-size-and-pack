package com.walmart.aex.sp.dto.storedistribution;

import java.util.List;

import lombok.Data;

@Data
public class StoreDistribution {
	private Integer finelineNbr;
	private String styleNbr;
	private List<InitialSetPlanData> initialSetPlanDataList;
}
