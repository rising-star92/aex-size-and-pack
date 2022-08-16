package com.walmart.aex.sp.dto.replenishment;

import lombok.Data;

@Data
public class UpdateVnPkWhPkReplnRequest {

    private Long planId;
    private String planDesc;
    private String channel;
    private Integer fixtureTypeRollupId;
    private String fixtureTypeRollupName;
    private Integer lvl3Nbr;
    private Integer lvl4Nbr;
    private Integer fineline;
    private String style;
    private String customerChoice;
    private String merchMethodDesc;
    private Integer ahsSizeId;
    private Integer vnpk;
    private Integer whpk;
    private Integer repleshUnits;
}
