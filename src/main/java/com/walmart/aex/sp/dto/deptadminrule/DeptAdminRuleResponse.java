package com.walmart.aex.sp.dto.deptadminrule;

import lombok.Data;

import java.util.Set;
@Data
public class DeptAdminRuleResponse {
    private Integer deptNbr;
    private Integer replItemPieceRule;
    private Integer minReplItemUnits;
    private Set<PlanAdminRuleResponse> planAdminRuleResponses;
}
