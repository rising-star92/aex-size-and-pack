package com.walmart.aex.sp.dto.deptadminrule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptAdminRuleRequest {
    private Integer deptNbr;
    private Integer replItemPieceRule;
    private Integer minReplItemUnits;
}
