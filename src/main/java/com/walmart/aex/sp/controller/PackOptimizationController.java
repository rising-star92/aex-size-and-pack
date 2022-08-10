package com.walmart.aex.sp.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.service.PackOptimizationService;


import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestController

@Api(consumes = MediaType.APPLICATION_JSON_VALUE)

public class PackOptimizationController {
	private final PackOptimizationService packOptService;

	public PackOptimizationController(PackOptimizationService packOptService) {
		this.packOptService = packOptService;
	}

	@QueryMapping
	public PackOptimizationResponse getPackOptimizationValues(@Argument Long planid, @Argument Integer channelid) {
		return packOptService.getPackOptDetails(planid, channelid);
	}

	@QueryMapping
	public FineLinePackOptimizationResponse getPackOptFinelineDetails(@Argument Long planId,@Argument Integer finelineNbr)
	{
		return packOptService.getPackOptFinelineDetails(planId,finelineNbr);

	}
}
