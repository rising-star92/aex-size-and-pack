package com.walmart.aex.sp.controller;


import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.service.PostPackOptimizationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.walmart.aex.sp.dto.packoptimization.FineLinePackOptimizationResponse;
import com.walmart.aex.sp.dto.packoptimization.PackOptimizationResponse;
import com.walmart.aex.sp.service.PackOptimizationService;
import com.walmart.aex.sp.dto.packoptimization.Execution;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptRequest;
import com.walmart.aex.sp.dto.packoptimization.RunPackOptResponse;

import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.service.IntegrationHubService;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

@Slf4j

@RestController

@Api(consumes = MediaType.APPLICATION_JSON_VALUE)

public class PackOptimizationController {
	private final PackOptimizationService packOptService;

	private final PostPackOptimizationService postPackOptimizationService;

	private final IntegrationHubService integrationHubService;

	public PackOptimizationController(PackOptimizationService packOptService,IntegrationHubService integrationHubService,PostPackOptimizationService postPackOptimizationService) {

		this.packOptService = packOptService;
    	this.integrationHubService = integrationHubService;
		this.postPackOptimizationService = postPackOptimizationService;
	}

	public static final String SUCCESS_STATUS = "Success";
	private static final String FAILURE_STATUS = "Failure";

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
		RunPackOptResponse response;
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

	@PostMapping(path = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}")
	public ResponseEntity<String> postInitialSetAndBumpPackQty(@PathVariable Long planId, @PathVariable Integer finelineNbr, @RequestBody ISAndBPQtyDTO isAndBPQtyDTO) {
		try{
			postPackOptimizationService.updateInitialSetAndBumpPackAty(planId,finelineNbr,isAndBPQtyDTO);
		} catch (Exception e){
			log.error("Error Occurred while updating values for Initial Set and Bump Pack ", e);
			return ResponseEntity.internalServerError().build();

		}
		return ResponseEntity.ok(SUCCESS_STATUS);
	}

	@MutationMapping
	public StatusResponse updatePackOptConstraints(@Argument UpdatePackOptConstraintRequestDTO request) {
		return packOptService.updatePackOptConstraints(request);
	}

	@QueryMapping
	public PackOptimizationResponse fetchPackOptConstraintsByFineline(@Argument PackOptConstraintRequest request)
	{
		return packOptService.getPackOptConstraintDetails(request);
	}


}

