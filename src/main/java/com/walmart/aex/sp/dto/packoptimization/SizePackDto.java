package com.walmart.aex.sp.dto.packoptimization;

import java.util.List;

import lombok.Data;

@Data
public class SizePackDto {
	private String sizeDesc;
	private List<MetricsPackDto>  metrics;

}
