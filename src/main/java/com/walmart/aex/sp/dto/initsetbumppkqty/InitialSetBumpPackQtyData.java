package com.walmart.aex.sp.dto.initsetbumppkqty;

import lombok.Data;

@Data
public class InitialSetBumpPackQtyData {
	private Long planId;
    private String planDesc;
    private Integer lvl0Nbr;
    private String lvl0Desc;
    private Integer lvl1Nbr;
    private String lvl1Desc;
    private Integer lvl2Nbr;
    private String lvl2Desc;
	private Integer lvl3Nbr;
	private String lvl3Desc;
	private Integer lvl4Nbr;
	private String lvl4Desc;
	private Integer finelineNbr;
	private String finelineDesc;
    private String finelineAltDesc;
	private String styleNbr;
	private String ccId;
	private String colorName;
	private Integer merchMethodCode;
	private String merchMethodDesc;
	private Integer ahsSizeId;
	private String sizeDesc;
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
}
