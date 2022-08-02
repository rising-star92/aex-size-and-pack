package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.buyquantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyquantity.BuyQtyResponse;
import com.walmart.aex.sp.service.SizeAndPackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j

public class BuyQntyController {

    private final SizeAndPackService sizeAndPackService;

    public BuyQntyController(SizeAndPackService sizeAndPackService) {
        this.sizeAndPackService = sizeAndPackService;
    }

    @QueryMapping
    public BuyQtyResponse getFinelineBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest)
    {
        return sizeAndPackService.fetchFinelineBuyQnty(buyQtyRequest);
    }

    @QueryMapping
    public BuyQtyResponse getCcBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest, @Argument Integer finelineNbr)
    {
        return sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, finelineNbr);
    }

    @QueryMapping
    public BuyQtyResponse getSizeBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest)
    {
        return sizeAndPackService.fetchSizeBuyQnty(buyQtyRequest);
    }
}
