package com.walmart.aex.sp.controller;

import com.walmart.aex.sp.dto.StatusResponse;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.deptadminrule.ReplItemResponse;
import com.walmart.aex.sp.service.DeptAdminRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.walmart.aex.sp.util.SizeAndPackConstants.FAILED_STATUS;
import static com.walmart.aex.sp.util.SizeAndPackConstants.REQUEST_INVALID;
import static com.walmart.aex.sp.util.SizeAndPackConstants.SUCCESS_STATUS;

@RestController
@Slf4j
public class DeptAdminRulesController {

    private final DeptAdminRuleService deptAdminRuleService;

    public DeptAdminRulesController(DeptAdminRuleService deptAdminRuleService) {
        this.deptAdminRuleService = deptAdminRuleService;
    }

    @QueryMapping
    public List<DeptAdminRuleResponse> getDeptAdminRules(@Argument List<Integer> deptNumbers) {
        return deptAdminRuleService.getDeptAdminRules(deptNumbers);
    }

    @QueryMapping
    public ReplItemResponse getRepelItemRules(@Argument Long planId, @Argument Integer lvl1Nbr) {
        return deptAdminRuleService.getRepelItemRule(planId, lvl1Nbr);
    }

    @MutationMapping
    public StatusResponse addDeptAdminRules(@Argument List<DeptAdminRuleRequest> deptAdminRuleRequests) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            deptAdminRuleService.addAdminRules(deptAdminRuleRequests);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            statusResponse.setStatus(FAILED_STATUS);
        }
        return statusResponse;
    }

    @MutationMapping
    public StatusResponse updateDeptAdminRules(@Argument List<DeptAdminRuleRequest> deptAdminRuleRequests) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            deptAdminRuleService.updateAdminRules(deptAdminRuleRequests);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            statusResponse.setStatus(FAILED_STATUS);
        }
        return statusResponse;
    }

    @MutationMapping
    public StatusResponse deleteDeptAdminRules(@Argument List<Integer> deptNbrs) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(REQUEST_INVALID);
        try {
            deptAdminRuleService.deleteDeptAdminRules(deptNbrs);
            statusResponse.setStatus(SUCCESS_STATUS);
        } catch (Exception e) {
            statusResponse.setStatus(FAILED_STATUS);
        }
        return statusResponse;
    }

}
