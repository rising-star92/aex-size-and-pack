package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.dto.buyquantity.CalculateBuyQtyRequest;
import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.service.CalculateBuyQuantityService;
import com.walmart.aex.sp.service.InitialSetBumpPackQtyService;
import com.walmart.aex.sp.service.ReplenishmentService;
import com.walmart.aex.sp.service.SizeAndPackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j

public class BuyQntyController {

    public static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";

    private final SizeAndPackService sizeAndPackService;
    private final CalculateBuyQuantityService calculateBuyQuantityService;
    private final ReplenishmentService replenishmentService;
    private final InitialSetBumpPackQtyService initSetBpPkQtyService;

	public BuyQntyController(SizeAndPackService sizeAndPackService,
			CalculateBuyQuantityService calculateBuyQuantityService, ReplenishmentService replenishmentService,
			InitialSetBumpPackQtyService initSetBpPkQtyService) {
		this.sizeAndPackService = sizeAndPackService;
		this.calculateBuyQuantityService = calculateBuyQuantityService;
		this.replenishmentService = replenishmentService;
		this.initSetBpPkQtyService = initSetBpPkQtyService;
	}

    @QueryMapping
    public BuyQtyResponse getFinelineBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest)
    {
        if (buyQtyRequest.getChannel() != null && buyQtyRequest.getChannel().equalsIgnoreCase(ChannelType.ONLINE.name())) {
            return replenishmentService.fetchOnlineFinelineBuyQnty(buyQtyRequest);
        }
        else return sizeAndPackService.fetchFinelineBuyQnty(buyQtyRequest);
    }

    @QueryMapping
    public BuyQtyResponse getCcBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest, @Argument Integer finelineNbr)
    {
        if (buyQtyRequest.getChannel() != null && buyQtyRequest.getChannel().equalsIgnoreCase(ChannelType.ONLINE.name())) {
            return replenishmentService.fetchOnlineCcBuyQnty(buyQtyRequest, finelineNbr);
        }
        else return sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, finelineNbr);
    }

    @QueryMapping
    public BuyQtyResponse getSizeBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest)
    {
        if (buyQtyRequest.getChannel() != null && buyQtyRequest.getChannel().equalsIgnoreCase(ChannelType.ONLINE.name())) {
            return replenishmentService.fetchOnlineSizeBuyQnty(buyQtyRequest);
        }
        else return sizeAndPackService.fetchSizeBuyQnty(buyQtyRequest);
    }

    @MutationMapping
    public StatusResponse calculateBuyQty(@Argument CalculateBuyQtyRequest calculateBuyQtyRequest) {

        StatusResponse response = new StatusResponse();

        try {
            calculateBuyQuantityService.calculateBuyQuantity(calculateBuyQtyRequest);
            response.setStatus(SUCCESS_STATUS);
            return response;
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
        }
        response.setStatus(SUCCESS_STATUS);
        return response;
    }
    
	@QueryMapping
	public BuyQtyResponse fetchInitialSetBumpPackByPlanFineline(@Argument BuyQtyRequest request) {
		return initSetBpPkQtyService.getInitSetBpPkByPlanFineline(request);
	}
}
