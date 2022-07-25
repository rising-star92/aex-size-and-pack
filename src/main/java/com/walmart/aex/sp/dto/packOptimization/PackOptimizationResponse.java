package com.walmart.aex.sp.dto.packOptimization;

import java.util.List;

import com.walmart.aex.sp.dto.planHierarchy.Lvl3;
import lombok.Data;

@Data
public class PackOptimizationResponse {
	private Long planId;
	private Integer channel;
	private List<Lvl3> lvl3List;
}
