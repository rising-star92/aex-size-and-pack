package com.walmart.aex.sp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkReplnRequest;
import com.walmart.aex.sp.dto.replenishment.UpdateVnPkWhPkResponse;
import com.walmart.aex.sp.service.ReplenishmentService;
import org.springframework.web.bind.annotation.RestController;


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
    public ReplenishmentResponse fetchReplnByPlanFinelineStyleCc(@Argument ReplenishmentRequest replenishmentRequest)
    {
        return replenishmentService.fetchSizeListReplenishment(replenishmentRequest);
    }
    
    @MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByCategory(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
       UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
       try
       {
          replenishmentService.updateVnpkWhpkForCatgReplnCons(updateVnPkWhPkReplnRequest);
       }
       catch(Exception e)
       {
          response.setStatus(FAILURE_STATUS);
       }

       response.setStatus(SUCCESS_STATUS);
       return response;
    }

    @MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigBySubCategory(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
       UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
       try
       {
          replenishmentService.updateVnpkWhpkForSubCatgReplnCons(updateVnPkWhPkReplnRequest);
       }
       catch(Exception e)
       {
          response.setStatus(FAILURE_STATUS);
       }

       response.setStatus(SUCCESS_STATUS);
       return response;
    }
    @MutationMapping
	public UpdateVnPkWhPkResponse updateReplnConfigByFineline(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
    	UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
		
		try
		{
			replenishmentService.updateVnpkWhpkForFinelineReplnCons(updateVnPkWhPkReplnRequest);
		}
		catch(Exception e)
		{
			response.setStatus(FAILURE_STATUS);
		}
		
		response.setStatus(SUCCESS_STATUS);
		
		return response;
	}

    
    @MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByStyle(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
    {
        UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
        
        try
        {
            replenishmentService.updateVnpkWhpkForStyleReplnCons(updateVnPkWhPkReplnRequest);
        }
        catch(Exception e)
        {
            response.setStatus(FAILURE_STATUS);
        }

        response.setStatus(SUCCESS_STATUS);
        
        return response;
    }
    
	@MutationMapping
	public UpdateVnPkWhPkResponse updateReplnConfigByCc(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest)
	{
		UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();
		
		try
		{
			replenishmentService.updateVnpkWhpkForCcReplnPkCons(updateVnPkWhPkReplnRequest);
		}
		catch(Exception e)
		{
			response.setStatus(FAILURE_STATUS);
		}
		
		response.setStatus(SUCCESS_STATUS);
		
		return response;
	}
	
	@MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByCcSpMerchMethod(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

		UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();

        try {
            replenishmentService.updateVnPkWhPkCcSpMerchMethodReplnCon(updateVnPkWhPkReplnRequest);
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
        }
        response.setStatus(SUCCESS_STATUS);
        return response;
    }
	
	@MutationMapping
    public UpdateVnPkWhPkResponse updateReplnConfigByCcSpSize(@Argument UpdateVnPkWhPkReplnRequest updateVnPkWhPkReplnRequest) {

		UpdateVnPkWhPkResponse response = new UpdateVnPkWhPkResponse();

        try {
            replenishmentService.updateVnPkWhPkCcSpSizeReplnCon(updateVnPkWhPkReplnRequest);
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
        }
        response.setStatus(SUCCESS_STATUS);
        return response;
    }

}


