package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.walmart.aex.sp.dto.planhierarchy.Lvl3;
import lombok.Data;

@Data
public class PackOptimizationResponse {
	private Long planId;
	private Integer channel;
	private Integer lvl0Nbr;
	private String lvl0Desc;
	private Integer lvl1Nbr;
	private String lvl1Desc;
	private Integer lvl2Nbr;
	private String lvl2Desc;
	private List<Lvl3> lvl3List;
}
