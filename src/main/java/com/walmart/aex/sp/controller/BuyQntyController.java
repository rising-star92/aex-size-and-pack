package com.walmart.aex.sp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.aex.sp.dto.assortproduct.ColorDefinition;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackData;
import com.walmart.aex.sp.dto.assortproduct.RFASizePackRequest;
import com.walmart.aex.sp.dto.buyquantity.*;
import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.enums.ChannelType;
import com.walmart.aex.sp.service.*;
import com.walmart.aex.sp.util.SizeAndPackConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@Slf4j

public class BuyQntyController {

    private final SizeAndPackService sizeAndPackService;
    private final CalculateBuyQuantityService calculateBuyQuantityService;
    private final ReplenishmentService replenishmentService;
    private final InitialSetBumpPackQtyService initSetBpPkQtyService;
    private final BigQueryClusterService bigQueryPostProcessingService;

	public BuyQntyController(SizeAndPackService sizeAndPackService,
                             CalculateBuyQuantityService calculateBuyQuantityService, ReplenishmentService replenishmentService,
                             InitialSetBumpPackQtyService initSetBpPkQtyService, BigQueryClusterService bigQueryPostProcessingService) {
		this.sizeAndPackService = sizeAndPackService;
		this.calculateBuyQuantityService = calculateBuyQuantityService;
		this.replenishmentService = replenishmentService;
		this.initSetBpPkQtyService = initSetBpPkQtyService;
        this.bigQueryPostProcessingService = bigQueryPostProcessingService;
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
    public BuyQtyResponse getCcBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest, @Argument Integer finelineNbr) {
        BuyQtyResponse response;
        if (buyQtyRequest.getChannel() != null && buyQtyRequest.getChannel().equalsIgnoreCase(ChannelType.ONLINE.name())) {
            response = replenishmentService.fetchOnlineCcBuyQnty(buyQtyRequest, finelineNbr);
        } else {
            response = sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, finelineNbr);
        }

        if (featureFlagService.isEnabled("enable_ecom_sp")) {
            Integer onlineReceiptQuantity = someService.getOnlineReceiptQuantity();
            response.setOnlineReceiptQuantity(onlineReceiptQuantity);
        }

        return response;
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
        StatusResponse statusResponse = new StatusResponse();
        try {
            statusResponse.setStatuses(calculateBuyQuantityService.calculateBuyQuantity(calculateBuyQtyRequest));
            statusResponse.setStatus(SizeAndPackConstants.SUCCESS_STATUS);
            return statusResponse;
        } catch (Exception e) {
            statusResponse.setStatus(SizeAndPackConstants.ERROR_STATUS);
            statusResponse.setStatuses(List.of(new StatusResponse(SizeAndPackConstants.ERROR_STATUS, null)));
            return statusResponse;
        }
    }
    
	@QueryMapping
	public BuyQtyResponse fetchInitialSetBumpPackByPlanFineline(@Argument BuyQtyRequest request) {
		return initSetBpPkQtyService.getInitSetBpPkByPlanFineline(request);
	}

    @PostMapping("/size-and-pack/v1/CalBuyQtyBigquery")
    public List<RFASizePackData> getCalBuyQtyQueryResponse (@RequestBody BuyQtyQueryRequest request) throws InterruptedException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<ColorDefinition> colorDefinition = mapper.readValue(request.getColors(),new TypeReference<List<ColorDefinition>>(){});
        return bigQueryPostProcessingService.fetchRFASizePackData(
                RFASizePackRequest.builder()
                        .fineline_nbr(request.getFinelineNbr())
                        .fiscal_year(request.getFiscalYear())
                        .plan_id(request.getPlanId())
                        .rpt_lvl_0_nbr(request.getLvl0())
                        .rpt_lvl_1_nbr(request.getLvl1())
                        .rpt_lvl_2_nbr(request.getLvl2())
                        .rpt_lvl_3_nbr(request.getLvl3())
                        .rpt_lvl_4_nbr(request.getLvl4())
                        .seasonCode(request.getSeasonCode())
                        .colors(colorDefinition)
                        .like_fineline_nbr(request.getLikeFinelineNbr())
                        .like_lvl1_nbr(request.getLikeLvl1())
                        .like_lvl3_nbr(request.getLikeLvl3())
                        .like_lvl4_nbr(request.getLikeLvl4())
                        .build(), request.getVolDeviation()
        );
    }
}
