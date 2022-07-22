package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.BuyQtyRequest;
import com.walmart.aex.sp.dto.FetchFineLineResponse;
import com.walmart.aex.sp.service.FineLineBuyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j

public class FineLineBQController {
    @Autowired
    private FineLineBuyService fineLineBuyService;

    @QueryMapping
    public FetchFineLineResponse getBuyQtyDetails(@Argument BuyQtyRequest buyQtyRequest)
    {
        return fineLineBuyService.getFineLineResponse(buyQtyRequest);
    }
}
