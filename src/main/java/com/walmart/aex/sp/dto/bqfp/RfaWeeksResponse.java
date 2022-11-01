package com.walmart.aex.sp.dto.bqfp;

import lombok.Data;

@Data
public class RfaWeeksResponse {
    private Long planId;
    private Integer lvl0Nbr;
    private Integer lvl1Nbr;
    private Integer lvl2Nbr;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer finelineNbr;
    private WeeksDTO inStoreWeek;
    private WeeksDTO markDownWeek;
}
