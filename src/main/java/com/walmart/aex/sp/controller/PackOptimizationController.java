package com.walmart.aex.sp.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import com.walmart.aex.sp.dto.packoptimization.Execution;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;
import com.walmart.aex.sp.service.IntegrationHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.service.PackOptimizationService;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.service.PackOptimizationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

@Slf4j

@RestController

@Api(consumes = MediaType.APPLICATION_JSON_VALUE)

public class PackOptimizationController {
	private final PackOptimizationService packOptService;

	public PackOptimizationController(PackOptimizationService packOptService) {
		this.packOptService = packOptService;
	}

	private final IntegrationHubService integrationHubService;

	public PackOptimizationController(IntegrationHubService integrationHubService) {
		this.integrationHubService = integrationHubService;
	}

	@QueryMapping
	public PackOptimizationResponse getPackOptimizationValues(@Argument Long planid, @Argument Integer channelid) {
		return packOptService.getPackOptDetails(planid, channelid);
	}
	
	
	@GetMapping("/api/packOptimization/plan/{planId}/fineline/{finelineNbr}")
	public FineLinePackOptimizationResponse getPackOptFinelineDetails(@PathVariable Long planId, @PathVariable Integer finelineNbr) {
		return packOptService.getPackOptFinelineDetails(planId,finelineNbr);
	}
	

	@MutationMapping
	public RunPackOptResponse createRunPackOptExecution(@Argument RunPackOptRequest request)
	{
		RunPackOptResponse response= new RunPackOptResponse();
		response = integrationHubService.callIntegrationHubForPackOpt(request);
		if(response!=null)
		{
			return response;
		}
		else {
			BigInteger bigInteger = BigInteger.ONE;
			response = new RunPackOptResponse(new Execution(bigInteger, 0, "NOT SENT TO ANALYTICS", "Error connecting with Integration Hub service"));
			return response;
		}
	}
}
