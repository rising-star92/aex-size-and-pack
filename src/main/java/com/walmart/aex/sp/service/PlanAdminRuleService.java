package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.PlanAdminRuleResponse;

import java.util.List;
import java.util.Set;

public interface PlanAdminRuleService {
    Set<PlanAdminRuleResponse> getPlanAdminRules(List<Long> planIds);
    void addPlanAdminRules(List<PlanAdminRuleRequest> planAdminRuleRequests);
    void updatePlanAdminRules(List<PlanAdminRuleRequest> plaAdminRuleRequests);
    void deletePlanAdminRule(List<Long> planId);
}
