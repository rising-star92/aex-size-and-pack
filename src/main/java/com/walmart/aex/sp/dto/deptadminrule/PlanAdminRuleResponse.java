package com.walmart.aex.sp.dto.deptadminrule;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlanAdminRuleResponse {
    private Long planId;
    private Integer deptNbr;
    private Integer replItemPieceRule;
    private Integer minReplItemUnits;

}
