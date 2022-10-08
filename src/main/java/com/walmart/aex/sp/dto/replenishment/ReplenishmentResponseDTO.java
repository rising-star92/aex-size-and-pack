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

    private Integer lvl3ReplQty;
    private Integer lvl3VenderPackCount;
    private Integer lvl3WhsePackCount;
    private Double lvl3vnpkWhpkRatio;
    private Integer lvl3finalBuyQty;
    private Integer lvl3ReplPack;
    private Integer lvl4Nbr;
    private String lvl4Desc;

    private Integer lvl4ReplQty;
    private Integer lvl4VenderPackCount;
    private Integer lvl4WhsePackCount;
    private Double lvl4vnpkWhpkRatio;
    private Integer lvl4finalBuyQty;
    private Integer lvl4ReplPack;
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
    private Integer merchMethod;
    private Integer ahsSizeId;
    private String sizeDesc;
    private Integer ccSpFinalBuyUnits;
    private Integer ccSpReplQty;
    private Integer ccSpVenderPackCount;
    private Integer ccSpWhsePackCount;
    private Integer ccMmSpVenderPackCount;
    private Integer ccMmSpWhsePackCount;
    private Double ccSpVnpkWhpkRatio;
    private Double ccMmSpVnpkWhpkRatio;
    private Integer ccSpReplPack;

    private Integer ccMmSpFinalBuyUnits;

    private Integer ccMMSpReplQty;

    private Integer ccMmSpReplPack;
    private String replenObject;


    public ReplenishmentResponseDTO(Long planId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr, String lvl1Desc,
                                    Integer lvl2Nbr, String lvl2Desc, Integer lvl3Nbr, String lvl3Desc, Integer lvl3ReplQty,Integer lvl3VenderPackCount ,
                                    Integer lvl3WhsePackCount ,Double lvl3vnpkWhpkRatio  , Integer lvl3ReplPack, Integer lvl4Nbr,
                                    String lvl4Desc,  Integer lvl4ReplQty, Integer lvl4VenderPackCount ,
                                    Integer lvl4WhsePackCount ,Double lvl4vnpkWhpkRatio  ,Integer lvl4ReplPack,Integer finelineNbr, String finelineDesc, String finelineAltDesc ,
                                    Integer finelineFinalBuyUnits,Integer finelineReplQty,
                                    Integer finelineVenderPackCount,Integer finelineWhsePackCount,
                                    Double finelineVnpkWhpkRatio,
                                    Integer finelineReplPack, Integer lvl3finalBuyQty, Integer lvl4finalBuyQty) {
        this.planId = planId;
        this.lvl0Nbr = lvl0Nbr;
        this.lvl0Desc = lvl0Desc;
        this.lvl1Nbr = lvl1Nbr;
        this.lvl1Desc = lvl1Desc;
        this.lvl2Nbr = lvl2Nbr;
        this.lvl2Desc = lvl2Desc;
        this.lvl3Nbr = lvl3Nbr;
        this.lvl3Desc = lvl3Desc;
        this.lvl3ReplQty = lvl3ReplQty;
        this.lvl3VenderPackCount = lvl3VenderPackCount;
        this.lvl3WhsePackCount = lvl3WhsePackCount;
        this.lvl3vnpkWhpkRatio = lvl3vnpkWhpkRatio;
        this.lvl3ReplPack = lvl3ReplPack;
        this.lvl4Nbr = lvl4Nbr;
        this.lvl4Desc = lvl4Desc;
        this.lvl4ReplQty = lvl4ReplQty;
        this.lvl4VenderPackCount = lvl4VenderPackCount;
        this.lvl4WhsePackCount = lvl4WhsePackCount;
        this.lvl4vnpkWhpkRatio = lvl4vnpkWhpkRatio;
        this.lvl4ReplPack = lvl4ReplPack;
        this.finelineNbr = finelineNbr;
        this.finelineDesc = finelineDesc;
        this.finelineAltDesc = finelineAltDesc;
        this.finelineFinalBuyUnits = finelineFinalBuyUnits;
        this.finelineReplQty = finelineReplQty;
        this.finelineVenderPackCount=finelineVenderPackCount;
        this.finelineWhsePackCount=finelineWhsePackCount;
        this.finelineVnpkWhpkRatio=finelineVnpkWhpkRatio;
        this.finelineReplPack=finelineReplPack;
        this.lvl3finalBuyQty= lvl3finalBuyQty;
        this.lvl4finalBuyQty= lvl4finalBuyQty;
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
                                    String styleNbr, String ccId, String colorName, Integer merchMethod, Integer ahsSizeId , String sizeDesc , Integer ccSpFinalBuyUnits, Integer ccSpReplQty ,Integer ccSpVenderPackCount,
                                    Integer ccSpWhsePackCount, Integer ccMmSpVenderPackCount, Integer ccMmSpWhsePackCount, Double ccSpVnpkWhpkRatio, Double ccMmSpVnpkWhpkRatio, Integer ccSpReplPack, Integer ccMmSpFinalBuyUnits ,Integer ccMMSpReplQty,
                                    String replenObject,Integer ccMmSpReplPack) {
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
        this.ccMmSpVenderPackCount = ccMmSpVenderPackCount;
        this.ccMmSpWhsePackCount = ccMmSpWhsePackCount;
        this.ccSpVnpkWhpkRatio = ccSpVnpkWhpkRatio;
        this.ccMmSpVnpkWhpkRatio = ccMmSpVnpkWhpkRatio;
        this.ccSpReplPack = ccSpReplPack;
        this.ccMmSpFinalBuyUnits = ccMmSpFinalBuyUnits;
        this.ccMMSpReplQty = ccMMSpReplQty;
        this.ccMmSpReplPack = ccMmSpReplPack;
        this.replenObject=replenObject;
        

    }
}
