package com.walmart.aex.sp.dto.packoptimization;

import lombok.Data;

import java.util.List;

@Data
public class DCInboundExcelResponse {
    private String lvl3Desc;
    private String lvl4Desc;
    private String finelineDesc;
    private String styleNbr;
    private String sizeDesc;
    private String ccId;
    private String channelDesc;
    private String merchMethodDesc;
    private String colorName;
    private String colorFamilyDesc;
    private List<DCinboundReplenishment> replenishment;
}
