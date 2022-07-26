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
    private Integer lvl3FinalBuyUnits;
    private Integer lvl3ReplUnits;
    private Integer lvl3VenderPackCount;
    private Integer lvl3WhsePackCount;
    private Integer lvl3VnpkWhpkRatio;
    private Integer lvl4FinalBuyUnits;
    private Integer lvl4ReplUnits;
    private Integer lvl4VenderPackCount;
    private Integer lvl4WhsePackCount;
    private Integer lvl4VnpkWhpkRatio;
    private Integer finelineFinalBuyUnits;
    private Integer finelineReplUnits;
    private Integer finelineVenderPackCount;
    private Integer finelineWhsePackCount;
    private Integer finelineVnpkWhpkRatio;

    public ReplenishmentResponseDTO(Long planId, Integer channelId, Integer lvl0Nbr, String lvl0Desc, Integer lvl1Nbr,
                                    String lvl1Desc, Integer lvl2Nbr, String lvl2Desc, Integer lvl3Nbr, String lvl3Desc,
                                    Integer lvl4Nbr, String lvl4Desc, Integer finelineNbr, String finelineDesc, Integer lvl3FinalBuyUnits,
                                    Integer lvl3ReplUnits, Integer lvl3VenderPackCount, Integer lvl3WhsePackCount, Integer lvl3VnpkWhpkRatio,
                                    Integer lvl4FinalBuyUnits, Integer lvl4ReplUnits, Integer lvl4VenderPackCount, Integer lvl4WhsePackCount,
                                    Integer lvl4VnpkWhpkRatio, Integer finelineFinalBuyUnits, Integer finelineReplUnits, Integer finelineVenderPackCount,
                                    Integer finelineWhsePackCount, Integer finelineVnpkWhpkRatio) {
        this.planId = planId;
        this.channelId = channelId;
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
        this.lvl3FinalBuyUnits = lvl3FinalBuyUnits;
        this.lvl3ReplUnits = lvl3ReplUnits;
        this.lvl3VenderPackCount = lvl3VenderPackCount;
        this.lvl3WhsePackCount = lvl3WhsePackCount;
        this.lvl3VnpkWhpkRatio = lvl3VnpkWhpkRatio;
        this.lvl4FinalBuyUnits = lvl4FinalBuyUnits;
        this.lvl4ReplUnits = lvl4ReplUnits;
        this.lvl4VenderPackCount = lvl4VenderPackCount;
        this.lvl4WhsePackCount = lvl4WhsePackCount;
        this.lvl4VnpkWhpkRatio = lvl4VnpkWhpkRatio;
        this.finelineFinalBuyUnits = finelineFinalBuyUnits;
        this.finelineReplUnits = finelineReplUnits;
        this.finelineVenderPackCount = finelineVenderPackCount;
        this.finelineWhsePackCount = finelineWhsePackCount;
        this.finelineVnpkWhpkRatio = finelineVnpkWhpkRatio;
    }
}
