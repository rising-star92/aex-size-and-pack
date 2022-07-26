package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

@Data
public class MetricsDto {
    private Double sizeProfilePct;
    private Double adjSizeProfilePct;
    private Double avgSizeProfilePct;
    private Double adjAvgSizeProfilePct;
    private Integer buyQty;
    private Integer finalBuyQty;
    private Integer finalInitialSetQty;
    private Integer finalReplenishmentQty;
    private Integer vendorPack;
    private Integer warehousePack;
    private Integer packRatio;
    private Integer replenishmentPacks;

}
