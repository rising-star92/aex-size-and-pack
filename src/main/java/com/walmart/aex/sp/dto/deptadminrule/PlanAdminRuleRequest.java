package com.walmart.aex.sp.dto.deptadminrule;

import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
public class PlanAdminRuleRequest extends ReplItemResponse {
    private Long planId;
    private Integer deptNbr;
}
