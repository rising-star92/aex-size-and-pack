package com.walmart.aex.sp.dto.deptadminrule;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanAdminRuleRequest extends ReplItemResponse {
    private Long planId;
    private Integer deptNbr;
    private Date createTs;
    private String createUserId;
    private String lastModifiedUserId;

    public PlanAdminRuleRequest(Long planId, Integer deptNbr, Integer replItemPieceRule, Integer minReplItemUnits, String createUserId , String lastModifiedUserId ) {
        super(replItemPieceRule , minReplItemUnits);
        this.planId = planId;
        this.deptNbr = deptNbr;
        this.createUserId = createUserId;
        this.lastModifiedUserId = lastModifiedUserId;
    }
}
