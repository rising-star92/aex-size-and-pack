package com.walmart.aex.sp.dto.deptadminrule;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
public class PlanAdminRuleRequest extends ReplItemResponse {
    private Long planId;
    private Integer deptNbr;
    private Date createTs;
    private String createUserId;
}
