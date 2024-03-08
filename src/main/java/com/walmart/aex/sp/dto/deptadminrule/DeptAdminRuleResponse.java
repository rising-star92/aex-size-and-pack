package com.walmart.aex.sp.dto.deptadminrule;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Set;
@Data
@SuperBuilder
public class DeptAdminRuleResponse extends ReplItemResponse {
    private Integer deptNbr;
    private Set<PlanAdminRuleResponse> planAdminRuleResponses;
}
