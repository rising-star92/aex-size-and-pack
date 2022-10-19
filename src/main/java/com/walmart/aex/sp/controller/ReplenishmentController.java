package com.walmart.aex.sp.controller;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkResponse;
import com.walmart.aex.sp.service.ReplenishmentService;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class ReplenishmentController {

	public static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";
    
    private final ReplenishmentService replenishmentService;

    public ReplenishmentController(ReplenishmentService replenishmentService) {
        this.replenishmentService = replenishmentService;
    }

    @QueryMapping
    public ReplenishmentResponse fetchReplnByPlan(@Argument ReplenishmentRequest replenishmentRequest)
    {
        return replenishmentService.fetchFinelineReplenishment(replenishmentRequest);
    }

    @QueryMapping
    public ReplenishmentResponse fetchReplnByPlanFineline(@Argument ReplenishmentRequest replenishmentRequest)
    {
        return replenishmentService.fetchCcReplenishment(replenishmentRequest);
    }
	@QueryMapping
	public ReplenishmentResponse fetchReplnFullHierarchyByPlanFineline(
			@Argument ReplenishmentRequest replenishmentRequest) {
		return replenishmentService.fetchSizeListReplenishmentFullHierarchy(replenishmentRequest);
	}
    @QueryMapping
    public ReplenishmentResponse fetchReplnByPlanFinelineStyleCc(@Argument ReplenishmentRequest replenishmentRequest)
    {
        return replenishmentService.fetchSizeListReplenishment(replenishmentRequest);
    }
    
    @MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByCategory(@Argument UpdateVnPkWhPkReplnRequest request)
    {
       UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
       try
       {
          replenishmentService.updateVnpkWhpkForCatgReplnCons(request);
          response.setStatus(SUCCESS_STATUS);
       }
       catch(Exception e)
       {
          response.setStatus(FAILURE_STATUS);
          log.error("Exception While updating Category Replenishment :", e);
       }
      
       return response;
    }

    @MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigBySubCategory(@Argument UpdateVnPkWhPkReplnRequest request)
    {
       UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
       try
       {
          replenishmentService.updateVnpkWhpkForSubCatgReplnCons(request);
          response.setStatus(SUCCESS_STATUS);
       }
       catch(Exception e)
       {
          response.setStatus(FAILURE_STATUS);
          log.error("Exception While updating Sub Category Replenishment :", e);
       }

       return response;
    }
    @MutationMapping
	public UpdateVnPkWhPkResponse updateReplnConfigByFineline(@Argument UpdateVnPkWhPkReplnRequest request)
	{
    	UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
		
		try
		{
			replenishmentService.updateVnpkWhpkForFinelineReplnCons(request);
			response.setStatus(SUCCESS_STATUS);
		}
		catch(Exception e)
		{
			response.setStatus(FAILURE_STATUS);
			log.error("Exception While updating Fineline Replenishment :", e);
		}
		
		return response;
	}

    
    @MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByStyle(@Argument UpdateVnPkWhPkReplnRequest request)
    {
        UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
        
        try
        {
            replenishmentService.updateVnpkWhpkForStyleReplnCons(request);
            response.setStatus(SUCCESS_STATUS);
        }
        catch(Exception e)
        {
            response.setStatus(FAILURE_STATUS);
            log.error("Exception While updating Style Replenishment :", e);
        }
   
        return response;
    }
    
	@MutationMapping
	public UpdateVnPkWhPkResponse updateReplnConfigByCc(@Argument UpdateVnPkWhPkReplnRequest request)
	{
		UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
		
		try
		{
			replenishmentService.updateVnpkWhpkForCcReplnPkCons(request);
			response.setStatus(SUCCESS_STATUS);
		}
		catch(Exception e)
		{
			response.setStatus(FAILURE_STATUS);
			log.error("Exception While updating Customer choice Replenishment :", e);
		}
			
		return response;
	}
	
	@MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByCcMerchMethod(@Argument UpdateVnPkWhPkReplnRequest request) {

		UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();

        try {
            replenishmentService.updateVnPkWhPkCcMerchMethodReplnCon(request);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
            log.error("Exception While updating Cc Merch method Replenishment :", e);
        }
        
        return response;
    }
	
	@MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByCcSpMmSize(@Argument UpdateVnPkWhPkReplnRequest request) {

		UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();

        try {
            replenishmentService.updateVnPkWhPkCcSpSizeReplnCon(request);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
            log.error("Exception While updating Cc Size Replenishment :", e);
        }
        
        return response;
    }

}


