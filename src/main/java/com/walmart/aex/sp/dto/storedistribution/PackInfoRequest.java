package com.walmart.aex.sp.dto.storedistribution;

import java.util.List;

import lombok.Data;

@Data
public class PackInfoRequest {
	private List<PackInfo> packInfoList;
	private String groupingType;
}
