package com.walmart.aex.sp.controller;


import java.util.List;

import javax.ws.rs.Consumes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.walmart.aex.sp.dto.commitmentreport.InitialBumpSetResponse;
import com.walmart.aex.sp.dto.commitmentreport.InitialSetPackRequest;
import com.walmart.aex.sp.dto.cr.storepacks.PackDetailsVolumeResponse;
import com.walmart.aex.sp.dto.isVolume.InitialSetVolumeRequest;
import com.walmart.aex.sp.dto.isVolume.InitialSetVolumeResponse;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDTO;
import com.walmart.aex.sp.dto.planhierarchy.PlanSizeAndPackDeleteDTO;
import com.walmart.aex.sp.dto.planhierarchy.SizeAndPackResponse;
import com.walmart.aex.sp.dto.storedistribution.PackInfoRequest;
import com.walmart.aex.sp.dto.storedistribution.StoreDistributionResponse;
import com.walmart.aex.sp.service.SizeAndPackService;
import com.walmart.aex.sp.service.StoreDistributionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/size-and-pack/v1")
@Controller
@Consumes(MediaType.APPLICATION_JSON_VALUE)
public class SizeAndPackController {

	@Autowired
	SizeAndPackService sizeAndPackService;

	private final StoreDistributionService storeDistributionService;

	public SizeAndPackController(StoreDistributionService storeDistributionService) {
		this.storeDistributionService = storeDistributionService;
	}


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello");
    }

    @PostMapping(path = "/sizeAndPackService", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> createLinePlan(@RequestBody PlanSizeAndPackDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.saveSizeAndPackData(request));
        } catch (Exception exp) {
            log.error("Exception occurred when creating a line plan: {}", exp.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @PutMapping(path = "/sizeAndPackService", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> updateLinePlan(@RequestBody PlanSizeAndPackDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.updateSizeAndPackData(request));
        } catch (Exception exp) {
            log.error("Exception occurred when updating a line plan : {}", exp.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }

    @DeleteMapping(path = "/sizeAndPackService", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    ResponseEntity<SizeAndPackResponse> deleteLinePlan(@RequestBody PlanSizeAndPackDeleteDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(sizeAndPackService.deleteSizeAndPackData(request));
        } catch (Exception exp) {
            log.error("Exception occurred when updating a line plan : {}", exp.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }	

    @QueryMapping
    public InitialBumpSetResponse getInitialAndBumpSetDetails(@Argument InitialSetPackRequest request) {
        return sizeAndPackService.getInitialAndBumpSetDetails(request);
    }

    @QueryMapping
    public List<InitialSetVolumeResponse> getInitialAndBumpSetDetailsByVolumeCluster(@Argument InitialSetVolumeRequest request) {
        return sizeAndPackService.getInitialAndBumpSetDetailsByVolumeCluster(request);
    }
    
    @QueryMapping
	public StoreDistributionResponse getStoreDistributionByPlan(@Argument PackInfoRequest request) {
		return storeDistributionService.fetchStoreDistributionResponse(request);
	}
    
    @QueryMapping
    public List<PackDetailsVolumeResponse> getPackStoreDetailsByVolumeCluster(@Argument InitialSetVolumeRequest request)
    {
    	return sizeAndPackService.getPackStoreDetailsByVolumeCluster(request);
    }
}




