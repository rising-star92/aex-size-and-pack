package com.walmart.aex.sp.dto.replenishment;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReplenishmentResponseDTO {

    private Long planId;
    private Integer channelId;
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
    private Integer finelineFinalBuyUnits;
    private Integer finelineReplQty;
    private Integer finelineVenderPackCount;
    private Integer finelineWhsePackCount;
    private Double finelineVnpkWhpkRatio;
    private Integer finelineReplPack;
    private String styleNbr;
    private Integer styleFinalBuyUnits;
    private Integer styleReplQty;
    private Integer styleVenderPackCount;
    private Integer styleWhsePackCount;
    private Double styleVnpkWhpkRatio;
    private Integer styleReplPack;
    private String ccId;
    private String colorName;
    private Integer ccFinalBuyUnits;
    private Integer ccReplQty;
    private Integer ccVenderPackCount;
    private Integer ccWhsePackCount;
    private Double ccVnpkWhpkRatio;
    private Integer ccReplPack;
    private String merchMethod;
    private Integer ahsSizeId;
    private String sizeDesc;
    private Integer ccSpFinalBuyUnits;
    private Integer ccSpReplQty;
    private Integer ccSpVenderPackCount;
    private Integer ccSpWhsePackCount;
    private Double ccSpVnpkWhpkRatio;
    private Integer ccSpReplPack;

    public ReplenishmentResponseDTO(Long planId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr, String lvl1Desc,
                                    Integer lvl2Nbr, String lvl2Desc, Integer lvl3Nbr, String lvl3Desc, Integer lvl4Nbr,
                                    String lvl4Desc, Integer finelineNbr, String finelineDesc, String finelineAltDesc ,
                                    Integer finelineVenderPackCount,Integer finelineWhsePackCount,
                                    Double finelineVnpkWhpkRatio,
                                    Integer finelineReplPack ,Integer finelineFinalBuyUnits,Integer finelineReplQty) {
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl0Desc = lvl0Desc;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl1Desc = lvl1Desc;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl2Desc = lvl2Desc;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Desc = lvl3Desc;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Desc = lvl4Desc;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.finelineAltDesc = finelineAltDesc;
        this.finelineVenderPackCount=finelineVenderPackCount;
        this.finelineWhsePackCount=finelineWhsePackCount;
        this.finelineVnpkWhpkRatio=finelineVnpkWhpkRatio;
        this.finelineReplPack=finelineReplPack;
        this.finelineFinalBuyUnits=finelineFinalBuyUnits;
        this.finelineReplQty=finelineReplQty;
    }

    public ReplenishmentResponseDTO(Long planId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr, String lvl1Desc,
                                    Integer lvl2Nbr, String lvl2Desc, Integer lvl3Nbr, String lvl3Desc, Integer lvl4Nbr,
                                    String lvl4Desc, Integer finelineNbr, String finelineDesc, String finelineAltDesc  , String styleNbr,
                                    Integer styleFinalBuyUnits, Integer styleReplQty, Integer styleVenderPackCount,
                                    Integer styleWhsePackCount, Double styleVnpkWhpkRatio, Integer styleReplPack,
                                    String ccId,String colorName, Integer ccFinalBuyUnits, Integer ccReplQty, Integer ccVenderPackCount,
                                    Integer ccWhsePackCount, Double ccVnpkWhpkRatio, Integer ccReplPack) {
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl0Desc = lvl0Desc;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl1Desc = lvl1Desc;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl2Desc = lvl2Desc;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Desc = lvl3Desc;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Desc = lvl4Desc;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.finelineAltDesc=finelineAltDesc;
        this.styleNbr = styleNbr;
        this.styleFinalBuyUnits = styleFinalBuyUnits;
        this.styleReplQty = styleReplQty;
        this.styleVenderPackCount = styleVenderPackCount;
        this.styleWhsePackCount = styleWhsePackCount;
        this.styleVnpkWhpkRatio = styleVnpkWhpkRatio;
        this.styleReplPack = styleReplPack;
        this.ccId = ccId;
        this.colorName = colorName;
        this.ccFinalBuyUnits = ccFinalBuyUnits;
        this.ccReplQty = ccReplQty;
        this.ccVenderPackCount = ccVenderPackCount;
        this.ccWhsePackCount = ccWhsePackCount;
        this.ccVnpkWhpkRatio = ccVnpkWhpkRatio;
        this.ccReplPack = ccReplPack;
    }

    public ReplenishmentResponseDTO(Long planId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr,
                                    String lvl1Desc, Integer lvl2Nbr, String lvl2Desc, Integer lvl3Nbr, String lvl3Desc,
                                    Integer lvl4Nbr, String lvl4Desc, Integer finelineNbr, String finelineDesc, String finelineAltDesc  ,
                                    String styleNbr, String ccId, String colorName, String merchMethod, Integer ahsSizeId , String sizeDesc , Integer ccSpFinalBuyUnits, Integer ccSpReplQty ,Integer ccSpVenderPackCount,
                                    Integer ccSpWhsePackCount, Double ccSpVnpkWhpkRatio, Integer ccSpReplPack) {
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl0Desc = lvl0Desc;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl1Desc = lvl1Desc;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl2Desc = lvl2Desc;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Desc = lvl3Desc;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Desc = lvl4Desc;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.finelineAltDesc=finelineAltDesc;
        this.styleNbr = styleNbr;
        this.ccId = ccId;
        this.colorName = colorName;
        this.merchMethod = merchMethod;
        this.ahsSizeId = ahsSizeId;
        this.sizeDesc=sizeDesc;
        this.ccSpFinalBuyUnits=ccSpFinalBuyUnits;
        this.ccSpReplQty=ccSpReplQty;
        this.ccSpVenderPackCount = ccSpVenderPackCount;
        this.ccSpWhsePackCount = ccSpWhsePackCount;
        this.ccSpVnpkWhpkRatio = ccSpVnpkWhpkRatio;
        this.ccSpReplPack = ccSpReplPack;
    }
}
