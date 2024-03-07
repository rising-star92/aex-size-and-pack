package com.walmart.aex.sp.dto.deptadminrule;

import lombok.Data;

@Data
public class PlanAdminRuleResponse {
    private Long planId;
    private Integer deptNbr;
    private Integer replItemPieceRule;
    private Integer minReplItemUnits;

}
