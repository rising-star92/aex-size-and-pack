package com.walmart.aex.sp.controller;
<<<<<<< HEAD

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.service.PackOptimizationService;


=======
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.service.IntegrationHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import com.walmart.aex.sp.service.PackOptimizationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
>>>>>>> ff019969582bfe1ace604db7db855fa14aada594
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

@Slf4j

@RestController

@Api(consumes = MediaType.APPLICATION_JSON_VALUE)

public class PackOptimizationController {
	private final PackOptimizationService packOptService;

<<<<<<< HEAD
	public PackOptimizationController(PackOptimizationService packOptService) {
		this.packOptService = packOptService;
	}
=======
	public static final String SUCCESS_STATUS = "Success";
	private static final String FAILURE_STATUS = "Failure";

	@Autowired
	private PackOptimizationService packOptService;
>>>>>>> ff019969582bfe1ace604db7db855fa14aada594

	private final IntegrationHubService integrationHubService;

	public PackOptimizationController(IntegrationHubService integrationHubService) {
		this.integrationHubService = integrationHubService;
	}

	@QueryMapping
	public PackOptimizationResponse getPackOptimizationValues(@Argument Long planid, @Argument Integer channelid) {
		return packOptService.getPackOptDetails(planid, channelid);
	}

<<<<<<< HEAD
	
	
	@GetMapping("/api/packOptimization/plan/{planId}/fineline/{finelineNbr}")
	public FineLinePackOptimizationResponse getPackOptFinelineDetails(@PathVariable Long planId, @PathVariable Integer finelineNbr) {
		return packOptService.getPackOptFinelineDetails(planId,finelineNbr);
	}
	
	
	
	
	
	
=======
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

	@PutMapping(path = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}")
	public UpdatePkOptResponse updatePackOptStatus(@PathVariable Long planId, @PathVariable Integer finelineNbr, @PathVariable Integer status) {
		UpdatePkOptResponse response = new UpdatePkOptResponse();
		if (status.equals(6) || status.equals(10)) {
			try {
				packOptService.UpdatePkOptServiceStatus(planId, finelineNbr, status);
				response.setStatus(SUCCESS_STATUS);
			} catch (Exception e) {
				response.setStatus(FAILURE_STATUS);
				log.error("Exception while updating status :", e);
			}
			return response;
		} else {
			response.setStatus(FAILURE_STATUS);
			return response;
		}
	}
>>>>>>> ff019969582bfe1ace604db7db855fa14aada594
}
