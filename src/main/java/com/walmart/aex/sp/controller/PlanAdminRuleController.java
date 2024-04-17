package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;
import com.walmart.aex.sp.service.PlanAdminRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

import static com.walmart.aex.sp.util.SizeAndPackConstants.*;

@RestController
@Slf4j
public class PlanAdminRuleController {
    private final PlanAdminRuleService planAdminRuleService;

    public PlanAdminRuleController(PlanAdminRuleService planAdminRuleService) {
        this.planAdminRuleService = planAdminRuleService;
    }

    @QueryMapping
    public Set<PlanAdminRuleResponse> getPlanAdminRules(@Argument List<Long> planIds) {
        return planAdminRuleService.getPlanAdminRules(planIds);
    }

    @MutationMapping
    public StatusResponse addPlanAdminRules(@Argument List<PlanAdminRuleRequest> planAdminRuleRequests) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            planAdminRuleService.addPlanAdminRules(planAdminRuleRequests);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            statusResponse.setStatus(FAILED_STATUS);
        }
        return statusResponse;
    }

    @MutationMapping
    public StatusResponse updatePlanAdminRules(@Argument List<PlanAdminRuleRequest> planAdminRuleRequests) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            planAdminRuleService.updatePlanAdminRules(planAdminRuleRequests);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            statusResponse.setStatus(FAILED_STATUS);
        }
        return statusResponse;
    }

    @MutationMapping
    public StatusResponse deletePlanAdminRules(@Argument List<Long> planIds) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            planAdminRuleService.deletePlanAdminRule(planIds);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            statusResponse.setStatus(FAILED_STATUS);
        }
        return statusResponse;
    }
}
