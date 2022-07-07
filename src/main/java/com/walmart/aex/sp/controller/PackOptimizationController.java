package com.walmart.aex.sp.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.aex.sp.dto.PackOptimizationResponse;
import com.walmart.aex.sp.entity.CcPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimization;
import com.walmart.aex.sp.entity.MerchantPackOptimizationID;
import com.walmart.aex.sp.entity.SubCatgPackOptimization;
import com.walmart.aex.sp.entity.fineLinePackOptimization;
import com.walmart.aex.sp.service.PackOptimizationService;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/size-and-pack/v1")
@RestController
@Api(consumes = MediaType.APPLICATION_JSON_VALUE)
public class PackOptimizationController {
	
	@Autowired
	private PackOptimizationService packOptService;

	@GetMapping("/getPackOptDetails/{planid}/{channelid}")
	public PackOptimizationResponse getPackOptimizationValues(@PathVariable Long planid, @PathVariable Integer channelid)
	{
		//MerchantPackOptimizationID MerchantPackOptimizationid= new MerchantPackOptimizationID();
		
		/*MerchantPackOptimizationid.setPlanId(planid);
		MerchantPackOptimizationid.setRepTLvl0(0);
		MerchantPackOptimizationid.setRepTLvl1(0);
		MerchantPackOptimizationid.setRepTLvl2(0);
		MerchantPackOptimizationid.setRepTLvl3(25);*/
		return packOptService.getPackOptDetails(planid, channelid);
	}
}
