package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

@Data
public class PackOptConstraintRequest {
    private Long planId;
    private String channel;
    private Integer finelineNbr;
}
