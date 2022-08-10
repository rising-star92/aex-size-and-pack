package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import com.walmart.aex.sp.dto.buyquantity.FinelineDto;

import lombok.Data;
@Data

public class FineLinePackOptimizationResponse {

	private Long planId;
	private String planDesc;
	private List<FinelineDto> finelines;

}
