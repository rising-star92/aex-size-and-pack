package com.walmart.aex.sp.dto.deptadminrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanAdminRuleRequest extends ReplItemResponse {
    private Long planId;
    private Integer deptNbr;
    private Date createTs;
    private String createUserId;

    public PlanAdminRuleRequest(Long planId, Integer deptNbr, Integer replItemPieceRule, Integer minReplItemUnits) {
        super(minReplItemUnits, replItemPieceRule);
        this.planId = planId;
        this.deptNbr = deptNbr;
    }
}
