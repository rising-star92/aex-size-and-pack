package com.walmart.aex.sp.dto;

import lombok.Data;

@Data
public class MetricsDto {
    private Integer sizeProfilePct;
    private Integer adjSizeProfilePct;
    private Integer avgSizeProfilePct;
    private Integer adjAvgSizeProfilePct;
    private Integer buyQty;
    private Integer finalBuyQty;
    private Integer finalInitialSetQty;
    private Integer finalReplenishmentQty;
}
