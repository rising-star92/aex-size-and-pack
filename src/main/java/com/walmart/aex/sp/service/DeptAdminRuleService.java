package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;
import com.walmart.aex.sp.dto.deptadminrule.ReplItemResponse;

import java.util.List;

public interface DeptAdminRuleService {
    List<DeptAdminRuleResponse> getDeptAdminRules(List<Integer> deptNumbers);

    Integer getInitialThreshold(Long planId, Integer lvl1Nbr);

    Integer getReplenishmentThreshold(Long planId, Integer lvl1Nbr);

    void addAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests);
    void updateAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests);

    void deleteDeptAdminRules(List<Integer> deptNbrs);

    ReplItemResponse getRepelItemRule(Long planId, Integer lvl1Nbr);
}
