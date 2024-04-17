package com.walmart.aex.sp.dto.storedistribution;

import java.util.List;

import lombok.Data;

@Data
public class PackInfo {
	private Long planId;
	private String season;
	private String fiscalYear;
	private String channel;
	private List<FinelineData> finelineDataList;

}