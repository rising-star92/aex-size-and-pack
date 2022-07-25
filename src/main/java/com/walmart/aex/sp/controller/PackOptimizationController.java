package com.walmart.aex.sp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.aex.sp.dto.PackOptimizationResponse;
import com.walmart.aex.sp.service.PackOptimizationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@RestController

@Api(consumes = MediaType.APPLICATION_JSON_VALUE)

public class PackOptimizationController {

	@Autowired
	private PackOptimizationService packOptService;

	@QueryMapping
	public PackOptimizationResponse getPackOptimizationValues(@Argument Long planid, @Argument Integer channelid) {
		return packOptService.getPackOptDetails(planid, channelid);
	}
}
