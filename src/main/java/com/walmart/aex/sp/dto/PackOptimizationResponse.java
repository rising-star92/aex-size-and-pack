package com.walmart.aex.sp.dto;

import java.util.List;

import lombok.Data;

@Data
public class PackOptimizationResponse {

	private Long planId;
	private Integer channel;
	
	private List<Lvl3> lvl3list;
}
