package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class RfaWeekRequest {
    private Long planId;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
}
