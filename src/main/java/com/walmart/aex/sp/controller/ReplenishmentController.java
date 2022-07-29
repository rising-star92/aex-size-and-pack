package com.walmart.aex.sp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentRequest;
import com.walmart.aex.sp.dto.replenishment.ReplenishmentResponse;
import com.walmart.aex.sp.service.ReplenishmentService;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
public class ReplenishmentController {


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

}


