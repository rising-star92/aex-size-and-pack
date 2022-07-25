package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.buyQuantity.BuyQtyRequest;
import com.walmart.aex.sp.dto.buyQuantity.FetchFineLineResponse;
import com.walmart.aex.sp.service.SizeAndPackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j

public class BuyQntyController {
    @Autowired
    private SizeAndPackService sizeAndPackService;

    @QueryMapping
    public FetchFineLineResponse getFinelineBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest)
    {
        return sizeAndPackService.fetchFinelineBuyQnty(buyQtyRequest);
    }

    @QueryMapping
    public FetchFineLineResponse getCcBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest, @Argument Integer finelineNbr)
    {
        return sizeAndPackService.fetchCcBuyQnty(buyQtyRequest, finelineNbr);
    }
}
