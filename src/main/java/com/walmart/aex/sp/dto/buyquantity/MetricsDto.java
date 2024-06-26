package com.walmart.aex.sp.dto.buyquantity;

import lombok.Data;

import java.util.List;

@Data
public class MetricsDto {
    private Double sizeProfilePct;
    private Double adjSizeProfilePct;
    private Double avgSizeProfilePct;
    private Double adjAvgSizeProfilePct;
    private Integer buyQty;
    private Integer bumpPackQty;
    private Integer finalBuyQty;
    private Integer finalInitialSetQty;
    private Integer finalReplenishmentQty;
    private Integer vendorPack;
    private Integer warehousePack;
    private Double packRatio;
    private Integer replenishmentPacks;
    private List<FactoryDTO> factories;
    private Integer onlineBuyQty;
}
