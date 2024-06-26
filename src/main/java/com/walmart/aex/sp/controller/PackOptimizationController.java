package com.walmart.aex.sp.controller;


import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.packoptimization.*;
import com.walmart.aex.sp.dto.packoptimization.isbpqty.ISAndBPQtyDTO;
import com.walmart.aex.sp.enums.Action;
import com.walmart.aex.sp.enums.RunStatusCodeType;
import com.walmart.aex.sp.service.IntegrationHubService;
import com.walmart.aex.sp.service.PackOptimizationService;
import com.walmart.aex.sp.service.PostPackOptimizationService;
import com.walmart.aex.sp.service.UpdateFromQuoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Consumes;
import java.math.BigInteger;
import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.FAILED_STATUS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.INCORRECT_ACTION_MSG;
import static com.walmart.aex.sp.util.SizeAndPackConstants.NO_ACTION_MSG;

@Slf4j
@RestController
@Consumes(MediaType.APPLICATION_JSON_VALUE)
public class PackOptimizationController {
    private final PackOptimizationService packOptService;

    private final PostPackOptimizationService postPackOptimizationService;

    private final IntegrationHubService integrationHubService;

    private final UpdateFromQuoteService updateFromQuoteService;

    private static final Integer DEFAULT_BUMPPACK = 1;

    public PackOptimizationController(PackOptimizationService packOptService, IntegrationHubService integrationHubService, PostPackOptimizationService postPackOptimizationService, UpdateFromQuoteService updateFromQuoteService) {

        this.packOptService = packOptService;
        this.integrationHubService = integrationHubService;
        this.postPackOptimizationService = postPackOptimizationService;
        this.updateFromQuoteService = updateFromQuoteService;
    }

    public static final String SUCCESS_STATUS = "Success";
    private static final String FAILURE_STATUS = "Failure";

    @QueryMapping
    public PackOptimizationResponse getPackOptimizationValues(@Argument Long planid, @Argument Integer channelid) {
        return packOptService.getPackOptDetails(planid, channelid);
    }

    @GetMapping("/api/packOptimization/plan/{planId}/fineline/{finelineNbr}")
    public FineLinePackOptimizationResponse getPackOptFinelineDetails(@PathVariable Long planId, @PathVariable Integer finelineNbr) {
        return packOptService.getPackOptFinelineDetails(planId, finelineNbr, DEFAULT_BUMPPACK);
    }

    @GetMapping("/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/bumppack/{bumpPackNbr}")
    public FineLinePackOptimizationResponse getPackOptFinelineDetails(@PathVariable Long planId, @PathVariable Integer finelineNbr, @PathVariable Integer bumpPackNbr) {
        log.info("Pack Optimization execution for BumpPackNbr: {}", bumpPackNbr);
        return packOptService.getPackOptFinelineDetails(planId, finelineNbr, bumpPackNbr);
    }


    @MutationMapping
    public RunPackOptResponse createRunPackOptExecution(@Argument RunPackOptRequest request) {
        RunPackOptResponse response = packOptService.callIntegrationHubForPackOptByFineline(request);
        if (response != null) {
            return response;
        } else {
            BigInteger bigInteger = BigInteger.ONE;
            response = new RunPackOptResponse(new Execution(bigInteger, 0, "NOT SENT TO ANALYTICS", "Error connecting with Integration Hub service"));
            return response;
        }
    }

    @PutMapping(path = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}/status/{status}")
    public UpdatePkOptResponse updatePackOptStatus(@PathVariable Long planId, @PathVariable String finelineNbr, @PathVariable Integer status, @RequestParam(value = "isResetPackOptStatusFlag", required = false, defaultValue = "false") boolean isResetPackOptStatusFlag, @RequestBody(required = false) UpdatePackOptStatusRequest request ) {
        UpdatePkOptResponse response = new UpdatePkOptResponse();
        Integer statusToUpdate = (RunStatusCodeType.ANALYTICS_RUN_COMPLETED.getId().equals(status) || RunStatusCodeType.ANALYTICS_ERRORS_LIST.contains(status)) ? status: RunStatusCodeType.COMMON_ERR_MSG.getId();
        try {
            packOptService.updatePackOptServiceStatus(planId, finelineNbr, statusToUpdate, isResetPackOptStatusFlag, request);
            response.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
            log.error("Exception while updating status for planId : {} , finelineNbr : {} with status : {} : ", planId, finelineNbr, status, e);
        }
        return response;

    }

    @PostMapping(path = "/api/packOptimization/plan/{planId}/fineline/{finelineNbr}")
    public ResponseEntity<String> postInitialSetAndBumpPackQty(@PathVariable Long planId, @PathVariable Integer finelineNbr, @RequestBody ISAndBPQtyDTO isAndBPQtyDTO) {
        try {
            postPackOptimizationService.updateInitialSetAndBumpPackAty(planId, finelineNbr, isAndBPQtyDTO);
        } catch (Exception e) {
            log.error("Error Occurred while updating values for Initial Set and Bump Pack ", e);
            return ResponseEntity.internalServerError().build();

        }
        return ResponseEntity.ok(SUCCESS_STATUS);
    }

    @MutationMapping
    public StatusResponse updatePackOptConstraints(@Argument UpdatePackOptConstraintRequestDTO request) {
        return packOptService.updatePackOptConstraints(request);
    }

    @QueryMapping
    public PackOptimizationResponse fetchPackOptConstraintsByFineline(@Argument PackOptConstraintRequest request) {
        return packOptService.getPackOptConstraintDetails(request);
    }

    @MutationMapping
    public StatusResponse updateColorCombination(@Argument ColorCombinationRequest request) {
        StatusResponse response = new StatusResponse();
        if (request.getAction() != null) {
            String action = Action.getEnumValue(request.getAction());
            if (Action.ADD.getDescription().equals(action))
                response = packOptService.addColorCombination(request);
            else if (Action.DELETE.getDescription().equals(action))
                response = packOptService.deleteColorCombination(request);
            else {
                response.setMessage(INCORRECT_ACTION_MSG);
                response.setStatus(FAILED_STATUS);
            }
        } else {
            response.setMessage(NO_ACTION_MSG);
            response.setStatus(FAILURE_STATUS);
        }
        return response;
    }

    @MutationMapping
    public StatusResponse updateFromQuote(@Argument RunPackOptRequest request) {
        return updateFromQuoteService.updateFactoryFromApproveQuotes(request);
    }

    @QueryMapping
    public List<PackOptFinelinesByStatusResponse> fetchPackOptFinelinesByStatus(@Argument List<Integer> statusCodes) {
        return packOptService.getPackOptFinelinesByStatus(statusCodes);
    }
}

