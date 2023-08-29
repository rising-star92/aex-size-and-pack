package com.walmart.aex.sp.dto.packoptimization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DCInboundResponse {
    private Long planId;
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
    private String styleNbr;
    private Integer channelId;
    private String channelDesc;
    private String sizeDesc;
    private String customerChoice;
    private Integer ahsSizeId;
    private String colorName;
    private String colorFamilyDesc;
    private String merchMethodDesc;
    private String replenishment;
}


