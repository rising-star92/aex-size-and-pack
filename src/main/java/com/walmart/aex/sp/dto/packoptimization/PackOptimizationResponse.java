package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import lombok.Data;

@Data
public class PackOptimizationResponse {
	private Long planId;
	private Integer channel;
	private List<Lvl3> lvl3List;
}
