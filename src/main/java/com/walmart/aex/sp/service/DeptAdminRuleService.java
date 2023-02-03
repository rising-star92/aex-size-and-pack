package com.walmart.aex.sp.service;

import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleRequest;
import com.walmart.aex.sp.dto.deptadminrule.DeptAdminRuleResponse;

import java.util.List;

public interface DeptAdminRuleService {
    List<DeptAdminRuleResponse> getDeptAdminRules(DeptAdminRuleRequest deptAdminRuleRequest);

    void addAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests);

    void deleteDeptAdminRules(List<DeptAdminRuleRequest> deptAdminRuleRequests);
}
